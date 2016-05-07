package pt.upa.handler;


/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class BrokerWsHandler extends UpaHandler {

    public BrokerWsHandler(){
        super();
        super.handlerConstants = new BrokerHandlerConstants();
    }
}
