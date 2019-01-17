package br.ufla.lemaf.ti.carteirapesca.interfaces.acesso.web;

import br.ufla.lemaf.ti.carteirapesca.interfaces.acesso.facade.AcessoServiceFacade;
import br.ufla.lemaf.ti.carteirapesca.interfaces.registro.facade.dto.ListaLicencaDTO;
import br.ufla.lemaf.ti.carteirapesca.interfaces.registro.facade.dto.PessoaDTO;
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
	public ResponseEntity<ListaLicencaDTO> buscarLicensas(@RequestBody final AcessoResource acessoResource) {

		var listaLicencaDTO = new ListaLicencaDTO();
		var pessoa = acessoServiceFacade.acessar(acessoResource);

		listaLicencaDTO.setPessoa(pessoa);
		listaLicencaDTO.setLicencas(acessoServiceFacade.buscarLicencasPorPessoaDTO(pessoa));

		return new ResponseEntity<>(listaLicencaDTO, HttpStatus.ACCEPTED);

	}

}
