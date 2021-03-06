package rest.service.model;

//For game instance
public class Game {
    private static int idGenerator = 1000;
    private int id;
    private double price;
    private String name;
    private GameGenre genre;

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public GameGenre getGenre() {
        return genre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenre(GameGenre genre) {
        this.genre = genre;
    }

    //Default constructor for JSON
    public Game() { }

    public Game(double price, String name, GameGenre genre) {
        this.id = idGenerator++;
        this.price = price;
        this.name = name;
        this.genre = genre;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
