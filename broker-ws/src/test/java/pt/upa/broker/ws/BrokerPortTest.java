package pt.upa.broker.ws;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.*;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;



public class BrokerPortTest {


    private static final int[] PRICES = new int[]{25, 35, 5, 50};
    private static final int CUSTOMER_PRICE = 27;
    private static final String NAME = "UpaTransporter";
    private static final String[] IDs = new String[]{"0", "1", "2", "3"};
    private static final String DESTINATION = "Porto";
    private static final String ORIGIN = "Lisboa";

    @BeforeClass
    public static void oneTimeSetUp() {
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {

    }


    /* =======================================================
    *             Auxiliary functions
    *  ======================================================= */

    private ConcurrentHashMap<String, TransporterClient> getTransporterClientTreeMap(@Mocked @Injectable TransporterClient transporterClient1, @Mocked @Injectable TransporterClient transporterClient2, @Mocked @Injectable TransporterClient transporterClient3, @Mocked @Injectable TransporterClient transporterClient4) {
        ConcurrentHashMap<String, TransporterClient> transporterClientTreeMap = new ConcurrentHashMap<>();
        transporterClientTreeMap.put(NAME + 1, transporterClient1);
        transporterClientTreeMap.put(NAME + 2, transporterClient2);
        transporterClientTreeMap.put(NAME + 3, transporterClient3);
        transporterClientTreeMap.put(NAME + 4, transporterClient4);
        return transporterClientTreeMap;
    }

    private JobView createJobView(JobStateView state, int price, String destination, String origin, String id, String companyName){
        JobView jb = new JobView();
        jb.setJobState(state);
        jb.setJobPrice(price);
        jb.setJobDestination(destination);
        jb.setJobOrigin(origin);
        jb.setJobIdentifier(id);
        jb.setCompanyName(companyName);
        return jb;
    }

    private void finalAssert(String id, BrokerPort mBrokerPort, String returnedFromTest) {
        final String EXPECTED = String.format("%s", id);
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
    *            Tests that only requires one Transporter
    *  ======================================================= */

    @Test
    public void simpleAccept(@Mocked final TransporterClient transporterClient) throws Exception {
        // Preparation code not specific to JMockit, if any.
        final String ID = "1";
        final int PRICE = 7;

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new Expectations() {{
            transporterClient.requestJob(ORIGIN, DESTINATION, PRICE);
            result = createJobView(JobStateView.PROPOSED, PRICE, DESTINATION, ORIGIN, ID, NAME + 1);
            transporterClient.decideJob(ID, true);
            JobView jb2 = createJobView(JobStateView.ACCEPTED, PRICE, DESTINATION, ORIGIN, ID, NAME + 1);
            result = jb2;
            transporterClient.jobStatus(ID);
            result = jb2;
        }};

        // Unit under test is exercised.
        ConcurrentHashMap<String, TransporterClient> transporterClientTreeMap = new ConcurrentHashMap<>();
        transporterClientTreeMap.put(NAME + 1, transporterClient);
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
        final int PRICE = 7;

        new Expectations() {{
            transporterClient.requestJob(ORIGIN, DESTINATION, PRICE);
            result = null;
        }};

        // Unit under test is exercised.
        ConcurrentHashMap<String, TransporterClient> transporterClientTreeMap = new ConcurrentHashMap<>();
        transporterClientTreeMap.put(NAME + 1, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        brokerPort.requestTransport(ORIGIN, DESTINATION, PRICE);

        new Verifications() {{
            transporterClient.requestJob(ORIGIN, DESTINATION, PRICE); maxTimes = 1;
        }};
    }


    @Test(expected = UnknownTransportFault_Exception.class)
    public void viewTransportWithNullID(@Mocked final TransporterClient transporterClient) throws Exception {

        // Unit under test is exercised.
        ConcurrentHashMap<String, TransporterClient> transporterClientTreeMap = new ConcurrentHashMap<>();
        transporterClientTreeMap.put(NAME + 1, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        brokerPort.viewTransport(null);
    }

    @Test(expected = UnknownTransportFault_Exception.class)
    public void viewTransportWithWrongID(@Mocked final TransporterClient transporterClient) throws Exception {

        // Unit under test is exercised.
        ConcurrentHashMap<String, TransporterClient> transporterClientTreeMap = new ConcurrentHashMap<>();
        transporterClientTreeMap.put(NAME + 1, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        brokerPort.viewTransport("WrongID");
    }


    @Test(expected = UnknownLocationFault_Exception.class)
    public void wrongDestination(@Mocked final TransporterClient transporterClient) throws Exception {

        final String WRONG_DESTINATION = "Canal Caveira";
        final int PRICE = 7;


        // Unit under test is exercised.
        ConcurrentHashMap<String, TransporterClient> transporterClientTreeMap = new ConcurrentHashMap<>();
        transporterClientTreeMap.put(NAME + 1, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        brokerPort.requestTransport(ORIGIN, WRONG_DESTINATION, PRICE);
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void wrongOrigin(@Mocked final TransporterClient transporterClient) throws Exception {

        final String WRONG_ORIGIN = "Corral de Moinas";
        final int PRICE = 7;


        // Unit under test is exercised.
        ConcurrentHashMap<String, TransporterClient> transporterClientTreeMap = new ConcurrentHashMap<>();
        transporterClientTreeMap.put(NAME + 1, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        brokerPort.requestTransport(WRONG_ORIGIN, DESTINATION, PRICE);


    }

    @Test(expected = InvalidPriceFault_Exception.class)
    public void wrongPrice(@Mocked final TransporterClient transporterClient) throws Exception {

        final int WRONG_PRICE = -7;

        // Unit under test is exercised.
        ConcurrentHashMap<String, TransporterClient> transporterClientTreeMap = new ConcurrentHashMap<>();
        transporterClientTreeMap.put(NAME + 1, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        brokerPort.requestTransport(ORIGIN, DESTINATION, WRONG_PRICE);


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

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new Expectations() {{
            transporterClient1.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE);
            result = createJobView(JobStateView.PROPOSED, PRICES[0], DESTINATION, ORIGIN, IDs[0], NAME + 1);

            transporterClient2.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE);
            result = createJobView(JobStateView.PROPOSED, PRICES[1], DESTINATION, ORIGIN, IDs[1], NAME + 2);

            transporterClient3.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); // It should pick this
            result = createJobView(JobStateView.PROPOSED, PRICES[2], DESTINATION, ORIGIN, IDs[2], NAME + 3);

            transporterClient4.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE);
            result = createJobView(JobStateView.PROPOSED, PRICES[3], DESTINATION, ORIGIN, IDs[3], NAME + 4);

            transporterClient1.decideJob(IDs[0], false);
            JobView jb11 = createJobView(JobStateView.REJECTED, PRICES[0], DESTINATION, ORIGIN, IDs[0], NAME + 1);
            result = jb11;
            transporterClient1.jobStatus(IDs[0]);
            result = jb11;

            transporterClient2.decideJob(IDs[1], false);
            JobView jb22 = createJobView(JobStateView.REJECTED, PRICES[1], DESTINATION, ORIGIN, IDs[1], NAME + 2);
            result = jb22;
            transporterClient2.jobStatus(IDs[1]);
            result = jb22;

            transporterClient3.decideJob(IDs[2], true);
            JobView jb33 = createJobView(JobStateView.ACCEPTED, PRICES[2], DESTINATION, ORIGIN, IDs[2], NAME + 3);
            result = jb33;
            transporterClient3.jobStatus(IDs[2]);
            result = jb33;

            transporterClient4.decideJob(IDs[3], false);
            JobView jb44 = createJobView(JobStateView.REJECTED, PRICES[3], DESTINATION, ORIGIN, IDs[3], NAME + 4);
            result = jb44;
            transporterClient4.jobStatus(IDs[3]);
            result = jb44;

        }};

        // Unit under test is exercised.

        BrokerPort mBrokerPort = new BrokerPort(getTransporterClientTreeMap(transporterClient1, transporterClient2, transporterClient3, transporterClient4));

        String returnedFromTest = mBrokerPort.requestTransport(ORIGIN, DESTINATION, CUSTOMER_PRICE);

        // One or more invocations to mocked types, causing expectations to be verified.
        new Verifications() {{
            // Verifies that zero or one invocations occurred, with the specified argument value:
            transporterClient1.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient2.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient3.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient4.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient1.decideJob(IDs[0], true); maxTimes = 0;
            transporterClient1.decideJob(IDs[0], false); maxTimes = 1;
            transporterClient2.decideJob(IDs[1], true); maxTimes = 0;
            transporterClient2.decideJob(IDs[1], false); maxTimes = 1;
            transporterClient3.decideJob(IDs[2], true); maxTimes = 1;
            transporterClient4.decideJob(IDs[3], true); maxTimes = 0;
            transporterClient4.decideJob(IDs[3], false); maxTimes = 1;
        }};


        // Additional verification code, if any, either here or before the verification block.
        finalAssert(IDs[2], mBrokerPort, returnedFromTest);

    }



    @Test
    public void multipleWithSomeRejections(@Mocked @Injectable final TransporterClient transporterClient1,
                               @Mocked @Injectable final TransporterClient transporterClient2,
                               @Mocked @Injectable final TransporterClient transporterClient3,
                               @Mocked @Injectable final TransporterClient transporterClient4) throws Exception {


        new Expectations() {{
            transporterClient1.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); // It should pick this
            result = createJobView(JobStateView.PROPOSED, PRICES[0], DESTINATION, ORIGIN, IDs[0], NAME + 1);

            transporterClient2.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE);
            result = createJobView(JobStateView.PROPOSED, PRICES[1], DESTINATION, ORIGIN, IDs[1], NAME + 2);

            transporterClient3.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE);
            result = null;

            transporterClient4.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE);
            result = createJobView(JobStateView.PROPOSED, PRICES[3], DESTINATION, ORIGIN, IDs[3], NAME + 4);

            transporterClient1.decideJob(IDs[0], true);
            JobView jb11 = createJobView(JobStateView.ACCEPTED, PRICES[0], DESTINATION, ORIGIN, IDs[0], NAME + 1);
            result = jb11;
            transporterClient1.jobStatus(IDs[0]);
            result = jb11;


            transporterClient2.decideJob(IDs[1], false);
            JobView jb22 = createJobView(JobStateView.REJECTED, PRICES[1], DESTINATION, ORIGIN, IDs[1], NAME + 2);
            result = jb22;
            transporterClient2.jobStatus(IDs[1]);
            result = jb22;


            transporterClient4.decideJob(IDs[3], false);
            JobView jb44 = createJobView(JobStateView.REJECTED, PRICES[3], DESTINATION, ORIGIN, IDs[3], NAME + 4);
            result = jb44;
            transporterClient4.jobStatus(IDs[3]);
            result = jb44;

        }};

        // Unit under test is exercised.

        BrokerPort mBrokerPort = new BrokerPort(getTransporterClientTreeMap(transporterClient1, transporterClient2, transporterClient3, transporterClient4));

        String returnedFromTest = mBrokerPort.requestTransport(ORIGIN, DESTINATION, CUSTOMER_PRICE);

        // One or more invocations to mocked types, causing expectations to be verified.
        new Verifications() {{
            // Verifies that zero or one invocations occurred, with the specified argument value:
            transporterClient1.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient2.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient3.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient4.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient1.decideJob(IDs[0], true); maxTimes = 1;
            transporterClient1.decideJob(IDs[0], false); maxTimes = 0;
            transporterClient2.decideJob(IDs[1], true); maxTimes = 0;
            transporterClient2.decideJob(IDs[1], false); maxTimes = 1;
            transporterClient3.decideJob(IDs[2], true); maxTimes = 0;
            transporterClient4.decideJob(IDs[3], true); maxTimes = 0;
            transporterClient4.decideJob(IDs[3], false); maxTimes = 1;
        }};


        // Additional verification code, if any, either here or before the verification block.
        finalAssert(IDs[1], mBrokerPort, returnedFromTest);

    }

    @Test(expected = UnavailableTransportPriceFault_Exception.class)
    public void multipleWithInflatedPrices(@Mocked @Injectable final TransporterClient transporterClient1,
                                           @Mocked @Injectable final TransporterClient transporterClient2,
                                           @Mocked @Injectable final TransporterClient transporterClient3,
                                           @Mocked @Injectable final TransporterClient transporterClient4) throws Exception {

        final int[] PRICES = { 50, 36, 70, 50};


        new Expectations() {{
            transporterClient1.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE);
            result = createJobView(JobStateView.PROPOSED, PRICES[0], DESTINATION, ORIGIN, IDs[0], NAME + 1);

            transporterClient2.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE);
            result = createJobView(JobStateView.PROPOSED, PRICES[1], DESTINATION, ORIGIN, IDs[1], NAME + 2);

            transporterClient3.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE);
            result = createJobView(JobStateView.PROPOSED, PRICES[2], DESTINATION, ORIGIN, IDs[2], NAME + 3);

            transporterClient4.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE);
            result = createJobView(JobStateView.PROPOSED, PRICES[3], DESTINATION, ORIGIN, IDs[3], NAME + 4);

