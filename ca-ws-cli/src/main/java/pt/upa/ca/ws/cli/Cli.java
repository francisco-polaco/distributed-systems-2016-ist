package pt.upa.ca.ws.cli;

import pt.upa.ca.ws.CA;

/**
 * Created by xxlxpto on 06-05-2016.
 */

public class Cli {
    private CA mCa;

    public Cli(CA ca){
        mCa = ca;
    }

    public pt.upa.ca.ws.Certificate getEntityCertificate(String entity){
        return mCa.getEntityCertificate(entity);
    }


}
