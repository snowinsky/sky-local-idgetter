package cn.sky.tools.idgetter.exception;

public class IdGetFatalException extends RuntimeException{

    private static final long serialVersionUID = 2621807640432418341L;

    public IdGetFatalException(String message) {
        super(message);
    }

    public IdGetFatalException(String message, Throwable cause) {
        super(message, cause);
    }
}
