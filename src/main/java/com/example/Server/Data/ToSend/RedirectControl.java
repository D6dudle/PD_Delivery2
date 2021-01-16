package com.example.Server.Data.ToSend;

public class RedirectControl {
    private String ipUDPRedirect;
    private int portUDPRedirect;

    public RedirectControl() {
    }

    public RedirectControl(String ipUDPRedirect, int portUDPRedirect) {
        this.ipUDPRedirect = ipUDPRedirect;
        this.portUDPRedirect = portUDPRedirect;
    }

    public String getIpUDPRedirect() {
        return ipUDPRedirect;
    }

    public void setIpUDPRedirect(String ipUDPRedirect) {
        this.ipUDPRedirect = ipUDPRedirect;
    }

    public int getPortUDPRedirect() {
        return portUDPRedirect;
    }

    public void setPortUDPRedirect(int portUDPRedirect) {
        this.portUDPRedirect = portUDPRedirect;
    }
}
