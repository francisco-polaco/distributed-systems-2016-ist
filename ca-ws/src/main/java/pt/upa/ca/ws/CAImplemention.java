package pt.upa.ca.ws;

import javax.jws.WebService;
import javax.security.cert.Certificate;

/**
 * Created by xxlxpto on 06-05-2016.
 */
@WebService(endpointInterface = "pt.upa.ca.ws.CA")
public class CAImplemention implements CA {

    @Override
    public Certificate getEntityCertificate(String entity) {
        if(entity == null){
            return null;
        }
        System.out.println(entity + " Certificate Requested...");
        if(entity.equals("Broker")){

        }else if(entity.equals("Transporter")){

        }else{
            // exception maybe?
            return null;
        }
        return null;
    }
}
