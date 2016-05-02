package pt.upa.transporter.ws.it;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

import javax.xml.registry.JAXRException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class TransporterClientIT {

    private static final String TEST_PROP_FILE = "/test.properties";

    private static TransporterClient client;

    private static Properties props = null;

    private static String uddiURL = "";
    private static String name = "";

    //SETUP
    @BeforeClass
    public static void oneTimeSetUp() throws IOException {
        props = new Properties();
        try {
            props.load(TransporterClientIT.class.getResourceAsStream(TEST_PROP_FILE));
        } catch (IOException e) {
            final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
            System.out.println(msg);
            throw e;
        }
        uddiURL = props.getProperty("uddi.url");
        name = props.getProperty("ws.name");
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }

    @BeforeClass
    public static void setUp() throws Exception {
        client = new TransporterClient(uddiURL, name);
    }

    @AfterClass
    public static void tearDown() {
        client = null;
    }

    //TESTS
    @Test
    public void testPing(){
        final String result= client.ping("test");
        assertNotNull(result);
    }

    @Test
    public void testPingNull(){
        final String result= client.ping(null);
        assertNotNull(result);
    }


    @Test
    public void requestJob() throws BadLocationFault_Exception, BadPriceFault_Exception {
        JobView result = client.requestJob("Lisboa", "Porto", 10);
        assertNotNull(result);
    }

    @Test
    public void decideJob() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
        JobView id = client.requestJob("Lisboa", "Porto", 10);
        JobView result = client.decideJob(id.getJobIdentifier(), true);
        assertNotNull(result);
    }

    @Test
    public void jobStatus() throws BadLocationFault_Exception, BadPriceFault_Exception {
        JobView id = client.requestJob("Lisboa", "Porto", 10);
        JobView result = client.jobStatus(id.getJobIdentifier());
        assertNotNull(result);
    }

    @Test
    public void listJobs() throws BadLocationFault_Exception, BadPriceFault_Exception {
        client.clearJobs();
        client.requestJob("Lisboa", "Porto", 10);
        List<JobView> result = client.listJobs();
        assertEquals("job not in the list", 1, result.size());
    }

    @Test
    public void clearJobs() throws BadLocationFault_Exception, BadPriceFault_Exception {
        client.requestJob("Lisboa", "Porto", 10);
        client.clearJobs();
        List<JobView> result = client.listJobs();
        assertEquals("job not deleted from the list", 0, result.size());
    }


    //ERRORCASES
    @Test(expected = BadLocationFault_Exception.class)
    public void requestJobForInvalidLocation() throws BadLocationFault_Exception, BadPriceFault_Exception {
        client.requestJob("Lisboa", "Vila Franca de Xira", 10);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void requestJobFromInvalidLocation() throws BadLocationFault_Exception, BadPriceFault_Exception {
        client.requestJob("Vila Franca de Xira", "Lisboa", 10);
    }

    @Test(expected = BadPriceFault_Exception.class)
    public void requestJobWithInvalidPrice() throws BadLocationFault_Exception, BadPriceFault_Exception {
        client.requestJob("Lisboa", "Porto", -1);
    }

    @Test(expected = BadJobFault_Exception.class)
    public void decideJobWithInvalidID() throws BadJobFault_Exception {
        client.decideJob("potato", true);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void requestJobfromnullLocation() throws BadLocationFault_Exception, BadPriceFault_Exception {
        client.requestJob(null, "Lisboa", 10);
    }
    @Test(expected = BadLocationFault_Exception.class)
    public void requestJobfornullLocation() throws BadLocationFault_Exception, BadPriceFault_Exception {
        client.requestJob("Lisboa", null, 10);
    }
    @Test(expected = BadJobFault_Exception.class)
    public void decideJobWithNullID() throws BadJobFault_Exception {
        client.decideJob(null, true);
    }
}
