package com.example.Client.Threads;

import Data.*;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientThread implements Commands{
    // Server data
    private String ipServerUDP; //TODO: falta fechar o socket UDP
    private int portServerUDP;
    private String ipServerTCP;
    private int portServerTCP;
    private int portReceiveFile;

    // Client data
    private String userName;
    private String userPassword;
    private String imgUrl;
    private Socket socket;
    private int portSendFile;
    private ObjectOutputStream oOS;
    private ObjectInputStream oIS;

    // Control data
    private LockObject lockObject;

    // Receive other user's on the channel messages
    private GetChannelMessagesThread getChannelMessagesThread;

    // Sends files to the server
    private SendFileTCPThread sendFileTCPThread;

    // Sends files to the server
    private ReceiveFileTCPThread receiveFileTCPThread;

    /**
     * constructor
     * @param ipUDPRedirect
     * @param portUDPRedirect
     * @param ipServerTCP
     * @param portServerTCP
     * @param portSendFile
     * @param portReceiveFile
     */
    public ClientThread(String ipUDPRedirect, int portUDPRedirect, String ipServerTCP, int portServerTCP, int portSendFile, int portReceiveFile) {
        this.ipServerUDP = ipServerUDP;
        this.portServerUDP = portServerUDP;
        this.ipServerTCP = ipServerTCP;
        this.portServerTCP = portServerTCP;
        this.portSendFile = portSendFile;
        this.portReceiveFile = portReceiveFile;
        this.lockObject = new LockObject(false, false, false, false, false, ipUDPRedirect, portUDPRedirect);

    }

    /**
     * Main Thread
     * Calls User Interface
     */
    public void runClient() {
        userInterface();
    }

    /**
     * Interface Thread (Main thread)
     * Gets Input from User & Initializes other threads
     * Circles through the LogIn/Register/Exit operations until 1 is successful
     * Project name: SlaX
     */
    public void userInterface() {
        int opt = -1;
        boolean rebalanced = false;

        Scanner scanner = new Scanner(System.in);
        System.out.println("------ Welcome to SlaX ------\n");

        do {
            do {
                System.out.println("Do you wish to:");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("0. Exit");
                System.out.print("Opt: ");

                while(!scanner.hasNextInt()){
                    System.out.print("Opt: ");
                    scanner.next();
                }
                opt = scanner.nextInt();

                if (opt < 0 || opt > 2) {
                    System.out.println("\nInvalid option!\n");
                }
            } while(opt < 0 || opt > 2);

            switch (opt) {
                case 0:
                    System.out.println("Thank you for choosing SLaX!");
                    return;
                case 1:
                    System.out.println("\n\n--- Register ---");
                    scanner.nextLine();
                    System.out.print("Username: ");
                    userName = scanner.nextLine();
                    System.out.print("Password: ");
                    userPassword = scanner.nextLine();
                    System.out.print("Image URL: ");
                    imgUrl = scanner.nextLine();
                    rebalanced = register();
                    break;
                case 2:
                    System.out.println("\n\n--- Login ---");
                    scanner.nextLine();
                    System.out.print("Username: ");
                    userName = scanner.nextLine();
                    System.out.print("Password: ");
                    userPassword = scanner.nextLine();
                    rebalanced = login();
                    break;
                default:
                    System.out.println("Error!");
                    return;
            }
        } while (!rebalanced);

        System.out.println("Log In successful!\n\n");

        try {
            socket = new Socket(ipServerTCP, portServerTCP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            oOS = new ObjectOutputStream(socket.getOutputStream());
            oIS = new ObjectInputStream(socket.getInputStream());

            getChannelMessagesThread = new GetChannelMessagesThread(userName, userPassword, oIS, lockObject, socket, portSendFile, portReceiveFile);
            getChannelMessagesThread.start();

            do {
                do {
                    System.out.println("\n\n");
                    System.out.println("1. Create a channel");
                    System.out.println("2. Join a channel");
                    System.out.println("3. Delete one of your channels");
                    System.out.println("4. Edit one of your channels");
                    System.out.println("5. Send a private message");
                    System.out.println("6. List the last private messages");
                    System.out.println("7. Channels information");
                    System.out.println("0. Exit");
                    System.out.print("Opt: ");

                    while (!scanner.hasNextInt()) {
                        System.out.print("Opt: ");
                        scanner.next();
                    }
                    opt = scanner.nextInt();

                    if (opt < 0 || opt > 7) {
                        System.out.println("\nInvalid option!\n");
                    }
                } while (opt < 0 || opt > 7);

                switch (opt) {
                    case 0:
                        System.out.println("Thank you for choosing SLaX!");
                        return;
                    case 1:
                        createChannel();
                        break;
                    case 2:
                        logIntoChannel();
                        break;
                    case 3:
                        deleteChannel();
                        break;
                    case 4:
                        editChannel();
                        break;
                    case 5:
                        privateMessage();
                        break;
                    case 6:
                        listPMs();
                        break;
                    case 7:
                        viewChannels();
                        break;
                    default:
                        break;
                }

            } while (true);
        } catch (IOException e) {
            failover(e);
        }
    }

    /**
     * "Clears" the screen
     */
    private void clearScreen() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    /**
     * UDP
     * Register operation
     * Tries to register as many times as it is rebalanced
     * @return a boolean to verify in the main thread if the register was successful
     */
    public boolean register() {
        boolean rebalancing = false;
        AuthenticationResponseData authenticationResponseData = null;

        try {
            DatagramSocket dS = new DatagramSocket();
            InetAddress ipServerUdp;
            do {
                ipServerUdp = InetAddress.getByName(ipServerUDP);

                ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
                ObjectOutputStream oOS = new ObjectOutputStream(bAOS);


                oOS.writeObject(new AuthenticationRequestData(userName, userPassword, imgUrl, true, portSendFile, portReceiveFile));

                byte[] bufStr = bAOS.toByteArray();

                DatagramPacket dP = new DatagramPacket(bufStr, bufStr.length, ipServerUdp, lockObject.getPortUDPRedirect());
                dS.send(dP);

                // Gets response
                authenticationResponseData = getResponseUDP(dS);

                rebalancing = authenticationResponseData.getRebalance();

                if (rebalancing) {
                    ipServerUDP = authenticationResponseData.getIpUDP();
                    portServerTCP = authenticationResponseData.getPortUDP();
                    lockObject.setPortUDPRedirect(authenticationResponseData.getPortUDP());
                    System.out.println("\nBeing redirected to another server...!\n");
                }

            } while (rebalancing);

            if (authenticationResponseData.getSuccess()) {
                System.out.println("\nOperation successful!\n");

                // load login image with a proper thread
                if (imgUrl != null && !imgUrl.equals("null")) {
                    UploadRegisterImageThread uploadRegisterImageThread = new UploadRegisterImageThread(ipServerUdp, imgUrl, authenticationResponseData.getPortImages());
                    uploadRegisterImageThread.start();
                }

                if(authenticationResponseData.getIpTCPServerSocket() != null)
                    ipServerTCP = authenticationResponseData.getIpTCPServerSocket();
                if(authenticationResponseData.getPortTCPServerSocket() != 0)
                    portServerTCP = authenticationResponseData.getPortTCPServerSocket();
                return true;
            } else {
                System.out.println("\nError on the selected operation!\n");
                return false;
            }

        } catch (IOException | ClassNotFoundException e) {
            //TODO: failover

            e.printStackTrace();
        }
        return false;
    }

    /**
     * UDP
     * Log In operation
     * Tries to log in as many times as it is rebalanced
     * @return a boolean to verify in the main thread if the log in was successful
     */
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
                oOS.writeObject(new AuthenticationRequestData(userName, userPassword, "null", false, portSendFile, portReceiveFile, lockObject.getCurrentChannelName()));

                byte[] bufStr = bAOS.toByteArray();

                DatagramPacket dP = new DatagramPacket(bufStr, bufStr.length, ip, lockObject.getPortUDPRedirect());
                dS.send(dP);

                // Gets response
                authenticationResponseData = getResponseUDP(dS);

                rebalancing = authenticationResponseData.getRebalance();

                if (rebalancing) {
                    ipServerUDP = authenticationResponseData.getIpUDP();
                    portServerUDP = authenticationResponseData.getPortUDP();
                    lockObject.setPortUDPRedirect(authenticationResponseData.getPortUDP());
                    System.out.println("\nBeing redirected to another server...!\n");
                }
            } while (rebalancing);

            if (authenticationResponseData.getSuccess()) {
                if(authenticationResponseData.getIpTCPServerSocket() != null)
                    ipServerTCP = authenticationResponseData.getIpTCPServerSocket();
                if(authenticationResponseData.getPortTCPServerSocket() != 0)
                    portServerTCP = authenticationResponseData.getPortTCPServerSocket();
                return true;
            } else {
                System.out.println("\nError on the selected operation!\n");
                return false;
            }

        } catch (IOException | ClassNotFoundException e) {
            //TODO: failover

            e.printStackTrace();
        }
        return false;
    }

    /**
     * UDP
     * Gets the response to either a Log In or a Register operation
     * @param dS
     * @return the object sent by the server (LoginResponseData)
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private AuthenticationResponseData getResponseUDP(DatagramSocket dS) throws IOException, ClassNotFoundException {
        DatagramPacket dP = new DatagramPacket(new byte[512], 512); //TODO: why 512/why not 512?
        dS.receive(dP);

        byte[] bufDP = dP.getData();

        ByteArrayInputStream bIS = new ByteArrayInputStream(bufDP);
        ObjectInputStream oIS = new ObjectInputStream(bIS);
        AuthenticationResponseData authenticationResponseData = (AuthenticationResponseData)oIS.readObject();

        //Debug
        //System.out.println(authenticationResponseData.toString());

        return authenticationResponseData;
    }

    /**
     * TCP
     * Creates a new channel
     * If the operation is successful sets the currentChannel value
     * @return true if the operation is successful and false if not
     */
    private boolean createChannel() {
        try {
            Scanner scanner = new Scanner(System.in);
            String channelName;
            String channelPassword;
            String channelPasswordVerification;
            String channelDescription; //not mandatory
            List<String> args = new ArrayList<>();

            do {
                System.out.println("\n\n--- New Channel ---");
                System.out.print("Channel Name: ");
                channelName = scanner.nextLine();
                System.out.print("Password: ");
                channelPassword = scanner.nextLine();
                System.out.print("Password verification: ");
                channelPasswordVerification = scanner.nextLine();
                System.out.print("Channel Description: ");
                channelDescription = scanner.nextLine();
            } while (channelPassword == null || channelPassword.equals("")
                    || channelName == null || channelName.equals("")
                    || channelPasswordVerification == null || channelPasswordVerification.equals("")
                    || !channelPassword.equals(channelPasswordVerification));

            args.add(channelName);
            args.add(channelPassword);
            args.add(channelDescription);

            Command cmd = new Command(CREATE_CHANNEL_CMD, args);

            // Creating and sending the message
            MessageRequest msgRequest = new MessageRequest(null, userName, LocalDateTime.now(), null, false, cmd);
            oOS.writeObject(msgRequest);

            synchronized (lockObject) {
                lockObject.wait();

                if(lockObject.isCreateChannelSuccess()) {
                    lockObject.setCreateChannelSuccess(false);

                    // Clears screen
                    clearScreen();

                    // Sends messages for the server to deploy to other users in the channel
                    channelChat();

                    // Clears screen
                    clearScreen();

                    return true;
                }
                else {
                    return false;
                }
            }
        } catch (IOException | InterruptedException e) {
            failover(e);
        }

        return false; //TODO: corrigir
    }

    /**
     * TCP
     * Logs user into a channel
     * If the operation is successful sets the currentChannel value
     * @return true if the operation is successful and false if not
     */
    private boolean logIntoChannel() {
        List<String> args = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(System.in);
            String channelName;
            String channelPassword;
            do {
                System.out.println("\n\n--- Join a Channel ---");
                System.out.print("Channel Name: ");
                channelName = scanner.nextLine();
                System.out.print("Password: ");
                channelPassword = scanner.nextLine();
            } while (channelPassword == null || channelPassword.equals("")
                    || channelName == null || channelName.equals(""));

            args.add(channelName);
            args.add(channelPassword);
            Command cmd = new Command(JOIN_CHANNEL_CMD, args);

            // Creating and sending the message
            MessageRequest msgRequest = new MessageRequest(null, userName, LocalDateTime.now(), null, false, cmd);
            oOS.writeObject(msgRequest);

            synchronized (lockObject) {
                lockObject.wait();

                if(lockObject.isJoinChannelSuccess()) {
                    lockObject.setJoinChannelSuccess(false);

                    // Clears screen
                    clearScreen();

                    // Sends messages for the server to deploy to other users in the channel
                    channelChat();

                    // Clears screen
                    clearScreen();

                    return true;
                }
                else {
                    return false;
                }
            }

        } catch (IOException | InterruptedException e) {
            failover(e);
        }

        return false; //TODO: corrigir
    }

    /**
     * TCP
     * Allow the user to edit either the name and/or the description from one of his channels
     */
    private void editChannel() {
        try {
            Scanner scanner = new Scanner(System.in);
            String oldChannelName;
            String newChannelName;
            String channelPassword;
            String channelDescription; // Not mandatory
            List<String> args = new ArrayList<>();

            do {
                System.out.println("\n\n--- Edit Channel ---");
                System.out.print("Old channel Name: ");
                oldChannelName = scanner.nextLine();
                System.out.print("Password: ");
                channelPassword = scanner.nextLine();
                System.out.print("New channel Name (optional): ");
                newChannelName = scanner.nextLine();
                System.out.print("Channel Description (optional): ");
                channelDescription = scanner.nextLine();
            } while (channelPassword == null || channelPassword.equals("")
                    || oldChannelName == null || oldChannelName.equals(""));

            args.add(oldChannelName);
            args.add(channelPassword);
            if(newChannelName == null || newChannelName.equals(""))
                args.add(oldChannelName);
            else
                args.add(newChannelName);
            args.add(channelDescription);

            Command cmd = new Command(EDIT_CHANNEL_CMD, args);

            // Creating and sending the message
            MessageRequest msgRequest = new MessageRequest(null, userName, LocalDateTime.now(), null, false, cmd);
            oOS.writeObject(msgRequest);

        } catch (IOException e) {
            failover(e);
        }
    }

    /**
     * TCP
     * Allows the user to delete one of his channels
     */
    private boolean deleteChannel() {
        try {
            Scanner scanner = new Scanner(System.in);
            String channelName;
            String channelPassword;
            String channelPasswordVerification;
            List<String> args = new ArrayList<>();

            do {
                System.out.println("\n\n--- Delete Channel ---");
                System.out.print("Channel Name: ");
                channelName = scanner.nextLine();
                System.out.print("Password: ");
                channelPassword = scanner.nextLine();
                System.out.print("Password verification: ");
                channelPasswordVerification = scanner.nextLine();
            } while (channelPassword == null || channelPassword.equals("")
                    || channelName == null || channelName.equals("")
                    || channelPasswordVerification == null || channelPasswordVerification.equals("")
                    || !channelPassword.equals(channelPasswordVerification));

            args.add(channelName);
            args.add(channelPassword);

            Command cmd = new Command(DELETE_CHANNEL_CMD, args);

            // Creating and sending the message
            MessageRequest msgRequest = new MessageRequest(null, userName, LocalDateTime.now(), null, false, cmd);
            oOS.writeObject(msgRequest);

            synchronized (lockObject) {
                lockObject.wait();

                if(lockObject.isDeleteChannelSuccess()) {
                    lockObject.setDeleteChannelSuccess(false);

                    // Clears screen
                    clearScreen();

                    return true;
                }
                else {
                    return false;
                }
            }
        } catch (IOException | InterruptedException e) {
            failover(e);
        }

        return false;
    }

    /**
     * TCP
     * Allows sending messages for the server to deploy to other users that are in the same channel
     */
    private void channelChat() {
        Scanner scanner = new Scanner(System.in);
        int nrOfMessages;

        System.out.println("----- " + lockObject.getCurrentChannelName() + " -----");

        do {
            try {
                System.out.print(userName +": ");
                String message = scanner.nextLine();

                if (message.equals(EXIT_CMD)) {
                    MessageRequest msgRequest = new MessageRequest(null, userName, LocalDateTime.now(), null, false, new Command(message, new ArrayList<>()));
                    oOS.writeObject(msgRequest);

                    synchronized (lockObject) {
                        lockObject.wait();

                        if (lockObject.isExitFlag()) {
                            lockObject.setExitFlag(false);
                            return;
                        }
                    }
                }
                else if (message.equals(SEND_FILE_CMD)) {
                    System.out.print("File name: ");
                    String arg = scanner.nextLine();

                    List<String> args = new ArrayList<>();
                    args.add(arg);

                    sendFileTCPThread = new SendFileTCPThread(portSendFile);
                    sendFileTCPThread.start();

                    oOS.writeObject(new MessageRequest(null, userName, LocalDateTime.now(), null, false, new Command(message, args)));
                }
                else if (message.equals(GET_FILE_CMD)) {
                    System.out.print("File name: ");
                    String arg = scanner.nextLine();

                    List<String> args = new ArrayList<>();
                    args.add(arg);

                    receiveFileTCPThread = new ReceiveFileTCPThread(ipServerTCP, portReceiveFile, arg, userName);
                    receiveFileTCPThread.start();

                    oOS.writeObject(new MessageRequest(null, userName, LocalDateTime.now(), null, false, new Command(message, args)));
                }
                else if(message.equals(LIST_MESSAGES_CMD)) {
                    System.out.print("Number of messages: ");
                    do {
                        while (!scanner.hasNextInt()) {
                            System.out.print("Number of messages: ");
                            scanner.next();
                        }
                        nrOfMessages = scanner.nextInt();

                        if (nrOfMessages < 0) {
                            System.out.println("\nInvalid number of message!\n");
                        }
                    } while (nrOfMessages < 0);

                    List<String> args = new ArrayList<>();
                    args.add(String.valueOf(nrOfMessages));

                    oOS.writeObject(new MessageRequest(null, userName, LocalDateTime.now(), null, false, new Command(LIST_MESSAGES_CMD, args)));
                    nrOfMessages = 0;
                }
                else if (message.equals(LIST_FILES_CMD)) {
                    List<String> args = new ArrayList<>();
                    args.add(lockObject.getCurrentChannelName());
                    oOS.writeObject(new MessageRequest(null, userName, LocalDateTime.now(), null, false, new Command(LIST_FILES_CMD, args)));
                }
                else {
                    MessageRequest msgRequest = new MessageRequest(message, userName, LocalDateTime.now(), null, false, null);
                    oOS.writeObject(msgRequest);
                }
            } catch (IOException | InterruptedException e) {
                failover(e);
            }
        } while (lockObject.getCurrentChannelName() != null);
    }



    /**
     * TCP
     * Sends a private message to a specific user, the message is sent to the server to be deployed to the right user
     */
    private void privateMessage() {
        String usernameTarget, message;
        Scanner scanner = new Scanner(System.in);

        System.out.print("User to message: ");
        usernameTarget = scanner.nextLine();

        System.out.print("Message: ");
        message = scanner.nextLine();
        try {
            if (message.equals(SEND_FILE_CMD)) {
                System.out.print("File name: ");
                String arg = scanner.nextLine();

                List<String> args = new ArrayList<>();
                args.add(arg);

                sendFileTCPThread = new SendFileTCPThread(portSendFile);
                sendFileTCPThread.start();


                oOS.writeObject(new MessageRequest("@private file from " + userName, userName, LocalDateTime.now(), usernameTarget, false, new Command(message, args)));

            } else if (message.equals(GET_FILE_CMD)) {
                System.out.print("File name: ");
                String arg = scanner.nextLine();

                List<String> args = new ArrayList<>();
                args.add(arg);

                receiveFileTCPThread = new ReceiveFileTCPThread(ipServerTCP, portReceiveFile, arg, userName);
                receiveFileTCPThread.start();

                oOS.writeObject(new MessageRequest("@private get file from " + userName, userName, LocalDateTime.now(), usernameTarget, false, new Command(message, args)));
            } else {
                oOS.writeObject(new MessageRequest("@private from " + userName + ": " + message, userName, LocalDateTime.now(), usernameTarget, false, null));

            }
        }catch (IOException e) {
            failover(e);
        }
    }

    /**
     * listPMs
     */
    private void listPMs() {
        try {
            String usernameTarget;
            int nrOfMessages;
            Scanner scanner = new Scanner(System.in);

            System.out.print("List private messages between you and: ");
            usernameTarget = scanner.nextLine();
            System.out.print("Number of messages: ");
            do {
                while (!scanner.hasNextInt()) {
                    System.out.print("Number of messages: ");
                    scanner.next();
                }
                nrOfMessages = scanner.nextInt();

                if (nrOfMessages < 0) {
                    System.out.println("\nInvalid number of message!\n");
                }
            } while (nrOfMessages < 0);

            List<String> args = new ArrayList<>();
            args.add(String.valueOf(nrOfMessages));

            oOS.writeObject(new MessageRequest(null, userName, LocalDateTime.now(), usernameTarget, false, new Command(LIST_PRIVATE_MSG_CMD, args)));
        } catch (IOException e) {
            failover(e);
        }
    }

    /**
     * viewChannels
     */
    private void viewChannels() {
        boolean backMainMenu = false;
        int opt;
        Scanner scanner = new Scanner(System.in);

        System.out.print("\n");
        do {
            do {
                System.out.println("What do you want to see? ");
                System.out.println("1. My channels information");
                System.out.println("2. All channels information");
                System.out.println("0. Back to main menu");
                System.out.print("Opt: ");

                while(!scanner.hasNextInt()){
                    System.out.print("Opt: ");
                    scanner.next();
                }
                opt = scanner.nextInt();

                if (opt < 0 || opt > 2) {
                    System.out.println("\nInvalid option!\n");
                }
            } while (opt < 0 || opt > 2);

            try {
                System.out.print("\n");
                switch (opt) {
                    case 0:
                        backMainMenu = true;
                        break;
                    case 1:
                        oOS.writeObject(new MessageRequest(null, userName, LocalDateTime.now(), null, false, new Command(LIST_USER_CHANNELS_CMD, null)));
                        synchronized (lockObject) {
                            lockObject.wait();

                            if (lockObject.isViewChannelsSuccess()) {
                                lockObject.setViewChannelsSuccess(false);
                                System.out.println("\n\n\n");
                                break;
                            }
                        }
                    case 2:
                        oOS.writeObject(new MessageRequest(null, userName, LocalDateTime.now(), null, false, new Command(LIST_ALL_CHANNELS_CMD, null)));
                        synchronized (lockObject) {
                            lockObject.wait();

                            if (lockObject.isViewChannelsSuccess()) {
                                lockObject.setViewChannelsSuccess(false);
                                System.out.println("\n\n\n");
                                break;
                            }
                        }
                        break;
                }
            } catch (IOException | InterruptedException e) {
                failover(e);
            }
        } while (!backMainMenu);
        System.out.print("\n");
    }

    /**
     * failover method
     * @param e
     */
    private void failover(Exception e) {
        if (e instanceof SocketException) {
            try {
                synchronized (lockObject) {
                    lockObject.wait();

                    if (lockObject.isNewSocket()) {
                        socket = lockObject.getNewSocket();
                        oOS = new ObjectOutputStream(lockObject.getNewSocket().getOutputStream());
                        oIS = new ObjectInputStream(lockObject.getNewSocket().getInputStream());
                        lockObject.setNewSocket(null);

                        getChannelMessagesThread = new GetChannelMessagesThread(userName, userPassword, oIS, lockObject, socket, portSendFile, portReceiveFile);
                        getChannelMessagesThread.start();
                    }
                }
            } catch (InterruptedException | IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
