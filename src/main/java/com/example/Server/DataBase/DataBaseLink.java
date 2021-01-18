package com.example.Server.DataBase;

import Data.DataUtils;
import com.example.Server.Data.ChannelData;
import com.example.Server.Data.MessageData;
import com.example.Server.DataBase.Interfaces.DataBaseQueries;
import com.example.Server.DataBase.Interfaces.DataBaseUtils;
import com.example.Server.DataBase.Interfaces.InsertQueries;
import com.example.Server.DataBase.Interfaces.UpdateQueries;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static Data.UtilsFunctions.getMessageStrToDatabase;
import static Data.UtilsFunctions.getMessageTime;

public class DataBaseLink implements DataBaseUtils, DataBaseQueries, InsertQueries, UpdateQueries, DataUtils {

    private Connection dbConnection;

    //TODO: use statements 4 queries
    // http://tutorials.jenkov.com/jdbc/preparedstatement.html

    /**
     * Creates the database connection
     * @throws SQLException
     */
    public DataBaseLink() throws SQLException {
        dbConnection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }

    /**
     * Closes the database
     * @throws SQLException
     */
    public void close() throws SQLException {
        if (dbConnection != null)
            dbConnection.close();
    }

    /**
     * Inserts a user in the database
     * @param userName
     * @param userPassword
     * @param userPhotoPath
     * @param userIp
     * @throws SQLException
     */
    public void saveUser(String userName, String userPassword, String userPhotoPath, String userIp) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(INSERT_USER);
        ps.setString(1, userName);
        ps.setString(2, userPassword);
        ps.setString(3, userPhotoPath);
        ps.setString(4, userIp);

