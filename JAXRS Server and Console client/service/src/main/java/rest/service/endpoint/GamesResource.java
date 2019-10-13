package rest.service.endpoint;

import rest.service.model.Game;
import rest.service.model.GameGenre;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Path("games")
public class GamesResource {

    private List<Game> gameList = new ArrayList<>();

    public GamesResource() {
        initSampleData(gameList);
    }

    private void initSampleData(List<Game> gameList) {
        gameList.add(new Game(29.99, "Rise of the Tomb Rider", GameGenre.ADVENTURE));
        gameList.add(new Game(35.99, "CS: Global Offensive", GameGenre.ACTION));
        gameList.add(new Game(19.99, "Farming Simulator", GameGenre.SIMULATION));
        gameList.add(new Game(25.99, "World of Warcraft", GameGenre.ROLEPLAYING));
        gameList.add(new Game(37.99, "Transistor", GameGenre.STRATEGY));
        gameList.add(new Game(89.99, "Starcraft II", GameGenre.SCIENTIFIC));
    }

    //region CRUD operations on Game

    //Read operation
    @GET //GET at http://localhost:XXX/store/rest/games/available-games
    @Path("available-games")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Game> getAvailableGames(){
        return gameList;
    }

    //Read operation
    @GET //GET at http://localhost:XXX/store/rest/games?id=X
    @Produces({MediaType.APPLICATION_JSON})
    public Response findCustomerById(@QueryParam("id") int gameId){
        for (Game g :
                gameList) {
            if (g.getId() == gameId){
                return Response.ok(g).build();
            }
        }

        return Response.serverError().entity("Could not find the game with id: " + gameId +
                "\nReason: ID does not exist").build();
    }

    //Create operation
    @POST //POST at http://localhost:XXX/store/rest/games.
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createStudentForm(@FormParam("name") String name, @FormParam("genre") GameGenre gameGenre, @FormParam("price") double price) {
        if (checkName(name) != null){
            return Response.serverError().entity("Could not add new game with name: " + name +
                    "\nReason: A game with this name already exists!").build();
        }

        Game toBeAdded = new Game(price, name, gameGenre);
        gameList.add(toBeAdded);
        return Response.noContent().build();
    }

    //Update operation
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomerNameAndEmail(Game newData){
        Game oldData = findGame(newData.getId());
        if (oldData != null){
            oldData.setName(newData.getName());
            oldData.setPrice(newData.getPrice());
            oldData.setGenre(newData.getGenre());
            return Response.noContent().build();
        } else {
            return Response.serverError().entity("Could not update the game with id " + newData.getId() +
                    "\nReason: Id not found!!").build();
        }
    }

    //Delete operation
    @DELETE
    @Path("{id}")
    public Response deleteCustomerById(@PathParam("id") int id){
        Game toBeDeleted = findGame(id);
        if (toBeDeleted != null){
            gameList.remove(toBeDeleted);
            return Response.noContent().build();
        }
        return Response.serverError().entity("Could not remove the game with id: " + id +
                "\nReason: ID does not exist").build();
    }

    //endregion

    //region Helper methods

    private Game findGame(int id) {
        for (Game g :
                gameList) {
            if (g.getId() == id){
                return g;
            }
        }
        return null;
    }

    private Game checkName(String gameName) {
        for (Game g :
                gameList) {
            if (g.getName().equals(gameName)) {
                return g;
            }
        }
        return null;
    }

    //endregion
}
