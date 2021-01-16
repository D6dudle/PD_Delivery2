package Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilsFunctions implements DataUtils {
    /**
     * Convert a LocalDateTime to a string with the format "dd/MM/yyyy HH:mm:ss"
     * @param localDateTime
     * @return a string with the date formatted
     */
    public static String getMessageTime(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_STRING_FORMAT));
    }

    /**
     * Convert a LocalDateTime to a string to save it in the database with the format "yyyy-MM-dd HH:mm:ss"
     * @param localDateTime
     * @return a string with the date formatted
     */
    public static String getMessageStrToDatabase(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_DATABASE_FORMAT));
    }

}
