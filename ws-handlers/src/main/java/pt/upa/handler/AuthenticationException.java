package pt.upa.handler;


/**
 * Created by xxlxpto on 08-05-2016.
 */
public class AuthenticationException extends RuntimeException {

    private String mInfo;

    public AuthenticationException(String info){
        super();
        mInfo = info;
    }

    @Override
    public String getMessage(){
        return "Invalid Authentication.\n" + mInfo;
    }
}
