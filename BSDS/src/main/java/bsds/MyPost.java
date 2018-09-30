package bsds;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author stephenxi
 */
@Path("mypost")
public class MyPost {
  @POST
  @Consumes(MediaType.TEXT_PLAIN)
  public int postText(String content){
    return content.length();
  }
}
