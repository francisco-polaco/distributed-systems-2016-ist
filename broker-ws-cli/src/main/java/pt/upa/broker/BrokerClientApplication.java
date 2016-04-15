package pt.upa.broker;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.BrokerClient;

import javax.xml.ws.BindingProvider;

import java.util.Map;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class BrokerClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");
		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", BrokerClient.class.getName());
			return;
		}
		BrokerClient clnt = new BrokerClient(args[0], args[1]);
		System.out.println(clnt.ping("Hello"));

		System.out.println("Goodbye!");
	}
}
