package pt.upa.handler;

/**
 * Created by xxlxpto on 06-05-2016.
 */
public class BrokerHandlerConstants {
    static final String CONTEXT_PROPERTY = "my.property";
     static final String ELEMENT_NAME = "signature";
     static final String PREFIX = "S";
     static final String NAMESPACE = "pt.upa.handler";
     static final String DIGEST_ALGORITHM = "SHA-1";
     static final String ASSYMETRIC_KEY_ALGORITHM = "RSA/ECB/PKCS1Padding";
     static final String SENDER_SERVICE_NAME = "UpaBroker";
     static final String RCPT_SERVICE_NAME = "UpaTransporter1";
     static final String SENDER_CERTIFICATE_FILE_PATH = SENDER_SERVICE_NAME + ".cre";
     static final String RCPT_CERTIFICATE_FILE_PATH = RCPT_SERVICE_NAME + ".cre";
    // This should be sent by the CA
     static final String CA_CERTIFICATE_FILE = "../ca-ws/ca-certificate.pem.txt";

     final static String KEYSTORE_FILE = SENDER_SERVICE_NAME + ".jks";
     final static String KEYSTORE_PASSWORD = "ins3cur3";

     final static String KEY_ALIAS = SENDER_SERVICE_NAME;
     final static String KEY_PASSWORD = "1nsecure";
}
