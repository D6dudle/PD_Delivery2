package com.example.Server.Data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientData implements Serializable {
    private static final long serialVersionUID = 1002L;
    private String ip;
    private int portTCP;
    private int portSendFile;
    private int portSendFilesClient;
    private String name;
    private String password;
    private Socket socket;
    private List<MessageData> privateMessages;
    private List<MessageData> newFiles;
    private String pathImage;
    private ObjectOutputStream oOS;
    private ObjectInputStream oIS;

    public ClientData(String ip, int portTCP, int portSendFile, int portSendFilesClient, String name, String password, String pathImage) {
        this.ip = ip;
        this.portTCP = portTCP;
        this.portSendFile = portSendFile;
        this.portSendFilesClient = portSendFilesClient;
        this.name = name;
        this.password = password;
        this.privateMessages = new ArrayList<>();
        this.newFiles = new ArrayList<>();
        this.pathImage = pathImage;
        this.oOS = null;
        this.oIS = null;
    }

    public ClientData(String ip,
                      int portTCP,
                      int portSendFile,
                      int portSendFilesClient,
                      String name,
                      String password,
                      String pathImage,
                      List<MessageData> privateMessages,
                      List<MessageData> newFiles) {
        this.ip = ip;
        this.portTCP = portTCP;
        this.portSendFile = portSendFile;
        this.portSendFilesClient = portSendFilesClient;
        this.name = name;
        this.password = password;
        this.privateMessages = new ArrayList<>();
        this.newFiles = new ArrayList<>();
        this.pathImage = pathImage;
        this.oOS = null;
        this.oIS = null;
        this.privateMessages = privateMessages;
        this.newFiles = newFiles;
    }

    public String getIp() {
        return ip;
    }

    public int getPortTCP() {
        return portTCP;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.oIS = new ObjectInputStream(socket.getInputStream());
        this.oOS = new ObjectOutputStream(socket.getOutputStream());
    }

    public ObjectOutputStream getOOS() {
        return oOS;
    }

    public ObjectInputStream getOIS() {
        return oIS;
    }

    public List<MessageData> getPrivateMessages() {
        return privateMessages;
    }

    public List<MessageData> getLastNPrivateMessages(int nrMessages) {
        List<MessageData> tempMessages = new ArrayList<>();

        for (int i = 0; i < nrMessages; i++){
            tempMessages.add(getPrivateMessages().get(getPrivateMessages().size() - i - 1));
        }
        return tempMessages;
    }

    public void addMessage(MessageData messageData){
        getPrivateMessages().add(messageData);
    }

    public String getPathImage() {
        return pathImage;
    }

    public int getPortSendFile() {
        return portSendFile;
    }

    public int getPortSendFilesClient() {
        return portSendFilesClient;
    }

    public void addNewFile(MessageData messageData){
        newFiles.add(messageData);
    }

    public List<MessageData> getNewFiles() {
        return newFiles;
    }

    public void clearNewFiles(){
        newFiles.clear();
    }

    @Override
    public String toString() {
        return "ClientData {" +
                " ip='" + ip + '\'' +
                ", portTCP=" + portTCP +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
