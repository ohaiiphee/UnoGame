import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) throws InvalidPlayerTurnException {

        int numberHumanPlayers = 0;
        int numberBotPlayers = 0;
        int totalNumberPlayers = 0;
        boolean validInput = false;
        String botColor = null;
        Database.createDatabase();
        String unoInput = " ";


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
                    //bot will play the first card in the that can be played
                    //which means that if a wildCard is before another validCard, the bot will play it --> bots can also participate in the +4 Strafe!
                    if (playerCard2.getColor().equals(game.getTopCard().getColor())) {
                        input = String.valueOf(i);
                        break;
                    }

                    if (playerCard2.getValue().equals(game.getTopCard().getValue())) {
                        input = String.valueOf(i);
                        break;
                    }

                    //check if it's a valid play
                    if (playerCard2.getColor().equals(UnoCard.Color.BLACK)) {
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

//Prompt the user for input

            int cardIndex;

            if (input.equals("help")) {
                game.helpMenu();

            } else if (input.equals("exit")) {
                gameExit = true;
            } else if (input.equals("uno") && playerHand.size() == 1) {
                System.out.println("UNO!");
                unoInput = "uno";
            } else if (input.equals("uno") && playerHand.size() != 1) {
                System.out.println("You have more than 1 card in your hand!");
                //not working properly - user gets a card even if they call uno/after calling uno and trying to do -1
            } else if (!input.equals("uno") && playerHand.size() == 1 && !input.equals("-1") && !unoInput.equalsIgnoreCase("uno")) {
                System.out.println("You forgot to say UNO - you get an extra card!");
                game.submitDraw(currentPlayer);
            } else {
                try {

                    cardIndex = Integer.parseInt(input);


                    if (cardIndex == -1) {
//Draw a card
                        try {
                            game.submitDraw(currentPlayer);
                        } catch (InvalidPlayerTurnException e) {
                            System.out.println("Invalid Player Turn: " + e.getMessage());
                        }
                    } else {

//Submit a player card
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
            if (game.checkIfPlayerHas500Points()) { //if a player has reached 500 points
                gameOver = true;
                break;
            }
                if (gameExit || game.isGameOver()) {
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


//Continue playing until someone has 0 cards!


            }
            System.out.println("Game Over. Thank you for playing!");
        }
    }

