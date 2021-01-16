package Data;

public interface Commands {
    // Command to exit a channel
    String EXIT_CMD = "!exit";

    // Command to send a file to a channel
    String SEND_FILE_CMD = "!sendFile";

    // Command to get a file from a channel
    String GET_FILE_CMD = "!getFile";

    // Command to join an existent channel
    String JOIN_CHANNEL_CMD = "joinChannel";

    // Command to create a channel
    String CREATE_CHANNEL_CMD = "createChannel";

    // Command to edit a channel
    String EDIT_CHANNEL_CMD = "editChannel";

    // Command to delete a channel
    String DELETE_CHANNEL_CMD = "deleteChannel";

    // Command to list the private messages between two users
    String LIST_PRIVATE_MSG_CMD = "!listPrivates";

    // Command to list N messages from a channel
    String LIST_MESSAGES_CMD = "!listMsgs";

    // Command to list all files in a channel
    String LIST_FILES_CMD = "!listFiles";

    // Command to list all user channels
    String LIST_USER_CHANNELS_CMD = "myChannels";

    // Command to list all non deleted channels
    String LIST_ALL_CHANNELS_CMD = "allChannels";
}