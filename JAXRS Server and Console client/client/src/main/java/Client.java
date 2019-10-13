import org.glassfish.jersey.client.ClientConfig;
import rest.service.model.*;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    private static Scanner s = new Scanner(System.in);
    private static ClientConfig config;
    private static javax.ws.rs.client.Client client;
    private static URI baseUri;
    private static WebTarget serviceTarget;
    private static Invocation.Builder requestBuilder;
    private static Response response;
    private static boolean mainShutdown, cmsShutdown, gmsShutdown, webshopShutdown;
    private static String cmsHost, gmsHost, webshopHost;

    public static void main(String[] args){
        mainShutdown = false;
        cmsHost = "http://localhost:8080/store/rest/customers";
        gmsHost = "http://localhost:8080/store/rest/games";
        webshopHost = "http://localhost:8080/store/rest/webshop";

        printMainInstructions();

        while (!mainShutdown){
            System.out.print("Your selection: ");
            int userSelection = s.nextInt();
            s.nextLine();

            switch (userSelection){
                case 0:
                    System.out.println("Bye and have a nice day!!");
                    mainShutdown = true;
                    break;

                case 1:
                    printMainInstructions();
                    break;

                case 2:
                    enterCMS();
                    break;

                case 3:
                    enterGMS();
                    break;

                case 4:
                    enterCompositeService();
                    break;

            }
        }

    }

    //region Composite Service

    private static void enterCompositeService() {
        webshopShutdown = false;
        printCompositeInstructions();
        webshopHost = "http://localhost:8080/store/rest/webshop";


        while(!webshopShutdown){
            System.out.print("Your selection: ");
            int userWebshopSelection = s.nextInt();
            s.nextLine();

            switch (userWebshopSelection){
                case 0:
                    System.out.println("Back to the main screen...");
                    printMainInstructions();
                    webshopShutdown = true;
                    break;

                case 1:
                    printCompositeInstructions();
                    break;

                case 2:
                    sellGameToCustomer();
                    break;

                case 3:
                    addBalance();
                    break;
            }
        }
    }

    private static void addBalance() {
        viewAllCustomers();
        setupConfig(webshopHost);

        System.out.println("Deposit regulations: " +
                "\n\t- The deposit amount will be increased by 20% if the customer's game library costs more than 50 euros." +
                "\n\t- The deposit amount will be increased by 10% if the customer's game library costs more than 25 euros." +
                "\n\t- The deposit amount stays the same for all other cases!!");
        System.out.println();
        System.out.print("Enter the id of the customer: ");
        int customerId = s.nextInt();
        s.nextLine();

        System.out.print("Enter the amount: ");
        double amount = s.nextDouble();
        s.nextLine();

        Form depositForm = new Form();
        depositForm.param("cusId", Integer.toString(customerId));
        depositForm.param("amount", Double.toString(amount));

        Entity<Form> entity = Entity.entity(depositForm, MediaType.APPLICATION_FORM_URLENCODED);
        response = serviceTarget.path("add-balance").request().accept(MediaType.TEXT_PLAIN).post(entity);

        if (response.getStatus() == Response.Status.OK.getStatusCode()){
            System.out.println("The balance of customer with id " + customerId +
                                " increased by " + "\u20ac" + round(response.readEntity(Double.class), 2));
            System.out.println();
        } else {
            handleError(response);
        }
    }

    private static void sellGameToCustomer() {
        viewAllCustomers();
        viewAllGames();
        setupConfig(webshopHost);

        System.out.print("Enter the id of the customer: ");
        int cusId = s.nextInt();
        s.nextLine();

        System.out.print("Enter the id of the game: ");
        int gameId = s.nextInt();
        s.nextLine();

        requestBuilder = serviceTarget.path("sell-game").queryParam("cusId", cusId).queryParam("gameId", gameId).request().accept(MediaType.APPLICATION_JSON);
        response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()){
            System.out.println();
            System.out.println("Successfully sold a game for the customer!");
            System.out.println();
        } else {
            handleError(response);
        }
    }

    //endregion

    //region Game Management Service methods

    private static void enterGMS() {
        gmsShutdown = false;
        setupConfig(gmsHost);
        printGMSInstructions();

        while(!gmsShutdown){
            System.out.print("Your selection: ");
            int userGSMSelection = s.nextInt();
            s.nextLine();

            switch (userGSMSelection){
                case 0:
                    System.out.println("Back to the main screen...");
                    printMainInstructions();
                    gmsShutdown = true;
                    break;

                case 1:
                    printGMSInstructions();
                    break;

                case 2:
                    viewAllGames();
                    break;

                case 3:
                    addGame();
                    break;

                case 4:
                    findGame();
                    break;

                case 5:
                    updateGame();
                    break;

                case 6:
                    deleteGame();
                    break;
            }
        }
    }

    private static void deleteGame() {
        viewAllGames();

        System.out.print("Enter the id of the game you want to delete: ");
        int idDelete = s.nextInt();
        s.nextLine();

        requestBuilder = serviceTarget.path(Integer.toString(idDelete)).request().accept(MediaType.APPLICATION_JSON);
        response = requestBuilder.delete();

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            System.out.println();
            System.out.println("Successfully deleted game with id " + idDelete);
            System.out.println();
        } else {
            handleError(response);
        }
    }

    private static void updateGame() {
        viewAllGames();

        System.out.print("Enter the id of the game you want to edit: ");
        int gameId = s.nextInt();
        s.nextLine();

        System.out.print("Enter the new name: ");
        String newGameName = s.nextLine();

        System.out.print("Enter the new price: ");
        double newPrice = s.nextDouble();
        s.nextLine();

        System.out.print("Select the new genre: ");
        GameGenre newGenre = getGameGenreFromUser(newGameName);

        Game toBeUpdated = new Game(newPrice, newGameName, newGenre);
        toBeUpdated.setId(gameId);

        Entity<Game> entity = Entity.entity(toBeUpdated, MediaType.APPLICATION_JSON);
        response = serviceTarget.request().accept(MediaType.TEXT_PLAIN).put(entity);

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            System.out.println();
            System.out.println("Successfully updated the game with id " + gameId + ", name " +
                    newGameName + " for " + round(newPrice,2) + " with genre " + newGenre.toString());
            System.out.println();
        } else {
            handleError(response);
        }
    }

    private static void findGame() {
        setupConfig(gmsHost);
        System.out.print("Enter the id of the game: ");
        int gameId = s.nextInt();
        s.nextLine();

        requestBuilder = serviceTarget.queryParam("id", gameId).request().accept(MediaType.APPLICATION_JSON);
        response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()){
            Game gameFound = response.readEntity(Game.class);
            System.out.println();
            System.out.println("Information for game with id: " + gameFound.getId());
            System.out.println();
            System.out.println("Game name: " + gameFound.getName());
            System.out.println("Game price: " + "\u20ac" + round(gameFound.getPrice(),2));
            System.out.println("Genre: " + gameFound.getGenre().toString());
            System.out.println();
        } else {
            handleError(response);
        }
    }

    private static void addGame() {
        setupConfig(gmsHost);

        System.out.print("Enter the new game name: ");
        String name = s.nextLine();

        System.out.print("Enter the price of " + name + ": ");
        double price = s.nextDouble();
        s.nextLine();

        System.out.print("Select the genre of " + name + ": ");
        GameGenre gameGenre = getGameGenreFromUser(name);

        Form addGame = new Form();
        addGame.param("name", name);
        addGame.param("price", Double.toString(price));
        addGame.param("genre", gameGenre.toString());

        Entity<Form> entity = Entity.entity(addGame, MediaType.APPLICATION_FORM_URLENCODED);
        response = serviceTarget.request().accept(MediaType.TEXT_PLAIN).post(entity);

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            System.out.println();
            System.out.println("Successfully added new game with name " + name + " for " + round(price,2) + " euros");
            System.out.println();
        } else {
            handleError(response);
        }
    }

    private static GameGenre getGameGenreFromUser(String gameName) {
        System.out.println("Select the genre of " + gameName + ": ");
        System.out.println("1 - Action");
        System.out.println("2 - Adventure");
        System.out.println("3 - Roleplaying");
        System.out.println("4 - Simulation");
        System.out.println("5 - Strategy");
        System.out.println("6 - Scientific");
        System.out.println();
        System.out.print("Your selection: ");

        int selection = s.nextInt();
        s.nextLine();
        GameGenre result = null;

        switch (selection){
            case 1:
                result = GameGenre.ACTION;
                break;

            case 2:
                result = GameGenre.ADVENTURE;
                break;

            case 3:
                result = GameGenre.ROLEPLAYING;
                break;

            case 4:
                result = GameGenre.SIMULATION;
                break;

            case 5:
                result = GameGenre.STRATEGY;
                break;

            case 6:
                result = GameGenre.SCIENTIFIC;
                break;
        }

        return result;
    }

    private static void viewAllGames() {
        setupConfig(gmsHost);

         requestBuilder = serviceTarget.path("available-games").request().accept(MediaType.APPLICATION_JSON);
         response = requestBuilder.get();

         if (response.getStatus() == Response.Status.OK.getStatusCode()){
             GenericType<ArrayList<Game>> genericType = new GenericType<>() {};
             ArrayList<Game> entity = response.readEntity(genericType);

             System.out.println("All available games: ");
             System.out.println();
             System.out.println("-----------------------------------------------------------------------------");
             System.out.printf("%10s %30s %20s %5s", "GAME ID", "NAME", "GENRE", "PRICE (EURO)");
             System.out.println();
             System.out.println("-----------------------------------------------------------------------------");
             for (Game g :
                     entity) {
                 System.out.format("%10s %30s %20s %5s",
                         g.getId(), g.getName(), g.getGenre().toString(), Double.toString(round(g.getPrice(),2)));
                 System.out.println();
             }
             System.out.println();
         } else {
             handleError(response);
         }
    }

    //endregion

    //region Print instructions

    private static void printCMSInstructions(){
        System.out.println();
        System.out.println();
        System.out.println("Customer Management Service" +
                "\n==============================" +
                "\nPlease select an option:\n");
        System.out.println("1 - View instructions");
        System.out.println("2 - View all available customers");
        System.out.println("3 - Create a new customer");
        System.out.println("4 - Find a customer by id");
        System.out.println("5 - Update a customer");
        System.out.println("6 - Delete a customer");
        System.out.println("0 - Back to main menu");
        System.out.println();
    }

    private static void printGMSInstructions() {
        System.out.println();
        System.out.println();
        System.out.println("Game Management Service" +
                "\n==============================" +
                "\nPlease select an option:\n");
        System.out.println("1 - View instructions");
        System.out.println("2 - View all available games in the store");
        System.out.println("3 - Add a new game");
        System.out.println("4 - Find a game by id");
        System.out.println("5 - Update a game");
        System.out.println("6 - Delete a game");
        System.out.println("0 - Back to main menu");
        System.out.println();
    }

    private static void printMainInstructions(){
        System.out.println();
        System.out.println();
        System.out.println("Welcome to the Game Store application" +
                "\n==============================" +
                "\nPlease select an option:\n");
        System.out.println("1 - View instructions");
        System.out.println("2 - Access Customer Management Service (Single service)");
        System.out.println("3 - Access Game Management Service (Single service)");
        System.out.println("4 - Access Webshopt Service (Composite service)");
        System.out.println("0 - Quit");
        System.out.println();
    }

    private static void printCompositeInstructions(){
        System.out.println();
        System.out.println();
        System.out.println("Welcome to the Webshop service (Composite)" +
                "\n==============================" +
                "\nPlease select an option:\n");
        System.out.println("1 - View instructions");
        System.out.println("2 - Sell a game to a customer");
        System.out.println("3 - Add balance to a customer");
        System.out.println("0 - Back to main menu");
        System.out.println();
    }

    //endregion

    //region Customer Management Service methods

    private static void enterCMS() {

        cmsShutdown = false;
        printCMSInstructions();

        while (!cmsShutdown){
            System.out.print("Your selection: ");
            int userCSMSelection = s.nextInt();
            s.nextLine();

            switch (userCSMSelection){
                case 0:
                    System.out.println("Back to the main screen...");
                    printMainInstructions();
                    cmsShutdown = true;
                    break;

                case 1:
                    printCMSInstructions();
                    break;

                case 2:
                    viewAllCustomers();
                    break;

                case 3:
                    createCustomer();
                    break;

                case 4:
                    findCustomer();
                    break;

                case 5:
                    updateCustomer();
                    break;

                case 6:
                    deleteCustomer();
                    break;
            }
        }
    }

    private static void deleteCustomer() {
        viewAllCustomers();

        System.out.print("Enter the id of the customer you want to delete: ");
        String idDelete = s.nextLine();

        requestBuilder = serviceTarget.path(idDelete).request().accept(MediaType.APPLICATION_JSON);
        response = requestBuilder.delete();

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            System.out.println();
            System.out.println("Successfully deleted customer with id " + idDelete);
            System.out.println();
        } else {
            handleError(response);
        }
    }

    private static void updateCustomer() {
        viewAllCustomers();

        System.out.print("Enter the id of the customer you want to edit: ");
        int id = s.nextInt();
        s.nextLine();

        requestBuilder = serviceTarget.queryParam("id", id).request().accept(MediaType.APPLICATION_JSON);
        response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()){
            Customer toBeUpdated = response.readEntity(Customer.class);

            System.out.print("Enter the new name for the customer: ");
            String newName = s.nextLine();

            System.out.print("Enter the new email for the customer: ");
            String newEmail = s.nextLine();

            toBeUpdated.setName(newName);
            toBeUpdated.setEmail(newEmail);

            Entity<Customer> entity = Entity.entity(toBeUpdated, MediaType.APPLICATION_JSON);
            response = serviceTarget.request().accept(MediaType.TEXT_PLAIN).put(entity);

            if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
                System.out.println();
                System.out.println("Successfully updated the customer with id " + id + ", name " + newName + ", email " + newEmail);
                System.out.println();
            } else {
                handleError(response);
            }
        } else {
            handleError(response);
        }
    }

    private static void findCustomer() {
        setupConfig(cmsHost);

        System.out.print("Enter the id of the customer: ");
        int cusId = s.nextInt();
        s.nextLine();

        requestBuilder = serviceTarget.queryParam("id", cusId).request().accept(MediaType.APPLICATION_JSON);
        response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()){
            Customer cusFound = response.readEntity(Customer.class);
            System.out.println();
            System.out.println("Information for customer with id: " + cusFound.getId());
            System.out.println();
            System.out.println("Customer name: " + cusFound.getName());
            System.out.println("Customer email: " + cusFound.getEmail());
            System.out.println("Current balance: " + "\u20ac" + cusFound.getBalance());
            System.out.println("Owned games: " + cusFound.getOwnedGames().size());
            if (cusFound.getOwnedGames().size() > 0){
                printOwnedGames(cusFound.getOwnedGames());
            }
            System.out.println();
        } else {
            handleError(response);
        }
    }

    private static void printOwnedGames(List<Game> ownedGames) {
        for (int i = 0; i < ownedGames.size(); i++){
            System.out.println("\t" + (i+1) + ". " + ownedGames.get(i).getName() + " - " + ownedGames.get(i).getGenre());
        }
    }

    private static void createCustomer() {
        setupConfig(cmsHost);

        System.out.print("Enter the new customer name: ");
        String name = s.nextLine();

        System.out.print("Enter the new customer email: ");
        String email = s.nextLine();

        Form createCus = new Form();
        createCus.param("name", name);
        createCus.param("email", email);

        Entity<Form> entity = Entity.entity(createCus, MediaType.APPLICATION_FORM_URLENCODED);
        response = serviceTarget.request().accept(MediaType.TEXT_PLAIN).post(entity);

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            System.out.println("Successfully created new customer with email " + email);
            System.out.println();
        } else {
            handleError(response);
        }
    }

    private static void viewAllCustomers() {
        setupConfig(cmsHost);
        requestBuilder = serviceTarget.path("all").request().accept(MediaType.APPLICATION_JSON);
        response = requestBuilder.get();

        try {
            if (response.getStatus() == Response.Status.OK.getStatusCode()){
                GenericType<ArrayList<Customer>> genericType = new GenericType<>() {};
                ArrayList<Customer> entity = response.readEntity(genericType);

                System.out.println("All available customers: ");
                System.out.println();
                System.out.println("---------------------------------------------------------------------------------------");
                System.out.printf("%10s %15s %30s %10s %10s", "CUSTOMER ID", "NAME", "EMAIL", "BALANCE (EURO)", "OWNED GAMES");
                System.out.println();
                System.out.println("---------------------------------------------------------------------------------------");
                for (Customer c :
                        entity) {
                    System.out.format("%10s %15s %30s %10s %10s",
                            c.getId(), c.getName(), c.getEmail(), Double.toString(round(c.getBalance(),2)), Integer.toString(c.getOwnedGames().size()));
                    System.out.println();
                }

                System.out.println();
            } else {
                System.out.println("ERROR: Cannot get the generic collections of student class! " + response);
                GenericType<ArrayList<Customer>> genericType = new GenericType<>() {};
                ArrayList<Customer> entity = response.readEntity(genericType);
                System.out.println(entity);
            }
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    //endregion

    //region Helper methods

    private static void setupConfig(String serviceHost){
        config = new ClientConfig();
        client = ClientBuilder.newClient(config);
        baseUri = UriBuilder.fromUri(serviceHost).build();
        serviceTarget = client.target(baseUri);
    }

    private static void handleError(Response response) {
        System.out.println();
        System.out.println("Error: " + response.readEntity(String.class));
        System.out.println();
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //endregion
}
