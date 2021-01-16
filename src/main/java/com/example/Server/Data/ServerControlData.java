package com.example.Server.Data;


import com.example.Server.Data.ToSend.ChannelDataMulticast;
import com.example.Server.Data.ToSend.ClientDataMulticast;

import java.io.Serializable;
import java.util.List;

public class ServerControlData implements Serializable {
    private static final long serialVersionUID = 1001L;
    private String ipUDP;
    private int portUDP;
    private int portSendFiles;
    private List<ClientDataMulticast> clients;
    private List<ChannelDataMulticast> channels;

    public ServerControlData(String ipUDP, int portUDP, int portSendFiles, List<ClientDataMulticast> clients, List<ChannelDataMulticast> channels) {
        this.ipUDP = ipUDP;
        this.clients = clients;
        this.channels = channels;
        this.portUDP = portUDP;
        this.portSendFiles = portSendFiles;
    }

    // Getters
    public String getIpUDP() {
        return ipUDP;
    }

    public int getNClients(){
        return clients.size();
    }

    public List<ClientDataMulticast> getClients() {
        return clients;
    }

    public List<ChannelDataMulticast> getChannels() {
        return channels;
    }

    public int getPortUDP() {
        return portUDP;
    }

    public int getPortSendFiles() {
        return portSendFiles;
    }

    // Setters
    public void setClients(List<ClientDataMulticast> clients) {
        this.clients = clients;
    }

    public void setChannels(List<ChannelDataMulticast> channels) {
        this.channels = channels;
    }

    @Override
    public String toString() {
        return "ServerControlData {" +
                " ipUDP='" + ipUDP + '\'' +
                ", portUDP=" + portUDP +
                ", portSendFiles=" + portSendFiles +
                ", clients=" + clients.size() +
                ", channels=" + channels.size() +
                '}';
    }


}
