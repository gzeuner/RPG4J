package de.zeus.hermes.service;

import de.zeus.hermes.util.Config;
import de.zeus.hermes.manager.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseService {
    private static final Logger LOGGER = Logger.getLogger(DatabaseService.class.getName());
    private DatabaseManager dbManager = new DatabaseManager();
    private Config config = Config.getInstance();

    public void performSampleDatabaseOperation() {
        Connection con = null;
        try {
            con = dbManager.getDatabaseConnection(config.getDriver(), config.getDatabaseUrl(), config.getUsername(), config.getPassword());
            String query = config.getQuery();
            PreparedStatement stmt = dbManager.getPreparedStmt(con, query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error when executing the database operation", e);
        } finally {
            dbManager.closeDatabaseConnection(con);
        }
    }
}
