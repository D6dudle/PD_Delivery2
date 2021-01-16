package com.example.Server.Data.Files;

import java.io.Serializable;

public class FileWrapper implements Serializable {
    private static final long serialVersionUID = 1005L;
    private String urlServer;
    private String usernameTarget;
    private String channelTarget;
    private int portGetFileFromServer;
    private String ipGetFileFromServer;
    private String ipFilePath; // Ip from client
    private int portFilePath; // Port from client

    public FileWrapper(String urlServer, String usernameTarget, String channelTarget) {
        this.urlServer = urlServer;
        this.usernameTarget = usernameTarget;
        this.channelTarget = channelTarget;
    }

    public FileWrapper(String urlServer, String usernameTarget, String channelTarget, int portGetFileFromServer, String ipGetFileFromServer, String ipFilePath, int portFilePath) {
        this.urlServer = urlServer;
        this.usernameTarget = usernameTarget;
        this.channelTarget = channelTarget;
        this.portGetFileFromServer = portGetFileFromServer;
        this.ipGetFileFromServer = ipGetFileFromServer;
        this.ipFilePath = ipFilePath;
        this.portFilePath = portFilePath;
    }

    public String getUrlServer() {
        return urlServer;
    }

    public String getUsernameTarget() {
        return usernameTarget;
    }

    public String getChannelTarget() {
        return channelTarget;
    }

    public int getPortGetFileFromServer() {
        return portGetFileFromServer;
    }

    public String getIpGetFileFromServer() {
        return ipGetFileFromServer;
    }

    public String getIpFilePath() {
        return ipFilePath;
    }

    public int getPortFilePath() {
        return portFilePath;
    }

    @Override
    public String toString() {
        return "FileWrapper {" +
                " urlServer='" + urlServer + '\'' +
                ", usernameTarget='" + usernameTarget + '\'' +
                ", channelTarget='" + channelTarget + '\'' +
                '}';
    }
}
