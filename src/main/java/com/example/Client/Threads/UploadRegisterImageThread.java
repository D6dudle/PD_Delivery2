package com.example.Client.Threads;

import Data.DataUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UploadRegisterImageThread extends Thread implements DataUtils {
    private DatagramSocket dS;
    private final InetAddress ipServerUdp;
    private final String filePath;
    private final int portImages;

    /**
     * Constructor
     * @param ipServerUdp
     * @param filePath
     * @param portImages
     */
    public UploadRegisterImageThread(InetAddress ipServerUdp, String filePath, int portImages) {
        this.ipServerUdp = ipServerUdp;
        this.filePath = filePath;
        this.portImages = portImages;
    }

    /**
     * run
     */
    @Override
    public void run() {
        int nBytes;
        try {
            dS = new DatagramSocket();

            byte[] buffer = new byte[MAX_DG_PACKAGE_SIZE];
            DatagramPacket dPackage = new DatagramPacket(buffer, buffer.length, ipServerUdp, portImages);

            File file = new File(filePath);
            String fPath = file.getAbsolutePath();
            fPath = fPath.substring(0, fPath.length() - filePath.length());
            fPath += PATH_FOR_LOGIN_IMAGES + filePath;
            FileInputStream fIS = new FileInputStream(fPath);

            do {
                nBytes = fIS.read(buffer);

                // In order to send an empty package at the end
                if (nBytes < 0) {
                    nBytes = 0;
                }

                dPackage.setLength(nBytes);
                dS.send(dPackage);
            } while (nBytes > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
