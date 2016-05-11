package pt.upa.broker.ws.cli;

/**
 * Created by daniel on 11-05-2016.
 */
public class ConnectionTimeOutException extends Exception {
    private static final long serialVersionUID = 1L;

    public ConnectionTimeOutException() {
    }

    public ConnectionTimeOutException(String message) {
        super(message);
    }

    public ConnectionTimeOutException(Throwable cause) {
        super(cause);
    }

    public ConnectionTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }
}
