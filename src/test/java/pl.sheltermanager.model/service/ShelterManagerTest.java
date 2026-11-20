
package pl.sheltermanager.service;

import org.junit.jupiter.api.Test;
import pl.sheltermanager.model.Animal;
import pl.sheltermanager.model.AnimalCondition;
import pl.sheltermanager.model.ShelterSector;
import static org.junit.jupiter.api.Assertions.*;

class ShelterManagerTest {

    @Test
    void shouldAssignUniqueIdsToAnimals() {

        ShelterManager manager = new ShelterManager();
        manager.addSector("Psy", 5);

        Animal firstAnimal = new Animal(
                "Max", "Pies", "Labrador",
                AnimalCondition.HEALTHY, 3, 25.5
        );

        Animal secondAnimal = new Animal(
                "Azor", "Pies", "Owczarek",
                AnimalCondition.HEALTHY, 5, 30.0
        );

        manager.addAnimalToSector("Psy", firstAnimal);
        manager.addAnimalToSector("Psy", secondAnimal);

        assertNotNull(firstAnimal.getId());
        assertNotNull(secondAnimal.getId());

        assertNotEquals(
                firstAnimal.getId(),
                secondAnimal.getId()
        );
    }

    @Test
    void shouldMoveAnimalBetweenSectors() {

        ShelterManager manager = new ShelterManager();

        manager.addSector("Psy", 5);
        manager.addSector("Kwarantanna", 2);

        Animal animal = new Animal(
                "Max", "Pies", "Labrador",
                AnimalCondition.HEALTHY, 3, 25.5
        );

        // Dodajemy zwierzę do pierwszego sektora
        manager.addAnimalToSector("Psy", animal);

        // Przenosimy zwierzę
        manager.moveAnimalBetweenSectors(
                animal.getId(),
                "Psy",
                "Kwarantanna"
        );

        ShelterSector dogs = manager.getAllSectors()
                .stream()
                .filter(s -> s.getSectorName().equals("Psy"))
                .findFirst()
                .orElseThrow();

        ShelterSector quarantine = manager.getAllSectors()
                .stream()
                .filter(s -> s.getSectorName().equals("Kwarantanna"))
                .findFirst()
                .orElseThrow();

        assertTrue(dogs.getAnimals().isEmpty());
        assertEquals(1, quarantine.getAnimals().size());
        assertTrue(quarantine.getAnimals().contains(animal));
    }

    @Test
    void shouldNotMoveAnimalToFullSector() {

        ShelterManager manager = new ShelterManager();

        manager.addSector("Psy", 5);
        manager.addSector("Kwarantanna", 1);

        Animal max = new Animal(
                "Max", "Pies", "Labrador",
                AnimalCondition.HEALTHY, 3, 25.5
        );

        Animal azor = new Animal(
                "Azor", "Pies", "Owczarek",
                AnimalCondition.SICK, 5, 30.0
        );

        manager.addAnimalToSector("Psy", max);
        manager.addAnimalToSector("Kwarantanna", azor);

        // Próba przeniesienia Maxa do pełnego sektora
        assertThrows(IllegalStateException.class, () ->
                manager.moveAnimalBetweenSectors(
                        max.getId(),
                        "Psy",
                        "Kwarantanna"
                )
        );

        ShelterSector dogs = manager.getAllSectors()
                .stream()
                .filter(s -> s.getSectorName().equals("Psy"))
                .findFirst()
                .orElseThrow();

        ShelterSector quarantine = manager.getAllSectors()
                .stream()
                .filter(s -> s.getSectorName().equals("Kwarantanna"))
                .findFirst()
                .orElseThrow();

        // Max nadal jest w sektorze Psy
        assertEquals(1, dogs.getAnimals().size());
        assertTrue(dogs.getAnimals().contains(max));

        // Kwarantanna nadal zawiera tylko Azora
        assertEquals(1, quarantine.getAnimals().size());
        assertTrue(quarantine.getAnimals().contains(azor));
    }

}
