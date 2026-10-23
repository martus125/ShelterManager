
package pl.sheltermanager;

import pl.sheltermanager.service.ShelterManager;

public class Main {

    public static void main(String[] args) {

        ShelterManager manager = new ShelterManager();

        manager.addSector("Sektor A", 10);
        manager.addSector("Sektor B", 5);
        manager.addSector("Kwarantanna", 3);

        manager.summary();

        System.out.println("\nPUSTE SEKTORY:");

        for (var sector : manager.findEmpty()) {
            System.out.println(sector.getSectorName());
        }

        manager.removeSector("Sektor B");

        System.out.println("\nPO USUNIĘCIU:");
        manager.summary();
    }
}
