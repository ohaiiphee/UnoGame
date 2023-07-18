
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
    private int currentPlayer;

    int totalPointsThisRound;

    int winningPlayerIndex; //for calculating points

    boolean hasValidCardForPlus4Check = false; //for the +4 challenge check

    int totalNumberPlayers;

    String challengeChoice; //also for the +4 challenge check

    int roundNumber = 1; //for the datenbank system

    //String array of the names of the players
    private String[] playerIds;

    //deck that the players are playing with
    private UnoDeck deck;

    //every player's hand is an array of uno cards -- to keep track of every players' hand, we make an array list of an array list --> playersHands is ALL of the players' hands
    private ArrayList<ArrayList<UnoCard>> playersHands;

    //stockpile of cards we put down
    private ArrayList<UnoCard> stockpile;

    private UnoCard.Color validColor;
    private UnoCard.Value validValue;


    //array to keep player points --> playerPoints[i] position matches playerIDs[i] position

    int[] playerPoints = new int[4];


    //to keep track of the game direction (clockwise/counter clockwise)
    boolean gameDirection;

    boolean exitGame = false;

    // Method to start the game - setting the number of human players as well as bots + their IDs
    public Game(int totalNumberPlayers, int numberHumanPlayers) {
        this.totalNumberPlayers = totalNumberPlayers;

        playerIds = new String[totalNumberPlayers];
        for (int i = 0; i < numberHumanPlayers; i++) {
            System.out.print("Enter the name of player " + (i + 1) + ": ");
            System.out.println(" ");
            Scanner scanner = new Scanner(System.in);
            playerIds[i] = scanner.nextLine();
        }
        for (int i = numberHumanPlayers; i < (totalNumberPlayers); i++) {
            playerIds[i] = "Bot_" + (i + 1);
        }

    }

    String myChoice;


    public void setChoice(String myChoice) {
        this.myChoice = myChoice;
    }

    //Here we set the rules to start a game
    //we also create a new Uno Deck, shuffle it and create a stockpile for all the cards that got played.
    public void startVorbereitung(int totalNumberPlayers) {

        exitGame = false;
        deck = new UnoDeck();
        deck.reset();
        deck.shuffle();
        stockpile = new ArrayList<UnoCard>();

        currentPlayer = 0;
        gameDirection = false; //standard game direction = false

//Here we create an ArrayList for each players' hand

        playersHands = new ArrayList<ArrayList<UnoCard>>();
        for (int i = 0; i < totalNumberPlayers; i++) {
            //array with how many cards a player starts with
            ArrayList<UnoCard> hand = new ArrayList<UnoCard>(Arrays.asList(deck.drawCard(7))); //FOR TESTING PURPOSES ONLY change how many cards players start with --> change back to 7!
            playersHands.add(hand); //keeps track of all players' hands
        }
    }

    //In this method we start the game
    public void start(Game game) {
        //first thing we do in the game --> get a card from the deck
        startVorbereitung(totalNumberPlayers);
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

        //If the first card happens to be a reverse, the game direction gets changed
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

    UnoCard.Color prevCardColor;

    //This method saves the previous color of the TopCard- if said card is not a black card
    void getPrevColor() {
        if (!getTopCard().getColor().equals(UnoCard.Color.BLACK)) {
            prevCardColor = getTopCard().getColor();

        }
    }

    //A method for ending the game
    public boolean isGameOver() {
        for (String player : this.playerIds) {
            if (hasEmptyHand(player)) {

                //if a player has an empty hand, a new row gets added to the database
                int sessionNumber = Database.generateSessionNumber();

                Database.addRowtoDatabase(player, sessionNumber, roundNumber, getWinner());
                roundNumber++;
                return true;
            }
        }
        return false;
    }

    public String getCurrentPlayer() {
        return this.playerIds[this.currentPlayer];
    }

    //Here we get the previous player- needed for e.g. skip, or reverse cards
    public String getPreviousPlayer(int i) {
        //get previous player clockwise
        int index = this.currentPlayer - 1;
        if (index == -1) {
            index = index + 4; //goes back to index 3 (last player)
        }
        //get previous player counterclockwise
        if (gameDirection) {
            index = this.currentPlayer + 1;
            if (index == 4) {
                index = playerIds.length - 4; //goes back to index 0 (first player)
            }
        }
        return this.playerIds[index];
    }

    //This method gets the next player in the games direction - needed for skip or reverse.
    public String getNextPlayer(int i) {
        //get next player clockwise
        int index = this.currentPlayer + 1;
        if (index == 4) {
            index = playerIds.length - 4; //goes back to index 0 (first player)
        }
        //get next player counterclockwise
        if (gameDirection) {
            index = this.currentPlayer - 1;
            if (index == -1) {
                index = index + 4; //goes back to index 3 (last player)
            }
        }
        return this.playerIds[index];
    }


    //This method shows the amount of cards a player has in their hand currently
    public ArrayList<UnoCard> getPlayerHand(String pid) {
        int index = Arrays.asList(playerIds).indexOf(pid);
        return playersHands.get(index);
    }


    public boolean hasEmptyHand(String pid) {
        return getPlayerHand(pid).isEmpty();
    }

    public boolean validCardPlay(UnoCard card) {
        return card.getColor() == validColor || card.getValue() == validValue;
    }

    //This method ensures that every player only plays when it's their turn
    public void checkPlayerTurn(String pid) throws InvalidPlayerTurnException {
        if (this.playerIds[this.currentPlayer] != pid) {
            throw new InvalidPlayerTurnException("It is not " + pid + "'s turn", pid);
        }
    }

    //This method gives the player a card when the player inputs -1
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
                currentPlayer = 3;
            }
        }

    }

    //This method ensures that the card being played is valid, and then plays the chosen card
    //If it is not, the player gets an extra card as penalty.
    public void submitPlayerCard(String pid, UnoCard card, UnoCard.Color declaredColor)
            throws InvalidColorSubmissionException, InvalidValueSubmissionException, InvalidPlayerTurnException {

        hasValidCardForPlus4Check = false; //resets everytime a card is played
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

            //if the player plays a +4, next player is asked if they want to challenge
            if (card.getValue() == UnoCard.Value.DrawFour) {
                System.out.println(pid + " wants to play a +4!");
                System.out.println(getNextPlayer(currentPlayer) + ", would you like to challenge the +4?");

                Scanner input = new Scanner(System.in);

//If the next player is a bot, the answer gets randomized
                String nextPlayer = getNextPlayer(currentPlayer);
                if (nextPlayer.contains("Bot")) {
                    int randomBotChoice = ThreadLocalRandom.current().nextInt(1, 2 + 1);
                    switch (randomBotChoice) {
                        case 1:
                            challengeChoice = "yes";
                            break;
                        case 2:
                            challengeChoice = "no";
                            break;
                    }
                } else {
                    challengeChoice = input.nextLine();
                }
//If a player tries to type in anything besides "yes" or "no" they get asked to give a valid answer
                while (!challengeChoice.equalsIgnoreCase("no") && !challengeChoice.equalsIgnoreCase("yes")) {
                    System.out.println("please answer yes or no...");
                    challengeChoice = input.nextLine();
                }
                if (challengeChoice.equalsIgnoreCase("no")) {
                    System.out.println(getNextPlayer(currentPlayer) + " doesn't want to challenge the card - continue...");
                } else if (challengeChoice.equalsIgnoreCase("yes")) {
                    System.out.println("We have a challenge!");

//If a player is challenged the program goes through their handcards and checks if there are any cards matching the color, or value of the top card.
                    pid = playerIds[currentPlayer];
                    ArrayList<UnoCard> playerHand = getPlayerHand(pid);

                    for (UnoCard playerCard : playerHand) {

                        if (playerCard.getColor().equals(prevCardColor))
                        {
                            hasValidCardForPlus4Check = true;
                            break;
                        }
                    }
                    if (hasValidCardForPlus4Check) {
                        System.out.println("Good choice, " + getNextPlayer(currentPlayer) + "! " + pid + " did have another card they could have played - they get the 4 cards.");

                    } else {
                        System.out.println("Bad choice, " + getNextPlayer(currentPlayer) + "... " + pid + "'s move was legit. You get 6 cards!");

                    }
                }
            }

            String colorChoice;

//The bots can also decide which color counts as valid next.
            if (!playerIds[currentPlayer].contains("Bot_")) {
                System.out.println("Choose the color (Red, Blue, Green, Yellow) that the next player must play:");


                Scanner colorInput = new Scanner(System.in);
                colorChoice = colorInput.nextLine();
                myChoice = colorChoice;

            } else {
                colorChoice = myChoice;
            }
            boolean validChoice = false;
            UnoCard.Color chosenColor = null;
            while (!validChoice) {
                if (colorChoice.equalsIgnoreCase("Red") || colorChoice.equalsIgnoreCase("Blue") ||
                        colorChoice.equalsIgnoreCase("Green") || colorChoice.equalsIgnoreCase("Yellow")) {
                    chosenColor = UnoCard.Color.valueOf(colorChoice.toUpperCase());
                    declaredColor = chosenColor;
                    validChoice = true;
                    // myChoice = null;
                } else {
                    System.out.println("Invalid choice - please choose a valid color.");
                    break;
                }


            }
        }

        pHand.remove(card);

