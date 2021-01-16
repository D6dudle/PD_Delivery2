package com.example.Server.Threads.ManageFiles;

import com.example.Server.Data.Files.FileWrapper;
import com.example.Server.Data.ServerUtils;
import com.example.Server.Data.ServerStorageData;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class SendFileUDPThread extends Thread implements ServerUtils {
    private DatagramSocket dS;
    private ServerStorageData serverStorageData;

    /**
     * Constructor
     * @param serverStorageData
     */
    public SendFileUDPThread(ServerStorageData serverStorageData) {
        this.serverStorageData = serverStorageData;
    }

    /**
     * run
     */
    @Override
    public void run() {
        DatagramPacket dPackage;
        byte[] buffer;
        byte[] bufDP;

        try {
            dS = new DatagramSocket(serverStorageData.getPortSendFiles());
            while (true) {
                int nBytes;

                buffer = new byte[MAX_DG_PACKAGE_SIZE];
                dPackage = new DatagramPacket(buffer, buffer.length);

                dS.receive(dPackage);
                bufDP = dPackage.getData();

                ByteArrayInputStream bAIS = new ByteArrayInputStream(bufDP);
                ObjectInputStream oIS = new ObjectInputStream(bAIS);
                FileWrapper fileWrapper = (FileWrapper) oIS.readObject(); //wait for a request

                File file = new File(fileWrapper.getUrlServer());
                String fPath = file.getAbsolutePath();
                fPath = fPath.substring(0, fPath.length() - fileWrapper.getUrlServer().length());
                fPath += PATH_FOR_FILES + fileWrapper.getIpFilePath() + "_" + fileWrapper.getPortFilePath() + "\\" + fileWrapper.getUrlServer();
                FileInputStream fIS = new FileInputStream(fPath);

                buffer = new byte[MAX_DG_PACKAGE_SIZE];
                dPackage.setData(buffer);

                do {
                    nBytes = fIS.read(buffer);

                    // In order to send an empty package at the end
                    if (nBytes < 0) {
                        nBytes = 0;
                    }

                    dPackage.setLength(nBytes);
                    dS.send(dPackage);
                } while (nBytes > 0);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            dS.close();
        }
    }

}
