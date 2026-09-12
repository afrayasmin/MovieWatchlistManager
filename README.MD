# 🎬 Movie Watchlist Manager

A JavaFX desktop application for managing a personal movie watchlist — add, edit, delete, search, and filter movies, track watched/unwatched status, mark favorites, rate movies personally, and view collection statistics.

Built as an academic project to demonstrate JavaFX UI development, MVC architecture, and SQLite database integration.

## Features

- **Full CRUD** — add, view, edit, and delete movies
- **Search** — find movies by title
- **Filtering** — by genre, watch status, and favorites
- **Watch status tracking** — mark movies as Watched / Unwatched
- **Favorites** — star your favorite movies and filter by them
- **Dual ratings** — track both IMDb rating and your own personal rating
- **Dashboard** — live stats (total, watched, unwatched) and a Recently Added preview
- **Statistics screen** — average ratings, most common genre, highest rated movie, and a PieChart of watch status
- **Input validation** — with clear error messages for invalid titles, years, and ratings
- **Confirmation dialogs** — for destructive actions like deleting a movie
- **Persistent storage** — SQLite database, no external server required

## Tech Stack

- **Java 17**
- **JavaFX 21** — UI framework (FXML + Scene Builder-compatible layouts)
- **Maven** — build and dependency management
- **SQLite** (via Xerial JDBC driver) — local persistent storage
- **CSS** — custom styling for a modern look

## Architecture

The project follows the **MVC (Model-View-Controller)** pattern:

```
com.movieapp
├── Main.java                  # Application entry point, scene switching
├── model/
│   └── Movie.java              # Movie data model (POJO)
├── controller/
│   ├── DashboardController.java
│   ├── AddMovieController.java
│   ├── MovieListController.java
│   └── StatisticsController.java
├── dao/
│   └── MovieDAO.java            # Database access layer (CRUD operations)
└── database/
    └── DatabaseConnection.java  # SQLite connection + schema setup/migrations
```
FXML files live under `src/main/resources/com/movieapp/fxml/`, and styling is in `src/main/resources/com/movieapp/css/style.css`.

### Key design decisions

- **DAO pattern** separates all SQL logic from the UI layer — controllers never touch JDBC directly
- **`Main.switchScene()`** is a shared helper that loads FXML, applies the stylesheet, and returns the controller — enabling data passing between screens (e.g., populating the edit form with a selected movie's data)
- **Schema migrations** — new columns (`favorite`, `my_rating`) are added via `ALTER TABLE ... ADD COLUMN` calls guarded with error handling, so the app upgrades existing databases without data loss

## Database Schema

```sql
CREATE TABLE movies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    genre TEXT,
    release_year INTEGER,
    rating REAL,           -- IMDb rating
    my_rating REAL,        -- Personal rating
    date_added TEXT,
    status TEXT,           -- 'Watched' or 'Unwatched'
    notes TEXT,
    favorite INTEGER       -- 0 or 1
);
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

3. The app will launch, and a local `movies.db` SQLite file will be created automatically in the project root on first run.

## Course Topics Demonstrated

| Topic | Where it appears |
|---|---|
| Stage / Scene / Scene Graph | `Main.java`, all FXML layouts |
| MVC Architecture | `model/`, `view` (FXML), `controller/` |
| FXML Loader | `Main.switchScene()` |
| Scene Builder-compatible layouts | All `.fxml` files |
| BorderPane, VBox, HBox, GridPane | Dashboard, forms, list screen |
| TableView | Movie List screen |
| PieChart | Statistics screen |
| SQLite + JDBC | `DatabaseConnection.java`, `MovieDAO.java` |
| CRUD operations | `MovieDAO.java` |
| Input validation | `AddMovieController.java` |
| Alerts (Info / Warning / Confirmation) | Throughout controllers |
| CSS Styling | `style.css` |
| Event Handling | All `onAction` button handlers |

## Screens

1. **Dashboard** — overview stats, recent movies, navigation
2. **Add/Edit Movie** — form with validation for adding or editing a movie
3. **Movie List** — searchable, filterable table with edit/delete/toggle actions
4. **Statistics** — aggregate stats and a watch-status PieChart

---
Built by Afra Yasmin as an academic JavaFX project.