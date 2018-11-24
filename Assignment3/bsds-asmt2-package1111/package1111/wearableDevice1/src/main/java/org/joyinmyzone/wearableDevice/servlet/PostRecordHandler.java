package org.joyinmyzone.wearableDevice.servlet;

import java.sql.SQLException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joyinmyzone.wearableDevice.dao.RecordDao;
import org.joyinmyzone.wearableDevice.dao.UserDailySummaryDao;
import org.joyinmyzone.wearableDevice.model.Record;
import org.joyinmyzone.wearableDevice.model.UserDailySummary;

@Path("/POST/{userId}/{dayId}/{hourId}/{stepCount}")
public class PostRecordHandler {
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
    public String postRecord(
    		@PathParam("userId") int userId,
    		@PathParam("dayId") int dayId,
    		@PathParam("hourId") int hourId,
    		@PathParam("stepCount") int stepCount) throws SQLException {	
		
		Record record = new Record(userId,dayId,hourId,stepCount);
		RecordDao recordDao = new RecordDao();
		Record resultRecord = recordDao.replace(record);
		UserDailySummaryDao userDailySummaryDao = new UserDailySummaryDao();
		UserDailySummary deltaDailySummary = userDailySummaryDao.getUserDailySummary(userId, dayId);
		deltaDailySummary.setSteps(deltaDailySummary.getSteps() + stepCount);
		userDailySummaryDao.replace(deltaDailySummary);
		
        return "Success Insert: " + resultRecord.toString();
    }
}
