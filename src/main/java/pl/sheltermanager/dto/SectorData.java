package pl.sheltermanager.dto;

import java.util.ArrayList;
import java.util.List;

public class SectorData {

    public String name;
    public int capacity;
    public List<AnimalData> animals = new ArrayList<>();

    public SectorData() {
    }

    public SectorData(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }
}
