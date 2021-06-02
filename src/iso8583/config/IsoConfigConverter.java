package iso8583.config;

import static iso8583.enumeration.TipoContagemEnum.BYTES;
import static iso8583.enumeration.TipoDadoEnum.ALFANUMERICO;
import static iso8583.enumeration.TipoDadoEnum.NUMERICO;
import static iso8583.enumeration.TipoTamanhoEnum.FIXO;


import iso8583.config.old.OldBitConfig;
import iso8583.config.old.OldIsoConfig;
import iso8583.enumeration.TipoContagemEnum;
import iso8583.enumeration.TipoDadoEnum;
import iso8583.enumeration.TipoFormatoEnum;
import iso8583.enumeration.TipoTamanhoEnum;

public class IsoConfigConverter {

	// converte o config legado pro config novo
	public static MensagemIsoConfig convert(OldIsoConfig old) {
		MensagemIsoConfig config = new MensagemIsoConfig();
		
		
		// Header e Footer
		BitConfig headerRx = new BitConfig();
		headerRx.setTamanho(old.getHeaderRx());
		headerRx.setTipoContagem(BYTES);
		headerRx.setTipoDado(ALFANUMERICO);
		headerRx.setTipoTamanho(FIXO);
		headerRx.setTipoFormato(TipoFormatoEnum.valueOf(old.getHeader()));
		
		BitConfig headerTx = new BitConfig();
		headerTx.setTamanho(old.getHeaderTx());
		headerTx.setTipoContagem(BYTES);
		headerTx.setTipoDado(ALFANUMERICO);
		headerTx.setTipoTamanho(FIXO);
		headerTx.setTipoFormato(TipoFormatoEnum.valueOf(old.getHeader()));

		BitConfig footerRx = new BitConfig();
		footerRx.setTamanho(old.getFooterRx());
		footerRx.setTipoContagem(BYTES);
		footerRx.setTipoDado(ALFANUMERICO);
		footerRx.setTipoTamanho(FIXO);
		footerRx.setTipoFormato(TipoFormatoEnum.valueOf(old.getFooter()));
		
		BitConfig footerTx = new BitConfig();
		footerTx.setTamanho(old.getFooterTx());
		footerTx.setTipoContagem(BYTES);
		footerTx.setTipoDado(ALFANUMERICO);
		footerTx.setTipoTamanho(FIXO);
		footerTx.setTipoFormato(TipoFormatoEnum.valueOf(old.getFooter()));
		
		// MTI
		BitConfig mti = new BitConfig();
		mti.setTamanho((TipoFormatoEnum.valueOf(old.getMapaBits()) == TipoFormatoEnum.ASCII)?4:2);
		mti.setTipoTamanho(FIXO);
		mti.setTipoContagem(BYTES);
		mti.setTipoFormato(TipoFormatoEnum.valueOf(old.getMti()));
		
		// Bitmap 1
		BitConfig bitmap1 = new BitConfig();
		bitmap1.setTamanho((TipoFormatoEnum.valueOf(old.getMapaBits()) == TipoFormatoEnum.BINARIO)?8:16);
		bitmap1.setTipoDado(ALFANUMERICO);
		bitmap1.setTipoTamanho(FIXO);
		bitmap1.setTipoContagem(BYTES);
		bitmap1.setTipoFormato(TipoFormatoEnum.valueOf(old.getMapaBits()));
		
		BitConfig bitmap2 = new BitConfig();
		bitmap2.setTamanho((TipoFormatoEnum.valueOf(old.getMapaBits()) == TipoFormatoEnum.BINARIO)?8:16);
		bitmap2.setTipoDado(ALFANUMERICO);
		bitmap2.setTipoTamanho(FIXO);
		bitmap2.setTipoContagem(BYTES);
		bitmap2.setTipoFormato(TipoFormatoEnum.valueOf(old.getMapaBits()));
		
		config.setHeaderTxConfig(headerTx);
		config.setHeaderRxConfig(headerRx);
		config.setFooterTxConfig(footerTx);
		config.setFooterRxConfig(footerRx);
		config.setMtiConfig(mti);
		config.setBitmapConfig(bitmap1);
		config.getBitConfig()[0] = bitmap2;
		
		// Converte os bits
		for(int i = 2; i <= 128; i++) {
			config.getBitConfig()[i - 1] = convert(old.getBits().get(String.format("%03d", i)));
		}

		config.setBitsSensiveis(old.getBitsSensiveis().stream().mapToInt(i->i.intValue()).toArray());
		
		return config;
	}
	
	private static BitConfig convert(OldBitConfig old) {
		BitConfig config = new BitConfig();
		
		config.setTamanho(old.getTamanho());
		config.setTipoContagem(TipoContagemEnum.getEnum(old.getTipoContagem()));
		config.setTipoDado(TipoDadoEnum.valueOf(old.getTipoDados()));
		config.setTipoFormato(TipoFormatoEnum.valueOf(old.getFormatoDados()));
		config.setTipoTamanho(TipoTamanhoEnum.valueOf(old.getTipoTamanho()));
		
		return config;
	}
	
}
