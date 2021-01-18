package com.example.Server.DataBase.Interfaces;

public interface DatabaseTablesInfo {
    // Table names
    String CHANNEL_TABLE_NAME = "pd_channel";
    String MESSAGE_TABLE_NAME = "pd_message";
    String USER_TABLE_NAME = "pd_user";

    // Column names
    String CHANNEL_ID = "channelId";
    String CHANNEL_DESCRIPTION = "channelDescription";
    String CHANNEL_NAME = "channelName";
    String CHANNEL_PASSWORD = "channelPassword";
    String CHANNEL_IS_DELETED = "isDeleted";
    String CHANNEL_USER_ID_OWNER = "userIdOwner";

    String MESSAGE_ID = "messageId";
    String MESSAGE_IS_FILE = "isFile";
    String MESSAGE_DATE_TIME = "messageDateTime";
    String MESSAGE_TEXT = "messageText";
    String MESSAGE_USER_ID_DESTINY = "userIdDestiny";
    String MESSAGE_USER_ID_ORIGIN = "userIdOrigin";
    String MESSAGE_IS_REST = "isRestMsg";

    String USER_ID = "userId";
    String USER_IP = "userIp";
    String USER_NAME = "userName";
    String USER_PASSWORD = "userPassword";
    String USER_PHOTO_PATH = "userPhotoPath";
}
