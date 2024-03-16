package de.zeus.hermes.manager;

import com.ibm.as400.access.DataQueue;
import com.ibm.as400.access.DataQueueAttributes;
import com.ibm.as400.access.QSYSObjectPathName;
import de.zeus.hermes.util.Config;

import java.util.logging.Level;
import java.util.logging.Logger;

public class QueueManager {

    private static final Logger LOGGER = Logger.getLogger(QueueManager.class.getName());
    private final Config config = Config.getInstance();
    private static QueueManager instance;

    private QueueManager() {
        // No objects should be created from this class.
    }

    // Public method to obtain the singleton instance
    public static synchronized QueueManager getInstance() {
        if (instance == null) {
            instance = new QueueManager();
        }
        return instance;
    }

    public void manageDataQueue() {
        LOGGER.log(Level.INFO, "Managing Data Queue.");
        createOrOpenDataQueue(config.getDataQueueLibrary(), config.getJavaToRpgQueueName());
        createOrOpenDataQueue(config.getDataQueueLibrary(), config.getRpgToJavaQueueName());
        clearDataQueue(config.getDataQueueLibrary(), config.getJavaToRpgQueueName());
        clearDataQueue(config.getDataQueueLibrary(), config.getRpgToJavaQueueName());
    }

    public void createOrOpenDataQueue(String library, String queueName) {
        try {
            String path = QSYSObjectPathName.toPath(library, queueName, Config.DTAQ);
            DataQueue queue = new DataQueue(System400Manager.getInstance().getAs400(), path);

            if (!queue.exists()) {
                LOGGER.info(String.format("Data Queue [%s] does not exist," +
                        " creating new one.", queueName));
                DataQueueAttributes attributes = new DataQueueAttributes();
                queue.create(attributes);
                LOGGER.info(String.format("Data Queue [%s] created.", queueName));
            } else {
                LOGGER.info(String.format("Data Queue [%s] already exists.", queueName));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to create Data Queue [%s] .", queueName), e);
        }
    }

    public void clearDataQueue(String library, String queueName) {
        try {
            String path = QSYSObjectPathName.toPath(library, queueName, Config.DTAQ);
            DataQueue queue = new DataQueue(System400Manager.getInstance().getAs400(), path);

            if (queue.exists()) {
                queue.clear();
                LOGGER.info(String.format("Data Queue [%s] successfully cleared.", queueName));
            } else {
                LOGGER.warning(String.format("Data Queue [%s] does not exist, cannot clear.", queueName));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Failed to clear Data Queue [%s].", queueName), e);
        }
    }

}
