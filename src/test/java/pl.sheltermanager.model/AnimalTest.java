import org.junit.jupiter.api.Test;
import pl.sheltermanager.model.Animal;
import pl.sheltermanager.model.AnimalCondition;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {

    @Test
    void shouldCreateAnimalWithCorrectData() {

        Animal animal = new Animal(
                "Max",
                "Pies",
                "Labrador",
                AnimalCondition.HEALTHY,
                3,
                25.5
        );

        assertEquals("Max", animal.getName());
        assertEquals("Pies", animal.getSpecies());
        assertEquals("Labrador", animal.getBreed());
        assertEquals(AnimalCondition.HEALTHY, animal.getCondition());
        assertEquals(3, animal.getAge());
        assertEquals(25.5, animal.getWeight(), 0.001);
    }

    @Test
    void shouldRejectNegativeAge() {
        assertThrows(IllegalArgumentException.class, () ->
                new Animal(
                        "Max", "Pies", "Labrador",
                        AnimalCondition.HEALTHY, -3, 25.5
                )
        );
    }

    @Test
    void shouldRejectNegativeWeight() {
        assertThrows(IllegalArgumentException.class, () ->
                new Animal(
                        "Max", "Pies", "Labrador",
                        AnimalCondition.HEALTHY, 3, -5.0
                )
        );
    }

    @Test
    void shouldRejectEmptyName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Animal(
                        "", "Pies", "Labrador",
                        AnimalCondition.HEALTHY, 3, 25.5
                )
        );
    }

}
