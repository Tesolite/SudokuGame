import java.util.*;

public class Sudoku {

    public static void main(String[] args) throws InterruptedException {

        //Initial variables for saving games and game boards, and checking whether a player wishes to continue the game

        //Flag to check whether a player wishes to exit the sudoku program
        int exitFlag = 0;
        //2D arrays for storing games if a player wishes to continue later, as well as the original board
        int[][] savedBoard = null;
        int[][] savedOriginalBoard = null;
        //counter for amount of games saved
        int savedGamesCount = 0;
        //array for storing board template, to be used for generating the same game that was saved using the template's seed.
        int[][] gameReplays = new int[9][];
        //2D string array, containing the seed of the generated game and an array of moves taken by the player
        String[][] gameReplaysRewatch = new String[9][];

        //While the player does not wish to exit the game, generate the initial menu and a new board.
        while (exitFlag != 1) {
            //Initiating 2D array which will contain 1 possible answer for the board, from which a player board will be made.
            int[][] sudokuAnswers = makeGrid(9);

            //If there is a game in progress that was saved, offer to continue the game.
            if (savedBoard != null) {
                System.out.println("------------------------------------------------------------------------------------------------");
                System.out.println("You have a saved game in progress. Continue? [y]/[n]");
                System.out.println("------------------------------------------------------------------------------------------------");
                //Otherwise, generate a new board for the player to play
            } else {
                System.out.println("----------------------------------------------------------------------------------");
                System.out.println("Generating Board...");
                generateBoard(sudokuAnswers);
                generateBoardComplete(sudokuAnswers);
                System.out.println("----------------------------------------------------------------------------------");
                System.out.println("Generation complete");
                System.out.println("----------------------------------------------------------------------------------\n\n");
                System.out.println("----------------------------------------------------------------------------------");
                //Inform the user that the various elements of the board, as well as the board itself, are all valid.
                System.out.println("Checking generated board... ");
                System.out.println("----------------------------------------------------------------------------------");
                System.out.println("Rows valid: " + validRows(sudokuAnswers));
                System.out.println("Columns valid: " + validColumns(sudokuAnswers));
                System.out.println("Blocks valid: " + validBlocks(sudokuAnswers));
                System.out.println("Board valid: " + validBoard(sudokuAnswers));

                //Present which difficulties are available for the user to choose
                System.out.println("------------------------------------------------------------------------------------------------");
                System.out.println("Choose your difficulty: \t [E]asy \t [M]edium \t [H]ard \t \t [Exit]");
                System.out.println("------------------------------------------------------------------------------------------------\n");

                //If the user has saved any games, these will be visible here, as well as an option to view them
                if (savedGamesCount > 0) {
                    System.out.println("======================================");
                    System.out.println("You have " + savedGamesCount + " games saved.   [view]");
                    System.out.println("======================================\n");
                }
            }

            //Copy the sudoku answers to the player board, which will be presented to the user
            int[][] playerBoard = Arrays.stream(sudokuAnswers).map(int[]::clone).toArray(int[][]::new);
            //Also save the original board before any modifications are done to it. This will be used to validate moves later.
            int[][] originalBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
            //Initiate the undo stack, where moves will be stored to provide the undo functionality
            Stack<String> undo = new Stack<>();
            //a flag for telling the game whether the move is done through undo, which will change the behaviour of the game's features.
            boolean moveIsUndo = false;
            //Initiate the redo stack, where moves will be stored to provide the redo functionality
            Stack<String> redo = new Stack<>();
            //a flag for telling the game whether the move is done through the redo function, which will change the game's behaviour accordingly.
            boolean moveIsRedo = false;
            //Arraylist to store all the moves of the player
            ArrayList<String> allMoves = new ArrayList<>();


            //flag for checking whether user input is valid
            boolean validAnswer;
            //flag for checking whether the user wants to continue the game
            boolean continueGame = true;

            do {
                validAnswer = true;
                //Scan for response regarding difficulty, or accessing other options such as viewing replays or continuing game.
                Scanner inputDifficulty = new Scanner(System.in);
                System.out.print("Response: ");
                //Convert user input into a string
                String difficulty = inputDifficulty.nextLine();
                System.out.println();

                //If a user has decided to save an in-progress game and they wish to continue it, return the player's board into the state it was in
                //when the game was saved and print it out
                if (savedBoard != null && difficulty.equalsIgnoreCase("y")) {
                    playerBoard = Arrays.stream(savedBoard).map(int[]::clone).toArray(int[][]::new);
                    originalBoard = Arrays.stream(savedOriginalBoard).map(int[]::clone).toArray(int[][]::new);
                    printGrid(playerBoard);
                    //If a user decides not to continue the game, clear the arrays that store this data and
                    //also set the flag to false to indicate that the player does not wish to continue.
                } else if (savedBoard != null && difficulty.equalsIgnoreCase("n")) {
                    continueGame = false;
                    savedBoard = null;
                    savedOriginalBoard = null;
                    break;

                }


                //If the player wants to view their replays, access this screen.
                else if (difficulty.equalsIgnoreCase("view") && savedGamesCount > 0) {
                    //flag for if rewatch is chosen, so that user returns to menu once they have rewatched their game.
                    boolean rewatchFlag = false;
                    System.out.println("===========================");
                    for (int i = 0; i < 9; i++) {
                        if (gameReplaysRewatch[i] != null) {
                            System.out.println("Save slot #[" + (i + 1) + "]");
                        }
                    }
                    System.out.println("\nPlease enter the number of the save slot you wish to access, or [q] to exit");
                    System.out.println("===========================================================================");
                    Scanner replaySelectScan = new Scanner(System.in);
                    boolean validSelection = true;
                    do {
                        System.out.print("Selection: ");
                        String replaySelect = replaySelectScan.nextLine();
                        if (replaySelect.equalsIgnoreCase("q")) {
                            continueGame = false;
                            break;
                        }
                        //Check whether the input is a digit and if it's a single-digit number (only 9 replays can be stored)
                        else if (Character.isDigit(replaySelect.charAt(0)) && replaySelect.length() == 1) {
                            int saveSlot = Integer.parseInt(replaySelect) - 1; // Subtract 1 from input to account for index offset.
                            //Check if user's choice exists, if it does then offer to either let them replay the game
                            //of watch how they played the game before
                            if (gameReplaysRewatch[saveSlot] != null) {
                                System.out.println("Select whether you want to [replay] or [rewatch]");
                                boolean validChoice = true;
                                do {
                                    System.out.print("Choice: ");
                                    replaySelect = replaySelectScan.nextLine();

                                    //If the user wants to replay the game, set the sudoku board to the same as it was before and
                                    //ask which difficulty they want to play
                                    if (replaySelect.equalsIgnoreCase("replay")) {
                                        sudokuAnswers = generateTemplate(gameReplays[saveSlot]);
                                        System.out.println("Choose your difficulty this time around: [E]asy     [M]edium     [H]ard");

                                        //flag to check if difficulty is valid.
                                        boolean validReplayDifficulty;
                                        do {
                                            validReplayDifficulty = true;
                                            System.out.print("Difficulty: ");
                                            replaySelect = replaySelectScan.nextLine();

                                            //Medium mode hides 3 values in each block
                                            if (replaySelect.equalsIgnoreCase("e")) {
                                                playerBoard = hideValues(Arrays.stream(sudokuAnswers).map(int[]::clone).toArray(int[][]::new), 3);
                                                originalBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
                                                System.out.println("\n\n" + "====".repeat(2) + " GAME START " + "====".repeat(3) + "\n");
                                                printGrid(playerBoard);

                                                //Medium mode hides 4 values in each block
                                            } else if (replaySelect.equalsIgnoreCase("m")) {
                                                playerBoard = hideValues(Arrays.stream(sudokuAnswers).map(int[]::clone).toArray(int[][]::new), 4);
                                                originalBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
                                                System.out.println("\n\n" + "====".repeat(2) + " GAME START " + "====".repeat(3) + "\n");
                                                printGrid(playerBoard);

                                                //Hard mode hides 5 values in each block
                                            } else if (replaySelect.equalsIgnoreCase("h")) {
                                                playerBoard = hideValues(Arrays.stream(sudokuAnswers).map(int[]::clone).toArray(int[][]::new), 5);
                                                originalBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
                                                System.out.println("\n\n" + "====".repeat(2) + " GAME START " + "====".repeat(3) + "\n");
                                                printGrid(playerBoard);
                                            } else {
                                                validReplayDifficulty = false;
                                                System.out.println("Invalid difficulty. Try again");
                                            }
                                        } while (validReplayDifficulty == false);

                                        //If the user wants to rewatch the game, call the rewatchGame method to perform that functionality
                                        //Also set the rewatch flag to true so that the user returns to the menu one they have watched the game.
                                    } else if (replaySelect.equalsIgnoreCase("rewatch")) {
                                        rewatchFlag = true;
                                        rewatchGame(gameReplaysRewatch, saveSlot);
                                        break;

                                    } else {
                                        validChoice = false;
                                        System.out.println("Invalid choice. Try again.");
                                    }
                                } while (validChoice == false);
                            } else {
                                System.out.println("Invalid selection. Try again.");
                                validSelection = false;
                            }
                        }
                    } while (validSelection == false);

                    //Stop executing the code below if the user has chosen to rewatch the game, so that they return to menu.
                    if (rewatchFlag == true) {
                        continueGame = false;
                        break;
                    }

                }
                //If the user wants to exit the program, tell them goodbye and exit the program.
                else if (difficulty.equalsIgnoreCase("exit")) {
                    exitFlag = 1;
                    System.out.println("Goodbye!");
                    System.exit(0);

                    //Otherwise, if a valid difficulty is entered, modify the board accordingly
                    //Easy mode hides 3 values in each block
                } else if (difficulty.equalsIgnoreCase("e")) {
                    playerBoard = hideValues(Arrays.stream(sudokuAnswers).map(int[]::clone).toArray(int[][]::new), 3);
                    originalBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
                    System.out.println("\n\n" + "====".repeat(2) + " GAME START " + "====".repeat(3) + "\n");
                    printGrid(playerBoard);

                    //Medium mode hides 4 values in each block
                } else if (difficulty.equalsIgnoreCase("m")) {
                    playerBoard = hideValues(Arrays.stream(sudokuAnswers).map(int[]::clone).toArray(int[][]::new), 4);
                    originalBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
                    System.out.println("\n\n" + "====".repeat(2) + " GAME START " + "====".repeat(3) + "\n");
                    printGrid(playerBoard);

                    //Hard mode hides 5 values in each block
                } else if (difficulty.equalsIgnoreCase("h")) {
                    playerBoard = hideValues(Arrays.stream(sudokuAnswers).map(int[]::clone).toArray(int[][]::new), 5);
                    originalBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
                    System.out.println("\n\n" + "====".repeat(2) + " GAME START " + "====".repeat(3) + "\n");
                    printGrid(playerBoard);


                    //Two hidden difficulties, implemented in order to allow for testing of the program. Normal users
                    //will remain unaware of these features as they are not listed anywhere.

                    //babyMode difficulty only hides one value per block
                } else if (difficulty.equalsIgnoreCase("babyMode")) {
                    playerBoard = hideValues(Arrays.stream(sudokuAnswers).map(int[]::clone).toArray(int[][]::new), 1);
                    originalBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
                    printGrid(playerBoard);

                    //veryVeryCleverr hides no values on the board and provides a fast way to win the game and test
                    //features (mainly replay features) that are available once the board has been completed.
                    //A feature purely for testing specific methods, and as such introduces some bugs that would otherwise not exist.
                } else if (difficulty.equalsIgnoreCase("veryVeryCleverr")) {
                    playerBoard = hideValues(Arrays.stream(sudokuAnswers).map(int[]::clone).toArray(int[][]::new), 0);
                    originalBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
                    printGrid(playerBoard);
                } else {
                    System.out.println("Invalid answer. Please only enter the character in the brackets to make your choice.");
                    validAnswer = false;
                }

            } while (validAnswer == false);


            //A seed for the game, saving values once they have been altered by other methods, such as hideValues.
            //More representative of the game played than methods that save the seeds of the template.
            int[] boardSeed = getGameSeed(playerBoard);

            //While the player wishes to continue the game, prompt them to make moves
            while (continueGame == true) {
                System.out.println("Enter the move you would like to make, 'help' for a list of commands, or 'quit' to quit this puzzle");
                System.out.print("Move: ");

                //Get user input and convert to string
                Scanner inputMove = new Scanner(System.in);
                String move = inputMove.nextLine().toUpperCase();

                //If the user wishes to quit, prompt them to save their game.
                if (move.equalsIgnoreCase("quit")) {
                    System.out.println("Would you like to save your game? [y]/[n]");
                    boolean validateSaveResponse;
                    do {
                        validateSaveResponse = true;
                        System.out.print("Response: ");
                        move = inputMove.nextLine();
                        if (move.equals("y")) {
                            savedBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
                            savedOriginalBoard = Arrays.stream(originalBoard).map(int[]::clone).toArray(int[][]::new);

                            //If they quit without saving their progress, they lose. Prompt return to menu.
                        } else if (move.equals("n")) {
                            savedBoard = null;
                            savedOriginalBoard = null;
                            System.out.println("======================================================");
                            System.out.println("GAME OVER. Would you like to return to menu? [y]/[n]");
                            System.out.println("======================================================");
                            boolean newGameResponseValid = true;
                            do {
                                System.out.print("Response: ");
                                move = inputMove.nextLine();
                                if (move.equalsIgnoreCase("y")) {
                                    break;
                                    //If user chooses to stop playing, set exitFlag to 1 to signal to the program to terminate.
                                } else if (move.equalsIgnoreCase("n")) {
                                    exitFlag = 1;
                                    System.out.println("Thank you for playing, goodbye!");
                                } else {
                                    System.out.println("Invalid input. Please enter your input again.");
                                    newGameResponseValid = false;
                                }
                            } while (newGameResponseValid == false);
                        } else {
                            System.out.println("Invalid input. Please enter your input again.");
                            validateSaveResponse = false;
                        }
                    } while (validateSaveResponse == false);
                    //Leave current game.
                    break;

                    //Print out a list of commands that are available to the user while they are playing.
                } else if (move.equalsIgnoreCase("help")) {
                    System.out.println("\n\n\n" + "------".repeat(10));
                    System.out.println();
                    System.out.println("TUTORIAL:To make a move, first write the column letter and then row number, followed by the value you wish to enter. ");
                    System.out.println("Example: 'B6 4' would place the number four in row 6, in the B column");
                    System.out.println();
                    System.out.println("----Commands" + "-----".repeat(9));
                    System.out.println("quit: \t quits the game, counts as a loss");
                    System.out.println("undo: \t undoes the previous move");
                    System.out.println("redo: \t redoes an undone move");
                    System.out.println("------".repeat(10) + "\n\n\n");
                    printGrid(playerBoard);
                    continue;

                    //Undo previous move
                } else if (move.equalsIgnoreCase("undo")) {
                    if (undo.empty() == false) {
                        //Set flag indicator to true to inform the game that next move is caused by undo function
                        moveIsUndo = true;
                        //Pop value from stack and set it as the next move
                        move = undo.pop();

                    } else {
                        System.out.println("\nNo moves left to undo.\n");
                        continue;
                    }

                    //Redo a previously undone move
                } else if (move.equalsIgnoreCase("redo")) {
                    if (redo.empty() == false) {
                        //Pop value from stack and set as next move. Also flag move as redo move.
                        move = redo.pop();
                        moveIsRedo = true;
                    } else {
                        System.out.println("\nNo moves left to redo.\n");
                        continue;
                    }
                } else if (move.length() < 4) {
                    System.out.println("\n--------------------------------");
                    System.out.println("ERROR: Invalid input. Try again.");
                    System.out.println("--------------------------------\n");
                    continue;
                }

                //Create variables to convert user input into valid moves
                int columnNum = Character.getNumericValue(move.charAt(0)) - 10;
                int rowNum = Character.getNumericValue(move.charAt(1)) - 1;
                int inputValue = Character.getNumericValue(move.charAt(3));

                //Variables used to check if move is valid
                boolean rowInRange = rowNum < playerBoard.length;
                boolean columnInRange = columnNum < playerBoard.length;
                boolean inputInRange = inputValue >= 0 && inputValue <= playerBoard.length;

                //Check if move is valid
                if (Character.isAlphabetic(move.charAt(0)) && columnInRange &&
                        Character.isDigit(move.charAt(1)) && rowInRange &&
                        Character.isSpaceChar(move.charAt(2)) &&
                        (Character.isDigit(move.charAt(3))) && inputInRange &&
                        move.length() == 4) {
                    //Check if user is attempting to change pre-set values.
                    if (originalBoard[rowNum][columnNum] != 0) {
                        System.out.println("\n-----------------------------------------------");
                        System.out.println("ERROR: Cannot change values of pre-set squares.");
                        System.out.println("-----------------------------------------------\n");
                        //If move is allowed, play it.
                    } else {
                        System.out.println("\n\n" + "~~~~~~~".repeat(playerBoard.length));
                        System.out.println("\n| Played move [" + move + "]... |\n");
                        //Add move to array of all moves.
                        allMoves.add(move);

                        //Push move into redo stack if needed
                        if (moveIsUndo == true && moveIsRedo == false) {
                            redo.push(move.substring(0, 3) + playerBoard[rowNum][columnNum]);

                            //If the move being played isn't one caused by undo or redo, clear the stack.
                        } else if (moveIsRedo != true) {
                            redo.clear();
                        }


                        //If move isn't one that's being undone, push the value of the position into undo stack before it's changed.
                        if (moveIsUndo == false) {
                            undo.push(move.substring(0, 3) + String.valueOf(playerBoard[rowNum][columnNum]));
                        }

                        //Change value of the position the user specified into one also specified by the user.
                        playerBoard[rowNum][columnNum] = inputValue;
                        printGrid(playerBoard);
                        System.out.println("~~~~~~~".repeat(playerBoard.length) + "\n");

                        //Reset undo and redo flags for move.
                        moveIsRedo = false;
                        moveIsUndo = false;
                    }
                } else {
                    System.out.println("\n--------------------------------");
                    System.out.println("ERROR: Invalid input. Try again.");
                    System.out.println("--------------------------------\n");
                }
                //If the player finishes the board and it's a valid board, they win.
                //Prompt whether the player would like to save the game or not.
                if (emptySpace(playerBoard) == false && validBoard(playerBoard)) {
                    System.out.println("===============================================================");
                    System.out.println("CONGRATULATIONS! You have successfully solved the sudoku board.");
                    System.out.println("Would you like to save a replay of this game? [y]/[n]");
                    System.out.println("===============================================================");
                    System.out.print("Response: ");
                    Scanner saveReplayResponse = new Scanner(System.in);
                    boolean replayResponseValidator = true;
                    do {
                        String replayResponse = saveReplayResponse.nextLine();
                        if (replayResponse.equalsIgnoreCase("y")) {
                            int saveSlot; //select a slot between 1-9 into which the game should be saved.
                            boolean saveSlotValidator = true; //flag to check if slot is valid.
                            savedGamesCount++; //Increment total amount of saved games
                            if (savedGamesCount > 9) {
                                savedGamesCount = 9; //Prevent the counter from going above 9, as this is the max.
                            }
                            String allMovesArray = allMoves.toString(); //Convert arraylist into string for storage.
                            int[] savedBoardSeed = boardSeed; //Save the board seed in an int array

                            String seedAsString = Arrays.toString(savedBoardSeed); //Convert board seed into string for storage

                            //Let user choose their save slot
                            System.out.println("Choose your save slot [1-9] (WARNING: If save slot has a replay saved, that replay is overwritten)");
                            do {
                                System.out.print("Response: ");
                                replayResponse = saveReplayResponse.nextLine();
                                if (replayResponse.length() == 1 && Character.isDigit(replayResponse.charAt(0)) &&
                                        Character.getNumericValue(replayResponse.charAt(0)) > 0 && Character.getNumericValue(replayResponse.charAt(0)) < 10) {
                                    saveSlot = Integer.parseInt(replayResponse) - 1;

                                    //Store the seed of the initial player board, and the moves the player played
                                    //Used for rewatching the game
                                    String[] rewatchData = {seedAsString, allMovesArray};
                                    //Store the rewatch data in the slot specified by the user.
                                    gameReplaysRewatch[saveSlot] = rewatchData;

                                    //Save the seed of the board template
                                    //Used for replaying the game.
                                    gameReplays[saveSlot] = getTemplateSeed(playerBoard);
                                    System.out.println("Game successfully saved.\n");
                                } else {
                                    saveSlotValidator = false;
                                    System.out.println("Invalid save slot. Please try again.");
                                }

                            } while (saveSlotValidator == false);

                        } else if (replayResponse.equalsIgnoreCase("n")) {
                            System.out.println("Okay, returning to menu without saving...");

                        } else {
                            replayResponseValidator = false;
                            System.out.println("Invalid input. Please try again.");
                        }

                    } while (replayResponseValidator == false);


                    break;
                }
            }
        }
    }


