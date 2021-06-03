package jisciple.iso8583.config;

import java.io.Serializable;

import jisciple.iso8583.enumeration.TipoContagemEnum;
import jisciple.iso8583.enumeration.TipoDadoEnum;
import jisciple.iso8583.enumeration.TipoFormatoEnum;
import jisciple.iso8583.enumeration.TipoTamanhoEnum;
import lombok.Data;

@Data
public class BitConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	protected TipoTamanhoEnum tipoTamanho;
	protected TipoContagemEnum tipoContagem;
	protected TipoFormatoEnum tipoFormato;
	protected TipoDadoEnum tipoDado;
	protected int tamanho;
	
}
