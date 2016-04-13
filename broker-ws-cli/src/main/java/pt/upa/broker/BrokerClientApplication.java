package pt.upa.broker;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.TransportView;
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


		System.out.println("============= PING =============");
		System.out.println(clnt.ping("Teste"));
		System.out.println("================================");
		clnt.clearTransports();
		String workId = clnt.requestTransport("Lisboa", "Porto", 45);
		System.out.println("ID0: " + workId);
		TransportView t = clnt.viewTransport(workId);
		
		System.out.println("O id " + t.getId()  + " da companhia "  + t.getTransporterCompany()+ " com o preço "
				+ t.getPrice() + " esta com o estado " + t.getState());
		String workId2 = clnt.requestTransport("Porto", "Braga", 20);
		TransportView t2 = clnt.viewTransport(workId2);
		System.out.println("O id " + t2.getId()  + " da companhia "  + t2.getTransporterCompany()+ " com o preço "
				+ t2.getPrice() + " esta com o estado " + t2.getState());

		System.out.println("Goodbye!");
	}
}
