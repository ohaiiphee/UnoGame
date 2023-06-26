import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InvalidPlayerTurnException {

        int numberPlayers = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Enter the number of players: ");
            Scanner scanner = new Scanner(System.in);
            numberPlayers = scanner.nextInt();


            if (numberPlayers >= 2 && numberPlayers <= 10) {
                validInput = true;
            } else {
                System.out.println("The number of players must be between 2 and 10. Please enter a number again.");
            }

        }

        Game game = new Game(numberPlayers);


//start the game
        game.start(game);

//Game loop
        boolean gameOver = false;
        boolean gameExit = false;

        while (!gameOver) {
//Get the top card
            UnoCard topCard = game.getTopCard();
            System.out.println("Top Card: " + topCard);


//Check current player
            String currentPlayer = game.getCurrentPlayer();
            System.out.println("The current player is: " + currentPlayer);


//Get player hand
            ArrayList<UnoCard> playerHand = game.getPlayerHand(currentPlayer);
            topCard.setNumberHandCards(playerHand.size());
            System.out.println(currentPlayer + "'s cards: (" + topCard.getNumberHandCards() + ") " + playerHand);


//Prompt the user for input
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the index of the card you want to play (or -1 to draw a card): ");
            String input = scanner.nextLine().toLowerCase();
            int cardIndex;
            if (input.equals("uno")) {
                System.out.println("UNO");
            } else if (input.equals("help")) {
                Game.helpMenu();

            } else if (input.equals("exit")) {
                gameExit = true;
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

            }

//Check if the game is over
            if (gameExit || game.isGameOver()) {
                gameOver = true;
            }


//Continue playing until someone has 0 cards!
        }

        System.out.println("Game Over. Thank you for playing!");
    }

}
