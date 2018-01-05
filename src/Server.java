import java.io.*;
import java.net.*;

class Server
{
    private static final int port = 9001;
    private static String currentClient = "";

    public static void main(String[] args){

        while(true) {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected");


                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream())
                        );

                while(true) {
                    String lastClient = currentClient;
                    System.out.println("Current client: "+currentClient);
                    out.println(currentClient);
                    currentClient = in.readLine();
                    if (currentClient == null) {
                        currentClient = lastClient;
                        break;
                    }
                    System.out.println("Client changed to: " + currentClient);

                }
                System.out.println("Disconnected");

            } catch (Exception ignored) {


            }
        }

    }

}