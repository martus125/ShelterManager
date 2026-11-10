package pl.sheltermanager.service; //przygotowanie listy zwierząt

import pl.sheltermanager.model.Animal;
import pl.sheltermanager.model.AnimalCondition;
import pl.sheltermanager.model.ShelterSector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AnimalQueryService {

    public List<Animal> getAnimals(
            ShelterSector sector,
            String searchText,
            AnimalCondition condition,
            String sortOption) {

        if (sector == null) {
            return new ArrayList<>();
        }

        List<Animal> animals;

        // Wyszukiwanie
        if (searchText != null && !searchText.isBlank()) {
            animals = new ArrayList<>(
                    sector.searchPartial(searchText)
            );
        } else {
            animals = new ArrayList<>(
                    sector.getAnimals()
            );
        }

        // Filtrowanie
        if (condition != null) {
            animals.removeIf(
                    animal -> animal.getCondition() != condition
            );
        }

        // Sortowanie
        if (sortOption != null) {

            switch (sortOption) {

                case "Imię A-Z":
                    animals.sort(
                            Comparator.comparing(
                                    Animal::getName,
                                    String.CASE_INSENSITIVE_ORDER
                            )
                    );
                    break;

                case "Imię Z-A":
                    animals.sort(
                            Comparator.comparing(
                                    Animal::getName,
                                    String.CASE_INSENSITIVE_ORDER
                            ).reversed()
                    );
                    break;

                case "Wiek rosnąco":
                    animals.sort(
                            Comparator.comparingInt(Animal::getAge)
                    );
                    break;

                case "Wiek malejąco":
                    animals.sort(
                            Comparator.comparingInt(
                                    Animal::getAge
                            ).reversed()
                    );
                    break;

                case "Waga rosnąco":
                    animals.sort(
                            Comparator.comparingDouble(
                                    Animal::getWeight
                            )
                    );
                    break;

                case "Waga malejąco":
                    animals.sort(
                            Comparator.comparingDouble(
                                    Animal::getWeight
                            ).reversed()
                    );
                    break;
            }
        }

        return animals;
    }
}
