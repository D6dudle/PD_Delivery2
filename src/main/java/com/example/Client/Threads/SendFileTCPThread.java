package com.example.Client.Threads;

import Data.DataUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SendFileTCPThread extends Thread implements DataUtils {

    private int portSendFile;

    /**
     * Constructor
     * @param portSendFile
     */
    public SendFileTCPThread(int portSendFile) {
        this.portSendFile = portSendFile;
    }

    /**
     * run
     */
    @Override
    public void run() {
        ServerSocket serverSocket;
        Socket s;

        try {
            serverSocket = new ServerSocket(portSendFile);

            s = serverSocket.accept();

            ObjectOutputStream oOS = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream oIS = new ObjectInputStream(s.getInputStream());

            String filePath = (String) oIS.readObject();

            File file = new File(filePath);
            String fPath = file.getAbsolutePath();
            fPath = fPath.substring(0, fPath.length() - filePath.length());
            fPath += PATH_FOR_FILES + filePath;
            FileInputStream fIS = new FileInputStream(fPath);

            int nBytes;
            byte[] buffer = new byte[MAX_DG_PACKAGE_SIZE];
            do {
                nBytes = fIS.read(buffer);

                if (nBytes < 0) {
                    nBytes = 0;
                }

                oOS.write(buffer);
                oOS.flush();
            } while (nBytes > 0);

            fIS.close();
            s.close();
            serverSocket.close();
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
