package pt.upa.broker.ws;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.*;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPort;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by xxlxpto on 06-04-2016.
 */
public class BrokerPortTest {



    @BeforeClass
    public static void oneTimeSetUp() {
        // TODO
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // TODO
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {

    }

    /* Tests that only requires one Transporter */

    @Test
    public void simpleAccept(@Mocked final TransporterClient transporterClient) throws Exception {
        // Preparation code not specific to JMockit, if any.
        final String ORIGIN = "Lisboa";
        final String DESTINATION = "Porto";
        final String ID = "0";
        final String NAME = "UpaTransporter1";
        final int PRICE = 7;

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new Expectations() {{
            transporterClient.requestJob(ORIGIN, DESTINATION, PRICE);
            JobView jb = new JobView();
            jb.setJobState(JobStateView.PROPOSED);
            jb.setJobPrice(PRICE);
            jb.setJobDestination(DESTINATION);
            jb.setJobOrigin(ORIGIN);
            jb.setJobIdentifier(ID);
            jb.setCompanyName(NAME);
            result = jb;
            transporterClient.decideJob(ID, true);
            JobView jb2 = new JobView();
            jb2.setJobState(JobStateView.ACCEPTED);
            jb2.setJobPrice(PRICE);
            jb2.setJobDestination(DESTINATION);
            jb2.setJobOrigin(ORIGIN);
            jb2.setJobIdentifier(ID);
            jb2.setCompanyName(NAME);
            result = jb2;
        }};

        // Unit under test is exercised.
        TreeMap<String, TransporterClient> transporterClientTreeMap = new TreeMap<>();
        transporterClientTreeMap.put(NAME, transporterClient);
        BrokerPort mBrokerPort = new BrokerPort(transporterClientTreeMap);

        String returnedFromTest = mBrokerPort.requestTransport(ORIGIN, DESTINATION, PRICE);

        // One or more invocations to mocked types, causing expectations to be verified.
        new Verifications() {{
            // Verifies that zero or one invocations occurred, with the specified argument value:
            transporterClient.requestJob(ORIGIN, DESTINATION, PRICE); maxTimes = 1;
            transporterClient.decideJob(ID, true); maxTimes = 1;
        }};


        // Additional verification code, if any, either here or before the verification block.
        final String EXPECTED = String.format("%s", ID);
        assertEquals("The Correct ID was not returned.", EXPECTED, returnedFromTest);

        List<TransportView> transports = mBrokerPort.listTransports();
        boolean itWorks = false;
        TransportView transport = null;

        for(TransportView tv : transports){
            if(tv.getId().equals(EXPECTED)){
                itWorks = true;
                transport = tv;
            }
        }

        assertEquals("Job wasn't listed.", itWorks, true);
        assertEquals("Job wasn't booked.", TransportStateView.BOOKED, transport.getState());

    }

    @Test(expected = UnavailableTransportFault_Exception.class)
    public void simpleReject(@Mocked final TransporterClient transporterClient) throws Exception {

        // Preparation code not specific to JMockit, if any.
        final String ORIGIN = "Lisboa";
        final String DESTINATION = "Porto";
        final String NAME = "UpaTransporter1";
        final int PRICE = 7;

        new Expectations() {{
            transporterClient.requestJob(ORIGIN, DESTINATION, PRICE);
            result = null;
        }};

        // Unit under test is exercised.
        TreeMap<String, TransporterClient> transporterClientTreeMap = new TreeMap<>();
        transporterClientTreeMap.put(NAME, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        brokerPort.requestTransport(ORIGIN, DESTINATION, PRICE);

        new Verifications() {{
            transporterClient.requestJob(ORIGIN, DESTINATION, PRICE); maxTimes = 1;
        }};

    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void wrongDestination(@Mocked final TransporterClient transporterClient) throws Exception {

        final String ORIGIN = "Lisboa";
        final String DESTINATION = "Canal Caveira";
        final String NAME = "UpaTransporter1";
        final int PRICE = 7;


        // Unit under test is exercised.
        TreeMap<String, TransporterClient> transporterClientTreeMap = new TreeMap<>();
        transporterClientTreeMap.put(NAME, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        brokerPort.requestTransport(ORIGIN, DESTINATION, PRICE);


    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void wrongOrigin(@Mocked final TransporterClient transporterClient) throws Exception {

        final String ORIGIN = "Corral de Moinas";
        final String DESTINATION = "Lisboa";
        final String NAME = "UpaTransporter1";
        final int PRICE = 7;


        // Unit under test is exercised.
        TreeMap<String, TransporterClient> transporterClientTreeMap = new TreeMap<>();
        transporterClientTreeMap.put(NAME, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        brokerPort.requestTransport(ORIGIN, DESTINATION, PRICE);


    }

    @Test(expected = InvalidPriceFault_Exception.class)
    public void wrongPrice(@Mocked final TransporterClient transporterClient) throws Exception {

        final String ORIGIN = "Porto";
        final String DESTINATION = "Lisboa";
        final String NAME = "UpaTransporter1";
        final int PRICE = -7;


        // Unit under test is exercised.
        TreeMap<String, TransporterClient> transporterClientTreeMap = new TreeMap<>();
        transporterClientTreeMap.put(NAME, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        brokerPort.requestTransport(ORIGIN, DESTINATION, PRICE);


    }




    /* =======================================================
    *                       Small tests
    *  ======================================================= */
    /*@Test
    public void pingTest(@Mocked final TransporterClient transporterClient){
        final String NAME = "UpaTransporter1";
        final String HELLO = "Hello!";
       final String HOLA = "Hola!";
        new Expectations() {{
            transporterClient.ping(HELLO);
            result = HOLA;
        }};

        // Unit under test is exercised.
        TreeMap<String, TransporterClient> transporterClientTreeMap = new TreeMap<>();
        transporterClientTreeMap.put(NAME, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        String returnedFromTest = brokerPort.ping(HELLO);

        // One or more invocations to mocked types, causing expectations to be verified.
        new Verifications() {{
            // Verifies that zero or one invocations occurred, with the specified argument value:
            transporterClient.ping(HELLO); maxTimes = 1;
        }};


        // Additional verification code, if any, either here or before the verification block.
        final String EXPECTED = String.format("%s", HOLA);
        assertEquals("Server didn't answer", EXPECTED, returnedFromTest);

    }*/

    @Test
    public void listTest(@Mocked final TransporterClient transporterClient){
        final String NAME = "UpaTransporter1";


        // Unit under test is exercised.
        TreeMap<String, TransporterClient> transporterClientTreeMap = new TreeMap<>();
        transporterClientTreeMap.put(NAME, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        List<TransportView> returnedFromTest = brokerPort.listTransports();

        assertNotNull("List was null", returnedFromTest);

    }

    @Test
    public void clearTest(@Mocked final TransporterClient transporterClient){
        final String NAME = "UpaTransporter1";


        // Unit under test is exercised.
        TreeMap<String, TransporterClient> transporterClientTreeMap = new TreeMap<>();
        transporterClientTreeMap.put(NAME, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        brokerPort.clearTransports();
        List<TransportView> returnedFromTest = brokerPort.listTransports();


        assertEquals("List wasn't cleared", returnedFromTest.size(), 0);

    }
}
