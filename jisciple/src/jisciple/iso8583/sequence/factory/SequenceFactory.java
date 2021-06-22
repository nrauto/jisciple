package jisciple.iso8583.sequence.factory;

public class SequenceFactory {

//	private static SequenceFactory factory;
	
	private int index;
	private int min;
	private int max;
	private int length;
	
	public SequenceFactory(int min, int max, int length) {
		this.min = min;
		this.max = max;
		this.length = length;
		this.index = min-1;
	}
	
	public static SequenceFactory getNewInstance(int min, int max, int length) {
		return new SequenceFactory(min, max, length);
	}
	
	public String getNext() {
		index ++;
		if(index > max) {
			index = min;
		}
		return String.format("%0" + length + "d", index);
	}
	
	
}
