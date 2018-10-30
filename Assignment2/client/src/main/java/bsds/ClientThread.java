package bsds;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ClientThread implements Runnable {

	private int iterations;
	private int requestsPerIteration;
	private CountDownLatch latch;
	private Client client;
	private WebTarget webTarget;
	private List<long[]> latencyIntervals;
	private int successfulRequsts;
	private final int phaseId;
	
	public ClientThread(int iterations, int hours, CountDownLatch latch, Client client, WebTarget webTarget, int phaseId) {
		this.iterations = iterations;
		this.requestsPerIteration = hours; 
		this.latch = latch;
		this.client = client;
		this.webTarget = webTarget;
		this.latencyIntervals = new LinkedList<>();
		this.phaseId = phaseId;
		this.successfulRequsts = 0;
	}

	public void run() {
		for (int i = 0 ; i < iterations ; i++) {
			for (int j = 0 ; j < requestsPerIteration ; j++) {
				for (int k = 0; k < 5; k++) {
					String[] path = generatePath(k);
					WebTarget requestEndpoint = webTarget.path(path[1]);
					Invocation.Builder invocationBuilder = requestEndpoint.request(MediaType.TEXT_PLAIN);
					Response response = null;
					long start = System.currentTimeMillis();
					if (path[0].equals("POST")) {
						 response = invocationBuilder.post(null);
					}
					if (path[0].equals("GET")) {
						response = invocationBuilder.get();
					}
					long end = System.currentTimeMillis();
					long[] latencyInterval = new long[2];
					latencyInterval[0] = start;
					latencyInterval[1] = end;
					this.latencyIntervals.add(latencyInterval);
					assert response != null;
					if (response.getStatus() == 200) {
						this.successfulRequsts++;
					}
					response.close();
				}
			}
		}
		ClientManager.appendLatencies(this.latencyIntervals,this.phaseId);
		ClientManager.successfulRequestCount.addAndGet(this.successfulRequsts);
		System.out.println("CurrentThread: " + latch.getCount());
		client.close();
		latch.countDown();
	}
	
	public String[] generatePath(int id) {
		String[] path = new String[2];
		Random random = new Random();
		switch (id) {
		case 0: 
			path[0] = "POST";
			int userId = random.nextInt(100000) + 1;
			int hour = random.nextInt(24);
			int steps = random.nextInt(5000);
			path[1] = "/" + userId + "/" + 1 + "/" + hour + "/" + steps;
			break;
		case 1:
			path[0] = "POST";
			int userId1 = random.nextInt(100000) + 1;
			int hour1 = random.nextInt(24);
			int steps1 = random.nextInt(5000);
			path[1] = "/" + userId1 + "/" + 1 + "/" + hour1 + "/" + steps1;
			break;
		case 2:
			path[0] = "GET";
			int userId2 = random.nextInt(100000) + 1;
			path[1] = "/current" + "/" + userId2;
			break;
		case 3:
			path[0] = "GET";
			int userId3 = random.nextInt(100000) + 1;
			path[1] = "/single" + "/" + userId3 + "/" + 1;
			break;
		case 4:
			path[0] = "POST";
			int userId4 = random.nextInt(100000) + 1;
			int hour4 = random.nextInt(24);
			int steps4 = random.nextInt(5000);
			path[1] = "/" + userId4 + "/" + 1 + "/" + hour4 + "/" + steps4;
			break;
		}
		return path;
	}
}
