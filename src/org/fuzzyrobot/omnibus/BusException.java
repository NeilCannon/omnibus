package org.fuzzyrobot.omnibus;

/**
 * User: neil
 * Date: 27/11/2012
 */
public class BusException extends RuntimeException {

    public BusException(String detailMessage) {
        super(detailMessage);
    }

    public BusException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BusException(Throwable throwable) {
        super(throwable);
    }
}
