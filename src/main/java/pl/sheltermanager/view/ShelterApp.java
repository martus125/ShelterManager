package pl.sheltermanager.view;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.sheltermanager.model.Animal;
import pl.sheltermanager.model.AnimalCondition;
import pl.sheltermanager.model.ShelterSector;
import pl.sheltermanager.service.ShelterManager;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.GridPane;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import pl.sheltermanager.repository.ShelterFileRepository;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.StandardCopyOption;
import pl.sheltermanager.service.AnimalQueryService;

public class ShelterApp extends Application {

    private Runnable updateAnimalTable;
    private Runnable updateStats;

    private ShelterManager currentManager;
    private ShelterFileRepository fileRepository;
    private Path dataFilePath;
    private final AnimalQueryService animalQueryService =
            new AnimalQueryService();

    @Override
    public void start(Stage stage) {

        ShelterFileRepository repository =
                new ShelterFileRepository();

        Path dataPath = Path.of("shelter-data.json");
        this.fileRepository = repository;
        this.dataFilePath = dataPath;

        ShelterManager manager;

        if (Files.exists(dataPath)) {

            try {
                manager = repository.loadFromFile(dataPath);
                System.out.println("Wczytano dane schroniska z JSON.");

            } catch (IOException e) {
                showError(
                        "Nie udało się wczytać danych: "
                                + e.getMessage()
                );
                return;
            }

        } else {

            // Dane początkowe, tylko przy pierwszym uruchomieniu
            manager = new ShelterManager();

            manager.addSector("Psy", 5);
            manager.addSector("Koty", 3);
            manager.addSector("Kwarantanna", 2);

            manager.addAnimalToSector("Psy", new Animal(
                    "Max", "Pies", "Labrador",
                    AnimalCondition.HEALTHY, 3, 25.5
            ));

            manager.addAnimalToSector("Psy", new Animal(
                    "Azor", "Pies", "Owczarek",
                    AnimalCondition.SICK, 5, 30.0
            ));

            manager.addAnimalToSector("Koty", new Animal(
                    "Bella", "Kot", "Europejski",
                    AnimalCondition.HEALTHY, 2, 4.2
            ));
        }
        final ShelterManager shelterManager = manager;
        this.currentManager = shelterManager;

        Label title = new Label("🐾 ShelterManager");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");


        Label summary = new Label();
        updateSummary(manager, summary);

        Label animalCount = new Label();
        Label sectorCount = new Label();
        Label freePlaces = new Label();

        animalCount.getStyleClass().add("stat-number");
        sectorCount.getStyleClass().add("stat-number");
        freePlaces.getStyleClass().add("stat-number");

        Runnable refreshStats = () -> {
            int animals = 0;
            int capacity = 0;

            for (ShelterSector sector : manager.getAllSectors()) {
                animals += sector.getAnimals().size();
                capacity += sector.getMaxCapacity();
            }

            animalCount.setText(String.valueOf(animals));
            sectorCount.setText(
                    String.valueOf(manager.getAllSectors().size())
            );
            freePlaces.setText(String.valueOf(capacity - animals));
        };

        this.updateStats = refreshStats;
        refreshStats.run();


        VBox animalsCard = new VBox(
                8, new Label("Zwierzęta"), animalCount
        );

        VBox sectorsCard = new VBox(
                8, new Label("Sektory"), sectorCount
        );

        VBox placesCard = new VBox(
                8, new Label("Wolne miejsca"), freePlaces
        );

        animalsCard.getStyleClass().add("stat-card");
        sectorsCard.getStyleClass().add("stat-card");
        placesCard.getStyleClass().add("stat-card");

        HBox stats = new HBox(
                10, animalsCard, sectorsCard, placesCard
        );

        // Tabela sektorów
        TableView<ShelterSector> sectorTable = new TableView<>();

        TableColumn<ShelterSector, String> sectorName =
                new TableColumn<>("Sektor");

        sectorName.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(
                        data.getValue().getSectorName()
                )
        );

        TableColumn<ShelterSector, String> capacity =
                new TableColumn<>("Zapełnienie");

