package pt.upa.transporter.ws.it;


import org.junit.Test;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.cli.TransporterClient;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class TransporterClientTest implements AbstractTest{

    private static TransporterClient client;

    //SETUP
    @Override
    public void setUp() {
        client = new TransporterClient(uddiURL, wsName);
    }

    @Override
    public void tearDown() {
        client = null;
    }

    //TESTS
    @Test
    public void testPing(){
        final String result= client.ping("test");
        assertNotNull(result);
    }

    @Test
    public void requestJob(){
        client.requestJob("Lisboa", "Porto", 80);
    }

    @Test
    public void decideJob(String id, boolean accept){
        client.decideJob(String id, true);
    }

    @Test
    public void jobStatus(String id){
        client.jobStatus(String id);
    }

    @Test
    public void listJobs(){
        client.listJobs();
    }

    @Test
    public void clearJobs(){
        client.clearJobs();
    }


    //ERRORCASES
    @Test(expected = BadLocationFault_Exception.class)
    public void requestJobWithInvalidLocation(){
        client.requestJob(String origin, String destination, int price);
    }
    @Test(expected = BadPriceFault_Exception.class)
    public void requestJobWithInvalidPrice(){
        client.requestJob(String origin, String destination, int price);
    }


    @Test(expected = BadJobFault_Exception.class)
    public void decideJobWithInvalidID(){
        client.decideJob(String id, boolean accept);
    }
}
