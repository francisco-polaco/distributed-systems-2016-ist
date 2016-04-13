package pt.upa.transporter.ws;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by xxlxpto on 06-04-2016.
 */
public class TransporterPortTest implements AbstractTest {

    private TransporterPort mTransporterPortImp;
    private TransporterPort mTransporterPortPar;
    private String _idPar;
    private String _idImp;

    @Before
    @Override
    public void setUp() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortImp = new TransporterPort("UpaTransporter1");
        mTransporterPortPar = new TransporterPort("UpaTransporter2");
        _idImp = mTransporterPortImp.requestJob("Lisboa", "Leiria",9).getJobIdentifier();
        _idPar = mTransporterPortPar.requestJob("Leiria", "Leiria",8).getJobIdentifier();
    }

    @After
    @Override
    public void tearDown() {
        mTransporterPortPar = null;
        mTransporterPortImp = null;
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void jobWithInvalidOrigin() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortImp.requestJob("Lisboooa", "Braga", 50);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void jobWithInvalidDestination() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortImp.requestJob("Lisboa", "Bragaa", 50);
    }

    @Test(expected = BadPriceFault_Exception.class)
    public void jobWithInvalidPrice() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortImp.requestJob("Faro", "Lisboa", -5);
    }

    @Test
    public void jobWithPriceAbove() throws BadLocationFault_Exception, BadPriceFault_Exception {
        assertNull("Price Bellow 100", mTransporterPortImp.requestJob("Faro", "Lisboa", 101));
    }

    @Test
    public void getJob(){
        assertNotNull("Job didn't exist.",  mTransporterPortPar.jobStatus(_idPar));
    }

    @Test
    public void listJob(){
        assertEquals("List doesn't have the right Jobs.", mTransporterPortPar.listJobs().size(), 1);
    }

    @Test
    public void clearJob(){
        mTransporterPortPar.clearJobs();
        assertEquals("Didn't delete all jobs.", mTransporterPortPar.listJobs().size(), 0);
    }

   /* @Test
    public void jobWasCreated() throws BadLocationFault_Exception, BadPriceFault_Exception {
        assertEquals("Job was not created successfully", test, mTransporterPort.jobStatus(test.getJobIdentifier()));*/

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
*/

 //-------------------------- IMPAR ---------------------------------------------------------------------------

    @Test
    public void requestJobPriceOddImp() throws BadLocationFault_Exception, BadPriceFault_Exception {
        boolean lowprice = false;
        int price =  mTransporterPortImp.requestJob("Lisboa", "Leiria",19).getJobPrice();
        if ( price < 19)
            lowprice = true;
        assertTrue("Price is above the client price", lowprice);
    }

    @Test
    public void requestJobPriceEvenImp() throws BadLocationFault_Exception, BadPriceFault_Exception {
        boolean  highprice = false;
        int price =  mTransporterPortImp.requestJob("Lisboa", "Leiria",18).getJobPrice();
        if ( price > 18)
            highprice = true;
        assertTrue("Price is below the client price",  highprice);
    }

    @Test
    public void jobWithInvalidRangeImp() throws BadLocationFault_Exception, BadPriceFault_Exception {
       assertNull("Origin out of range", mTransporterPortImp.requestJob("Porto", "Braga", 50));
    }

//-------------------------------------------------------------------------------------------------------------

//-------------------------- Par ---------------------------------------------------------------------------

    @Test
    public void requestJobPriceOddPar() throws BadLocationFault_Exception, BadPriceFault_Exception {
        boolean  highprice = false;
        int price =  mTransporterPortPar.requestJob("Lisboa", "Leiria",19).getJobPrice();
        if ( price > 19)
            highprice = true;
        assertTrue("Price is below the client price",  highprice);
    }

    @Test
    public void requestJobPriceEvenPar() throws BadLocationFault_Exception, BadPriceFault_Exception {
        boolean lowprice = false;
        int price =  mTransporterPortPar.requestJob("Lisboa", "Leiria",18).getJobPrice();
        if ( price < 18)
            lowprice = true;
        assertTrue("Price is above the client price", lowprice);
    }

    @Test
    public void jobWithInvalidRangePar() throws BadLocationFault_Exception, BadPriceFault_Exception {
       assertNull("Origin out of range", mTransporterPortPar.requestJob("Faro", "Beja", 50));
    }

//-------------------------------------------------------------------------------------------------------------
}
