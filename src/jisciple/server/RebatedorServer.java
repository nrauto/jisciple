package jisciple.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import jisciple.iso8583.config.MensagemIsoConfig;
import jisciple.iso8583.config.sizeheader.SizeHeaderConfig;

public class RebatedorServer {

	private MensagemIsoConfig isoConfig;
	private SizeHeaderConfig headerConfig;
	private int port;
	
	private ServerSocket serverSocket;
	private Socket socket;
	
	public RebatedorServer(MensagemIsoConfig iso, SizeHeaderConfig header, int port) {
		this.isoConfig = iso;
		this.headerConfig = header;
		this.port = port;
	}

	public void run() {

		try {
			serverSocket = new ServerSocket(port);
			System.out.println("socket criado na porta " + port);
		} catch (IOException e) {
			// TODO tratar isso direito
			e.printStackTrace();
		}

		while (true) { // TODO comando pra sair gracefully

			try {
				socket = serverSocket.accept(); // blocante
			} catch (IOException e) {
				// TODO tratar isso direito
				e.printStackTrace();
			}
			new RebatedorThread(isoConfig, headerConfig, socket).start();;
			

		}

	}

}