//This prints the winning players ID + standard winning text if they have no cards left in their hand
        if (hasEmptyHand(this.playerIds[currentPlayer])) {
            System.out.println(" ");
            System.out.println(this.playerIds[currentPlayer] + " won this round!");
            System.out.println(" ");


            // Give the winning player points based on the other players' handcards
            totalPointsThisRound = calculatePoints(this.playerIds[currentPlayer]);

            System.out.println("Points after this round: ");
            System.out.println(" ");

            winningPlayerIndex = currentPlayer;
            playerPoints[winningPlayerIndex] += totalPointsThisRound;
            System.out.println(" ");

            displayPoints();
            System.out.println(" ");

            if (isGameOver()) {
                exitGame = true;
            }
        }

        validColor = card.getColor();
        validValue = card.getValue();
        stockpile.add(card);

        //jumps to next player

        if (gameDirection == false) {
            currentPlayer = (currentPlayer + 1) % playerIds.length;
        } else if (gameDirection == true) {
            currentPlayer = (currentPlayer - 1) % playerIds.length;
            if (currentPlayer == -1) {
                currentPlayer = playerIds.length - 1;
            }
        }

//This ensures that the players chosen color is the next valid color when a black card comes into play

        if (card.getColor() == UnoCard.Color.BLACK) {
            validColor = declaredColor;
        }
