package com.example.Server.Data;

import RMI.RemoteObserver;
import com.example.Server.Data.ChannelData;
import com.example.Server.Data.ClientData;
import com.example.Server.Data.Files.FileWrapper;
import com.example.Server.Data.ServerControlData;
import com.example.Server.Data.ToSend.RedirectControl;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerStorageData {

    // Communication data
    private String serverIpMulticast;
    private String serverIpUDP;
    private String serverIpTCP;
    private int portTCP;
    private int portUDP;
    private Socket socket;
    private int portImages;
    private int portSendFiles;
    private int portSendFilesClient;
    private RedirectControl redirectControl;

    // Data Control
    private int serverNClients; //TODO: possivelmente retirar
    private List<ServerControlData> controlDataOtherServers; //TODO: sincronizar
    private List<ClientData> clients; //TODO: sincronizar
    private List<ChannelData> channels; //TODO: sincronizar
    private List<ClientData> newClientData; //TODO: sincronizar
    private List<ChannelData> newChannelData; //TODO: sincronizar
    private List<FileWrapper> filesToGetFromClient; //TODO: sincronizar
    private List<FileWrapper> filesToGetFromServers;
    private List<RemoteObserver> remoteObservers;
    private boolean exit;


    public ServerStorageData(){
        this.serverIpMulticast = "239.1.2.3";
        this.serverIpUDP = "127.0.0.1";
        this.serverIpTCP = "127.0.0.3";
        this.portTCP = 9008;
        this.portUDP = 9005;
        this.portImages = 8080;
        this.portSendFiles = 9030;
        this.portSendFilesClient = 9031;
        this.exit = false;
        this.controlDataOtherServers = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.channels = new ArrayList<>();
        this.newClientData = new ArrayList<>();
        this.newChannelData = new ArrayList<>();
        this.filesToGetFromClient = new ArrayList<>();
        this.filesToGetFromServers = new ArrayList<>();
        this.remoteObservers = new ArrayList<>();
        this.redirectControl = new RedirectControl(serverIpUDP, portUDP);

    }

    /*
    public ServerStorageData(){
        this.serverIpMulticast = "239.1.2.3";
        this.serverIpUDP = "127.0.0.2";
        this.serverIpTCP = "127.0.0.3";
        this.portTCP = 9010;
        this.portUDP = 9011;
        this.portImages = 8081;
        this.portSendFiles = 8088;
        this.portSendFilesClient = 8089;
        this.exit = false;
        this.controlDataOtherServers = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.channels = new ArrayList<>();
        this.newClientData = new ArrayList<>();
        this.newChannelData = new ArrayList<>();
        this.filesToGetFromClient = new ArrayList<>();
        this.filesToGetFromServers = new ArrayList<>();
        this.remoteObservers = new ArrayList<>();
        this.redirectControl = new RedirectControl(serverIpUDP, portUDP);
    }
 */

    public String getServerIpMulticast() {
        return serverIpMulticast;
    }

    public String getServerIpUDP() {
        return serverIpUDP;
    }

    public String getServerIpTCP() {
        return serverIpTCP;
    }

    public int getPortTCP() {
        return portTCP;
    }

    public int getPortUDP() {
        return portUDP;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getPortImages() {
        return portImages;
    }

    public int getPortSendFiles() {
        return portSendFiles;
    }

    public int getPortSendFilesClient() {
        return portSendFilesClient;
    }

    public RedirectControl getRedirectControl() {
        return redirectControl;
    }

    public int getServerNClients() {
        return serverNClients;
    }

    public List<ServerControlData> getControlDataOtherServers() {
        return controlDataOtherServers;
    }

    public List<ClientData> getClients() {
        return clients;
    }

    public List<ChannelData> getChannels() {
        return channels;
    }

    public List<ClientData> getNewClientData() {
        return newClientData;
    }

    public List<ChannelData> getNewChannelData() {
        return newChannelData;
    }

    public List<FileWrapper> getFilesToGetFromClient() {
        return filesToGetFromClient;
    }

    public List<FileWrapper> getFilesToGetFromServers() {
        return filesToGetFromServers;
    }

    public List<RemoteObserver> getRemoteObservers() {
        return remoteObservers;
    }

    public boolean isExit() {
        return exit;
    }

    public void setServerIpMulticast(String serverIpMulticast) {
        this.serverIpMulticast = serverIpMulticast;
    }

    public void setServerIpUDP(String serverIpUDP) {
        this.serverIpUDP = serverIpUDP;
    }

    public void setServerIpTCP(String serverIpTCP) {
        this.serverIpTCP = serverIpTCP;
    }

    public void setPortTCP(int portTCP) {
        this.portTCP = portTCP;
    }

    public void setPortUDP(int portUDP) {
        this.portUDP = portUDP;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setPortImages(int portImages) {
        this.portImages = portImages;
    }

    public void setPortSendFiles(int portSendFiles) {
        this.portSendFiles = portSendFiles;
    }

    public void setPortSendFilesClient(int portSendFilesClient) {
        this.portSendFilesClient = portSendFilesClient;
    }

    public void setRedirectControl(RedirectControl redirectControl) {
        this.redirectControl = redirectControl;
    }

    public void setServerNClients(int serverNClients) {
        this.serverNClients = serverNClients;
    }

    public void setControlDataOtherServers(List<ServerControlData> controlDataOtherServers) {
        this.controlDataOtherServers = controlDataOtherServers;
    }

    public void setClients(List<ClientData> clients) {
        this.clients = clients;
    }

    public void setChannels(List<ChannelData> channels) {
        this.channels = channels;
    }

    public void setNewClientData(List<ClientData> newClientData) {
        this.newClientData = newClientData;
    }

    public void setNewChannelData(List<ChannelData> newChannelData) {
        this.newChannelData = newChannelData;
    }

    public void setFilesToGetFromClient(List<FileWrapper> filesToGetFromClient) {
        this.filesToGetFromClient = filesToGetFromClient;
    }

    public void setFilesToGetFromServers(List<FileWrapper> filesToGetFromServers) {
        this.filesToGetFromServers = filesToGetFromServers;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public void setRemoteObservers(List<RemoteObserver> remoteObservers) {
        this.remoteObservers = remoteObservers;
    }

    /**
     * clearNewChannelData()
     */
    public void clearNewChannelData() {
        for(ChannelData channel : newChannelData){
            channel.getChat().clear();
        }
    }
}
