package com.example.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import com.example.server.modules.Message;
import com.example.server.modules.Structures;
/**
 * Classe che gestisce il server, utilizza i socket per comunicare con i client
 */
public class ServerController {
    /**
     * Metodo per avviare il server sulla porta 8081, mandando in esecuzione il metodo saveMessageFromClient (ultimo metodo in questo file)
     * per salvare i messaggi ricevuti
     *
     */
    public void startServer() {
        System.out.println("Avvio del server sulla porta " + Structures.PORT);

        try (ServerSocket serverSocket = new ServerSocket(Structures.PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                saveMessageFromClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Errore durante l'esecuzione del server: " + e.getMessage());
        }
    }

    /**
     * Metodo per ottenere i dati dal client, in questo caso l'email del destinatario con tutti i dati della mail dall'input stream
     * @param clientSocket
     * @return
     * @throws IOException
     */
    public String getDataFromClient(Socket clientSocket) throws IOException {
        System.out.println("Connessione stabilita con il client: " + clientSocket.getInetAddress());
        String data = "";
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            data = in.readLine(); // Leggi l'email del destinatario
        } catch (IOException e) {
            System.err.println("Errore nella comunicazione con il client: " + e.getMessage());
        }
        if(data != ""){
            return data;
        } else {
            throw new IOException("Failed getting data from client");
        }
    }
    /**
     * Metodo per salvare i messaggi ricevuti dal client nel file data.txt
     * @param message
     * @throws IOException
     */
    public void saveMessage(Message message) throws IOException {
        File file = new File(Structures.FILE_PATH);
        if (!file.exists()) {
            throw new IOException("File does not exists "+ Structures.FILE_PATH);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(message.toString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del file: " + e.getMessage());
        }
    }

    /**
     * Metodo per salvare i messaggi ricevuti dal client, vengono parsificati
     * @param clientSocket
     * @throws IOException
     */
    public void saveMessageFromClient(Socket clientSocket) throws IOException{
        String message = getDataFromClient(clientSocket);
        Message parsed_message = Message.fromLine(message);
        saveMessage(parsed_message);
    }
}