        ps.executeUpdate();
        ps.close();
    }

    public void updateUser(String userName, String userIp) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(UPDATE_USER_IP);
        ps.setString(1, userIp);
        ps.setLong(2, getUserIdByName(userName));

        ps.executeUpdate();
        ps.close();
    }

    /**
     * Checks if a user is already registered or not
     * @param userName
     * @return true if there is a registered user with that username, false if it's not
     * @throws SQLException
     */
    public boolean isRegistered(String userName) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_USER_WITH_SAME_NAME);
        ps.setString(1, userName);

        ResultSet rSet = ps.executeQuery();

        if (!rSet.next()){
            ps.close();
            return false;
        }
        ps.close();
        return true;
    }

    /**
     * Checks if a user inserted the correct credentials to login
     * @param userName
     * @param userPassword
     * @return true if there the credentials are corrected, false if they aren't
     * @throws SQLException
     */
    public boolean logInOperation(String userName, String userPassword) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_USER_LOGIN);
        ps.setString(1, userName);
        ps.setString(2, userPassword);

        ResultSet rSet = ps.executeQuery();

        if (!rSet.next()) {
            ps.close();
            return false;
        }
        ps.close();
        return true;
    }

    /**
     * Saves a new channel in the database
     * @param channelName
     * @param channelPassword
     * @param channelDescription
     * @param userName
     * @throws SQLException
     */
    public long saveChannel(String channelName, String channelPassword, String channelDescription, String userName) throws SQLException {
        final long userId = getUserIdByName(userName);

        PreparedStatement ps = dbConnection.prepareStatement(INSERT_CHANNEL);
        ps.setString(1, channelName);
        ps.setString(2, channelPassword);
        ps.setString(3, channelDescription);
        ps.setLong(4, userId);
        ps.setInt(5, 0);

        ps.executeUpdate();
        ps.close();

        return getChannelIdByName(channelName);
    }

    /**
     * Edits a channel (name and/or description)
     * @param oldChannelName
     * @param newChannelName
     * @param channelDescription
     * @throws SQLException
     */
    public void editChannelByOldName(String oldChannelName, String newChannelName, String channelDescription) throws SQLException {
        final long channelId = getChannelIdByName(oldChannelName);

        PreparedStatement ps;
        if (!channelDescription.equals("")) {
            ps = dbConnection.prepareStatement(UPDATE_CHANNEL_NAME_DESCRIPTION);
            ps.setString(1, newChannelName);
            ps.setString(2, channelDescription);
            ps.setLong(3, channelId);
        } else {
            ps = dbConnection.prepareStatement(UPDATE_CHANNEL_NAME);
            ps.setString(1, newChannelName);
            ps.setLong(2, channelId);
        }

        ps.executeUpdate();
        ps.close();
    }

    /**
     * Edits a channel (name and/or description)
     * @param channelId
     * @param newChannelName
     * @param channelDescription
     * @throws SQLException
     */
    public void editChannelById(Long channelId, String newChannelName, String channelDescription) throws SQLException {
        if(channelId == null || channelId == 0)
            return;
        PreparedStatement ps = dbConnection.prepareStatement(UPDATE_CHANNEL_NAME_DESCRIPTION);
        ps.setString(1, newChannelName);
        ps.setString(2, channelDescription);
        ps.setLong(3, channelId);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Sets a channel as deleted
     * @param channelName
     * @throws SQLException
     */
    public void deleteChannel(String channelName) throws SQLException {
        final long channelId = getChannelIdByName(channelName);
        PreparedStatement ps = dbConnection.prepareStatement(UPDATE_CHANNEL_DELETED);
        ps.setLong(1, channelId);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Gets all channels that are not deleted
     * @return a list of channels, if there aren't channels then the list will be empty
     * @throws SQLException
     */
    public List<ChannelData> getAllChannels() throws SQLException {
        List<ChannelData> channels = new ArrayList<>();

        PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_NON_DELETED_CHANNELS);
        ResultSet rSet = ps.executeQuery();

        while (rSet.next()) {
            channels.add(new ChannelData(
                    rSet.getInt(1),
                    rSet.getString(2),
                    rSet.getString(3),
                    rSet.getString(4),
                    getUserNameById(rSet.getLong(5))
            ));
        }
        ps.close();
        return channels;
    }

    /**
     * Gets a user id by his name
     * @param userId
     * @return user id
     * @throws SQLException
     */
    private String getUserNameById(Long userId) throws SQLException {
        if(userId == null || userId == 0)
            return null;

        PreparedStatement ps = dbConnection.prepareStatement(SELECT_USER_NAME_BY_ID);
        ps.setLong(1, userId);

        ResultSet rSet = ps.executeQuery();
        rSet.next();
        final String userName = rSet.getString(1);

        ps.close();
        return userName;
    }

    /**
     * Gets the user id by his name
     * @param userName
     * @return user name
     * @throws SQLException
     */
    private long getUserIdByName(String userName) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_USER_ID_BY_NAME);
        ps.setString(1, userName);

        ResultSet rSet = ps.executeQuery();
        rSet.next();
        final long userId = rSet.getInt(1);

        ps.close();
        return userId;
    }

    /**
     * Verifies if a client exists in the database
     * @return true if it exists, false if not
     */
    public boolean existsClient(String clientName) {
        try {
            getUserIdByName(clientName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Gets a channel name by its id
     * @param channelName
     * @return channel id
     * @throws SQLException
     */
    private long getChannelIdByName(String channelName) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_CHANNEL_ID_BY_NAME);
        ps.setString(1, channelName);

        ResultSet rSet = ps.executeQuery();
        rSet.next();
        final long channelId = rSet.getInt(1);

        ps.close();
        return channelId;
    }

    /**
     * Verifies if a channel exists in the database
     * @param channelId
     * @return true if it exists, false if not
     */
    public boolean existsChannel(long channelId) {
        try {
            getChannelNameById(channelId);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Gets a channel id by its name
     * @param channelId
     * @return channel id
     * @throws SQLException
     */
    private String getChannelNameById(Long channelId) throws SQLException {
        if(channelId == null || channelId == 0)
            return null;
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_CHANNEL_NAME_BY_ID);
        ps.setLong(1, channelId);

        ResultSet rSet = ps.executeQuery();
        rSet.next();
        final String channelName = rSet.getString(1);

        ps.close();
        return channelName;
    }

    /**
     * Saves a message in the database
     * @param message
     */
    public void saveMessage(MessageData message) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(INSERT_MESSAGE);
        ps.setString(1, message.getMessage());
        ps.setString(2, getMessageStrToDatabase(message.getLocalDateTime()));
        ps.setLong(3, getUserIdByName(message.getOriginName()));

        // If the message is not private saves the channel id
        if(!message.isPrivate()) {
            ps.setNull(4, Types.INTEGER);
            ps.setLong(5, getChannelIdByName(message.getChannelTarget()));
        } else { // If not saved the id from the target user
            ps.setLong(4, getUserIdByName(message.getUsernameTarget()));
            ps.setNull(5, Types.INTEGER);
        }
        ps.setInt(6, message.isFile() ? 1 : 0);
        ps.setInt(7, 1); // sent

        ps.executeUpdate();
        ps.close();
    }

    /**
     * Saves a rest message in the database
     * @param message
     */
    public void saveRestMessage(MessageData message) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(INSERT_MESSAGE);
        ps.setString(1, message.getMessage());
        ps.setString(2, getMessageStrToDatabase(message.getLocalDateTime()));
        ps.setLong(3, 0);
        ps.setNull(4, Types.INTEGER);
        ps.setNull(5, Types.INTEGER);
        ps.setInt(6, message.isFile() ? 1 : 0);
        ps.setInt(7, 0); // not sent

        ps.executeUpdate();
        ps.close();
    }

    /**
     * Verifies if a channel message exists in the database
     * @param message
     * @return true if it exists, false if not
     */
    public boolean existsChannelMessage(MessageData message){
        try {
            PreparedStatement ps = dbConnection.prepareStatement(SELECT_CHANNEL_MESSAGE);

            ps.setString(1, message.getMessage());
            ps.setLong(2, getUserIdByName(message.getOriginName()));
            ps.setLong(3,getChannelIdByName(message.getChannelTarget()));
            ps.setString(4, getMessageStrToDatabase(message.getLocalDateTime()));

            ps.executeUpdate();
            ps.close();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Verifies if a private message exists in the database
     * @param message
     * @return true if it exists, false if not
     */
    public boolean existsPrivateMessage(MessageData message) {
        try {
            PreparedStatement ps = dbConnection.prepareStatement(SELECT_PRIVATE_MESSAGE);

            ps.setString(1, message.getMessage());
            ps.setLong(2, getUserIdByName(message.getOriginName()));
            ps.setLong(3,getChannelIdByName(message.getUsernameTarget()));
            ps.setString(4, getMessageStrToDatabase(message.getLocalDateTime()));

            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Gets all the messages from a channel
     * @param channelName
     * @return list of messages
     * @throws SQLException
     */
    public List<MessageData> loadChannelMessages(String channelName) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_CHANNEL_MESSAGES);
        ps.setLong(1, getChannelIdByName(channelName));

        List<MessageData> messages = new ArrayList<>();
        ResultSet rSet = ps.executeQuery();
        while (rSet.next()) {
            messages.add(new MessageData(
                    rSet.getString(1),
                    getUserNameById(rSet.getLong(2)),
                    getChannelNameById(rSet.getLong(3)),
                    "",
                    LocalDateTime.parse(rSet.getString(4), DateTimeFormatter.ofPattern(DATE_DATABASE_FORMAT)),
                    false,
                    rSet.getInt(5) > 0
            ));
        }
        ps.close();
        return messages;
    }

    /**
     * Gets all the private messages between two users
     * @param userNameOrigin
     * @param userNameTarget
     * @return list of messages
     * @throws SQLException
     */
    public List<MessageData> loadPrivateMessages(String userNameOrigin, String userNameTarget) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_PRIVATE_MESSAGES);
        ps.setLong(1, getUserIdByName(userNameOrigin));
        ps.setLong(2, getUserIdByName(userNameTarget));

        List<MessageData> messages = new ArrayList<>();
        ResultSet rSet = ps.executeQuery();
        while (rSet.next()) {
            messages.add(new MessageData(
                    rSet.getString(1),
                    getUserNameById(rSet.getLong(2)),
                    "",
                    getUserNameById(rSet.getLong(3)),
                    LocalDateTime.parse(rSet.getString(4), DateTimeFormatter.ofPattern(DATE_DATABASE_FORMAT)),
                    true,
                    rSet.getInt(5) > 0
            ));
        }

        ps = dbConnection.prepareStatement(SELECT_ALL_PRIVATE_MESSAGES);
        ps.setLong(2, getUserIdByName(userNameOrigin));
        ps.setLong(1, getUserIdByName(userNameTarget));

        rSet = ps.executeQuery();
        while (rSet.next()) {
            messages.add(new MessageData(
                    rSet.getString(1),
                    getUserNameById(rSet.getLong(2)),
                    "",
                    getUserNameById(rSet.getLong(3)),
                    LocalDateTime.parse(rSet.getString(4), DateTimeFormatter.ofPattern(DATE_DATABASE_FORMAT)),
                    true,
                    rSet.getInt(5) > 0
            ));
        }

        ps.close();
        return messages;
    }

    /**
     * Gets a hashmap with all the user channels (name and description)
     * @param userName
     * @return a hashmap with channels information
     * @throws SQLException
     */
    public HashMap<String, String> getAllClientChannels(String userName) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_USER_CHANNELS_NAME_DESCRIPTION);
        ps.setLong(1, getUserIdByName(userName));

        HashMap<String, String> userChannels = new HashMap<>();
        ResultSet rSet = ps.executeQuery();

        while (rSet.next()) {
            userChannels.put(
                    rSet.getString(1),
                    rSet.getString(2)
            );
        }

        ps.close();
        return userChannels;
    }

    /**
     * Get all user names in the database
     * @return a list with all the usernames
     * @throws SQLException
     */
    public List<String> getAllClientNames() throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_USER_NAMES);

        List<String> userNames = new ArrayList<>();
        ResultSet rSet = ps.executeQuery();

        while (rSet.next()) {
            userNames.add(rSet.getString(1));
        }

        ps.close();
        return userNames;
    }

    /**
     * Gets all files available in a channel
     * @param channelName
     * @return a list with this file information
     * @throws SQLException
     */
    public List<String> getAllFilesInAChannel(String channelName) throws SQLException {
        List<String> fileInformation = new ArrayList<>();
        try {
            PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_FILES_IN_A_CHANNEL);
            ps.setLong(1, getChannelIdByName(channelName));

            ResultSet rSet = ps.executeQuery();

            while (rSet.next()) {
                fileInformation.add(rSet.getString(1) + " "  +
                        "sent by " + getUserNameById(rSet.getLong(3)) + " (" +
                        getMessageTime(LocalDateTime.parse(rSet.getString(2), DateTimeFormatter.ofPattern(DATE_DATABASE_FORMAT))) + ")"
                );
            }

            ps.close();
        } catch (SQLException e) {
            fileInformation.add("This channel does not have files!");
        }
        return fileInformation;
    }

    /**
     * Gets all channels in the database
     * @return a list with all channel information
     * @throws SQLException
     */
    public List<String> getAllChannelsInformation() throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_CHANNELS);

        List<String> channelsInformation = new ArrayList<>();
        ResultSet rSet = ps.executeQuery();

        while (rSet.next()) {
            channelsInformation.add(rSet.getString(1) + " ("  +
                    rSet.getString(2) + ") " +
                    (rSet.getInt(3) > 0 ? "deleted" : "not deleted") +
                    ", created by " + getUserNameById(rSet.getLong(4))
            );
        }

        ps.close();
        return channelsInformation;
    }

    /**
     * Gets channel information
     * @param channelName
     * @return a string with this channel information
     */
    public String getChannelsInformation(String channelName)  {
        StringBuilder channelInformation = new StringBuilder();
        try {
            PreparedStatement ps = dbConnection.prepareStatement(SELECT_CHANNEL_INFORMATION);
            ps.setLong(1, getChannelIdByName(channelName));

            ResultSet rSet = ps.executeQuery();

            while (rSet.next()) {
                channelInformation.append(rSet.getString(1))
                        .append(" (").append(rSet.getString(2)).append(") ")
                        .append(rSet.getInt(3) > 0 ? "deleted" : "not deleted")
                        .append(", created by ").append(getUserNameById(rSet.getLong(4)));
            }

            ps.close();
        } catch (SQLException e) {
            channelInformation.append("This channel does not exists!");
        }

        return channelInformation.toString();
    }

    /**
     * Get all channels statistics
     * @return list with all channels statistics
     * @throws SQLException
     */
    public List<String> getChannelsStatistics() throws SQLException {
        List<String> channelsStatistics = new ArrayList<>();
        List<String> channelsInformation = getAllChannelsInformation();

        // Number of channels in the database
        channelsStatistics.add("Number of channels: " + channelsInformation.size() + " \n");

        for(String str : channelsInformation) {
            String[] aux = str.split(" ");

            // Channel name: Number of users, number of messages and number of files
            channelsStatistics.add(
                    aux[0] + ": \n" +
                    "\tNumber of messages: " + loadChannelMessages(aux[0]).size() + " \n" +
                    "\tNumber of files: " + getAllFilesInAChannel(aux[0]).size() + " \n "
            );
        }

        return channelsStatistics;
    }

    /**
     * Get this channel statistics
     * @param channelName
     * @return a list with all the statistics for this channel
     */
    public List<String> getChannelStatistics(String channelName) {
        List<String> channelsStatistics = new ArrayList<>();
        try {
            List<String> channelsInformation = getAllChannelsInformation();

            for (String str : channelsInformation) {
                String[] aux = str.split(" ");

                if (aux[0].equals(channelName)) {
                    // Channel name: Number of users, number of messages and number of files
                    channelsStatistics.add(
                            aux[0] + ": \n" +
                                    "\tNumber of messages: " + loadChannelMessages(aux[0]).size() + " \n" +
                                    "\tNumber of files: " + getAllFilesInAChannel(aux[0]).size() + " \n "
                    );
                }
            }

            if (channelsStatistics.isEmpty()) {
                channelsStatistics.add("This channel does not exists!");
            }
        } catch (SQLException e) {
            channelsStatistics.add("This channel does not exists!");
        }
        return channelsStatistics;
    }

    /**
     *
     * @param nMessages
     * @return
     * @throws SQLException
     */
    public List<MessageData> getLastNMessages(int nMessages) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_MESSAGES);

        List<MessageData> messages = new ArrayList<>();
        ResultSet rSet = ps.executeQuery();
        while (rSet.next()) {
            messages.add(new MessageData(
                    rSet.getString(1),
                    getUserNameById(rSet.getLong(2)),
                    getChannelNameById(rSet.getLong(3)),
                    getUserNameById(rSet.getLong(4)),
                    LocalDateTime.parse(rSet.getString(5), DateTimeFormatter.ofPattern(DATE_DATABASE_FORMAT)),
                    false,
                    false
            ));
            if(messages.size() >= nMessages) break;
        }
        ps.close();
        return messages;
    }

    public List<MessageData> getUnsentMessages() throws SQLException {//UPDATE_MESSAGES_AS_SENT
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_NOT_SENT_MESSAGES);
        ResultSet rSet = ps.executeQuery();
        List<MessageData> messages = new ArrayList<>();

        while (rSet.next()) {
            messages.add(new MessageData(
                    rSet.getString(1),
                    "restApi",
                    null,
                    "global",
                    LocalDateTime.parse(rSet.getString(5), DateTimeFormatter.ofPattern(DATE_DATABASE_FORMAT)),
                    false,
                    false
            ));
        }

        return messages;
    }
    public void markMessageAsSent() throws SQLException {//UPDATE_MESSAGES_AS_SENT
        PreparedStatement ps = dbConnection.prepareStatement(UPDATE_MESSAGES_AS_SENT);
        ps.executeUpdate();
    }
}
