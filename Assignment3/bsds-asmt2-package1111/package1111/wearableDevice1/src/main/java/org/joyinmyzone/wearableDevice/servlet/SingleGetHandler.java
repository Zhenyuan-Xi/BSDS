package org.joyinmyzone.wearableDevice.servlet;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joyinmyzone.wearableDevice.dao.UserDailySummaryDao;

@Path("/GET/single/{userId}/{day}")
public class SingleGetHandler {
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public int getStepsByUserIdAndDayId(@PathParam("userId") int userId, @PathParam("day") int dayId) throws SQLException {
		UserDailySummaryDao userDailySummaryDao = new UserDailySummaryDao();
		int steps = userDailySummaryDao.getStepsByUserIdAndDayId(userId, dayId);
		return steps;
	}
	
}