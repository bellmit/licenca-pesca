package br.ufla.lemaf.ti.carteirapesca.domain.services.impl;

import br.com.caelum.stella.boleto.*;
import br.com.caelum.stella.boleto.bancos.Santander;
import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto;
import br.ufla.lemaf.ti.carteirapesca.domain.model.licenca.Modalidade;
import br.ufla.lemaf.ti.carteirapesca.domain.model.protocolo.Protocolo;
import br.ufla.lemaf.ti.carteirapesca.domain.services.BoletoBuilder;
import br.ufla.lemaf.ti.carteirapesca.infrastructure.config.Properties;
import br.ufla.lemaf.ti.carteirapesca.interfaces.shared.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import main.java.br.ufla.lemaf.beans.pessoa.Pessoa;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

/**
 * Buider do Boleto.
 * <p>
 * Cria o pdf do boleto para carteira de pesca.
 * Referência: https://github.com/caelum/caelum-stella/wiki/Gerando-boleto
 *
 * @author Highlander Paiva
 * @author Marcio Azevedo
 * @since 1.0
 */
@Slf4j
@Service
public class BoletoBuilderImpl implements BoletoBuilder {

	/**
	 * Cria o boleto da carteira de pesca.
	 *
	 * @param protocolo  O Protocolo
	 * @param modalidade A modalidade
	 * @param pessoa     A PEssoa solicitante
	 * @return O caminho do arquivo do boleto
	 */
	@Override
	public String gerarBoleto(final Protocolo protocolo,
	                          final Modalidade modalidade,
	                          final Pessoa pessoa) {

		try {

			var geradorDeBoleto = new GeradorDeBoleto(
				montarBoleto(pessoa, protocolo, modalidade)
			);

			var caminhoBoleto = Paths.get(
				Properties.pathBoletoPagamentoCarteiraPesca()
					+ protocolo.getProtocoloNaoFormatado()
					+ "/"
					+ protocolo.getProtocoloNaoFormatado()
					+ "-banco-santander.pdf"
			);

			var boleto = caminhoBoleto.toFile();

			if (!boleto.exists())
				Files.createDirectories(caminhoBoleto.getParent());

			geradorDeBoleto.geraPDF(caminhoBoleto.toString());

			return caminhoBoleto.toString();

		} catch (IOException | NullPointerException e) {

			log.error(e.getMessage());

			throw new ResourceNotFoundException();

		}

	}

	/**
	 * Monta os campos do boleto.
	 *
	 * @param pessoa A pessoa solicitante
	 * @param protocolo O protocolo da licença
	 * @param modalidade A modalidade da licença
	 * @return O boleto
	 */
	private static Boleto montarBoleto(final Pessoa pessoa,
	                                   final Protocolo protocolo,
	                                   final Modalidade modalidade) {

		val santander = new Santander();

		return Boleto.novoBoleto()
			.comBanco(santander)
			.comDatas(montarDatas())
			.comBeneficiario(montarBeneficiario())
			.comPagador(montarPagador(pessoa))
			.comValorBoleto(montarValorBoleto(modalidade))
			.comNumeroDoDocumento(protocolo.getCodigoFormatado())
			.comInstrucoes(
				"Boleto gerado somente para testes"
			)
			.comLocaisDePagamento(
				"Boleto não pode ser pago"
			);

	}

	/**
	 * Monta os dados de endereço do solicitante.
	 *
	 * @param pessoa A Pessoa
	 * @return O Endereço para o boleto
	 */
	private static Endereco montarEnderecoPessoa(Pessoa pessoa) {

		String logradouro = endereco(pessoa).logradouro
			+ ", Nº " + endereco(pessoa).numero + " "
			+ (endereco(pessoa).complemento != null
				? endereco(pessoa).complemento
				: "");

		return Endereco.novoEndereco()
			.comLogradouro(logradouro)
			.comBairro(endereco(pessoa).bairro)
			.comCep(endereco(pessoa).cep)
			.comCidade(endereco(pessoa).municipio.nome)
			.comUf(endereco(pessoa).municipio.estado.sigla);

	}

	/**
	 * Monta os dados de data do boleto.
	 *
	 * @return Objeto com as datas para o boleto
	 */
	private static Datas montarDatas() {

		// TODO - Montar estrutura para cálculo de datas a partir do processamento do banco
		LocalDate date = LocalDate.now();

		var day = date.getDayOfMonth();
		var month = date.getMonthValue();
		var year = date.getYear();

		return Datas.novasDatas()
			.comDocumento(day, month, year)
			.comProcessamento(day, month, year)
			.comVencimento(day, month, year);
	}

	/**
	 * Monta o Pagador.
	 *
	 * @param pessoa A pessoa solicitante
	 * @return O Pagador do boleto
	 */
	private static Pagador montarPagador(Pessoa pessoa) {

		return Pagador.novoPagador()
			.comNome(pessoa.nome)
			.comDocumento(pessoa.cpf)
			.comEndereco(montarEnderecoPessoa(pessoa));
	}

	/**
	 * Constroi o campo valor carteira.
	 * <p>
	 *
	 * @param modalidade A modalidade da carteira
	 * @return O campo de valor da carteira
	 */
	private static String montarValorBoleto(Modalidade modalidade) {
		switch (modalidade) {
			case ESPORTIVA:
				return "41.21";
			case RECREATIVA:
				return "57.21";
			default:
				return "";
		}

	}

	/**
	 * Busca o endereço do solicitante
	 * da carteira.
	 *
	 * @param pessoa A Pessoa solicitante
	 * @return O endereço
	 */
	private static main.java.br.ufla.lemaf.beans.pessoa.Endereco endereco(Pessoa pessoa) {
		return pessoa.enderecos
			.stream()
			.filter(endereco ->
				StringUtils.isNotBlank(endereco.logradouro) && StringUtils.isNotBlank(endereco.cep))
			.findFirst()
			.orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Monta o beneficiario.
	 * <p>
	 * Dado fantasia por falta de definições.
	 *
	 * @return O Beneficiario
	 * @implNote Reimplementar com dados reais
	 */
	private static Beneficiario montarBeneficiario() {
		// TODO - colocar dado real
		val enderecoBeneficiario = Endereco.novoEndereco()
			.comLogradouro("Av das Empresas, 777")
			.comBairro("Bairro Centro")
			.comCep("37200-000")
			.comCidade("Lavras")
			.comUf("MG");

		return Beneficiario.novoBeneficiario()
			.comNomeBeneficiario("Universidade Federal de Lavras")
			.comNumeroConvenio("3903125")
			.comCarteira("102")
			.comNossoNumero("382713000472")
			.comDigitoNossoNumero("2")
			.comEndereco(enderecoBeneficiario);

	}
}