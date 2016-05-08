package pt.upa.handler;


/**
 * Created by xxlxpto on 08-05-2016.
 */
public class AuthenticationException extends RuntimeException {


    @Override
    public String getMessage(){
        return "Invalid Authentication";
    }
}
