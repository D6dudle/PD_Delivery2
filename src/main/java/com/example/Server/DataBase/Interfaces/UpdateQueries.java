package com.example.Server.DataBase.Interfaces;


import static com.example.Server.DataBase.Interfaces.DatabaseTablesInfo.*;


public interface UpdateQueries {
    String UPDATE_CHANNEL_NAME_DESCRIPTION = "UPDATE " + CHANNEL_TABLE_NAME +
            " SET " + CHANNEL_NAME + " = ?, " + CHANNEL_DESCRIPTION + " = ?" +
            " WHERE " + CHANNEL_ID + " = ?";

    String UPDATE_CHANNEL_NAME = "UPDATE " + CHANNEL_TABLE_NAME +
            " SET " + CHANNEL_NAME + " = ?" +
            " WHERE " + CHANNEL_ID + " = ?";

    String UPDATE_CHANNEL_DELETED = "UPDATE " + CHANNEL_TABLE_NAME +
            " SET " + CHANNEL_IS_DELETED + " = 1" +
            " WHERE " + CHANNEL_ID + " = ?";

    String UPDATE_USER_IP = "UPDATE " + USER_TABLE_NAME +
            " SET " + USER_IP + " = ?" +
            " WHERE " + USER_ID + " = ?";
}
