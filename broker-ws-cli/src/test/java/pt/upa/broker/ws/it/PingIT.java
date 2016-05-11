package pt.upa.broker.ws.it;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import pt.upa.broker.ws.cli.BrokerClientException;
import pt.upa.broker.ws.cli.ConnectionTimeOutException;

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
