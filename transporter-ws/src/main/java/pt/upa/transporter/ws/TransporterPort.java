package pt.upa.transporter.ws;

import javax.jws.WebService;
import java.util.List;

@WebService(
        endpointInterface="pt.upa.transporter.ws.TransporterPortType",
        wsdlLocation="transporter.1_0.wsdl",
        name="TransporterWebService",
        portName="TransporterPort",
        targetNamespace="http://ws.transporter.upa.pt/",
        serviceName="TransporterService"
)
public class TransporterPort implements TransporterPortType{
    @Override
    public String ping(String name) {
        return name;
    }

    @Override
    public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception {
        return null;
    }

    @Override
    public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
        return null;
    }

    @Override
    public JobView jobStatus(String id) {
        return null;
    }

    @Override
    public List<JobView> listJobs() {
        return null;
    }

    @Override
    public void clearJobs() {

    }


}
