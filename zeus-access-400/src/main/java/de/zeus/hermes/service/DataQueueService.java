package de.zeus.hermes.service;

import com.ibm.as400.access.DataQueue;
import com.ibm.as400.access.DataQueueEntry;
import com.ibm.as400.access.QSYSObjectPathName;
import de.zeus.hermes.manager.QueueManager;
import de.zeus.hermes.manager.System400Manager;
import de.zeus.hermes.util.Config;
import de.zeus.hermes.util.GenericRestClient;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataQueueService {

    private static final Logger LOGGER = Logger.getLogger(DataQueueService.class.getName());
    private final QueueManager queueManager = QueueManager.getInstance();
    private final Config config = Config.getInstance();


    public DataQueueService() {
        this.queueManager.manageDataQueue();;
    }

    // Read Queue and perform Call
    public void processQueueAndCallREST() {
        String library = config.getDataQueueLibrary();
        String queueName = config.getRpgToJavaQueueName();
        try {
            // Read Message from queue
            String path = QSYSObjectPathName.toPath(library, queueName, Config.DTAQ);
            DataQueue queue = new DataQueue(System400Manager.getInstance().getAs400(), path);
            DataQueueEntry entry = queue.read(-1);
            String message = new String(entry.getData(), StandardCharsets.UTF_8);

            // Perform REST-Call
            String response = performRestCall(message);

            // Send Answer to second queue
            String responseQueueName = config.getJavaToRpgQueueName();
            path = QSYSObjectPathName.toPath(library, responseQueueName, Config.DTAQ);
            DataQueue responseQueue = new DataQueue(System400Manager.getInstance().getAs400(), path);
            responseQueue.write(response.getBytes(StandardCharsets.UTF_8));

            LOGGER.info("REST call response successfully sent back to RPG program.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing queue and calling REST", e);
        }
    }

    private String performRestCall(String message) {

        GenericRestClient restClient = new GenericRestClient();
        String responseData = restClient.fetchResponseAsString(message);

        if (responseData != null) {
            LOGGER.log(Level.INFO, "Received data from REST call: {0}", responseData);
            return responseData;
        } else {
            LOGGER.log(Level.SEVERE, "Failed to fetch data from REST call.");
            return "Failed to fetch data.";
        }
    }

}
