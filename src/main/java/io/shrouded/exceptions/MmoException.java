package io.shrouded.exceptions;

public class MmoException extends RuntimeException {

    private final String requestId;
    private final int respondCode;

    public MmoException(String requestId, String message, int respondCode) {
        super(message);
        this.requestId = requestId;
        this.respondCode = respondCode;
    }

    public MmoException(String message, int respondCode) {
        super(message);
        this.requestId = "no-request-id";
        this.respondCode = respondCode;
    }

    public String getRequestId() {
        return requestId;
    }

    public int getRespondCode() {
        return respondCode;
    }
}