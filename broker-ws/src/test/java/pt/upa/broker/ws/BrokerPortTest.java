package pt.upa.broker.ws;

import mockit.Expectations;
import mockit.Injectable;
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


    /* =======================================================
    *            Tests that only requires one Transporter
    *  ======================================================= */

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

        for(TransportView tv : transports){
            if(tv.getId().equals(EXPECTED)){
                itWorks = true;
                assertEquals("Job wasn't booked.", TransportStateView.BOOKED, tv.getState());
            }
        }
        assertEquals("Job wasn't listed.", itWorks, true);


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
    *            Tests that requires multiple Transporters
    *  ======================================================= */

    @Test
    public void multipleAccept(@Mocked @Injectable final TransporterClient transporterClient1,
                               @Mocked @Injectable final TransporterClient transporterClient2,
                               @Mocked @Injectable final TransporterClient transporterClient3,
                               @Mocked @Injectable final TransporterClient transporterClient4) throws Exception {
        // Preparation code not specific to JMockit, if any.
        final String ORIGIN = "Lisboa";
        final String DESTINATION = "Porto";
        final String[] IDs = {"0", "1", "2", "3"};
        final String NAME = "UpaTransporter";
        final int CUSTOMER_PRICE = 27;
        final int[] PRICES = { 25, 35, 5, 50};

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new Expectations() {{
            transporterClient1.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE);
            JobView jb1 = new JobView();
            jb1.setJobState(JobStateView.PROPOSED);
            jb1.setJobPrice(PRICES[0]);
            jb1.setJobDestination(DESTINATION);
            jb1.setJobOrigin(ORIGIN);
            jb1.setJobIdentifier(IDs[0]);
            jb1.setCompanyName(NAME + 1);
            result = jb1;

            transporterClient2.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE);
            JobView jb2 = new JobView();
            jb2.setJobState(JobStateView.PROPOSED);
            jb2.setJobPrice(PRICES[1]);
            jb2.setJobDestination(DESTINATION);
            jb2.setJobOrigin(ORIGIN);
            jb2.setJobIdentifier(IDs[1]);
            jb2.setCompanyName(NAME + 2);
            result = jb2;

            transporterClient3.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); // It should pick this
            JobView jb3 = new JobView();
            jb3.setJobState(JobStateView.PROPOSED);
            jb3.setJobPrice(PRICES[2]);
            jb3.setJobDestination(DESTINATION);
            jb3.setJobOrigin(ORIGIN);
            jb3.setJobIdentifier(IDs[2]);
            jb3.setCompanyName(NAME + 3);
            result = jb3;
            
            transporterClient4.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE);
            JobView jb4 = new JobView();
            jb4.setJobState(JobStateView.PROPOSED);
            jb4.setJobPrice(PRICES[3]);
            jb4.setJobDestination(DESTINATION);
            jb4.setJobOrigin(ORIGIN);
            jb4.setJobIdentifier(IDs[3]);
            jb4.setCompanyName(NAME + 4);
            result = jb4;

            transporterClient1.decideJob(IDs[0], false);
            JobView jb11 = new JobView();
            jb11.setJobState(JobStateView.REJECTED);
            jb11.setJobPrice(PRICES[0]);
            jb11.setJobDestination(DESTINATION);
            jb11.setJobOrigin(ORIGIN);
            jb11.setJobIdentifier(IDs[0]);
            jb11.setCompanyName(NAME + 1);
            result = jb11;
            
            transporterClient2.decideJob(IDs[1], false);
            JobView jb22 = new JobView();
            jb22.setJobState(JobStateView.REJECTED);
            jb22.setJobPrice(PRICES[1]);
            jb22.setJobDestination(DESTINATION);
            jb22.setJobOrigin(ORIGIN);
            jb22.setJobIdentifier(IDs[1]);
            jb22.setCompanyName(NAME + 2);
            result = jb22;
            
            transporterClient3.decideJob(IDs[2], true);
            JobView jb33 = new JobView();
            jb33.setJobState(JobStateView.ACCEPTED);
            jb33.setJobPrice(PRICES[2]);
            jb33.setJobDestination(DESTINATION);
            jb33.setJobOrigin(ORIGIN);
            jb33.setJobIdentifier(IDs[2]);
            jb33.setCompanyName(NAME + 3);
            result = jb33;
            
            transporterClient4.decideJob(IDs[3], false);
            JobView jb44 = new JobView();
            jb44.setJobState(JobStateView.REJECTED);
            jb44.setJobPrice(PRICES[3]);
            jb44.setJobDestination(DESTINATION);
            jb44.setJobOrigin(ORIGIN);
            jb44.setJobIdentifier(IDs[3]);
            jb44.setCompanyName(NAME + 4);
            result = jb44;
            
        }};

        // Unit under test is exercised.
        TreeMap<String, TransporterClient> transporterClientTreeMap = new TreeMap<>();
        transporterClientTreeMap.put(NAME + 1, transporterClient1);
        transporterClientTreeMap.put(NAME + 2, transporterClient2);
        transporterClientTreeMap.put(NAME + 3, transporterClient3);
        transporterClientTreeMap.put(NAME + 4, transporterClient4);

        BrokerPort mBrokerPort = new BrokerPort(transporterClientTreeMap);

        String returnedFromTest = mBrokerPort.requestTransport(ORIGIN, DESTINATION, CUSTOMER_PRICE);

        // One or more invocations to mocked types, causing expectations to be verified.
        new Verifications() {{
            // Verifies that zero or one invocations occurred, with the specified argument value:
           transporterClient1.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient2.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient3.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient4.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            //transporterClient1.decideJob(IDs[0], true); maxTimes = 0;
          //  transporterClient1.decideJob(IDs[0], false); maxTimes = 1;
            //transporterClient2.decideJob(IDs[1], true); maxTimes = 0;
            //transporterClient2.decideJob(IDs[1], false); maxTimes = 1;
            transporterClient3.decideJob(IDs[2], true); maxTimes = 1;
            //transporterClient4.decideJob(IDs[3], true); maxTimes = 0;
            //transporterClient4.decideJob(IDs[3], false); maxTimes = 1;
        }};


        // Additional verification code, if any, either here or before the verification block.
        final String EXPECTED = String.format("%s", IDs[2]);
        assertEquals("The Correct ID was not returned.", EXPECTED, returnedFromTest);

        List<TransportView> transports = mBrokerPort.listTransports();
        boolean itWorks = false;

        for(TransportView tv : transports){
            if(tv.getId().equals(EXPECTED)){
                itWorks = true;
                assertEquals("Job wasn't booked.", TransportStateView.BOOKED, tv.getState());
            }else
                assertEquals("Job wasn't set as failed.", TransportStateView.FAILED, tv.getState());
        }

        assertEquals("Job wasn't listed.", itWorks, true);

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
