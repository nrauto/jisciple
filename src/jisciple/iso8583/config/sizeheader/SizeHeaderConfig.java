package jisciple.iso8583.config.sizeheader;

import lombok.Data;

@Data
public class SizeHeaderConfig {
	
	private String tipo; // ASCII, BINARIO
	private Integer tamanho; // 4 OU 2
	private String formato; // HL OU LH 
	private Boolean incluiProprioTamanho; // TRUE OU FALSE
	
}
