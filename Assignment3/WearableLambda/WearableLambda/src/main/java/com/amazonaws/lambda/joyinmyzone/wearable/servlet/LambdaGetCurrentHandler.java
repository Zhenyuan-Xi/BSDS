package com.amazonaws.lambda.joyinmyzone.wearable.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;

import com.amazonaws.lambda.joyinmyzone.wearable.dao.UserDailySummaryDao;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class LambdaGetCurrentHandler implements RequestStreamHandler {

	JSONParser parser = new JSONParser();
	
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

    	LambdaLogger logger = context.getLogger();

   	 	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
   	 	JSONObject responseJson = new JSONObject();
     
   	 	Integer userId = -1;
   	 	String responseCode = "200";
   	 	try {
	 		JSONObject event = (JSONObject) parser.parse(reader);
	 		if (event.get("pathParameters") != null) {
	 			JSONObject pps = (JSONObject)event.get("pathParameters");
	 			if (pps.get("userId") != null) {
	                userId = Integer.parseInt((String)pps.get("userId"));
	            }
	 		}
	 		
	 		UserDailySummaryDao userDailySummaryDao = new UserDailySummaryDao();
			Integer steps = userDailySummaryDao.getCurrentStepsByUserId(userId);
			
			responseJson.put("statusCode", responseCode);
			responseJson.put("body", steps.toString());  
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
