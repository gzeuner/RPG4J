package de.zeus.hermes.service;

import de.zeus.hermes.controller.DatabaseController;
import java.util.logging.Logger;

public class DatabaseService {
    private static final Logger LOGGER = Logger.getLogger(DatabaseService.class.getName());
    DatabaseController databaseController = new DatabaseController();

    public void sqlToXml() {
        databaseController.runSqlToXmlProcess();
    }

}
