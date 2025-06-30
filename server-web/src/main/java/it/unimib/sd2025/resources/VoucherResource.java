package it.unimib.sd2025.resources;

import java.time.LocalDateTime;

import it.unimib.sd2025.models.DBcomunication;
import it.unimib.sd2025.models.MessageDB;
import it.unimib.sd2025.models.User;
import it.unimib.sd2025.models.Voucher;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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
@Path("/voucher")
public class VoucherResource {
    // Private static attributes
    private static DBcomunication conn = new DBcomunication();
    // Static block to initialize the connection
    static {
        // ...
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response CreateNewVoucher(Voucher voucher, @QueryParam("fiscalCode") String fiscalCode) { // synchronized
        String QueryRetrieve, QueryUpdate, QueryCreate, identifier;
        MessageDB message1, message2, message3;
        Jsonb jsonb = JsonbBuilder.create();
        User user;
        // prendo oggetto utente
        QueryRetrieve = conn.MakeQuery("RETRIEVE", fiscalCode, null, null, null);
        message1 = conn.SendQuery(QueryRetrieve);

        if (message1.getStatusCode().equals("500"))
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(message1.getMessage()).build();
        if (message1.getStatusCode().equals("404"))
            return Response.status(Status.NOT_FOUND).build();

        user = jsonb.fromJson(message1.getStatusCode(), User.class);
        if (voucher.getamount() < 0.0)
            return Response.status(Status.BAD_REQUEST).entity("amount value must be higher than 0")
                    .build();
        if (voucher.getamount() > user.getcontributions())
            return Response.status(Status.BAD_REQUEST)
                    .entity("amount value must be lower than " + user.getcontributions())
                    .build();
        // Update values in user
        String[] updateKeys = { "contributions, NextVoucherID" };
        Object[] updateValues = { user.getcontributions() - voucher.getamount(), user.getNextVoucherID() + 1 };
        QueryUpdate = conn.MakeQuery("UPDATE", fiscalCode, updateKeys, updateValues, null);
        message2 = conn.SendQuery(QueryUpdate);

        if (message2.getStatusCode().equals("500"))
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(message2.getMessage()).build();
        if (message2.getStatusCode().equals("404"))
            return Response.status(Status.NOT_FOUND).build();
        if (message2.getStatusCode().equals("400"))
            return Response.status(Status.BAD_REQUEST).entity(message2.getMessage()).build();
        // voucher creation
        identifier = fiscalCode + "/" + user.getNextVoucherID();
        String[] createkeys = { "Identificatore", "amount", "category", "status", "creationDate", "expirationDate",
                "userId", "type" };
        Object[] createValues = { identifier, voucher.getamount(), voucher.getcategory(), voucher.getstatus(),
                voucher.getcreationDate(), voucher.getexpirationDate(), fiscalCode , "Voucher"};
        QueryCreate = conn.MakeQuery("CREATE", identifier, createkeys, createValues, null);
        message3 = conn.SendQuery(QueryCreate);

        if (message3.getStatusCode().equals("500"))
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(message3.getMessage()).build();
        if (message3.getStatusCode().equals("400"))
            return Response.status(Status.BAD_REQUEST).entity(message3.getMessage()).build();
        return Response.ok(message3.getMessage()).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ConsumeVoucher(Voucher voucher) {
        String Query;
        MessageDB message;

        if (!voucher.getstatus())
            return Response.status(Status.BAD_REQUEST)
                    .entity("Voucher already expired").build();

        voucher.setstatus(false);
        LocalDateTime useDate = LocalDateTime.now();
        voucher.setexpirationDate(useDate.toString());
        String[] key = { "status", "expirationDate" };
        Object[] value = { voucher.getstatus(), voucher.getexpirationDate() };
        Query = conn.MakeQuery("UPDATE", voucher.getidVoucher(), key, value, null);
        message = conn.SendQuery(Query);

        if (message.getStatusCode().equals("404")) {
            return Response.status(Status.NOT_FOUND).build();
        } else if (message.getStatusCode().equals("400")) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(message.getMessage()).build();
        } else if (message.getStatusCode().equals("500"))
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(message.getMessage()).build();

        return Response.ok(message.getMessage()).build();
    }

    @Path("/{category}")
    @PUT 
    @Consumes(MediaType.APPLICATION_JSON)
    public Response UpdateVoucher(Voucher voucher, @PathParam("category") String category) {
        String Query;
        MessageDB message;

        if (!voucher.getstatus())
            return Response.status(Status.BAD_REQUEST)
                    .entity("Voucher already expired").build();

        voucher.setcategory(category);
        String[] key = { "category" }, value = { voucher.getcategory() };
        Query = conn.MakeQuery("UPDATE", voucher.getidVoucher(), key, value, null);
        message = conn.SendQuery(Query);

        if (message.getStatusCode().equals("404")) {
            return Response.status(Status.NOT_FOUND).build();
        } else if (message.getStatusCode().equals("400")) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(message.getMessage()).build();
        } else if (message.getStatusCode().equals("500"))
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(message.getMessage()).build();
        return Response.ok(message.getMessage()).build();

    }

    @DELETE // delete voucher
    @Consumes(MediaType.APPLICATION_JSON) // consumes JSON
    public Response deleteVoucher(Voucher voucher, @QueryParam("contributions") Double contributions) {
        String QueryDelete, QueryUpdate;
        MessageDB message, messageUpdate;
        if (!voucher.getstatus())
            return Response.status(Status.BAD_REQUEST)
                    .entity("Voucher already expired").build();
        String [] key = {"contributions"}; Object [] value = {contributions + voucher.getamount()};
        QueryUpdate = conn.MakeQuery("update", voucher.getuserId(), key, value, null);
        messageUpdate = conn.SendQuery(QueryUpdate);
        if (messageUpdate.getStatusCode().equals("404")) {
            return Response.status(Status.NOT_FOUND).build();
        } else if (messageUpdate.getStatusCode().equals("400")) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(messageUpdate.getMessage()).build();
        } else if (messageUpdate.getStatusCode().equals("500"))
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(messageUpdate.getMessage()).build();

        QueryDelete = conn.MakeQuery("DELETE", voucher.getidVoucher(), null, null, null);
        message = conn.SendQuery(QueryDelete);

        if (message.getStatusCode().equals("404")) {
            return Response.status(Status.NOT_FOUND).build();
        } else if (message.getStatusCode().equals("400")) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(message.getMessage()).build();
        } else if (message.getStatusCode().equals("500"))
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(message.getMessage()).build();
        return Response.ok(message.getMessage()).build();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetVoucherInfo(@PathParam("id") String id) {
        String Query;
        MessageDB message;

        Query = conn.MakeQuery("RETRIEVE", id, null, null, null);
        message = conn.SendQuery(Query);

        if (message.getStatusCode().equals("404")) {
            return Response.status(Status.NOT_FOUND).build();
        } else if (message.getStatusCode().equals("500"))
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(message.getMessage()).build();
        return Response.ok(message.getQueryResult()).build();
    }

}