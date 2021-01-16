package Data;

public interface DataUtils {
    // Format to present the date on screen
    String DATE_STRING_FORMAT = "dd/MM/yyyy HH:mm:ss";

    // Format to save the data on the database
    String DATE_DATABASE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // Max size for the buffer to transfer the images
    int MAX_DG_PACKAGE_SIZE = 4000;

    // Path in the project which represents the local where the user images are stored
    String PATH_FOR_LOGIN_IMAGES = "\\SaveFiles\\ImagesToSend\\";

    // Path in the project which represents the local where the user files are stored
    String PATH_FOR_FILES = "\\SaveFiles\\ClientFiles\\";
}
