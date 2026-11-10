package pl.sheltermanager.view;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import pl.sheltermanager.service.ShelterManager;
import pl.sheltermanager.model.ShelterSector;

public class SectorDialogs {

    public void showAddSectorDialog(
            ShelterManager manager,
            Runnable onChanged) {

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

        Button save = (Button) dialog.getDialogPane()
                .lookupButton(saveButton);

        save.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                String name = nameField.getText().trim();
                int capacity = Integer.parseInt(
                        capacityField.getText().trim()
                );

                manager.addSector(name, capacity);
                onChanged.run();

                DialogUtils.showSuccess(
                        "Dodano sektor: " + name
                );

            } catch (NumberFormatException e) {
                DialogUtils.showError(
                        "Pojemność musi być liczbą całkowitą."
                );
                event.consume();

            } catch (IllegalArgumentException
                     | IllegalStateException e) {
                DialogUtils.showError(e.getMessage());
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    public void showEditSectorDialog(
            ShelterManager manager,
            ShelterSector sector,
            Runnable onChanged) {

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

        save.addEventFilter(ActionEvent.ACTION, event -> {
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

                onChanged.run();

                DialogUtils.showSuccess(
                        "Dane sektora zostały zaktualizowane."
                );

            } catch (NumberFormatException e) {
                DialogUtils.showError(
                        "Pojemność musi być liczbą całkowitą."
                );
                event.consume();

            } catch (IllegalArgumentException
                     | IllegalStateException e) {
                DialogUtils.showError(e.getMessage());
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    public void showRemoveSectorDialog(
            ShelterManager manager,
            ShelterSector sector,
            Runnable onChanged) {

        // Nie pozwalamy usunąć sektora ze zwierzętami
        if (!sector.getAnimals().isEmpty()) {
            DialogUtils.showError(
                    "Nie można usunąć sektora, w którym znajdują się zwierzęta."
            );
            return;
        }

        boolean confirmed = DialogUtils.confirm(
                "Usuń sektor",
                "Czy na pewno chcesz usunąć sektor?",
                sector.getSectorName()
        );

        if (!confirmed) {
            return;
        }

        try {
            manager.removeSector(sector.getSectorName());

            onChanged.run();

            DialogUtils.showSuccess(
                    "Sektor został usunięty."
            );

        } catch (IllegalArgumentException
                 | IllegalStateException e) {
            DialogUtils.showError(e.getMessage());
        }
    }

}
