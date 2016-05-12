package pt.upa.broker;

import pt.upa.broker.ws.cli.BrokerClient;

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
		System.out.println("starting");
		clnt.ping("hi");
		System.out.println("Hello");
		String a = clnt.requestTransport("Lisboa", "Faro", 8);
		System.out.println("requested");
		clnt.viewTransport(a);
		System.out.println("viewed");
		clnt.clearTransports();
		System.out.println("cleared");
		System.out.println("Goodbye!");
	}
}
