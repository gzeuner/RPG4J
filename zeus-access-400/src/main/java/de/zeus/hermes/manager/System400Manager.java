package de.zeus.hermes.manager;

import com.ibm.as400.access.*;
import de.zeus.hermes.util.Config;

import java.util.logging.Level;
import java.util.logging.Logger;

public class System400Manager {

    private static final Logger LOGGER = Logger.getLogger(System400Manager.class.getName());
    private static System400Manager instance;
    private final Config config = Config.getInstance();
    private AS400 as400;

    private System400Manager() {
        // No objects should be created from this class.
    }

    public static synchronized System400Manager getInstance() {
        if (instance == null) {
            instance = new System400Manager();
        }
        return instance;
    }
    public AS400 getAs400() {
        if (as400 == null) {
            try {
                as400 = new AS400(
                        config.getAs400System(),
                        config.getAs400Username(),
                        config.getAs400Password());
                LOGGER.info("AS400 connection successfully initialized.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize AS400 connection.", e);
            }
        }
        return as400;
    }

    public void closeAs400() {
        if (as400 != null && this.as400.isConnected()) {
            try {
                as400.disconnectAllServices();
                as400 = null;
                LOGGER.info("AS400 connection closed successfully.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to close AS400 connection.", e);
            }
        }
    }
}
