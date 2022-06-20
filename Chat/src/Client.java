import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    final static int ServerPort = 1234;

    public static void main(String args[]) throws UnknownHostException, IOException {
        Scanner sc = new Scanner(System.in);

        InetAddress ip = InetAddress.getByName("localhost");

        // Conexão
        Socket soc = new Socket(ip, ServerPort);

        // Declaração das streams de entrada e saída
        DataInputStream dis = new DataInputStream(soc.getInputStream());
        DataOutputStream dos = new DataOutputStream(soc.getOutputStream());

        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    String msg = sc.nextLine();

                    try {
                        // escreve na stream de saída
                        dos.writeUTF(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        // lê a mensagem enviada para o cliente
                        String msg = dis.readUTF();
                        System.out.println(msg);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
}
