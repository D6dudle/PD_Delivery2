package com.example.Server.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MessageData implements Serializable {
    private static final long serialVersionUID = 1004L;
    private String message;
    private String originName;
    private String channelTarget;
    private String usernameTarget;
    private LocalDateTime localDateTime;
    private boolean isPrivate;
    private boolean isFile;

    public MessageData(String message,
                       String originName,
                       String channelTarget,
                       String usernameTarget,
                       LocalDateTime localDateTime,
                       boolean isPrivate,
                       boolean isFile) {
        this.message = message;
        this.originName = originName;
        this.channelTarget = channelTarget;
        this.usernameTarget = usernameTarget;
        this.localDateTime = localDateTime;
        this.isPrivate = isPrivate;
        this.isFile = isFile;
    }

    public String getMessage() {
        return message;
    }

    public String getOriginName() {
        return originName;
    }

    public String getChannelTarget() {
        return channelTarget;
    }

    public String getUsernameTarget() {
        return usernameTarget;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageData {" +
                " message='" + message + '\'' +
                ", originName='" + originName + '\'' +
                ", channelTarget='" + channelTarget + '\'' +
                ", usernameTarget='" + usernameTarget + '\'' +
                ", localDateTime=" + localDateTime +
                ", isPrivate=" + isPrivate +
                ", isFile=" + isFile +
                '}';
    }
}
