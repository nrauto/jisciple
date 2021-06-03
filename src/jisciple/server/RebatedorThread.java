package jisciple.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

import jisciple.iso8583.MensagemIso;
import jisciple.iso8583.MensagemIsoParser;
import jisciple.iso8583.config.BitConfig;
import jisciple.iso8583.config.MensagemIsoConfig;
import jisciple.iso8583.config.sizeheader.SizeHeaderConfig;
import jisciple.iso8583.enumeration.TipoContagemEnum;
import jisciple.iso8583.enumeration.TipoFormatoEnum;
import jisciple.iso8583.util.Util;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RebatedorThread extends Thread {

	private MensagemIsoConfig isoConfig;
	private SizeHeaderConfig headerConfig;
	private Socket socket;

	public void run() {
		InputStream inputStream = null;
		DataOutputStream outputStream = null;

		try {
			inputStream = socket.getInputStream();
			outputStream = new DataOutputStream(socket.getOutputStream());

			System.out.println("[SimpleThread] conseguiu pegar os I/O streams");

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		byte[] data;

		while (true) {
			try {
				
				data = inputStream.readAllBytes();
				
				if(data == null) {
					socket.close();
					return;
				} else {
					

					System.out.println("Recebendo " + data.length + " bytes na entrada: ");
					
					System.out.println(Util.toPrintableDump(data));
					
					MensagemIso msg = MensagemIsoParser.parseIso(data, isoConfig, headerConfig);					
					System.out.println(msg.prettyPrint());
					
					msg.check();			
					
					System.out.println();
					
					// MTI
					BitConfig mtiConfig = isoConfig.getMtiConfig();
					byte[] mti = new byte[msg.getMti().getValor().length];
					if(mtiConfig.getTipoFormato() == TipoFormatoEnum.ASCII) {
						mti[2] += 0x01; // incrementa 1 nas dezenas do MTI 
					} else {
						mti[1] += 0x10; // o mesmo mas se for binario ou bcd
					}
					msg.getMti().setValor(mti);
					
					// BIT 39
					BitConfig bit39Config = isoConfig.getBitConfig()[38];
					byte[] bit39;
					if(bit39Config.getTipoContagem() == TipoContagemEnum.BYTES) { 
						bit39 = new byte[bit39Config.getTamanho()];
					} else {
						bit39 = new byte[bit39Config.getTamanho()/2 + bit39Config.getTamanho()%2];
					}
					if(isoConfig.getBitConfig()[38].getTipoFormato() == TipoFormatoEnum.ASCII) {
						Arrays.fill(bit39, (byte) 0x30);
					} else {
						Arrays.fill(bit39, (byte) 0x00);
					}
					msg.getDados()[38].setValor(bit39);
					

					System.out.println("REBATENDO: ");
					
					System.out.println(msg.prettyPrint());
					byte[] saida = msg.toByteArray();
					
					System.out.println("Enviando " + saida.length + " bytes na saida:");
					System.out.println(Util.toPrintableDump(saida));
					
					outputStream.write(saida);
					outputStream.flush();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

	}

}
