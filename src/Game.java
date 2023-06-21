import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Game {
    private int currentPlayer;

    //String array of the names of the players
    private String[] playerIds;

    //deck that the players are playing with
    private UnoDeck deck;

    //every player's hand is an array of uno cards -- to keep track of every players' hand, we make an array list of an array list --> playerHand is actually ALL of the players' hands
    private ArrayList<ArrayList<UnoCard>> playerHand;

    //stockpile of cards we put down
    private ArrayList<UnoCard> stockpile;

    private UnoCard.Color validColor;
    private UnoCard.Value validValue;

    //to keep track of the game direction (clockwise/counter clockwise)
    boolean gameDirection;

    public Game(int numberPlayers) {
        if(numberPlayers <2 || numberPlayers > 10){
            throw new IllegalArgumentException("Number of players must be between 2 and 10.");
        }

        deck = new UnoDeck();
        deck.reset();
        deck.shuffle();
        stockpile = new ArrayList<UnoCard>();

        playerIds = new String[numberPlayers];
        for (int i = 0; i < numberPlayers; i++) {
            System.out.print("Enter the name of player " + (i + 1) + ": ");
            Scanner scanner = new Scanner(System.in);
            playerIds[i] = scanner.nextLine();
        }

        currentPlayer = 0;
        gameDirection = false; //standard game direction

        playerHand = new ArrayList<ArrayList<UnoCard>>();
        for (int i = 0; i < numberPlayers; i++) {
            //create a hand of uno cards and fill it with cards from the deck
            ArrayList<UnoCard> hand = new ArrayList<UnoCard>(Arrays.asList(deck.drawCard(7)));
            playerHand.add(hand); //keeps track of all players' hands
        }
    }

    public void start(Game game) {
        //first thing we do in the game --> get a card from the deck
        UnoCard card = deck.drawCard();
        //check which color/value player can play --> color/value of the previous card
        validColor = card.getColor();
        validValue = card.getValue();

        //if the 1st card happens to be a + card or a wild color card, we want to start the game fresh -- the 1st card shouldn't be a +4 card :)
        if (card.getValue() == UnoCard.Value.WildColor || card.getValue() == UnoCard.Value.DrawFour || card.getValue() == UnoCard.Value.DrawTwo) {
            start(game);
        }

        //if the 1st card happens to be a skip, 1st player gets skipped
        if (card.getValue() == UnoCard.Value.Skip) {
            System.out.println(playerIds[currentPlayer] + " was skipped!");
            if (gameDirection == false) {
                currentPlayer = (currentPlayer + 1) % playerIds.length;
            } else if (gameDirection == true) {
                currentPlayer = (currentPlayer - 1) % playerIds.length;
                if (currentPlayer == -1) {
                    currentPlayer = playerIds.length - 1;
                }
            }
        }

        if (card.getValue() == UnoCard.Value.Reverse) {
            System.out.println("The game direction has changed!");
            gameDirection ^= true; //^= --> will flip every time; if it's false it becomes true and vice-versa
            currentPlayer = playerIds.length - 1;
        }

        stockpile.add(card);
    }

    public UnoCard getTopCard() {
        return new UnoCard(validColor, validValue);
    }


    public boolean isGameOver() {
        for (String player : this.playerIds) {
            if (hasEmptyHand(player)) {
                return true;
            }
        }
        return false;
    }

    public String getCurrentPlayer() {
        return this.playerIds[this.currentPlayer];
    }

    public String getPreviousPlayer(int i) {
        int index = this.currentPlayer - 1;
        if (index == -1) {
            index = playerIds.length - 1;
        }
        return this.playerIds[index];
    }

    public String[] getPlayers() {
        return playerIds;
    }

    public ArrayList<UnoCard> getPlayerHand(String pid) {
        int index = Arrays.asList(playerIds).indexOf(pid);
        return playerHand.get(index);
    }

    public int getPlayerHandSize(String pid) {
        return getPlayerHand(pid).size();
    }

    public UnoCard getPlayerCard(String pid, int choice) {
        ArrayList<UnoCard> hand = getPlayerHand(pid);
        return hand.get(choice);
    }

    public boolean hasEmptyHand(String pid) {
        return getPlayerHand(pid).isEmpty();
    }

    public boolean validCardPlay(UnoCard card) {
        return card.getColor() == validColor || card.getValue() == validValue;
    }

    public void checkPlayerTurn(String pid) throws InvalidPlayerTurnException {
        if (this.playerIds[this.currentPlayer] != pid) {
            throw new InvalidPlayerTurnException("It is not " + pid + "'s turn", pid);
        }
    }

    public void submitDraw(String pid) throws InvalidPlayerTurnException {
        checkPlayerTurn(pid);

        if (deck.isDeckEmpty()) {
            deck.replaceDeckWith(stockpile);
            deck.shuffle();
        }

        getPlayerHand(pid).add(deck.drawCard());
        if (gameDirection == false) {
            currentPlayer = (currentPlayer + 1) % playerIds.length;
        } else if (gameDirection == true) {
            currentPlayer = (currentPlayer - 1) % playerIds.length;
            if (currentPlayer == -1) {
                currentPlayer = playerIds.length;
            }
        }
    }

    public void setCardColor(UnoCard.Color color) {
        validColor = color;
    }

    public void submitPlayerCard(String pid, UnoCard card, UnoCard.Color declaredColor)
            throws InvalidColorSubmissionException, InvalidValueSubmissionException, InvalidPlayerTurnException {
        checkPlayerTurn(pid);
        ArrayList<UnoCard> pHand = getPlayerHand(pid);

        if (!validCardPlay(card)) {
            if (card.getColor() == UnoCard.Color.BLACK) {
                validColor = card.getColor();
                validValue = card.getValue();
            } else {
                throw new InvalidColorSubmissionException("Invalid move", card.getColor(), validColor);
            }

            if (card.getValue() != validValue) {
                throw new InvalidValueSubmissionException("Invalid move", card.getValue(), validValue);
            }
        }
//if the player plays a wildColor card, scanner to let person choose a color/value
        if (card.getColor() == UnoCard.Color.BLACK) {
            System.out.println("Choose the color (Red, Blue, Green, Yellow) that the next player must play:");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine();

            boolean validChoice = false;
            UnoCard.Color chosenColor = null;
            while (!validChoice) {
                if (choice.equalsIgnoreCase("Red") || choice.equalsIgnoreCase("Blue") ||
                        choice.equalsIgnoreCase("Green") || choice.equalsIgnoreCase("Yellow")) {
                    chosenColor = UnoCard.Color.valueOf(choice.toUpperCase());
                    declaredColor = chosenColor;
                    validChoice = true;
                } else {
                    System.out.println("Invalid choice - please choose a valid color.");
                    choice = scanner.nextLine();
                }


            }
        }


        pHand.remove(card);
        if (hasEmptyHand(this.playerIds[currentPlayer])) {
            System.out.println(this.playerIds[currentPlayer] + " won the game! Thank you for playing.");
            System.exit(0);
        }

        validColor = card.getColor();
        validValue = card.getValue();
        stockpile.add(card);

        if (gameDirection == false) {
            currentPlayer = (currentPlayer + 1) % playerIds.length;
        } else if (gameDirection == true) {
            currentPlayer = (currentPlayer - 1) % playerIds.length;
            if (currentPlayer == -1) {
                currentPlayer = playerIds.length - 1;
            }
        }

        if (card.getColor() == UnoCard.Color.BLACK) {
            validColor = declaredColor;
        }

        if (card.getValue() == UnoCard.Value.DrawTwo) {
            pid = playerIds[currentPlayer];
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            System.out.println(pid + " drew 2 cards.");
        }

        if (card.getValue() == UnoCard.Value.DrawFour) {
            pid = playerIds[currentPlayer];
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            System.out.println(pid + " drew 4 cards.");
        }

        if (card.getValue() == UnoCard.Value.Skip) {
            System.out.println(playerIds[currentPlayer] + " was skipped!");

            if (gameDirection == false) {
                currentPlayer = (currentPlayer + 1) % playerIds.length;
            } else if (gameDirection == true) {
                currentPlayer = (currentPlayer - 1) % playerIds.length;
                if (currentPlayer == -1) {
                    currentPlayer = playerIds.length - 1;
                }
            }
        }

        if (card.getValue() == UnoCard.Value.Reverse) {
            System.out.println(pid + " changed the game direction.");

            gameDirection ^= true;
            if (gameDirection == true) {
                currentPlayer = (currentPlayer - 2) % playerIds.length;
                if (currentPlayer == -1) {
                    currentPlayer = playerIds.length - 1;
                }

                if (currentPlayer == -2) {
                    currentPlayer = playerIds.length - 2;
                }
            } else if (gameDirection == false) {
                currentPlayer = (currentPlayer + 2) % playerIds.length;
            }
        }
    }
}

