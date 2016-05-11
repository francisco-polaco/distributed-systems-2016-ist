package pt.upa.broker.ws.cli;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.*;
import pt.upa.transporter.ws.cli.TransporterClient;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class BrokerClient {

    private BrokerPortType mPort;
    private int mfault = 0;
    private String mUddiURL;
    private String mName;
    private boolean mFirst = true;

    public BrokerClient(String uddiURL, String name) throws JAXRException, BrokerClientException {
        mUddiURL = uddiURL;
        mName = name;
        reconnect();
        mFirst = false;
    }

    public BrokerClient(String endpointAddress){
        bind(endpointAddress);
    }


    private String LookUp(String uddiURL, String name) throws BrokerClientException {
        String endpointAddress;
        try {
            System.out.printf("Contacting UDDI at %s%n", uddiURL);
            UDDINaming uddiNaming = new UDDINaming(uddiURL);

            System.out.printf("Looking for '%s'%n", name);
            endpointAddress = uddiNaming.lookup(name);
        } catch (Exception e) {
            String msg = String.format("Client failed lookup on UDDI at %s!",
                    uddiURL);
            throw new BrokerClientException(msg, e);
        }
        if (endpointAddress == null) {
            String msg = String.format("Service with name %s not found on UDDI at %s", name, uddiURL);
            throw new BrokerClientException(msg);
        } else {
            System.out.printf("Found %s%n", endpointAddress);
            return endpointAddress;
        }
    }

    private void bind(String endpointAddress){
        System.out.println("Creating stub ...");
        BrokerService service = new BrokerService();
        mPort = service.getBrokerPort();

        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) mPort;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
    }

    public String ping(String message) throws ConnectionTimeOutException {
        try {
            String ret = mPort.ping(message);
            mfault = 0;
            return ret;
        }catch (javax.xml.ws.WebServiceException e){
            System.out.println("Connection Lost. Reconnecting...");
            if(mfault < 3){
                mfault++;
                try {
                    reconnect();
                } catch (BrokerClientException ignored) {}
                return ping(message);
            }else{
                System.out.println("Cannot connect to servers after 3 attempts.");
                throw new ConnectionTimeOutException();
            }
        }

    }


    public String requestTransport(String origin, String destination, int price) throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception, ConnectionTimeOutException {

        try {
            String ret = mPort.requestTransport(origin, destination, price);
            mfault = 0;
            return ret;
        }catch (javax.xml.ws.WebServiceException e){
            System.out.println("Connection Lost. Reconnecting...");
            if(mfault < 3){
                mfault++;
                try {
                    reconnect();
                } catch (BrokerClientException ignored) {}
                return requestTransport(origin, destination, price);
            }else{
                System.out.println("Cannot connect to servers after 3 attempts.");
                throw new ConnectionTimeOutException();
            }
        }
    }


    public TransportView viewTransport(String id) throws UnknownTransportFault_Exception, ConnectionTimeOutException {
        try {
            TransportView ret = mPort.viewTransport(id);
            mfault = 0;
            return ret;
        }catch (javax.xml.ws.WebServiceException e){
            System.out.println("Connection Lost. Reconnecting...");
            if(mfault < 3){
                mfault++;
                try {
                    reconnect();
                } catch (BrokerClientException ignored) {}
                return viewTransport(id);
            }else{
                System.out.println("Cannot connect to servers after 3 attempts.");
                throw new ConnectionTimeOutException();
            }
        }
    }


    public List<TransportView> listTransports() throws ConnectionTimeOutException {
        try {
            List<TransportView> ret = mPort.listTransports();
            mfault = 0;
            return ret;
        }catch (javax.xml.ws.WebServiceException e){
            System.out.println("Connection Lost. Reconnecting...");
            if(mfault < 3){
                mfault++;
                try {
                    reconnect();
                } catch (BrokerClientException ignored) {}
                return listTransports();
            }else{
                System.out.println("Cannot connect to servers after 3 attempts.");
                throw new ConnectionTimeOutException();
            }
        }
    }


    public void clearTransports() throws ConnectionTimeOutException {
        try {
            mPort.clearTransports();
            mfault = 0;
        }catch (javax.xml.ws.WebServiceException e){
            System.out.println("Connection Lost. Reconnecting...");
            if(mfault < 3){
                mfault++;
                try {
                    reconnect();
                } catch (BrokerClientException ignored) {}
                clearTransports();
            }else{
                System.out.println("Cannot connect to servers after 3 attempts.");
                throw new ConnectionTimeOutException();
            }
        }
    }

    private void reconnect() throws BrokerClientException {
        if (!mFirst){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String endpointAddress = LookUp(mUddiURL, mName);
        bind(endpointAddress);
    }

    public void updateTransporters(String s, String t){
        mPort.updateTransporters(s, t);
    }
    public void updateOffers(String s, TransportView t){
        mPort.updateOffers(s, t);
    }
    public void updateTable(String s, String t){
        mPort.updateTable(s, t);
    }
    public void updateSeed(int seed){
        mPort.updateSeed(seed);
    }
    public void updateClear(){
        mPort.updateClr("");
    }
    public String areYouAlive(){
        return mPort.areYouAlive("");
    }
}
