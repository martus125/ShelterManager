package pl.sheltermanager.model;

public class Animal implements Comparable<Animal> {

    private String id;
    private String name;
    private String species;
    private String breed;
    private AnimalCondition condition;
    private int age;
    private double weight;

//konstruktor
    public Animal(String id, String name, String species,
                  String breed, AnimalCondition condition,
                  int age, double weight) {

        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.condition = condition;
        this.age = age;
        this.weight = weight;
    }
// gettery i settery
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public AnimalCondition getCondition() {
        return condition;
    }

    public void setCondition(AnimalCondition condition) {
        this.condition = condition;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
//wszystkie informacje o zwierzeciu
    public void print() {
        System.out.println("ID: " + id);
        System.out.println("Imię: " + name);
        System.out.println("Gatunek: " + species);
        System.out.println("Rasa: " + breed);
        System.out.println("Stan zdrowia: " + condition);
        System.out.println("Wiek: " + age + " lat");
        System.out.println("Waga: " + weight + " kg");
    }
//metoda zwracajaca kolejnosc zwierzat
    @Override
    public int compareTo(Animal other) {

        int nameComparison =
                this.name.compareToIgnoreCase(other.name);

        if (nameComparison != 0) {
            return nameComparison;
        }

        int speciesComparison =
                this.species.compareToIgnoreCase(other.species);

        if (speciesComparison != 0) {
            return speciesComparison;
        }

        return Integer.compare(this.age, other.age);
    }

}
