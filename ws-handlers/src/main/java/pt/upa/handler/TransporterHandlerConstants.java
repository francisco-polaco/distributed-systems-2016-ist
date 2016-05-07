package pt.upa.handler;

/**
 * Created by xxlxpto on 06-05-2016.
 */
public class TransporterHandlerConstants {
    /* static final String CONTEXT_PROPERTY = BrokerHandlerConstants.CONTEXT_PROPERTY;
     static final String ELEMENT_NAME = BrokerHandlerConstants.ELEMENT_NAME;
     static final String PREFIX = BrokerHandlerConstants.PREFIX;
     static final String NAMESPACE = BrokerHandlerConstants.NAMESPACE;
     static final String DIGEST_ALGORITHM = BrokerHandlerConstants.DIGEST_ALGORITHM;
     static final String ASSYMETRIC_KEY_ALGORITHM = BrokerHandlerConstants.ASSYMETRIC_KEY_ALGORITHM;
     static final String SENDER_SERVICE_NAME = BrokerHandlerConstants.RCPT_SERVICE_NAME;
     static final String RCPT_SERVICE_NAME = BrokerHandlerConstants.SENDER_SERVICE_NAME;

     static final String SENDER_CERTIFICATE_FILE_PATH = BrokerHandlerConstants.RCPT_SERVICE_NAME + ".cre";
     static final String RCPT_CERTIFICATE_FILE_PATH = BrokerHandlerConstants.SENDER_SERVICE_NAME + ".cre";
    // This should be sent by the CA
     static final String CA_CERTIFICATE_FILE = BrokerHandlerConstants.CA_CERTIFICATE_FILE;

     final static String KEYSTORE_FILE = BrokerHandlerConstants.RCPT_SERVICE_NAME + ".jks";
     final static String KEYSTORE_PASSWORD = "ins3cur3";

     final static String KEY_ALIAS = BrokerHandlerConstants.RCPT_SERVICE_NAME;
     final static String KEY_PASSWORD = "1nsecure";*/


    /*static final String CONTEXT_PROPERTY = BrokerHandlerConstants.CONTEXT_PROPERTY;
    static final String ELEMENT_NAME = BrokerHandlerConstants.ELEMENT_NAME;
    static final String PREFIX = BrokerHandlerConstants.PREFIX;
    static final String NAMESPACE = BrokerHandlerConstants.NAMESPACE;
    static final String DIGEST_ALGORITHM = BrokerHandlerConstants.DIGEST_ALGORITHM;
    static final String ASSYMETRIC_KEY_ALGORITHM = BrokerHandlerConstants.ASSYMETRIC_KEY_ALGORITHM;
    static final String SENDER_SERVICE_NAME = "UpaTransporter1";
    static final String RCPT_SERVICE_NAME = "UpaBroker";

    static final String SENDER_CERTIFICATE_FILE_PATH = SENDER_SERVICE_NAME + ".cre";
    static final String RCPT_CERTIFICATE_FILE_PATH = RCPT_SERVICE_NAME + ".cre";
    // This should be sent by the CA
    static final String CA_CERTIFICATE_FILE = BrokerHandlerConstants.CA_CERTIFICATE_FILE;

    final static String KEYSTORE_FILE = SENDER_SERVICE_NAME + ".jks";
    final static String KEYSTORE_PASSWORD = "ins3cur3";

    final static String KEY_ALIAS =SENDER_SERVICE_NAME;
    final static String KEY_PASSWORD = "1nsecure";*/

    static final String CONTEXT_PROPERTY = "my.property";
    static final String ELEMENT_NAME = "signature";
    static final String PREFIX = "S";
    static final String NAMESPACE = "pt.upa.handler";
    static final String DIGEST_ALGORITHM = "SHA-1";
    static final String ASSYMETRIC_KEY_ALGORITHM = "RSA/ECB/PKCS1Padding";
    static final String SENDER_SERVICE_NAME = "UpaTransporter1";
    static final String RCPT_SERVICE_NAME = "UpaBroker";
    static final String SENDER_CERTIFICATE_FILE_PATH = SENDER_SERVICE_NAME + ".cre";
    static final String RCPT_CERTIFICATE_FILE_PATH = RCPT_SERVICE_NAME + ".cre";
    // This should be sent by the CA
    static final String CA_CERTIFICATE_FILE = "../ca-ws/ca-certificate.pem.txt";

    final static String KEYSTORE_FILE = SENDER_SERVICE_NAME + ".jks";
    final static String KEYSTORE_PASSWORD = "ins3cur3";

    final static String KEY_ALIAS = SENDER_SERVICE_NAME;
    final static String KEY_PASSWORD = "1nsecure";
}