//class for when the wrong player tries to play (check if really necessary)
class InvalidPlayerTurnException extends Exception {
    String playerId;

    public InvalidPlayerTurnException(String message, String pid) {
        super(message);
        playerId = pid;
    }

    public String getPid() {
        return playerId;
    }
    public static void helpMenu(String input) {
        System.out.println("This is the help menu how can I help you?");
        boolean help = true;
        while (help) {
            switch (input.toLowerCase()) {
                case "help":
                    System.out.println("This is the help menu how can I help you?");
                    break;
                case "play card":
                    System.out.println("Enter the index number to play the card. The index starts with 0. ");
                    break;
                case "take card":
                    System.out.println("Type -1");
                    break;
                case "+2":
                    System.out.println("The next player has to take 2 Cards");
                    break;
                case "+4":
                    System.out.println("You can choose a color und the next player has to take 4 Cards");
                    break;
                case "wildcard":
                    System.out.println("You can choose a color");
                    break;
                case "objection":
                    System.out.println("When you play a +4 the next player can challenge you. If you where able to play another card you have to draw the 4 cards yourself. " +
                            "If you where unable to play another card the challenger has to draw the 6 cards.");
                    break;
                case "revers":
                    System.out.println("Changes the direction of the game.");
                    break;
                case "skip":
                    System.out.println("The next player has to skip his turn. ");
                    break;
                case "win":
                    System.out.println("The first player how has an empty hand wins this round.");
                    break;
                case "points":
                    System.out.println("If a player wins the round he gets points for every card in the other players hands.");
                    System.out.println("Points for cards:");
                    System.out.println("Numbercards: the number on it");
                    System.out.println("+2, Skip and Reverse: 20 points per card");
                    System.out.println("+4 and Wildcard: 50 per card");
                    break;
                case "end game":
                    System.out.println("The game ends when one player get's 500 points or when type 'exit'.");
                    break;
                case "close":
                    help = false;
                    break;

            }
        }
    }

}

//class for everytime someone submits a color that isn't the correct color
class InvalidColorSubmissionException extends Exception {
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

//class for everytime someone submits a value that isn't the correct value
class InvalidValueSubmissionException extends Exception {
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