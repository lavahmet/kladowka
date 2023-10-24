package ku.epta.service.enums;

public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION9("/registration"),
    CANCEL("/cancel"),
    START("/start");
    private String value;

    ServiceCommands(String cmd){
        this.value = cmd;
    }

    public static ServiceCommands fromValue(String text) {
        for (ServiceCommands c : ServiceCommands.values()) {
            if (c.value.equals(text)) {
                return c;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean equals(String cmd) {
        return toString().equals(cmd);
    }
}
