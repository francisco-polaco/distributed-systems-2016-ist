package pt.upa.ca.ws;

import pt.upa.ca.ws.exception.UnknownServiceException;

import javax.jws.WebService;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * Created by xxlxpto on 06-05-2016.
 */
@WebService
interface CA {
    byte[] getEntityCertificate(String entity) throws UnknownServiceException;

}
