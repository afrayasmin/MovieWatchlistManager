# 🎬 Movie Watchlist Manager

A JavaFX desktop application for managing a personal movie watchlist — add, edit, delete, search, and filter movies, track watched/unwatched status, mark favorites, rate movies personally, and view collection statistics.

Built as an academic project to demonstrate JavaFX UI development, MVC architecture, relational SQLite database integration, and multithreaded concurrency.

## Features

- **Full CRUD** — add, view, edit, and delete movies
- **Search** — find movies by title
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
- **Background threading** — all database reads and writes run off the JavaFX Application Thread via `Task` and an `ExecutorService`, so the UI never freezes during a query or save

## Tech Stack

- **Java 17**
- **JavaFX 21** — UI framework (FXML + Scene Builder-compatible layouts)
- **Maven** — build and dependency management
- **SQLite** (via Xerial JDBC driver) — local persistent storage
- **CSS** — custom styling for a modern look
- **java.util.concurrent** — `ExecutorService` and `javafx.concurrent.Task` for background database operations

## Architecture

The project follows the **MVC (Model-View-Controller)** pattern:

com.movieapp
├── Main.java # Application entry point, scene switching
├── model/
│ ├── Movie.java # Movie data model (POJO)
│ └── Genre.java # Genre data model (POJO)
├── controller/
│ ├── DashboardController.java
│ ├── AddMovieController.java
│ ├── MovieListController.java
│ └── StatisticsController.java
├── dao/
│ ├── MovieDAO.java # Database access layer for movies (CRUD, joins on genres)
│ └── GenreDAO.java # Database access layer for genres
└── database/
├── DatabaseConnection.java # SQLite connection + schema setup/migrations
└── DatabaseActivityMonitor.java # Synchronized counter tracking in-flight DB operations across threads

FXML files live under `src/main/resources/com/movieapp/fxml/`, and styling is in `src/main/resources/com/movieapp/css/style.css`.

### Key design decisions

- **DAO pattern** separates all SQL logic from the UI layer — controllers never touch JDBC directly
- **`Main.switchScene()`** is a shared helper that loads FXML, applies the stylesheet, and returns the controller — enabling data passing between screens (e.g., populating the edit form with a selected movie's data)
- **Schema migrations** — new columns and tables (`favorite`, `my_rating`, `genres`, `genre_id`) are added via `ALTER TABLE` / `CREATE TABLE` calls guarded with error handling, so the app upgrades existing databases without data loss. Existing movies' free-text genre values are automatically migrated into the new `genre_id` foreign key on first run after the upgrade
- **Relational integrity** — `movies.genre_id` is a foreign key referencing `genres.id`, a genuine one-to-many relationship (one genre, many movies), with an index on `genre_id` since every movie query joins on it
- **Background threading** — every `MovieDAO` call from the controllers is wrapped in a `javafx.concurrent.Task` and submitted to a single-thread `ExecutorService`, keeping the UI responsive. `DatabaseActivityMonitor` demonstrates safe access to a shared mutable resource across threads via `synchronized` methods

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
| BorderPane, VBox, HBox, GridPane | Dashboard, forms, list screen |
| TableView | Movie List screen |
| PieChart | Statistics screen (Watch Status + Genre Breakdown) |
| SQLite + JDBC | `DatabaseConnection.java`, `MovieDAO.java`, `GenreDAO.java` |
| Relational database design (primary/foreign keys, joins) | `genres` ↔ `movies` via `genre_id`, `MovieDAO`'s `JOIN` queries |
| CRUD operations | `MovieDAO.java`, `GenreDAO.java` |
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
2. **Add/Edit Movie** — form with validation for adding or editing a movie; personal rating field is only enabled when status is Watched
3. **Movie List** — searchable, filterable table with edit/delete/toggle actions; toggling a movie to Watched prompts for a rating
4. **Statistics** — aggregate stats, a Watch Status PieChart, and a Genre Breakdown PieChart

---
Built by Afra Yasmin as an academic JavaFX project.
