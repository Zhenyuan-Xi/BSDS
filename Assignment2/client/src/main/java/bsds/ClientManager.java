package bsds;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class ClientManager {

  protected static final String SERVER_HOST_NAME = "ec2-34-205-125-134.compute-1.amazonaws.com";
  // protected static final String ELB_DNS = "";
  protected static final int PORT = 8080;
  protected static final String APPLICATION = "bsds_server";
  private static final String csvPath = "C:/Users/steph/Documents/Reports";

  private static final int REQUEST_PER_ITERATION = 3;
  protected static List<List<long[]>> latenciesIntervals;
  protected static long testStartTime = -1;
  protected static long testEndTime = -1;
  protected static AtomicInteger successfulRequestCount;

  private static List<List<long[]>> latencyIntervals;
  private static List<Long> latencies;

  public static void main(String[] args) throws InterruptedException {

    int maxThreads = Integer.parseInt(args[0]);
    int iterations = Integer.parseInt(args[1]);
    latenciesIntervals = new ArrayList<>(4);
    for (int i = 0; i < 4; i++) {
      latenciesIntervals.add(new LinkedList<>());
    }
    int totalThreads = (int) (maxThreads * 0.1) + (int) (maxThreads * 0.5) + maxThreads + (int) (maxThreads * 0.25);
    successfulRequestCount = new AtomicInteger();
    CountDownLatch mainLatch = new CountDownLatch(totalThreads);
    System.out.println("Start Testing ...");
    testStartTime = System.currentTimeMillis();
    int numOfThreads;

    // Warm Up
    numOfThreads = (int) (maxThreads * 0.1);
    System.out.println("Warm Up phase ... Number Of Threads: " + numOfThreads);
    ThreadGroup group = new ThreadGroup("WarmUp");
    int numOfLaggerThreads = Math.min(10, (int) (maxThreads * 0.1 * 0.10));
    startThread(iterations, mainLatch, numOfThreads, group, 0);
    while (group.activeCount() > numOfLaggerThreads) {}

    // Loading
    numOfThreads = (int) (maxThreads * 0.5);
    System.out.println("Loading phase ... Number Of Threads: " + numOfThreads);
    group = new ThreadGroup("Loading");
    numOfLaggerThreads = Math.min(10, (int) (maxThreads * 0.5 * 0.10));
    startThread(iterations, mainLatch, numOfThreads, group, 1);
    while (group.activeCount() > numOfLaggerThreads) {}

    // Peak
    numOfThreads = maxThreads;
    System.out.println("Peak phase ... Number Of Threads: " + numOfThreads);
    group = new ThreadGroup("Peak");
    numOfLaggerThreads = Math.min(10, (int) (maxThreads * 0.10));
    startThread(iterations, mainLatch, numOfThreads, group, 2);
    while (group.activeCount() > numOfLaggerThreads) {}

    // Cool Down
    numOfThreads = (int) (maxThreads * 0.25);
    System.out.println("Cool Down phase ... Number Of Threads: " + numOfThreads);
    group = new ThreadGroup("CoolDown");
    numOfLaggerThreads = Math.min(10, (int) (maxThreads * 0.25 * 0.10));
    startThread(iterations, mainLatch, numOfThreads, group, 3);
    while (group.activeCount() > numOfLaggerThreads) {}

    mainLatch.await();
    testEndTime = System.currentTimeMillis();
    System.out.println("----------------------------------------------");

    latencyIntervals = latenciesIntervals;
    for (List<long[]> subLatencyPairs : latencyIntervals) {
      subLatencyPairs.sort((a, b) -> (int) (a[0] - b[0]));
    }
    latencies = new ArrayList<>(latencyIntervals.size());
    for (List<long[]> latencyPairsOfEachPhase : latencyIntervals) {
      for (long[] latencyPair : latencyPairsOfEachPhase) {
        latencies.add(latencyPair[1] - latencyPair[0]);
      }
    }
    Collections.sort(latencies);

    long totalRunTime = (ClientManager.testEndTime - ClientManager.testStartTime) / 1000;
    System.out.println("Total Run Time: " + totalRunTime + " seconds");
    int totalRequests = latencies.size();
    System.out.println("Total Requests Sent: " + totalRequests);
    System.out.println("Total Successful Requests: " + ClientManager.successfulRequestCount);
    long throughput = getMeanLatency();
    System.out.println("Mean Latency: " + throughput + " ms");
    long percentile95 = getLatencyPercentile(95);
    System.out.println("95 Percentile Latency: " + percentile95 + " ms");
    long percentile99 = getLatencyPercentile(99);
    System.out.println("99 Percentile Latency: " + percentile99 + " ms");

    // Warm Up
    System.out.println("Phase: Warm Up");
    long startTime = latencyIntervals.get(0).get(0)[0];
    System.out.println("Phase Start Time: " + startTime + " ms");
    long endTime = getEndTimeOfAPhase(0);
    System.out.println("Phase End Time: " + endTime + " ms");
    int phaseRunTime = (int) ((endTime - startTime) / 1000);
    System.out.println("Phase Run Time: " + phaseRunTime + " seconds");
    int numberOfRequests = ((int) (maxThreads * 0.1)) * iterations * ClientManager.REQUEST_PER_ITERATION;
    System.out.println("Phase Requests Sent: " + numberOfRequests);
    long phaseAverageLatency = getPhaseAverageLatency(0);
    System.out.println("Phase Throughput: " + phaseAverageLatency + " ms");

    // Loading
    System.out.println("Phase: Loading");
    startTime = latencyIntervals.get(1).get(0)[0];
    System.out.println("Phase Start Time: " + startTime + " ms");
    endTime = getEndTimeOfAPhase(1);
    System.out.println("Phase End Time: " + endTime + " ms");
    phaseRunTime = (int) ((endTime - startTime) / 1000);
    System.out.println("Phase Run Time: " + phaseRunTime + " seconds");
    numberOfRequests = ((int) (maxThreads * 0.5)) * iterations * ClientManager.REQUEST_PER_ITERATION;
    System.out.println("Phase Requests Sent: " + numberOfRequests);
    phaseAverageLatency = getPhaseAverageLatency(1);
    System.out.println("Phase Throughput: " + phaseAverageLatency + " ms");

    // Peak
    System.out.println("Phase: Peak");
    startTime = latencyIntervals.get(2).get(0)[0];
    System.out.println("Phase Start Time: " + startTime + " ms");
    endTime = getEndTimeOfAPhase(2);
    System.out.println("Phase End Time: " + endTime + " ms");
    phaseRunTime = (int) ((endTime - startTime) / 1000);
    System.out.println("Phase Run Time: " + phaseRunTime + " seconds");
    numberOfRequests = maxThreads * iterations * ClientManager.REQUEST_PER_ITERATION;
    System.out.println("Phase Requests Sent: " + numberOfRequests);
    phaseAverageLatency = getPhaseAverageLatency(2);
    System.out.println("Phase Throughput: " + phaseAverageLatency + " ms");

    // Cool Down
    System.out.println("Phase: Cool Down");
    startTime = latencyIntervals.get(3).get(0)[0];
    System.out.println("Phase Start Time: " + startTime + " ms");
    endTime = getEndTimeOfAPhase(3);
    System.out.println("Phase End Time: " + endTime + " ms");
    phaseRunTime = (int) ((endTime - startTime) / 1000);
    System.out.println("Phase Run Time: " + phaseRunTime + " seconds");
    numberOfRequests = ((int) (maxThreads * 0.25)) * iterations * ClientManager.REQUEST_PER_ITERATION;
    System.out.println("Phase Requests Sent: " + numberOfRequests);
    phaseAverageLatency = getPhaseAverageLatency(3);
    System.out.println("Phase Throughput: " + phaseAverageLatency + " ms");

    exportRequestCounts(csvPath, getRequestPerSecond());
  }

  private static void startThread(int iterations, CountDownLatch mainLatch, int numOfThreads, ThreadGroup group, int phase) {
    for (int i = 0; i < numOfThreads; i++) {
      Client client = ClientBuilder.newClient();
      WebTarget webTarget = client.target("http://" + ClientManager.SERVER_HOST_NAME + ":" + ClientManager.PORT + "/" + ClientManager.APPLICATION + "/webapi");
      Thread thread = new Thread(group, new ClientThread(iterations, REQUEST_PER_ITERATION, mainLatch, client, webTarget, phase));
      thread.start();
    }
  }

  public static synchronized void appendLatencies(List<long[]> latencyInterval, int phase) {
    latenciesIntervals.get(phase).addAll(latencyInterval);
  }

  private static long getPhaseAverageLatency(int phase) {
    long sumLatencies = 0;
    List<long[]> latencyInterval = ClientManager.latencyIntervals.get(phase);
    for (long[] interval : latencyInterval) {
      long latency = interval[1] - interval[0];
      sumLatencies += latency;
    }
    return sumLatencies / latencyInterval.size();
  }

  private static long getEndTimeOfAPhase(int phase) {
    long endTime = -1;
    List<long[]> latencyInterval = ClientManager.latencyIntervals.get(phase);
    for (long[] interval : latencyInterval) {
      endTime = Math.max(endTime, interval[1]);
    }
    return endTime;
  }

  private static List<Integer> getRequestPerSecond() {
    List<Integer> endTimes = new LinkedList<>();
    long startTime = latencyIntervals.get(0).get(0)[0];
    for (List<long[]> latencyInterval : latencyIntervals) {
      for (long[] interval : latencyInterval) {
        Integer endTime = (int) ((interval[1] - startTime) / 1000);
        endTimes.add(endTime);
      }
    }
    Collections.sort(endTimes);
    int longestTime = endTimes.get(endTimes.size() - 1);
    List<Integer> requestPerSecond = new ArrayList<>(longestTime);
    for (int i = 0; i <= longestTime; i++) {
      requestPerSecond.add(0);
    }
    for (Integer endTime : endTimes) {
      int cnt = requestPerSecond.get(endTime);
      requestPerSecond.set(endTime, cnt + 1);
    }
    return requestPerSecond;
  }

  private static long getMeanLatency() {
    long sum = 0;
    for (Long latency : latencies) {
      sum += latency;
    }
    return sum / latencies.size();
  }

  private static long getLatencyPercentile(int percentile) {
    int size = latencies.size();
    int index = size * percentile / 100;
    Collections.sort(latencies);
    return latencies.get(index);
  }

  private static void exportRequestCounts(String filePath, List<Integer> reqCount) {
    FileWriter fileWriter = null;
    try {
      fileWriter = new FileWriter(filePath);
      fileWriter.append("Timestamp,NumberOfRequests\r\n");
      for (int i = 0; i < reqCount.size(); i++) {
        Integer second = i;
        Integer count = reqCount.get(i);
        fileWriter.append(String.valueOf(second)).append(",").append(String.valueOf(count)).append("\r\n");
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      try {
        assert fileWriter != null;
        fileWriter.flush();
        fileWriter.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

}
