package pt.upa.transporter.ws.it;

import org.junit.*;

import javax.xml.registry.JAXRException;

import static org.junit.Assert.*;

/** Test suite */
public interface AbstractTest {

    // static members

    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        // TODO
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // TODO
    }

    // members

    // initialization and clean-up for each test
    @Before
    public void setUp() throws JAXRException;

    @After
    public void tearDown();

}