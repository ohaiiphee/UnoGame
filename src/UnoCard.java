public class UnoCard {

    //uno card has 2 attributes - color and number. since these are fixed values/constant, we use enums
    enum Color {
        RED, BLUE, GREEN, YELLOW, BLACK;

        //color array with all possibilities in it
        private static final Color[] colors = Color.values();

        //getter to get color
        public static Color getColor(int i) {
            return Color.colors[i];
        }
    }

    enum Value {
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, DrawTwo, Skip, Reverse, DrawFour, WildColor;

        //value array with all possibilities in it
        private static final Value[] values = Value.values();

        //getter to get value
        public static Value getValue(int i) {
            return Value.values[i];
        }
    }

    //create our three variables
    private Color color; //can't be final so we can change it @ bot color choices
    private final Value value;

    protected int points; //points per card

    protected int numberHandCards;

    public UnoCard(Color color, Value value) {
        this.color = color;
        this.value = value;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    public int getNumberHandCards() {
        return this.numberHandCards;
    }

    public void setNumberHandCards(int numberHandCards) {
        this.numberHandCards = numberHandCards;
    }

    public Value getValue() {
        return this.value;
    }


    public int getPoints() {
        switch (value) {
            case ZERO:
                return 0;
            case ONE:
                return 1;
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            case SIX:
                return 6;
            case SEVEN:
                return 7;
            case EIGHT:
                return 8;
            case NINE:
                return 9;
            case DrawTwo:
            case Skip:
            case Reverse:
                return 20;
            case WildColor:
            case DrawFour:
                return 50;
            default:
                break;
        }
        return 0;
    }

    @Override
    public String toString() {
        return color + "_" + value + "(points:" + getPoints() + ")";
    }
}
