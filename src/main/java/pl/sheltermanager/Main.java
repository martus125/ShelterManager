
package pl.sheltermanager;

import pl.sheltermanager.model.Animal;
import pl.sheltermanager.model.AnimalCondition;
import pl.sheltermanager.service.ShelterManager;

public class Main {

    public static void main(String[] args) {

        ShelterManager manager = new ShelterManager();

        // 1. Tworzenie sektorów
        manager.addSector("Psy", 3);
        manager.addSector("Koty", 2);
        manager.addSector("Kwarantanna", 2);

        // 2. Tworzenie zwierząt
        Animal max = new Animal(
                "Max", "Pies", "Labrador",
                AnimalCondition.HEALTHY, 3, 25.5
        );

        Animal azor = new Animal(
                "Azor", "Pies", "Owczarek",
                AnimalCondition.SICK, 5, 30.0
        );

        Animal bella = new Animal(
                "Bella", "Kot", "Europejski",
                AnimalCondition.HEALTHY, 2, 4.2
        );

        // 3. Dodawanie zwierząt
        manager.addAnimalToSector("Psy", max);
        manager.addAnimalToSector("Psy", azor);
        manager.addAnimalToSector("Koty", bella);

        System.out.println("\n=== PODSUMOWANIE ===");
        manager.summary();

        // 4. Wyszukiwanie
        System.out.println("\n=== WYSZUKIWANIE ===");
        for (Animal animal : manager.getSector("Psy").searchPartial("pie")) {
            System.out.println(animal.getName());
        }

        // 5. Sortowanie
        System.out.println("\n=== SORTOWANIE WEDŁUG WAGI ===");
        for (Animal animal : manager.getSector("Psy").sortByWeight(false)) {
            System.out.println(
                    animal.getName() + " - " + animal.getWeight()
            );
        }

        // 6. Zmiana stanu zdrowia
        manager.getSector("Psy").changeCondition(
                azor, AnimalCondition.TREATMENT
        );

        // 7. Przenoszenie zwierzęcia
        manager.moveAnimalBetweenSectors(
                azor.getId(), "Psy", "Kwarantanna"
        );

        // 8. Liczenie zwierząt według stanu zdrowia
        System.out.println("\nZwierzęta w trakcie leczenia:");
        System.out.println(
                manager.getSector("Kwarantanna")
                        .countByCondition(AnimalCondition.TREATMENT)
        );

        // 9. Wyszukiwanie pustych sektorów
        System.out.println("\n=== PUSTE SEKTORY ===");
        for (var sector : manager.findEmpty()) {
            System.out.println(sector.getSectorName());
        }

        // 10. Podsumowanie końcowe
        System.out.println("\n=== WYNIK KOŃCOWY ===");
        manager.summary();

        System.out.println("\n=== TEST ZABEZPIECZENIA ID ===");

        Animal newDog = new Animal(
                "Reksio", "Pies", "Kundelek",
                AnimalCondition.HEALTHY, 2, 12.0
        );

        try {
            manager.getSector("Psy").addAnimal(newDog);
        } catch (IllegalArgumentException e) {
            System.out.println("BŁĄD: " + e.getMessage());
        }

// Poprawne dodanie przez managera
        manager.addAnimalToSector("Psy", newDog);

        System.out.println("ID Reksia: " + newDog.getId());

    }
}
