package pl.psi.aaas;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DebugExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exc) {
        return Response.ok(exc.toString()).status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
