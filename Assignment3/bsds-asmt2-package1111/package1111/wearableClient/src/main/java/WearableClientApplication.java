import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class WearableClientApplication {
	
	private static final int DEFAULT_MAX_THREAD = 64;
	protected static final String SERVER_HOST_NAME = "localhost";
	protected static final String SERVER_AUTO_SCALING_GROUP_DNS = "bsds-asmt2-load-balaner-1518186170.us-west-2.elb.amazonaws.com";
	protected static final int PORT = 8080;
	protected static final String APPLICATION = "wearableDevice";
	static final int NUM_PHASES = 4;
	static final String[] PHASE_NAMES = {"WarmUp", "Loading", "Peak", "CoolDown"}; // name of each phase
	static final double[] PHASE_WEIGHTS = {0.1, 0.5, 1, 0.25};
	static final int DEFAULT_ITERATIONS = 100;
	static final int[] PHASE_LENGTHS = {3, 5, 11, 5};
	protected static List<List<long[]>> globalLatencyPairs;
	protected static long testStartTime = -1;
	protected static long testEndTime = -1;
	protected static AtomicInteger globalSuccessfulRequestCount;
	
    public static void main(String[] args) throws InterruptedException {
    	
    	System.out.println("[System] Welcome To Wearable Device Request Test");
    	
    	/**
    	 * TEST CONFIGURATION
    	 */
    	int maxThreads = DEFAULT_MAX_THREAD;
    	if (args[0] != null) {
    		maxThreads = Integer.parseInt(args[0]);
    	}
    	System.out.println("[System] You Set Max Threads As: " + maxThreads);
    	int iterations = DEFAULT_ITERATIONS;
    	if (args[1] != null) {
    		iterations = Integer.parseInt(args[1]);
    	}
    	System.out.println("[System] You Set Number of Iterations As: " + iterations);
    	// Initialize the latency pairs
    	globalLatencyPairs = new ArrayList<List<long[]>>(NUM_PHASES); 
    	for (int i = 0 ; i < NUM_PHASES; i++) {
    		globalLatencyPairs.add(new LinkedList<long[]>());
    	}
    	int totalThreads = (int)(maxThreads * PHASE_WEIGHTS[0]) + (int)( maxThreads * PHASE_WEIGHTS[1]) 
    					+ (int)( maxThreads * PHASE_WEIGHTS[2]) + (int)(maxThreads * PHASE_WEIGHTS[3]);  
    	System.out.println( "[System] We Will Run "+ totalThreads + " Threads In This Test.");
    	globalSuccessfulRequestCount = new AtomicInteger();
    	CountDownLatch mainLatch = new CountDownLatch(totalThreads);
    	
    	
    	/** TEST PHASES
    	 */
    	System.out.println("[System] Test Started");
    	testStartTime = System.currentTimeMillis();
    	
    	for (int i = 0 ; i < NUM_PHASES ; i++) {
    	
    		int numOfThreads =(int) (maxThreads * PHASE_WEIGHTS[i]);
    		System.out.printf("[System] %s phase started.\n", PHASE_NAMES[i] + " Number Of Threads: " + numOfThreads);
    		int phaseLength = PHASE_LENGTHS[i];
    		ThreadGroup group = new ThreadGroup(PHASE_NAMES[i]);
    		int numOfLaggerThreads = Math.min(10,  (int) (maxThreads * PHASE_WEIGHTS[i] * 0.10));
    		
    		for (int j = 0 ; j < numOfThreads ; j++) {
    				Client client = ClientBuilder.newClient();
    				WebTarget webTarget = client.target("http://" + WearableClientApplication.SERVER_HOST_NAME 
    															+ ":" + WearableClientApplication.PORT + "/" 
    					  										+ WearableClientApplication.APPLICATION + "/webapi");
    				Thread thread = new Thread(group,new RequestMaker(iterations,phaseLength,mainLatch,client,webTarget,i));
        			thread.start();
        			if (group.activeCount() > 180) {
        				// just wait
        			}
    		}
    		
    		while (group.activeCount() > numOfLaggerThreads) {
    			// just wait
    		}
    		
    	}
    	
    	mainLatch.await();
    	testEndTime = System.currentTimeMillis();
    	System.out.println("[System] Test Finished");
    	
    	
    	/** 
    	 * TEST REPORT 
    	 */
    	System.out.println("[System] Generating Test Report ......");
    	Reporter reporter = new Reporter(globalLatencyPairs, maxThreads);
    	reporter.doReport();
    	System.out.println("[System] Thank you! Bye!");
    }
    
    
    /**
     * Append the latency pairs of a thread to the global latency pairs
     */
    public static synchronized void appendLatencies(List<long[]> latencyPairs, int phaseId) {
		globalLatencyPairs.get(phaseId).addAll(latencyPairs);
	}
	
}
