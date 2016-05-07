package pt.upa.handler;


/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class TransporterWsHandler extends UpaHandler {

    public TransporterWsHandler(){
        super();
        super.handlerConstants = new TransporterHandlerConstants();
    }

}