        capacity.setCellValueFactory(data -> {
            ShelterSector sector = data.getValue();
            return new ReadOnlyStringWrapper(
                    sector.getAnimals().size() + "/"
                            + sector.getMaxCapacity()
            );
        });

        sectorTable.getColumns().addAll(sectorName, capacity);

        sectorTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        sectorName.setPrefWidth(180);
        capacity.setPrefWidth(160);

        sectorTable.setPrefWidth(380);

        sectorTable.setItems(
                FXCollections.observableArrayList(manager.getAllSectors())
        );

        // Tabela zwierząt
        TableView<Animal> animalTable = new TableView<>();
        TableColumn<Animal, String> idColumn =
                new TableColumn<>("ID");
        animalTable.setTableMenuButtonVisible(true);

        idColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(
                        data.getValue().getId()
                )
        );

        TableColumn<Animal, String> animalName =
                new TableColumn<>("Imię");

        animalName.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(
                        data.getValue().getName()
                )
        );

        TableColumn<Animal, String> species =
                new TableColumn<>("Gatunek");

        species.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(
                        data.getValue().getSpecies()
                )
        );

        TableColumn<Animal, Number> age =
                new TableColumn<>("Wiek");

        age.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        data.getValue().getAge()
                )
        );

        TableColumn<Animal, Number> weight =
                new TableColumn<>("Waga (kg)");

        weight.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        data.getValue().getWeight()
                )
        );

        TableColumn<Animal, String> breedColumn =
                new TableColumn<>("Rasa");

        breedColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(
                        data.getValue().getBreed()
                )
        );

        TableColumn<Animal, String> conditionColumn =
                new TableColumn<>("Stan zdrowia");

        conditionColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(
                        data.getValue().getCondition().name()
                )
        );


        animalTable.getColumns().addAll(
                idColumn,
                animalName,
                species,
                breedColumn,
                age,
                weight,
                conditionColumn
        );

        idColumn.setPrefWidth(65);
        animalName.setPrefWidth(100);
        species.setPrefWidth(90);
        breedColumn.setPrefWidth(120);
        age.setPrefWidth(60);
        weight.setPrefWidth(90);
        conditionColumn.setPrefWidth(135);

        Button addSectorButton = new Button("+ Dodaj sektor");

        addSectorButton.setOnAction(event -> {

            SectorDialogs sectorDialogs = new SectorDialogs();

            sectorDialogs.showAddSectorDialog(
                    manager,
                    () -> {
                        saveData();

                        sectorTable.setItems(
                                FXCollections.observableArrayList(
                                        manager.getAllSectors()
                                )
                        );

                        sectorTable.getSelectionModel().selectLast();
                        updateSummary(manager, summary);
                    }
            );
        });


        Button removeSectorButton = new Button("Usuń sektor");


        removeSectorButton.setOnAction(event -> {

            ShelterSector selectedSector =
                    sectorTable.getSelectionModel().getSelectedItem();

            if (selectedSector == null) {
                showError("Najpierw wybierz sektor do usunięcia.");
                return;
            }

            SectorDialogs sectorDialogs = new SectorDialogs();

            sectorDialogs.showRemoveSectorDialog(
                    manager,
                    selectedSector,
                    () -> {
                        saveData();

                        sectorTable.setItems(
                                FXCollections.observableArrayList(
                                        manager.getAllSectors()
                                )
                        );

                        if (!sectorTable.getItems().isEmpty()) {
                            sectorTable.getSelectionModel().selectFirst();
                        } else {
                            sectorTable.getSelectionModel().clearSelection();
                        }

                        updateAnimalTable.run();
                        animalTable.refresh();
                        updateSummary(manager, summary);
                    }
            );
        });


        Button editSectorButton = new Button("Edytuj sektor");


        editSectorButton.setOnAction(event -> {

            ShelterSector selectedSector =
                    sectorTable.getSelectionModel().getSelectedItem();

            if (selectedSector == null) {
                showError("Najpierw wybierz sektor do edycji.");
                return;
            }

            SectorDialogs sectorDialogs = new SectorDialogs();

            sectorDialogs.showEditSectorDialog(
                    manager,
                    selectedSector,
                    () -> {
                        saveData();

                        sectorTable.refresh();
                        animalTable.refresh();

                        updateAnimalTable.run();
                        updateSummary(manager, summary);
                    }
            );
        });

        VBox sectorButtons = new VBox(
                10,
                addSectorButton,
                editSectorButton,
                removeSectorButton
        );
        addSectorButton.setMaxWidth(Double.MAX_VALUE);
        editSectorButton.setMaxWidth(Double.MAX_VALUE);
        removeSectorButton.setMaxWidth(Double.MAX_VALUE);

        VBox left = new VBox(
                12,
                new Label("Sektory schroniska"),
                sectorButtons,
                sectorTable
        );

        Button addAnimalButton = new Button("+ Dodaj zwierzę");

        addAnimalButton.setOnAction(event -> {

            ShelterSector selectedSector =
                    sectorTable.getSelectionModel().getSelectedItem();

            if (selectedSector == null) {
                showError("Najpierw wybierz sektor.");
                return;
            }

            AnimalDialogs animalDialogs = new AnimalDialogs();

            animalDialogs.showAddAnimalDialog(
                    manager,
                    selectedSector,
                    () -> {
                        saveData();
                        updateAnimalTable.run();
                        animalTable.refresh();
                        sectorTable.refresh();
                        updateSummary(manager, summary);
                    }
            );
        });


        Button editAnimalButton = new Button("Edytuj zwierzę");
        Button removeAnimalButton = new Button("Usuń zwierzę");
        Button moveAnimalButton = new Button("Przenieś zwierzę");

        moveAnimalButton.setOnAction(event -> {

            ShelterSector selectedSector =
                    sectorTable.getSelectionModel().getSelectedItem();

            Animal selectedAnimal =
                    animalTable.getSelectionModel().getSelectedItem();

            if (selectedSector == null || selectedAnimal == null) {
                showError("Najpierw wybierz zwierzę do przeniesienia.");
                return;
            }

            AnimalDialogs animalDialogs = new AnimalDialogs();

            animalDialogs.showMoveAnimalDialog(
                    manager,
                    selectedSector,
                    selectedAnimal,
                    () -> {
                        saveData();
                        updateAnimalTable.run();
                        animalTable.refresh();
                        sectorTable.refresh();
                        updateSummary(manager, summary);
                    }
            );
        });

        editAnimalButton.setOnAction(event -> {

            Animal selectedAnimal =
                    animalTable.getSelectionModel().getSelectedItem();

            if (selectedAnimal == null) {
                showError("Najpierw wybierz zwierzę z tabeli.");
                return;
            }

            AnimalDialogs animalDialogs = new AnimalDialogs();


            animalDialogs.showEditAnimalDialog(
                    selectedAnimal,
                    () -> {
                        saveData();

                        updateAnimalTable.run();
                        animalTable.refresh();
                        sectorTable.refresh();

                        updateSummary(manager, summary);
                    }
            );

        });

        removeAnimalButton.setOnAction(event -> {

            ShelterSector selectedSector =
                    sectorTable.getSelectionModel().getSelectedItem();

            Animal selectedAnimal =
                    animalTable.getSelectionModel().getSelectedItem();

            if (selectedSector == null || selectedAnimal == null) {
                showError("Najpierw wybierz zwierzę do usunięcia.");
                return;
            }

            AnimalDialogs animalDialogs = new AnimalDialogs();

            animalDialogs.showRemoveAnimalDialog(
                    selectedSector,
                    selectedAnimal,
                    () -> {
                        saveData();
                        updateAnimalTable.run();
                        animalTable.refresh();
                        sectorTable.refresh();
                        updateSummary(manager, summary);
                    }
            );
        });

        HBox buttons = new HBox(
                10,
                addAnimalButton,
                editAnimalButton,
                removeAnimalButton,
                moveAnimalButton
        );

        ComboBox<String> sortBox = new ComboBox<>();

        sortBox.getItems().addAll(
                "Imię A-Z",
                "Imię Z-A",
                "Wiek rosnąco",
                "Wiek malejąco",
                "Waga rosnąco",
                "Waga malejąco"
        );

        sortBox.setValue("Imię A-Z");
        sortBox.setPrefWidth(160);

