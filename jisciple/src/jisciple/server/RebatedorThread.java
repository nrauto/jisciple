package jisciple.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import jisciple.iso8583.MensagemIso;
import jisciple.iso8583.MensagemIsoParser;
import jisciple.iso8583.builder.MensagemIsoBuilder;
import jisciple.iso8583.config.MensagemIsoConfig;
import jisciple.iso8583.config.sizeheader.SizeHeaderConfig;
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

			System.out.println("[RebatedorThread] conseguiu pegar os I/O streams");

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		byte[] data;

		while (true) {
			try {

				data = inputStream.readAllBytes();

				if (data == null) {
					socket.close();
					return;
				} else {

					System.out.println("[RebatedorThread] Recebendo " + data.length + " bytes na entrada: ");

					System.out.println(Util.toPrintableDump(data));

					MensagemIso msgRecebida = MensagemIsoParser.parseIso(data, isoConfig, headerConfig);
					System.out.println(msgRecebida.prettyPrint());

					msgRecebida.check();

					System.out.println();
					
					MensagemIso mensagemRebatida = MensagemIsoBuilder.buildMensagemRebatida(msgRecebida, isoConfig);
					
					byte[] saida = mensagemRebatida.toByteArray();

					System.out.println("[RebatedorThread] Enviando " + saida.length + " bytes na saida:");
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