            transporterClient1.decideJob(IDs[0], false);
            result = createJobView(JobStateView.REJECTED, PRICES[0], DESTINATION, ORIGIN, IDs[0], NAME + 1);


            transporterClient2.decideJob(IDs[1], false);
            result = createJobView(JobStateView.REJECTED, PRICES[1], DESTINATION, ORIGIN, IDs[1], NAME + 2);

            transporterClient3.decideJob(IDs[2], false);
            result = createJobView(JobStateView.REJECTED, PRICES[2], DESTINATION, ORIGIN, IDs[2], NAME + 3);

            transporterClient4.decideJob(IDs[3], false);
            result = createJobView(JobStateView.REJECTED, PRICES[3], DESTINATION, ORIGIN, IDs[3], NAME + 4);


        }};


        BrokerPort mBrokerPort = new BrokerPort(getTransporterClientTreeMap(transporterClient1, transporterClient2, transporterClient3, transporterClient4));

        mBrokerPort.requestTransport(ORIGIN, DESTINATION, CUSTOMER_PRICE);

        new Verifications() {{

            transporterClient1.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient2.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient3.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient4.requestJob(ORIGIN, DESTINATION, CUSTOMER_PRICE); maxTimes = 1;
            transporterClient1.decideJob(IDs[0], true); maxTimes = 0;
            transporterClient1.decideJob(IDs[0], false); maxTimes = 1;
            transporterClient2.decideJob(IDs[1], true); maxTimes = 0;
            transporterClient2.decideJob(IDs[1], false); maxTimes = 1;
            transporterClient3.decideJob(IDs[2], true); maxTimes = 0;
            transporterClient3.decideJob(IDs[2], false); maxTimes = 1;
            transporterClient4.decideJob(IDs[3], true); maxTimes = 0;
            transporterClient4.decideJob(IDs[3], false); maxTimes = 1;
        }};


    }


    /* =======================================================
    *                       Small tests
    *  ======================================================= */
    @Test
    public void pingTest(@Mocked final TransporterClient transporterClient){

        final String HELLO = "Hello!";
        final String HOLA = "Holla!";
        new Expectations() {{
            transporterClient.ping(HELLO);
            result = HOLA;
        }};

        // Unit under test is exercised.
        ConcurrentHashMap<String, TransporterClient> transporterClientTreeMap = new ConcurrentHashMap<>();
        transporterClientTreeMap.put(NAME + 1, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        assertNotNull("Server didn't answer", brokerPort.ping(HELLO));
    }

    @Test
    public void listTest(@Mocked final TransporterClient transporterClient){



        // Unit under test is exercised.
        ConcurrentHashMap<String, TransporterClient> transporterClientTreeMap = new ConcurrentHashMap<>();
        transporterClientTreeMap.put(NAME + 1, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        List<TransportView> returnedFromTest = brokerPort.listTransports();

        assertNotNull("List was null", returnedFromTest);

    }

    @Test
    public void clearTest(@Mocked final TransporterClient transporterClient){
        final String NAME = "UpaTransporter1";


        // Unit under test is exercised.
        ConcurrentHashMap<String, TransporterClient> transporterClientTreeMap = new ConcurrentHashMap<>();
        transporterClientTreeMap.put(NAME + 1, transporterClient);
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        brokerPort.clearTransports();
        List<TransportView> returnedFromTest = brokerPort.listTransports();


        assertEquals("List wasn't cleared", returnedFromTest.size(), 0);

    }
}
