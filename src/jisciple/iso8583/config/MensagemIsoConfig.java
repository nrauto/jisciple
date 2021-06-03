package jisciple.iso8583.config;

import lombok.Data;

@Data
public class MensagemIsoConfig {

	protected BitConfig mtiConfig;
	protected BitConfig headerTxConfig;
	protected BitConfig footerTxConfig;
	protected BitConfig headerRxConfig;
	protected BitConfig footerRxConfig;
	protected BitConfig bitmapConfig;
	protected int[] bitsSensiveis;
	
	protected BitConfig[] bitConfig = new BitConfig[128];
	
}
