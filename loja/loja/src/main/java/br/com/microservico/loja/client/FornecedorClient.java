package br.com.microservico.loja.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.microservico.loja.dto.InfoForcedorDto;
import br.com.microservico.loja.dto.InfoPedidoDto;
import br.com.microservico.loja.dto.ItemDaCompraDto;

@FeignClient("forneceedor")
public interface FornecedorClient {

	@RequestMapping("/info/{estado}")
	InfoForcedorDto getInfoPorEstado(@PathVariable String estado);

	@RequestMapping(method = RequestMethod.POST, value="/pedido")
	InfoPedidoDto realizaPedido(List<ItemDaCompraDto> itens);
}
