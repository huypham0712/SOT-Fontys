package rest.service.endpoint;
import rest.service.model.Customer;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("customers")
@Singleton
public class CustomersResource {
    private List<Customer> customers = new ArrayList<>();

    public CustomersResource() {
        initSampleData(customers);
    }

    //region Helper methods

    private void initSampleData(List<Customer> customers) {
        Customer tmp = new Customer("Huy Pham", "h.pham@student.fontys.nl");
        tmp.addBalance(70.23);
        customers.add(tmp);

        tmp = new Customer("Maria Vasileva", "m.vasileva@student.fontys.nl");
        tmp.addBalance(200.3);
        customers.add(tmp);

        tmp = new Customer("Andreea Moise", "a.moise@student.fontys.nl");
        tmp.addBalance(50.34);
        customers.add(tmp);

        tmp = new Customer("Eduard Oancea", "e.oancea@student.fontys.nl");
        tmp.addBalance(175.34);
        customers.add(tmp);
    }

    private Customer checkEmail(String email){
        for (Customer c :
                customers) {
            if (c.getEmail().equals(email))
                return c;
        }
        return null;
    }

    private Customer findCustomer(int id) {
        for (Customer c :
                customers) {
            if (c.getId() == id){
                return c;
            }
        }
        return null;
    }

    //endregion

    //region Get all the customers in the system at http://localhost:XXX/students/all

    @GET //GET at http://localhost:XXX/store/rest/customers/all
    @Path("all")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Customer> getAllCustomers(){
        return customers;
    }

    //endregion

    //region FORM PARAMETERS for creating new customer at http://localhost:XXX/store/rest/customers

    @POST //POST at http://localhost:XXX/store/rest/customers.
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createStudentForm(@FormParam("name") String name, @FormParam("email") String email) {
        if (checkEmail(email) != null){
            return Response.serverError().entity("Could not create new customer with email: " + email +
                    "\nReason: This email has been used!").build();
        }

        Customer toBeAdded = new Customer(name, email);
        customers.add(toBeAdded);
        return Response.noContent().build();
    }

    //endregion

    //region QUERY PARAMETER for searching for a customer at http://localhost:XXX/store/rest/customers?id=X where X is the id of the customer

    @GET //GET at http://localhost:XXX/store/rest/customers?id=X
    @Produces({MediaType.APPLICATION_JSON})
    public Response findCustomerById(@QueryParam("id") int customerId){
        for (Customer c :
                customers) {
            if (c.getId() == customerId){
                return Response.ok(c).build();
            }
        }

        return Response.serverError().entity("Could not find the customer with id: " + customerId +
                "\nReason: ID does not exist").build();
    }

    //endregion

    //region UPDATE customer at http://localhost:XXX/store/rest/customers

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomer(Customer newData){
        Customer oldData = findCustomer(newData.getId());
        if (oldData != null){
            oldData.setName(newData.getName());
            oldData.setEmail(newData.getEmail());
            oldData.setOwnedGames(newData.getOwnedGames());
            oldData.setBalance(newData.getBalance());
            return Response.noContent().build();
        } else {
            return Response.serverError().entity("Could not update the customer with id " + newData.getId() +
                                    "\nReason: Id not found!!").build();
        }
    }


    //endregion

    //region DELETE customer at http://localhost:XXX/store/rest/customers/X where X is the id of the customer

    @DELETE
    @Path("{id}")
    public Response deleteCustomerById(@PathParam("id") int id){
        Customer toBeDeleted = findCustomer(id);
        if (toBeDeleted != null){
            customers.remove(toBeDeleted);
            return Response.noContent().build();
        }
        return Response.serverError().entity("Could not remove the customer with id: " + id +
                                        "\nReason: ID does not exist").build();
    }

    //endregion
}
