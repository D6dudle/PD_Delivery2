package com.example.Server.Threads.ManageFiles;

import com.example.Server.Data.ServerUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SendFileTCPThread extends Thread implements ServerUtils {
    private int portSendFilesClient;
    private String clientIp;
    private int clientPort;

    /**
     * Constructor
     * @param portSendFilesClient
     */
    public SendFileTCPThread(int portSendFilesClient, String clientIp, int clientPort) {
        this.portSendFilesClient = portSendFilesClient;
        this.clientIp = clientIp;
        this.clientPort = clientPort;
    }

    /**
     * run
     */
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(portSendFilesClient);

            Socket s = serverSocket.accept();

            ObjectOutputStream oOS = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream oIS = new ObjectInputStream(s.getInputStream());

            String fileName = (String) oIS.readObject();

            File file = new File(fileName);
            String fPath = file.getAbsolutePath();
            fPath = fPath.substring(0, fPath.length() - fileName.length());
            file = new File(fPath + PATH_FOR_FILES + clientIp + "_" + clientPort);
            FileInputStream fIS = new FileInputStream(file + "\\" + fileName);

            int nBytes;
            byte[] buffer = new byte[MAX_DG_PACKAGE_SIZE];
            do{
                nBytes = fIS.read(buffer);

                if (nBytes < 0) {
                    nBytes = 0;
                }

                oOS.write(buffer);
                oOS.flush();
            } while (nBytes > 0);
            s.close();

            //serverSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
