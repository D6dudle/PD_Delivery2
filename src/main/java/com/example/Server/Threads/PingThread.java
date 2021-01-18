package com.example.Server.Threads;

import Data.Command;
import Data.MessageRequest;
import com.example.Server.Data.*;
import com.example.Server.Data.Files.FileWrapper;
import com.example.Server.Data.ToSend.*;
import com.example.Server.DataBase.DataBaseLink;
import com.example.Server.Data.ServerStorageData;
import com.example.Server.Threads.ManageFiles.ReceiveFileUDPThread;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PingThread extends Thread implements ServerUtils {
    private MulticastSocket mSocket;
    private InetAddress mGroupIp;
    private ServerStorageData serverStorageData;

    // Database data
    private DataBaseLink serverDbLink;


    /**
     *
     * @param mSocket
     * @param mGroupIp
     * @param serverStorageData
     */
    public PingThread(MulticastSocket mSocket,
                      InetAddress mGroupIp,
                      ServerStorageData serverStorageData) {
        this.mSocket = mSocket;
        this.mGroupIp = mGroupIp;
        this.serverStorageData = serverStorageData;
    }

    /**
     * run
     */
    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        DatagramPacket dP;
        PingRequestData pingRequestData;
        boolean flagNewServer = true;

        try {
            serverDbLink = new DataBaseLink();

            while (true) {
                // If there are other servers send a ping
                ByteArrayOutputStream bOS = new ByteArrayOutputStream();
                ObjectOutputStream oOS = new ObjectOutputStream(bOS);
                pingRequestData = new PingRequestData(new ServerControlData(serverStorageData.getServerIpUDP(),
                        serverStorageData.getPortUDP(),
                        serverStorageData.getPortSendFiles(),
                        convertClients(serverStorageData.getNewClientData()),
                        convertChannels(serverStorageData.getNewChannelData())));

                oOS.writeObject(pingRequestData);
                byte[] bufDPOut = bOS.toByteArray();

                dP = new DatagramPacket(bufDPOut,
                        bufDPOut.length,
                        mGroupIp,
                        PORT_MULTICAST);
                mSocket.send(dP);
                clearNewData();

                //Debug
                //System.out.println("\nEnviei: " + pingRequestData.toString() + ", para: " + " (" + dP.getAddress().getHostAddress() + ":" + dP.getPort() + ")");

                // Wait for other pings
                dP = new DatagramPacket(new byte[10000], 10000);
                mSocket.receive(dP);

                byte[] bufDP = dP.getData();

                ByteArrayInputStream bIS = new ByteArrayInputStream(bufDP);
                ObjectInputStream oIS = new ObjectInputStream(bIS);
                pingRequestData = (PingRequestData) oIS.readObject();

                // Verifies if it's loop
                if (!pingRequestData.getIpUDP().equals(serverStorageData.getServerIpUDP())) {

                    serverStorageData.getRedirectControl().setIpUDPRedirect(pingRequestData.getIpUDP());
                    serverStorageData.getRedirectControl().setPortUDPRedirect(pingRequestData.getPortUDP());

                    //Debug
                    //System.out.println("\nRecebi:" + pingRequestData.toString() + ", de: " + " (" + dP.getAddress().getHostAddress() + ":" + dP.getPort() + ")");

                    // Verifies if it's the first ping, if not, update this server information
                    for (ServerControlData serverControlData : serverStorageData.getControlDataOtherServers()) {
                        if (serverControlData.getIpUDP().equals(pingRequestData.getIpUDP())) {
                            serverControlData.setClients(pingRequestData.getClients());
                            serverControlData.setChannels(pingRequestData.getChannels());
                            flagNewServer = false;
                            saveServerProperties(serverControlData);
                            addNewData(pingRequestData.getClients(), pingRequestData.getChannels());
                        }
                    }
                    if (flagNewServer) {
                        ServerControlData serverControlData = new ServerControlData(pingRequestData.getIpUDP(),pingRequestData.getPortUDP(), pingRequestData.getPortSendFiles(), pingRequestData.getClients(), pingRequestData.getChannels());
                        serverStorageData.getControlDataOtherServers().add(serverControlData);
                        saveServerProperties(serverControlData);
                        addNewData(pingRequestData.getClients(), pingRequestData.getChannels());
                    }

                    flagNewServer = true;

                    updateFileDownloadList(pingRequestData.getServerControlData());
                    getFilesFromOtherServers();
                }

                sleep(2000);
                sendRestMessages();
                clearNewFilesFromClients(serverStorageData.getClients());
            }
            //serverDbLink.close();
        } catch (IOException | ClassNotFoundException | SQLException | InterruptedException e) {
            if(e.getCause() instanceof SocketTimeoutException) {
                System.out.println("Timeout MulticastSocket");
            }
            else{
                e.printStackTrace();
            }
        }
    }

    /**
     * saveServerProperties
     * @param serverControlData
     */
    private void saveServerProperties(ServerControlData serverControlData) throws SQLException {
        for (ChannelDataMulticast channelDataMulticast : serverControlData.getChannels()) {
            if (serverDbLink.existsChannel(channelDataMulticast.getChannelId())) {
                serverDbLink.editChannelById(channelDataMulticast.getChannelId(),
                        channelDataMulticast.getName(),
                        channelDataMulticast.getDescription());
            }
            else {
                serverDbLink.saveChannel(channelDataMulticast.getName(),
                        channelDataMulticast.getPassword(),
                        channelDataMulticast.getDescription(),
                        channelDataMulticast.getCreatorName());
            }

            for (MessageData message : channelDataMulticast.getChat()) {
                if (serverDbLink.existsChannelMessage(message))
                    serverDbLink.saveMessage(message);
            }
        }

        for (ClientDataMulticast client : serverControlData.getClients()) {
            if (!serverDbLink.existsClient(client.getUserName()))
                serverDbLink.saveUser(client.getUserName(),  client.getPassword(),  client.getPathImage(),  client.getIp());

            for (MessageData message : client.getPrivateMessages()) {
                if (serverDbLink.existsPrivateMessage(message))
                    serverDbLink.saveMessage(message);
            }
        }
    }

    /**
     * updateChannels
     * @param localChannels
     * @return
     */
    private List<ChannelDataMulticast> convertChannels(List<ChannelData> localChannels) {
        List<ChannelDataMulticast> channels = new ArrayList<>();

        for (ChannelData channelData : localChannels) {
            ChannelDataMulticast channelDataMulticast =
                    new ChannelDataMulticast(channelData.getChannelId(),
                            channelData.getName(),
                            channelData.getPassword(),
                            channelData.getDescription(),
                            channelData.getCreatorName());
            channelDataMulticast.setClients(convertClients(channelData.getClients()));
            channelDataMulticast.setChat(channelData.getChat());
            channels.add(channelDataMulticast);
        }
        return channels;
    }

    /**
     * convertClients
     * @param localClients
     * @return
     */
    private List<ClientDataMulticast> convertClients(List<ClientData> localClients) {
        List<ClientDataMulticast> clients = new ArrayList<>();

        if(localClients != null){
            for (ClientData clientData : localClients) {
                clients.add(new ClientDataMulticast(clientData.getIp(),
                        clientData.getPortTCP(),
                        clientData.getName(),
                        clientData.getPassword(),
                        clientData.getPathImage(),
                        clientData.getNewFiles()));
            }
        }

        return clients;
    }

    /**
     * clearNewFilesFromClients
     * @param localClients
     */
    private void clearNewFilesFromClients(List<ClientData> localClients) {
        if(localClients != null)
            for (ClientData clientData : localClients) {
                clientData.clearNewFiles();
            }
    }

    /**
     * updateFileDownloadList
     * @param serverControlData
     */
    private void updateFileDownloadList(ServerControlData serverControlData){

        for(ClientDataMulticast clientDataMulticast : serverControlData.getClients()){
            //TODO: synchronized
            for(MessageData newFile : clientDataMulticast.getNewFiles()){
                System.out.println("newFile: " + newFile.toString());
                if(newFile.getUsernameTarget() == null || newFile.getUsernameTarget().equals(""))
                    serverStorageData.getFilesToGetFromServers().add(new FileWrapper(newFile.getMessage(),
                            "",
                            newFile.getChannelTarget(),
                            serverControlData.getPortSendFiles(),
                            serverControlData.getIpUDP(),
                            clientDataMulticast.getIp(),
                            clientDataMulticast.getPortTCP()));
                else
                    serverStorageData.getFilesToGetFromServers().add(new FileWrapper(newFile.getMessage(),
                            newFile.getUsernameTarget(),
                            "",
                            serverControlData.getPortSendFiles(),
                            serverControlData.getIpUDP(),
                            clientDataMulticast.getIp(),
                            clientDataMulticast.getPortTCP()));
            }
            clientDataMulticast.clearNewFiles();
        }
    }

    /**
     * getFilesFromOtherServers
     */
    private void getFilesFromOtherServers(){
        //TODO: synchronized
        if(serverStorageData.getFilesToGetFromServers() != null && serverStorageData.getFilesToGetFromServers().size() > 0)
        for(FileWrapper fileWrapper : serverStorageData.getFilesToGetFromServers()){
            System.out.println("\n\nGetting a file from other server...");
            ReceiveFileUDPThread receiveFileUDPThread = new ReceiveFileUDPThread(fileWrapper);
            receiveFileUDPThread.start();
        }
    }

    /**
     *
     */
    private void clearNewData(){

        boolean newClient = true;
        boolean newChannel = true;

        if(serverStorageData.getNewClientData() != null)
            for(ClientData client : serverStorageData.getNewClientData()){
                for(ClientData clientSave : serverStorageData.getClients()){
                    if(client.getName().equals(clientSave.getName())){
                        clientSave.getPrivateMessages().addAll(client.getPrivateMessages()); //adds new messages
                        client.getPrivateMessages().clear(); //clears new messages
                        newClient = false;
                        break;
                    }
                }

                if(newClient){
                    serverStorageData.getClients().add(client);
                }
                else{
                    newClient = true;
                }
            }

        for(ChannelData channel : serverStorageData.getNewChannelData()){
            for(ChannelData channelSave : serverStorageData.getChannels()){
                if(channel.getName().equals(channelSave.getName())){
                    channelSave.getChat().addAll(channel.getChat()); //adds new messages
                    //channelSave.getClients().clear(); //clears old clients
                    //channelSave.getClients().addAll(channel.getClients()); //add remaining clients in chat
                    channel.getChat().clear(); //clears new messages
                    newChannel = false;
                    break;
                }
            }

            if(newChannel){
                serverStorageData.getChannels().add(channel);
            }
            else{
                newChannel = true;
            }
        }
    }

    /**
     * Toda a informação de outro servidor é adicionada a este servidor com a excepção de:
     * a) Clientes em Canais --> os canais presentes nas listas deste servidor, apenas terão:
     *  1. Clientes que se autenticaram neste sevidor e no respetivo canal
     *  2.Clientes que foram redirecionados pelo mecanismo de failover de outro servidor
     * b) Clientes --> as listas de Clientes deste servidor apenas terão clientes que se autenticaram neste servidor (normalmente ou por rebalance).
     * Porém, esta mesma informação é armazenada em BD, para que futuramente se possam autenticar neste servidor sem registo
     * @param newClientDataFromOtherServer
     * @param newChannelDataFromOtherServer
     */
    private void addNewData(List<ClientDataMulticast> newClientDataFromOtherServer, List<ChannelDataMulticast> newChannelDataFromOtherServer) throws IOException, SQLException {

        if(newClientDataFromOtherServer.size() > 0)
            addNewClientData(newClientDataFromOtherServer);
        if(newChannelDataFromOtherServer.size() > 0)
            addNewServerData(newChannelDataFromOtherServer);
    }


    /**
     * addNewClientData
     * @param newClientDataFromOtherServer
     */
    private void addNewClientData(List<ClientDataMulticast> newClientDataFromOtherServer) throws SQLException {
        boolean newClient = true;


        for(ClientDataMulticast clientLoggedInOtherServer : newClientDataFromOtherServer){
            for(ClientData localClient : serverStorageData.getClients()){
                if(clientLoggedInOtherServer.getUserName().equals(localClient.getName())){

                    localClient.getPrivateMessages().addAll(clientLoggedInOtherServer.getPrivateMessages()); //adds new messages
                    clientLoggedInOtherServer.getPrivateMessages().clear(); //clears new messages //prob. no need since it's the data from other server

                    newClient = false;
                    break;
                }
            }
            if(newClient){
                if(!serverDbLink.existsClient(clientLoggedInOtherServer.getUserName()))
                    serverDbLink.saveUser(clientLoggedInOtherServer.getUserName(), clientLoggedInOtherServer.getPassword(), clientLoggedInOtherServer.getPathImage(), clientLoggedInOtherServer.getIp());
            }
            else{
                newClient = true;
            }

            for(MessageData newPrivateMessage : clientLoggedInOtherServer.getPrivateMessages()){
                for(ClientData localClient : serverStorageData.getClients()){
                    if(localClient.getName().equals(newPrivateMessage.getUsernameTarget())){

                        List<ClientData> tempClientList = new ArrayList<>();
                        tempClientList.add(localClient);

                        deployMessages(tempClientList, newPrivateMessage, true);
                    }
                }
            }
        }
    }

    /**
     *
     * @param newChannelDataFromOtherServer
     */
    private void addNewServerData(List<ChannelDataMulticast> newChannelDataFromOtherServer) {
        boolean newChannel = true;

        for(ChannelDataMulticast channelDataFromOtherServer : newChannelDataFromOtherServer) {
            for (ChannelData localChannelData : serverStorageData.getNewChannelData()) {
                if (channelDataFromOtherServer.getName().equals(localChannelData.getName())) {

                    for(MessageData message : channelDataFromOtherServer.getChat()) {
                        deployMessages(localChannelData.getClients(), message, false);
                    }
                    localChannelData.getChat().addAll(channelDataFromOtherServer.getChat()); //adds new messages

                    channelDataFromOtherServer.getChat().clear(); //clears new messages //prob. no need since it's the data from other server
                    newChannel = false;
                }
            }

            if(newChannel){
                ChannelData tempChannel = new ChannelData(channelDataFromOtherServer.getChannelId(),
                        channelDataFromOtherServer.getName(),
                        channelDataFromOtherServer.getPassword(),
                        channelDataFromOtherServer.getDescription(),
                        channelDataFromOtherServer.getCreatorName(),
                        channelDataFromOtherServer.getChat());
                ChannelData tempChannel2 = new ChannelData(tempChannel.getChannelId(),
                        tempChannel.getName(),
                        tempChannel.getPassword(),
                        tempChannel.getDescription(),
                        tempChannel.getCreatorName());


                serverStorageData.getChannels().add(tempChannel);
                serverStorageData.getNewChannelData().add(tempChannel2);
            }
            else
                newChannel = true;

        }

    }


    /**
     *
     * @param clients
     * @param message
     */
    private void deployMessages(List<ClientData> clients, MessageData message, boolean isPrivate) {

        try {
            for (ClientData client : clients) {
                if (!client.getName().equals(message.getOriginName())) {
                    client.getOOS().writeObject(generateMessageRequest(message.getMessage(),
                            message.getOriginName(),
                            true,
                            client.getName(),
                            null));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        return new MessageRequest(message,
                nameOrigin,
                LocalDateTime.now(),
                usernameTarget,
                isSuccess,
                cmd);
    }

    private void sendRestMessages(){
        try {
            List<MessageData> messagesToSend = serverDbLink.getUnsentMessages();
            if(messagesToSend.size() <= 0) return;
            for(MessageData messageData : messagesToSend){
                for(ClientData clientData : serverStorageData.getNewClientData()){
                    clientData.getOOS().writeObject(new MessageRequest(messageData.getMessage(),
                            messageData.getOriginName(),
                            LocalDateTime.now(),
                            messageData.getUsernameTarget(),
                            true,
                            null
                    ));
                }
            }
            serverDbLink.markMessageAsSent();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
