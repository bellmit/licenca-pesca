package br.ufla.lemaf.ti.carteirapesca.interfaces.acesso.web;

import br.ufla.lemaf.ti.carteirapesca.application.RegistroApplication;
import br.ufla.lemaf.ti.carteirapesca.infrastructure.utils.CarteiraUtils;
import br.ufla.lemaf.ti.carteirapesca.infrastructure.utils.Gerador;
import br.ufla.lemaf.ti.carteirapesca.infrastructure.utils.WebServiceUtils;
import br.ufla.lemaf.ti.carteirapesca.interfaces.acesso.facade.AcessoServiceFacade;
import br.ufla.lemaf.ti.carteirapesca.interfaces.consulta.facade.dto.LicencaDTO;
import br.ufla.lemaf.ti.carteirapesca.interfaces.registro.facade.dto.ListaLicencaDTO;
import br.ufla.lemaf.ti.carteirapesca.interfaces.registro.facade.dto.PessoaDTO;
import br.ufla.lemaf.ti.carteirapesca.interfaces.registro.facade.dto.PessoaEUDTO;
import br.ufla.lemaf.ti.carteirapesca.interfaces.shared.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.transaction.Transactional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Manipula o acesso de usuarios.
 *
 * @author Highlander Paiva
 * @since 1.0
 */
@Slf4j
@Controller
@Transactional
@RequestMapping("/api")
public class AcessoController {

	private AcessoServiceFacade acessoServiceFacade;

	/**
	 * Injeta a dependencia da controller.
	 *
	 * @param acessoServiceFacade Serviço de Facade da camada
	 *                            de interface.
	 */
	@Autowired
	public AcessoController(final AcessoServiceFacade acessoServiceFacade) {
		this.acessoServiceFacade = acessoServiceFacade;
	}


	/**
	 * Controller para o acesso público. Recebe como parâmetro
	 * obrigatório o CPF do usuário.
	 * <p>
	 * Caso o mesmo tenha cadastro no entrada única, retorna seus dados,
	 * e se o mesmo não tiver cadastro, retornará {@link PessoaDTO} vazio.
	 *
	 * @param acessoResource Paramêtro com o recurso de
	 *                       acesso do Usuário.
	 * @return {@link PessoaDTO} Pessoa vazia caso não exista a mesma na
	 * base de dados, ou a pessoa instanciada com
	 * seus dados caso exista. Em forma de {@link ResponseEntity}
	 */
	@CrossOrigin("*")
	@PostMapping("/acessar")
	public ResponseEntity<PessoaDTO> acessar(@RequestBody final AcessoResource acessoResource) {

		var pessoa = acessoServiceFacade.acessar(acessoResource);
		pessoa.add(linkTo(methodOn(AcessoController.class)
			.acessar(acessoResource))
			.withSelfRel());

		return new ResponseEntity<>(pessoa, HttpStatus.ACCEPTED);

	}

	@CrossOrigin("*")
	@PostMapping("/buscarLicensas")
	public ResponseEntity<ListaLicencaDTO> buscarLicensas(@RequestBody final AcessoResource acessoResource) throws Exception {

		var listaLicencaDTO = new ListaLicencaDTO();
		var pessoa = acessoServiceFacade.acessar(acessoResource);

		listaLicencaDTO.setPessoa(pessoa);

		listaLicencaDTO.setLicencas(acessoServiceFacade.buscarLicencasPorPessoaDTO(pessoa));

		return new ResponseEntity<>(listaLicencaDTO, HttpStatus.ACCEPTED);

	}

	//validacao
	@CrossOrigin("*")
	@PostMapping("/verificaDados")
	public ResponseEntity verificaDados(@RequestBody final ValidacaoDTO validacaoDTO) {

		AcessoController.verificaDadosProrietario(validacaoDTO);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	public static Boolean verificaDadosProrietario(ValidacaoDTO validacaoDTO) {

		if (!verificaDadosPessoa(validacaoDTO)) {
			return false;
		}
		return true;

	}

	private static Boolean verificaDadosPessoa(ValidacaoDTO validacaoDTO) {

		WebServiceUtils.validarWebService();

		var pessoa = WebServiceUtils
			.webServiceEU()
			.buscarPessoaFisicaPeloCpf(validacaoDTO.getCpf());

		if(!pessoa.dataNascimento.equals(validacaoDTO.getDataNascimento())){
			return false;
		} else if(!pessoa.nomeMae.equals(validacaoDTO.getMae())) {
			return false;
		} else if(pessoa.enderecos.stream()
			.filter(endereco -> endereco.municipio.nome
				.equals(validacaoDTO.getMunicipio()))
			.findFirst() != null) {
			return false;
		}

		return true;

	}

	@CrossOrigin("*")
	@PostMapping("/buscaDados")
	public ResponseEntity buscarConfirmarDados(@RequestBody final AcessoResource acessoResource) {

		var listaLicencaDTO = new ListaLicencaDTO();

		PessoaDTO pessoa = acessoServiceFacade.acessar(acessoResource);

		listaLicencaDTO.setPessoa(pessoa);

		return new ResponseEntity<>(preencherListaVerificacao(pessoa), HttpStatus.ACCEPTED);

	}


	private static Map<String, Object[]> preencherListaVerificacao(PessoaDTO pessoa) {


		Map<String, Object[]> listasVerificacao = new HashMap<>();


		Integer qtdCaracteresCpf = pessoa.getCpf().length();

		if (qtdCaracteresCpf == 11) {
			preencherListaVerificacaoPessoa(listasVerificacao, pessoa);
		}

		return listasVerificacao;

	}

	private static void preencherListaVerificacaoPessoa(Map<String, Object[]> listasVerificacao, PessoaDTO pessoa) {

		Gerador gerador = new Gerador();

		Integer quantidade = 5;
		Integer padrao = Integer.valueOf(pessoa.getCpf().substring(pessoa.getCpf().length()-1));
		Integer posicao = padrao > 3 ? Math.abs(padrao/3) : padrao;


		String[] municipios = gerador.gerarMunicipios(quantidade, padrao);
		municipios[posicao] = CarteiraUtils.capitalize(pessoa.getEnderecoPrincipal().getMunicipio().nome);
		listasVerificacao.put("municipios", municipios);

		if(posicao > 3) {
			posicao = 0;
		}

		String[] maes = gerador.gerarMaes(quantidade, padrao);

		maes[posicao++] = CarteiraUtils.capitalize(pessoa.getNomeMae());
		listasVerificacao.put("maes", maes);

	}

}
