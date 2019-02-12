package br.ufla.lemaf.ti.carteirapesca.domain.repository.banco;

import br.ufla.lemaf.ti.carteirapesca.domain.model.Banco.Titulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TituloRepository extends JpaRepository<Titulo, Integer> {

	@Query(value = "SELECT * FROM carteira_pesca.titulo WHERE id_remessa IS NULL", nativeQuery = true)
	List<Titulo> buscaTitulosSemRemessaGerada();

	Titulo findByNossoNumero(Integer nossoNumero);

}
