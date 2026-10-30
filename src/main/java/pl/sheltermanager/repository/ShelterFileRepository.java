package pl.sheltermanager.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import pl.sheltermanager.dto.AnimalData;
import pl.sheltermanager.dto.SectorData;
import pl.sheltermanager.dto.ShelterData;
import pl.sheltermanager.model.Animal;
import pl.sheltermanager.model.ShelterSector;
import pl.sheltermanager.service.ShelterManager;
import pl.sheltermanager.model.AnimalCondition;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;

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

        mapper.writeValue(filePath.toFile(), shelterData);
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
