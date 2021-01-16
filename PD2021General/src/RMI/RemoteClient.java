package RMI;

import java.rmi.Remote;

public interface RemoteClient extends Remote {
    String BIND_NAME_CLIENT = "clientBindService";
}
