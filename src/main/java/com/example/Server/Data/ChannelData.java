package com.example.Server.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.Server.Data.ServerUtilsFunctions.clientsToString;

public class ChannelData implements Serializable {
    private static final long serialVersionUID = 1003L;
    private long channelId;
    private String name;
    private String password;
    private String description;
    private String creatorName;
    private List<MessageData> chat;
    private List<ClientData> clients;

    public ChannelData(String name, String password,String description, String creatorName) {
        this.name = name;
        this.password = password;
        this.chat = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.creatorName = creatorName;
        this.description = description;
    }

    public ChannelData(long channelId, String name, String password,String description, String creatorName) {
        this.channelId = channelId;
        this.name = name;
        this.password = password;
        this.chat = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.creatorName = creatorName;
        this.description = description;
    }

    public ChannelData(long channelId,
                       String name,
                       String password,
                       String description,
                       String creatorName,
                       List<MessageData> chat) {
        this.channelId = channelId;
        this.name = name;
        this.password = password;
        this.clients = new ArrayList<>();
        this.creatorName = creatorName;
        this.description = description;
        this.chat = chat;
        this.description = description;
    }

    // Getters
    public long getChannelId() {
        return channelId;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<ClientData> getClients() {
        return clients;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getDescription() {
        return description;
    }

    public List<MessageData> getChat() {
        return chat;
    }

    public List<MessageData> getLastNMessagesFromChat(int nrMessages) {
        List<MessageData> tempMessages = new ArrayList<>();

        for (int i = 0; i < nrMessages; i++){
            tempMessages.add(getChat().get(getChat().size() - i - 1));
        }
        return tempMessages;
    }

    // Setters
    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClients(List<ClientData> clients) {
        this.clients = clients;
    }

    public void clearClients(){ this.clients.clear(); }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addClient(ClientData client){
        if (!containsClient(client))
            clients.add(client);
    }

    public void removeClient(ClientData client){
            clients.remove(client);
    }

    public boolean containsClient(ClientData client){
        for(ClientData clientData : clients){
            if(clientData.getName().equals(client.getName()))
                return true;
        }
        return false;
    }

    public boolean verifyName(String name){
        return this.getName().equals(name);
    }

    public boolean verifyPassword(String password){
        return this.getPassword().equals(password);
    }

    public boolean verifyCreatorName(String creatorName){
        return this.getCreatorName().equals(creatorName);
    }

    public void addMessage(MessageData messageData){
        chat.add(messageData);
    }

    @Override
    public String toString() {
        return "ChannelData {" +
                "id= " + channelId + '\'' +
                " name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", description='" + description + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", clients=" + clientsToString(clients) +
                '}';
    }
}
