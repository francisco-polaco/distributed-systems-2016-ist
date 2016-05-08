package pt.upa.handler;

/**
 * Created by xxlxpto on 06-05-2016.
 */
public class BrokerHandlerConstants extends HandlerConstants {

  public BrokerHandlerConstants(){
   SENDER_SERVICE_NAME = "UpaBroker1";
   RCPT_SERVICE_NAME = "UpaTransporter1";
   SENDER_CERTIFICATE_FILE_PATH = SENDER_SERVICE_NAME + CERTIFICATE_EXTENSION;
   RCPT_CERTIFICATE_FILE_PATH = RCPT_SERVICE_NAME + CERTIFICATE_EXTENSION;
   KEYSTORE_FILE = SENDER_SERVICE_NAME + ".jks";
   KEY_ALIAS = SENDER_SERVICE_NAME;
  }
}
