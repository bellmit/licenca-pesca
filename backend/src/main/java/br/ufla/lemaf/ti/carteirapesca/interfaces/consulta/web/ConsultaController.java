package br.ufla.lemaf.ti.carteirapesca.interfaces.consulta.web;

import br.ufla.lemaf.ti.carteirapesca.application.ConsultaApplication;
import br.ufla.lemaf.ti.carteirapesca.application.RegistroApplication;
import br.ufla.lemaf.ti.carteirapesca.application.TaxaApplication;
import br.ufla.lemaf.ti.carteirapesca.domain.model.licenca.Licenca;
import br.ufla.lemaf.ti.carteirapesca.domain.model.licenca.Pais;
import br.ufla.lemaf.ti.carteirapesca.domain.model.protocolo.Protocolo;
import br.ufla.lemaf.ti.carteirapesca.infrastructure.utils.ProtocoloFormatter;
import br.ufla.lemaf.ti.carteirapesca.infrastructure.utils.ProtocoloValidator;
import br.ufla.lemaf.ti.carteirapesca.interfaces.acesso.facade.AcessoServiceFacade;
import br.ufla.lemaf.ti.carteirapesca.interfaces.acesso.web.AcessoController;
import br.ufla.lemaf.ti.carteirapesca.interfaces.acesso.web.AcessoResource;
import br.ufla.lemaf.ti.carteirapesca.interfaces.consulta.facade.ConsultaServiceFacade;
import br.ufla.lemaf.ti.carteirapesca.interfaces.consulta.facade.dto.LicencaDTO;
import br.ufla.lemaf.ti.carteirapesca.interfaces.registro.facade.dto.CarteiraDTO;
import br.ufla.lemaf.ti.carteirapesca.interfaces.registro.facade.dto.LicencaPescaDTO;
import br.ufla.lemaf.ti.carteirapesca.interfaces.registro.facade.dto.ValidacaoDTO;
import br.ufla.lemaf.ti.carteirapesca.interfaces.shared.Controller.DefaultController;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Manipula as consultas da licença.
 *
 * @author Highlander Paiva
 * @since 1.0
 */
@Slf4j
@Controller
@Transactional
@RequestMapping("/api")
public class ConsultaController extends DefaultController {

	private static final String NOME_BOLETO_COM_EXTENSAO = "boleto-licenca-pesca.pdf";

	private ConsultaServiceFacade facade;

	@Autowired
	private ConsultaApplication consultaApplication;

	@Autowired
	private AcessoServiceFacade acessoServiceFacade;

	@Autowired
	private RegistroApplication registroApplication;

	@Autowired
	private TaxaApplication taxaApplication;

	/**
	 * Injetando dependências.
	 *
	 * @param facade A Facede de consulta
	 */
	@Autowired
	public ConsultaController(ConsultaServiceFacade facade) {
		this.facade = facade;
	}

	/**
	 * Controller para a consulta de licença. Recebe como parâmetro
	 * o protocolo.
	 *
	 * @param protocolo O protocolo buscado
	 * @return {@link LicencaDTO} A Licença. Em forma de {@link ResponseEntity}
	 */
	@CrossOrigin("*")
	@GetMapping("/consultar")
	public ResponseEntity<LicencaDTO> consultar(@RequestParam final String protocolo) {

		var licenca = facade.consultarLicencaDTO(protocolo);

		if (licenca == null)
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

		licenca.add(linkTo(methodOn(ConsultaController.class)
			.consultar(protocolo))
			.withSelfRel());

		return new ResponseEntity<>(licenca, HttpStatus.OK);

	}

