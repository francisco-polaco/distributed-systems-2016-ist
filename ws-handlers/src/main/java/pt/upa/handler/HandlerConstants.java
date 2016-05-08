package pt.upa.handler;

/**
 * Created by xxlxpto on 07-05-2016.
 */
public abstract class HandlerConstants {
     final String CERTIFICATE_EXTENSION = ".cer";
     final String CONTEXT_PROPERTY = "my.property";
     final String SIG_ELEMENT_NAME = "signature";
     final String SENDER_ELEMENT_NAME = "sender";
     final String PREFIX = "S";
     final String NAMESPACE = "pt.upa.handler";
     String SENDER_SERVICE_NAME = "UpaBroker1";
     String RCPT_SERVICE_NAME = "UpaTransporter1";
     String SENDER_CERTIFICATE_FILE_PATH = SENDER_SERVICE_NAME + CERTIFICATE_EXTENSION;
     String RCPT_CERTIFICATE_FILE_PATH = RCPT_SERVICE_NAME + CERTIFICATE_EXTENSION;
     String KEYSTORE_FILE = SENDER_SERVICE_NAME + ".jks";
     final String KEYSTORE_PASSWORD = "ins3cur3";
     String KEY_ALIAS = SENDER_SERVICE_NAME;
     final String KEY_PASSWORD = "1nsecure";
}
