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
    private String new_user_email;

    // Map to keep track of file pointers for each client
    private final Map<String, Integer> clientFilePointers = new HashMap<>();

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

                    // Read client authentication
                    try {
                        ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                        ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                        Request<?> generic_request = (Request<?>) input.readObject();

                        // Create a new ClientManager for the client
                        ClientManager clientManager = new ClientManager(serverController, this, output, input, clientSocket);
                        clientManager.set_socket(clientSocket);

                        if (generic_request != null && generic_request.getRequestCode() == Structures.LOGIN_CHECK) { // Client logging in for the first time
                            if (handleLoginCheck((Request<String>) generic_request, output)) {
                                // Add new user
                                String new_email = ((Request<String>) generic_request).getPayload();
                                clientFilePointers.put(new_email, 0);
                                serverController.addLog("Client: " + new_email + " encountered for the first time, adding authenticator.");
                            }
                        } else if (clientFilePointers.containsKey(generic_request.getAuthToken())) { // Generic request, retrieve authentication
                            String client_email = generic_request.getAuthToken();
                            serverController.addLog("File pointer retrieved for client: " + client_email + " with value: " + clientFilePointers.get(generic_request.getAuthToken()));
                            clientManager.handleClient(generic_request);
                        } else {
                            throw new Exception("Unknown client");
                        }

                    } catch (Exception e) {
                        serverController.addLog("Authentication process failed");
                    }

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

    public synchronized int getClientFilePointer(String email) {
        return clientFilePointers.get(email);
    }

    public synchronized void updateClientFilePointer(String email, int filePointer) {
        clientFilePointers.put(email, filePointer);
        serverController.addLog("Updating file pointer for: " + email + " to: " + filePointer);
    }

    private boolean handleLoginCheck(Request<String> request, ObjectOutputStream output) throws IOException {
        String userEmail = request.getPayload();
        boolean userExists = Structures.checkUserExists(userEmail);
        int responseCode;
        boolean response_bool;
        if (userExists) {
            serverController.addLog("User " + userEmail + " found. Sending LOGIN_OK.");
            responseCode = Structures.LOGIN_OK;
            response_bool = true;
        } else {
            serverController.addLog("User " + userEmail + " not found. Sending LOGIN_ERROR.");
            responseCode = Structures.LOGIN_ERROR;
            response_bool = false;
        }

        Request<String> response = new Request<>(responseCode, userEmail, "SERVER", 0);
        output.writeObject(response);
        return response_bool;
    }
}