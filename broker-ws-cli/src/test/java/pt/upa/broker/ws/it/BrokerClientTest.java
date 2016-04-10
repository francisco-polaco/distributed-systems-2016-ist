package pt.upa.broker.ws.it;


import org.junit.Test;
import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.BrokerClient;
import pt.upa.transporter.ws.TransporterPortType;

import javax.xml.registry.JAXRException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class BrokerClientTest implements AbstractTest {

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
    public void requestTransport(){
        String result = client.requestTransport("Lisboa", "Porto", 80);
        assertNotNull(result);
    }

    @Test
    public void viewTransport(){
        String id = client.requestTransport("Lisboa", "Porto", 80);
        TransportView result = client.viewTransport(id);
        assertNotNull(result);
    }

    @Test
    public void listTransports(){
        List<TransportView> result = client.listTransports(mPort);
        assertNotNull(result);
    }

    @Test
    public void clearTransports(){
        client.clearTransports(mPort);
        List<TransportView> result = client.listTransports(mPort);
        assertEquals("error", 0, result.size());
    }

    //ERRORCASES
    @Test(expected = UnavailableTransportPriceFault_Exception.class)
    public void requestTransportWithInvalidPrice(){
        client.requestTransport("Lisboa", "Porto", -1);
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void requestTransportToUnknownLocation(){
        client.requestTransport("Lisboa", "Vila Franca de Xira", 10);
    }

    @Test(expected = UnknownTransportFault_Exception.class)
    public void viewTransportWithInvalidID(){
        client.viewTransport("potato");
    }


}
