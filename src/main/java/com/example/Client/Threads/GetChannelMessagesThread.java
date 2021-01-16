package com.example.Client.Threads;

import Data.*;

import java.io.*;
import java.net.*;

import static Data.UtilsFunctions.getMessageTime;

public class GetChannelMessagesThread extends Thread implements Commands {
    private ObjectInputStream oIS;
    private LockObject lockObject;
    private String userName; //TODO: synchronize
    private String userPassword;
    private Socket socket;
    private int portSendFile;
    private int portReceiveFile;


    /**
     * Constructor
     * @param oIS
     * @param lockObject
     */
    public GetChannelMessagesThread(String userName,
                                    String userPassword,
                                    ObjectInputStream oIS,
                                    LockObject lockObject,
                                    Socket socket,
                                    int portSendFile,
                                    int portReceiveFile) {
         this.userName = userName;
         this.userPassword = userPassword;
         this.oIS = oIS;
         this.lockObject = lockObject;
         this.socket = socket;
         this.portSendFile = portSendFile;
         this.portReceiveFile = portReceiveFile;
    }

    /**
     * run
     */
    @Override
    public void run() {
        try {
            do {
                MessageRequest messageResponse = (MessageRequest)oIS.readObject();
                lockObject.setIpUDPRedirect(messageResponse.getIpToRedirect());
                lockObject.setPortUDPRedirect(messageResponse.getPortToRedirect());

                if (messageResponse.getCmd() != null) {
                    switch (messageResponse.getCmd().getCommand()) {
                        case EXIT_CMD:
                            if (messageResponse.isSuccess()) {
                                System.out.println("\n" + messageResponse.getNameOrigin() + ": " + messageResponse.getMessage() + "\n");
                                synchronized (lockObject) {
                                    lockObject.setCurrentChannelName(null);
                                    lockObject.setExitFlag(true);
                                    lockObject.notify();
                                    Thread.currentThread().interrupt();
                                }
                            }
                            else {
                                System.out.println("\n" + messageResponse.getNameOrigin() + ": " + messageResponse.getMessage() + "\n");
                                synchronized (lockObject) {
                                    lockObject.notify();
                                }
                            }
                            break;
                        case JOIN_CHANNEL_CMD:
                            if (messageResponse.isSuccess()) {
                                System.out.println("\n" + messageResponse.getNameOrigin() + ": " + messageResponse.getMessage() + "\n");
                                synchronized (lockObject) {
                                    lockObject.setJoinChannelSuccess(true);
                                    lockObject.setCurrentChannelName(messageResponse.getCmd().getArguments().get(0));
                                    lockObject.notify();
                                    Thread.currentThread().interrupt();
                                }
                            }
                            else {
                                System.out.println("\n" + messageResponse.getNameOrigin() + ": " + messageResponse.getMessage() + "\n");
                                synchronized (lockObject) {
                                    lockObject.notify();
                                }
                            }
                            break;
                        case CREATE_CHANNEL_CMD:
                            if (messageResponse.isSuccess()) {
                                System.out.println("\n" + messageResponse.getNameOrigin() + ": " + messageResponse.getMessage() + "\n");
                                synchronized (lockObject) {
                                    lockObject.setCreateChannelSuccess(true);
                                    lockObject.setCurrentChannelName(messageResponse.getCmd().getArguments().get(0));
                                    lockObject.notify();
                                    Thread.currentThread().interrupt();
                                }
                            }
                            else {
                                System.out.println("\n" + messageResponse.getNameOrigin() + ": " + messageResponse.getMessage() + "\n");
                                synchronized (lockObject) {
                                    lockObject.notify();
                                }
                            }
                            break;
                        case EDIT_CHANNEL_CMD:
                            System.out.println("\n" + messageResponse.getNameOrigin() + ": " + messageResponse.getMessage());
                            break;
                        case DELETE_CHANNEL_CMD:
                            if (messageResponse.isSuccess()) {
                                System.out.println("\n" + messageResponse.getNameOrigin() + ": " + messageResponse.getMessage() + "\n");
                                //TODO: resolver synchronized
                                lockObject.setCurrentChannelName(null);
                                synchronized (lockObject) {
                                    lockObject.setDeleteChannelSuccess(true);
                                    lockObject.notify();
                                    Thread.currentThread().interrupt();
                                }
                            }
                            else {
                                System.out.println("\n" + messageResponse.getNameOrigin() + ": " + messageResponse.getMessage() + "\n");
                                synchronized (lockObject) {
                                    lockObject.notify();
                                }
                            }
                            break;
                        case LIST_USER_CHANNELS_CMD:
                        case LIST_ALL_CHANNELS_CMD:
                            if (messageResponse.isSuccess()) {
                                System.out.println("\n" + messageResponse.getMessage() + "\n");
                                synchronized (lockObject) {
                                    lockObject.setViewChannelsSuccess(true);
                                    lockObject.notify();
                                    Thread.currentThread().interrupt();
                                }
                            } else {
                                synchronized (lockObject) {
                                    lockObject.notify();
                                }
                            }
                            break;
                        default:
                            System.out.println("\n\n\n" + messageResponse.getNameOrigin() + ": " + messageResponse.getMessage() + "\n\n\n");
                            if (lockObject.getCurrentChannelName() == null || lockObject.getCurrentChannelName().equals(""))
                                System.out.print("Opt: ");
                            else
                                System.out.print(userName + ": ");
                            break;
                    }
                }
                else {
                    if (messageResponse.getMessage().contains("@private")) {
                        System.out.println("\n\n\n" + messageResponse.getMessage() + " (" + getMessageTime(messageResponse.getLocalDateTime()) + ")" + "\n\n\n");

                    }
                    else {
                        System.out.println("\n" + messageResponse.getNameOrigin() + ": " + messageResponse.getMessage() + " (" + getMessageTime(messageResponse.getLocalDateTime()) + ")");
                    }
                    if (lockObject.getCurrentChannelName() != null && !lockObject.getCurrentChannelName().equals("")) {
                        if (lockObject.getCurrentChannelName() == null || lockObject.getCurrentChannelName().equals(""))
                            System.out.print("Opt: ");
                        else
                            System.out.print(userName + ": ");
                    }
                }
            } while (true);
        }
        catch (IOException | ClassNotFoundException e) {
            //TODO: failover
            //e.printStackTrace();
            if(e instanceof SocketException) {
                catchSocketException();
            }
        }
    }

