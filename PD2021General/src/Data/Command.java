package Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Command implements Serializable {
    private static final long serialVersionUID = 1004L;
    private String command;
    private List<String> arguments;

    public Command() {
        this.command = "";
        this.arguments = new ArrayList<>();
    }

    public Command(String command, List<String> arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getArguments() {
        return arguments;
    }
}
