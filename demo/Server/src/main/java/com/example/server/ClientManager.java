// ClientManager.java
package com.example.server;

import com.example.shared.Mail;
import com.example.shared.Structures;
import com.example.shared.Request;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientManager {

    private final MessageService messageService;
    private final ServerController serverController;
    private final ServerManager serverManager;
    private final Socket clientSocket;

    private static final Object lock = new Object();

    public ClientManager(ServerController serverController, ServerManager serverManager, Socket clientSocket) {
        this.messageService = new MessageService(serverManager);
        this.serverController = serverController;
        this.serverManager = serverManager;
        this.clientSocket = clientSocket;
    }

    public void handleClient() {
        synchronized (lock) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

                String requestJson = reader.readLine();
                Request<?> request = Request.fromJson(requestJson);

                switch (request.getRequestCode()) {
                    case Structures.UPDATE_MAILS -> handleUpdateMails((Request<List<Integer>>) request, writer);
                    case Structures.PING -> handlePing((Request<String>) request, writer);
                    case Structures.SEND_MAIL -> handleSendMail((Request<String>) request, writer);
                    case Structures.DEST_CHECK -> handleDestCheck((Request<String>) request, writer);
                    case Structures.LOGOUT -> handleLogout(writer);
                    case Structures.DELETE -> handleDelete((Request<ArrayList<Integer>>) request, writer);
                    case Structures.LOGIN_CHECK -> handleLoginCheck((Request<String>) request, writer);
                    default -> {
                        serverController.addLog("Codice richiesta non riconosciuto: " + request.getRequestCode());
                        throw new Exception("Codice richiesta sconosciuto");
                    }
                }

            } catch (Exception e) {
                serverController.addLog("Errore nella gestione del client: " + e.getMessage());
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }
    }

    private void handleUpdateMails(Request<List<Integer>> request, BufferedWriter writer) {
        try {
            List<Integer> existingIds = request.getPayload();
            ArrayList<String> newMails = messageService.getMessagesByReceiver(request.getRequestId(), existingIds);
            Request<ArrayList<String>> response = new Request<>(Structures.UPDATE_MAILS, newMails, "SERVER");
            writer.write(response.toJson());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePing(Request<String> request, BufferedWriter writer) {
        try {
            Request<String> response = new Request<>(Structures.PING, "pong", "SERVER");
            writer.write(response.toJson());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSendMail(Request<String> request, BufferedWriter writer) {
        try {
            String mailJson = request.getPayload();
            Mail mail = Mail.fromJson(mailJson);
            List<String> receivers = extractReceiversFromJson(mailJson);
            for (String receiver : receivers) {
                messageService.saveMessage(receiver, mailJson);
            }
            writer.write("SUCCESS");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> extractReceiversFromJson(String mailJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(mailJson);
        JsonNode receiversNode = rootNode.path("receivers");
        List<String> receivers = new ArrayList<>();
        for (JsonNode receiverNode : receiversNode) {
            receivers.add(receiverNode.asText());
        }
        return receivers;
    }

    private void handleDestCheck(Request<String> request, BufferedWriter writer) {
        try {
            String email = request.getPayload();
            boolean userExists = Structures.checkUserExists(email);
            int responseCode = userExists ? Structures.DEST_OK : Structures.DEST_ERROR;
            Request<String> response = new Request<>(responseCode, email, "SERVER");
            writer.write(response.toJson());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogout(BufferedWriter writer) {
        serverController.addLog("Logout request received, resetting file pointer.");
        try {
            writer.write("LOGOUT_SUCCESS");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Request<ArrayList<Integer>> request, BufferedWriter writer) {
        try {
            ArrayList<Integer> mailIds = request.getPayload();
            messageService.deleteMessages(request.getRequestId(), mailIds);
            writer.write("DELETE_SUCCESS");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLoginCheck(Request<String> request, BufferedWriter writer) {
        try {
            String userEmail = request.getPayload();
            boolean userExists = Structures.checkUserExists(userEmail);
            int responseCode = userExists ? Structures.LOGIN_OK : Structures.LOGIN_ERROR;
            Request<String> response = new Request<>(responseCode, userEmail, "SERVER");
            writer.write(response.toJson());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            serverController.addLog("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }
}