# 🐾 ShelterManager

ShelterManager is a desktop application developed in Java for managing an animal shelter. The application allows users to manage animals and shelter sectors, monitor animal health conditions, track available space, and store shelter data in JSON files.

The project was developed as part of the *Graphical User Interface Programming* course.

## Features

### Animal Management
- Add, edit, and remove animals.
- Automatically assign unique animal IDs.
- Move animals between shelter sectors.
- Store animal information, including name, species, breed, age, weight, and health condition.
- Search animals by name or species.
- Sort animals by name, age, or weight.
- Filter animals by health condition.

### Shelter Sector Management
- Add, edit, and remove shelter sectors.
- Define and modify sector capacity.
- Display animals assigned to each sector.
- Monitor sector occupancy and available space.
- Prevent adding animals to full sectors.
- Prevent removing sectors containing animals.

### Data Management
- Automatically save shelter data to a JSON file.
- Restore data when the application starts.
- Import and export shelter data in JSON format.
- Validate input data and display error messages.
- Create a backup when importing new data.

### User Interface
- Desktop interface built with JavaFX.
- Custom CSS styling.
- Responsive layout with adjustable window size.
- Tables displaying shelter sectors and animals.
- Forms for adding and editing data.
- Dashboard displaying the number of animals, sectors, and available spaces.

## Technologies

- **Java 21** — application logic.
- **JavaFX** — graphical user interface.
- **Maven** — project and dependency management.
- **Jackson** — JSON serialization and deserialization.
- **JUnit 5** — unit testing.
- **JaCoCo** — test coverage reporting.
- **CSS** — interface styling.
- **Git & GitHub** — version control.

## Project Structure

```text
src/
├── main/
│   ├── java/pl/sheltermanager/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── view/
│   │   ├── Launcher.java
│   │   └── Main.java
│   └── resources/
│       └── style.css
└── test/
    └── java/
        └── pl/sheltermanager/
```

### Main Components

- `Animal` — stores information about individual animals.
- `AnimalCondition` — defines animal health conditions.
- `ShelterSector` — manages animals assigned to a sector.
- `ShelterManager` — handles shelter management operations.
- `AnimalQueryService` — implements searching, sorting, and filtering.
- `ShelterFileRepository` — handles JSON file operations.
- `AnimalData`, `SectorData`, `ShelterData` — data transfer objects used for persistence.
- `ShelterApp` — creates the main JavaFX interface.
- `AnimalDialogs` — handles animal management dialogs.
- `SectorDialogs` — handles sector management dialogs.
- `DialogUtils` — provides common dialog messages.

## Running the Application

### Requirements
- Java Development Kit (JDK) 21
- Maven or IntelliJ IDEA with Maven support

### Instructions

1. Open the project in IntelliJ IDEA.
2. Make sure JDK 21 is selected.
3. Reload Maven dependencies.
4. Open the Maven tool window.
5. Run `Plugins → javafx → javafx:run`.

Alternatively, if Maven is installed and available in the terminal:

```bash
mvn javafx:run
```

The application starts with sample shelter data if no saved data file exists.

## Testing

Unit tests are implemented using JUnit 5 and cover animal validation, shelter operations, searching, sorting, filtering, and JSON persistence.

To execute tests:

```bash
mvn test
```

To execute tests and generate a JaCoCo coverage report:

```bash
mvn verify
```

The HTML coverage report is generated at:

`target/site/jacoco/index.html`

The project includes 20 unit tests, successfully executed during final verification.

## Data Storage

Shelter data is automatically saved to `shelter-data.json` and restored when the application is launched again.

The application also supports importing and exporting data in JSON format. Local data files are excluded from version control.

## Author

Marta Jędrys

AGH University of Krakow