package com.example.Server.Threads.ManageFiles;

import com.example.Server.Data.ServerUtils;

import java.io.*;
import java.net.Socket;

public class GetFileTCPThread extends Thread implements ServerUtils {
    private String clientIp;
    private int clientPort;
    private int clientFilePort;
    private String fileName;

    /**
     * Constructor
     * @param clientIp
     * @param clientPort
     * @param clientFilePort
     * @param fileName
     */
    public GetFileTCPThread(String clientIp, int clientPort, int clientFilePort, String fileName) {
        this.clientIp = clientIp;
        this.clientPort = clientPort;
        this.clientFilePort = clientFilePort;
        this.fileName = fileName;
    }

    /**
     * run
     */
    @Override
    public void run() {
        try {
            Socket s = new Socket(clientIp, clientFilePort);

            File file = new File(fileName);
            String fPath = file.getAbsolutePath();
            fPath = fPath.substring(0, fPath.length() - fileName.length());
            file = new File(fPath + PATH_FOR_FILES + clientIp + "_" + clientPort);

            // If the directory doesn't exist create a new one
            if (!file.exists()) {
                file.mkdirs();
            }
            fPath = file.getAbsolutePath();

            FileOutputStream fOut = new FileOutputStream(fPath + "\\" + fileName);

            ObjectInputStream oIS = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream oOS = new ObjectOutputStream(s.getOutputStream());

            oOS.writeObject(fileName);

            int nBytes;
            byte[] bufStr = new byte[MAX_DG_PACKAGE_SIZE];
            do {
                nBytes = oIS.read(bufStr);

                if (nBytes > 0)
                    fOut.write(bufStr, 0, nBytes);

            } while (nBytes > 0);

            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
