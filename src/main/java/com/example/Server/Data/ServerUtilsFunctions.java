package com.example.Server.Data;



import com.example.Server.Data.ToSend.ChannelDataMulticast;
import com.example.Server.Data.ToSend.ClientDataMulticast;

import java.util.List;

public class ServerUtilsFunctions {
    /**
     * Creates a string with all client information
     * @param clients
     * @return string with client info
     */
    public static String clientsToString(List<ClientData> clients) {
        StringBuilder str = new StringBuilder();
        for (ClientData c : clients) {
            str.append(c.toString()).append("\n");
        }
        return str.toString();
    }

    /**
     * Creates a string with all channel information
     * @param channels
     * @return string with channel info
     */
    public static String channelsToSendToString(List<ChannelDataMulticast> channels) {
        StringBuilder str = new StringBuilder();
        for (ChannelDataMulticast c : channels) {
            str.append(c.toString()).append("\n");
        }
        return str.toString();
    }

    /**
     * Creates a string with all client information
     * @param clients
     * @return string with client info
     */
    public static String clientsToSendToString(List<ClientDataMulticast> clients) {
        StringBuilder str = new StringBuilder();
        for (ClientDataMulticast c : clients) {
            str.append(c.toString()).append("\n");
        }
        return str.toString();
    }

}
