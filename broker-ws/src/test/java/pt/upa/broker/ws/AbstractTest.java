package pt.upa.broker.ws;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

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
    public void setUp();

    @After
    public void tearDown();

}