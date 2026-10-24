package pl.sheltermanager.service;

import pl.sheltermanager.model.ShelterSector;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import pl.sheltermanager.model.Animal;

public class ShelterManager {

    private Map<String, ShelterSector> sectors;
    private int nextAnimalId = 1;
//konstruktor
    public ShelterManager() {
        sectors = new HashMap<>();
    }

//dodawanie sektorow
    public void addSector(String name, int maxCapacity) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Nazwa sektora nie może być pusta."
            );
        }

        name = name.trim();

        if (sectors.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Sektor o takiej nazwie już istnieje."
            );
        }

        ShelterSector sector = new ShelterSector(name, maxCapacity);

        sectors.put(name, sector);

        System.out.println("Dodano sektor: " + name);
    }
//usuwanie sektorow
    public void removeSector(String name) {

        ShelterSector sector = sectors.get(name);

        if (sector == null) {
            throw new IllegalArgumentException(
                    "Nie znaleziono sektora."
            );
        }

        if (!sector.getAnimals().isEmpty()) {
            throw new IllegalStateException(
                    "Nie można usunąć sektora, w którym są zwierzęta."
            );
        }

        sectors.remove(name);

        System.out.println("Usunięto sektor: " + name);
    }
//zwracanie pustych sektorow
    public List<ShelterSector> findEmpty() {

        List<ShelterSector> emptySectors = new ArrayList<>();

        for (ShelterSector sector : sectors.values()) {

            if (sector.getAnimals().isEmpty()) {
                emptySectors.add(sector);
            }
        }

        return emptySectors;
    }
//zwraca wszystkie sektory i procent zapelnienia
    public void summary() {

        System.out.println("PODSUMOWANIE SCHRONISKA");

        for (ShelterSector sector : sectors.values()) {

            int count = sector.getAnimals().size();

            double occupancy =
                    (double) count / sector.getMaxCapacity() * 100;

            System.out.printf(
                    "%s: %d/%d zwierząt (%.1f%%)%n",
                    sector.getSectorName(),
                    count,
                    sector.getMaxCapacity(),
                    occupancy
            );
        }
    }
//wyszukiwanie konkretnych sektorow
    public ShelterSector getSector(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Nazwa sektora nie może być pusta."
            );
        }

        ShelterSector sector = sectors.get(name.trim());

        if (sector == null) {
            throw new IllegalArgumentException(
                    "Nie znaleziono sektora: " + name
            );
        }

        return sector;
    }
//unikalne id dla kazdego zwierzecia
    public Animal findAnimalById(String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "ID zwierzęcia nie może być puste."
            );
        }

        for (ShelterSector sector : sectors.values()) {

            for (Animal animal : sector.getAnimals()) {

                if (animal.getId().equals(id)) {
                    return animal;
                }
            }
        }

        return null;
    }
//dodawanie zwierzat do sektora
    public void addAnimalToSector(String sectorName, Animal animal) {

        if (animal == null) {
            throw new IllegalArgumentException(
                    "Nie można dodać pustego zwierzęcia."
            );
        }

        ShelterSector sector = getSector(sectorName);

        if (animal.getId() != null) {
            throw new IllegalArgumentException(
                    "Zwierzę ma już przypisane ID."
            );
        }

        if (sector.getAnimals().size() >= sector.getMaxCapacity()) {
            throw new IllegalStateException(
                    "Brak wolnych miejsc w sektorze."
            );
        }

        String newId = String.format("A%03d", nextAnimalId);
//przed przydzieleniem ID sprawdzamy, czy taki identyfikator już istnieje - licznik zwiększamy po udanym dodaniu zwierzęcia
        while (findAnimalById(newId) != null) {
            nextAnimalId++;
            newId = String.format("A%03d", nextAnimalId);
        }

        animal.assignId(newId);
        sector.addAnimal(animal);

        nextAnimalId++;
    }
//przenoszenie zwierzat miedzy sektorami
    public void moveAnimalBetweenSectors(
            String animalId,
            String sourceSectorName,
            String targetSectorName
    ) {

        ShelterSector source = getSector(sourceSectorName);
        ShelterSector target = getSector(targetSectorName);

        Animal animal = source.getAnimals().stream()
                .filter(a -> a.getId().equals(animalId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nie znaleziono zwierzęcia w sektorze źródłowym."
                ));

        source.moveAnimal(animal, target);
    }



}
