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
    private final Color color;
    private final Value value;

    protected int points;

    public UnoCard(Color color, Value value) {
        this.color = color;
        this.value = value;
    }

    public Color getColor() {
        return this.color;
    }

    public Value getValue() {
        return this.value;
    }

    public int getPoints() {
        return this.points;
    }

    @Override
    public String toString() {
        return color + "_" + value + "(points:" + points+")";
    }
}
