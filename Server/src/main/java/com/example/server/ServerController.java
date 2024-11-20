package com.example.server;

import com.example.server.modules.Message;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerController {
    private static final int PORT = 8081; // Porta del server
    private final MessageService messageService;

    public ServerController() {
        this.messageService = new MessageService();
    }

    public void startServer() {
        System.out.println("Avvio del server sulla porta " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Errore durante l'esecuzione del server: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        System.out.println("Connessione stabilita con il client: " + clientSocket.getInetAddress());
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            String receiverEmail = in.readLine(); // Leggi l'email del destinatario inviata dal client
            List<Message> messages = messageService.getMessagesByReceiver(receiverEmail);

            for (Message message : messages) {
                out.write(message.toString());
                out.newLine();
            }
            out.flush();

        } catch (IOException e) {
            System.err.println("Errore nella comunicazione con il client: " + e.getMessage());
        }
    }
}
