package BSDS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ClientManager {
  private int maxNumOfThreads;
  private int numOfIterPerThread;
  private String IP;
  private int port;
  protected static AtomicInteger requestSent = new AtomicInteger();
  protected static AtomicInteger responseReceived = new AtomicInteger();
  private static List<Long> latencies = new ArrayList<>();

  public ClientManager(int maxNumOfThreads, int numOfIterPerThread, String IP, int port) {
    this.maxNumOfThreads = maxNumOfThreads;
    this.numOfIterPerThread = numOfIterPerThread;
    this.IP = IP;
    this.port = port;
  }

  public int getMaxNumOfThreads() {
    return maxNumOfThreads;
  }

  public int getNumOfIterPerThread() {
    return numOfIterPerThread;
  }

  public String getIP() {
    return IP;
  }

  public int getPort() {
    return port;
  }

  public static synchronized void addLatency(Long latency) {
    ClientManager.latencies.add(latency);
  }

  public static void main(String[] args) throws InterruptedException {
    int maxNumOfThreads = Integer.valueOf(args[0]);
    int numOfIterPerThread = Integer.valueOf(args[1]);
    String IP = args[2].trim();
    int port = Integer.valueOf(args[3]);
    ClientManager clientManager = new ClientManager(maxNumOfThreads, numOfIterPerThread, IP, port);
    long startTime = System.currentTimeMillis();
    System.out.println("Client starting .....Time: " + startTime);

    // WARM UP
    int number1 = (int) (clientManager.getMaxNumOfThreads() * 0.1);
    CountDownLatch latch1 = new CountDownLatch(number1);
    for (int i = 0; i < number1; i++) {
      Thread thread = new Thread(new ClientThread(clientManager.getIP(), clientManager.getPort(), clientManager.getNumOfIterPerThread(), latch1));
      thread.start();
    }
    long startTime1 = System.currentTimeMillis();
    System.out.println("Warmup phase: All threads running ....");
    latch1.await();
    long endTime1 = System.currentTimeMillis();
    System.out.println("Warmup phase complete: Time " + (endTime1 - startTime1) / 1000 + " seconds");

    // LOADING
    int number2 = (int) (clientManager.getMaxNumOfThreads() * 0.5);
    CountDownLatch latch2 = new CountDownLatch(number2);
    for (int i = 0; i < number2; i++) {
      Thread thread = new Thread(new ClientThread(clientManager.getIP(), clientManager.getPort(), clientManager.getNumOfIterPerThread(), latch2));
      thread.start();
    }
    long startTime2 = System.currentTimeMillis();
    System.out.println("Loading phase: All threads running ....");
    latch2.await();
    long endTime2 = System.currentTimeMillis();
    System.out.println("Loading phase complete: Time " + (endTime2 - startTime2) / 1000 + " seconds");

    // PEAK
    int number3 = clientManager.getMaxNumOfThreads();
    CountDownLatch latch3 = new CountDownLatch(number3);
    for (int i = 0; i < number3; i++) {
      Thread thread = new Thread(new ClientThread(clientManager.getIP(), clientManager.getPort(), clientManager.getNumOfIterPerThread(), latch3));
      thread.start();
    }
    long startTime3 = System.currentTimeMillis();
    System.out.println("Peak phase: All threads running ....");
    latch3.await();
    long endTime3 = System.currentTimeMillis();
    System.out.println("Peak phase complete: Time " + (endTime3 - startTime3) / 1000 + " seconds");

    // COOL DOWN
    int number4 = (int) (clientManager.getMaxNumOfThreads() * 0.25);
    CountDownLatch latch4 = new CountDownLatch(number4);
    for (int i = 0; i < number4; i++) {
      Thread thread = new Thread(new ClientThread(clientManager.getIP(), clientManager.getPort(), clientManager.getNumOfIterPerThread(), latch4));
      thread.start();
    }
    long startTime4 = System.currentTimeMillis();
    System.out.println("Cooldown phase: All threads running ....");
    latch4.await();
    long endTime4 = System.currentTimeMillis();
    System.out.println("Cooldown phase complete: Time " + (endTime4 - startTime4) / 1000 + " seconds");

    long endTime = System.currentTimeMillis();

    System.out.println("============================================");
    System.out.println("Total number of requests sent: " + requestSent);
    System.out.println("Total number of Successful responses: " + responseReceived);
    System.out.println("Test Wall Time: " + (endTime - startTime) / 1000 + " seconds");
    System.out.println("Overall throughput across all phases: " + requestSent.get() / ((endTime - startTime) / 1000));
    System.out.println("Mean latency for all requests: " + getMean(latencies) / 1000 + " seconds");
    System.out.println("Median latency for all requests: " + getMedian(latencies) / 1000 + " seconds");
    System.out.println("99th percentile latency: " + getPercentile(latencies, 0.99) / 1000 + " seconds");
    System.out.println("95th percentile latency: " + getPercentile(latencies, 0.95) / 1000 + " seconds");
  }

  private static long getMean(List<Long> latencies) {
    long sum = 0;
    for (long latency : latencies) {
      sum += latency;
    }
    return sum / latencies.size();
  }

  private static long getMedian(List<Long> latencies) {
    Collections.sort(latencies);
    int size = latencies.size();
    return (size & 1) == 0 ? (latencies.get((size >> 1) - 1) + latencies.get(size >> 1)) / 2 : latencies.get(size >> 1);
  }

  private static long getPercentile(List<Long> latencies, double percentile) {
    Collections.sort(latencies);
    return latencies.get((int)(percentile * latencies.size()));
  }
}

class ClientThread implements Runnable {
  private String IP;
  private int port;
  private int numOfIter;
  private CountDownLatch flg;
  private WebTarget webTarget;

  public ClientThread(String IP, int port, int numOfIter, CountDownLatch flg) {
    this.IP = IP;
    this.port = port;
    this.numOfIter = numOfIter;
    this.flg = flg;
  }

  public String getIP() {
    return IP;
  }

  public int getPort() {
    return port;
  }

  @Override
  public void run() {
    for (int i = 0; i < numOfIter; i++) {
      ClientManager.requestSent.getAndIncrement();
      long startTime = System.currentTimeMillis();
      int status = getStatus();
      long endTime = System.currentTimeMillis();
      if (status == 200) {
        ClientManager.responseReceived.getAndIncrement();
      }
      ClientManager.addLatency(endTime - startTime);

      ClientManager.requestSent.getAndIncrement();
      startTime = System.currentTimeMillis();
      status = postText("Hello");
      endTime = System.currentTimeMillis();
      if (status == 200) {
        ClientManager.responseReceived.getAndIncrement();
      }
      ClientManager.addLatency(endTime - startTime);
    }
    flg.countDown();
  }

  private int getStatus() throws ClientErrorException {
    int status;
    webTarget = ClientBuilder.newClient().target("http://" + this.getIP() + ":" + this.getPort() + "/myapp/myget");
    Invocation.Builder builder = webTarget.request(MediaType.TEXT_PLAIN);
    Response response = builder.get();
    status = response.getStatus();
    response.close();
    return status;
  }

  private int postText(String requestEntity) throws ClientErrorException {
    int status;
    webTarget = ClientBuilder.newClient().target("http://" + this.getIP() + ":" + this.getPort() + "/myapp/mypost");
    Invocation.Builder builder = webTarget.request(MediaType.TEXT_PLAIN);
    Response response = builder.post(Entity.entity(requestEntity, MediaType.TEXT_PLAIN));
    status = response.getStatus();
    response.close();
    return status;
  }
}
