package pt.upa.broker.ws.cli;

import pt.upa.broker.ws.*;
import pt.upa.transporter.ws.TransporterPortType;

import java.util.List;

public class BrokerClient {

    @Override
    public String ping(TransporterPortType port, String message) {
        String result = port.ping(message);
        System.out.println(result);
    }
/*
    @Override
    public String requestTransport(TransporterPortType port, String origin, String destination, int price){
        try{
            port.requestTransport(origin, destination, price);
        }catch(InvalidPriceFault_Exception e) {
            System.err.println(e.getMessage());
        }catch (UnavailableTransportFault_Exception e){
            System.err.println(e.getMessage());
        }catch(UnavailableTransportPriceFault_Exception e) {
            System.err.println(e.getMessage());
        }catch (UnknownLocationFault_Exception e){
            System.err.println(e.getMessage());
        }
    }

    @Override
    public TransportView viewTransport(TransporterPortType port, String id) {
        port.viewTransport(id);
    }





    @Override
    public List<TransportView> listTransports(TransporterPortType port) {
        port.listTransports();
    }

    @Override
    public void clearTransports(TransporterPortType port) {
        port.clearTransports();
    }*/
}
