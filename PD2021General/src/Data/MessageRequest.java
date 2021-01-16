package Data;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MessageRequest implements Serializable {
    private static final long serialVersionUID = 1003L;

    private String message; // ou diretoria do ficheiro
    private String nameOrigin;
    private String usernameTarget; //mensagens privadas
    private LocalDateTime localDateTime;
    private boolean isSuccess;
    private Command cmd;
    private String ipToRedirect;
    private int portToRedirect;
    //TODO: pode ser necessário acrescentar campos para o servidor devolver estatísticas

    public MessageRequest(String message, String nameOrigin, LocalDateTime localDateTime, String usernameTarget, boolean isSuccess, Command cmd) {
        this.message = message;
        this.nameOrigin = nameOrigin;
        this.localDateTime = localDateTime;
        this.usernameTarget = usernameTarget;
        this.isSuccess = isSuccess;
        this.cmd = cmd;
    }

    public MessageRequest(String message,
                          String nameOrigin,
                          LocalDateTime localDateTime,
                          String usernameTarget,
                          boolean isSuccess,
                          Command cmd,
                          String ipToRedirect,
                          int portToRedirect) {
        this.message = message;
        this.nameOrigin = nameOrigin;
        this.usernameTarget = usernameTarget;
        this.localDateTime = localDateTime;
        this.isSuccess = isSuccess;
        this.cmd = cmd;
        this.ipToRedirect = ipToRedirect;
        this.portToRedirect = portToRedirect;
    }

    public String getMessage() {
        return message;
    }

    public String getNameOrigin() {
        return nameOrigin;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public String getUsernameTarget() {
        return usernameTarget;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public Command getCmd() {
        return cmd;
    }

    public String getIpToRedirect() {
        return ipToRedirect;
    }

    public int getPortToRedirect() {
        return portToRedirect;
    }

    @Override
    public String toString() {
        return "MessageRequest {" +
                " message='" + message + '\'' +
                ", nameOrigin='" + nameOrigin + '\'' +
                ", usernameTarget='" + usernameTarget + '\'' +
                ", localDateTime=" + localDateTime +
                ", isSuccess=" + isSuccess +
                ", cmd=" + cmd +
                '}';
    }
}
