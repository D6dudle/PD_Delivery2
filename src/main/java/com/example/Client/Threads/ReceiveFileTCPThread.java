package com.example.Client.Threads;

import Data.DataUtils;

import java.io.*;
import java.net.Socket;

public class ReceiveFileTCPThread extends Thread implements DataUtils {
    private String serverIp;
    private int portReceiveFile;
    private String fileName;
    private String userName;

    /**
     * Constructor
     * @param serverIp
     * @param portReceiveFile
     * @param fileName
     * @param userName
     */
    public ReceiveFileTCPThread(String serverIp, int portReceiveFile, String fileName, String userName) {
        this.serverIp = serverIp;
        this.portReceiveFile = portReceiveFile;
        this.fileName = fileName;
        this.userName = userName;
    }

    /**
     * run
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run() {
        try {
            Socket s = new Socket(serverIp, portReceiveFile);

            File file = new File(fileName);
            String fPath = file.getAbsolutePath();
            fPath = fPath.substring(0, fPath.length() - fileName.length());
            file = new File(fPath + PATH_FOR_FILES + userName);

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

                if(nBytes > 0)
                    fOut.write(bufStr, 0, nBytes);

            } while (nBytes > 0);

            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
