package ru.velesov7493.products.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.Properties;

public class DbUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DbUtils.class.getName());
    private static final DbUtils INSTANCE = new DbUtils();

    private final Properties cfg;

    private DbUtils() {
        cfg = loadProperties();
        initDatabase();
    }

    public static DbUtils getInstance() {
        return INSTANCE;
    }

    private boolean processScript(String sqlResourceName) {
        boolean result = false;
        try (
                BufferedReader br =
                    new BufferedReader(
                        new InputStreamReader(
                                DbUtils.class
                                .getClassLoader()
                                .getResourceAsStream(sqlResourceName)
                        )
                    )
        ) {
            Connection cn = getConnection();
            Statement s = cn.createStatement();
            while (br.ready()) {
                String line = br.readLine();
                s.executeUpdate(line);
            }
            s.close();
            cn.close();
            result = true;
        } catch (IOException ex) {
            LOG.error("Ошибка - невозможно прочитать sql-скрипт: ", ex);
            LOG.info("Выключаюсь...");
            System.exit(2);
        } catch (SQLException ex) {
            LOG.error("Ошибка при выполнении sql-скрипта: ", ex);
            LOG.info("Выключаюсь...");
            System.exit(2);
    }
        return result;
    }

    private void initDatabase() {
        Properties cfg = loadProperties();
        String script = cfg.getProperty("jdbc.init-script");
        if (script != null) {
            processScript(script);
        }
        script = cfg.getProperty("jdbc.data-script");
        if (script != null) {
            processScript(script);
        }
    }

    private Properties loadProperties() {
        Properties result = new Properties();
        try (
                InputStream in = new BufferedInputStream(
                    DbUtils.class
                    .getClassLoader()
                    .getResourceAsStream("application.properties")
                )
        ) {
            result.load(in);
        } catch (IOException ex) {
            LOG.error("Ошибка - невозможно прочитать свойства подключения к БД: ", ex);
            LOG.info("Выключаюсь...");
            System.exit(2);
        }
        return result;
    }

    public synchronized Connection getConnection() {
        Connection result = null;
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            result = DriverManager.getConnection(
                    cfg.getProperty("jdbc.url"),
                    cfg.getProperty("jdbc.user", ""),
                    cfg.getProperty("jdbc.password", "")
            );
            result.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (ClassNotFoundException | SQLException ex) {
            throw new IllegalStateException(ex);
        }
        return result;
    }
}