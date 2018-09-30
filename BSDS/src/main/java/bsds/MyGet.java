package bsds;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author stephenxi
 */
@Path("myget")
public class MyGet {
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getStatus() {
    return "Got it!";
  }
}
