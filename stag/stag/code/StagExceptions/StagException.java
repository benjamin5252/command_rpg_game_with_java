package StagExceptions;

public class StagException extends Exception {
    private final String failMessage;
    public  StagException(String failMessage) {
        this.failMessage = failMessage;
    }
    public String toString(){
        return "[ERROR]: " + failMessage;
    }
}
