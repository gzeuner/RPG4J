package de.zeus.hermes.service;

import de.zeus.hermes.manager.System400Manager;
import de.zeus.hermes.util.Config;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DataQueueService {

    private static final Logger LOGGER = Logger.getLogger(DataQueueService.class.getName());
    private final System400Manager system400Manager;
    private final Config config = Config.getInstance();

    public DataQueueService(System400Manager system400Manager) {
        this.system400Manager = system400Manager;
    }

    public void manageDataQueue() {
        LOGGER.log(Level.INFO, "Managing Data Queue.");
        system400Manager.getAs400();
        system400Manager.createOrOpenDataQueue(config.getDataQueueLibrary(), config.getJavaToRpgQueueName());
        system400Manager.createOrOpenDataQueue(config.getDataQueueLibrary(), config.getRpgToJavaQueueName());
        system400Manager.closeAs400();
    }
}
