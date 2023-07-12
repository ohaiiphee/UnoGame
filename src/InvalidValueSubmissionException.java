public class InvalidValueSubmissionException extends Exception {
    private UnoCard.Value expected;
    private UnoCard.Value actual;

    public InvalidValueSubmissionException(String message, UnoCard.Value actual, UnoCard.Value expected) {
        super(message);
        this.actual = actual;
        this.expected = expected;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " (Actual: " + actual + ", Expected: " + expected + ")";
    }
}
