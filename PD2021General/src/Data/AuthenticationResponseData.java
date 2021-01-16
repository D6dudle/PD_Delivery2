package Data;

import java.io.Serializable;

public class AuthenticationResponseData implements Serializable {
    private static final long serialVersionUID = 1002L;

    private Boolean isRebalance;
    private Boolean isSuccess;
    private String operationMessage;
    private String imgUrl;
    private String ipUDP; //rebalanceamento
    private int portUDP; //rebalanceamento
    private int portImages;
    private String ipTCPServerSocket;
    private int portTCPServerSocket;

    public AuthenticationResponseData(Boolean isRebalance, Boolean isSuccess, String operationMessage, int portImages) {
        this.isRebalance = isRebalance;
        this.isSuccess = isSuccess;
        this.operationMessage = operationMessage;
        this.portImages = portImages;
    }


    public AuthenticationResponseData(Boolean isRebalance, Boolean isSuccess, String operationMessage) {
        this.isRebalance = isRebalance;
        this.isSuccess = isSuccess;
        this.operationMessage = operationMessage;
    }

    public AuthenticationResponseData(Boolean isRebalance, Boolean isSuccess, String operationMessage, String imgUrl, String ipUDP, int portUDP) {
        this.isRebalance = isRebalance;
        this.isSuccess = isSuccess;
        this.operationMessage = operationMessage;
        this.ipUDP = ipUDP;
        this.imgUrl = imgUrl;
        this.portUDP = portUDP;
    }

    public AuthenticationResponseData(Boolean isRebalance, Boolean isSuccess, String operationMessage, String ipTCPServerSocket, int portTCPServerSocket) {
        this.isRebalance = isRebalance;
        this.isSuccess = isSuccess;
        this.operationMessage = operationMessage;
        this.ipTCPServerSocket = ipTCPServerSocket;
        this.portTCPServerSocket = portTCPServerSocket;
    }

    public Boolean getRebalance() {
        return isRebalance;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public String getOperationMessage() {
        return operationMessage;
    }

    public String getIpUDP() {
        return ipUDP;
    }

    public int getPortUDP() {
        return portUDP;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getPortImages() {
        return portImages;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getPortTCPServerSocket() {
        return portTCPServerSocket;
    }

    public String getIpTCPServerSocket() {
        return ipTCPServerSocket;
    }

    @Override
    public String toString() {
        return "AuthenticationResponseData {" +
                " isRebalance=" + isRebalance +
                ", isSuccess=" + isSuccess +
                ", operationMessage='" + operationMessage + '\'' +
                ", ipUDP='" + ipUDP + '\'' +
                '}';
    }
}
