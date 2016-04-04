package pt.upa.transporter.ws;

import org.junit.*;
import static org.junit.Assert.*;

/** Test suite */
public abstract class AbstractTest {

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
    public abstract void setUp();

    @After
    public abstract void tearDown();

    // tests
    @Test
    public abstract void test();
}