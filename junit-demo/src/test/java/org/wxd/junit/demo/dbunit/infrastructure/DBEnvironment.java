package org.wxd.junit.demo.dbunit.infrastructure;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class DBEnvironment {
    private static final int INIT_CAPACITY = 5;

    private static final Map<DBType, String> DRIVER_CLASS_NAME = new HashMap<>(INIT_CAPACITY);

    private static final Map<DBType, String> URL = new HashMap<>(INIT_CAPACITY);

    private static final Map<DBType, String> USERNAME = new HashMap<>(INIT_CAPACITY);

    private static final Map<DBType, String> PASSWORD = new HashMap<>(INIT_CAPACITY);

    private static final Map<DBType, String> SCHEMA = new HashMap<>(INIT_CAPACITY);

    @Getter
    private final DBType DBType;

    public DBEnvironment(final DBType DBType) {
        this.DBType = DBType;
        fillData();
    }

    private void fillData() {
        DRIVER_CLASS_NAME.put(DBType.H2, "org.h2.Driver");
        URL.put(DBType.H2, "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL");
        USERNAME.put(DBType.H2, "sa");
        PASSWORD.put(DBType.H2, "");
        SCHEMA.put(DBType.H2, null);

        DRIVER_CLASS_NAME.put(DBType.MySQL, "com.mysql.jdbc.Driver");
        URL.put(DBType.MySQL, "jdbc:mysql://localhost:3306/%s");
        USERNAME.put(DBType.MySQL, "root");
        PASSWORD.put(DBType.MySQL, "root");
        SCHEMA.put(DBType.MySQL, null);

        DRIVER_CLASS_NAME.put(DBType.PostgreSQL, "org.postgresql.Driver");
        URL.put(DBType.PostgreSQL, "jdbc:postgresql://localhost:5432/%s");
        USERNAME.put(DBType.PostgreSQL, "postgres");
        PASSWORD.put(DBType.PostgreSQL, "");
        SCHEMA.put(DBType.PostgreSQL, null);

        DRIVER_CLASS_NAME.put(DBType.SQLServer, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        URL.put(DBType.SQLServer, "jdbc:sqlserver://db.mssql:1433;DatabaseName=%s");
        USERNAME.put(DBType.SQLServer, "sa");
        PASSWORD.put(DBType.SQLServer, "sa");
        SCHEMA.put(DBType.SQLServer, null);

        DRIVER_CLASS_NAME.put(DBType.Oracle, "oracle.jdbc.driver.OracleDriver");
        URL.put(DBType.Oracle, "jdbc:oracle:thin:@db.oracle:8521:db_1");
        USERNAME.put(DBType.Oracle, "oracle");
        PASSWORD.put(DBType.Oracle, "oracle");
        SCHEMA.put(DBType.Oracle, "%s");

    }

    public String getDriverClassName() {
        return DRIVER_CLASS_NAME.get(DBType);
    }

    public String getURL(final String dbName) {
        return String.format(URL.get(DBType), dbName);
    }

    public String getUsername() {
        return USERNAME.get(DBType);
    }

    public String getPassword() {
        return PASSWORD.get(DBType);
    }

    public String getSchema(final String dbName) {
        return null == SCHEMA.get(DBType) ? null : String.format(SCHEMA.get(DBType), dbName);
    }
}
