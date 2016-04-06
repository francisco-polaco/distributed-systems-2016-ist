package pt.upa.transporter.ws;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by xxlxpto on 06-04-2016.
 */
public class TransporterPortTest implements AbstractTest {

    private TransporterPort mTransporterPort;

    @Before
    @Override
    public void setUp() {
        mTransporterPort = new TransporterPort("UpaTransporter1");
    }

    @After
    @Override
    public void tearDown() {
        mTransporterPort = null;
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
