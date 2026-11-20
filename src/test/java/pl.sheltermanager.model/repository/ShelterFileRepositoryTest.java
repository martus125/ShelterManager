
package pl.sheltermanager.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pl.sheltermanager.model.Animal;
import pl.sheltermanager.model.AnimalCondition;
import pl.sheltermanager.model.ShelterSector;
import pl.sheltermanager.service.ShelterManager;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.io.IOException;

class ShelterFileRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldSaveAndLoadShelterData() throws Exception {

        // Przygotowanie schroniska
        ShelterManager manager = new ShelterManager();
        manager.addSector("Psy", 5);

        Animal animal = new Animal(
                "Max", "Pies", "Labrador",
                AnimalCondition.HEALTHY, 3, 25.5
        );

        manager.addAnimalToSector("Psy", animal);

        // Zapis do tymczasowego pliku JSON
        Path file = tempDir.resolve("shelter-test.json");

        ShelterFileRepository repository =
                new ShelterFileRepository();

        repository.saveToFile(manager, file);

        // Odczyt danych z pliku
        ShelterManager loadedManager =
                repository.loadFromFile(file);

        // Sprawdzenie sektorów
        assertEquals(
                1,
                loadedManager.getAllSectors().size()
        );

        ShelterSector loadedSector =
                loadedManager.getAllSectors().iterator().next();

        assertEquals("Psy", loadedSector.getSectorName());
        assertEquals(5, loadedSector.getMaxCapacity());

        // Sprawdzenie zwierząt
        assertEquals(1, loadedSector.getAnimals().size());

        Animal loadedAnimal =
                loadedSector.getAnimals().get(0);

        assertEquals("Max", loadedAnimal.getName());
        assertEquals("Pies", loadedAnimal.getSpecies());
        assertEquals("Labrador", loadedAnimal.getBreed());
        assertEquals(AnimalCondition.HEALTHY,
                loadedAnimal.getCondition());
        assertEquals(3, loadedAnimal.getAge());
        assertEquals(25.5, loadedAnimal.getWeight(), 0.001);

        // ID musi pozostać takie samo
        assertEquals(animal.getId(), loadedAnimal.getId());
    }

    @Test
    void shouldRejectInvalidJsonFile() throws IOException {

        // Tworzymy plik z nieprawidłową składnią JSON
        Path file = tempDir.resolve("invalid-shelter.json");

        Files.writeString(
                file,
                "{ invalid json !!!"
        );

        ShelterFileRepository repository =
                new ShelterFileRepository();

        // Odczyt powinien zakończyć się wyjątkiem
        assertThrows(IOException.class, () ->
                repository.loadFromFile(file)
        );
    }

    @Test
    void shouldAssignUniqueIdAfterLoadingJson() throws Exception {

        // Tworzymy schronisko i dodajemy pierwsze zwierzę
        ShelterManager manager = new ShelterManager();
        manager.addSector("Psy", 5);

        Animal max = new Animal(
                "Max", "Pies", "Labrador",
                AnimalCondition.HEALTHY, 3, 25.5
        );

        manager.addAnimalToSector("Psy", max);

        String originalId = max.getId();

        // Zapisujemy dane do pliku
        Path file = tempDir.resolve("shelter-ids.json");

        ShelterFileRepository repository =
                new ShelterFileRepository();

        repository.saveToFile(manager, file);

        // Wczytujemy schronisko ponownie
        ShelterManager loadedManager =
                repository.loadFromFile(file);

        // Dodajemy nowe zwierzę po odczycie
        Animal azor = new Animal(
                "Azor", "Pies", "Owczarek",
                AnimalCondition.HEALTHY, 5, 30.0
        );

        loadedManager.addAnimalToSector("Psy", azor);

        // Sprawdzamy unikalność identyfikatorów
        assertNotNull(azor.getId());
        assertNotEquals(originalId, azor.getId());

        ShelterSector sector = loadedManager.getAllSectors()
                .stream()
                .filter(s -> s.getSectorName().equals("Psy"))
                .findFirst()
                .orElseThrow();

        assertEquals(2, sector.getAnimals().size());

        // Sprawdzamy, czy oba identyfikatory są unikalne
        long uniqueIds = sector.getAnimals()
                .stream()
                .map(Animal::getId)
                .distinct()
                .count();

        assertEquals(2, uniqueIds);
    }

}
