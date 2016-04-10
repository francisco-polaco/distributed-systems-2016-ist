package pt.upa.transporter.ws.it;


import org.junit.Test;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

import javax.xml.registry.JAXRException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class TransporterClientTest implements AbstractTest{

    private static TransporterClient client;

    private static String uddiURL = "";             //TODO WHAT DO???
    private static String name = "";                //TODO

    //SETUP
    @Override
    public void setUp() throws JAXRException {
        client = new TransporterClient(uddiURL, name);
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
        JobView result = client.requestJob("Lisboa", "Porto", 10);
        assertNotNull(result);
    }

    @Test
    public void decideJob(){
        JobView id = client.requestJob("Lisboa", "Porto", 10);
        JobView result = client.decideJob(id.getJobIdentifier(), true);
        assertNotNull(result);
    }

    @Test
    public void jobStatus(){
        JobView id = client.requestJob("Lisboa", "Porto", 10);
        JobView result = client.jobStatus(id.getJobIdentifier());
        assertNotNull(result);
    }

    @Test
    public void listJobs(){
        client.requestJob("Lisboa", "Porto", 10);
        List<JobView> result = client.listJobs();
        assertEquals("error", 1, result.size());
    }

    @Test
    public void clearJobs(){
        client.requestJob("Lisboa", "Porto", 10);
        client.clearJobs();
        List<JobView> result = client.listJobs();
        assertEquals("error", 0, result.size());
    }


    //ERRORCASES
    @Test(expected = BadLocationFault_Exception.class)
    public void requestJobWithInvalidLocation(){
        client.requestJob("Lisboa", "Vila Franca de Xira", 10);
    }

    @Test(expected = BadPriceFault_Exception.class)
    public void requestJobWithInvalidPrice(){
        client.requestJob("Lisboa", "Porto", -1);
    }

    @Test(expected = BadJobFault_Exception.class)
    public void decideJobWithInvalidID(){
        client.decideJob("potato", true);
    }
}
