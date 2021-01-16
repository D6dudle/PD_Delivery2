package com.example.Observer;

import Data.AuthenticationRequestData;
import Data.AuthenticationResponseData;
import Data.MessageRequest;
import RMI.RemoteObserver;
import RMI.RemoteServer;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class RemoteObserverClass extends UnicastRemoteObject implements RemoteObserver {

    private boolean getNotificacoes, exit;

    protected RemoteObserverClass() throws RemoteException {
    }

    @Override
    public boolean isGetNotificacao() throws IOException {
        return getNotificacoes;
    }

    @Override
    public void setExit(boolean value) {
        exit = value;
    }

    @Override
    public void printNotificacao(MessageRequest messageRequest, String channelName) throws RemoteException {
        System.out.println("\n\n");

        if(channelName != null && channelName != "")
            System.out.println("Message from user " +
                    messageRequest.getNameOrigin() + " to channel " +
                    channelName + ": " +
                    messageRequest.getMessage());
        else
            System.out.println("Message from user " +
                    messageRequest.getNameOrigin() + " to " +
                    messageRequest.getUsernameTarget() + ": " +
                    messageRequest.getMessage());

        System.out.println("\n\nOpt:");
    }

    private void observerUI(List<RemoteServer> remoteServers, RemoteObserverClass remoteObserverClass) {
        System.out.println("------ Welcome to SlaX ------\n");

        int opt = -1;
        Scanner scanner = new Scanner(System.in);
        do {
            do {
                System.out.println("\n\n\n");
                System.out.println("Do you wish to:");
                System.out.println("1. Register observer");
                System.out.println("2. Unregister observer");
                System.out.println("3. Register client");
                System.out.println("4. Send Message");
                System.out.println("5. Get message's notifications");
                System.out.println("6. Stop notifications");
                System.out.print("Opt: ");

                while (!scanner.hasNextInt()) {
                    System.out.print("Opt: ");
                    scanner.next();
                }
                opt = scanner.nextInt();

                if (opt < 0 || opt > 6) {
                    System.out.println("\nInvalid option!\n");
                }

                System.out.println("\n\n\n");
            } while (opt < 0 || opt > 6);

            switch (opt) {
                case 1:
                    try {
                        for(RemoteServer remoteServer : remoteServers){
                            if(remoteServer.registerObserver(remoteObserverClass)){
                                System.out.println("Observer's register operation successful.");
                            }
                            else{
                                System.out.println("Error in register operation.");
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        for(RemoteServer remoteServer : remoteServers){
                            if(remoteServer.unregisterObserver(remoteObserverClass)){
                                System.out.println("Observer unregister operation successful.");
                            }
                            else{
                                System.out.println("Error in unregister operation.");
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    for(RemoteServer remoteServer : remoteServers){
                        registerClient(remoteServer);
                    }
                    break;
                case 4:
                    for(RemoteServer remoteServer : remoteServers) {
                        sendMessage(remoteServer);
                    }
                    break;
                case 5:
                    getNotificacoes = true;
                    break;
                case 6:
                    getNotificacoes = false;
                    break;
                default:
                    break;
            }
        }while(!exit);
    }

    private void sendMessage(RemoteServer remoteServer) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Insira a mensagem a distribuir: ");
        String message = scanner.nextLine();

        MessageRequest messageRequest = new MessageRequest(message, "Observer", LocalDateTime.now(), null, true, null);
        try {
            remoteServer.sendMessageToAllClients(messageRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerClient(RemoteServer remoteServer){
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n\n--- Register ---");
        System.out.print("Username: ");
        String userName = scanner.nextLine();
        System.out.print("Password: ");
        String userPassword = scanner.nextLine();
        System.out.print("Image URL: ");
        String imgUrl = scanner.nextLine();

        AuthenticationResponseData authenticationResponseData =
                null;
        try {
            authenticationResponseData = remoteServer.register(new AuthenticationRequestData(userName, userPassword, imgUrl, true, 9000, 9001));
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        System.out.println("Resposta do servidor: " + authenticationResponseData.getOperationMessage());
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {

        //TODO: como fazer isto p mulçtiplos server c ips diferentes (se na msm maquina, portos diferentes)

        List<Integer> servers = new ArrayList<>(List.of(1099,
                1200,
                1301));
        List<RemoteServer> remoteServers = new ArrayList<>();

        for(Integer i : servers){
            Registry registry = LocateRegistry.getRegistry(i);
            Remote remoteObject = registry.lookup(RemoteServer.BIND_NAME_SERVER);
            RemoteServer remoteServer = (RemoteServer) remoteObject;
            remoteServers.add(remoteServer);
        }




        RemoteObserverClass remoteObserverClass = new RemoteObserverClass();

        remoteObserverClass.observerUI(remoteServers, remoteObserverClass);
    }
}
