package pl.sheltermanager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;

public class ShelterSector {

    private String sectorName;
    private int maxCapacity;
    private List<Animal> animals; //liczba zwierzat w sektorze

    //konstruktor tworzacy sektor
    public ShelterSector(String sectorName, int maxCapacity) {
        if (sectorName == null || sectorName.isBlank()) {
            throw new IllegalArgumentException(
                    "Nazwa sektora nie może być pusta."
            );
        }

        if (maxCapacity <= 0) {
            throw new IllegalArgumentException(
                    "Pojemność sektora musi być większa od zera."
            );
        }

        this.sectorName = sectorName;
        this.maxCapacity = maxCapacity;
        this.animals = new ArrayList<>(); //nowa lista dla nowego sektora
    }

    //gettery
    public String getSectorName() {
        return sectorName;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public List<Animal> getAnimals() {
        return List.copyOf(animals);
    }

    //dodawanie zwierzecia
    public void addAnimal(Animal animal) {

        if (animal == null) {
            throw new IllegalArgumentException(
                    "Nie można dodać pustego zwierzęcia."
            );
        }

        if (animal.getId() == null || animal.getId().isBlank()) {
            throw new IllegalArgumentException(
                    "Zwierzę musi mieć przypisane ID."
            );
        }

        for (Animal existing : animals) {
            if (existing.getId().equals(animal.getId())) {
                throw new IllegalArgumentException(
                        "Zwierzę o takim ID już istnieje w sektorze."
                );
            }
        }

        if (animals.size() >= maxCapacity) {
            throw new IllegalStateException(
                    "Brak wolnych miejsc w sektorze."
            );
        }

        animals.add(animal);

        System.out.println("Dodano zwierzę: " + animal.getName());
    }


    //usuwanie zwierzecia
    public void removeAnimal(Animal animal) {

        if (animal == null) {
            throw new IllegalArgumentException(
                    "Nie wybrano zwierzęcia do usunięcia."
            );
        }

        boolean removed = animals.removeIf(
                a -> a.getId().equals(animal.getId())
        );

        if (!removed) {
            throw new IllegalArgumentException(
                    "Nie znaleziono zwierzęcia w sektorze."
            );
        }

        System.out.println(
                "Usunięto zwierzę: " + animal.getName()
        );
    }

    //przenoszenie zwierzecia
    public void moveAnimal(Animal animal, ShelterSector targetSector) {

        if (targetSector == null) {
            throw new IllegalArgumentException(
                    "Nie wybrano sektora docelowego."
            );
        }

        if (targetSector == this) {
            throw new IllegalArgumentException(
                    "Zwierzę znajduje się już w tym sektorze."
            );
        }

        if (animal == null || animals.stream().noneMatch(
                a -> a.getId().equals(animal.getId())
        )) {
            throw new IllegalArgumentException(
                    "Zwierzę nie znajduje się w sektorze źródłowym."
            );
        }

        // Najpierw próbujemy dodać zwierzę do nowego sektora.
        targetSector.addAnimal(animal);

        // Usuwamy je ze starego sektora dopiero po udanym dodaniu.
        removeAnimal(animal);

        System.out.println(
                "Przeniesiono zwierzę do sektora: "
                        + targetSector.getSectorName()
        );

    }

    //zmiana stanu zdrowia
    public void changeCondition(
            Animal animal,
            AnimalCondition condition
    ) {

        if (animal == null || animals.stream().noneMatch(
                a -> a.getId().equals(animal.getId())
        )) {
            throw new IllegalArgumentException(
                    "Nie znaleziono zwierzęcia w sektorze."
            );
        }

        if (condition == null) {
            throw new IllegalArgumentException(
                    "Stan zdrowia nie może być pusty."
            );
        }

        animal.setCondition(condition);

        System.out.println(
                "Zmieniono stan zdrowia: " + animal.getName()
        );
    }

    //zmiana wieku zwierzecia
    public void changeAge(Animal animal, int age) {

        if (animal == null || animals.stream().noneMatch(
                a -> a.getId().equals(animal.getId())
        )) {
            throw new IllegalArgumentException(
                    "Nie znaleziono zwierzęcia w sektorze."
            );
        }

        if (age < 0) {
            throw new IllegalArgumentException(
                    "Wiek nie może być ujemny."
            );
        }

        animal.setAge(age);

        System.out.println(
                "Zmieniono wiek zwierzęcia: " + animal.getName()
        );
    }

    //szukanie po stanie zdrowia
    public int countByCondition(AnimalCondition condition) {

        if (condition == null) {
            throw new IllegalArgumentException(
                    "Stan zdrowia nie może być pusty."
            );
        }

        int count = 0;

        for (Animal animal : animals) {
            if (animal.getCondition() == condition) {
                count++;
            }
        }

        return count;
    }

    //szukanie po imieniu
    public List<Animal> sortByName() {

        List<Animal> sortedAnimals = new ArrayList<>(animals);
//zwraca posortowaną kopię listy nie zmienia oryginalnej kolejności zwierząt w sektorze
        sortedAnimals.sort(
                Comparator.comparing(
                        Animal::getName,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        return sortedAnimals;
    }
//szukanie po wadze

    public List<Animal> sortByWeight(boolean ascending) {

        List<Animal> sortedAnimals = new ArrayList<>(animals);

        Comparator<Animal> comparator =
                Comparator.comparingDouble(Animal::getWeight);

        if (!ascending) {
            comparator = comparator.reversed();
        }

        sortedAnimals.sort(comparator);

        return sortedAnimals;
    }

    //domyślnie sortowanie rosnące
    public List<Animal> sortByWeight() {
        return sortByWeight(true);
    }

//wyszukiwanie po dokladnym imieniu
    public List<Animal> search(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Podaj imię zwierzęcia."
            );
        }

        List<Animal> results = new ArrayList<>();

        for (Animal animal : animals) {
            if (animal.getName().equalsIgnoreCase(name.trim())) {
                results.add(animal);
            }
        }

        results.sort(Comparator.naturalOrder());

        return results;
    }
//wyszukiwanie po fragmencie tekstu
    public List<Animal> searchPartial(String text) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Podaj tekst do wyszukania."
            );
        }

        String searchText = text.trim().toLowerCase();

        List<Animal> results = new ArrayList<>();

        for (Animal animal : animals) {

            if (animal.getName().toLowerCase().contains(searchText)
                    || animal.getSpecies().toLowerCase().contains(searchText)) {

                results.add(animal);
            }
        }

        return results;
    }
//info o wszystkich zwierzeciach w danym sektorze
    public void summary() {

        System.out.println("SEKTOR: " + sectorName);
        System.out.println("Liczba zwierząt: " + animals.size());
        System.out.println("Maksymalna pojemność: " + maxCapacity);

        for (Animal animal : animals) {
            animal.print();
        }
    }
//zwierze najciezsze
    public Animal max() {

        if (animals.isEmpty()) {
            throw new IllegalStateException(
                    "Sektor jest pusty."
            );
        }

        return Collections.max(
                animals,
                Comparator.comparingDouble(Animal::getWeight)
        );
    }

}
