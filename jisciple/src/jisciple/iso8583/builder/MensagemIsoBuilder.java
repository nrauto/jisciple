package jisciple.iso8583.builder;

import java.util.Arrays;

import jisciple.iso8583.Bit;
import jisciple.iso8583.MensagemIso;
import jisciple.iso8583.config.BitConfig;
import jisciple.iso8583.config.MensagemIsoConfig;
import jisciple.iso8583.enumeration.TipoContagemEnum;
import jisciple.iso8583.enumeration.TipoFormatoEnum;

public class MensagemIsoBuilder {

	public static MensagemIso buildMensagemRebatida(MensagemIso msg, MensagemIsoConfig isoConfig) {
		// MTI
		BitConfig mtiConfig = isoConfig.getMtiConfig();
		byte[] mti = Arrays.copyOf(msg.getMti().getValor(), msg.getMti().getValor().length);
		if (mtiConfig.getTipoFormato() == TipoFormatoEnum.ASCII) {
			mti[2] += 0x01; // incrementa 1 nas dezenas do MTI
		} else {
			mti[1] += 0x10; // o mesmo mas se for binario ou bcd
		}
		msg.getMti().setValor(mti);

		// BIT 39
		BitConfig bit39Config = isoConfig.getBitConfig()[38];
		byte[] codResp;
		if (bit39Config.getTipoContagem() == TipoContagemEnum.BYTES) {
			codResp = new byte[bit39Config.getTamanho()];
		} else {
			codResp = new byte[bit39Config.getTamanho() / 2 + bit39Config.getTamanho() % 2];
		}
		if (isoConfig.getBitConfig()[38].getTipoFormato() == TipoFormatoEnum.ASCII) {
			Arrays.fill(codResp, (byte) 0x30);
		} else {
			Arrays.fill(codResp, (byte) 0x00);
		}

		Bit bit39 = new Bit(bit39Config);
		bit39.setValor(codResp);
		msg.getDados()[38] = bit39;

		System.out.println("[RebatedorThread] REBATENDO: ");

		System.out.println(msg.prettyPrint());
		
		return msg;
	}
	
}
