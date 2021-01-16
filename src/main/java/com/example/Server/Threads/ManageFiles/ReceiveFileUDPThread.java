package com.example.Server.Threads.ManageFiles;

import com.example.Server.Data.Files.FileWrapper;
import com.example.Server.Data.ServerUtils;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ReceiveFileUDPThread extends Thread implements ServerUtils {
    private DatagramSocket dS;
    private  FileWrapper fileWrapper;

    /**
     * Constructor
     * @param fileWrapper
     */
    public ReceiveFileUDPThread(FileWrapper fileWrapper) {
        this.fileWrapper = fileWrapper;
    }

    /**
     * run
     */
    @Override
    public void run() {
        DatagramPacket dPackage;
        byte[] buffer;

        try {
            dS = new DatagramSocket();

            ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
            ObjectOutputStream oOS = new ObjectOutputStream(bAOS);

            oOS.writeObject(fileWrapper);
            buffer = bAOS.toByteArray();

            dPackage = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(fileWrapper.getIpGetFileFromServer()), fileWrapper.getPortGetFileFromServer());
            dS.send(dPackage);

            // Creates the filepath
            File file = new File(fileWrapper.getUrlServer());
            String fPath = file.getAbsolutePath();
            fPath = fPath.substring(0, fPath.length() - fileWrapper.getUrlServer().length());
            //file = new File(fPath + PATH_FOR_FILES + fileWrapper.getIpFilePath() + "_" + fileWrapper.getPortFilePath());
            file = new File(fPath + PATH_FOR_FILES + fileWrapper.getIpFilePath() + "_serverGetFileUDP_" + fileWrapper.getPortFilePath()); //TODO: retirar underscores a mais

            System.out.println("\n\n" + file.getAbsolutePath() + "\n\n");

            // If the directory doesn't exist create a new one
            if (!file.exists()) {
                file.mkdirs();
            }
            fPath = file.getAbsolutePath();

            FileOutputStream fOut = new FileOutputStream(fPath + "\\" + fileWrapper.getUrlServer());

            do {
                dPackage = new DatagramPacket(new byte[MAX_DG_PACKAGE_SIZE], MAX_DG_PACKAGE_SIZE);
                dS.receive(dPackage);

                fOut.write(dPackage.getData(), 0, dPackage.getLength());
            } while (dPackage.getLength() > 0);

            fOut.close();
            dS.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
