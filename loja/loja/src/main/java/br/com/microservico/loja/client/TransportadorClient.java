package br.com.microservico.loja.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.microservico.loja.dto.InfoEntregaDTO;
import br.com.microservico.loja.dto.VoucherDTO;

@FeignClient("transportador")
public interface TransportadorClient {

	@RequestMapping(path = "/entrega", method = RequestMethod.POST)
	VoucherDTO reversaEntrega(InfoEntregaDTO entregaDTO);

	
}
