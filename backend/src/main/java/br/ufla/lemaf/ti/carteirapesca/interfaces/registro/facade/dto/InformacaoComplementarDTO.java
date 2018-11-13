package br.ufla.lemaf.ti.carteirapesca.interfaces.registro.facade.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;

/**
 * DTO de Questionário.
 *
 * @author Highlander
 * @since 1.0
 */
@Getter
@EqualsAndHashCode(callSuper = false, doNotUseGetters = true)
public final class InformacaoComplementarDTO extends ResourceSupport {

	private Integer modalidade;

	private Integer localizacaoPreferencialPesca;

	private Integer rendaMensal;

	private Integer diasPescaPorAno;

	private Double gastoMedioPesca;

	private Integer faixaEtaria;

	private Integer localPesca;

	private Integer materialPesca;

	private Integer tipoIsca;

	private Integer modalidadeMaisPraticada;

	private Boolean agenciaTurismo;

	/**
	 * Construtor de Informação complementar.
	 *
	 * @param modalidade                   Inteiro representando a
	 *                                     modalidade de pesca
	 * @param localizacaoPreferencialPesca Inteiro representando a
	 *                                     localização preferencial de pesca
	 * @param rendaMensal                  Inteiro representando a
	 *                                     faixa da renda mensal
	 * @param diasPescaPorAno              Número de dias de pesca por ano
	 * @param gastoMedioPesca              Valor médio do gastos com pesca
	 * @param faixaEtaria                  Inteiro representando a
	 *                                     faixa etária
	 * @param localPesca                   Inteiro representando o local da
	 *                                     pesca
	 * @param materialPesca                Inteiro representando o
	 *                                     material de pesca
	 * @param tipoIsca                     Inteiro representando o
	 *                                     tipo de isca
	 * @param modalidadeMaisPraticada      Inteiro representando a modalidade
	 *                                     de pesca nais praticada
	 * @param agenciaTurismo               {@code true} se o usuário costuma
	 *                                     contratar agências de turismo para a
	 *                                     prática
	 */
	@JsonCreator
	InformacaoComplementarDTO(@JsonProperty("modalidade") final Integer modalidade,
	                          @JsonProperty("localizacaoPreferencialPesca")
	                          final Integer localizacaoPreferencialPesca,
	                          @JsonProperty("rendaMensal") final Integer rendaMensal,
	                          @JsonProperty("diasPescaPorAno") final Integer diasPescaPorAno,
	                          @JsonProperty("gastoMedioPesca") final Double gastoMedioPesca,
	                          @JsonProperty("faixaEtaria") final Integer faixaEtaria,
	                          @JsonProperty("localPesca") final Integer localPesca,
	                          @JsonProperty("materialPesca") final Integer materialPesca,
	                          @JsonProperty("tipoIsca") final Integer tipoIsca,
	                          @JsonProperty("modalidadeMaisPraticada") final Integer modalidadeMaisPraticada,
	                          @JsonProperty("agenciaTurismo") final Boolean agenciaTurismo) {
		this.modalidade = modalidade;
		this.localizacaoPreferencialPesca = localizacaoPreferencialPesca;
		this.rendaMensal = rendaMensal;
		this.diasPescaPorAno = diasPescaPorAno;
		this.gastoMedioPesca = gastoMedioPesca;
		this.faixaEtaria = faixaEtaria;
		this.localPesca = localPesca;
		this.materialPesca = materialPesca;
		this.tipoIsca = tipoIsca;
		this.modalidadeMaisPraticada = modalidadeMaisPraticada;
		this.agenciaTurismo = agenciaTurismo;
	}
}
