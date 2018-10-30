package bsds;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import bsds.Data;
import bsds.DataDao;

@Path("/GET/single/{userId}/{day}")
class DayGetHandler {
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public int getStepsByUserIdAndDayId(@PathParam("userId") int userId, @PathParam("day") int day) throws SQLException {
		DataDao dataDao = DataDao.getInstance();
		int steps = dataDao.getStepCountByUserIdAndDay(userId, day);
		return steps;
	}
	
}

@Path("/GET/current/{userId}")
class GetHandler {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public int getCurrentDayStepsByUserId(@PathParam("userId") int userId) throws SQLException {
		DataDao dataDao = DataDao.getInstance();
		int steps = dataDao.getStepCountByUserId(userId);
		return steps;
	}
}

@Path("/POST/{userId}/{dayId}/{hourId}/{stepCount}")
class PostHandler {

  @POST
  @Produces(MediaType.TEXT_PLAIN)
  public String postRecord(
      @PathParam("userId") int userId,
      @PathParam("dayId") int day,
      @PathParam("hourId") int hour,
      @PathParam("stepCount") int stepCount) throws SQLException {

    Data data = new Data(userId,day,hour,stepCount);
    DataDao dataDao = DataDao.getInstance();
    Data resultData = dataDao.insert(data);
    return "Success Insert: " + resultData.toString();
  }
}

@Path("/GET/range/{userId}/{startDay}/{numDays}")
class RangeGetHandler {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public int getStepsByUserIdAndDayId(
      @PathParam("userId") int userId,
      @PathParam("startDay") int startDay,
      @PathParam("numDays") int numDays) throws SQLException {
    DataDao dataDao = DataDao.getInstance();
    int steps = dataDao.getStepCountByUserIdAndDayRange(userId, startDay, numDays);
    return steps;
  }

}