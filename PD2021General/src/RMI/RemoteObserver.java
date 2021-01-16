package RMI;

import Data.MessageRequest;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteObserver extends Remote {
    boolean isGetNotificacao()  throws RemoteException, IOException;
    void setExit(boolean value)  throws RemoteException;
    void printNotificacao(MessageRequest messageRequest, String channelName)throws RemoteException;
}
