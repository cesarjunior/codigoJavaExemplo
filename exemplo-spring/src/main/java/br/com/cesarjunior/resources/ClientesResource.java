package br.com.cesarjunior.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cesarjunior.entity.Cliente;
import br.com.cesarjunior.repository.ClienteRepository;
import br.com.cesarjunior.specifications.ClienteSpecification;

@RestController
@RequestMapping("/clientes")
public class ClientesResource {
	
	@Autowired
	private ClienteRepository clienteRepository;

	@GetMapping(produces = "application/json")
	public List<Cliente> listarClientes() {
		return clienteRepository.findAll(ClienteSpecification.homemMaior(20));
	}

}
