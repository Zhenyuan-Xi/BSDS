package org.joyinmyzone.wearableDevice.servlet;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joyinmyzone.wearableDevice.dao.RecordDao;
import org.joyinmyzone.wearableDevice.dao.UserDailySummaryDao;

@Path("/GET/range/{userId}/{startDay}/{numDays}")
public class RangeGetHandler {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public int getStepsByUserIdAndDayId(
			@PathParam("userId") int userId, 
			@PathParam("startDay") int startDay,
			@PathParam("numDays") int numDays) throws SQLException {
		UserDailySummaryDao userDailySummaryDao = new UserDailySummaryDao();
		int steps = userDailySummaryDao.getStepsByUserIdAndDayRange(userId, startDay, numDays);
		return steps;
	}
	
}