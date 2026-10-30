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

public class ShelterApp extends Application {
    private Runnable updateAnimalTable;
    @Override
    public void start(Stage stage) {

        ShelterFileRepository repository =
                new ShelterFileRepository();

        Path dataPath = Path.of("shelter-data.json");

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

        Label title = new Label("🐾 ShelterManager");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label summary = new Label();
        updateSummary(manager, summary);

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
        sectorTable.setItems(
                FXCollections.observableArrayList(manager.getAllSectors())
        );

        // Tabela zwierząt
        TableView<Animal> animalTable = new TableView<>();

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

        animalTable.getColumns().addAll(
                animalName, species, age, weight
        );

        Button addSectorButton = new Button("+ Dodaj sektor");

        addSectorButton.setOnAction(event -> {
            showAddSectorDialog(manager, sectorTable, summary);
        });

        Button removeSectorButton = new Button("Usuń sektor");

        removeSectorButton.setOnAction(event -> {

            ShelterSector selectedSector =
                    sectorTable.getSelectionModel().getSelectedItem();

            if (selectedSector == null) {
                showError("Najpierw wybierz sektor do usunięcia.");
                return;
            }

            showRemoveSectorDialog(
                    manager,
                    selectedSector,
                    sectorTable,
                    summary
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

            showEditSectorDialog(
                    manager,
                    selectedSector,
                    sectorTable,
                    summary
            );
        });

        HBox sectorButtons = new HBox(
                10,
                addSectorButton,
                editSectorButton,
                removeSectorButton
        );

        VBox left = new VBox(
                10,
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

            showAddAnimalDialog(manager, selectedSector, animalTable,
                    sectorTable, summary);
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

            showMoveAnimalDialog(
                    manager,
                    selectedSector,
                    selectedAnimal,
                    animalTable,
                    sectorTable,
                    summary
            );
        });


        editAnimalButton.setOnAction(event -> {

            ShelterSector selectedSector =
                    sectorTable.getSelectionModel().getSelectedItem();

            Animal selectedAnimal =
                    animalTable.getSelectionModel().getSelectedItem();

            if (selectedSector == null || selectedAnimal == null) {
                showError("Najpierw wybierz zwierzę z tabeli.");
                return;
            }

            showEditAnimalDialog(
                    selectedSector,
                    selectedAnimal,
                    animalTable
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

            removeSelectedAnimal(
                    manager,
                    selectedSector,
                    selectedAnimal,
                    animalTable,
                    sectorTable,
                    summary
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


        HBox tables = new HBox(20, left, right);
        HBox.setHgrow(right, javafx.scene.layout.Priority.ALWAYS);
        animalTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        Button exportButton = new Button("Eksportuj JSON");

        exportButton.setOnAction(event -> {
            exportShelterData(stage, manager, repository);
        });

        VBox root = new VBox(20, title, summary, exportButton, tables);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #F5F7F5;");

        Scene scene = new Scene(root, 1000, 600);

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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
    }


    private void showAddAnimalDialog(
            ShelterManager manager,
            ShelterSector sector,
            TableView<Animal> animalTable,
            TableView<ShelterSector> sectorTable,
            Label summary) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Dodaj zwierzę");
        dialog.setHeaderText("Nowe zwierzę — " + sector.getSectorName());

        ButtonType saveButton = new ButtonType(
                "Zapisz", ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButton, ButtonType.CANCEL
        );

        TextField nameField = new TextField();
        TextField speciesField = new TextField();
        TextField breedField = new TextField();
        TextField ageField = new TextField();
        TextField weightField = new TextField();

        ComboBox<AnimalCondition> conditionBox = new ComboBox<>();
        conditionBox.getItems().addAll(AnimalCondition.values());
        conditionBox.setValue(AnimalCondition.HEALTHY);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.addRow(0, new Label("Imię:"), nameField);
        grid.addRow(1, new Label("Gatunek:"), speciesField);
        grid.addRow(2, new Label("Rasa:"), breedField);
        grid.addRow(3, new Label("Stan zdrowia:"), conditionBox);
        grid.addRow(4, new Label("Wiek:"), ageField);
        grid.addRow(5, new Label("Waga (kg):"), weightField);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == saveButton) {
            try {
                int age = Integer.parseInt(ageField.getText().trim());

                double weight = Double.parseDouble(
                        weightField.getText().trim().replace(",", ".")
                );

                Animal animal = new Animal(
                        nameField.getText(),
                        speciesField.getText(),
                        breedField.getText(),
                        conditionBox.getValue(),
                        age,
                        weight
                );

                manager.addAnimalToSector(
                        sector.getSectorName(), animal
                );

                updateAnimalTable.run();

                sectorTable.refresh();
                updateSummary(manager, summary);

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setHeaderText(null);
                success.setContentText(
                        "Dodano zwierzę: " + animal.getName()
                                + "\nID: " + animal.getId()
                );
                success.showAndWait();

            } catch (NumberFormatException e) {
                showError("Wiek musi być liczbą całkowitą, a waga liczbą.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                showError(e.getMessage());
            }
        }
    }

    private void showEditAnimalDialog(
            ShelterSector sector,
            Animal animal,
            TableView<Animal> animalTable) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edytuj zwierzę");
        dialog.setHeaderText(
                "Edycja: " + animal.getName()
                        + " (ID: " + animal.getId() + ")"
        );

        ButtonType saveButton = new ButtonType(
                "Zapisz", ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButton, ButtonType.CANCEL
        );

        TextField nameField = new TextField(animal.getName());
        TextField speciesField = new TextField(animal.getSpecies());
        TextField breedField = new TextField(animal.getBreed());
        TextField ageField = new TextField(
                String.valueOf(animal.getAge())
        );
        TextField weightField = new TextField(
                String.valueOf(animal.getWeight())
        );

        ComboBox<AnimalCondition> conditionBox = new ComboBox<>();
        conditionBox.getItems().addAll(AnimalCondition.values());
        conditionBox.setValue(animal.getCondition());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.addRow(0, new Label("Imię:"), nameField);
        grid.addRow(1, new Label("Gatunek:"), speciesField);
        grid.addRow(2, new Label("Rasa:"), breedField);
        grid.addRow(3, new Label("Stan zdrowia:"), conditionBox);
        grid.addRow(4, new Label("Wiek:"), ageField);
        grid.addRow(5, new Label("Waga (kg):"), weightField);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == saveButton) {

            try {
                String name = nameField.getText().trim();
                String species = speciesField.getText().trim();
                String breed = breedField.getText().trim();

                int age = Integer.parseInt(
                        ageField.getText().trim()
                );

                double weight = Double.parseDouble(
                        weightField.getText().trim().replace(",", ".")
                );

                // Najpierw sprawdzamy wszystkie nowe dane
                Animal validated = new Animal(
                        name,
                        species,
                        breed,
                        conditionBox.getValue(),
                        age,
                        weight
                );

                // Aktualizujemy istniejące zwierzę
                animal.setName(validated.getName());
                animal.setSpecies(validated.getSpecies());
                animal.setBreed(validated.getBreed());
                animal.setCondition(validated.getCondition());
                animal.setAge(validated.getAge());
                animal.setWeight(validated.getWeight());

                updateAnimalTable.run();

                Alert success = new Alert(
                        Alert.AlertType.INFORMATION
                );
                success.setHeaderText(null);
                success.setContentText(
                        "Zaktualizowano dane zwierzęcia."
                );
                success.showAndWait();

            } catch (NumberFormatException e) {
                showError("Wiek i waga muszą być liczbami.");
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        }
    }

    private void removeSelectedAnimal(
            ShelterManager manager,
            ShelterSector sector,
            Animal animal,
            TableView<Animal> animalTable,
            TableView<ShelterSector> sectorTable,
            Label summary) {

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Usuń zwierzę");
        confirmation.setHeaderText(
                "Czy na pewno chcesz usunąć zwierzę?"
        );
        confirmation.setContentText(
                animal.getName() + " (ID: " + animal.getId() + ")"
        );

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            try {
                sector.removeAnimal(animal);

                updateAnimalTable.run();

                sectorTable.refresh();
                updateSummary(manager, summary);

                Alert success = new Alert(
                        Alert.AlertType.INFORMATION
                );
                success.setHeaderText(null);
                success.setContentText(
                        "Zwierzę zostało usunięte."
                );
                success.showAndWait();

            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        }
    }

    private void showMoveAnimalDialog(
            ShelterManager manager,
            ShelterSector sourceSector,
            Animal animal,
            TableView<Animal> animalTable,
            TableView<ShelterSector> sectorTable,
            Label summary) {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Przenieś zwierzę");
        dialog.setHeaderText(
                "Przenoszenie: " + animal.getName()
                        + " (ID: " + animal.getId() + ")"
        );

        ButtonType moveButton = new ButtonType(
                "Przenieś", ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                moveButton, ButtonType.CANCEL
        );

        ComboBox<String> sectorBox = new ComboBox<>();

        for (ShelterSector sector : manager.getAllSectors()) {
            if (sector != sourceSector) {
                sectorBox.getItems().add(sector.getSectorName());
            }
        }

        sectorBox.setPromptText("Wybierz sektor docelowy");

        VBox content = new VBox(
                10,
                new Label("Sektor źródłowy: "
                        + sourceSector.getSectorName()),
                new Label("Sektor docelowy:"),
                sectorBox
        );

        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == moveButton) {

            if (sectorBox.getValue() == null) {
                showError("Wybierz sektor docelowy.");
                return;
            }

            try {
                manager.moveAnimalBetweenSectors(
                        animal.getId(),
                        sourceSector.getSectorName(),
                        sectorBox.getValue()
                );

                updateAnimalTable.run();

                // Aktualizacja zapełnienia sektorów
                sectorTable.refresh();
                updateSummary(manager, summary);

                Alert success = new Alert(
                        Alert.AlertType.INFORMATION
                );

                success.setHeaderText(null);
                success.setContentText(
                        "Przeniesiono " + animal.getName()
                                + " do sektora " + sectorBox.getValue()
                );

                success.showAndWait();

            } catch (IllegalArgumentException | IllegalStateException e) {
                showError(e.getMessage());
            }
        }
    }

    private void refreshAnimalTable(
            ShelterSector sector,
            TableView<Animal> animalTable,
            String searchText,
            AnimalCondition condition,
            String sortOption) {

        if (sector == null) {
            animalTable.getItems().clear();
            return;
        }

        // Pobieramy zwierzęta z wybranego sektora
        List<Animal> animals = new ArrayList<>(
                sector.getAnimals()
        );

        // Wyszukiwanie
        if (searchText != null && !searchText.isBlank()) {
            animals = new ArrayList<>(
                    sector.searchPartial(searchText)
            );
        }

        // Filtrowanie według stanu zdrowia
        if (condition != null) {
            animals.removeIf(
                    animal -> animal.getCondition() != condition
            );
        }

        // Sortowanie
        if (sortOption != null) {
            switch (sortOption) {
                case "Imię A-Z":
                    animals.sort(
                            Comparator.comparing(
                                    Animal::getName,
                                    String.CASE_INSENSITIVE_ORDER
                            )
                    );
                    break;

                case "Imię Z-A":
                    animals.sort(
                            Comparator.comparing(
                                    Animal::getName,
                                    String.CASE_INSENSITIVE_ORDER
                            ).reversed()
                    );
                    break;

                case "Wiek rosnąco":
                    animals.sort(
                            Comparator.comparingInt(Animal::getAge)
                    );
                    break;

                case "Wiek malejąco":
                    animals.sort(
                            Comparator.comparingInt(
                                    Animal::getAge
                            ).reversed()
                    );
                    break;

                case "Waga rosnąco":
                    animals.sort(
                            Comparator.comparingDouble(
                                    Animal::getWeight
                            )
                    );
                    break;

                case "Waga malejąco":
                    animals.sort(
                            Comparator.comparingDouble(
                                    Animal::getWeight
                            ).reversed()
                    );
                    break;
            }
        }

        animalTable.setItems(
                FXCollections.observableArrayList(animals)
        );
    }

    private void showAddSectorDialog(
            ShelterManager manager,
            TableView<ShelterSector> sectorTable,
            Label summary) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Dodaj sektor");
        dialog.setHeaderText("Tworzenie nowego sektora");

        ButtonType saveButton = new ButtonType(
                "Zapisz", ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButton, ButtonType.CANCEL
        );

        TextField nameField = new TextField();
        nameField.setPromptText("Np. Sektor D");

        TextField capacityField = new TextField();
        capacityField.setPromptText("Np. 10");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.addRow(0, new Label("Nazwa sektora:"), nameField);
        grid.addRow(1, new Label("Pojemność:"), capacityField);

        dialog.getDialogPane().setContent(grid);

        // Zapisywanie bez zamykania formularza przy błędnych danych
        Button save = (Button) dialog.getDialogPane()
                .lookupButton(saveButton);

        save.addEventFilter(
                javafx.event.ActionEvent.ACTION, event -> {
                    try {
                        String name = nameField.getText().trim();

                        int capacity = Integer.parseInt(
                                capacityField.getText().trim()
                        );

                        manager.addSector(name, capacity);

                        sectorTable.setItems(
                                FXCollections.observableArrayList(
                                        manager.getAllSectors()
                                )
                        );

                        updateSummary(manager, summary);

                        Alert success = new Alert(
                                Alert.AlertType.INFORMATION
                        );
                        success.setHeaderText(null);
                        success.setContentText(
                                "Dodano sektor: " + name
                        );
                        success.showAndWait();

                    } catch (NumberFormatException e) {
                        showError(
                                "Pojemność musi być liczbą całkowitą."
                        );
                        event.consume();

                    } catch (IllegalArgumentException e) {
                        showError(e.getMessage());
                        event.consume();
                    }
                }
        );

        dialog.showAndWait();
    }

