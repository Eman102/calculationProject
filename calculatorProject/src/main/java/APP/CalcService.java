package APP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Ejbs.Calculation;

@Stateless
@Path("/project")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CalcService {
    @PersistenceContext(unitName = "hello")
    private EntityManager entityManager;

    @POST
    @Path("calc")
    public Response performCalculation(Calculation calculation) {
        int result = 0;
        switch (calculation.getOperation()) {
            case "+":
                result = calculation.getNumber1() + calculation.getNumber2();
                break;
            case "-":
                result = calculation.getNumber1() - calculation.getNumber2();
                break;
            case "*":
                result = calculation.getNumber1() * calculation.getNumber2();
                break;
            case "/":
                if (calculation.getNumber2() == 0) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Division by zero is not allowed")
                            .build();
                }
                result = calculation.getNumber1() / calculation.getNumber2();
                break;
            default:
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Unsupported operation")
                        .build();
        }
        entityManager.persist(calculation);
        return Response.ok(result).build();
    }
 
    

    @GET
    @Path("calculation")
    public Response getCalculations() {
        try {
            String simpleQuery = "SELECT c FROM Calculation c";
            TypedQuery<Calculation> query = entityManager.createQuery(simpleQuery, Calculation.class);
            return Response.ok(query.getResultList()).build();
        } catch (RuntimeException err) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(err.getMessage())
                    .build();
        }
    }
}