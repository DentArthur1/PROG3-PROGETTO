package com.example.server;

import com.example.shared.Request;
import com.example.shared.Structures;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerManager implements Runnable {
    private volatile boolean running = false;
    private ServerSocket serverSocket;
    private final ServerController serverController;


    public ServerManager(ServerController serverController) {
        this.serverController = serverController;
    }

    public synchronized void start() {
        if (running) {
            serverController.addLog("The server is already running.");
            return;
        }
        running = true;
        new Thread(this).start();  // Start the server thread
        serverController.addLog("Server started.");
    }

    public synchronized void stop() {
        if (!running) {
            serverController.addLog("The server is not running.");
            return;
        }
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            serverController.addLog("Error closing the server socket: " + e.getMessage());
        }
        serverController.addLog("Server stopped.");
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(Structures.PORT)) {
            this.serverSocket = serverSocket;
            serverController.addLog("Server listening on port " + Structures.PORT);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept(); // Wait for a new client
                    serverController.addLog("Connection received from: " + clientSocket.getInetAddress());

                    new Thread(() -> {
                        // Read client authentication
                        try {
                            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                            Request<?> generic_request = (Request<?>) input.readObject();

                            // Create a new ClientManager for the client
                            ClientManager clientManager = new ClientManager(serverController, this, output, input, clientSocket);
                            clientManager.set_socket(clientSocket);

                            //chiamo handle_client
                            clientManager.handleClient(generic_request);

                        } catch (Exception e) {
                            serverController.addLog("Authentication process failed");
                        }
                    }).start();


                } catch (IOException e) {
                    if (running) {
                        serverController.addLog("Error connecting to client: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            serverController.addLog("Error running the server: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            serverController.addLog("Error during server socket cleanup: " + e.getMessage());
        }
    }



}