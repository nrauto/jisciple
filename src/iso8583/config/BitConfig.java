package iso8583.config;

import java.io.Serializable;

import iso8583.enumeration.TipoContagemEnum;
import iso8583.enumeration.TipoFormatoEnum;
import iso8583.enumeration.TipoTamanhoEnum;
import iso8583.enumeration.TipoDadoEnum;
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
