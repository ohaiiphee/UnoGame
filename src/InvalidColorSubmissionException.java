public class InvalidColorSubmissionException extends Exception {
    private UnoCard.Color expected;
    private UnoCard.Color actual;

    public InvalidColorSubmissionException(String message, UnoCard.Color actual, UnoCard.Color expected) {
        super(message);
        this.actual = actual;
        this.expected = expected;
    }
    @Override
    public String getMessage() {
        return super.getMessage() + " (Actual: " + actual + ", Expected: " + expected + ")";
    }

}
