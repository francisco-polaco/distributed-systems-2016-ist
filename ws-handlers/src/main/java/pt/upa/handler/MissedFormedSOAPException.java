package pt.upa.handler;


/**
 * Created by xxlxpto on 08-05-2016.
 */
public class MissedFormedSOAPException extends RuntimeException {

    private String mInfo;

    public MissedFormedSOAPException(String info){
        super();
        mInfo = info;
    }

    @Override
    public String getMessage(){
        return "SOAP Message is miss formed.\n" + mInfo;
    }
}
