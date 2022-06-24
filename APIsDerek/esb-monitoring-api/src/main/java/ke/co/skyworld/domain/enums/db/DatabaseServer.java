package ke.co.skyworld.domain.enums.db;

public enum DatabaseServer {

    MySQL("MySQL"),
    PostgresSQL("PostgresSQL"),
    MicrosoftSQL("MicrosoftSQL"),
    Oracle("Oracle");

    private String value;

    DatabaseServer(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }

    /**
     * Convert String to enum
     * @param value - String to be converted
     * @return DatabaseServer enum
     */
    public static DatabaseServer parse(String value) {
        for (DatabaseServer databaseServer : DatabaseServer.values()) {
            if (databaseServer.value().equalsIgnoreCase(value)) {
                return databaseServer;
            }
        }

        //Return MySQL as default
        return DatabaseServer.MySQL;
    }
}