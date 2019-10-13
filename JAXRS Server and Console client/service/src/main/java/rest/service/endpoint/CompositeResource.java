package rest.service.endpoint;

import org.glassfish.jersey.client.ClientConfig;
import rest.service.model.Customer;
import rest.service.model.Game;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Path("webshop")
public class CompositeResource {
    private ClientConfig config;
    private javax.ws.rs.client.Client client;
    private URI baseUri;
    private WebTarget serviceTarget;
    private Response response;
    private Invocation.Builder requestBuilder;
    private List<Customer> customers;
    private List<Game> games;
    private String serviceHost;


    public CompositeResource() {
        serviceHost = "http://localhost:8080/store/rest";
        setupConfig(serviceHost);
        customers = new ArrayList<>();
        games = new ArrayList<>();
    }

    //region Helper methods

    private double getLibraryValue(List<Game> ownedGames) {
        double libraryValue = 0;
        for (Game g :
                ownedGames) {
            libraryValue += g.getPrice();
        }
        return libraryValue;
    }

    private boolean updateData(){
        customers = updateCustomersResource();
        games = updateGamesResource();
        return (customers != null && games != null);
    }

    private List<Customer> updateCustomersResource() {
        requestBuilder = serviceTarget.path("customers/all").request().accept(MediaType.APPLICATION_JSON);
        response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()){
            GenericType<ArrayList<Customer>> genericType = new GenericType<>() {};
            return response.readEntity(genericType);
        } else {
            return null;
        }
    }

    private List<Game> updateGamesResource() {
        requestBuilder = serviceTarget.path("games/available-games").request().accept(MediaType.APPLICATION_JSON);
        response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()){
            GenericType<ArrayList<Game>> genericType = new GenericType<>() {};
            return response.readEntity(genericType);
        } else {
            return null;
        }
    }

    private Game findGameById(int gameId) {
        for (Game g :
                games){
            if (g.getId() == gameId)
                return g;
        }
        return null;
    }

    private void setupConfig(String serviceHost) {
        config = new ClientConfig();
        client = ClientBuilder.newClient(config);
        baseUri = UriBuilder.fromUri(serviceHost).build();
        serviceTarget = client.target(baseUri);
    }

    private Customer findCustomerById(int customerId) {
        for (Customer c :
                customers) {
            if (c.getId() == customerId)
                return c;
        }
        return null;
    }

    //endregion

    //region CRUD with Composite Service

    //Sell the game to a customer by the customer ID and game ID.
    @GET
    @Path("sell-game")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response sellGame(@QueryParam("cusId") int cusId, @QueryParam("gameId") int gameId){
        if (updateData()){
            Customer customer = findCustomerById(cusId);
            Game game = findGameById(gameId);
            if (customer == null || game == null || customer.getBalance() < game.getPrice()){
                return Response.serverError().entity("Could not process the game selling procedure" +
                        "\nReason: Customer/Game ID does not exist OR the customer balance is not enough!!").build();
            }

            if (!customer.buyGame(game)){
                return Response.serverError().entity("Could not process the game selling procedure" +
                                                "\nReason: " + "The current user with id " +
                                                customer.getId() + " already has this game: " + game.getName()).build();
            } else {
                Entity<Customer> entity = Entity.entity(customer, MediaType.APPLICATION_JSON);
                response = serviceTarget.path("customers").request().accept(MediaType.TEXT_PLAIN).put(entity);

                if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
                    return Response.ok().build();
                } else {
                    return Response.serverError().entity(response.readEntity(String.class)).build();
                }
            }
        }
        return Response.serverError().entity("Could not update the resources!!" +
                "\nError: Data were not updated!!").build();
    }

    @POST
    @Path("add-balance")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response addBalance(@FormParam("cusId") int cusId, @FormParam("amount") double amount){
        if (updateData()){
            Customer customer = findCustomerById(cusId);

            if (customer == null || amount <= 0){
                return Response.serverError().entity("Could not process the deposit procedure" +
                        "\nReason: The current user with id" + cusId + "does not exist OR the deposit amount is invalid!!").build();
            }

            double libraryValue = getLibraryValue(customer.getOwnedGames());
            if (libraryValue >= 50){
                amount *= 1.2;
            } else if (libraryValue >= 25 && libraryValue < 50){
                amount *= 1.1;
            }
            customer.addBalance(amount);

            Entity<Customer> entity = Entity.entity(customer, MediaType.APPLICATION_JSON);
            response = serviceTarget.path("customers").request().accept(MediaType.TEXT_PLAIN).put(entity);

            if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
                return Response.ok(amount).build();
            } else {
                return Response.serverError().entity(response.readEntity(String.class)).build();
            }
        }
        return Response.serverError().entity("Could not update the resources!!" +
                "\nError: Data were not updated!!").build();
    }

    //endregion

}
