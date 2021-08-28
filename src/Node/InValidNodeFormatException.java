package Node;

public class InValidNodeFormatException extends RuntimeException {
    public InValidNodeFormatException(String msg) {
        super(msg);
    }

    InValidNodeFormatException() {
        super("Node.InValidNodeFormatException");
    }
}
