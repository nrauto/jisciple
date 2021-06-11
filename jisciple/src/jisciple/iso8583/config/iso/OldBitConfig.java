package jisciple.iso8583.config.iso;

import java.io.Serializable;

import lombok.Data;

@Data
public class OldBitConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tipoTamanho;
	private String tipoContagem;
	private Integer tamanho;
	private String tipoDados;
	private String formatoDados;

}
