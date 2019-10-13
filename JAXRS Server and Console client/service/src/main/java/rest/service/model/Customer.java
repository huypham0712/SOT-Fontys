package rest.service.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private static int idGenerator = 500;
    private int id;
    private String name;
    private String email;
    private double balance;
    private List<Game> ownedGames;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public double getBalance() {
        return balance;
    }

    public List<Game> getOwnedGames() {
        return ownedGames;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setOwnedGames(List<Game> ownedGames) {
        this.ownedGames = ownedGames;
    }

    //Default constructor for JSON
    public Customer() { }

    public Customer(String name, String email) {
        this.id = idGenerator++;
        this.name = name;
        this.email = email;
        this.balance = 0;
        this.ownedGames = new ArrayList<>();
    }

    public void addBalance(double amount){
        this.balance += amount;
    }

    public boolean buyGame(Game game){
        Game toBeAdded = findGame(game.getId());
        if(toBeAdded != null || this.balance < game.getPrice()){
            return false;
        }

        this.balance -= game.getPrice();
        ownedGames.add(game);
        return true;
    }

    public boolean sellGame(Game game){
        Game toBeSell = findGame(game.getId());
        if (toBeSell == null){
            return false;
        }

        this.balance += (toBeSell.getPrice()/2);
        this.ownedGames.remove(toBeSell);
        return true;
    }

    private Game findGame(int gameId){
        for (Game g :
                ownedGames) {
            if (g.getId() == gameId){
                return g;
            }
        }
        return null;
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