//When a +2 card gets played the next player has the next 2 cards added to their hand

        if (card.getValue() == UnoCard.Value.DrawTwo) {
            pid = playerIds[currentPlayer];
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            System.out.println(pid + " drew 2 cards.");
        }

        if (card.getValue() == UnoCard.Value.DrawFour) {
            //if during the +4 check the current player had another card they could play:
            if (hasValidCardForPlus4Check) {
                pid = getPreviousPlayer(currentPlayer);
                getPlayerHand(pid).add(deck.drawCard());
                getPlayerHand(pid).add(deck.drawCard());
                getPlayerHand(pid).add(deck.drawCard());
                getPlayerHand(pid).add(deck.drawCard());
                System.out.println(pid + " drew 4 cards.");

                //if during the +4 check the current player didn't have another card that they could play:
            } else if (challengeChoice.equalsIgnoreCase("yes") && !hasValidCardForPlus4Check) {
                pid = playerIds[currentPlayer];
                getPlayerHand(pid).add(deck.drawCard());
                getPlayerHand(pid).add(deck.drawCard());
                getPlayerHand(pid).add(deck.drawCard());
                getPlayerHand(pid).add(deck.drawCard());
                getPlayerHand(pid).add(deck.drawCard());
                getPlayerHand(pid).add(deck.drawCard());
                System.out.println(pid + " drew 6 cards.");

                //if next player decides to not challenge the +4
            } else {
                pid = playerIds[currentPlayer];
                getPlayerHand(pid).add(deck.drawCard());
                getPlayerHand(pid).add(deck.drawCard());
                getPlayerHand(pid).add(deck.drawCard());
                getPlayerHand(pid).add(deck.drawCard());
                System.out.println(pid + " drew 4 cards.");
            }
        }
