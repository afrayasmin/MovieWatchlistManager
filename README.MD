# 🎬 Movie Watchlist Manager

A JavaFX desktop application for managing a personal movie watchlist — add, edit, delete, search, and filter movies, track watched/unwatched status, mark favorites, rate movies personally, and view collection statistics.

Built as an academic project to demonstrate JavaFX UI development, MVC architecture, relational SQLite database integration, multithreaded concurrency, and external API integration.

## Features

- **Full CRUD** — add, view, edit, and delete movies
- **Search** — find movies by title
- **Search Online (OMDb integration)** — look up a movie by title against the OMDb API from the Add/Edit form and auto-fill its details (genre, release year, IMDb rating) instead of typing them in by hand
- **Filtering** — by genre, watch status, and favorites
- **Watch status tracking** — mark movies as Watched / Unwatched
- **Personal rating tied to watch status** — the "My Rating" field is only available once a movie is marked Watched. Marking a movie Unwatched automatically clears its personal rating; toggling a movie to Watched from the list screen prompts for a rating on the spot
- **Favorites** — star your favorite movies and filter by them
- **Dual ratings** — track both IMDb rating and your own personal rating
- **Dashboard** — live stats (total, watched, unwatched) and a Recently Added preview
- **Statistics screen** — average ratings, most common genre, highest rated movie, a Watch Status PieChart, and a Genre Breakdown PieChart
- **Input validation** — with clear error messages for invalid titles, years, and ratings
- **Confirmation dialogs** — for destructive actions like deleting a movie
- **Persistent storage** — SQLite database, no external server required
- **Relational schema** — genres are stored in their own table and linked to movies via a foreign key, not as free-text strings
- **Background threading** — all database reads and writes, and OMDb network calls, run off the JavaFX Application Thread via `Task` and an `ExecutorService`, so the UI never freezes during a query, save, or online lookup
- **Loading feedback** — a `StackPane` overlay with a spinner appears over the movie table during background database operations, giving clear visual feedback instead of just a disabled view
- **Responsive layout** — the search field's width is explicitly bound to the window's width via `widthProperty()`, so it resizes proportionally instead of staying fixed

## Tech Stack

- **Java 17**
- **JavaFX 21** — UI framework (FXML + Scene Builder-compatible layouts)
- **Maven** — build and dependency management
- **SQLite** (via Xerial JDBC driver) — local persistent storage
- **CSS** — custom styling for a modern look
- **java.util.concurrent** — `ExecutorService` and `javafx.concurrent.Task` for background database and network operations
- **HTTP + JSON** — Java's built-in `HttpClient` and Jackson (`jackson-databind`) for calling the OMDb API and parsing its response

## Architecture

The project follows the **MVC (Model-View-Controller)** pattern:

com.movieapp
├── Main.java # Application entry point, scene switching
├── model/
│ ├── Movie.java # Movie data model (POJO)
│ ├── Genre.java # Genre data model (POJO)
│ └── OmdbMovieResult.java # Maps a parsed OMDb API JSON response
├── controller/
│ ├── DashboardController.java
│ ├── AddMovieController.java
│ ├── MovieListController.java
│ └── StatisticsController.java
├── dao/
│ ├── Crud.java # Generic CRUD contract (interface)
│ ├── BaseDAO.java # Shared connection + error-logging behavior (abstract class)
│ ├── MovieDAO.java # Database access layer for movies (CRUD, joins on genres)
│ └── GenreDAO.java # Database access layer for genres
├── service/
│ └── OmdbService.java # Calls the OMDb API over HTTP and parses the JSON response
└── database/
├── DatabaseConnection.java # SQLite connection + schema setup/migrations
└── DatabaseActivityMonitor.java # Synchronized counter tracking in-flight DB operations across threads

FXML files live under `src/main/resources/com/movieapp/fxml/`, and styling is in `src/main/resources/com/movieapp/css/style.css`.

### Key design decisions

