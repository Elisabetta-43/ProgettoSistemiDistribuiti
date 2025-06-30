package it.unimib.sd2025.resources;

import java.util.HashMap;
import java.util.Map;

import it.unimib.sd2025.models.DBcomunication;
import it.unimib.sd2025.models.MessageDB;
import it.unimib.sd2025.models.User;
import it.unimib.sd2025.models.Voucher;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Rapresent the resource "example" in "http://localhost:8080/example".
 */
@Path("/user")
public class UserResource {

    private static DBcomunication conn = new DBcomunication();
    // Static block to initialize the database connection or any other static resources.
    // This block is executed once when the class is loaded.
    static {
        // ...
    }

    /**
     * GET "/example" implementation.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetUser(@QueryParam("id") String id) {
        String Query;
        MessageDB message;

        Query = conn.MakeQuery("RETRIEVE", id, null, null, null);
        message = conn.SendQuery(Query);
        if (message.getStatusCode().equals("500")) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(message.getMessage()).build(); 
        } else if (message.getStatusCode().equals("404")) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(message.getQueryResult()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response SignIn(User user) {
        String StringUser;

        user.setAdmin(false);
        user.setcontributions(500.0);
        user.setNextVoucherID(1);

        String[] keys = { "contributions", "fiscalCode", "name", "surname", "email", "NextVoucherID", "admin", "type" };
        Object[] values = { user.getcontributions(), user.getfiscalCode(), user.getname(), user.getsurname(),
                user.getEmail(), user.getNextVoucherID(), user.getAdmin(), "User" }; 
        StringUser = conn.MakeQuery("CREATE", "fiscalCode", keys, values, null);
        MessageDB message = conn.SendQuery(StringUser);

        if (message.getStatusCode().equals("500")) { //
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(message.getMessage()).build();
        } else if (message.getStatusCode().equals("400")) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(message.getMessage()).build();
        }
        return Response.ok(message.getQueryResult()).build();

    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetUserinfo(@PathParam("id") String id) {
        Map<String, Double> result = new HashMap<>();
        Jsonb jsonb = JsonbBuilder.create();
        String Query, resultString;
        Double consumedTotal = 0.0, unusedTotal = 0.0, total = 0.0;
        Voucher voucher;

        // Query creation to retrieve vouchers for the user with the given ID
        Query = conn.MakeQuery("RETRIEVE", null, null, null, "userId=" + id);
        MessageDB message = conn.SendQuery(Query);

        // Error handling based on the status code of the response
        if (message.getStatusCode().equals("500")) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(message.getMessage()).build();
        } else if (message.getStatusCode().equals("404")) {
            return Response.status(Status.NOT_FOUND).build();
        }

        try {
            // Response parsing and voucher processing
            String[] VoucherList = jsonb.fromJson(message.getQueryResult(), String[].class);
            for (String voucherString : VoucherList) {
                voucher = jsonb.fromJson(voucherString, Voucher.class);
                if (voucher.getstatus()) {
                    unusedTotal += voucher.getamount();
                } else {
                    consumedTotal += voucher.getamount();
                }
                total += voucher.getamount();
            }

            // Populating the result map with the aggregated values
            result.put("available", total);
            result.put("assigned", unusedTotal);
            result.put("spent", consumedTotal);

            // Map serialization to JSON string
            resultString = jsonb.toJson(result);
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("parsing error").build();
        }

        // Returning the response with the JSON string
        return Response.ok(resultString).build();
    }

    @Path("/{id}/VoucherList")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetVoucherList(@PathParam("id") String id) {
        String Query;
        MessageDB message;
 
        String cond = "userId="+id; 
        Query = conn.MakeQuery("RETRIEVE", null, null, null, cond);
        message = conn.SendQuery(Query);

        if (message.getStatusCode().equals("404")) {
            return Response.status(Status.NOT_FOUND).build();
        } else if (message.getStatusCode().equals("500"))
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(message.getMessage()).build();

        return Response.ok(message.getQueryResult()).build();
    }

    @Path("/user/admin")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetApplicationState() {
        String Query1, Query2, result;
        MessageDB message1, message2;
        String[] Userlist, Voucherlist;
        Map resultmap = new HashMap<String, Double>();
        Jsonb jsonb = JsonbBuilder.create();
        User user;
        Voucher voucher;
        Double totale = 0.0, totaleSpeso = 0.0, totalerimasto = 0.0;
        int totalebuoni = 0, buoniConsumati = 0, buoniValidi = 0;

        Query1 = conn.MakeQuery("RETRIEVE", null, null, null, "type=Voucher");
        message1 = conn.SendQuery(Query1);

        Query2 = conn.MakeQuery("RETRIEVE", null, null, null, "type=User");
        message2 = conn.SendQuery(Query2);
        
        if (message1.getStatusCode().equals("404") || message2.getStatusCode().equals("404")) {
            return Response.status(Status.NOT_FOUND).build();
        } else if (message1.getStatusCode().equals("500") || message1.getStatusCode().equals("500"))
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(message1.getMessage()+"|"+message2.getMessage()).build();

        try {
            Userlist = jsonb.fromJson(message2.getQueryResult(), String[].class);
            for (String Userstring : Userlist) {
                user = jsonb.fromJson(Userstring, User.class);
                totale = totale + 500.0;
                totalerimasto = totalerimasto + user.getcontributions();
            }
            totaleSpeso = totale - totalerimasto;

            Voucherlist = jsonb.fromJson(message2.getQueryResult(), String[].class);
            for (String Voucherstring : Voucherlist) {
                voucher = jsonb.fromJson(Voucherstring, Voucher.class);
                if (voucher.getstatus())
                    buoniValidi++;
                else
                    buoniConsumati++;
                totalebuoni++;
            }
            resultmap.put("numero utenti", 1.0 * Userlist.length);
            resultmap.put("totale", totale);
            resultmap.put("totale Speso", totaleSpeso);
            resultmap.put("totale caricato in buoni", totalerimasto);
            resultmap.put("buoni", 1.0 * totalebuoni);
            resultmap.put("buoni spesi", 1.0 * buoniConsumati);
            resultmap.put("buoni validi", 1.0 * buoniValidi);
            result = jsonb.toJson(resultmap);
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("parsing error").build();
        }

        return Response.ok(result).build();

    }

}