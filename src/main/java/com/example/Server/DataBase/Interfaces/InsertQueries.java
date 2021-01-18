package com.example.Server.DataBase.Interfaces;

import static com.example.Server.DataBase.Interfaces.DatabaseTablesInfo.*;

public interface InsertQueries {
    String INSERT_USER = "INSERT INTO " + USER_TABLE_NAME +
            " (" + USER_NAME + ", " +  USER_PASSWORD + ", " + USER_PHOTO_PATH + ", " + USER_IP + ")" +
            " VALUES (?, ?, ?, ?)";

    String INSERT_CHANNEL = "INSERT INTO " + CHANNEL_TABLE_NAME +
            " (" + CHANNEL_NAME + ", " + CHANNEL_PASSWORD + ", " + CHANNEL_DESCRIPTION +
            ", " + CHANNEL_USER_ID_OWNER + ", " + CHANNEL_IS_DELETED + ")" +
            " VALUES (?, ?, ?, ?, ?)";

    String INSERT_MESSAGE = "INSERT INTO " + MESSAGE_TABLE_NAME +
            " (" + MESSAGE_TEXT + ", " + MESSAGE_DATE_TIME + ", " + MESSAGE_USER_ID_ORIGIN +
            ", " + MESSAGE_USER_ID_DESTINY + ", " + CHANNEL_ID + ", " + MESSAGE_IS_FILE +  ", " + MESSAGE_IS_REST + ")" +
            " VALUES (?, ?, ?, ?, ?, ?, ?)";

}
