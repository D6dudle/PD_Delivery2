package com.example.Server.DataBase.Interfaces;

import static com.example.Server.DataBase.Interfaces.DatabaseTablesInfo.*;

public interface DataBaseQueries {
    String SELECT_USER_WITH_SAME_NAME = "SELECT " + USER_NAME +
            " FROM " + USER_TABLE_NAME +
            " WHERE " + USER_NAME + " = ?";

    String SELECT_USER_LOGIN = "SELECT " + USER_NAME +
            ", " + USER_PASSWORD +
            " FROM " + USER_TABLE_NAME + "" +
            " WHERE " + USER_NAME + " = ? AND " + USER_PASSWORD + " = ?";

    String SELECT_USER_NAME_BY_ID = "SELECT " + USER_NAME + " FROM " + USER_TABLE_NAME + " WHERE " + USER_ID + " = ?";

    String SELECT_USER_ID_BY_NAME = "SELECT " + USER_ID + " FROM " + USER_TABLE_NAME + " WHERE " + USER_NAME + " = ?";

    String SELECT_ALL_NON_DELETED_CHANNELS = "SELECT " + CHANNEL_ID +
            ", " + CHANNEL_NAME +
            ", " + CHANNEL_PASSWORD +
            ", " + CHANNEL_DESCRIPTION +
            ", " + CHANNEL_USER_ID_OWNER +
            " FROM " + CHANNEL_TABLE_NAME +
            " WHERE " + CHANNEL_IS_DELETED + " = 0";

    String SELECT_CHANNEL_ID_BY_NAME = "SELECT " + CHANNEL_ID + " FROM " + CHANNEL_TABLE_NAME + " WHERE " + CHANNEL_NAME + " = ?";

    String SELECT_CHANNEL_NAME_BY_ID = "SELECT " + CHANNEL_NAME + " FROM " + CHANNEL_TABLE_NAME + " WHERE " + CHANNEL_ID + " = ?";

    String SELECT_ALL_CHANNEL_MESSAGES = "SELECT " + MESSAGE_TEXT +
            ", " + MESSAGE_USER_ID_ORIGIN +
            ", " + CHANNEL_ID +
            ", "  + MESSAGE_DATE_TIME +
            ", " + MESSAGE_IS_FILE +
            " FROM " + MESSAGE_TABLE_NAME +
            " WHERE " + CHANNEL_ID + " = ?";

    String SELECT_ALL_MESSAGES = "SELECT " + MESSAGE_TEXT +
            ", " + MESSAGE_USER_ID_ORIGIN +
            ", " + CHANNEL_ID +
            ", " + MESSAGE_USER_ID_DESTINY +
            ", "  + MESSAGE_DATE_TIME +
            " FROM " + MESSAGE_TABLE_NAME +
            " WHERE " + MESSAGE_IS_FILE + " = 0"+
            " ORDER BY " + MESSAGE_DATE_TIME + " DESC";

    String SELECT_ALL_PRIVATE_MESSAGES = "SELECT " + MESSAGE_TEXT +
            ", " + MESSAGE_USER_ID_ORIGIN +
            ", " + MESSAGE_USER_ID_DESTINY +
            ", " + MESSAGE_DATE_TIME +
            ", " + MESSAGE_IS_FILE +
            " FROM " + MESSAGE_TABLE_NAME +
            " WHERE " + MESSAGE_USER_ID_ORIGIN + " = ? AND " + MESSAGE_USER_ID_DESTINY + " = ?" +
            " ORDER BY " + MESSAGE_DATE_TIME + " ASC";

    String SELECT_PRIVATE_MESSAGE = "SELECT " + MESSAGE_TEXT +
            ", " + MESSAGE_USER_ID_ORIGIN +
            ", " + MESSAGE_USER_ID_DESTINY +
            ", " + MESSAGE_DATE_TIME +
            " FROM " + MESSAGE_TABLE_NAME +
            " WHERE " + MESSAGE_TEXT + " = ? AND " +
            MESSAGE_USER_ID_ORIGIN + " = ? AND " +
            MESSAGE_USER_ID_DESTINY + " = ? AND " +
            MESSAGE_DATE_TIME + " = ?" +
            " ORDER BY " + MESSAGE_DATE_TIME + " ASC";

    String SELECT_CHANNEL_MESSAGE = "SELECT " + MESSAGE_TEXT +
            ", " + MESSAGE_USER_ID_ORIGIN +
            ", " + CHANNEL_ID +
            ", " + MESSAGE_DATE_TIME +
            " FROM " + MESSAGE_TABLE_NAME +
            " WHERE " + MESSAGE_TEXT + " = ? AND " +
            MESSAGE_USER_ID_ORIGIN + " = ? AND " +
            CHANNEL_ID + " = ? AND " +
            MESSAGE_DATE_TIME + " = ?";

    String SELECT_ALL_USER_CHANNELS_NAME_DESCRIPTION = "SELECT " + CHANNEL_NAME + ", " + CHANNEL_DESCRIPTION +
            " FROM " + CHANNEL_TABLE_NAME +
            " WHERE " + CHANNEL_USER_ID_OWNER + " = ?";

    String SELECT_ALL_USER_NAMES = "SELECT " + USER_NAME + " FROM " + USER_TABLE_NAME;

    String SELECT_ALL_FILES_IN_A_CHANNEL = "SELECT " + MESSAGE_TEXT +
            ", " + MESSAGE_DATE_TIME +
            ", " + MESSAGE_USER_ID_ORIGIN +
            " FROM " + MESSAGE_TABLE_NAME +
            " WHERE " + MESSAGE_IS_FILE + " = 1 AND " + CHANNEL_ID + " = ?";

    String SELECT_ALL_CHANNELS = "SELECT " + CHANNEL_NAME +
            ", " + CHANNEL_DESCRIPTION +
            ", " + CHANNEL_IS_DELETED +
            ", " + CHANNEL_USER_ID_OWNER +
            " FROM " + CHANNEL_TABLE_NAME;

    String SELECT_CHANNEL_INFORMATION = "SELECT " + CHANNEL_NAME +
            ", " + CHANNEL_DESCRIPTION +
            ", " + CHANNEL_IS_DELETED +
            ", " + CHANNEL_USER_ID_OWNER +
            " FROM " + CHANNEL_TABLE_NAME +
            " WHERE " + CHANNEL_ID + " = ?";

    String SELECT_NOT_SENT_MESSAGES = "SELECT " + MESSAGE_TEXT +
            ", " + MESSAGE_USER_ID_ORIGIN +
            ", " + CHANNEL_ID +
            ", " + MESSAGE_USER_ID_DESTINY +
            ", "  + MESSAGE_DATE_TIME +
            " FROM " + MESSAGE_TABLE_NAME +
            " WHERE " + MESSAGE_IS_REST + " = 0 AND " + MESSAGE_USER_ID_ORIGIN + " = 0";
}
