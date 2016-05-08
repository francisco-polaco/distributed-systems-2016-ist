package pt.upa.handler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by xxlxpto on 06-05-2016.
 */
public class TransporterHandlerConstants extends HandlerConstants {

    private static final String PROP_FILE =  "/server.properties";


    public TransporterHandlerConstants(){
        try {
            loadProperties();
        } catch (IOException e) {
            // If nothing is defined
            SENDER_SERVICE_NAME = "UpaTransporter1";
        }
        RCPT_SERVICE_NAME = "UpaBroker1";
        SENDER_CERTIFICATE_FILE_PATH = SENDER_SERVICE_NAME + CERTIFICATE_EXTENSION;
        RCPT_CERTIFICATE_FILE_PATH = RCPT_SERVICE_NAME + CERTIFICATE_EXTENSION;
        KEYSTORE_FILE = SENDER_SERVICE_NAME + ".jks";
        KEY_ALIAS = SENDER_SERVICE_NAME;
    }


    private void loadProperties() throws IOException {
        Properties props = new Properties();
        try {
            props.load(TransporterHandlerConstants.class.getResourceAsStream(PROP_FILE));
        } catch (IOException e) {
            final String msg = String.format("Could not load properties file {%s}", PROP_FILE);
            System.out.println(msg);
            throw e;
        }
        SENDER_SERVICE_NAME = props.getProperty("ws.name");
        System.out.println(SENDER_SERVICE_NAME);
        SENDER_SERVICE_NAME = "UpaTransporter1"; //FIXME: Remove after solve the bug above
    }
}
