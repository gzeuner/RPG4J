package de.zeus.hermes.manager;

import com.ibm.as400.access.CommandCall;

import java.util.logging.Level;
import java.util.logging.Logger;


public class CommandManager {

    private static final Logger LOGGER = Logger.getLogger(CommandManager.class.getName());
    private CommandCall commandCall;
    private static CommandManager instance;

    private CommandManager() {
        // No objects should be created from this class.
    }

    public static synchronized CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

        public boolean runCommand(String cmd) {
        if (this.commandCall == null) {
            try {
                this.commandCall = new CommandCall(System400Manager.getInstance().getAs400());
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
}
