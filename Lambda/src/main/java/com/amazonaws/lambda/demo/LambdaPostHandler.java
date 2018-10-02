package com.amazonaws.lambda.demo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaPostHandler implements RequestHandler<String, Integer> {

	@Override
    public Integer handleRequest(String input, Context context) {
    	
        // TODO: implement your handler
        return input.length();
    }
}
