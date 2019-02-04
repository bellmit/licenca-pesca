package br.ufla.lemaf.ti.carteirapesca.domain.repository.banco;

import br.ufla.lemaf.ti.carteirapesca.domain.model.Banco.Remessa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RemessaRepository extends JpaRepository<Remessa, Integer> {

	@Query(value = "SELECT * " +
		"FROM carteira_pesca.remessa " +
		"WHERE to_char(dt_cadastro, 'dd/MM/YYYY') = to_char(now(), 'dd/MM/YYYY') " +
		"ORDER BY id DESC " +
		"LIMIT 1;", nativeQuery = true)
	Remessa buscaUltimaRemessaGerada();

}
