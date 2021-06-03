package jisciple.iso8583.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoDadoEnum {

	NUMERICO		("\\d*"), 
	ALFANUMERICO	("(\\d|[a-zA-z])*"), 
	ALFANUMESPECIAL (".*")
	;
	
	private String regex;
}
