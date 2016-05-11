package pt.upa.handler;

/**
 * Created by jp_s on 5/10/2016.
 */
public class InvalidTimestampSOAPException extends RuntimeException {

        private String mInfo;

        public InvalidTimestampSOAPException(String info){
            super();
            mInfo = info;
        }

        @Override
        public String getMessage(){
            return "Invalid Timestamp:\n" + mInfo;
        }
}


