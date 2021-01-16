package com.example.Client;

import com.example.Client.Threads.ClientThread;

public class Main {

    public static void main(String[] args) {
        ClientThread clientThread = new ClientThread("localhost", 9005, "127.0.0.3", 9008, 8080, 9031);
        //ClientThread clientThread = new ClientThread("localhost", 9011, "127.0.0.3", 9008, 8081, 8089);
        clientThread.runClient();
    }
}
