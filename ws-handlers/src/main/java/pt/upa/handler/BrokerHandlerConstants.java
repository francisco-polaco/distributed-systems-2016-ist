package pt.upa.handler;

/**
 * Created by xxlxpto on 06-05-2016.
 */
public class BrokerHandlerConstants extends HandlerConstants {

  public BrokerHandlerConstants(){
   CONTEXT_PROPERTY = "my.property";
   ELEMENT_NAME = "signature";
   PREFIX = "S";
   NAMESPACE = "pt.upa.handler";
   DIGEST_ALGORITHM = "SHA-1";
   ASSYMETRIC_KEY_ALGORITHM = "RSA/ECB/PKCS1Padding";
   SENDER_SERVICE_NAME = "UpaBroker";
   RCPT_SERVICE_NAME = "UpaTransporter1";
   SENDER_CERTIFICATE_FILE_PATH = SENDER_SERVICE_NAME + ".cre";
   RCPT_CERTIFICATE_FILE_PATH = RCPT_SERVICE_NAME + ".cre";
   // This should be sent by the CA
   CA_CERTIFICATE_FILE = "../ca-ws/ca-certificate.pem.txt";
   KEYSTORE_FILE = SENDER_SERVICE_NAME + ".jks";
   KEYSTORE_PASSWORD = "ins3cur3";
   KEY_ALIAS = SENDER_SERVICE_NAME;
   KEY_PASSWORD = "1nsecure";
  }
}