    //Makes a grid of specified size using 2D array. This program only made grids of size 9.
    static int[][] makeGrid(int size) {

        //Set all values in 2D array to be 0, representing empty spaces.
        int[][] sudokuGrid = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                sudokuGrid[row][column] = 0;
            }
        }

        return sudokuGrid;
    }

    //Formats the printing of the grid to be presentable.
    static void printGrid(int[][] grid) {
        int rowNum = 0;
        int columnNum = 0;
        int gridSqrt = (int) Math.sqrt(grid.length);
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        System.out.print("     ");
        for (int i = 0; i < grid.length; i++) {
            if (i % gridSqrt == 0 && i != 0) {
                System.out.print(" ".repeat(gridSqrt));
            }
            if ((i + 1) % gridSqrt == 0) {
                System.out.print(alphabet.charAt(i));
            } else {
                System.out.print(alphabet.charAt(i) + " ");
            }
        }
        System.out.println();
        for (int[] row : grid) {
            if (rowNum % gridSqrt == 0 && rowNum != 0) {
                System.out.println("---".repeat(row.length));
            }
            rowNum++;
            System.out.print("[" + rowNum + "]  ");

            for (int num : row) {
                if (columnNum % gridSqrt == 0 && columnNum != 0) {
                    System.out.print(" | ");
                }
                if ((columnNum + 1) % gridSqrt == 0) {
                    if (num == 0) {
                        System.out.print("X");
                    } else {
                        System.out.print(num);
                    }
                } else {
                    if (num == 0) {
                        System.out.print("X" + "-");
                    } else {
                        System.out.print(num + "-");
                    }
                }
                columnNum++;


            }
            System.out.println();
            columnNum = 0;
        }
        System.out.println();
    }

    //Checks if rows are valid
    private static boolean validRows(int[][] grid) {
        ArrayList<Integer> allValuesInRow = new ArrayList<>(); //Stores all values of rows.
        HashSet<Integer> uniqueValuesInRow = new HashSet<>(); //Stores only the unique values of rows.
        int flag = 0; // flag to indicate whether all rows are valid or not

        for (int row = 0; row < grid.length; row++) {
            for (int columnIndex = 0; columnIndex < grid.length; columnIndex++) {
                //If the value is not an empty space, save it in both the arraylist and the hashset
                if (grid[row][columnIndex] != 0) {
                    allValuesInRow.add(grid[row][columnIndex]);
                    uniqueValuesInRow.add(grid[row][columnIndex]); //HashSets only store values if they are not present already.

                }
            }

            //If any of the rows have less unique values than total values, change flag to indicate invalid rows.
            if (allValuesInRow.size() != uniqueValuesInRow.size()) {
                flag = 1;
            }
            //Reset both the arraylist and hashset for next row.
            allValuesInRow.clear();
            uniqueValuesInRow.clear();
        }
        //Method returns false if even a single row is invalid.
        if (flag == 1) {
            return false;
        } else {
            return true;
        }
    }

    //Checks if all columns are valid using the same principle as validRows.
    private static boolean validColumns(int[][] grid) {
        ArrayList<Integer> allValuesInColumn = new ArrayList<>();
        HashSet<Integer> uniqueValuesInColumn = new HashSet<>();
        int flag = 0;

        for (int column = 0; column < grid.length; column++) {
            for (int rowIndex = 0; rowIndex < grid.length; rowIndex++) {
                if (grid[rowIndex][column] != 0) {
                    allValuesInColumn.add(grid[rowIndex][column]);
                    uniqueValuesInColumn.add(grid[rowIndex][column]);
                }

            }

            if (allValuesInColumn.size() != uniqueValuesInColumn.size()) {
                flag = 1;//If even a single column is invalid, flag is set so function returns false
            }
            allValuesInColumn.clear();
            uniqueValuesInColumn.clear();
        }
        if (flag == 1) {
            return false;
        } else {
            return true;
        }
    }

    //Checks if all blocks are valid
    private static boolean validBlocks(int[][] grid) {
        ArrayList<Integer> allValuesInBlock = new ArrayList<>();
        HashSet<Integer> uniqueValuesInBlock = new HashSet<>();
        int flag = 0;
        int gridSqrt = (int) Math.sqrt(grid.length);
        int blockStartRow = 0; //Variable that serves as index for starting row of a block
        int blockStartColumn = 0; //Variable that serves as index for starting column of a block
        int blockArrayIndex = 0; //A counter for the amount of values in a block
        int blockEndRow = gridSqrt; //Variable that serves as index for end row of a block
        int blockEndColumn = gridSqrt; //Variable that serves as index for end column of a block
        int blocksInRow = 0; //A counter for the amount of blocks across the length of the grid.
        int previousBlocksInRow = 0;//A counter for the amount of blocks from previous iteration, used to check if a block has been filled


        //Iterate through the rows while the current row is >= starting row index and < ending row index, and row is within the range of the grid.
        for (int currentRow = blockStartRow; currentRow >= blockStartRow && currentRow < blockEndRow && currentRow != grid.length; currentRow++) {
            //Iterate through the columns while the current column is >= starting column index and < ending column index, and column is within the range of the grid.
            for (int currentColumn = blockStartColumn; currentColumn >= blockStartColumn && currentColumn < blockEndColumn && currentColumn != grid.length; currentColumn++) {
                //If the value in current position is not 0, save it to the array and hashset
                if (grid[currentRow][currentColumn] != 0) {
                    allValuesInBlock.add(grid[currentRow][currentColumn]);
                    uniqueValuesInBlock.add(grid[currentRow][currentColumn]);
                }
                //Increment amount of values in block by 1
                blockArrayIndex++;

                //If the block is filled with the same amount of values as the rows and columns, and the position is within range of grid
                //Finish identifying the values as part of the current block and check if it's valid.
                if (blockArrayIndex == grid.length && blockEndColumn <= grid.length) {
                    if (allValuesInBlock.size() != uniqueValuesInBlock.size()) {
                        flag = 1; //If even a single block is invalid, flag is set so function returns false
                    }
                    //Set the starting column for the next block to be where the current block finishes
                    blockStartColumn = currentColumn + 1;
                    //Increment the end column index at a fixed rate of sqrt(grid) to provide same amount of columns to iterate through
                    blockEndColumn += gridSqrt;
                    //Since we are starting a new block, amount of values in new block is 0
                    blockArrayIndex = 0;
                    //Increment count of blocks in row to signify a completed block
                    blocksInRow++;
                    //Clear the values in arraylist and hashset as the block has been validated already.
                    allValuesInBlock.clear();
                    uniqueValuesInBlock.clear();
                }

                //If a new block is being validated, and is within the maximum possible amount of blocks in the row, set
                //the new parameters of rows to iterate over
                if (blocksInRow > previousBlocksInRow && blocksInRow < gridSqrt) {
                    //Reset the current row to the starting row, so that they can be iterated over using new column values.
                    currentRow = blockStartRow;
                    //A new block has started, so the previous values is set to the current amount of completed blocks in row.
                    previousBlocksInRow = blocksInRow;

                    //If the length of the grid has been covered by the blocks, move onto the next set of rows.
                } else if (blocksInRow == gridSqrt) {
                    //Set the starting row for the next block to be back at the start of the grid.
                    blockStartRow = currentRow + 1;
                    //Increment end row index at fixed rate of sqrt(grid) to provide same amount of rows to iterate through.
                    blockEndRow += gridSqrt;
                    //Reset the starting and ending columns
                    blockStartColumn = 0;
                    blockEndColumn = gridSqrt;

                    //Reset counter for amount of blocks in row, and counter of previous amount of blocks in row.
                    blocksInRow = 0;
                    previousBlocksInRow = 0;
                }
            }


        }
        if (flag == 1) {
            return false;
        } else {
            return true;
        }
    }

    //Method to validate board
    private static boolean validBoard(int[][] grid) {
        //If all rows, columns, and blocks are valid, the board follows the rules of sudoku and thus is valid.
        if (validRows(grid) == true &&
                validColumns(grid) == true &&
                validBlocks(grid) == true) {
            return true;
        } else {
            return false;
        }
    }

    //Check if a move is valid
    private static boolean validMove(int[][] grid, int row, int column, int number) {
        //Create a copy of a grid to test move on and enter the hypothetical move into the test grid
        int[][] testGrid = Arrays.stream(grid).map(int[]::clone).toArray(int[][]::new);
        testGrid[row][column] = number;

        //If the board remains valid despite the move, it is a valid move.
        if (validBoard(testGrid) == true) {
            return true;
        } else {
            return false;
        }
    }

    //First method used in generating a playable game, sets random values into positions that do not interact/invalidate each other.
    private static void generateBoard(int[][] grid) {
        Random random = new Random();
        int gridSqrt = (int) Math.sqrt(grid.length);
        int columnIndex = 0;
        int offsetCounter = 0;

        for (int row = 0; row < grid.length; row++) {

            //At the end of each block, increment offsetCounter and reset columnIndex
            // Used to ensure that, on top of values not being in the same block, randomised values are not in the same column either.
            if (row % gridSqrt == 0 && row != 0) {
                offsetCounter++;
                columnIndex = 0;
            }
            //Set value of column which will be used to set the position of randomised number.
            int currentColumn = offsetCounter + columnIndex;

            //Randomise the number in the specified row and column
            grid[row][currentColumn] = random.nextInt(grid.length) + 1;
            //Increment columnIndex by sqrt(grid) to avoid randomised values being in the same grid.
            columnIndex += gridSqrt;
        }
    }

    /*
    Method to get the randomised values of the generateBoard metho
    Works in an identical way, but instead of setting the values in the specified position,
    it retrieves them and stores them in an int array.
    */
    private static int[] getTemplateSeed(int[][] grid) {
        int gridSqrt = (int) Math.sqrt(grid.length);
        int[] seed = new int[grid.length]; //Stores the retrieved values and returns them when the method is called
        int seedIndex = 0; //Index of seed array
        int columnIndex = 0;
        int offsetCounter = 0;

        for (int row = 0; row < grid.length; row++) {

            if (row % gridSqrt == 0 && row != 0) {
                offsetCounter++;
                columnIndex = 0;
            }
            int currentColumn = offsetCounter + columnIndex;

            seed[seedIndex] = grid[row][currentColumn];
            seedIndex++; // Point to next index in which value should be stored
            columnIndex += gridSqrt;
        }
        return seed;
    }

    //Method which iterates through all values in a grid and returns them as single int array.
    //Used for generating replays for a saved game
    private static int[] getGameSeed(int[][] grid) {
        //Create a grid with sufficient size for all values in 2D array.
        int[] seed = new int[grid.length * grid.length];
        int seedIndex = 0;
        //Iterate through all values in grid and save each one in the array.
        for (int[] row : grid) {
            for (int num : row) {
                seed[seedIndex] = num;
                seedIndex++; //Point to next index in which value should be stored.
            }
        }
        return seed;
    }


    //Implementation of backtracking algorithm to generate a proper sudoku board
    private static boolean generateBoardComplete(int[][] grid) {
        long genStart = System.currentTimeMillis(); //Variable that stores the time at which board generation has been started

        if (emptySpace(grid) == false) {
            return true;
        }

        //Iterate through each value in the grid that was passed in as a parameter
        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid.length; column++) {
                //If a value is empty, iterate through the list of possible moves and find one that works
                if (grid[row][column] == 0) {

                    //Iterate through each value that would be a valid move
                    for (int value = 1; value <= grid.length; value++) {

                        //If a value is a valid move, set that value as the value in the grid
                        if (validMove(grid, row, column, value) == true) {
                            grid[row][column] = value;

                            //If there is a valid move available for the next grid, then continue the process
                            //Otherwise, set the value back to zero, look for the next valid move, and repeat.
                            if (generateBoardComplete(grid) == true) {
                                return true;

                            }
                        } else {
                            grid[row][column] = 0;
                        }

                        //While there is an empty space on the board, check how long backtracking is taking
                        if (emptySpace(grid) == true) {
                            long checkTime = System.currentTimeMillis(); //Checks current time
                            long genTime = checkTime - genStart; //Compare current time to time since backtracking started

                            //If it has been longer than 2.5 seconds since board generation has started, start with a new board.
                            if (genTime > 2500) {
                                System.out.println("Error generating board, retrying...");
                                //Reset whole board
                                for (int resetRow = 0; resetRow < grid.length; resetRow++) {
                                    for (int resetColumn = 0; resetColumn < grid.length; resetColumn++) {
                                        grid[resetRow][resetColumn] = 0;
                                    }
                                }
                                //Generate new random values
                                generateBoard(grid);
                                //Call this function again
                                generateBoardComplete(grid);

                                //Reset rows, columns, and values checked
                                row = 0;
                                column = 0;
                                value = 0;
                                //Reset start timer
                                genStart = System.currentTimeMillis();
                            }

                        }
                    }
                    //Return false if there is no valid move available.
                    return false;
                }
            }

        }
        //If there are no empty values left, the board has been solved and thus return true;
        return true;
    }

    //Method that turns template seeds into their respective templates
    //Works identically to getTemplate seed, but rather than retrieving values, it sets them.
    private static int[][] generateTemplate(int[] seed) {
        int[][] grid = makeGrid(seed.length);
        int gridSqrt = (int) Math.sqrt(grid.length);
        int seedIndex = 0;
        int columnIndex = 0;
        int offsetCounter = 0;

        for (int row = 0; row < grid.length; row++) {

            if (row % gridSqrt == 0 && row != 0) {
                offsetCounter++;
                columnIndex = 0;
            }
            int currentColumn = offsetCounter + columnIndex;

            grid[row][currentColumn] = seed[seedIndex];
            seedIndex++;
            columnIndex += gridSqrt;
        }
        generateBoardComplete(grid);
        return grid;
    }

    /*
    Hides values randomly in each block of a sudoku board
    Takes in 2 parameters:
    - The grid of which the values should be hidden
    - The amount of values per block that should be hidden
    Returns the grid with the values hidden.
     */
    static int[][] hideValues(int[][] grid, int amountHidden) {
        Random hideIndex = new Random(); //Randomises which positions should be hidden
        HashSet<Integer> removeFromBlock = new HashSet<>(); //Stores indexes of allValuesInBlock arraylist which will be hidden
        ArrayList<Integer> allValuesInBlock = new ArrayList<>();
        HashMap<Integer, Integer> gridLinkRow = new HashMap<>(); //Links a stored value to a row
        HashMap<Integer, Integer> gridLinkColumn = new HashMap<>(); //Links a stored value to a column


        //Variables used to set rows and columns for blocks
        //Use is identical to use in validBlocks method.
        int blockStartColumn = 0;
        int gridSqrt = (int) Math.sqrt(grid.length);
        int blockStartRow = 0;
        int blockArrayIndex = 0;
        int blockEndRow = gridSqrt;
        int blockEndColumn = gridSqrt;
        int blocksInRow = 0;
        int previousBlocksInRow = 0;

        //Iterates through the rows and columns in identical way as validBlocks method.
        for (int currentRow = blockStartRow; currentRow >= blockStartRow && currentRow < blockEndRow && currentRow != grid.length; currentRow++) {
            for (int currentColumn = blockStartColumn; currentColumn >= blockStartColumn && currentColumn < blockEndColumn && currentColumn != grid.length; currentColumn++) {
                //If position has a value, save the value as a key to the row and column it is in
                if (grid[currentRow][currentColumn] != 0) {
                    allValuesInBlock.add(grid[currentRow][currentColumn]);
                    gridLinkRow.put(grid[currentRow][currentColumn], currentRow);
                    gridLinkColumn.put(grid[currentRow][currentColumn], currentColumn);
                }
                blockArrayIndex++;

                if (blockArrayIndex == grid.length && blockEndColumn <= grid.length) {

                    //While the HashSet containing values to hide does not contain the amount specified, generate random values to hide.
                    while (removeFromBlock.size() != amountHidden) {
                        removeFromBlock.add(hideIndex.nextInt(9));
                    }
                    /*
                    For each index of allValuesInBlock stored in removeFromBlock,
                    retrieve the position of the value, using the value as a key for getting the row and array.
                    Then, set the position specified by the row and array to 0, indicating an empty position.
                     */
                    for (int removeIndex : removeFromBlock) {
                        grid[gridLinkRow.get(allValuesInBlock.get(removeIndex))][gridLinkColumn.get(allValuesInBlock.get(removeIndex))] = 0;
                    }

                    blockStartColumn = currentColumn + 1;
                    blockEndColumn += gridSqrt;
                    blockArrayIndex = 0;
                    blocksInRow++;
                    allValuesInBlock.clear();
                    removeFromBlock.clear();
                }


                if (blocksInRow > previousBlocksInRow && blocksInRow < gridSqrt) {
                    currentRow = blockStartRow;
                    previousBlocksInRow = blocksInRow;
                } else if (blocksInRow == gridSqrt) {
                    blockStartRow = currentRow + 1;
                    blockStartColumn = 0;
                    blockEndRow += gridSqrt;
                    blockEndColumn = gridSqrt;
                    blocksInRow = 0;
                    previousBlocksInRow = 0;
                }
            }


        }
        return grid;

    }

    //Method that checks whether there is empty space on a board or not
    private static boolean emptySpace(int[][] grid) {
        //Iterate over all values in 2D array
        for (int[] row : grid) {
            for (int num : row) {
                //If any value is = 0, return true to indicate empty space present
                if (num == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
    Method that lets the user rewatch their gameplay. Takes in 2 values
    - A 2D string array, comprising of a string that contains the seed of the board, and a string that stores all moves made,
    indexed by a slot variable.
    - An int indicating which saved game to replay.
     */
    private static void rewatchGame(String[][] data, int slot) throws InterruptedException {
        String seed = data[slot][0]; //Retrieves the seed stored as the first string in the 2D array

        //Format the seed string so that the values stored in it can be retrieved
        String seedFormat = seed.replaceAll("\\[", "");
        seedFormat = seedFormat.replaceAll("]", "");
        seedFormat = seedFormat.replaceAll(",", "");
        //Split up each value of the seed into its own string
        String[] seedFormatArray = seedFormat.split(" ");
        //Create an int array to store all seed values as they are processed
        int[] seedToInt = new int[seedFormatArray.length];
        //Iterate through each seed string stored in the string array and save it to an int array instead
        for (int i = 0; i < seedFormatArray.length; i++) {
            seedToInt[i] = Integer.parseInt(seedFormatArray[i]);
        }
        int seedIndex = 0; // Keeps track of which value to input into an empty grid next to recreate the saved board.

        String moves = data[slot][1]; //Retrieves the string containing all moves made in the game, stored as the second string in the 2D array

        //Format the string of moves so that they can be processed as moves once again.
        String movesFormat = moves.replaceAll("\\[", "").replaceAll("]", "").replaceAll(" ", "");
        //Separate each move stored into its own string
        String[] allMovesArray = movesFormat.split(",");

        //Generate a brand-new grid
        int[][] sudokuGrid = new int[(int) Math.sqrt(seedToInt.length)][(int) Math.sqrt(seedToInt.length)];

        //Iterate through the grid and add all the values from the seed int array
        for (int row = 0; row < sudokuGrid.length; row++) {
            for (int column = 0; column < sudokuGrid.length; column++) {
                sudokuGrid[row][column] = seedToInt[seedIndex];
                seedIndex++;
            }
        }

        //Print out the initial board that the user was presented with when they first played the game
        System.out.println("\n===========================================================================");
        System.out.println("Initial board");
        System.out.println("===========================================================================\n");
        printGrid(sudokuGrid);

        //Convert all move strings into valid moves for the board and play them out
        for (String singleMove : allMovesArray) {
            int columnNum = Character.getNumericValue(singleMove.charAt(0)) - 10;
            int rowNum = Character.getNumericValue(singleMove.charAt(1)) - 1;
            int inputValue = Character.getNumericValue(singleMove.charAt(2));


            sudokuGrid[rowNum][columnNum] = inputValue;
            Thread.sleep(2000); //A 2 second delay between each move so that they can be properly observed
            System.out.println("=====|| Applied move: " + singleMove.charAt(0) + singleMove.charAt(1) + " " + singleMove.charAt(2) + " ||=====================================================");
            System.out.println("Column: " + singleMove.charAt(0));
            System.out.println("Row: " + singleMove.charAt(1));
            System.out.println("Value: " + singleMove.charAt(2) + "\n");
            printGrid(sudokuGrid);
            System.out.println("==================================================================================\n\n\n");
        }
        System.out.println("Replay complete, returning to menu in 5 seconds");
        Thread.sleep(5000);

    }

}
