package jisciple.iso8583.builder;

import java.util.Arrays;

import jisciple.iso8583.Bit;
import jisciple.iso8583.MensagemIso;
import jisciple.iso8583.config.BitConfig;
import jisciple.iso8583.config.MensagemIsoConfig;
import jisciple.iso8583.enumeration.TipoContagemEnum;
import jisciple.iso8583.enumeration.TipoFormatoEnum;
import jisciple.iso8583.sequence.factory.SequenceFactory;
import jisciple.iso8583.util.Util;

public class MensagemIsoBuilder {

	private SequenceFactory sequenceFactoryTamanho9 = SequenceFactory.getNewInstance(1, 999999999, 9);
	private SequenceFactory sequenceFactoryTamanho6 = SequenceFactory.getNewInstance(1, 5, 6);
	
	public MensagemIso buildMensagemRebatida(MensagemIso msg, MensagemIsoConfig isoConfig) {
		
		// TODO usar config externa para fazer override de bits especificos
		return buildMensagemRebatidaPadrao(msg, isoConfig);
	}
	
	/**
	 * Rebate todos os campos, adicionando bit 38 (cod autorizacao) e bit 127 (nsu host)
	 * 
	 * @param msg
	 * @param isoConfig
	 * @return
	 */
	public MensagemIso buildMensagemRebatidaPadrao(MensagemIso msg, MensagemIsoConfig isoConfig) {

		
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
		
		// BIT 127
		
		String nsuHost = sequenceFactoryTamanho9.getNext();
		Bit bit127 = new Bit(isoConfig.getBitConfig()[126]);
		if (isoConfig.getBitConfig()[126].getTipoFormato() == TipoFormatoEnum.ASCII) {
			bit127.setValor(nsuHost.getBytes());
		} else {
			bit127.setValor(Util.asciiToBcd(nsuHost.getBytes()));
		}
		msg.getDados()[126] = bit127; 		
		// BIT 38
		String codAutorizacao = sequenceFactoryTamanho6.getNext();
		Bit bit38 = new Bit(isoConfig.getBitConfig()[37]);
		if (isoConfig.getBitConfig()[37].getTipoFormato() == TipoFormatoEnum.ASCII) {
			bit38.setValor(codAutorizacao.getBytes());
		} else {
			bit38.setValor(Util.asciiToBcd(codAutorizacao.getBytes()));
		}
		msg.getDados()[37] = bit38; 	
		
		
		
		
		System.out.println("[RebatedorThread] REBATENDO: ");

		System.out.println(msg.prettyPrint());
		
		return msg;
		
	}
	
	
	
}
