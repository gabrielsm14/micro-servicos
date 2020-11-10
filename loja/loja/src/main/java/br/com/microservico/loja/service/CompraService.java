package br.com.microservico.loja.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.microservico.loja.client.FornecedorClient;
import br.com.microservico.loja.controller.dto.CompraDto;
import br.com.microservico.loja.controller.dto.InfoForcedorDto;
import br.com.microservico.loja.controller.dto.InfoPedidoDto;
import br.com.microservico.loja.model.Compra;

@Service
public class CompraService {

	private static final Logger LOG = LoggerFactory.getLogger(CompraService.class);
	
	@Autowired
	private FornecedorClient fornecedorClient;

	public Compra realizaCompra(CompraDto compra) {

		final String estado = compra.getEndereco().getEstado();
		
		LOG.info("Buscando informações do fornecedor de {}", estado);
		InfoForcedorDto info = fornecedorClient.getInfoPorEstado(compra.getEndereco().getEstado());
		
		LOG.info("Realizando o pedido");
		InfoPedidoDto pedido = fornecedorClient.realizaPedido(compra.getItens());

		System.out.println(info.getEndereco());

		Compra compraSalva = new Compra();
		compraSalva.setPedidoId(pedido.getId());
		compraSalva.setTempoDePreparo(pedido.getTempoDePreparo());
		compraSalva.setEnderecoDestino(compra.getEndereco().toString());
		
		return compraSalva;

	}

}