// Filtr stanu zdrowia
        ComboBox<String> conditionFilter = new ComboBox<>();

        conditionFilter.getItems().addAll(
                "Wszystkie",
                "HEALTHY",
                "SICK",
                "TREATMENT",
                "QUARANTINE"
        );

        conditionFilter.setValue("Wszystkie");
        conditionFilter.setPrefWidth(150);

        TextField searchField = new TextField();
        searchField.setPromptText("Szukaj po imieniu lub gatunku...");
        searchField.setPrefWidth(250);

        Runnable updateTable = () -> {

            ShelterSector selectedSector =
                    sectorTable.getSelectionModel().getSelectedItem();

            AnimalCondition selectedCondition = null;

            if (!conditionFilter.getValue().equals("Wszystkie")) {
                selectedCondition = AnimalCondition.valueOf(
                        conditionFilter.getValue()
                );
            }

            refreshAnimalTable(
                    selectedSector,
                    animalTable,
                    searchField.getText(),
                    selectedCondition,
                    sortBox.getValue()
            );
        };
        this.updateAnimalTable = updateTable;
// Zmiana sektora
        sectorTable.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (obs, oldSector, newSector) -> updateTable.run()
                );

// Wyszukiwanie
        searchField.textProperty().addListener(
                (obs, oldValue, newValue) -> updateTable.run()
        );