//Here we skip the next player when a skip card comes into play
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

//Here we change the games direction by reassigning the player IDs according to the games direction
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

    //this method checks if one of the players has reached 500 points and therefore won the game
    public boolean checkIfPlayerHas500Points() {
        for (int i = 0; i < playerIds.length; i++) {
            if (playerPoints[i] >= 500) { //FOR TESTING PURPOSES lower points to test endgame faster --> change to 500!
                System.out.println(playerIds[i] + " has won the game!");
                return true;
            }
        }
        return false;
    }


    //Method to show points on the console
    public void displayPoints() {
        for (int i = 0; i < playerIds.length; i++) {
            System.out.println(playerIds[i] + " has " + playerPoints[i] + " points");
        }
    }

    //Method to show winner on the console
    public int getWinner() {
        return playerPoints[winningPlayerIndex];

    }

    //Our help menu - based on what the player types in they receive a fitting response
    public void helpMenu() {
        Scanner scan = new Scanner(System.in);
        System.out.println(" ");
        System.out.println("This is the help menu how can I help you?");
        System.out.println("Here are all keywords:");
        System.out.println("Play Card, Take Card, +2, +4, Wildcard, Objection, Reverse, skip, Win, Points, Score, UNO, End Game");
        System.out.println("Enter 'Score' to see the current score");
        System.out.println("If you want to close the help menu enter keyword 'Close'");
        System.out.println(" ");

        String input = scan.nextLine();

        switch (input.toLowerCase()) {
            case "help":
                helpMenu();
                break;
            case "play card":
                System.out.println("Enter the index number to play the card. The index starts with 0. ");
                helpMenu();
                break;
            case "take card":
                System.out.println("Type -1");
                helpMenu();
                break;
            case "+2":
                System.out.println("The next player has to take 2 Cards");
                helpMenu();
                break;
            case "+4":
                System.out.println("You can choose a color und the next player has to take 4 Cards");
                helpMenu();
                break;
            case "wildcard":
                System.out.println("You can choose a color");
                helpMenu();
                break;
            case "objection":
                System.out.println("When you play a +4 the next player can challenge you. If you were able to play another card you have to draw the 4 cards yourself. " +
                        "If you were unable to play another card the challenger has to draw the 6 cards.");
                helpMenu();
                break;
            case "reverse":
                System.out.println("Changes the direction of the game.");
                helpMenu();
                break;
            case "skip":
                System.out.println("The next player has to skip his turn. ");
                helpMenu();
                break;
            case "win":
                System.out.println("The first player how has an empty hand wins this round.");
                helpMenu();
                break;
            case "points":
                System.out.println("If a player wins the round they get points for every card in the other players hands.");
                System.out.println("Points for cards:");
                System.out.println("Numbercards: the number on it");
                System.out.println("+2, Skip and Reverse: 20 points per card");
                System.out.println("+4 and Wildcard: 50 per card");
                helpMenu();
                break;
            case "end game":
                System.out.println("The game ends when one player gets 500 points or when type 'exit'.");
                helpMenu();
                break;
            case "score":
                System.out.println("Current points for each player: ");
                displayPoints();
                helpMenu();
                break;
            case "uno":
                System.out.println("When a player only has one card, they should say 'UNO' - if you don't, you'll get 1 extra card as a penalty!");
                helpMenu();
                break;
            case "close":
                System.out.println("Until next time :)");
                break;
            default:
                System.out.println("Sorry i couldn't understand you");
                helpMenu();

        }
    }


    //Here we calculate the points of the winning player by summing up the values of the other players handcards
    private int calculatePoints(String pid) {
        int points = 0;
        for (String player : playerIds) {
            if (!player.equals(pid)) {
                ArrayList<UnoCard> hand = getPlayerHand(player);
                for (UnoCard card : hand) {
                    points += card.getPoints();
                }
            }
        }
        return points;
    }


}






