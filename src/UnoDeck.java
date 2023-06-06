import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class UnoDeck {

    //A deck has 108 cards
    //1x0 + 2x 1-9 + 2x every special color card + 4x wildColor + 4x drawFour


    //our card array
    private UnoCard[] cards;

    //keeping track of how many cards are in our deck
    private int cardsInDeck;

    public UnoDeck() {
        //a deck has 108 cards so
        cards = new UnoCard[108];
    }

    //reset/start the deck
    public void reset() {
        //an array of our enum colors
        UnoCard.Color[] colors = UnoCard.Color.values();
        cardsInDeck = 0;

        //fills the color with the values red, blue, green and yellow - no special black cards!
        for (int i = 0; i < colors.length - 1; i++) {
            UnoCard.Color color = colors[i];

            //create card value 0 for all 4 colors
            cards[cardsInDeck++] = new UnoCard(color, UnoCard.Value.getValue(0));

            //create cards value 1-9 for all 4 colors x2
            for (int j = 1; j < 10; j++) {
                cards[cardsInDeck++] = new UnoCard(color, UnoCard.Value.getValue(j));
                cards[cardsInDeck++] = new UnoCard(color, UnoCard.Value.getValue(j));
            }

            //an array only with the values drawTwo, Skip and Reverse
            UnoCard.Value[] values = new UnoCard.Value[]{
                    UnoCard.Value.DrawTwo, UnoCard.Value.Skip, UnoCard.Value.Reverse
            };

            //create cards DrawTwo, Skip and Reverse for all 4 colors x2
            for (UnoCard.Value value : values) {
                cards[cardsInDeck++] = new UnoCard(color, value);
                cards[cardsInDeck++] = new UnoCard(color, value);
            }
        }

        //an array with only the values WildColor and DrawFour
        UnoCard.Value[] values = new UnoCard.Value[]{
                UnoCard.Value.WildColor, UnoCard.Value.DrawFour
        };

        //create cards WildColor and DrawFour x4
        for (UnoCard.Value value : values) {
            for (int i = 0; i < 4; i++) {
                cards[cardsInDeck++] = new UnoCard(UnoCard.Color.BLACK, value);
            }
        }
    }

    //when the deck runs out, replace it with the already played cards
    public void replaceDeckWith(ArrayList<UnoCard> cards) {
        this.cards = cards.toArray(new UnoCard[cards.size()]);
        this.cardsInDeck = this.cards.length;
    }

    //check if the deck is empty
    public boolean isDeckEmpty() {
        return cardsInDeck == 0;
    }

    public void shuffle() {
        int n = cards.length;
        Random random = new Random();

        for (int i = 0; i < cards.length; i++) {
            //create temp randomValue, draw randomCard from the cards[randomValue], put current card(cards[i])
            //at the position of cards[randomValue] and then put randomCard as the current card(cards[i])
            int randomValue = i + random.nextInt(n - i);
            UnoCard randomCard = cards[randomValue];
            cards[randomValue] = cards[i];
            cards[i] = randomCard;
        }
    }

    //draw a card method -- if the deck is empty, displays an error (that action isn't possible as the deck is empty)
    public UnoCard drawCard() throws IllegalArgumentException {
        if (isDeckEmpty()) {
            throw new IllegalArgumentException("Cannot draw a card - deck is empty!");
        }
        return cards[--cardsInDeck];
    }

    //draw multiple cards method - for when the player receives a +2 or +4 card --> (int n) is the number of cards to draw
    public UnoCard[] drawCard(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Must draw a positive number of cards - can't draw " + n + " cards!");
        }
        if (n > cardsInDeck) {
            throw new IllegalArgumentException("Can't draw " + n + "cards - deck only has " + cardsInDeck + " cards.");
        }

//decrease [i] cards on the deck (the ones given to the player)
        UnoCard[] ret = new UnoCard[n];
        for (int i = 0; i < n; i++) {
            ret[i] = cards[--cardsInDeck];
        }
        return ret;
    }

    @Override
    public String toString() {
        return "Cards=" + Arrays.toString(cards) + " || Cards in the deck: " + cardsInDeck;
    }


}

