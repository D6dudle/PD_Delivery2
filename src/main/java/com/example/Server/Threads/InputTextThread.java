package com.example.Server.Threads;

import com.example.Server.Data.ChannelData;
import com.example.Server.Data.ClientData;
import com.example.Server.DataBase.DataBaseLink;
import com.example.Server.Data.ServerStorageData;

import java.sql.SQLException;
import java.util.Scanner;

public class InputTextThread extends Thread{
    private DataBaseLink serverDbLink;
    private ServerStorageData serverStorageData;

    /**
     *
     * @param serverStorageData
     */
    public InputTextThread(ServerStorageData serverStorageData) {
        this.serverStorageData = serverStorageData;
    }

    /**
     * run
     */
    @Override
    public void run() {
        try {
            int opt;
            Scanner scanner = new Scanner(System.in);
            serverDbLink = new DataBaseLink();

            System.out.println(" ----- Welcome SLaX admin -----");
            do{
                do {
                    System.out.println("1. Users");
                    System.out.println("2. Channels");
                    System.out.println("3. Statistics");
                    System.out.println("0. Exit");
                    System.out.print("Opt: ");

                    while (!scanner.hasNextInt()) {
                        System.out.print("Opt: ");
                        scanner.next();
                    }
                    opt = scanner.nextInt();

                    if (opt < 0 || opt > 3) {
                        System.out.println("\nInvalid option!\n");
                    }
                } while(opt < 0 || opt > 3);

                switch (opt) {
                    case 0:
                        serverStorageData.setExit(true);
                        break;
                    case 1:
                        manageUsers();
                        break;
                    case 2:
                        manageChannels();
                        break;
                    case 3:
                        presentStatistics();
                        break;
                }

            } while(!serverStorageData.isExit());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Display client information
     */
    private void manageUsers() {
        int opt;
        boolean returnToMainMenu = false;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("\n\n ----- Users -----");
            do {
                System.out.println("1. View all users");
                System.out.println("2. View online users");
                System.out.println("0. Exit");
                System.out.print("Opt: ");

                while (!scanner.hasNextInt()) {
                    System.out.print("Opt: ");
                    scanner.next();
                }
                opt = scanner.nextInt();

                if (opt < 0 || opt > 2) {
                    System.out.println("\nInvalid option!\n");
                }
            } while (opt < 0 || opt > 2);

            System.out.print("\n");
            try {
                switch (opt) {
                    case 0:
                        returnToMainMenu = true;
                        break;
                    case 1:
                        for (String str : serverDbLink.getAllClientNames()) {
                            System.out.println(str);
                        }
                        break;
                    case 2:
                        if (serverStorageData.getClients().isEmpty()) {
                            System.out.println("No clients online yet!");
                            break;
                        }

                        for (ClientData clientData : serverStorageData.getClients())
                            System.out.println(clientData.getName() + " (" + clientData.getIp() + ")");
                        break;
                }
            } catch (SQLException e) {
                System.out.println("Couldn't do that operation. Try again later!");
                returnToMainMenu = true;
            }
        } while (!returnToMainMenu);
        System.out.print("\n");
    }

    /**
     * Display channel information
     */
    private void manageChannels() {
        int opt;
        String channelName;
        boolean returnToMainMenu = false;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("\n\n ----- Channels -----");
            do {
                System.out.println("1. View all channels information");
                System.out.println("2. View all channel names with users");
                System.out.println("3. View a channel information");
                System.out.println("4. View a files in a channel");
                System.out.println("0. Exit");
                System.out.print("Opt: ");

                while (!scanner.hasNextInt()) {
                    System.out.print("Opt: ");
                    scanner.next();
                }
                opt = scanner.nextInt();

                if (opt < 0 || opt > 4) {
                    System.out.println("\nInvalid option!\n");
                }
            } while (opt < 0 || opt > 4);

            System.out.print("\n");
            try {
                switch (opt) {
                    case 0:
                        returnToMainMenu = true;
                        break;
                    case 1:
                        for (String str : serverDbLink.getAllChannelsInformation())
                            System.out.println(str);
                        break;
                    case 2:
                        if (serverStorageData.getChannels().isEmpty()) {
                            System.out.println("No channels created yet!");
                            break;
                        }

                        for (ChannelData channelData : serverStorageData.getChannels()) {
                            if (!channelData.getClients().isEmpty()) {
                                System.out.println(channelData.getName() + " with " + channelData.getClients().size() + " clients.");

                                for (ClientData clientData : channelData.getClients())
                                    System.out.println("\t- " + clientData.getName());
                            } else
                                System.out.println("No users logged in yet!");
                        }
                        break;
                    case 3:
                        System.out.print("Channel name: ");
                        scanner.nextLine();
                        channelName = scanner.nextLine();

                        System.out.println(serverDbLink.getChannelsInformation(channelName));
                        break;
                    case 4:
                        System.out.print("Channel name: ");
                        scanner.nextLine();
                        channelName = scanner.nextLine();

                        for (String str : serverDbLink.getAllFilesInAChannel(channelName))
                            System.out.println(str);
                        break;
                }
            } catch (SQLException e) {
                System.out.println("Couldn't do that operation. Try again later!");
                returnToMainMenu = true;
            }
        } while (!returnToMainMenu);
        System.out.print("\n");
    }

    /**
     * Display statistics from one or all the channels
     */
    private void presentStatistics() {
        int opt;
        boolean returnToMainMenu = false;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("\n\n ----- Statistics -----");
            do {
                System.out.println("1. View all channels statistics");
                System.out.println("2. View channel statistic");
                System.out.println("0. Exit");
                System.out.print("Opt: ");

                while (!scanner.hasNextInt()) {
                    System.out.print("Opt: ");
                    scanner.next();
                }
                opt = scanner.nextInt();

                if (opt < 0 || opt > 2) {
                    System.out.println("\nInvalid option!\n");
                }
            } while (opt < 0 || opt > 2);

            System.out.print("\n");
            try {
                switch (opt) {
                    case 0:
                        returnToMainMenu = true;
                        break;
                    case 1:
                        for (String str : serverDbLink.getChannelsStatistics())
                            System.out.println(str);
                        break;
                    case 2:
                        System.out.print("Channel name: ");
                        scanner.nextLine();
                        String channelName = scanner.nextLine();

                        for (String str : serverDbLink.getChannelStatistics(channelName))
                            System.out.println(str);
                        break;
                }
            } catch (SQLException e) {
                System.out.println("Couldn't do that operation. Try again later!");
                returnToMainMenu = true;
            }
        } while (!returnToMainMenu);
        System.out.print("\n");
    }
}
