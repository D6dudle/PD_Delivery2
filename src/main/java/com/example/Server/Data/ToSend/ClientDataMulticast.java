package com.example.Server.Data.ToSend;


import com.example.Server.Data.MessageData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientDataMulticast implements Serializable {
    private static final long serialVersionUID = 1002L;
    private String ip;
    private int portTCP;
    private String userName;
    private String password;
    private List<MessageData> privateMessages;
    private List<MessageData> newFiles;
    private String pathImage;

    public ClientDataMulticast(String ip, int portTCP, String name, String password, String pathImage, List<MessageData> newFiles) {
        this.ip = ip;
        this.portTCP = portTCP;
        this.userName = name;
        this.password = password;
        this.privateMessages = new ArrayList<>();
        this.newFiles = newFiles;
        this.pathImage = pathImage;
    }

    public String getIp() {
        return ip;
    }

    public int getPortTCP() {
        return portTCP;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
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

    public List<MessageData> getNewFiles() {
        return newFiles;
    }

    public void addMessage(MessageData messageData){
        getPrivateMessages().add(messageData);
    }

    public String getPathImage() {
        return pathImage;
    }

    public void addFiles(List<MessageData> messageData){
        newFiles = messageData;
    }

    public void clearNewFiles(){
        newFiles.clear();
    }

    @Override
    public String toString() {
        return "ClientDataMulticast {" +
                " ip='" + ip + '\'' +
                ", portTCP=" + portTCP +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", privateMessages=" + privateMessages.size() +
                ", newFiles=" + newFiles.size() +
                ", pathImage='" + pathImage + '\'' +
                '}';
    }
}
