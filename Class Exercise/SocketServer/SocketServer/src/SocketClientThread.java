/**
 *
 * @author Ian Gortan
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

// Sockets of this class are coordinated  by a CyclicBarrier which pauses all threads 
// until the last one completes. At this stage, all threads terminate

public class SocketClientThread extends Thread {
    private long clientID;
    String hostName;
    int port;
    CyclicBarrier synk;
    
    public SocketClientThread(String hostName, int port, CyclicBarrier barrier) {
        this.hostName = hostName;
        this.port = port;
        clientID = Thread.currentThread().getId();
        synk = barrier;
    }
    
    public void run() {
        
        try {
            // TO DO insert code to pass 10k messages to the SocketServer

            for(int i = 0; i < 10; i++){
                Socket s = new Socket(hostName, port);
                System.out.println(i);

            PrintWriter out =
                new PrintWriter(s.getOutputStream(), true);
            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(s.getInputStream()));
            out.println("Client ID is " +  Long.toString(clientID));

            System.out.println(in.readLine());
            s.close();

            }
           
        
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
        try {
            synk.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
