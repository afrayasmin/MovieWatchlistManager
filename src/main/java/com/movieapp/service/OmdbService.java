package com.movieapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieapp.model.OmdbMovieResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OmdbService {

    // Free-tier course project key. Not meant for production use.
    private static final String API_KEY = "26e54de0";
    private static final String BASE_URL = "https://www.omdbapi.com/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Builds the request URL, opens an HTTP connection to it, reads the
    // response body as text, then hands that text to Jackson to convert
    // into an OmdbMovieResult object.
    public OmdbMovieResult searchByTitle(String title) throws IOException {
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        String urlString = BASE_URL + "?t=" + encodedTitle + "&apikey=" + API_KEY;

        URL url = URI.create(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        try {
            int status = connection.getResponseCode();
            if (status != 200) {
                throw new IOException("OMDb API returned HTTP status " + status);
            }

            StringBuilder json = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
            }

            return objectMapper.readValue(json.toString(), OmdbMovieResult.class);

        } finally {
            connection.disconnect();
        }
    }
}