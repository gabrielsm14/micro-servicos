package br.com.microservico.loja.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.microservico.loja.model.Compra;

@Repository
public interface CompraRepository extends CrudRepository<Compra, Long> {

}
