import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Reporter {

	private List<List<long[]>> latencyPairs;
	private List<Long> latencies;
	private int maxThreads;
	private static final String csvPath = "C:/Users/WearableReports";
	
	/**
	 * Constructor, instantiates a Reporter.
	 */
	public Reporter(List<List<long[]>> latencyPairs, int maxThreads) {
		this.maxThreads = maxThreads;
		this.latencyPairs = latencyPairs;
		// sort data by start time
		for (List<long[]> subLatencyPairs : this.latencyPairs) {
			Collections.sort(subLatencyPairs, new Comparator<long[]>() {
				public int compare(long[] a, long[] b) {
					return (int) (a[0] - b[0]);
				}
			});
		}
		this.latencies = new ArrayList<Long>(latencyPairs.size());
		for (List<long[]> latencyPairsOfEachPhase : latencyPairs) {
			for (long[] latencyPair : latencyPairsOfEachPhase) {
				this.latencies.add(latencyPair[1] - latencyPair[0]);
			}
		}
		Collections.sort(this.latencies);
	}
	
	/**
	 * The statistics you should produce are as follows: 
	 * Total run time (wall time) for all threads to complete (as previous step) 
	 * Total Number of requests sent 
	 * Total Number of successful requests  
	 * Overall throughput across all phases (total number of requests/total wall time) 
	 * Mean and median latencies for all requests 
	 * 99th and 95th percentile latency 
	 */
	public void doReport() {
    	System.out.println("==================== TEST REPORT ====================");
		// Total run time (wall time)
		long totalRunTime = (WearableClientApplication.testEndTime - WearableClientApplication.testStartTime) / 1000;
		System.out.println("[Reporter] Total Run Time: " + totalRunTime + " seconds");
		// Total number of requests sent
		int totalRequests = this.latencies.size();
		System.out.println("[Reporter] Total Requests Sent: " + totalRequests);
		// Total number of successful requests
		System.out.println("[Reporter] Total Successful Requests: " + WearableClientApplication.globalSuccessfulRequestCount);
		// calculate overall throughput
		long throughput = getMeanLatency();
		System.out.println("[Reporter] Mean Latency: " + throughput + " ms");
		// calculate 95 percentile
		long percentile95 = getLatencyPercentile(95);
		System.out.println("[Reporter] 95 Percentile Latency: " + percentile95 + " ms");
		// calculate 99 percentile
		long percentile99 = getLatencyPercentile(99);
		System.out.println("[Reporter] 99 Percentile Latency: " + percentile99 + " ms");
		// Wall Time and throughput of each phase
		for (int i = 0 ; i < WearableClientApplication.NUM_PHASES ; i++) {
			System.out.println("[Reporter] Phase: " + WearableClientApplication.PHASE_NAMES[i]);
			long startTime = this.latencyPairs.get(i).get(0)[0];
			System.out.println("[Reporter] 		Phase Start Time: " + startTime +  " ms");
			long endTime = getEndTimeOfAPhase(i);
			System.out.println("[Reporter] 		Phase End Time: " + endTime +  " ms");
			int phaseRunTime = (int) ((endTime - startTime) / 1000);
			System.out.println("[Reporter] 		Phase Run Time: " + phaseRunTime + " seconds");
			int numberOfRequests = ((int)(WearableClientApplication.PHASE_WEIGHTS[i] * this.maxThreads)) 
									* WearableClientApplication.DEFAULT_ITERATIONS 
									* WearableClientApplication.PHASE_LENGTHS[i]
									* 5;
			System.out.println("[Reporter] 		Phase Requests Sent: " + numberOfRequests);
			long phaseAverageLatency = getPhaseAverageLatency(i);
			System.out.println("[Reporter] 		Phase Throughput: " + phaseAverageLatency + " ms");
		}
		// write request per second report to csv file 
		System.out.println("[Reporter] Generating request_second.csv file ......");
		exportRequestCounts(Reporter.csvPath, this.getRequestProcessedPerSecond());
		System.out.println("[Reporter] request_second.csv file Generated");
		System.out.println("==================== TEST REPORT ====================");
	}
	
	private long getPhaseAverageLatency(int phaseId) {
		long sumLatencies = 0;
		List<long[]> latencyPairs = this.latencyPairs.get(phaseId);
		for (long[] latencyPair : latencyPairs) {
			long latency = latencyPair[1] - latencyPair[0];
			sumLatencies += latency;
		} 
		return sumLatencies / latencyPairs.size();
	}
	
	private long getEndTimeOfAPhase(int phaseId) {
		long endTime = -1;
		List<long[]> latencyPairs = this.latencyPairs.get(phaseId);
		for (long[] latencyPair : latencyPairs) {
			endTime = Math.max(endTime, latencyPair[1]);
		}
		return endTime;
	}
	
	private List<Integer> getRequestProcessedPerSecond() {
		//______transform request time from system time to relative time
		List<Integer> finishedTimeOfRequests = new LinkedList<Integer>(); 
		long begTime = latencyPairs.get(0).get(0)[0]; 
		for (List<long[]> latencyPairsOfEachPhase : this.latencyPairs) {
			for(long[] pair : latencyPairsOfEachPhase) {
				Integer finishedTime = (int) ((pair[1] - begTime) / 1000);
				finishedTimeOfRequests.add(finishedTime);
			}
		}
		//______sort finish time in ascending order
		Collections.sort(finishedTimeOfRequests);
		int longestTime = finishedTimeOfRequests.get(finishedTimeOfRequests.size() - 1);
		//______count number of request of every second from begTime to endTime
		List<Integer> requestProcessedPerSecond = new ArrayList<Integer>(longestTime);
		for (int i = 0 ; i <= longestTime; i++) {
			requestProcessedPerSecond.add(0);
		}
		for (int i = 0 ; i < finishedTimeOfRequests.size() ; i++) {
			int finishedTime = finishedTimeOfRequests.get(i);
			int oldCount = requestProcessedPerSecond.get(finishedTime);
			requestProcessedPerSecond.set(finishedTime, oldCount + 1);
		}
		return requestProcessedPerSecond;
	}
	
	private long getMeanLatency() {
		long sum = 0;
		for(Long latency : this.latencies) {
			sum += latency;
		}
		return sum / this.latencies.size();
	}
	
	private long getLatencyPercentile(int percentile) {
		int size = this.latencies.size();
		int index = (int) (size * percentile / 100);
		Collections.sort(this.latencies);
		return latencies.get(index);
	}

	private void exportRequestCounts(String filePath, List<Integer> reqCount) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(filePath + maxThreads); // throw I/O Exception
			fileWriter.append("Time(Second),NumberOfRequests(Times)\r\n");
			for(int i = 0 ; i < reqCount.size() ; i++) {
				Integer second = i;
				Integer count = reqCount.get(i);
				fileWriter.append(String.valueOf(second)).append(",").append(String.valueOf(count)).append("\r\n");
			}
		} catch (Exception ex) {
			System.out.println("[Reporter] File Writer Failure" + ex);
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
