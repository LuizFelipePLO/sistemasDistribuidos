//Feito por Bárbara Ville e Luiz Felipe Oliveira
// Instruções:
// 1. Execute a classe Server.java
// 2. Execute Client.java o número de vezes igual a quantos clientes você desejar
// 3. Mande mensagens com o seguinte formato: 
// mensagem # client (índice da ordem que foi criado e.g client 1 se foi o primeiro client a ser criado...)
// 4. escreva "sair" para desativar um cliente
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
		Scanner sc = new Scanner(System.in);
		Socket soc;
		String name;

		// Laço para requisição de clientes
		while (true) {
			soc = ss.accept();

			System.out.println("Nova requisição de cliente recebida: " + soc);

			// Streams de entrada e saída
			DataInputStream dis = new DataInputStream(soc.getInputStream());
			DataOutputStream dos = new DataOutputStream(soc.getOutputStream());

			System.out.println("Criando um controlador para o cliente...");
			System.out.println("Escreva seu nome: ");
			name = sc.nextLine();
			System.out.println("Bem-vindo, " + name + "!");

			ClientHandler ch = new ClientHandler(soc, name, dis, dos);

			Thread t = new Thread(ch);

			System.out.println("Ativando cliente...");

			ClientList.add(ch);

			t.start();

			clientTotal++;

		}
	}
}

class ClientHandler implements Runnable {
	Scanner sc = new Scanner(System.in);
	String name;
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket soc;
	boolean isLoggedIn;

	public ClientHandler(Socket soc, String name,
			DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
		this.soc = soc;
		this.name = name;
		this.isLoggedIn = true;
	}

	@Override
	public void run() {

		String received;
		while (true) {

			try {

				received = dis.readUTF();

				System.out.println(received);

				if (received.equals("sair")) {
					this.isLoggedIn = false;
					this.soc.close();
					break;
				}

				// parte a string entre mensagem e destinatário
				StringTokenizer st = new StringTokenizer(received, "#");
				String MsgToSend = st.nextToken();
				String recipient = st.nextToken();

				// Procura o destinatário na lista de clientes
				for (ClientHandler ch : Server.ClientList) {
					// Se encontrado, envia a mensagem para o cliente indicado
					if (ch.name.equals(recipient) && ch.isLoggedIn == true) {
						ch.dos.writeUTF(this.name + " : " + MsgToSend);
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
