
package pl.sheltermanager.service;

import org.junit.jupiter.api.Test;
import pl.sheltermanager.model.Animal;
import pl.sheltermanager.model.AnimalCondition;
import pl.sheltermanager.model.ShelterSector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnimalQueryServiceTest {

    @Test
    void shouldFindAnimalByName() {

        ShelterManager manager = new ShelterManager();
        manager.addSector("Psy", 5);

        Animal max = new Animal(
                "Max", "Pies", "Labrador",
                AnimalCondition.HEALTHY, 3, 25.5
        );

        Animal azor = new Animal(
                "Azor", "Pies", "Owczarek",
                AnimalCondition.SICK, 5, 30.0
        );

        manager.addAnimalToSector("Psy", max);
        manager.addAnimalToSector("Psy", azor);

        ShelterSector sector = manager.getAllSectors()
                .stream()
                .filter(s -> s.getSectorName().equals("Psy"))
                .findFirst()
                .orElseThrow();

        AnimalQueryService queryService =
                new AnimalQueryService();

        List<Animal> result = queryService.getAnimals(
                sector,
                "Max",
                null,
                "Imię A-Z"
        );

        assertEquals(1, result.size());
        assertEquals("Max", result.get(0).getName());
    }

    @Test
    void shouldSortAnimalsByAgeAscending() {

        ShelterManager manager = new ShelterManager();
        manager.addSector("Psy", 5);

        Animal max = new Animal(
                "Max", "Pies", "Labrador",
                AnimalCondition.HEALTHY, 5, 25.5
        );

        Animal azor = new Animal(
                "Azor", "Pies", "Owczarek",
                AnimalCondition.SICK, 2, 30.0
        );

        Animal burek = new Animal(
                "Burek", "Pies", "Mieszaniec",
                AnimalCondition.HEALTHY, 8, 18.0
        );

        manager.addAnimalToSector("Psy", max);
        manager.addAnimalToSector("Psy", azor);
        manager.addAnimalToSector("Psy", burek);

        ShelterSector sector = manager.getAllSectors()
                .stream()
                .filter(s -> s.getSectorName().equals("Psy"))
                .findFirst()
                .orElseThrow();

        AnimalQueryService queryService = new AnimalQueryService();

        List<Animal> result = queryService.getAnimals(
                sector,
                "",
                null,
                "Wiek rosnąco"
        );

        assertEquals(3, result.size());
        assertEquals("Azor", result.get(0).getName());
        assertEquals("Max", result.get(1).getName());
        assertEquals("Burek", result.get(2).getName());
    }

    @Test
    void shouldFilterAnimalsByHealthCondition() {

        ShelterManager manager = new ShelterManager();
        manager.addSector("Psy", 5);

        Animal max = new Animal(
                "Max", "Pies", "Labrador",
                AnimalCondition.HEALTHY, 3, 25.5
        );

        Animal azor = new Animal(
                "Azor", "Pies", "Owczarek",
                AnimalCondition.SICK, 5, 30.0
        );

        Animal burek = new Animal(
                "Burek", "Pies", "Mieszaniec",
                AnimalCondition.HEALTHY, 2, 18.0
        );

        manager.addAnimalToSector("Psy", max);
        manager.addAnimalToSector("Psy", azor);
        manager.addAnimalToSector("Psy", burek);

        ShelterSector sector = manager.getAllSectors()
                .stream()
                .filter(s -> s.getSectorName().equals("Psy"))
                .findFirst()
                .orElseThrow();

        AnimalQueryService queryService =
                new AnimalQueryService();

        List<Animal> result = queryService.getAnimals(
                sector,
                "",
                AnimalCondition.HEALTHY,
                "Imię A-Z"
        );

        assertEquals(2, result.size());
        assertEquals("Burek", result.get(0).getName());
        assertEquals("Max", result.get(1).getName());

        assertTrue(result.stream().allMatch(
                animal -> animal.getCondition() == AnimalCondition.HEALTHY
        ));
    }

}
