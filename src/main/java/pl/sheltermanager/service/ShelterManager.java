package pl.sheltermanager.service;

import pl.sheltermanager.model.ShelterSector;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShelterManager {

    private Map<String, ShelterSector> sectors;

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

}
