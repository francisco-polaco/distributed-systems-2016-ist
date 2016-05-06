package pt.upa.ca.ws;

import javax.jws.WebService;
import javax.security.cert.Certificate;

/**
 * Created by xxlxpto on 06-05-2016.
 */
@WebService
interface CA {
    Certificate getEntityCertificate(String entity);

}
