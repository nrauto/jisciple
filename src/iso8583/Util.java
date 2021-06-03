package iso8583;

import java.util.Arrays;
import java.util.regex.Pattern;

public class Util {

	public static byte[] concat(byte[] first, byte[] second) {

		if (first == null && second == null) {
			return null;
		}
		if (first == null) {
			return second;
		}
		if (second == null) {
			return first;
		}

		byte[] out = new byte[first.length + second.length];
		System.arraycopy(first, 0, out, 0, first.length);
		System.arraycopy(second, 0, out, first.length, second.length);

		return out;
	}

	// "235" (0x32 0x33 0x35) -> 0x02 0x35
	// "27" (0x32 0x37) -> 0x27
	// "2" (0x32) -> 0x02
	public static byte[] asciiToBcd(byte[] ascii) {

		byte[] input = Arrays.copyOf(ascii, ascii.length);
		if (ascii.length % 2 == 1) {
			input = concat(new byte[] { 0x30 }, ascii);
		}

		byte[] out = new byte[input.length / 2];

		for (int i = 0; i < out.length; i++) {
			out[i] = (byte) ((input[2 * i] & 0x0F) << 4 | (input[2 * i + 1] & 0x0F));
		}

		return out;
	}

	// "F" (0x46) --> 0x0F
	// "2F" (0x32 0x46) --> 0x2F
	// "B2F" (0x42 0x32 0x46) --> 0x0B 0x2F
	public static byte[] asciiToBytes(byte[] ascii) {

		byte[] input = Arrays.copyOf(ascii, ascii.length);
		if (ascii.length % 2 == 1) {
			input = concat(new byte[] { 0x30 }, ascii);
		}
		byte[] out = new byte[input.length / 2];

		for (int i = 0; i < out.length; i++) {
			byte high = (byte) ((input[2 * i] & 0x0F) + ((input[2 * i] <= 0x39) ? 0 : 9));
			byte low = (byte) ((input[2 * i + 1] & 0x0F) + ((input[2 * i + 1] <= 0x39) ? 0 : 9));
			out[i] = (byte) (high << 4 | low);
		}

		return out;
	}

	// 0x02 0x35 -> 235
	public static int bcdToInt(byte[] bcd) {
		int result = 0;
		for (int i = 0; i < bcd.length; i++) {
			result *= 100;
			result += Integer.valueOf(String.format("%02X", bcd[i]));
		}
		return result;
	}

	// 0x0B 0x2F -> "0B2F" (0x30 0x42 0x32 0x46)
	public static byte[] bytesToAscii(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			sb.append(String.format("%02X", bytes[i]));
		}
		return sb.toString().getBytes();
	}

	// lê um bitmap em binario e retorna uma array com os indices (numero do bit -
	// 1) dos bits a serem lidos
	//
	public static int[] processBitmap(byte[] bitmap) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bitmap) {
			sb.append(String.format("%8s", Integer.toUnsignedString(b & 0x00FF, 2)).replace(' ', '0'));
		}
		String bitmapBinary = sb.toString();

		int[] bits = new int[bitmapBinary.replace("0", "").length()];
		for (int i = 0, j = 0; i < bitmapBinary.length(); i++) {
			if (bitmapBinary.charAt(i) == '1') {
				bits[j] = i;
				j++;
				//System.out.print((i+1) + " ");
			}
		}
		//System.out.println();
		return bits;

	}
	
	public static String toPrintableString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			sb.append(String.format("%02X", bytes[i]));
		}
		return sb.toString();
	}

	public static String toCamelCase(String snakeCase) {
		return Pattern.compile("[-|_]([a-z])").matcher(snakeCase).replaceAll(m -> m.group(1).toUpperCase());
	}
//
//	public static void main(String[] args) {
//		byte[] test = new byte[] {0x0B, 0x2F};
//		
//		byte[] out = bytesToAscii(test);
//		
//		for(int i = 0; i < out.length; i++) {
//			System.out.print(String.format("%02X ", out[i]));
//		}
//		
//	}
//	
}
