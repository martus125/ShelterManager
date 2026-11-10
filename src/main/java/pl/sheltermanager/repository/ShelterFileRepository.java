package pl.sheltermanager.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import pl.sheltermanager.dto.AnimalData;
import pl.sheltermanager.dto.SectorData;
import pl.sheltermanager.dto.ShelterData;
import pl.sheltermanager.model.Animal;
import pl.sheltermanager.model.AnimalCondition;
import pl.sheltermanager.model.ShelterSector;
import pl.sheltermanager.service.ShelterManager;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ShelterFileRepository {

    private final ObjectMapper mapper;

    public ShelterFileRepository() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveToFile(ShelterManager manager, Path filePath)
            throws IOException {

        ShelterData shelterData = new ShelterData();

        for (ShelterSector sector : manager.getAllSectors()) {

            SectorData sectorData = new SectorData(
                    sector.getSectorName(),
                    sector.getMaxCapacity()
            );

            for (Animal animal : sector.getAnimals()) {

                AnimalData animalData = new AnimalData(
                        animal.getId(),
                        animal.getName(),
                        animal.getSpecies(),
                        animal.getBreed(),
                        animal.getCondition().name(),
                        animal.getAge(),
                        animal.getWeight()
                );

                sectorData.animals.add(animalData);
            }

            shelterData.sectors.add(sectorData);
        }


        Path target = filePath.toAbsolutePath();
        Path directory = target.getParent();

        Files.createDirectories(directory);

// Tworzymy plik tymczasowy w tym samym folderze
        Path tempFile = Files.createTempFile(
                directory,
                "shelter-",
                ".tmp"
        );

        try {
            // Najpierw zapisujemy kompletny JSON do pliku tymczasowego
            mapper.writeValue(tempFile.toFile(), shelterData);

            // Dopiero po poprawnym zapisie zastępujemy plik główny
            try {
                Files.move(
                        tempFile,
                        target,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );

            } catch (AtomicMoveNotSupportedException e) {
                // Nie każdy system plików obsługuje atomową podmianę
                Files.move(
                        tempFile,
                        target,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } finally {
            // Sprzątamy pozostały plik tymczasowy
            Files.deleteIfExists(tempFile);
        }

    }

    public ShelterManager loadFromFile(Path filePath)
            throws IOException {

        if (!Files.exists(filePath)) {
            throw new IOException(
                    "Plik nie istnieje: " + filePath
            );
        }

        ShelterData data = mapper.readValue(
                filePath.toFile(),
                ShelterData.class
        );

        if (data == null || data.sectors == null) {
            throw new IOException(
                    "Nieprawidłowa struktura pliku JSON."
            );
        }

        ShelterManager manager = new ShelterManager();

        try {
            // Najpierw odtwarzamy sektory
            for (SectorData sectorData : data.sectors) {
                manager.addSector(
                        sectorData.name,
                        sectorData.capacity
                );
            }

            // Następnie odtwarzamy zwierzęta i ich ID
            for (SectorData sectorData : data.sectors) {

                if (sectorData.animals == null) {
                    throw new IllegalArgumentException(
                            "Brak listy zwierząt w sektorze."
                    );
                }

                for (AnimalData animalData : sectorData.animals) {

                    Animal animal = new Animal(
                            animalData.name,
                            animalData.species,
                            animalData.breed,
                            AnimalCondition.valueOf(
                                    animalData.condition
                            ),
                            animalData.age,
                            animalData.weight
                    );

                    manager.restoreAnimalToSector(
                            sectorData.name,
                            animal,
                            animalData.id
                    );
                }
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IOException(
                    "Nieprawidłowe dane schroniska: "
                            + e.getMessage(),
                    e
            );
        }

        return manager;
    }

}
