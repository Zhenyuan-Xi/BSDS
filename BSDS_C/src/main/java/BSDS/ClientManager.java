package BSDS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

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
    System.out.println("Server starting .....: http://" + IP + ":" + port + "/myapp/myget");
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    System.out.println("Client starting .....Time: " + df.format(startTime));

    // WARM UP
    int number1 = (int) (clientManager.getMaxNumOfThreads() * 0.1);
    CountDownLatch latch1 = new CountDownLatch(number1);
    multiThread(number1, IP, port, numOfIterPerThread, latch1);
    long startTime1 = System.currentTimeMillis();
    System.out.println("Warmup phase: All threads running ....");
    latch1.await();
    long endTime1 = System.currentTimeMillis();
    System.out.println("Warmup phase complete: Time " + (endTime1 - startTime1) / 1000 + " seconds");

    // LOADING
    int number2 = (int) (clientManager.getMaxNumOfThreads() * 0.5);
    CountDownLatch latch2 = new CountDownLatch(number2);
    multiThread(number2, IP, port, numOfIterPerThread, latch2);
    long startTime2 = System.currentTimeMillis();
    System.out.println("Loading phase: All threads running ....");
    latch2.await();
    long endTime2 = System.currentTimeMillis();
    System.out.println("Loading phase complete: Time " + (endTime2 - startTime2) / 1000 + " seconds");

    // PEAK
    int number3 = clientManager.getMaxNumOfThreads();
    CountDownLatch latch3 = new CountDownLatch(number3);
    multiThread(number3, IP, port, numOfIterPerThread, latch3);
    long startTime3 = System.currentTimeMillis();
    System.out.println("Peak phase: All threads running ....");
    latch3.await();
    long endTime3 = System.currentTimeMillis();
    System.out.println("Peak phase complete: Time " + (endTime3 - startTime3) / 1000 + " seconds");

    // COOL DOWN
    int number4 = (int) (clientManager.getMaxNumOfThreads() * 0.25);
    CountDownLatch latch4 = new CountDownLatch(number4);
    multiThread(number4, IP, port, numOfIterPerThread, latch4);
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
    System.out.println("Overall throughput across all phases: " + requestSent.get() / ((endTime - startTime) / 1000) + " seconds");
    System.out.println("Mean latency for all requests: " + getMean(latencies) + " milliseconds");
    System.out.println("Median latency for all requests: " + getMedian(latencies) + " milliseconds");
    System.out.println("99th percentile latency: " + getPercentile(latencies, 0.99) + " milliseconds");
    System.out.println("95th percentile latency: " + getPercentile(latencies, 0.95) + " milliseconds");
  }

  private static void multiThread(int number, String IP, int port, int numOfIterPerThread, CountDownLatch latch) {
    Thread[] threads = new Thread[number];
    for (int i = 0; i < number; i++) {
      threads[i] = new Thread(new ClientThread(IP, port, numOfIterPerThread, latch));
    }
    for (Thread thread : threads){
      thread.start();
    }
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
