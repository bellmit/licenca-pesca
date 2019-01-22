package br.ufla.lemaf.ti.carteirapesca.interfaces.acesso.web;

import br.ufla.lemaf.ti.carteirapesca.infrastructure.utils.CPFUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class AcessoResource {


	private String cpf;

	private String passaporte;

	private Date dataNascimento;

	private String nomeMae;

	/**
	 * Construtor.
	 *
	 * @param cpf        O CPF para o acesso
	 * @param passaporte O Passaprte para o acesso
	 * @apiNote Necessário a implementação para mostrar
	 * ao JSON como serializar o objeto.
	 */
	@JsonCreator
	public AcessoResource(@JsonProperty("cpf") String cpf,
						  @JsonProperty("passaporte") String passaporte,
						  @JsonProperty("dataNascimento") Date dataNascimento,
						  @JsonProperty("nomeMae") String nomeMae) {

		this.cpf = (cpf != null ? CPFUtils.unformat(cpf) : null);
		this.passaporte = passaporte;
		this.dataNascimento = dataNascimento;
		this.nomeMae = nomeMae;
	}

	@Override
	public String toString() {
		return String.format("Usuario{cpf='%s', passaporte='%s'}", cpf, passaporte);
	}

}
