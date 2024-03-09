package de.zeus.hermes.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericRestClient {

    private static final Logger LOGGER = Logger.getLogger(GenericRestClient.class.getName());

    /**
     * Executes a GET request to the specified URL and returns the response as a String.
     * This method is designed to be generic, allowing for various types of RESTful requests
     * by simply changing the URL and request properties as needed. It is particularly useful
     * for APIs that return JSON responses, but can be used with any service that responds
     * to GET requests.
     *
     * @param urlString The complete URL of the RESTful API endpoint, including any necessary query parameters.
     * @return A String containing the response from the server, typically in JSON format, or null in case of failure.
     */
    public String fetchResponseAsString(String urlString) {
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            return response.toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch response from URL", e);
            return null;
        }
    }
}
