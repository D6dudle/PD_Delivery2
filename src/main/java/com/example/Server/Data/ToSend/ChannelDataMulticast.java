package com.example.Server.Data.ToSend;

import com.example.Server.Data.MessageData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.Server.Data.ServerUtilsFunctions.clientsToSendToString;


public class ChannelDataMulticast implements Serializable {
    private static final long serialVersionUID = 1003L;
    private long channelId;
    private String name;
    private String password;
    private String description;
    private String creatorName;
    private List<MessageData> chat;
    private List<ClientDataMulticast> clients;
    private String oldName;

    public ChannelDataMulticast(long channelId, String name, String password, String description, String creatorName) {
        this.channelId = channelId;
        this.name = name;
        this.password = password;
        this.chat = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.creatorName = creatorName;
        this.description = description;
        this.oldName = null;
    }

    public ChannelDataMulticast(long channelId, String name, String password, String description, String creatorName, String oldName) {
        this.channelId = channelId;
        this.name = name;
        this.password = password;
        this.chat = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.creatorName = creatorName;
        this.description = description;
        this.oldName = oldName;
    }

    public long getChannelId() {
        return channelId;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<ClientDataMulticast> getClients() {
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

    public void setChat(List<MessageData> chat) {
        this.chat = chat;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClients(List<ClientDataMulticast> clients) {
        this.clients = clients;
    }

    public void clearClients(){ this.clients.clear(); }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addClient(ClientDataMulticast client){
        if (!containsClient(client))
            clients.add(client);
    }

    public void removeClient(ClientDataMulticast client){
        clients.remove(client);
    }

    public boolean containsClient(ClientDataMulticast client){
        for(ClientDataMulticast channelClient : clients){
            if(channelClient.getUserName().equals(client.getUserName()))
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
        return "ChannelDataMulticast {" +
                "id= " + channelId + '\'' +
                " name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", description='" + description + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", clients=" + clientsToSendToString(clients) +
                '}';
    }
}
