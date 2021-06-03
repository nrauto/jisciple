package jisciple.iso8583;

import java.util.Arrays;

import jisciple.iso8583.config.BitConfig;
import jisciple.iso8583.enumeration.TipoContagemEnum;
import jisciple.iso8583.enumeration.TipoFormatoEnum;
import jisciple.iso8583.enumeration.TipoTamanhoEnum;
import jisciple.iso8583.util.Util;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bit extends BitConfig {

	private static final long serialVersionUID = 1L;

	private byte[] valor;

	public byte[] toIsoBytes() {
		if (valor == null) {
			return null;
		}
		if (tipoTamanho == TipoTamanhoEnum.FIXO) {
			return Arrays.copyOf(valor, tamanho);
		} else {
			int tamVar;
			if (tipoContagem == TipoContagemEnum.BYTES) {
				tamVar = valor.length;
			} else {
				tamVar = valor.length * 2;
			}

			if (tipoTamanho == TipoTamanhoEnum.LLVAR) {
				return Util.concat(String.format("%02d", tamVar).getBytes(), valor);
			} else if (tipoTamanho == TipoTamanhoEnum.LLLVAR) {
				return Util.concat(String.format("%03d", tamVar).getBytes(), valor);
			} else if (tipoTamanho == TipoTamanhoEnum.LLBCD) {
				return Util.concat(Util.asciiToBcd((String.format("%02d", tamVar).getBytes())), valor);
			} else if (tipoTamanho == TipoTamanhoEnum.LLLBCD) {
				return Util.concat(Util.asciiToBcd((String.format("%03d", tamVar).getBytes())), valor);
			}
		}
		return null;
	}

	public Bit(BitConfig config) {
		this.tamanho = config.getTamanho();
		this.tipoContagem = config.getTipoContagem();
		this.tipoDado = config.getTipoDado();
		this.tipoFormato = config.getTipoFormato();
		this.tipoTamanho = config.getTipoTamanho();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (valor == null) {
			return null;
		}
		sb.append(new String(valor));
		sb.append(", 0x");
		sb.append(new String(Util.bytesToAscii(valor)));

		sb.append(", config = [TAM ");
		sb.append(getTamanho());
		sb.append(", ");
		sb.append(getTipoContagem());
		sb.append(", ");
		sb.append(getTipoDado());
		sb.append(", ");
		sb.append(getTipoTamanho());
//		sb.append(", ");
//		sb.append(getTipoContagem());
		sb.append("]");
		return sb.toString();
	}

	public boolean useBitmap2() {
		if (this.getTipoFormato() == TipoFormatoEnum.ASCII) {
			return ((Util.asciiToBytes(this.getValor())[0] & 0x80) != 0);
		} else {
			return ((this.getValor()[0] & 0x80) != 0);
		}
	}
}
