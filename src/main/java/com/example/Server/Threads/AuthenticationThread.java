package com.example.Server.Threads;

import Data.AuthenticationRequestData;
import Data.AuthenticationResponseData;
import com.example.Server.Data.*;
import com.example.Server.DataBase.DataBaseLink;
import com.example.Server.Data.ServerStorageData;
import com.example.Server.Threads.ManageFiles.DownloadRegisterImageThread;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AuthenticationThread extends Thread implements ServerUtils {
    private DataBaseLink serverDbLink;
    private ServerStorageData serverStorageData;

    private ServerSocket serverSocket;
    private Socket socket;

    //new


    /**
     *
     * @param serverStorageData
     */
    public AuthenticationThread(ServerStorageData serverStorageData) {

        this.serverStorageData = serverStorageData;
        this.serverSocket = null;
        this.socket = null;
    }


    /**
     * Authentication thread: run
     */
    @Override
    public void run() {
        try {
            DatagramSocket dS = new DatagramSocket(serverStorageData.getPortUDP());
            DatagramPacket dP;
            AuthenticationResponseData authenticationResponseData = null;
            ByteArrayInputStream bIS;
            ObjectInputStream oIS;

            serverSocket = new ServerSocket(serverStorageData.getPortTCP());
            serverDbLink = new DataBaseLink();

            while (true) {
                dP = new DatagramPacket(new byte[512], 512); //TODO: why 512/why not 512?
                dS.receive(dP);

                byte[] bufDP = dP.getData();

                bIS = new ByteArrayInputStream(bufDP);
                oIS = new ObjectInputStream(bIS);
                AuthenticationRequestData authenticationRequestData = (AuthenticationRequestData)oIS.readObject();

                System.out.println("Pedido de login de: " + authenticationRequestData.getUsername());

                // Test if rebalance is needed
                List<ServerControlData> serversHalfNClients = serverStorageData.getControlDataOtherServers().stream().filter(c -> c.getNClients() <= (serverStorageData.getServerNClients() / 2)).collect(Collectors.toList());

                if (serverStorageData.getClients().size() == 0 || serversHalfNClients.size() <= 0) {

                    if (authenticationRequestData.getRegister())
                        authenticationResponseData = register(authenticationRequestData, dP.getAddress().getHostAddress());
                    else
                        authenticationResponseData = logIn(authenticationRequestData, dP.getAddress().getHostAddress());

                    // Add (or not) a new client
                    // (when not a rebalance operation as response and when the operation is a success, as the validation as it is a register is above)
                    if (!authenticationResponseData.getRebalance() && authenticationResponseData.getSuccess()) {
                        ClientData clientData = new ClientData(dP.getAddress().getHostAddress(),
                                dP.getPort(),
                                authenticationRequestData.getPortSendFile(),
                                authenticationRequestData.getPortSendFilesClient(),
                                authenticationRequestData.getUsername(),
                                authenticationRequestData.getPassword(),
                                authenticationRequestData.getImgUrl());

                        //login by failover
                        if(authenticationRequestData.getCurrentChannel() != null)
                            for(ChannelData channel : serverStorageData.getNewChannelData()){
                                if(authenticationRequestData.getCurrentChannel().equals(channel.getName())){
                                    channel.addClient(clientData);
                                }
                            }

                        serverStorageData.setServerNClients(serverStorageData.getServerNClients() + 1);
                        serverStorageData.getClients().add(clientData);
                        serverStorageData.getNewClientData().add(clientData);

                        System.out.println("vou enviar resposta");
                        sendResponseToClient(dS, dP, authenticationResponseData);

                        socket = serverSocket.accept();

                        /*
                        for (ClientData clientData: newClientData) {
                            if (clientData.getIp().equals(socket.getInetAddress().getHostAddress())) {
                                System.out.println("Conectou-se o cliente de ip: " + socket.getInetAddress().getHostAddress());
                                client = clientData;
                            }
                        }
                        */

                        clientData.setSocket(socket);

                        // Initialize a new Client's thread
                        MessagesThread messagesThread = new MessagesThread(clientData,
                                serverStorageData.getClients(),
                                serverStorageData.getChannels(),
                                serverStorageData.getNewClientData(),
                                serverStorageData.getNewChannelData(),
                                serverStorageData.getFilesToGetFromClient(),
                                serverStorageData.getRedirectControl(),
                                serverStorageData.getRemoteObservers());
                        messagesThread.start();
                    }
                }
                else { // Rebalance
                    authenticationResponseData = new AuthenticationResponseData(true, true, "Rebalance operation", null, serversHalfNClients.get(0).getIpUDP(), serversHalfNClients.get(0).getPortUDP());
                    sendResponseToClient(dS, dP, authenticationResponseData);
                }

                if(!authenticationResponseData.getSuccess()){
                    sendResponseToClient(dS, dP, authenticationResponseData);
                }
                // Run Thread to upLoad Image if, is a register and it was successful
                else if (authenticationRequestData.getRegister()
                        && authenticationRequestData.getImgUrl() != null
                        && !authenticationRequestData.getImgUrl().equals("null")) {
                    DownloadRegisterImageThread downloadRegisterImageThread = new DownloadRegisterImageThread(
                            dP.getAddress().getHostAddress(),
                            dP.getPort(),
                            authenticationRequestData.getImgUrl(),
                            serverStorageData.getPortImages());
                    downloadRegisterImageThread.start();
                }

                //dS.close();
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            try {
                serverDbLink.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            serverDbLink.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void sendResponseToClient(DatagramSocket dS, DatagramPacket dP, AuthenticationResponseData authenticationResponseData) throws IOException {
        // Send response
        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        ObjectOutputStream oOS = new ObjectOutputStream(bAOS);
        oOS.writeObject(authenticationResponseData);
        byte[] bufStr = bAOS.toByteArray();

        dP = new DatagramPacket(bufStr, bufStr.length, dP.getAddress(), dP.getPort());
        dS.send(dP);
    }

    /**
     * Register operation
     * @param authenticationRequestData
     * @return an AuthenticationResponseData with information based on the register information
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
     * LogIn operation
     * @param authenticationRequestData
     * @return an AuthenticationResponseData with information based on the login information
     * @throws SQLException
     */
    public AuthenticationResponseData logIn(AuthenticationRequestData authenticationRequestData, String userIp) throws SQLException {
        if (clientExists(authenticationRequestData))
            return new AuthenticationResponseData(false, false, "You are already logged in!");

        if (isPasswordValid(authenticationRequestData)) {
            serverDbLink.updateUser(authenticationRequestData.getUsername(), userIp);
            return new AuthenticationResponseData(false, true, "LogIn operation successful!", serverStorageData.getServerIpTCP(), serverStorageData.getPortTCP());
        }
        else {
             return new AuthenticationResponseData(false, false, "Error at LogIn operation!");
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

    /**
     * Verifies if a password is valid or not
     * @param authenticationRequestData
     * @return true if the password is corrected, false if it is not
     * @throws SQLException
     */
    public boolean isPasswordValid(AuthenticationRequestData authenticationRequestData) throws SQLException {
        return serverDbLink.logInOperation(authenticationRequestData.getUsername(), authenticationRequestData.getPassword());
    }

    /**
     * Checks if a client exists in the clients list
     * @param authenticationRequestData
     * @return true if the client exists, false if it is not
     */
    public boolean clientExists(AuthenticationRequestData authenticationRequestData) {
        for (ClientData c : serverStorageData.getClients())
            if (c.getName().equals(authenticationRequestData.getUsername()))
                return true;
        return false;
    }
}
