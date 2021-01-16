package com.example.Server.Threads;

import Data.Command;
import Data.Commands;
import Data.MessageRequest;
import RMI.RemoteObserver;
import com.example.Server.Data.*;
import com.example.Server.Data.Files.FileWrapper;
import com.example.Server.Data.ToSend.RedirectControl;
import com.example.Server.DataBase.DataBaseLink;
import com.example.Server.Threads.ManageFiles.GetFileTCPThread;
import com.example.Server.Threads.ManageFiles.SendFileTCPThread;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;

public class MessagesThread extends Thread implements ServerUtils, Commands {
    private List<ClientData> clients;
    private List<ChannelData> channels;
    private List<ClientData> newClientData; //TODO: sincronizar
    private List<ChannelData> newChannelData; //TODO: sincronizar
    private ClientData client;
    private DataBaseLink serverDbLink;
    private List<FileWrapper> filesToGet;
    private RedirectControl redirectControl;
    private List<RemoteObserver> remoteObservers;

    /**
     * Constructor
     * @param client
     * @param clients
     * @param channels
     * @param
     */
    public MessagesThread(ClientData client,
                          List<ClientData> clients,
                          List<ChannelData> channels,
                          List<ClientData> newClientData,
                          List<ChannelData> newChannelData,
                          List<FileWrapper> filesToGet,
                          RedirectControl redirectControl,
                          List<RemoteObserver> remoteObservers) {
        this.clients = clients;
        this.client = client;
        this.channels = channels;
        this.serverDbLink = null;
        this.filesToGet = filesToGet;
        this.newClientData = newClientData;
        this.newChannelData = newChannelData;
        this.redirectControl = redirectControl;
        this.remoteObservers = remoteObservers;
    }

