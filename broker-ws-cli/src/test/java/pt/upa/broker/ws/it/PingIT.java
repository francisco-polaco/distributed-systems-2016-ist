package pt.upa.broker.ws.it;

import org.junit.Test;
import pt.upa.broker.ws.cli.ConnectionTimeOutException;

import static org.junit.Assert.assertNotNull;

/**
 * Test suite
 */
public class PingIT extends AbstractIT {

	// tests
	// assertEquals(expected, actual);

	// public String ping(String x)

	@Test
	public void pingTest() throws ConnectionTimeOutException {
		assertNotNull(CLIENT.ping("test"));
	}

}
