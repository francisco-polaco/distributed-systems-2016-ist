package pt.upa.broker.ws;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(
        endpointInterface="pt.upa.broker.ws.BrokerPortType",
        wsdlLocation="broker.1_0.wsdl",
        name="BrokerWebService",
        portName="BrokerPort",
        targetNamespace="http://ws.broker.upa.pt/",
        serviceName="BrokerService"
)
public class BrokerPort implements BrokerPortType{

	// TODO

    @Override
    public String ping(String name){
                    return null;
    }

    @Override
    public String requestTransport(String origin, String destination, int price)
            throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    return null;
    }

    @Override
    public TransportView viewTransport(String id)  throws UnknownTransportFault_Exception{
        return null;
    }

    @Override
    public List<TransportView> listTransports(){
        return null;
    }

    @Override
    public void clearTransports(){

    }

}