    /**
     * Main method from thread
     */
    @Override
    public void run() {
        try {

            // Initialize Database
            serverDbLink = new DataBaseLink();

            //TODO: retirar e inicializar a thread de recessão de mensagens aqui
            // fazer com que essa thread tenha o código necssário a tratar de todas as respostas
            do {
                MessageRequest requestFromClient = (MessageRequest) client.getOIS().readObject();

                deployMessageToObservers(requestFromClient);

                if (requestFromClient.getCmd() != null) {
                    switch (requestFromClient.getCmd().getCommand()) {
                        case EXIT_CMD:
                            client.getOOS().writeObject(leaveChannel(requestFromClient.getNameOrigin(),
                                    requestFromClient.getCmd()));
                            break;
                        case SEND_FILE_CMD:
                            client.getOOS().writeObject(receiveFile(requestFromClient.getCmd().getArguments().get(0),
                                    requestFromClient.getUsernameTarget(),
                                    requestFromClient.getNameOrigin(),
                                    requestFromClient.getCmd()));
                            deployMessageTo(requestFromClient);
                            break;
                        case GET_FILE_CMD:
                            client.getOOS().writeObject(sendFile(requestFromClient.getCmd().getArguments().get(0),
                                    requestFromClient.getUsernameTarget(),
                                    requestFromClient.getNameOrigin(),
                                    requestFromClient.getCmd()
                            ));
                            deployMessageTo(requestFromClient);
                            break;
                        case JOIN_CHANNEL_CMD:
                            client.getOOS().writeObject(joinChannel(requestFromClient.getCmd().getArguments().get(0),
                                    requestFromClient.getCmd().getArguments().get(1),
                                    requestFromClient.getNameOrigin(),
                                    requestFromClient.getCmd()));
                            break;
                        case CREATE_CHANNEL_CMD:
                            client.getOOS().writeObject(createChannel(requestFromClient.getCmd().getArguments().get(0),
                                    requestFromClient.getCmd().getArguments().get(1),
                                    requestFromClient.getCmd().getArguments().get(2),
                                    requestFromClient.getNameOrigin(),
                                    requestFromClient.getCmd()));
                            break;
                        case EDIT_CHANNEL_CMD:
                            client.getOOS().writeObject(editChannel(requestFromClient.getCmd().getArguments().get(0),
                                    requestFromClient.getCmd().getArguments().get(1),
                                    requestFromClient.getCmd().getArguments().get(2),
                                    requestFromClient.getCmd().getArguments().get(3),
                                    requestFromClient.getNameOrigin(),
                                    requestFromClient.getCmd()));
                            break;
                        case DELETE_CHANNEL_CMD:
                            client.getOOS().writeObject(deleteChannel(requestFromClient.getCmd().getArguments().get(0),
                                    requestFromClient.getCmd().getArguments().get(1),
                                    requestFromClient.getNameOrigin(),
                                    requestFromClient.getCmd()));
                            break;
                        case LIST_PRIVATE_MSG_CMD:
                            client.getOOS().writeObject(listPMs(requestFromClient.getCmd().getArguments().get(0),
                                    requestFromClient.getNameOrigin(),
                                    requestFromClient.getUsernameTarget(),
                                    requestFromClient.getCmd()));
                            break;
                        case LIST_MESSAGES_CMD:
                            client.getOOS().writeObject(listMsgs(requestFromClient.getCmd().getArguments().get(0),
                                    requestFromClient.getNameOrigin(),
                                    requestFromClient.getCmd()));
                            break;
                        case LIST_FILES_CMD:
                            client.getOOS().writeObject(listFiles(requestFromClient.getCmd().getArguments().get(0),
                                    requestFromClient.getNameOrigin(),
                                    requestFromClient.getCmd()));
                            break;
                        case LIST_USER_CHANNELS_CMD:
                        case LIST_ALL_CHANNELS_CMD:
                            client.getOOS().writeObject(getChannelsInformation(requestFromClient.getNameOrigin(),
                                    requestFromClient.getCmd()));
                            break;
                        default:
                            break;
                    }
                }
                else {
                    deployMessageTo(requestFromClient);
                }
            } while(true);
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void deployMessageToObservers(MessageRequest requestFromClient) {
        if(requestFromClient.getCmd() == null || requestFromClient.getCmd().getCommand() == null || requestFromClient.getCmd().getCommand() == ""){
            System.out.println("entrei aqui 1");
            String channelName = "";
            for(ChannelData channel : newChannelData){
                if (channel.containsClient(client)) {
                    channelName = channel.getName();
                }
            }
            System.out.println("entrei aqui 2 " + channelName);
            for(RemoteObserver remoteObserver : remoteObservers){
                try {
                    if(remoteObserver.isGetNotificacao()){
                        System.out.println("entrei aqui 3");
                        remoteObserver.printNotificacao(requestFromClient, channelName);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Deploys the message received from a user to all other users on the same channel he is in by calling the "deployMessage" method
     * @param requestFromClient
     * @throws SQLException
     */
    private void deployMessageTo(MessageRequest requestFromClient) throws SQLException {
        MessageData newMessage;
        for (ChannelData channel : newChannelData) {
            if (channel.containsClient(client)) {
                if (requestFromClient.getCmd() != null && (requestFromClient.getCmd().getCommand().equals(SEND_FILE_CMD) || requestFromClient.getCmd().getCommand().equals(GET_FILE_CMD)))
                    newMessage = new MessageData(requestFromClient.getMessage() + requestFromClient.getCmd().getArguments().get(0),
                            client.getName(),
                            channel.getName(),
                            null,
                            LocalDateTime.now(),
                            false,
                            true);
                else {
                    newMessage = new MessageData(requestFromClient.getMessage(),
                            client.getName(),
                            channel.getName(),
                            null,
                            LocalDateTime.now(),
                            false,
                            false);
                    saveMessage(newMessage);
                }
                channel.addMessage(newMessage);
                deployMessage(channel.getClients(), requestFromClient, newMessage.isPrivate());
                return;
            }
        }

        List<ClientData> clientTemp = new ArrayList<>();

        for (ClientData c : clients) {
            if (c.getName().equals(requestFromClient.getUsernameTarget())) {
                if (requestFromClient.getCmd() != null && (requestFromClient.getCmd().getCommand().equals(SEND_FILE_CMD) || requestFromClient.getCmd().getCommand().equals(GET_FILE_CMD)))
                    newMessage = new MessageData(requestFromClient.getMessage() + requestFromClient.getCmd().getArguments().get(0),
                            client.getName(),
                            null,
                            requestFromClient.getUsernameTarget(),
                            LocalDateTime.now(),
                            true,
                            true);
                else
                    newMessage = new MessageData(requestFromClient.getMessage(),
                            client.getName(),
                            null,
                            requestFromClient.getUsernameTarget(),
                            LocalDateTime.now(),
                            true,
                            false);
                client.addMessage(newMessage);
                saveMessage(newMessage);
                clientTemp.add(c);
                deployMessage(clientTemp, requestFromClient, newMessage.isPrivate());
                return;
            }
        }

        try {
            client.getOOS().writeObject(generateMessageRequest("User not found for private message", SERVER, false, client.getName(), null));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Deploys the message received from a user to all other users on the same channel he is in
     * @param clients
     * @param requestFromClient
     */
    private void deployMessage(List<ClientData> clients, MessageRequest requestFromClient, boolean isPrivate) {
        try {
            for (ClientData client : clients) {
                if (!client.getName().equals(requestFromClient.getNameOrigin())) {
                    if (requestFromClient.getCmd() != null && requestFromClient.getCmd().getCommand().equals(SEND_FILE_CMD)) {
                        if (isPrivate) {
                            client.getOOS().writeObject(generateMessageRequest(requestFromClient.getMessage() + " '" + requestFromClient.getCmd().getArguments().get(0) + "'",
                                    requestFromClient.getNameOrigin(),
                                    true,
                                    client.getName(),
                                    requestFromClient.getCmd()));
                        } else {
                            client.getOOS().writeObject(generateMessageRequest("Sent a file '" + requestFromClient.getCmd().getArguments().get(0) + "'",
                                    requestFromClient.getNameOrigin(),
                                    true,
                                    client.getName(),
                                    requestFromClient.getCmd()));
                        }
                    }
                    else if (requestFromClient.getCmd() != null && requestFromClient.getCmd().getCommand().equals(GET_FILE_CMD)) {
                        if (isPrivate) {
                            client.getOOS().writeObject(generateMessageRequest(requestFromClient.getMessage() + " '" + requestFromClient.getCmd().getArguments().get(0) + "'",
                                    requestFromClient.getNameOrigin(),
                                    true,
                                    client.getName(),
                                    requestFromClient.getCmd()));
                        }
                    }
                    else {
                        client.getOOS().writeObject(generateMessageRequest(requestFromClient.getMessage(),
                                requestFromClient.getNameOrigin(),
                                true,
                                client.getName(),
                                null));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Analyses a request from a client that is trying to create a channel
     * @param channelName
     * @param channelPassword
     * @param nameOrigin
     * @param cmd
     * @return MessageRequest object
     */
    private MessageRequest createChannel(String channelName, String channelPassword, String channelDescription, String nameOrigin, Command cmd) throws SQLException {
        boolean operationSuccessful;
        MessageRequest messageRequest;
        ChannelData tempChannel = new ChannelData(channelName, channelPassword, channelDescription, client.getName());

        operationSuccessful = addChannelToList(tempChannel);

        if (operationSuccessful) {
            messageRequest =  generateMessageRequest("Channel successfully created.", SERVER,true, nameOrigin, cmd);
            tempChannel.setChannelId(saveChannel(channelName, channelPassword, channelDescription, client.getName()));
        }
        else {
            messageRequest = generateMessageRequest("Channel name already taken, please select another name for the channel.", SERVER,false, nameOrigin, cmd);
        }

        return messageRequest;
    }

    /**
     * Analyses a request from a client that is trying to log into a channel
     * @param channelName
     * @param channelPassword
     * @param nameOrigin
     * @param cmd
     * @return MessageRequest object
     */
    private MessageRequest joinChannel(String channelName, String channelPassword, String nameOrigin, Command cmd) {
        System.out.println("\n\n\n" + channels.toString() + "\n\n\n");
        if (newChannelData.isEmpty()) {
            return generateMessageRequest("Channel list is empty.", SERVER,false, nameOrigin, cmd);
        }
        for (ChannelData c : newChannelData) {
            if (c.getName().equals(channelName)) {
                if (c.getPassword().equals(channelPassword)) {
                    if (c.containsClient(client)) {
                        return generateMessageRequest("You are already in the channel you have tried to log into.", SERVER,false, nameOrigin, cmd);
                    }
                    else {
                        // Add client to the channel
                        c.addClient(client);

                        // Remove client from other channel
                        for (ChannelData i : newChannelData) {
                            if (i.containsClient(client)
                                    && !i.getName().equals(channelName)) {
                                i.removeClient(client);
                            }
                        }

                        return generateMessageRequest("Log into channel successful.", SERVER,true, nameOrigin, cmd);
                    }
                }
                else {
                    String message = "Invalid password for channel " + channelName + ".";
                    return generateMessageRequest(message, SERVER,false, nameOrigin, cmd);
                }
            }
        }
        return generateMessageRequest("Invalid channel name.", SERVER, false, nameOrigin, cmd);
    }

    /**
     * Verifies if a user can delete a channel
     * If he can, updates the channel in the database and notify other clients
     * If not sends an error message
     * @param channelName
     * @param channelPassword
     * @param nameOrigin
     * @param cmd
     * @return MessageRequest object
     */
    private MessageRequest deleteChannel(String channelName, String channelPassword, String nameOrigin, Command cmd) throws SQLException, IOException {
        ChannelData channelData = null;
        if (newChannelData.isEmpty()) {
            return generateMessageRequest("Channel list is empty.", SERVER,false, nameOrigin, cmd);
        }
        for (ChannelData c : newChannelData) {
            if (c.getName().equals(channelName)) {
                if (c.getPassword().equals(channelPassword)) {
                    if (c.getCreatorName().equals(client.getName())) {
                        channelData = c;
                    }
                    else {
                        return generateMessageRequest("You have no permissions on this channel to delete it!", SERVER,false, nameOrigin, cmd);
                    }
                }
                else {
                    String message = "Invalid password for channel " + channelName + ".";
                    return generateMessageRequest(message, SERVER,false, nameOrigin, cmd);
                }
            }
        }
        if (channelData == null)
            return generateMessageRequest("Invalid channel name.", SERVER, false, nameOrigin, cmd);
        else {
            //TODO: notify clients
            for(ClientData clientData : channelData.getClients()){
                clientData.getOOS().writeObject(generateMessageRequest("Channel deleted by owner.", SERVER, true, nameOrigin, cmd));
            }
            newChannelData.remove(channelData);
            channelData.clearClients();
            deleteChannel(channelData.getName());

            return generateMessageRequest("Channel deleted successfully.", SERVER, true, nameOrigin, cmd);
        }
    }

    /**
     * Verifies if a user can edit a channel
     * If he can updates either the channel name and/or the description
     * If not returns an error
     * @param oldChannelName
     * @param channelPassword
     * @param newChannelName
     * @param channelDescription
     * @param nameOrigin
     * @param cmd
     * @return MessageRequest object
     */
    private MessageRequest editChannel(String oldChannelName, String channelPassword, String newChannelName, String channelDescription, String nameOrigin, Command cmd) throws SQLException {
        if (client == null)
            return generateMessageRequest("[Error] You are not logged in this server!", SERVER, false, nameOrigin, cmd);

        if (newChannelData.isEmpty())
            return generateMessageRequest("[Error] There are no channels on any server, please be the first to create one :)", SERVER, false, nameOrigin, cmd);

        if (!validateNewChannelName(oldChannelName, newChannelName))
            return generateMessageRequest("[Error] There's already a channel with the new name you've selected", SERVER, false, nameOrigin, cmd);

        for (ChannelData c : newChannelData) {
            if (c.verifyName(oldChannelName)) {
                if (c.verifyCreatorName(nameOrigin)) {
                    if (c.verifyPassword(channelPassword)) {
                        c.setName(newChannelName);
                        c.setDescription(channelDescription);
                        updateChannelInformation(oldChannelName, newChannelName, channelDescription);
                        return generateMessageRequest("Edit channel operation successful", SERVER, true, nameOrigin, cmd);
                    }
                    else {
                        return generateMessageRequest("[Error] The password you've inserted doesn't match with the channel's password", SERVER, false, nameOrigin, cmd);
                    }
                }
                else {
                    return generateMessageRequest("[Error] You are not the host to this channel", SERVER, false, nameOrigin, cmd);
                }
            }
        }
        return generateMessageRequest("[Error] There's no channel with the name you've selected", SERVER, false, nameOrigin, cmd);
    }

    /**
     * Receives a file to upload from the user and created a thread to do it in the background
     * @param fileName
     * @param usernameTarget
     * @return MessageRequest object
     */
    private MessageRequest receiveFile(String fileName, String usernameTarget, String nameOrigin, Command cmd) throws SQLException {
        if (fileName.isEmpty() || fileName.equals("")) {
            return generateMessageRequest("Invalid file.", SERVER,false, nameOrigin, cmd);
        }

        GetFileTCPThread getFileTCPThread = new GetFileTCPThread(client.getIp(), client.getPortTCP(), client.getPortSendFile(), fileName);
        getFileTCPThread.start();

        if (usernameTarget == null || usernameTarget.equals("")) {
            for (ChannelData channelData : newChannelData) {
                if (channelData.containsClient(client)) {
                    filesToGet.add(new FileWrapper(fileName, null, channelData.getName()));
                    MessageData messageData = new MessageData(cmd.getArguments().get(0), nameOrigin, channelData.getName(), "", LocalDateTime.now(), false, true);
                    channelData.addMessage(messageData); // Add message (file) to channel chat
                    client.addNewFile(messageData); // Multicast download helper
                    saveMessage(messageData); // Save on db
                }
            }
        }
        else {
            filesToGet.add(new FileWrapper(fileName, usernameTarget, null));
        }

        return generateMessageRequest("Send file request successful.", SERVER,false, nameOrigin, cmd);
    }

    /**
     * sendFile
     * @param fileName
     * @param usernameTarget
     * @param nameOrigin
     * @param cmd
     * @return
     */
    private MessageRequest sendFile(String fileName, String usernameTarget, String nameOrigin, Command cmd) {
        if (fileName.isEmpty() || fileName.equals("")) {
            return generateMessageRequest("Invalid file.", SERVER,false, nameOrigin, cmd);
        }

        SendFileTCPThread sendFileTCPThread = new SendFileTCPThread(client.getPortSendFilesClient(), client.getIp(), client.getPortTCP());
        sendFileTCPThread.start();

        if (usernameTarget == null || usernameTarget.equals("")) {
            for (ChannelData channelData : newChannelData) {
                if (channelData.containsClient(client)) {
                    MessageData messageData = new MessageData(cmd.getArguments().get(0), nameOrigin, channelData.getName(), "", LocalDateTime.now(), false, true);
                    channelData.addMessage(messageData); // Add message (file) to channel chat
                }
            }
        }

        return generateMessageRequest("Get file request successful.", SERVER,false, nameOrigin, cmd);
    }

    /**
     * Validates a channel name based on a new name inserted by the user
     * @param oldChannelName
     * @param newChannelName
     * @return true if the name is valid, false if it is not
     */
    private boolean validateNewChannelName(String oldChannelName, String newChannelName) {
        if (newChannelName != null && !newChannelName.equals("") && !oldChannelName.equals(newChannelName)) {
            for (ChannelData c : newChannelData) {
                if (c.getName().equals(newChannelName)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Removes a client from their current channel
     * @param usernameTarget
     * @param cmd
     * @return MessageRequest object
     */
    private MessageRequest leaveChannel(String usernameTarget, Command cmd) {
        for (ChannelData c : newChannelData) {
            c.removeClient(client);
        }

        for (ChannelData c : newChannelData) {
            if (c.containsClient(client))
                // String message, String nameOrigin, boolean isSuccess, String usernameTarget
                return generateMessageRequest("[Error] User still in channel " + c.getName() + ".", SERVER, false, usernameTarget, cmd);
        }

        return generateMessageRequest("Exit from channel operation successful", SERVER, true, usernameTarget, cmd);
    }

    /**
     * Adds a client to a channel
     * @param tempChannel
     * @return true if the client can be added to the list, false if it is not
     */
    private boolean addChannelToList(ChannelData tempChannel) {
        if (newChannelData.size() == 0) {
            tempChannel.addClient(client);
            newChannelData.add(tempChannel);

            return true;
        }

        for (ChannelData c : newChannelData) {
            if (c.getName().equals(tempChannel.getName())) {
                return false;
            }
        }

        for (ChannelData c : newChannelData) {
            c.removeClient(client);
        }

        tempChannel.addClient(client);
        newChannelData.add(tempChannel);

        return true;
    }

    /**
     * Creates a MessageRequest object
     * @param message
     * @param nameOrigin
     * @param isSuccess
     * @param usernameTarget
     * @return MessageRequest object
     */
    private MessageRequest generateMessageRequest(String message, String nameOrigin, boolean isSuccess, String usernameTarget, Command cmd) {
        System.out.println("ipUDPRedirect: " + redirectControl.getIpUDPRedirect() + ", portUDPRedirect: " + redirectControl.getPortUDPRedirect());
        return new MessageRequest(message,
                nameOrigin,
                LocalDateTime.now(),
                usernameTarget,
                isSuccess,
                cmd,
                redirectControl.getIpUDPRedirect(),
                redirectControl.getPortUDPRedirect());
    }

    /**
     * listPMs
     * @param nrMsgs
     * @param nameOrigin
     * @param usernameTarget
     * @param cmd
     * @return
     */
    private MessageRequest listPMs(String nrMsgs, String nameOrigin, String usernameTarget, Command cmd) throws SQLException {
        StringBuilder strReturn = new StringBuilder("\n");
        List<MessageData> messagesData = serverDbLink.loadPrivateMessages(nameOrigin, usernameTarget);

        //System.out.println("nr mensagens " + messagesData.size());
        if (messagesData.size() == 0)
            return generateMessageRequest("You haven't traded PMs with user " + usernameTarget, SERVER, true, nameOrigin, cmd);

        if (parseInt(nrMsgs) < messagesData.size()) {
            for (int i = parseInt(nrMsgs) - 1; i >= 0; i--) { //TODO: trocar a ordem
                strReturn.append(messagesData.get(messagesData.size() - i).getOriginName()).append(" to ").append(messagesData.get(messagesData.size() - i).getUsernameTarget()).append(": ").append(messagesData.get(messagesData.size() - i).getMessage()).append("\n");
            }
        }
        else {
            for (MessageData messageData : messagesData) {
                strReturn.append(messageData.getOriginName()).append(" to ").append(messageData.getUsernameTarget()).append(": '").append(messageData.getMessage()).append("'\n");
            }
        }

        System.out.println(strReturn);
        return generateMessageRequest(strReturn.toString(), SERVER, true, nameOrigin, cmd);
    }

    /**
     * listMsgs
     * @param nrMsgs
     * @param nameOrigin
     * @param cmd
     * @return
     */
    //TODO: para já está a usar a lista "desatualizada" de canais
    private MessageRequest listMsgs(String nrMsgs, String nameOrigin, Command cmd) throws SQLException {
        StringBuilder strReturn = new StringBuilder("\n");

        for (ChannelData channel : channels) {
            if (channel.containsClient(client)) {
                List<MessageData> messagesData = serverDbLink.loadChannelMessages(channel.getName());
                if (messagesData.size() <= 0)
                    return generateMessageRequest("The conversation in this channel is yet to begin. Try to engage with other users :). ", SERVER, true, nameOrigin, cmd);

                if (parseInt(nrMsgs) < messagesData.size()) {
                    for (int i = parseInt(nrMsgs) - 1; i >= 0; i--) { //TODO: trocar a ordem
                        strReturn.append(messagesData.get(i).getOriginName()).append(": "). append(messagesData.get(i).getMessage()).append("\n");
                    }
                    return generateMessageRequest(strReturn.toString(), SERVER, true, nameOrigin, cmd);
                }
                else {
                    for (MessageData messageData : messagesData) {
                        strReturn.append(messageData.getOriginName()).append(": "). append(messageData.getMessage()).append("\n");
                    }
                    return generateMessageRequest(strReturn.toString(), SERVER, true, nameOrigin, cmd);
                }
            }
        }

        return generateMessageRequest("[Error] You are not in a channel!", SERVER, false, nameOrigin, cmd);
    }

    /**
     * listFiles
     * @param channelName
     * @param nameOrigin
     * @param cmd
     * @return
     */
    private MessageRequest listFiles(String channelName, String nameOrigin, Command cmd) {
        StringBuilder strReturn = new StringBuilder("\n");
        try {
            List<String> fileNames = serverDbLink.getAllFilesInAChannel(channelName);

            for (String str : fileNames)
                strReturn.append("\t").append(str).append("\n");

            return generateMessageRequest(strReturn.toString(), SERVER, true, nameOrigin, cmd);
        } catch (SQLException e) {
            strReturn.append("[Error] This channel has not files available yet!");
        }
        return generateMessageRequest(strReturn.toString(), SERVER, false, nameOrigin, cmd);
    }

    /**
     * getChannelsInformation
     * @param nameOrigin
     * @param cmd
     * @return
     */
    private MessageRequest getChannelsInformation(String nameOrigin, Command cmd) throws SQLException {
        StringBuilder strReturn = new StringBuilder("\n");
        if (cmd.getCommand().equals(LIST_USER_CHANNELS_CMD)) {
            HashMap<String, String> userChannels = serverDbLink.getAllClientChannels(nameOrigin);
            for (String key : userChannels.keySet())
                strReturn.append(key).append(" (").append(userChannels.get(key)).append(")\n");
        } else {
            for (ChannelData channelData : newChannelData)
                strReturn.append(channelData.getName()).append(" (").append(channelData.getDescription()).append(")\n");
        }
        return generateMessageRequest(strReturn.toString(), SERVER, true, nameOrigin, cmd);
    }

    /**
     * Saves a channel created from the user
     * @param channelName
     * @param channelPassword
     * @param channelDescription
     * @param userName
     * @throws SQLException
     */
    private long saveChannel(String channelName, String channelPassword, String channelDescription, String userName) throws SQLException {
        System.out.println("Saving channel " + channelName + "...");
        return serverDbLink.saveChannel(channelName, channelPassword, channelDescription, userName);
    }

    /**
     * Delete a channel for the database
     * @param channelName
     * @throws SQLException
     */
    private void deleteChannel(String channelName) throws SQLException {
        serverDbLink.deleteChannel(channelName);
    }

    /**
     * Update the channel information in the database
     * @param oldChannelName
     * @param newChannelName
     * @param channelDescription
     * @throws SQLException
     */
    private void updateChannelInformation(String oldChannelName, String newChannelName, String channelDescription) throws SQLException {
        serverDbLink.editChannelByOldName(oldChannelName, newChannelName, channelDescription);
    }

    /**
     * Save a message in the database
     * @param message
     */
    private void saveMessage(MessageData message) throws SQLException {
        serverDbLink.saveMessage(message);
    }
}
