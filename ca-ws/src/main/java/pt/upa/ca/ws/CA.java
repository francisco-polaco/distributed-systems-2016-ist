package pt.upa.ca.ws;

import pt.upa.ca.ws.exception.CertificateDoesntExists;

import javax.jws.WebService;

/**
 * Created by xxlxpto on 06-05-2016.
 */
@WebService
interface CA {
    byte[] getEntityCertificate(String entity) throws CertificateDoesntExists;

}