// Sortowanie
        sortBox.valueProperty().addListener(
                (obs, oldValue, newValue) -> updateTable.run()
        );

// Filtrowanie
        conditionFilter.valueProperty().addListener(
                (obs, oldValue, newValue) -> updateTable.run()
        );


        HBox filters = new HBox(
                10,
                searchField,
                sortBox,
                conditionFilter
        );

        VBox right = new VBox(
                10,
                new Label("Zwierzęta w wybranym sektorze"),
                filters,
                buttons,
                animalTable
        );

        HBox.setHgrow(right, javafx.scene.layout.Priority.ALWAYS);
        animalTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        Button exportButton = new Button("Eksportuj JSON");

        exportButton.setOnAction(event -> {
            exportShelterData(stage, manager, repository);
        });

        Button importButton = new Button("Importuj JSON");

        importButton.setOnAction(event -> {
            importShelterData(stage, repository, dataPath);
        });

        HBox fileButtons = new HBox(
                10,
                importButton,
                exportButton
        );

        sectorTable.setPrefHeight(320);
        VBox leftPanel = new VBox(
                20,
                stats,
                fileButtons,
                left
        );

        leftPanel.setPrefWidth(380);
        leftPanel.setMinWidth(320);

        HBox mainContent = new HBox(
                30,
                leftPanel,
                right
        );

        HBox.setHgrow(right, javafx.scene.layout.Priority.ALWAYS);

        VBox root = new VBox(
                25,
                title,
                mainContent
        );

        root.setPadding(new Insets(25));

        Scene scene = new Scene(root, 1250, 700);

        scene.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        stage.setTitle("ShelterManager");
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {

            try {
                repository.saveToFile(shelterManager, dataPath);

                System.out.println(
                        "Dane schroniska zostały zapisane."
                );

            } catch (IOException e) {
                event.consume();

                showError(
                        "Nie udało się zapisać danych. "
                                + "Aplikacja pozostanie otwarta.\n"
                                + e.getMessage()
                );
            }
        });

        stage.show();

        // Domyślnie wybieramy pierwszy sektor
        sectorTable.getSelectionModel().selectFirst();
    }

    private void showError(String message) {
        DialogUtils.showError(message);
    }

    private void updateSummary(ShelterManager manager, Label summary) {

        int totalAnimals = 0;
        int totalCapacity = 0;

        for (ShelterSector sector : manager.getAllSectors()) {
            totalAnimals += sector.getAnimals().size();
            totalCapacity += sector.getMaxCapacity();
        }

        summary.setText(
                "Zwierzęta: " + totalAnimals
                        + " | Sektory: " + manager.getAllSectors().size()
                        + " | Wolne miejsca: " + (totalCapacity - totalAnimals)
        );
//updatowanie kafelkow
        if (updateStats != null) {
            updateStats.run();
        }
    }
    private void refreshAnimalTable(
            ShelterSector sector,
            TableView<Animal> animalTable,
            String searchText,
            AnimalCondition condition,
            String sortOption) {

        List<Animal> animals = animalQueryService.getAnimals(
                sector,
                searchText,
                condition,
                sortOption
        );

        animalTable.setItems(
                FXCollections.observableArrayList(animals)
        );
    }
        private void exportShelterData(
                Stage stage,
                ShelterManager manager,
                ShelterFileRepository repository) {

            FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("Eksportuj dane schroniska");
            fileChooser.setInitialFileName("shelter-export.json");

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Pliki JSON (*.json)", "*.json"
                    )
            );

            File file = fileChooser.showSaveDialog(stage);

            // Użytkownik anulował wybór pliku
            if (file == null) {
                return;
            }

            try {
                repository.saveToFile(manager, file.toPath());

                Alert success = new Alert(
                        Alert.AlertType.INFORMATION
                );
                success.setTitle("Eksport zakończony");
                success.setHeaderText(null);
                success.setContentText(
                        "Dane schroniska zostały wyeksportowane:\n"
                                + file.getAbsolutePath()
                );
                success.showAndWait();

            } catch (IOException e) {
                showError(
                        "Nie udało się wyeksportować danych:\n"
                                + e.getMessage()
                );
            }
        }

    private void importShelterData(
            Stage stage,
            ShelterFileRepository repository,
            Path dataPath) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importuj dane schroniska");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Pliki JSON (*.json)", "*.json"
                )
        );

        File file = fileChooser.showOpenDialog(stage);

        if (file == null) {
            return;
        }

        // Najpierw sprawdzamy poprawność pliku
        ShelterManager importedManager;

        try {
            importedManager = repository.loadFromFile(
                    file.toPath()
            );
        } catch (IOException e) {
            showError(
                    "Nie udało się wczytać pliku:\n"
                            + e.getMessage()
            );
            return;
        }

        // Potwierdzenie zastąpienia danych
        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Import danych");
        confirmation.setHeaderText(
                "Czy zastąpić aktualne dane schroniska?"
        );
        confirmation.setContentText(
                "Przed importem zostanie utworzona kopia "
                        + "dotychczasowego pliku JSON."
        );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        if (result.isEmpty()
                || result.get() != ButtonType.OK) {
            return;
        }

        Path backupPath = Path.of("shelter-data-backup.json");
        Path tempPath = Path.of("shelter-import-temp.json");

        try {
            // Kopia dotychczasowych danych
            repository.saveToFile(
                    getCurrentShelterManager(),
                    tempPath
            );

            Files.copy(
                    tempPath,
                    backupPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Zapis nowych danych do pliku tymczasowego
            repository.saveToFile(importedManager, tempPath);

            // Zastąpienie głównego pliku
            Files.move(
                    tempPath,
                    dataPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Ponowne zbudowanie interfejsu
            start(stage);

            Alert success = new Alert(
                    Alert.AlertType.INFORMATION
            );
            success.setTitle("Import zakończony");
            success.setHeaderText(null);
            success.setContentText(
                    "Dane schroniska zostały zaimportowane."
            );
            success.showAndWait();

        } catch (IOException e) {
            showError(
                    "Błąd importowania danych:\n"
                            + e.getMessage()
            );
        }
    }
    private ShelterManager getCurrentShelterManager() {
        return currentManager;
    }

    private boolean saveData() {

        if (currentManager == null
                || fileRepository == null
                || dataFilePath == null) {
            showError("Nie zainicjalizowano zapisu danych.");
            return false;
        }

        try {
            fileRepository.saveToFile(
                    currentManager,
                    dataFilePath
            );

            System.out.println(
                    "Automatycznie zapisano dane schroniska."
            );

            return true;

        } catch (IOException e) {
            showError(
                    "Nie udało się zapisać danych:\n"
                            + e.getMessage()
            );
            return false;
        }
    }

}