package cn.momia.admin.web.error;

/**
 * Created by hoze on 15/6/16.
 */
public class SessionTimeoutException extends Exception {


    private static final long serialVersionUID = 5721686959716896576L;
    public SessionTimeoutException(){

    }

    public SessionTimeoutException(Throwable cause){
        super(cause);
    }

    public SessionTimeoutException(String message){
        super(message);
    }
}
