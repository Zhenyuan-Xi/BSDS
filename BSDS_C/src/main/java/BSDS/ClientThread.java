package BSDS;

import java.util.concurrent.CountDownLatch;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ClientThread implements Runnable {
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
      //System.out.println((endTime-startTime));
      ClientManager.addLatency(endTime - startTime);

      ClientManager.requestSent.getAndIncrement();
      startTime = System.currentTimeMillis();
      status = postText("Hello");
      endTime = System.currentTimeMillis();
      if (status == 200) {
        ClientManager.responseReceived.getAndIncrement();
      }
      //System.out.println((endTime-startTime));
      ClientManager.addLatency(endTime - startTime);
    }
    flg.countDown();
  }

  private int getStatus() throws ClientErrorException {
    int status;
    //System.out.println("http://" + this.getIP() + ":" + this.getPort() + "/myapp/myget");
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
