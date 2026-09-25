package com.movieapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Maps the JSON fields OMDb returns (they're capitalized, e.g. "Title", "Year")
// onto normal Java field names via @JsonProperty. @JsonIgnoreProperties means
// any JSON field we don't care about (like "Poster", "Actors") is silently
// skipped instead of causing an error.
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmdbMovieResult {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Year")
    private String year;

    @JsonProperty("Genre")
    private String genre;

    @JsonProperty("imdbRating")
    private String imdbRating;

    @JsonProperty("Plot")
    private String plot;

    @JsonProperty("Response")
    private String response;

    @JsonProperty("Error")
    private String error;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getImdbRating() { return imdbRating; }
    public void setImdbRating(String imdbRating) { this.imdbRating = imdbRating; }

    public String getPlot() { return plot; }
    public void setPlot(String plot) { this.plot = plot; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    // OMDb returns "Response": "True" or "Response": "False" (as a string,
    // not a boolean) to indicate whether the search actually found a movie.
    public boolean isSuccess() {
        return "True".equalsIgnoreCase(response);
    }
}