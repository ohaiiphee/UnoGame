import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) throws InvalidPlayerTurnException {
// First we define what we need and assign a value so that we can change it later

        //test

        int numberHumanPlayers = 0;
        int numberBotPlayers = 0;
        int totalNumberPlayers = 0;
        boolean validInput = false;
        String botColor = null;
        Database.createDatabase();
        String unoInput = " ";

//Here the players put in the amount of human players. The input has to be between 1 and 4 to be valid

        while (!validInput) {
            System.out.print("Enter the number of human players: ");
            System.out.println(" ");
            Scanner scanner = new Scanner(System.in);
            numberHumanPlayers = scanner.nextInt();

            if (numberHumanPlayers >= 1 && numberHumanPlayers <= 4) {
                validInput = true;
            } else {
                System.out.println("The number of human players must be between 1 and 4. Please enter a number again.");
            }

// Here the rest of valid players get substituted by bots until the total amount of players equals 4.

            if (numberHumanPlayers < 4) {
                if (numberHumanPlayers == 1) {
                    numberBotPlayers = 3;
                } else if (numberHumanPlayers == 2) {
                    numberBotPlayers = 2;
                } else {
                    numberBotPlayers = 1;
                }
            }

            totalNumberPlayers = numberHumanPlayers + numberBotPlayers;

        }

//set up the game
        Game game = new Game(totalNumberPlayers, numberHumanPlayers);


//start the game
        game.start(game);

//Game loop
        boolean gameOver = false;
        boolean gameExit = false;

        while (!gameOver) {

//Get the top card
            game.getPrevColor();
            UnoCard topCard = game.getTopCard();
            System.out.println(" ");
            System.out.println("Top Card: " + topCard);


//Check current player
            String currentPlayer = game.getCurrentPlayer();
            System.out.println("The current player is: " + currentPlayer);


//Get player hand
            ArrayList<UnoCard> playerHand = game.getPlayerHand(currentPlayer);
            topCard.setNumberHandCards(playerHand.size());

            System.out.println(currentPlayer + "'s cards: (" + topCard.getNumberHandCards() + ") " + playerHand);


            Scanner scanner = new Scanner(System.in);
            //we don't need to ask bots which card they want to play, they can think for themselves ;)
            if (!currentPlayer.toLowerCase().contains("bot")) {
                System.out.println("Enter the index of the card you want to play (or -1 to draw a card): ");
            }
            String input = null;

// here the Bots take their turns
            if (currentPlayer.toLowerCase().contains("bot")) {
                UnoCard playerCard2 = null;
                System.out.println("I'm a bot and it's my turn :)");
                for (int i = 0; i < playerHand.size(); i++) {

                    if (playerHand.size() == 1 && !unoInput.equalsIgnoreCase("uno")) {
                        input = "uno";
                        break;
                    }

                    playerCard2 = playerHand.get(i);

                    //if the color of card[i] on bot's hand matches topCard's color, play that card
                    //bot will play the first card in the hand that can be played
                    //which means that if a wildCard is before another validCard, the bot will play it --> bots can also participate in the +4 Strafe!
                    if (playerCard2.getColor().equals(game.getTopCard().getColor())) {
                        input = String.valueOf(i);
                        break;
                    }

                    if ((playerCard2.getValue().equals(game.getTopCard().getValue())&& (playerCard2.getColor().equals("Black") == false))) {
                        input = String.valueOf(i);
                        break;
                    }

                    //check if it's a valid play
                    if (playerCard2.getColor().equals(UnoCard.Color.BLACK) && !(topCard.getValue().equals(UnoCard.Value.DrawFour))) {
                        input = String.valueOf(i);

                        //if bot plays a wildColor or +4, make it choose a color randomly
                        if (playerCard2.getColor() == UnoCard.Color.BLACK) {
                            System.out.println(currentPlayer + " is playing a wild card...");
                            int randomColor = ThreadLocalRandom.current().nextInt(1, 4 + 1);

                            switch (randomColor) {
                                case 1:
                                    botColor = "red";
                                    break;
                                case 2:
                                    botColor = "blue";
                                    break;
                                case 3:
                                    botColor = "green";
                                    break;
                                case 4:
                                    botColor = "yellow";
                                    break;
                                default:
                                    botColor = null;
                            }

                            game.setChoice(botColor);
                            break;

                        }
                    }
                    if (!game.validCardPlay(playerCard2)) {
                        //if bot has no cards to play, it takes a card
                        input = "-1";
                    }

                    if (game.validCardPlay(playerCard2)) {
                        try {
                            game.submitPlayerCard(currentPlayer, playerCard2, UnoCard.Color.RED);
                        } catch (InvalidColorSubmissionException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidValueSubmissionException e) {
                            throw new RuntimeException(e);
                        }

                    }

                }


            }
            if (!currentPlayer.toLowerCase().contains("bot")) {
                input = scanner.nextLine().toLowerCase();
            }

//Prompt the user for input. They can either choose the index of the card they want to play, open the help menu, say "UNO" or draw a card by typing -1

            int cardIndex;

            if (input != null && input.equals("help")) {
                game.helpMenu();

            } else if ((input != null) && (input.equals("exit"))) {
                gameExit = true;
            } else if ((input != null) && (input.equals("uno")) && (playerHand.size() == 1)) {
                System.out.println("UNO!");
                unoInput = "uno";
            } else if ((input != null) && (input.equals("uno")) && (playerHand.size() != 1)) {
                System.out.println("You have more than 1 card in your hand!");

            } else if ((input != null) && !(input.equals("uno")) && (playerHand.size() == 1) && !(input.equals("-1")) && !(unoInput.equalsIgnoreCase("uno"))) {
                System.out.println("You forgot to say UNO - you get an extra card!");
                game.submitDraw(currentPlayer);
            } else {
                try {

                    cardIndex = Integer.parseInt(input);


                    if (cardIndex == -1) {

//Draw a card by typing in -1
                        try {
                            game.submitDraw(currentPlayer);
                        } catch (InvalidPlayerTurnException e) {
                            System.out.println("Invalid Player Turn: " + e.getMessage());
                        }
                    } else {

//Submit a player card
// If a player tries to play an invalid card they get an extra card added to their hand as a penalty

                        UnoCard playerCard = playerHand.get(cardIndex);
                        try {
                            game.submitPlayerCard(currentPlayer, playerCard, UnoCard.Color.RED);
                        } catch (InvalidColorSubmissionException e) {
                            System.out.println("Invalid Color Submission: " + e.getMessage());
                            System.out.println("You get an extra card - please play a valid card!");
                            game.submitDraw(currentPlayer);
                        } catch (InvalidValueSubmissionException e) {
                            System.out.println("Invalid Value Submission: " + e.getMessage());
                            System.out.println("You get an extra card - please play a valid card!");
                            game.submitDraw(currentPlayer);
                        } catch (InvalidPlayerTurnException e) {
                            System.out.println("Invalid Player Turn: " + e.getMessage());
                            System.out.println("You get an extra card - please play a valid card!");
                            game.submitDraw(currentPlayer);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("If you don't know how to play try tipping 'help'");
                }
                unoInput = "null";

            }

//Check if the game is over

            if (gameExit || game.exitGame) {
                if (game.checkIfPlayerHas500Points()) { //if a player has reached 500 points
                    gameOver = true;
                    break;
                }

// After a round the player(s) can decide whether or not they want to play another round by typing "yes".
// If they type in anything else the game ends.
                System.out.println("Next Round? Type Yes");
                Scanner playerinput = new Scanner(System.in);
                String input2 = playerinput.nextLine().toLowerCase();

                if (input2.equalsIgnoreCase("yes")) {
                    game.start(game);
                    System.out.println("New Round start:");
                } else {
                    gameOver = true;
                }
            }

        }
        System.out.println("Game Over. Thank you for playing!");
    }
}

