package de.zeus.hermes.manager;

import com.ibm.as400.access.*;
import de.zeus.hermes.util.Config;

import java.util.logging.Level;
import java.util.logging.Logger;

public class System400Manager {

    private static final Logger LOGGER = Logger.getLogger(System400Manager.class.getName());
    private AS400 as400;
    private CommandCall commandCall;
    private final Config config = Config.getInstance();

    public AS400 getAs400() {
        if (this.as400 == null) {
            try {
                this.as400 = new AS400(
                        config.getAs400System(),
                        config.getAs400Username(),
                        config.getAs400Password());
                LOGGER.info("AS400 connection successfully initialized.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize AS400 connection.", e);
            }
        }
        return this.as400;
    }

    public void createOrOpenDataQueue(String library, String queueName) {
        try {
            String path = QSYSObjectPathName.toPath(library, queueName, "DTAQ");
            DataQueue queue = new DataQueue(getAs400(), path);

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

    public boolean runCommand(String cmd) {
        if (this.commandCall == null) {
            try {
                this.commandCall = new CommandCall(getAs400());
                LOGGER.info("CommandCall instance successfully created.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to create CommandCall instance.", e);
                return false;
            }
        }
        try {
            boolean result = this.commandCall.run(cmd);
            if (result) {
                LOGGER.info(String.format("Command executed successfully: %s", cmd));
                return true;
            } else {
                LOGGER.info(String.format("Command execution returned false: %s", cmd));
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Failed to execute command: %s", cmd), e);
        }
        return false;
    }


    public void closeAs400() {
        if (this.as400 != null && this.as400.isConnected()) {
            try {
                this.as400.disconnectAllServices();
                this.as400 = null;
                LOGGER.info("AS400 connection closed successfully.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to close AS400 connection.", e);
            }
        }
    }
}
