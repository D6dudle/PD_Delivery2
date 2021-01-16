package RMI;

import Data.AuthenticationRequestData;
import Data.AuthenticationResponseData;
import Data.MessageRequest;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface RemoteServer extends Remote {
    String BIND_NAME_SERVER = "serverBindService";
    int MAX_CHUNK_LENGTH = 512;
    AuthenticationResponseData register(AuthenticationRequestData authenticationRequestData) throws RemoteException, SQLException;
    boolean registerObserver(RemoteObserver remoteObserver) throws RemoteException;
    boolean unregisterObserver(RemoteObserver remoteObserver) throws RemoteException;
    void sendMessageToAllClients(MessageRequest messageRequest)  throws RemoteException, IOException;
}
