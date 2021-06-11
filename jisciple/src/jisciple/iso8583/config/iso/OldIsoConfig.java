package jisciple.iso8583.config.iso;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class OldIsoConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	private String mti;
	private String mapaBits;
	private String header;
	private Integer headerTx;
	private Integer headerRx;
	private String footer;
	private Integer footerTx;
	private Integer footerRx;
	private List<Integer> bitsSensiveis;
	private Map<String, OldBitConfig> bits;

}
