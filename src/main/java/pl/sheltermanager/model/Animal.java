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
    public Animal(String name, String species,
                  String breed, AnimalCondition condition,
                  int age, double weight) {

        setName(name);
        setSpecies(species);
        setBreed(breed);
        setCondition(condition);
        setAge(age);
        setWeight(weight);
    }

// gettery i settery
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Imię zwierzęcia nie może być puste."
            );
        }
        this.name = name.trim();
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        if (species == null || species.isBlank()) {
            throw new IllegalArgumentException(
                    "Gatunek nie może być pusty."
            );
        }
        this.species = species.trim();
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        if (breed == null || breed.isBlank()) {
            throw new IllegalArgumentException(
                    "Rasa nie może być pusta."
            );
        }
        this.breed = breed.trim();
    }

    public AnimalCondition getCondition() {
        return condition;
    }

    public void setCondition(AnimalCondition condition) {
        if (condition == null) {
            throw new IllegalArgumentException(
                    "Stan zdrowia musi być określony."
            );
        }
        this.condition = condition;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException(
                    "Wiek nie może być ujemny."
            );
        }
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        if (!Double.isFinite(weight) || weight <= 0) {
            throw new IllegalArgumentException(
                    "Waga musi być dodatnią, skończoną liczbą."
            );
        }
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
//przypisaywanie id
    public void assignId(String id) {

        if (this.id != null) {
            throw new IllegalStateException(
                    "Zwierzę ma już przypisane ID."
            );
        }

        if (id == null || !id.matches("A\\d{3,}")) {
            throw new IllegalArgumentException(
                    "Niepoprawny format ID zwierzęcia."
            );
        }

        this.id = id;
    }


}
