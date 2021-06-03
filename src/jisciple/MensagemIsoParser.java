package jisciple;

import java.util.Arrays;

import jisciple.iso8583.config.MensagemIsoConfig;
import jisciple.iso8583.config.sizeheader.SizeHeaderConfig;
import jisciple.iso8583.enumeration.TipoContagemEnum;
import jisciple.iso8583.enumeration.TipoFormatoEnum;
import jisciple.iso8583.util.Util;

public class MensagemIsoParser {

	public static MensagemIso parseIso(byte[] rawInput, MensagemIsoConfig config, SizeHeaderConfig sizeHeaderConfig) {
		byte[] input = Arrays.copyOf(rawInput, rawInput.length);
		MensagemIso msg = new MensagemIso();

		msg.setConfig(config);
		msg.setSizeHeader(sizeHeaderConfig);
		
		int numBytesLidos = 0;

		// tamanho no cabeçalho tcp pra debug
		//byte[] tcpHeader = Arrays.copyOfRange(input, numBytesLidos, numBytesLidos + sizeHeaderConfig.getTamanho());
		
		numBytesLidos += sizeHeaderConfig.getTamanho();
		input = Arrays.copyOfRange(input, numBytesLidos, input.length);

		// imprime o tamanho no cabeçalho tcp pra debug
//		int tcpLength;
//		if(sizeHeaderConfig.getTipo().equalsIgnoreCase("BINARIO")) {
//			byte[] hl;
//			if(sizeHeaderConfig.getFormato().equalsIgnoreCase("HL")) {
//				hl = new byte[] {tcpHeader[0], tcpHeader[1]};
//			} else { // LH
//				hl = new byte[] {tcpHeader[1], tcpHeader[0]};
//			}
//			tcpLength = Integer.parseInt(new String(Util.bytesToAscii(hl)), 16);
//		} else {
//			tcpLength = Integer.parseInt(new String(tcpHeader)); // TODO
//		}
		//System.out.println("tcp length: " + tcpLength);
		
		// Header
		Bit header = new Bit(config.getHeaderRxConfig());
		numBytesLidos = parseBit(input, header);
		input = Arrays.copyOfRange(input, numBytesLidos, input.length);
		//System.out.println("header: " + header + ", index = " + numBytesLidos);
		
		// MTI
		Bit mti = new Bit(config.getMtiConfig());
		numBytesLidos = parseBit(input, mti);
		input = Arrays.copyOfRange(input, numBytesLidos, input.length);
		//System.out.println("mti: " + mti + ", index = " + numBytesLidos);
		
		// Bitmap 1 e 2
		Bit bitmap1 = new Bit(config.getBitmapConfig());
		numBytesLidos = parseBit(input, bitmap1);
		input = Arrays.copyOfRange(input, numBytesLidos, input.length);
		//System.out.println("bitmap1: " + bitmap1 + ", index = " + numBytesLidos);
		
		byte[] bitmapCompleto;
		
		if(bitmap1.useBitmap2()) { 
			Bit bitmap2 = new Bit(config.getBitmapConfig());
			numBytesLidos = parseBit(input, bitmap2);
			input = Arrays.copyOfRange(input, numBytesLidos, input.length);
			bitmapCompleto = Util.concat(bitmap1.getValor(), bitmap2.getValor());
			msg.getDados()[0] = bitmap2;
		} else {
			bitmapCompleto = bitmap1.getValor();
		}
		
		// Dados
		if(bitmap1.getTipoFormato() == TipoFormatoEnum.ASCII) {
			bitmapCompleto = Util.asciiToBytes(bitmapCompleto);
		}
		
		int[] bitsParaLer = Util.processBitmap(bitmapCompleto);
		
		if(bitsParaLer[0] == 0) { // ignorar segundo bitmap
			bitsParaLer = Arrays.copyOfRange(bitsParaLer, 1, bitsParaLer.length);
		}
		
		for(int i = 0; i < bitsParaLer.length; i++) {
			//System.out.print(" parseando bit " + (bitsParaLer[i]+1) );
			Bit b = new Bit(config.getBitConfig()[bitsParaLer[i]]);
			numBytesLidos = parseBit(input, b);
			input = Arrays.copyOfRange(input, numBytesLidos, input.length);
			msg.getDados()[bitsParaLer[i]] = b;

			//System.out.println(" ->  " + Util.toPrintableString(b.getValor()) );
			
		}
		
		// Footer
		Bit footer = new Bit(config.getFooterRxConfig());
		numBytesLidos += parseBit(input, footer);
		
		msg.setBitmap1(bitmap1);
		msg.setHeader(header);
		msg.setFooter(footer);
		msg.setMti(mti);
		
		return msg; 
	}

	/**
	 * parseia um bit, retornando o campo bit.valor preenchido e o numero de
	 * posicoes lidas
	 */
	private static int parseBit(byte[] input, Bit bit) {

		byte[] lengthBytes;
		int lengthInt;
		
		if(bit.getTamanho() == 0) {
			return 0;
		}
		
		switch(bit.getTipoTamanho()) {
		case FIXO:
			bit.setValor(Arrays.copyOf(input, bit.getTamanho()));
			return bit.getTamanho();
			
		case LLBCD:
			lengthBytes = Arrays.copyOf(input, 1);
			lengthInt =  Util.bcdToInt(lengthBytes);
			break;
			
		case LLLBCD:
			lengthBytes = Arrays.copyOf(input, 2);
			lengthInt =  Util.bcdToInt(lengthBytes);
			break;
			
		case LLVAR:
			lengthBytes = Arrays.copyOf(input, 2);
			lengthInt = Integer.valueOf(new String(lengthBytes));
			break;
			
		case LLLVAR:
			lengthBytes = Arrays.copyOf(input, 3);
			lengthInt = Integer.valueOf(new String(lengthBytes));
			break;
		default:
			return 0;		
		}
		
		int bytesParaLer = lengthInt;
		if(bit.getTipoContagem() == TipoContagemEnum.NIBBLES) { 
			bytesParaLer = lengthInt/2 + lengthInt%2;
		}
		//System.out.print(String.format("[%d -> %d]", lengthInt, bytesParaLer));
		
		byte[] dado = new byte[bytesParaLer];
		System.arraycopy(input, lengthBytes.length, dado, 0, bytesParaLer);
		
		bit.setValor(dado);
		
		return lengthBytes.length + bytesParaLer;
	}

//			protected TamanhoEnum tipoTamanho; <- FIXO, LLVAR, LLLVAR, LLBCD, LLLBCD
//			protected ContagemEnum tipoContagem; <- BYTE, NIBBLE
//			protected FormatoEnum tipoFormato; <- BINARIO, ASCII
//			protected TipoDadoEnum tipoDado; <- N, AN, ANS
//			protected int tamanho;

}
