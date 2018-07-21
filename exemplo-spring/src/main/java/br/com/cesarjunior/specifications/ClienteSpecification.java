package br.com.cesarjunior.specifications;

import org.springframework.data.jpa.domain.Specification;

import br.com.cesarjunior.entity.Cliente;

public class ClienteSpecification {

	private ClienteSpecification() {

	}

	public static Specification<Cliente> homemMaior(int idade) {
		return (root, criteriaQuery, criteriaBuilder) -> 
					criteriaBuilder.and(
							criteriaBuilder.equal(root.get("sexo"), "H"), 
							criteriaBuilder.greaterThan(root.get("idade"), idade)
							);
	}
}