    /**
     * In case the server turns off
     */
    private void catchSocketException() {
        System.out.println("\n\n\n" + "Server shut down... redirecting to another server..." + "\n\n\n");
        login();
    }

    public boolean login() {
        boolean rebalancing = false;
        AuthenticationResponseData authenticationResponseData = null;

        try {
            do {
                DatagramSocket dS = new DatagramSocket();
                InetAddress ip = InetAddress.getByName(lockObject.getIpUDPRedirect());

                ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
                ObjectOutputStream oOS = new ObjectOutputStream(bAOS);

                // Send request
                oOS.writeObject(new AuthenticationRequestData(userName,
                        userPassword,
                        "null",
                        false,
                        portSendFile,
                        portReceiveFile,
                        lockObject.getCurrentChannelName()));

                byte[] bufStr = bAOS.toByteArray();

                DatagramPacket dP = new DatagramPacket(bufStr, bufStr.length, ip, lockObject.getPortUDPRedirect());
                dS.send(dP);

                // Gets response
                authenticationResponseData = getResponseUDP(dS);

                //System.out.println("authenticationResponseData: " + authenticationResponseData);

                rebalancing = authenticationResponseData.getRebalance();

                if (rebalancing) {
                    lockObject.setIpUDPRedirect(authenticationResponseData.getIpUDP());
                    lockObject.setPortUDPRedirect(authenticationResponseData.getPortUDP());
                }
                else{
                    socket = new Socket(authenticationResponseData.getIpTCPServerSocket(), authenticationResponseData.getPortTCPServerSocket());
                    synchronized (lockObject) {
                        lockObject.setNewSocket(socket);
                        lockObject.notify();
                    }
                }
            } while (rebalancing);

            if (authenticationResponseData.getSuccess()) {
                return true;
            } else {
                System.out.println("\nError on the selected operation!\n");
                return false;
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private AuthenticationResponseData getResponseUDP(DatagramSocket dS) throws IOException, ClassNotFoundException {
        DatagramPacket dP = new DatagramPacket(new byte[512], 512); //TODO: why 512/why not 512?
        dS.receive(dP);

        byte[] bufDP = dP.getData();

        ByteArrayInputStream bIS = new ByteArrayInputStream(bufDP);
        ObjectInputStream oIS = new ObjectInputStream(bIS);

        //Debug
        //System.out.println(authenticationResponseData.toString());

        return (AuthenticationResponseData)oIS.readObject();
    }
}
