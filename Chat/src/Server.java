// Instruções:
// 1. Execute a classe Server.java
// 2. Execute Client.java o número de vezes igual a quantos clientes você desejar
// 3. Mande mensagens com o seguinte formato: mensagem # cliente
// obs: não utilize Ctrl+Alt+N para rodar as classes, o VS Code não reconhece os arquivos

import java.io.*;
import java.util.*;
import java.net.*;

public class Server {

	// Vetor para armazenar clientes
	static Vector<ClientHandler> ClientList = new Vector<>();

	static int clientTotal = 1;

	public static void main(String[] args) throws IOException {

		ServerSocket ss = new ServerSocket(1234);

		Socket soc;

		// Laço para requisição de clientes
		while (true) {
			soc = ss.accept();

			System.out.println("New client request received : " + soc);

			// Streams de entrada e saída
			DataInputStream dis = new DataInputStream(soc.getInputStream());
			DataOutputStream dos = new DataOutputStream(soc.getOutputStream());

			System.out.println("Creating a new handler for this client...");

			ClientHandler ch = new ClientHandler(soc, "client " + clientTotal, dis, dos);

			Thread t = new Thread(ch);

			System.out.println("Adding this client to active client list");

			ClientList.add(ch);

			t.start();

			clientTotal++;

		}
	}
}

class ClientHandler implements Runnable {
	Scanner scn = new Scanner(System.in);
	private String name;
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket soc;
	boolean isLoggedIn;

	public ClientHandler(Socket soc, String name,
			DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
		this.name = name;
		this.soc = soc;
		this.isLoggedIn = true;
	}

	@Override
	public void run() {

		String received;
		while (true) {
			try {

				received = dis.readUTF();

				System.out.println(received);

				if (received.equals("logout")) {
					this.isLoggedIn = false;
					this.soc.close();
					break;
				}

				// parte a string entre mensagem e recipiente
				StringTokenizer st = new StringTokenizer(received, " # ");
				String MsgToSend = st.nextToken();
				String recipient = st.nextToken();

				// Procura o destinatário na lista de clientes
				for (ClientHandler mc : Server.ClientList) {
					// Se encontrado, envia a mensagem para o cliente indicado
					if (mc.name.equals(recipient) && mc.isLoggedIn == true) {
						mc.dos.writeUTF(this.name + " : " + MsgToSend);
						break;
					}
				}
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
		try {
			this.dis.close();
			this.dos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