- **DAO pattern** separates all SQL logic from the UI layer — controllers never touch JDBC directly
- **`Crud<T>` interface** — `MovieDAO` implements a generic `add` / `getAll` / `update` / `delete` contract, formalizing what a data-access class is expected to provide. `GenreDAO` intentionally does **not** implement it: it has no update/delete lifecycle (genres are only read or resolved/created on the fly from the movie form), so forcing the interface onto it would mean writing meaningless stub methods rather than a genuine contract
- **`BaseDAO` abstract class** — both `MovieDAO` and `GenreDAO` extend it to share the connection-acquisition and error-logging pattern every DAO method repeated, removing real duplication rather than adding an abstract class purely for its own sake
- **`Main.switchScene()`** is a shared helper that loads FXML, applies the stylesheet, and returns the controller — enabling data passing between screens (e.g., populating the edit form with a selected movie's data)
- **Schema migrations** — new columns and tables (`favorite`, `my_rating`, `genres`, `genre_id`) are added via `ALTER TABLE` / `CREATE TABLE` calls guarded with error handling, so the app upgrades existing databases without data loss. Existing movies' free-text genre values are automatically migrated into the new `genre_id` foreign key on first run after the upgrade
- **Relational integrity** — `movies.genre_id` is a foreign key referencing `genres.id`, a genuine one-to-many relationship (one genre, many movies), with an index on `genre_id` since every movie query joins on it
- **Background threading** — every `MovieDAO` call from the controllers, and every OMDb lookup, is wrapped in a `javafx.concurrent.Task` and submitted to a single-thread `ExecutorService`, keeping the UI responsive. `DatabaseActivityMonitor` demonstrates safe access to a shared mutable resource across threads via `synchronized` methods
- **On PasswordField** — the course's UI-variety criteria list `PasswordField` as an example control, but it has no natural place in a single-user movie watchlist app — there's no real authentication use case to build around. Rather than adding an artificial "app lock" feature just to include it, UI variety is instead demonstrated through `TextField`, `TextArea`, `ComboBox`, `CheckBox`, `TableView`, `PieChart`, `BorderPane`, and `StackPane` (used for the loading overlay), each serving an actual function in the app

## Database Schema

```sql
CREATE TABLE genres (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE movies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    genre_id INTEGER REFERENCES genres(id),  -- Foreign key, replaces the old free-text genre column
    release_year INTEGER,
    rating REAL,           -- IMDb rating
    my_rating REAL,        -- Personal rating (0 = not rated / not watched)
    date_added TEXT,
    status TEXT,           -- 'Watched' or 'Unwatched'
    notes TEXT,
    favorite INTEGER       -- 0 or 1
);

CREATE INDEX idx_movies_genre_id ON movies(genre_id);
```

## How to Run

### Prerequisites
- JDK 17 or higher
- No separate Maven installation needed — the project includes the Maven Wrapper
- An OMDb API key (free at https://www.omdbapi.com/apikey.aspx) if you want to use the "Search Online" feature

### Steps

1. Clone the repository:

git clone https://github.com/afrayasmin/MovieWatchlistManager.git
cd MovieWatchlistManager


2. Run the application:

.\mvnw.cmd clean javafx:run

(On Mac/Linux: `./mvnw clean javafx:run`)

3. The app will launch, and a local `movies.db` SQLite file will be created automatically in the project root on first run. Existing databases from earlier versions are automatically migrated to the new relational schema.

## Course Topics Demonstrated

| Topic | Where it appears |
|---|---|
| Stage / Scene / Scene Graph | `Main.java`, all FXML layouts |
| MVC Architecture | `model/`, `view` (FXML), `controller/` |
| FXML Loader | `Main.switchScene()` |
| Scene Builder-compatible layouts | All `.fxml` files |
| BorderPane, VBox, HBox, GridPane, StackPane | Dashboard, forms, list screen, loading overlay |
| TableView | Movie List screen |
| PieChart | Statistics screen (Watch Status + Genre Breakdown) |
| Interfaces | `Crud<T>`, implemented by `MovieDAO` |
| Abstract classes | `BaseDAO`, extended by `MovieDAO` and `GenreDAO` |
| Layout responsiveness (property bindings) | `searchField.prefWidthProperty()` bound to `rootPane.widthProperty()` in `MovieListController` |
| SQLite + JDBC | `DatabaseConnection.java`, `MovieDAO.java`, `GenreDAO.java` |
| Relational database design (primary/foreign keys, joins) | `genres` ↔ `movies` via `genre_id`, `MovieDAO`'s `JOIN` queries |
| CRUD operations | `MovieDAO.java`, `GenreDAO.java` |
| Networking (HTTP) | `OmdbService.java` — calls the OMDb REST API |
| JSON parsing | Jackson deserialization of the OMDb response into `OmdbMovieResult` |
| Input validation | `AddMovieController.java` |
| Alerts (Info / Warning / Confirmation) | Throughout controllers |
| CSS Styling | `style.css` |
| Event Handling | All `onAction` button handlers |
| Thread & Runnable | `javafx.concurrent.Task` submitted to `ExecutorService` |
| Thread lifecycle | `Task` success/failure callbacks, visible loading/saving states |
| Synchronization & shared resources | `DatabaseActivityMonitor`'s `synchronized` counter |
| Executors & concurrent task management | `ExecutorService` thread pools in `MovieListController` and `AddMovieController` |
| Git & version control | Feature-branch workflow (branch → commit → push → merge) used throughout development |

## Screens

1. **Dashboard** — overview stats, recent movies, navigation
2. **Add/Edit Movie** — form with validation for adding or editing a movie; "Search Online" pulls details from OMDb by title; personal rating field is only enabled when status is Watched
3. **Movie List** — searchable, filterable table with edit/delete/toggle actions; toggling a movie to Watched prompts for a rating; a loading overlay appears during background database operations
4. **Statistics** — aggregate stats, a Watch Status PieChart, and a Genre Breakdown PieChart

---
Built by Afra Yasmin as an academic JavaFX project.
