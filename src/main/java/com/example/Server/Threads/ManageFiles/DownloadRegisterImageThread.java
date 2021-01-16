package com.example.Server.Threads.ManageFiles;

import com.example.Server.Data.ServerUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DownloadRegisterImageThread extends Thread implements ServerUtils {
    private DatagramSocket dS;
    final private String ipClient;
    final private int portClient;
    final private String fileName;
    final private int portImages;

    /**
     * Constructor
     * @param ipClient
     * @param portClient
     * @param fileName
     * @param portImages
     */
    public DownloadRegisterImageThread(String ipClient, int portClient, String fileName, int portImages) {
        this.dS = null;
        this.ipClient = ipClient;
        this.portClient = portClient;
        this.fileName = fileName;
        this.portImages = portImages;
    }

    /**
     * run
     */
    @Override
    public void run() {
        try {
            dS = new DatagramSocket(portImages);
            // Creates the filepath
            File file = new File(fileName);
            String fPath = file.getAbsolutePath();
            fPath = fPath.substring(0, fPath.length() - fileName.length());
            file = new File(fPath + PATH_FOR_FILES + ipClient + "_" + portClient);

            // If the directory doesn't exist create a new one
            if (!file.exists()) {
                file.mkdirs();
            }
            fPath = file.getAbsolutePath();

            FileOutputStream fOut = new FileOutputStream(fPath + "\\" + fileName);
            DatagramPacket dP = new DatagramPacket(new byte[MAX_DG_PACKAGE_SIZE], MAX_DG_PACKAGE_SIZE);

            do {
                dS.receive(dP);
                fOut.write(dP.getData(), 0, dP.getLength());
            } while (dP.getLength() > 0);

            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
