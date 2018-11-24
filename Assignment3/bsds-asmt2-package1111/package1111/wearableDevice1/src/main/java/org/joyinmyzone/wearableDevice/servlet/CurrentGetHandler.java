package org.joyinmyzone.wearableDevice.servlet;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joyinmyzone.wearableDevice.dao.RecordDao;
import org.joyinmyzone.wearableDevice.dao.UserDailySummaryDao;

@Path("/GET/current/{userId}")
public class CurrentGetHandler {
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public int getCurrentDayStepsByUserId(@PathParam("userId") int userId) throws SQLException {
		UserDailySummaryDao userDailySummaryDao = new UserDailySummaryDao();
		int steps = userDailySummaryDao.getCurrentStepsByUserId(userId);
		return steps;
	}
}
