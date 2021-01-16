package Data;

import java.io.Serializable;

public class AuthenticationRequestData implements Serializable {
    private static final long serialVersionUID = 1001L;

    private String username;
    private String password;
    private String imgUrl;
    private Boolean isRegister;
    private int portSendFile;
    private int portSendFilesClient;
    private String currentChannel;

    public AuthenticationRequestData(String username, String password, String imgUrl, Boolean register, int portSendFile, int portSendFilesClient) {
        this.username = username;
        this.password = password;
        this.imgUrl = imgUrl;
        this.isRegister = register;
        this.portSendFile = portSendFile;
        this.portSendFilesClient = portSendFilesClient;
        this.currentChannel = null;
    }

    public AuthenticationRequestData(String username, String password, String imgUrl, Boolean isRegister, int portSendFile, int portSendFilesClient, String currentChannel) {
        this.username = username;
        this.password = password;
        this.imgUrl = imgUrl;
        this.isRegister = isRegister;
        this.portSendFile = portSendFile;
        this.portSendFilesClient = portSendFilesClient;
        this.currentChannel = currentChannel;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Boolean getRegister() {
        return isRegister;
    }

    public int getPortSendFile() {
        return portSendFile;
    }

    public int getPortSendFilesClient() {
        return portSendFilesClient;
    }

    public String getCurrentChannel() {
        return currentChannel;
    }

    @Override
    public String toString() {
        return "LoginRequest {" +
                " username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
