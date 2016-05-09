package pt.upa.transporter;

import pt.upa.handler.UpaHandler;
import pt.upa.transporter.ws.cli.TransporterClient;

public class TransporterClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");
		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", TransporterClient.class.getName());
			return;
		}
		TransporterClient clnt = new TransporterClient(args[0], args[1]);
		UpaHandler.handlerConstants.SENDER_SERVICE_NAME = "UpaBroker1";

		System.out.println(clnt.ping("Hello"));

		System.out.println("Goodbye!");
	}
}
