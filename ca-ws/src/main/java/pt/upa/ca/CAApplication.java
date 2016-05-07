package pt.upa.ca;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ca.ws.CAImplemention;
import pt.upa.ca.ws.X509CertificateCheck;

import javax.xml.ws.Endpoint;
import java.util.Collection;
import java.util.List;

/**
 * Created by xxlxpto on 06-05-2016.
 */
public class CAApplication {
    public static void main(String[] args){
        System.out.println("CA starting...");
        if (args.length < 3) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL wsName wsURL%n", CAApplication.class.getName());
            return;
        }
        try {
            new X509CertificateCheck().main(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String uddiURL = args[0];
        String name = args[1];
        String url = args[2];

        Endpoint endpoint = null;
        UDDINaming uddiNaming = null;
        try {
            endpoint = Endpoint.create(new CAImplemention());
            // publish endpoint
            System.out.printf("Starting %s%n", url);
            endpoint.publish(url);

            // publish to UDDI
            System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
            uddiNaming = new UDDINaming(uddiURL);

            // To clean uddi, comment after
            uddiNaming.unbind("UpaTransporter3");
            uddiNaming.unbind("UpaTransporter2");
            uddiNaming.unbind("UpaTransporter1");
            uddiNaming.unbind("UpaBroker");

            uddiNaming.rebind(name, url);

            // wait
            System.out.println("Awaiting connections");
            System.out.println("Press enter to shutdown");
            System.in.read();

        } catch (Exception e) {
            System.out.printf("Caught exception: %s%n", e);
            e.printStackTrace();

        } finally {
            try {
                if (endpoint != null) {
                    // stop endpoint
                    endpoint.stop();
                    System.out.printf("Stopped %s%n", url);
                }
            } catch (Exception e) {
                System.out.printf("Caught exception when stopping: %s%n", e);
            }
            try {
                if (uddiNaming != null) {
                    // delete from UDDI
                    uddiNaming.unbind(name);
                    System.out.printf("Deleted '%s' from UDDI%n", name);
                }
            } catch (Exception e) {
                System.out.printf("Caught exception when deleting: %s%n", e);
            }
        }
    }
}
