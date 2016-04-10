package pt.upa.broker.ws.it;


import org.junit.Test;
import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.BrokerClient;
import pt.upa.transporter.ws.TransporterPortType;

import javax.xml.registry.JAXRException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class BrokerClientIT implements AbstractTest {

    private static BrokerClient client;

    private static String uddiURL = "";             //TODO WHAT DO???
    private static String name = "";                //TODO
    private static TransporterPortType mPort = null;//TODO

    //SETUP
    @Override
    public void setUp() throws JAXRException {
        client = new BrokerClient(uddiURL, name);
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
        client.requestTransport("Lisboa", "Porto", 10);
        List<TransportView> result = client.listTransports(mPort);
        assertEquals("transport not in the list", 1, result.size());
    }

    @Test
    public void clearTransports() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        client.requestTransport("Lisboa", "Porto", 10);
        client.clearTransports(mPort);
        List<TransportView> result = client.listTransports(mPort);
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
