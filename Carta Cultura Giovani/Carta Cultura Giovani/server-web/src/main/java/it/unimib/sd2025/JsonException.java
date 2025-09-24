package it.unimib.sd2025;

import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * When an exception is thrown in Jersey, either by the developer (in the defined methods),
 * or by Jersey itself, custom mappers can be defined to map Java exceptions to HTTP responses.
 * 
 * JsonParsingException is invoked when there is a JSON deserialization error; instead of
 * returning status 500, it simply returns 400.
 * 
 * The "@Provider" annotation is used to automatically register the mapping in JAX-RS.
 */
@Provider
public class JsonException implements ExceptionMapper<JsonbException> {
  public Response toResponse(JsonbException ex) {
    return Response.status(Status.BAD_REQUEST).entity(ex.getMessage()).type("text/plain").build();
  }
}
