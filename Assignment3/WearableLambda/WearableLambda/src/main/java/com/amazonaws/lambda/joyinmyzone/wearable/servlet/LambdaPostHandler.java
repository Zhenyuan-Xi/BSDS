package com.amazonaws.lambda.joyinmyzone.wearable.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import com.amazonaws.lambda.joyinmyzone.wearable.dao.RecordDao;
import com.amazonaws.lambda.joyinmyzone.wearable.dao.UserDailySummaryDao;
import com.amazonaws.lambda.joyinmyzone.wearable.model.Record;
import com.amazonaws.lambda.joyinmyzone.wearable.model.UserDailySummary;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class LambdaPostHandler implements RequestStreamHandler {

	JSONParser parser = new JSONParser();
	
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
    	
    	LambdaLogger logger = context.getLogger();

   	 	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
   	 	JSONObject responseJson = new JSONObject();
   	 	
   	 	int userId = -1;
	 	int dayId = -1;
	 	int hourId = -1;
	 	int stepCount = -1;
	 	String responseCode = "200";
	 	 
	 	try {
	 		JSONObject event = (JSONObject) parser.parse(reader);
	 		if (event.get("pathParameters") != null) {
	 			JSONObject pps = (JSONObject)event.get("pathParameters");
	 			if ( pps.get("userId") != null) {
	                userId = Integer.parseInt((String)pps.get("userId"));
	            }
	            if ( pps.get("dayId") != null) {
	                dayId = Integer.parseInt((String)pps.get("dayId"));
	            }
	            if ( pps.get("hourId") != null) {
	                hourId = Integer.parseInt((String)pps.get("hourId"));
	            }
	            if ( pps.get("stepCount") != null) {
	                stepCount = Integer.parseInt((String)pps.get("stepCount"));
	            }
	 		}
	 		
	 		Record record = new Record(userId,dayId,hourId,stepCount);
			RecordDao recordDao = new RecordDao();
			Record resultRecord = recordDao.replace(record);
			UserDailySummaryDao userDailySummaryDao = new UserDailySummaryDao();
			UserDailySummary deltaDailySummary = userDailySummaryDao.getUserDailySummary(userId, dayId);
			deltaDailySummary.setSteps(deltaDailySummary.getSteps() + stepCount);
			userDailySummaryDao.replace(deltaDailySummary);
			
			responseJson.put("statusCode", responseCode);
			responseJson.put("body", resultRecord.toString());  
			
	 	} catch(ParseException pex) {
            responseJson.put("statusCode", "401"); // self defined error code
        } catch(SQLException sqle) {
        	responseJson.put("statusCode", "402"); // self defined error code
        }
	 	
	 	logger.log(responseJson.toJSONString());
	    OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
	    writer.write(responseJson.toJSONString());  
	    writer.close();
    }

}
