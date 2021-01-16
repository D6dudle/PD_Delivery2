package Data;

import java.net.Socket;

public class LockObject {
    private boolean exitFlag;
    private boolean createChannelSuccess;
    private boolean joinChannelSuccess;
    private boolean deleteChannelSuccess;
    private boolean viewChannelsSuccess;
    private String currentChannelName;
    private String currentUserNameTarget;
    private Socket newSocket;
    private String ipUDPRedirect;
    private int portUDPRedirect;

    public LockObject(boolean exitFlag, boolean createChannelSuccess, boolean joinChannelSuccess, boolean deleteChannelSuccess, boolean viewChannelsSuccess, String ipUDPRedirect, int portUDPRedirect) {
        this.exitFlag = exitFlag;
        this.createChannelSuccess = createChannelSuccess;
        this.joinChannelSuccess = joinChannelSuccess;
        this.deleteChannelSuccess = deleteChannelSuccess;
        this.viewChannelsSuccess = viewChannelsSuccess;
        this.newSocket = null;
        this.currentChannelName = null;
        this.currentUserNameTarget = null;
        this.ipUDPRedirect = ipUDPRedirect;
        this.portUDPRedirect = portUDPRedirect;
    }

    // Setters
    public void setExitFlag(boolean exitFlag) {
        this.exitFlag = exitFlag;
    }

    public void setCreateChannelSuccess(boolean createChannelSuccess) {
        this.createChannelSuccess = createChannelSuccess;
    }

    public void setJoinChannelSuccess(boolean joinChannelSuccess) {
        this.joinChannelSuccess = joinChannelSuccess;
    }

    public void setCurrentChannelName(String currentChannelName) {
        this.currentChannelName = currentChannelName;
    }

    public void setDeleteChannelSuccess(boolean deleteChannelSuccess) {
        this.deleteChannelSuccess = deleteChannelSuccess;
    }

    public void setCurrentUserNameTarget(String currentUserNameTarget) {
        this.currentUserNameTarget = currentUserNameTarget;
    }

    public void setViewChannelsSuccess(boolean viewChannelsSuccess) {
        this.viewChannelsSuccess = viewChannelsSuccess;
    }

    public void setNewSocket(Socket newSocket) {
        this.newSocket = newSocket;
    }

    // Getters
    public boolean isExitFlag(){
        return this.exitFlag;
    }

    public boolean isCreateChannelSuccess(){
        return this.createChannelSuccess;
    }

    public boolean isJoinChannelSuccess() {
        return this.joinChannelSuccess;
    }

    public String getCurrentChannelName() {
        return currentChannelName;
    }

    public String getCurrentUserNameTarget() {
        return currentUserNameTarget;
    }

    public boolean isDeleteChannelSuccess() {
        return this.deleteChannelSuccess;
    }

    public boolean isViewChannelsSuccess() {
        return viewChannelsSuccess;
    }

    public Socket getNewSocket() {
        return newSocket;
    }

    public boolean isNewSocket() {
        return newSocket != null;
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

    @Override
    public String toString() {
        return "LockObject {" +
                " exitFlag=" + exitFlag +
                ", createChannelSuccess=" + createChannelSuccess +
                ", joinChannelSuccess=" + joinChannelSuccess +
                ", deleteChannelSuccess=" + deleteChannelSuccess +
                ", currentChannelName='" + currentChannelName + '\'' +
                ", currentUserNameTarget='" + currentUserNameTarget + '\'' +
                '}';
    }
}
