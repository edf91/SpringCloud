package org.wxd.junit.demo.dbunit.infrastructure;

import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.tools.RunScript;
import org.junit.Before;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * dbunit测试
 */
public abstract class AbstractDBUnitTest {

    private static boolean initialized = false;


    @Before
    public final void importDataSet() throws Exception {
        DBEnvironment dbEnv = new DBEnvironment(DBType.H2);
        for (String each : getDataSetFiles()) {
            InputStream is = AbstractDBUnitTest.class.getClassLoader().getResourceAsStream(each);
            IDataSet dataSet = new FlatXmlDataSetBuilder().build(new InputStreamReader(is));
            IDatabaseTester databaseTester = new DBUnitDatabaseTester(dbEnv.getDriverClassName(), dbEnv.getURL(getDatabaseName(each)),
                    dbEnv.getUsername(), dbEnv.getPassword(), dbEnv.getSchema(getDatabaseName(each)));
            databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
            databaseTester.setDataSet(dataSet);
            databaseTester.onSetup();
        }
    }

    protected abstract List<String> getDataSetFiles();


    private String getDatabaseName(final String dataSetFile) {
        String fileName = new File(dataSetFile).getName();
        if (-1 == fileName.lastIndexOf(".")) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }





    /**
     * 创建表
     */
    static {
        createSchema(DBType.H2);
    }

    private static synchronized void createSchema(DBType dbType) {
        if (!initialized) {
            try {
                Connection conn = initialConnection("unit_test", dbType);
                RunScript.execute(conn, new InputStreamReader(AbstractDBUnitTest.class.getClassLoader().getResourceAsStream("schema.sql")));
                conn.close();
            } catch (final SQLException ex) {
                ex.printStackTrace();
            }
        }
        initialized = true;
    }

    private static Connection initialConnection(final String dbName, final DBType type) throws SQLException {
        return buildDataSource(dbName, type).getConnection();
    }

    private static BasicDataSource buildDataSource(final String dbName, final DBType type) {
        DBEnvironment dbEnv = new DBEnvironment(type);
        BasicDataSource result = new BasicDataSource();
        result.setDriverClassName(dbEnv.getDriverClassName());
        result.setUrl(dbEnv.getURL(dbName));
        result.setUsername(dbEnv.getUsername());
        result.setPassword(dbEnv.getPassword());
        result.setMaxActive(1000);
        if (DBType.Oracle == dbEnv.getDBType()) {
            result.setConnectionInitSqls(Collections.singleton("ALTER SESSION SET CURRENT_SCHEMA = " + dbName));
        }
        return result;
    }
}