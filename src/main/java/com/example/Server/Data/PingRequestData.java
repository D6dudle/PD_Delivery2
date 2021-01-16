package com.example.Server.Data;

import com.example.Server.Data.ToSend.ChannelDataMulticast;
import com.example.Server.Data.ToSend.ClientDataMulticast;

import java.io.Serializable;
import java.util.List;

public class PingRequestData implements Serializable {
    private static final long serialVersionUID = 1000L;
    private ServerControlData serverControlData;

    public PingRequestData(ServerControlData serverControlData) {
        this.serverControlData = serverControlData;
    }

    // Getters
    public String getIpUDP() {
        return serverControlData.getIpUDP();
    }

    public int getPortUDP() {
        return serverControlData.getPortUDP();
    }

    public int getNClients(){
        return serverControlData.getNClients();
    }

    public List<ClientDataMulticast> getClients() {
        return serverControlData.getClients();
    }

    public List<ChannelDataMulticast> getChannels() {
        return serverControlData.getChannels();
    }

    public ServerControlData getServerControlData() {
        return serverControlData;
    }

    public int getPortSendFiles() {
        return serverControlData.getPortSendFiles();
    }

    // Setters
    public void setClients(List<ClientDataMulticast> clients) {
        serverControlData.setClients(clients);
    }

    public void setChannels(List<ChannelDataMulticast> channels) {
        serverControlData.setChannels(channels);
    }

    @Override
    public String toString() {
        return "PingRequestData {" +
                " serverControlData=" + serverControlData.toString() +
                '}';
    }
}
