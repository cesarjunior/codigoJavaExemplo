package br.com.cesarjunior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.cesarjunior.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, String>, JpaSpecificationExecutor<Cliente> {

}
