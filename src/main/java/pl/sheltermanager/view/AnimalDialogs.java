//formularz dodawania zwierzęcia
package pl.sheltermanager.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import pl.sheltermanager.model.Animal;
import pl.sheltermanager.model.AnimalCondition;
import pl.sheltermanager.model.ShelterSector;
import pl.sheltermanager.service.ShelterManager;
import javafx.scene.layout.VBox;
import java.util.Optional;

public class AnimalDialogs {

    public void showAddAnimalDialog(
            ShelterManager manager,
            ShelterSector sector,
            Runnable onChanged) {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Dodaj zwierzę");
        dialog.setHeaderText(
                "Nowe zwierzę — " + sector.getSectorName()
        );

        ButtonType saveButton = new ButtonType(
                "Zapisz",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButton,
                ButtonType.CANCEL
        );

        TextField nameField = new TextField();
        TextField speciesField = new TextField();
        TextField breedField = new TextField();
        TextField ageField = new TextField();
        TextField weightField = new TextField();

        ComboBox<AnimalCondition> conditionBox =
                new ComboBox<>();

        conditionBox.getItems().addAll(
                AnimalCondition.values()
        );
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

        if (result.isPresent()
                && result.get() == saveButton) {

            try {
                int age = Integer.parseInt(
                        ageField.getText().trim()
                );

                double weight = Double.parseDouble(
                        weightField.getText()
                                .trim()
                                .replace(",", ".")
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
                        sector.getSectorName(),
                        animal
                );

                // Zapis danych i odświeżenie interfejsu
                onChanged.run();

                DialogUtils.showSuccess(
                        "Dodano zwierzę: " + animal.getName()
                                + "\nID: " + animal.getId()
                );

            } catch (NumberFormatException e) {
                DialogUtils.showError(
                        "Wiek musi być liczbą całkowitą, "
                                + "a waga liczbą."
                );

            } catch (IllegalArgumentException
                     | IllegalStateException e) {
                DialogUtils.showError(e.getMessage());
            }
        }
    }
    public void showEditAnimalDialog(
            Animal animal,
            Runnable onChanged) {

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

                // Sprawdzamy poprawność nowych danych
                Animal validated = new Animal(
                        name, species, breed,
                        conditionBox.getValue(), age, weight
                );

                // Aktualizujemy istniejące zwierzę
                animal.setName(validated.getName());
                animal.setSpecies(validated.getSpecies());
                animal.setBreed(validated.getBreed());
                animal.setCondition(validated.getCondition());
                animal.setAge(validated.getAge());
                animal.setWeight(validated.getWeight());

                onChanged.run();

                DialogUtils.showSuccess(
                        "Zaktualizowano dane zwierzęcia."
                );

            } catch (NumberFormatException e) {
                DialogUtils.showError(
                        "Wiek musi być liczbą całkowitą, a waga liczbą."
                );
            } catch (IllegalArgumentException e) {
                DialogUtils.showError(e.getMessage());
            }
        }
    }
    //usuwanie z oknem potwierdzenia
    public void showRemoveAnimalDialog(
            ShelterSector sector,
            Animal animal,
            Runnable onChanged) {

        boolean confirmed = DialogUtils.confirm(
                "Usuń zwierzę",
                "Czy na pewno chcesz usunąć zwierzę?",
                animal.getName() + " (ID: " + animal.getId() + ")"
        );

        if (!confirmed) {
            return;
        }

        try {
            sector.removeAnimal(animal);

            onChanged.run();

            DialogUtils.showSuccess(
                    "Zwierzę zostało usunięte."
            );

        } catch (IllegalArgumentException e) {
            DialogUtils.showError(e.getMessage());
        }
    }

    public void showMoveAnimalDialog(
            ShelterManager manager,
            ShelterSector sourceSector,
            Animal animal,
            Runnable onChanged) {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Przenieś zwierzę");
        dialog.setHeaderText(
                "Przenoszenie: " + animal.getName()
                        + " (ID: " + animal.getId() + ")"
        );

        ButtonType moveButton = new ButtonType(
                "Przenieś",
                ButtonBar.ButtonData.OK_DONE
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
                new Label(
                        "Sektor źródłowy: "
                                + sourceSector.getSectorName()
                ),
                new Label("Sektor docelowy:"),
                sectorBox
        );

        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == moveButton) {

            if (sectorBox.getValue() == null) {
                DialogUtils.showError(
                        "Wybierz sektor docelowy."
                );
                return;
            }

            try {
                manager.moveAnimalBetweenSectors(
                        animal.getId(),
                        sourceSector.getSectorName(),
                        sectorBox.getValue()
                );

                onChanged.run();

                DialogUtils.showSuccess(
                        "Przeniesiono " + animal.getName()
                                + " do sektora "
                                + sectorBox.getValue()
                );

            } catch (IllegalArgumentException
                     | IllegalStateException e) {
                DialogUtils.showError(e.getMessage());
            }
        }
    }

}
