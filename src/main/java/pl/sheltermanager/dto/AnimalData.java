package pl.sheltermanager.dto;

public class AnimalData {

    public String id;
    public String name;
    public String species;
    public String breed;
    public String condition;
    public int age;
    public double weight;

    public AnimalData() {
    }

    public AnimalData(
            String id,
            String name,
            String species,
            String breed,
            String condition,
            int age,
            double weight) {

        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.condition = condition;
        this.age = age;
        this.weight = weight;
    }
}
