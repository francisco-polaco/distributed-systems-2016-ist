package pt.upa.transporter.ws.cli;

import pt.upa.transporter.TransporterClientApplication;
import pt.upa.transporter.ws.*;

import java.util.List;

public class TransporterClient{

    @Override
    public String ping(TransporterPortType port, String message) {
        String result = port.ping(message);
        System.out.println(result);
    }

    @Override
    public JobView requestJob(TransporterPortType port, String origin, String destination, int price){
        try{
            port.requestJob(origin, destination, price);
        }catch(BadLocationFault_Exception e) {
            System.err.println(e.getMessage());
        }catch (BadPriceFault_Exception e){
            System.err.println(e.getMessage());
        }
    }

    @Override
    public JobView decideJob(TransporterPortType port, String id, boolean accept){
        try{
            port.decideJob(id, accept);
        }catch(BadJobFault_Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public JobView jobStatus(TransporterPortType port, String id) {
        port.jobStatus(id);
    }

    @Override
    public List<JobView> listJobs(TransporterPortType port) {
        port.listJobs();
    }

    @Override
    public void clearJobs(TransporterPortType port) {
        port.clearJobs();
    }

}
