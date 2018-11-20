package br.ufla.lemaf.ti.carteirapesca.domain.model.solicitante;

import br.ufla.lemaf.ti.carteirapesca.domain.model.licenca.Licenca;
import br.ufla.lemaf.ti.carteirapesca.domain.model.licenca.Protocolo;
import br.ufla.lemaf.ti.carteirapesca.domain.model.licenca.Status;
import br.ufla.lemaf.ti.carteirapesca.domain.utils.Entity;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Value Object de Solicitante, ou seja, a pessoa que solicitou
 * {@link Licenca}.
 *
 * @author Highlander Paiva
 * @since 1.0
 */
@NoArgsConstructor
public class Solicitante implements Entity<Solicitante, SolicitanteId> {

	private SolicitanteId identity;

	private List<Licenca> licencas = new ArrayList<>();

	/**
	 * Construtor de solicitante.
	 *
	 * @param cpf        O CPF do solicitante
	 * @param passaporte O passaporte do Solicitante
	 */
	public Solicitante(final CPF cpf, final Passaporte passaporte) {
		SolicitanteIdFactory factory = new SolicitanteIdFactory();
		this.identity = factory.gerarSolicitanteId(cpf, passaporte);
	}

	/**
	 * Busca uma String com um unico
	 * identificador do solicitante.
	 *
	 * @return O identificador em string
	 */
	public String buscarIdentificadorUnico() {
		return identity.buscarIdentificadorValido();
	}

	/**
	 * Busca se o Solicitante possui alguma licença ativa.
	 *
	 * @return {@code true} se alguma licença estiver ativa
	 */
	public boolean pussuiLicencaAtiva() {
		boolean flag = false;
		for (Licenca licenca : licencas) {
			flag = flag || licenca.status().sameValueAs(Status.ATIVO);
		}
		return flag;
	}

	/**
	 * Adiciona uma licença ao solicitante se não
	 * houver uma liceça ativa.
	 *
	 * @param licenca A licença
	 * @return O protocolo da licença
	 */
	public Protocolo adicionarLicenca(Licenca licenca) {
		if (!pussuiLicencaAtiva()) {
			licencas.add(licenca);
			return licenca.protocolo();
		} else {
			return null;
		}
	}

	/**
	 * @return Todas as Licencas do Solicitante
	 */
	public List<Licenca> buscarTodasLicencas() {
		return licencas;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameIdentityAs(final Solicitante other) {
		return other != null && identity.sameValueAs(other.identity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SolicitanteId identity() {
		return identity;
	}

	// Surrugate key para o Hibernate
	@SuppressWarnings("unused")
	private Long id;

	/**
	 * Factory de SolicitanteId.
	 *
	 * @author Highlander Paiva
	 * @since 1.0
	 */
	static class SolicitanteIdFactory {

		/**
		 * Gera o identificador do solicitante.
		 *
		 * @param cpf        O CPF
		 * @param passaporte O Passaporte
		 * @return O SolicitanteId
		 */
		SolicitanteId gerarSolicitanteId(final CPF cpf, final Passaporte passaporte) {
			return new SolicitanteId(cpf, passaporte);
		}

	}
}
