package pl.sheltermanager.model;

import org.junit.jupiter.api.Test;
import pl.sheltermanager.service.ShelterManager;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
class ShelterSectorTest {

    @Test
    void shouldCreateSectorWithCorrectData() {

        ShelterSector sector = new ShelterSector("Psy", 5);

        assertEquals("Psy", sector.getSectorName());
        assertEquals(5, sector.getMaxCapacity());
        assertTrue(sector.getAnimals().isEmpty());
    }

    @Test
    void shouldAddAnimalToSector() {

        ShelterManager manager = new ShelterManager();
        manager.addSector("Psy", 5);

        Animal animal = new Animal(
                "Max", "Pies", "Labrador",
                AnimalCondition.HEALTHY, 3, 25.5
        );

        manager.addAnimalToSector("Psy", animal);

        ShelterSector sector = manager.getAllSectors()
                .stream()
                .filter(s -> s.getSectorName().equals("Psy"))
                .findFirst()
                .orElseThrow();

        assertEquals(1, sector.getAnimals().size());
        assertTrue(sector.getAnimals().contains(animal));
    }

    @Test
    void shouldRemoveAnimalFromSector() {

        ShelterManager manager = new ShelterManager();
        manager.addSector("Psy", 5);

        Animal animal = new Animal(
                "Max", "Pies", "Labrador",
                AnimalCondition.HEALTHY, 3, 25.5
        );

        manager.addAnimalToSector("Psy", animal);

        ShelterSector sector = manager.getAllSectors()
                .stream()
                .filter(s -> s.getSectorName().equals("Psy"))
                .findFirst()
                .orElseThrow();

        sector.removeAnimal(animal);

        assertTrue(sector.getAnimals().isEmpty());
    }

    @Test
    void shouldRejectAnimalWhenSectorIsFull() {

        ShelterManager manager = new ShelterManager();

        // Tworzymy sektor z miejscem tylko dla 1 zwierzęcia
        manager.addSector("Psy", 1);

        Animal firstAnimal = new Animal(
                "Max", "Pies", "Labrador",
                AnimalCondition.HEALTHY, 3, 25.5
        );

        Animal secondAnimal = new Animal(
                "Azor", "Pies", "Owczarek",
                AnimalCondition.HEALTHY, 5, 30.0
        );

        // Pierwsze zwierzę powinno zostać dodane
        manager.addAnimalToSector("Psy", firstAnimal);

        // Drugie zwierzę powinno zostać odrzucone
        assertThrows(IllegalStateException.class, () ->
                manager.addAnimalToSector("Psy", secondAnimal)
        );

        ShelterSector sector = manager.getAllSectors()
                .stream()
                .filter(s -> s.getSectorName().equals("Psy"))
                .findFirst()
                .orElseThrow();

        // Sprawdzamy, czy nadal jest tylko jedno zwierzę
        assertEquals(1, sector.getAnimals().size());
    }

    @Test
    void shouldSortAnimalsByName() {

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
                .iterator().next();

        List<Animal> sortedAnimals = sector.sortByName();

        assertEquals("Azor", sortedAnimals.get(0).getName());
        assertEquals("Max", sortedAnimals.get(1).getName());

    }

    @Test
    void shouldSortAnimalsByWeightDescending() {

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
                .iterator().next();

        List<Animal> sortedAnimals = sector.sortByWeight(false);

        assertEquals("Azor", sortedAnimals.get(0).getName());
        assertEquals("Max", sortedAnimals.get(1).getName());

    }

    @Test
    void shouldCountAnimalsByCondition() {

        ShelterManager manager = new ShelterManager();
        manager.addSector("Psy", 5);

        manager.addAnimalToSector("Psy", new Animal(
                "Max", "Pies", "Labrador",
                AnimalCondition.HEALTHY, 3, 25.5
        ));

        manager.addAnimalToSector("Psy", new Animal(
                "Azor", "Pies", "Owczarek",
                AnimalCondition.SICK, 5, 30.0
        ));

        manager.addAnimalToSector("Psy", new Animal(
                "Burek", "Pies", "Mieszaniec",
                AnimalCondition.HEALTHY, 2, 18.0
        ));

        ShelterSector sector = manager.getAllSectors()
                .iterator().next();

        assertEquals(
                2,
                sector.countByCondition(AnimalCondition.HEALTHY)
        );

        assertEquals(
                1,
                sector.countByCondition(AnimalCondition.SICK)
        );
    }

}
