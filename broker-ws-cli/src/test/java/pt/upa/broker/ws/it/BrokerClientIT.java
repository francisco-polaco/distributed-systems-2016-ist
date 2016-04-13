package pt.upa.broker.ws.it;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.BrokerClient;
import pt.upa.transporter.ws.TransporterPortType;

import java.io.IOException;
import java.util.Properties;

import javax.xml.registry.JAXRException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class BrokerClientIT {

    private static final String TEST_PROP_FILE = "/test.properties";

    private static BrokerClient client;

    private static Properties props = null;

    private static String uddiURL = "";
    private static String name = "";

    //SETUP
    @BeforeClass
    public static void oneTimeSetUp() throws IOException {
        props = new Properties();
        try {
            props.load(BrokerClientIT.class.getResourceAsStream(TEST_PROP_FILE));
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
    public static void setUp() throws JAXRException, IOException {
        client = new BrokerClient(uddiURL, name);
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
    public void requestTransport() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        String result = client.requestTransport("Lisboa", "Porto", 10);
        assertNotNull(result);
    }

    @Test
    public void viewTransport() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception, UnknownTransportFault_Exception {
        String id = client.requestTransport("Lisboa", "Porto", 10);
        TransportView result = client.viewTransport(id);
        assertNotNull(result);
    }

    @Test
    public void listTransports() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        client.clearTransports(client.getPort());
        client.requestTransport("Lisboa", "Porto", 10);
        List<TransportView> result = client.listTransports(client.getPort());
        assertEquals("transport not in the list", 1, result.size());
    }

    @Test
    public void clearTransports() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        client.requestTransport("Lisboa", "Porto", 10);
        client.clearTransports(client.getPort());
        List<TransportView> result = client.listTransports(client.getPort());
        assertEquals("transport not deleted from the list", 0, result.size());
    }

    //ERRORCASES
    @Test(expected = UnavailableTransportFault_Exception.class)
    public void requestTransportWithHighPrice() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        client.requestTransport("Lisboa", "Porto", 200);
    }

    @Test(expected = InvalidPriceFault_Exception.class)
    public void requestTransportWithInvalidPrice() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        client.requestTransport("Lisboa", "Porto", -1);
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void requestTransportToUnknownLocation() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        client.requestTransport("Lisboa", "Vila Franca de Xira", 10);
    }

    @Test(expected = UnknownTransportFault_Exception.class)
    public void viewTransportWithInvalidID() throws UnknownTransportFault_Exception {
        client.viewTransport("potato");
    }
}