	@CrossOrigin("*")
	@GetMapping("/consultarCarteira")
	public ResponseEntity<CarteiraDTO> consultarCarteira(@RequestParam final String protocolo) throws Exception {

		CarteiraDTO carteiraDTO = new CarteiraDTO();

		var licenca = facade.consultar(protocolo);

		carteiraDTO.setLicenca(licenca);

		var  cpf = "";
		var passaporte = "";
		if(licenca.solicitante().getIdentity().cpf() != null){
			cpf = licenca.solicitante().getIdentity().cpf().getNumero();
		} else {
			cpf = null;
		}

		if(licenca.solicitante().getIdentity().passaporte() != null){
			passaporte = licenca.solicitante().getIdentity().passaporte().getNumero();
		} else {
			passaporte = null;
		}

		AcessoResource acessoResource = new AcessoResource(cpf, passaporte);

		var pessoa = acessoServiceFacade.acessar(acessoResource);

		ValidacaoDTO validacaoDTO = new ValidacaoDTO(acessoResource);

		pessoa.add(linkTo(methodOn(AcessoController.class)
			.acessar(validacaoDTO))
			.withSelfRel());

		carteiraDTO.setPessoaDTO(pessoa);

		if(licenca == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(carteiraDTO, HttpStatus.OK);
	}

	@CrossOrigin("*")
	@GetMapping("/boleto")
	public ResponseEntity<InputStreamResource> downloadBoleto(@RequestParam String protocolo) throws Exception {

		Licenca licenca = facade.consultar(protocolo);

		if(licenca == null) {
			throw new Exception("Não foi encontrada nenhuma licença com o protocolo informado");
		}

		File documentoArrecadacao = taxaApplication.downloadDocumentoArrecadacao(licenca);

		return downloadArquivo(documentoArrecadacao, NOME_BOLETO_COM_EXTENSAO);

	}

	/**
	 * Download da carteira de pesca.
	 *
	 * @param protocolo O protocolo da Carteira
	 * @return O arquivo
	 */
	@CrossOrigin("*")
	@GetMapping("/carteira")
	public ResponseEntity<InputStreamResource> downloadCarteira(@RequestParam String protocolo) {

		try {

			Protocolo protocoloObj;
			if (protocolo.length() > 9) {

				var formatterNovo = new ProtocoloFormatter("$1-$2/$3-$4", ProtocoloValidator.FORMATED_RENOVADO, "$1$2$3$4", ProtocoloValidator.UNFORMATED_RENOVADO);
				protocoloObj = new Protocolo(protocolo, formatterNovo);

			} else {
				protocoloObj = new Protocolo(protocolo);
			}

			var licenca = consultaApplication.consulta(protocoloObj);

//			if(licenca.getConvenio().getPagamento() == null) {
//				throw new Exception("O pagamento não foi efetuado ou processado pelo banco.");
//			}

			var solicitante = licenca.solicitante();
			var pessoa = registroApplication.buscarDadosSolicitante(solicitante);
			var carteira = facade.gerarCarteira(protocoloObj, licenca, pessoa);

			var httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_PDF);

			var isr = new InputStreamResource(new FileInputStream(carteira));

			return new ResponseEntity<>(isr, httpHeaders, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>((InputStreamResource) null, HttpStatus.NOT_FOUND);

		}

	}

	/**
	 * Autenticidade da carteira, trazer todos os dados da carteira, menos limite de captura
	 * code do QR que é o numero da licença
	 * pessoa: Nome, CPF/CNPJ, enderecos{CEP, PAIS, MUNICIPIO}
	 * licenca: protocolo(numero da licenca), modalidade, emissao(dataCriacao), validade
	 * */
	@CrossOrigin("*")
	@GetMapping("/informacao-carteira")
	public ResponseEntity<LicencaPescaDTO> buscarDadosCarteira(
		@RequestParam final String protocolo){

		var protocoloObj = new Protocolo(protocolo);
		var licenca = consultaApplication.consulta(protocoloObj);
		var solicitante = licenca.solicitante();
		var pessoa = registroApplication.buscarDadosSolicitante(solicitante);

		var licencaPesca = new LicencaPescaDTO(licenca, protocoloObj, pessoa);



		return new ResponseEntity<>(licencaPesca, HttpStatus.OK);
	}

	@CrossOrigin("*")
	@GetMapping("/fetch-paises")
	public ResponseEntity<List<Pais>> fetchPaises(){

		var listPaises = consultaApplication.fetchPaises();

		return new ResponseEntity<>(listPaises, HttpStatus.OK);
	}

}
