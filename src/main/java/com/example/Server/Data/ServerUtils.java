package com.example.Server.Data;

public interface ServerUtils {
     // Multicast port
     int PORT_MULTICAST = 5432;

     // Max size for the buffer to transfer the images
     int MAX_DG_PACKAGE_SIZE = 4000;

     // Path in the project which represents the local where the user images will be stored
     String PATH_FOR_FILES = "\\SaveFiles\\ServersFileSave\\";

     // Constant used in the messages which refers to the Server
     String SERVER = "Server";
}
