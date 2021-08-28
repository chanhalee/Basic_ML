package Edge.Exceptions;

public class InValidEdgeFormatException extends RuntimeException {
    public InValidEdgeFormatException(String msg) {
        super(msg);
    }

    public InValidEdgeFormatException() {
        super("Edge.Exceptions.InValidEdgeFormatException");
    }
}
