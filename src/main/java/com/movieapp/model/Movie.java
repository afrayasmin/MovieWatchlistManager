package com.movieapp.model;

public class Movie {

    private int id;
    private String title;
    private String genre;
    private int releaseYear;
    private double rating;       // IMDb rating
    private double myRating;     // Personal rating
    private String dateAdded;
    private String status;
    private String notes;
    private boolean favorite;

    // Constructor for a NEW movie (no id yet)
    public Movie(String title, String genre, int releaseYear,
                 double rating, double myRating, String dateAdded, String status, String notes, boolean favorite) {
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.myRating = myRating;
        this.dateAdded = dateAdded;
        this.status = status;
        this.notes = notes;
        this.favorite = favorite;
    }

    // Constructor for a movie LOADED from the database (has an id)
    public Movie(int id, String title, String genre, int releaseYear,
                 double rating, double myRating, String dateAdded, String status, String notes, boolean favorite) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.myRating = myRating;
        this.dateAdded = dateAdded;
        this.status = status;
        this.notes = notes;
        this.favorite = favorite;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getReleaseYear() { return releaseYear; }
    public double getRating() { return rating; }
    public double getMyRating() { return myRating; }
    public String getDateAdded() { return dateAdded; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
    public boolean isFavorite() { return favorite; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    public void setRating(double rating) { this.rating = rating; }
    public void setMyRating(double myRating) { this.myRating = myRating; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }
    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public String getFavoriteDisplay() {
        return favorite ? "⭐" : "☆";
    }

    // Shows "-" instead of "0.0" when no personal rating has been set yet
    public String getMyRatingDisplay() {
        return myRating > 0 ? String.valueOf(myRating) : "-";
    }

    @Override
    public String toString() {
        return title + " (" + releaseYear + ") - " + status;
    }
}