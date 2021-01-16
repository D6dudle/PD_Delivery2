package com.example.Server;


import Data.AuthenticationRequestData;
import Data.AuthenticationResponseData;
import Data.MessageRequest;
import RMI.RemoteObserver;
import RMI.RemoteServer;
import com.example.Server.Data.ClientData;
import com.example.Server.Data.MessageData;
import com.example.Server.Data.ServerStorageData;
import com.example.Server.Data.ServerUtils;
import com.example.Server.DataBase.DataBaseLink;
import com.example.Server.Threads.AuthenticationThread;
import com.example.Server.Threads.InputTextThread;
import com.example.Server.Threads.ManageFiles.SendFileUDPThread;
import com.example.Server.Threads.PingThread;

import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Server extends UnicastRemoteObject implements RemoteServer, ServerUtils {

    private ServerStorageData serverStorageData;
    //private ServerStorageData serverStorageData;
    // Database data
    private DataBaseLink serverDbLink;

    protected Server() throws RemoteException, SQLException {
    }

    public static void main(String[] args) throws RemoteException, SQLException {

        Server s = new Server();

        Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

        registry.rebind(BIND_NAME_SERVER, s);

        //System.out.println("RMI Server is running...");
        s.runServer();
    }

    public void runServer() {
        MulticastSocket mSocket = null;
        InetAddress mGroupIp = null;
        InetSocketAddress mGroupSocket = null;
        NetworkInterface nIf = null;

        //ServerSocket serverSocket;


        try {

            serverStorageData = new ServerStorageData();
            serverDbLink = new DataBaseLink();
            serverStorageData.setChannels(serverDbLink.getAllChannels());
            serverStorageData.setNewChannelData(serverDbLink.getAllChannels());
            serverStorageData.clearNewChannelData();

            //serverSocket = new ServerSocket(portTCP);

            // Initializes group socket for Ping thread & between servers UDP communication
            mSocket = new MulticastSocket(PORT_MULTICAST);
            mGroupIp = InetAddress.getByName(serverStorageData.getServerIpMulticast());
            mGroupSocket = new InetSocketAddress(mGroupIp, PORT_MULTICAST);

            try {
                nIf = NetworkInterface.getByInetAddress(mGroupIp);
            } catch(SocketException | NullPointerException | SecurityException ex){
                nIf = NetworkInterface.getByName(serverStorageData.getServerIpMulticast());
            }

            mSocket.joinGroup(mGroupSocket, nIf);

            // Initialize Ping thread
            PingThread pingThread = new PingThread(mSocket,
                    mGroupIp,
                    serverStorageData);
            pingThread.start();

            // Initialize "Send files to other Servers" thread
            SendFileUDPThread sendFileUDPThread = new SendFileUDPThread(serverStorageData);
            sendFileUDPThread.start();

            // Initialize Login/Register thread
            AuthenticationThread authenticationThread = new AuthenticationThread(serverStorageData);
            authenticationThread.start();

            InputTextThread inputTextThread = new InputTextThread(serverStorageData);
            inputTextThread.start();

            do{

            }while(!serverStorageData.isExit());

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AuthenticationResponseData register(AuthenticationRequestData authenticationRequestData) throws RemoteException, SQLException{

        try {
            if (serverDbLink.isRegistered(authenticationRequestData.getUsername())) {
                return new AuthenticationResponseData(false, false, "Error at register operation. Client already registered!");
            }
            else {
                serverDbLink.saveUser(authenticationRequestData.getUsername(), authenticationRequestData.getPassword(), authenticationRequestData.getImgUrl(), "0.0.0.0");
                return new AuthenticationResponseData(false, true, "Register operation successful!", serverStorageData.getPortImages());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new AuthenticationResponseData(false, false, "Error at register operation!");
        }
    }

    @Override
    public boolean registerObserver(RemoteObserver remoteObserver) {
        System.out.println("A registar observer... " + remoteObserver);
        if(serverStorageData.getRemoteObservers().size() <= 0 ||
                !serverStorageData.getRemoteObservers().contains(remoteObserver)){
            serverStorageData.getRemoteObservers().add(remoteObserver);
            System.out.println("serverStorageData.getRemoteObservers() --->" + serverStorageData.getRemoteObservers());
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean unregisterObserver(RemoteObserver remoteObserver) {
        if(serverStorageData.getRemoteObservers().size() <= 0 ||
                !serverStorageData.getRemoteObservers().contains(remoteObserver)){
            return false;
        }
        else{
            serverStorageData.getRemoteObservers().remove(remoteObserver);
            System.out.println("serverStorageData.getRemoteObservers() --->" + serverStorageData.getRemoteObservers());
            return true;
        }
    }


    @Override
    public void sendMessageToAllClients(MessageRequest messageRequest) throws IOException {
        MessageData newMessage = new MessageData(messageRequest.getMessage(),
                "Observer",
                null,
                null,
                LocalDateTime.now(),
                true,
                true);

        for(ClientData client : serverStorageData.getClients()){
            client.getOOS().writeObject(messageRequest);
        }
    }

    /**
     * LogIn operation
     * @param authenticationRequestData
     * @return an AuthenticationResponseData with information based on the login information
     * @throws SQLException
     */
    public AuthenticationResponseData register(AuthenticationRequestData authenticationRequestData, String userIp) throws SQLException {
        if (isRegistered(authenticationRequestData)) {
            return new AuthenticationResponseData(false, false, "Error at register operation!");
        }
        else {
            serverDbLink.saveUser(authenticationRequestData.getUsername(), authenticationRequestData.getPassword(), authenticationRequestData.getImgUrl(), userIp);
            return new AuthenticationResponseData(false, true, "Register operation successful!", serverStorageData.getPortImages());
        }
    }

    /**
     * Checks if a user is registered
     * @param authenticationRequestData
     * @return true if the user is registered, false if it is not
     * @throws SQLException
     */
    public boolean isRegistered(AuthenticationRequestData authenticationRequestData) throws SQLException {
        return serverDbLink.isRegistered(authenticationRequestData.getUsername());
    }
}
