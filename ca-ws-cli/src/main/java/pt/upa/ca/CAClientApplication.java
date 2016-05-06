package pt.upa.ca;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ca.ws.CA;
import pt.upa.ca.ws.CAImplementionService;
import pt.upa.ca.ws.cli.Cli;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

/**
 * Created by xxlxpto on 06-05-2016.
 */
public class CAClientApplication {
    public static void main(String[] args) throws JAXRException {
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL name%n", CA.class.getName());
            return;
        }
        CAImplementionService tttImplService = new CAImplementionService();
        CA port = tttImplService.getCAImplementionPort();
        String uddiURL = args[0];
        String name = args[1];

        System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);

        System.out.printf("Looking for '%s'%n", name);
        String endpointAddress = uddiNaming.lookup(name);

        if (endpointAddress == null) {
            System.out.println("Not found!");
            return;
        } else {
            System.out.printf("Found %s%n", endpointAddress);
        }

        System.out.println("Creating stub ...");
        // ...

        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

        // Start CA
        Cli cli = new Cli(port);
        //cli.getEntityCertificate("Broker");
    }
}
