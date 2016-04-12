package pt.upa.transporter.ws;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
        mTransporterPortPar.requestJob("Espanha", "Lisboa", 50);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void jobWithInvalidDestination() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortPar.requestJob("Lisboa", "Praga", 50);
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
    public void listJobImp(){
        assertEquals("List doesn't have the right Jobs.", mTransporterPortImp.listJobs().size(), 1);
    }

    @Test
    public void requestJobPriceEven(){
        //int price =  mTransporterPortImp.requestJob("Lisboa", "Leiria",9).getJobPrice();
        assertEquals("List doesn't have the right Jobs.", mTransporterPortImp.listJobs().size(), 1);
    }

    @Test
    public void JobImp(){
        assertNotNull("Job didn't exist.",  mTransporterPortImp.jobStatus(_idImp));
    }

    @Test
    public void jobWithInvalidOriginImp(){
       assertNull("Origin out of range", mTransporterPortImp.requestJob("Port", "Lisboa", 50));
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void jobWithInvalidDestinationImp() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortImp.requestJob("Lisboa", "Braga", 50);
    }

    @Test(expected = BadPriceFault_Exception.class)
    public void jobWithInvalidPriceImp() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortImp.requestJob("Faro", "Lisboa", -5);
    }

    @Test
    public void clearJobImp(){
        mTransporterPortImp.clearJobs();
        assertEquals("Didn't delete all jobs.", mTransporterPortImp.listJobs().size(), 0);
    }

//-------------------------------------------------------------------------------------------------------------

//-------------------------- Par ---------------------------------------------------------------------------
    @Test(expected = BadLocationFault_Exception.class)
    public void jobWithInvalidOriginPar() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortPar.requestJob("Beja", "Lisboa", 50);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void jobWithInvalidDestinationPar() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortPar.requestJob("Lisboa", "Faro", 50);
    }

    @Test(expected = BadPriceFault_Exception.class)
    public void jobWithInvalidPricePar() throws BadLocationFault_Exception, BadPriceFault_Exception {
        mTransporterPortPar.requestJob("Porto", "Viseu", -5);
    }

    @Test
    public void JobPar(){
        assertNotNull("Job didn't exist.",  mTransporterPortPar.jobStatus(_idPar));
    }

    @Test
    public void listJobPar(){
        assertEquals("List doesn't have the right Jobs.", mTransporterPortPar.listJobs().size(), 1);
    }

    @Test
    public void clearJobPar(){
        mTransporterPortPar.clearJobs();
        assertEquals("Didn't delete all jobs.", mTransporterPortPar.listJobs().size(), 0);
    }

//-------------------------------------------------------------------------------------------------------------
}