    private void showRemoveSectorDialog(
            ShelterManager manager,
            ShelterSector sector,
            TableView<ShelterSector> sectorTable,
            Label summary) {

        if (!sector.getAnimals().isEmpty()) {
            showError(
                    "Nie można usunąć sektora, w którym znajdują się zwierzęta."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Usuń sektor");
        confirmation.setHeaderText(
                "Czy na pewno chcesz usunąć sektor?"
        );
        confirmation.setContentText(sector.getSectorName());

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {
                manager.removeSector(sector.getSectorName());

                sectorTable.setItems(
                        FXCollections.observableArrayList(
                                manager.getAllSectors()
                        )
                );

                updateSummary(manager, summary);

                // Zaznaczamy kolejny dostępny sektor
                if (!sectorTable.getItems().isEmpty()) {
                    sectorTable.getSelectionModel().selectFirst();
                } else {
                    sectorTable.getSelectionModel().clearSelection();
                    updateAnimalTable.run();
                }
                Alert success = new Alert(
                        Alert.AlertType.INFORMATION
                );
                success.setHeaderText(null);
                success.setContentText(
                        "Sektor został usunięty."
                );
                success.showAndWait();

            } catch (IllegalArgumentException | IllegalStateException e) {
                showError(e.getMessage());
            }
        }
    }

    private void showEditSectorDialog(
            ShelterManager manager,
            ShelterSector sector,
            TableView<ShelterSector> sectorTable,
            Label summary) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edytuj sektor");
        dialog.setHeaderText(
                "Edycja sektora: " + sector.getSectorName()
        );

        ButtonType saveButton = new ButtonType(
                "Zapisz", ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButton, ButtonType.CANCEL
        );

        TextField nameField = new TextField(
                sector.getSectorName()
        );

        TextField capacityField = new TextField(
                String.valueOf(sector.getMaxCapacity())
        );

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.addRow(0, new Label("Nazwa sektora:"), nameField);
        grid.addRow(1, new Label("Pojemność:"), capacityField);

        dialog.getDialogPane().setContent(grid);

        Button save = (Button) dialog.getDialogPane()
                .lookupButton(saveButton);

        save.addEventFilter(
                javafx.event.ActionEvent.ACTION, event -> {

                    try {
                        String oldName = sector.getSectorName();
                        String newName = nameField.getText().trim();

                        int newCapacity = Integer.parseInt(
                                capacityField.getText().trim()
                        );

                        manager.updateSector(
                                oldName,
                                newName,
                                newCapacity
                        );

                        sectorTable.refresh();
                        updateSummary(manager, summary);

                        Alert success = new Alert(
                                Alert.AlertType.INFORMATION
                        );
                        success.setHeaderText(null);
                        success.setContentText(
                                "Dane sektora zostały zaktualizowane."
                        );
                        success.showAndWait();

                    } catch (NumberFormatException e) {
                        showError(
                                "Pojemność musi być liczbą całkowitą."
                        );
                        event.consume();

                    } catch (IllegalArgumentException
                             | IllegalStateException e) {
                        showError(e.getMessage());
                        event.consume();
                    }
                }
        );

        dialog.showAndWait();
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
    }