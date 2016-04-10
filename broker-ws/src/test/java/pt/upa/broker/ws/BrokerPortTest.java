package pt.upa.broker.ws;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPort;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by xxlxpto on 06-04-2016.
 */
public class BrokerPortTest implements AbstractTest {

    private BrokerPort mBrokerPort;

    @Before
    @Override
    public void setUp() {
        /*mTransporterPort = new TransporterClient("UpaTransporter1");*/
    }

    @After
    @Override
    public void tearDown() {
        mBrokerPort = null;
    }

    @Test
    public void testMocks(@Mocked final TransporterClient transporterClient) throws Exception {

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
        BrokerPort brokerPort = new BrokerPort(transporterClientTreeMap);

        String returnedFromTest = brokerPort.requestTransport(ORIGIN, DESTINATION, PRICE);

        // One or more invocations to mocked types, causing expectations to be verified.
        new Verifications() {{
            // Verifies that zero or one invocations occurred, with the specified argument value:
            transporterClient.requestJob(ORIGIN, DESTINATION, PRICE); maxTimes = 1;
            transporterClient.decideJob(ID, true); maxTimes = 1;
        }};

        // Additional verification code, if any, either here or before the verification block.
        final String EXPECTED = String.format("%s",
                ID);
        assertEquals(EXPECTED, returnedFromTest);
    }

   /* @Test
    public void jobWasCreated() throws BadLocationFault_Exception, BadPriceFault_Exception {
        JobView test;
        test = mTransporterPort.requestJob("Porto", "Lisboa", 50);
        assertEquals("Job was not created successfully", test, mTransporterPort.jobStatus(test.getJobIdentifier()));

    }

    @Test
    public void jobWithPriceAbove100() throws BadLocationFault_Exception, BadPriceFault_Exception {
        JobView test;
        test = mTransporterPort.requestJob("Porto", "Lisboa", 200);
        assertNull("JobView was not null.", test);
    }

    *//* Como e que se faz um test para ver se o preco orcamentado e menor ?*//*

    @Test(expected = BadLocationFault_Exception.class)
    public void jobWithInvalidOrigin() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPort.requestJob("Orgrimmar", "Lisboa", 50);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void jobWithInvalidDestination() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPort.requestJob("Porto", "Stormwind", 50);
    }

    @Test(expected = BadPriceFault_Exception.class)
    public void jobWithInvalidPrice() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPort.requestJob("Porto", "Lisboa", -5);
    }
*/
}
