package pt.upa.transporter.ws;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class TransporterPortTest {

    private static TransporterPort mTransporterPortImp;
    private static TransporterPort mTransporterPortPar;
    private static String _idPar;
    private static String _idImp;

    @BeforeClass
    public static void oneTimeSetUp() {
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }

    @BeforeClass
    public static void setUp() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortImp = new TransporterPort("UpaTransporter1");
        mTransporterPortPar = new TransporterPort("UpaTransporter2");
        _idImp = mTransporterPortImp.requestJob("Lisboa", "Leiria",9).getJobIdentifier();
        _idPar = mTransporterPortPar.requestJob("Leiria", "Leiria",8).getJobIdentifier();
    }

    @AfterClass
    public static void tearDown() {
        mTransporterPortPar = null;
        mTransporterPortImp = null;
    }

   /* @Test(expected = BadLocationFault_Exception.class)
    public void jobWithInvalidOrigin() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortImp.requestJob("Lisboooa", "Braga", 50);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void jobWithInvalidDestination() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortImp.requestJob("Lisboa", "Bragaa", 50);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void jobWithNullDestination() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortImp.requestJob("Lisboa", null, 50);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void jobWithNullOrigin() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortImp.requestJob(null, "Braga", 50);
    }

    @Test(expected = BadPriceFault_Exception.class)
    public void jobWithInvalidPrice() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortImp.requestJob("Faro", "Lisboa", -5);
    }

    @Test(expected = BadJobFault_Exception.class)
    public void decideJobWithNullID() throws BadJobFault_Exception {
        mTransporterPortImp.decideJob(null, false);
    }

    @Test(expected = BadJobFault_Exception.class)
    public void decideJobWithWrongID() throws BadJobFault_Exception {
        mTransporterPortImp.decideJob("wrongid", false);
    }

    @Test
    public void transporterPortWithNullArg() {
        TransporterPort t = new TransporterPort(null);
        assertNotNull(t);
    }

    @Test
    public void ping() {
        String t = mTransporterPortImp.ping("test");
        assertNotNull(t);
    }

    @Test
    public void pingWithNullArg() {
        String t = mTransporterPortImp.ping(null);
        assertNotNull(t);
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
    public void listJob() {
        assertEquals("List doesn't have the right Jobs.", mTransporterPortPar.listJobs().size(), 3);
    }

    @Test
    public void clearJob(){
        mTransporterPortPar.clearJobs();
        assertEquals("Didn't delete all jobs.", mTransporterPortPar.listJobs().size(), 0);
    }

    @Test
    public void verifyState() throws BadJobFault_Exception, InterruptedException {
        int i;
        TreeMap<Integer, Boolean> states = new TreeMap<>();
        JobView work = mTransporterPortImp.decideJob(_idImp, true);
        for (int j=1; j < 5; j++)
            states.put(j, false);
       for(i = 0; i < 3000  ; i++){
            String _state = work.getJobState().value();
            if (_state.equals("HEADING")) {
                states.put(1, true);
            }
            else if (_state.equals("ONGOING")) {
                states.put(2, true);
            }
            else if (_state.equals("COMPLETED")) {
                states.put(3, true);
            }
            Thread.sleep(5);
        }
        if(states.get(1).equals(true) && states.get(2).equals(true) && states.get(3).equals(true))
            states.put(4,true);
        assertTrue("Failled completing the job.", states.get(4));
    }

    @Test
    public void rejectJob() throws BadJobFault_Exception {
        JobView work = mTransporterPortPar.decideJob(_idPar, false);
        assertEquals("Job not rejected ", work.getJobState().value(), "REJECTED");
    }

    @Test
    public void jobWasCreated() throws BadLocationFault_Exception, BadPriceFault_Exception {
        JobView test;
        test = mTransporterPortImp.requestJob("Porto", "Lisboa", 50);
        assertEquals("Job was not created successfully", test, mTransporterPortImp.jobStatus(test.getJobIdentifier()));
    }

    @Test
    public void jobStatusNullID() throws BadLocationFault_Exception, BadPriceFault_Exception {
        assertNull(mTransporterPortImp.jobStatus(null));
    }

    @Test
    public void jobWithPriceAbove100() throws BadLocationFault_Exception, BadPriceFault_Exception {
        JobView test;
        test = mTransporterPortPar.requestJob("Porto", "Lisboa", 200);
        assertNull("JobView was not null.", test);
    }*/


 //-------------------------- IMPAR ---------------------------------------------------------------------------

    @Test
    public void requestJobPriceOddImp() throws BadLocationFault_Exception, BadPriceFault_Exception {
        boolean lowprice = false;
        int price =  mTransporterPortImp.requestJob("Lisboa", "Leiria",19).getJobPrice();
        if ( price < 19)
            lowprice = true;
        assertTrue("Price is above the client price", lowprice);
    }

/*    @Test
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
    }*/

//-------------------------------------------------------------------------------------------------------------
}
