package pt.upa.ca.ws.exception;

import javax.jws.WebService;

/**
 * Created by xxlxpto on 08-05-2016.
 */
public class CertificateDoesntExists extends Exception{

    private String mEntity;

    public CertificateDoesntExists(String entity){
        mEntity = entity;
    }

    @Override
    public String getMessage(){
        return "CA does not have the certificate of " + mEntity;
    }
}
