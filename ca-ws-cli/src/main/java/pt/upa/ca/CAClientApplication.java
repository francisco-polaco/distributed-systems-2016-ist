package pt.upa.ca;

import pt.upa.ca.ws.CA;
import pt.upa.ca.ws.cli.CAClient;

import javax.xml.registry.JAXRException;

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


        // Start CA
        CAClient CAClient = new CAClient();
        //CAClient.getEntityCertificate("Broker");
    }
}
