package com.example.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import com.example.server.modules.Structures;

public class ServerController {

    public void startServer() {
        System.out.println("Avvio del server sulla porta " + Structures.PORT);
        /**
         * Documentazione socket:
         * accept() Accetta una connessione in entrata, è un metodo della classe ServerSocket
         * hanleClient() chiama il metodo più in basso, che si occupa di gestire la comunicazione con il client
         */
        /**
         * Documentazione socket:
         * getInputStream() Restituisce un flusso di input per leggere i dati inviati dal client
         * getOutputStream() Restituisce un flusso di output per inviare i dati al client
         */
        try (ServerSocket serverSocket = new ServerSocket(Structures.PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                getDataFromClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Errore durante l'esecuzione del server: " + e.getMessage());
        }
    }
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
}
