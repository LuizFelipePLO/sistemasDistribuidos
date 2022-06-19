// Instruções:
// 1. Execute a classe Server.java
// 2. Execute Client.java o número de vezes igual a quantos clientes você desejar
// 3. Mande mensagens com o seguinte formato: mensagem # remetente

import java.io.*;
import java.util.*;
import java.net.*;

public class Server {

	static Vector<ClientHandler> ar = new Vector<>();

	static int clientTotal = 0;

	public static void main(String[] args) throws IOException {

		try (ServerSocket ss = new ServerSocket(1234)) {
			Socket s;

			// loop infinito para o requisição de cliente
			while (true) {

				s = ss.accept();

				System.out.println("New client request received : " + s);

				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());

				System.out.println("Creating a new handler for this client...");

				ClientHandler mtch = new ClientHandler(s, "client " + clientTotal, dis, dos);

				Thread t = new Thread(mtch);

				System.out.println("Adding this client to active client list");

				ar.add(mtch);

				t.start();

				clientTotal++;

			}
		}
	}
}

class ClientHandler implements Runnable {
	Scanner sc = new Scanner(System.in);
	private String name;
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket s;
	boolean isLoggedIn;

	public ClientHandler(Socket s, String name,
			DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
		this.name = name;
		this.s = s;
	}

	@Override
	public void run() {

		String received;
		while (true) {
			try {
				// receive the string
				received = dis.readUTF();

				System.out.println(received);

				if (received.equals("logout")) {
					this.isLoggedIn = false;
					this.s.close();
					break;
				}

				// divide a string em mensagem e recipiente
				StringTokenizer st = new StringTokenizer(received, "#");
				String MsgToSend = st.nextToken();
				String recipient = st.nextToken();

				// busca se o recipiente está cadastrado no vetor
				for (ClientHandler mc : Server.ar) {
					// se o recipiente for encontrado, devolve a mensagem assim
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
