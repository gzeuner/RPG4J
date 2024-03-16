package de.zeus.hermes;

import de.zeus.hermes.service.DataQueueService;
import de.zeus.hermes.manager.System400Manager;
import com.ibm.as400.access.DataQueue;
import com.ibm.as400.access.DataQueueEntry;
import com.ibm.as400.access.QSYSObjectPathName;
import de.zeus.hermes.util.Config;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DataQueueServiceTest {
    private static final Logger LOGGER = Logger.getLogger(DataQueueServiceTest.class.getName());
    private final DataQueueService dataQueueService;
    private final Config config;
    private final System400Manager system400Manager;

    public DataQueueServiceTest() {
        this.config = Config.getInstance();
        this.system400Manager = System400Manager.getInstance();
        this.dataQueueService = new DataQueueService();
    }

    public void runTest() {
        // Define the test URL
        String testUrl = "https://official-joke-api.appspot.com/random_joke";

        // Write the test URL in the first queue
        writeToQueue(config.getDataQueueLibrary(), config.getRpgToJavaQueueName(), testUrl);

        // Execute the REST call and write the result to the second queue
        dataQueueService.processQueueAndCallREST();

        // Read the result from the second queue and log it
        String result = readFromQueue(config.getDataQueueLibrary(), config.getJavaToRpgQueueName());
        LOGGER.info("REST-Call result: " + result);
    }

    private void writeToQueue(String library, String queueName, String message) {
        try {
            String path = QSYSObjectPathName.toPath(library, queueName, Config.DTAQ);
            DataQueue queue = new DataQueue(system400Manager.getAs400(), path);
            queue.write(message.getBytes(StandardCharsets.UTF_8));
            LOGGER.info("Message successfully written to the queue.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while writing to the queue", e);
        }
    }

    private String readFromQueue(String library, String queueName) {
        try {
            String path = QSYSObjectPathName.toPath(library, queueName, Config.DTAQ);
            DataQueue queue = new DataQueue(system400Manager.getAs400(), path);
            DataQueueEntry entry = queue.read(-1);
            return new String(entry.getData(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error when reading from the queue", e);
            return "Error reading the answer.";
        }
    }
}
