package br.com.microservico.loja.service;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import br.com.microservico.loja.client.FornecedorClient;
import br.com.microservico.loja.client.TransportadorClient;
import br.com.microservico.loja.dto.CompraDto;
import br.com.microservico.loja.dto.InfoEntregaDTO;
import br.com.microservico.loja.dto.InfoForcedorDto;
import br.com.microservico.loja.dto.InfoPedidoDto;
import br.com.microservico.loja.dto.VoucherDTO;
import br.com.microservico.loja.model.Compra;
import br.com.microservico.loja.model.CompraState;
import br.com.microservico.loja.repository.CompraRepository;

@Service
public class CompraService {

	private static final Logger LOG = LoggerFactory.getLogger(CompraService.class);

	@Autowired
	private FornecedorClient fornecedorClient;

	@Autowired
	private TransportadorClient transportadorClient;

	@Autowired
	private CompraRepository compraRepository;

	@HystrixCommand(threadPoolKey = "getByIdThreadPool")
	public Compra getById(Long id) {
		return compraRepository.findById(id).orElse(new Compra());
	}

	public Compra reprossaCompra(Long id) {
		return null; // TODO implementar
	}

	public Compra cancelaCompra(Long id) {
		return null; // TODO implementar
	}

	@HystrixCommand(fallbackMethod = "realizaCompraFallback", threadPoolKey = "realizarCompraThreadPool")
	public Compra realizaCompra(CompraDto compra) {

		// COMPRA RECEBIDA 
		Compra compraSalva = new Compra();
		compraSalva.setState(CompraState.RECEBIDO);
		compraSalva.setEnderecoDestino(compra.getEndereco().toString());
		compraRepository.save(compraSalva);
		compra.setCompraId(compraSalva.getId());

		// PEDIDO REALIZADO
		LOG.info("Buscando informações do fornecedor de {}", compra.getEndereco().getEstado());
		InfoForcedorDto info = fornecedorClient.getInfoPorEstado(compra.getEndereco().getEstado());
		LOG.info("Realizando o pedido");
		InfoPedidoDto pedido = fornecedorClient.realizaPedido(compra.getItens());
		compraSalva.setState(CompraState.PEDIDO_REALIZADO);
		compraSalva.setPedidoId(pedido.getId());
		compraSalva.setTempoDePreparo(pedido.getTempoDePreparo());
		compraRepository.save(compraSalva);

		// RESERVA REALIZADA
		InfoEntregaDTO entregaDTO = new InfoEntregaDTO();
		entregaDTO.setPedidoId(pedido.getId());
		entregaDTO.setDataParaEntrega(LocalDate.now().plusDays(pedido.getTempoDePreparo()));
		entregaDTO.setEnderecoOrigem(info.getEndereco());
		entregaDTO.setEnderecoDestino(compra.getEndereco().toString());
		VoucherDTO voucher = transportadorClient.reversaEntrega(entregaDTO);
		compraSalva.setState(CompraState.RESERVA_ENTREGA_REALIZADA);
		compraSalva.setDataParaEntrega(voucher.getPrevisaoParaEntrega());
		compraSalva.setVoucher(voucher.getNumero());
		compraRepository.save(compraSalva);

		return compraSalva;

	}

	public Compra realizaCompraFallback(CompraDto compra) {
		if (compra.getCompraId() != null) {
			return compraRepository.findById(compra.getCompraId()).get();
		}

		Compra compraFallback = new Compra();
		compraFallback.setEnderecoDestino(compra.getEndereco().toString());
		return compraFallback;
	}

}
