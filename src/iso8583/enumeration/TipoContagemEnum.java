package iso8583.enumeration;

public enum TipoContagemEnum {
	NIBBLES,
	BYTES;
	
	public static TipoContagemEnum getEnum(String input) {
		try {
			return TipoContagemEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return BYTES;
		}
	}
}
