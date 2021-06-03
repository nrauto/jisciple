package iso8583;

import java.nio.ByteBuffer;
import java.util.Arrays;

import iso8583.config.MensagemIsoConfig;
import iso8583.config.sizeheader.SizeHeaderConfig;
import iso8583.enumeration.TipoFormatoEnum;
import iso8583.util.Util;
import lombok.Data;

@Data
public class MensagemIso {
	
	private MensagemIsoConfig config;
	private SizeHeaderConfig sizeHeader;
	
	private Bit mti;
	private Bit header;
	private Bit footer;
	private Bit bitmap1;
	
	private Bit[] dados = new Bit[128];
	// bit 1  = segundo bitmap (indice: dados[0])
		
	public Bit computeBitmap1() {
		Bit b = new Bit(config.getBitmapConfig());
		
		
		long bitmap1 = 0;
		// verifica se habilita o bit 1 (bitmap2)
		for(int i = 64; i < 128 && bitmap1 == 0; i++) {
			if(dados[i] != null && dados[i].getValor() != null) {
				bitmap1 = 1;
			}
		}
		for(int i = 1; i < 64; i++) {
			bitmap1 <<= 1;
			bitmap1 |= (dados[i] != null && dados[i].getValor() != null)?1L:0;
		}
		
		if(b.getTipoFormato() == TipoFormatoEnum.ASCII) {
			b.setValor(Util.bytesToAscii(ByteBuffer.allocate(Long.BYTES).putLong(bitmap1).array()));
		} else {
			b.setValor(ByteBuffer.allocate(Long.BYTES).putLong(bitmap1).array());
		}
		
		return b;
	}
	
	public Bit computeBitmap2() {
		Bit b = new Bit(config.getBitmapConfig());
				
		long bitmap2 = 0;
		for(int i = 64; i < 128; i++) {
			bitmap2 <<= 1;
			bitmap2 |= (dados[i] != null)?1L:0;
		}
		
		if(b.getTipoFormato() == TipoFormatoEnum.BINARIO) {
			b.setValor(ByteBuffer.allocate(Long.BYTES).putLong(bitmap2).array());
		} else {
			b.setValor(Util.bytesToAscii(ByteBuffer.allocate(Long.BYTES).putLong(bitmap2).array()));
		}
		return b;
	}
	
	public byte[] toByteArray() {

		byte[] bytes = Util.concat(header.toIsoBytes(), mti.toIsoBytes());
		
		Bit bitmap1calc = computeBitmap1();
		bytes = Util.concat(bytes, bitmap1calc.toIsoBytes());
		if(bitmap1.useBitmap2()) {
			bytes = Util.concat(bytes, computeBitmap2().toIsoBytes());
		}
		
		for(int i = 1; i < 128; i++) { 
			if(dados[i] != null) {
				byte[] bit = dados[i].toIsoBytes();
				//System.out.println("dado do bit " + (i+1) + " = " + new String(bit));
				bytes = Util.concat(bytes, bit);
			}
		}
		
		// FOOTER
		bytes = Util.concat(bytes, footer.toIsoBytes());
		
		int length = bytes.length;
		//System.out.println("tamanho da msg sem o header = " + length);
		if(sizeHeader.getIncluiProprioTamanho()) {
			//System.out.println("adicionando tam " + sizeHeader.getTamanho() + " ao header de tamanho " + length);
			length += sizeHeader.getTamanho();
			
		}
		
		// HEADER TCP (no começo da msg)
		byte[] tcpHeader; 
		if(sizeHeader.getTipo().equalsIgnoreCase("ASCII")) { 
			String headerStr = String.format("%0" + sizeHeader.getTamanho() + "d", length);
			if(sizeHeader.getFormato().equalsIgnoreCase("LH")) {
				headerStr = headerStr.substring(sizeHeader.getTamanho()/2) + headerStr.substring(0, sizeHeader.getTamanho()/2);
			}
			tcpHeader = headerStr.getBytes();
		} else { // BINARIO
			String headerStr = String.format("%0" + sizeHeader.getTamanho() + "X", length);
			if(sizeHeader.getFormato().equalsIgnoreCase("LH")) {
				headerStr = headerStr.substring(sizeHeader.getTamanho()/2) + headerStr.substring(0, sizeHeader.getTamanho()/2);
			} 
			tcpHeader = Util.asciiToBytes(headerStr.getBytes());
		} // essa gambi de pegar metade do tamanho do header so funciona pra 2 ou 4, mas good enough
		
		bytes = Util.concat(tcpHeader, bytes);
		
		return bytes;
	}
	
	
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();

		String printableHeader = getReadableString(header);
		if(printableHeader != null) {
			sb.append(String.format("%12.12s: [%s]\n", "HEADER", printableHeader));
		}
		sb.append(String.format("%12.12s: [%s]\n", "MTI", getReadableString(mti)));
		
		for(int i = 1; i < 128; i++) {
			if(dados[i] != null && dados[i].getValor() != null) {
				sb.append(String.format("%6d (%3d): [%s]\n", (i+1), dados[i].getValor().length, getReadableString(dados[i])));
			}
		}
		
		String printableFooter = getReadableString(footer);
		if(printableFooter != null) {
			sb.append(String.format("%12.12s: [%s]\n", "FOOTER", getReadableString(footer)));
		}
		
		return sb.toString();
	}
	
	private String getReadableString(Bit b) {
		if(b == null || b.getValor() == null) {
			return null;
		}
		if(b.getTipoFormato() == TipoFormatoEnum.ASCII) {
			return new String(b.getValor());
		} else {
			return Util.toPrintableString(b.getValor());
		}
	}
	
	public void check() {
		// verifica bitmaps
		Bit bitmap1calc = computeBitmap1();
		
		if(Arrays.compare(bitmap1calc.getValor(), bitmap1.getValor()) != 0) {
			// TODO lancar exception no lugar desse print
			System.err.println("Bitmap 1 diferente: calculado [" + getReadableString(bitmap1calc) + "], atual [" + getReadableString(bitmap1) + "]");
		}
		
		if(bitmap1.useBitmap2()) {
			Bit bitmap2calc = computeBitmap2();
			if(dados[0] == null || dados[0].getValor() == null) {
				System.err.println("Bitmap 2 calculado [" + getReadableString(bitmap2calc) + "], atual NULL");
			}
			if(Arrays.compare(bitmap2calc.getValor(), dados[0].getValor()) != 0) {
				System.err.println("Bitmap 2 diferente: calculado [" + getReadableString(bitmap2calc) + "], atual [" + getReadableString(dados[0]) + "]");
			}
		}
		// TODO validar formato dos dados
		
		
	}
	
}
