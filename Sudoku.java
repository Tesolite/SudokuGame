import java.util.*;

public class Sudoku {

    public static void main(String[] args) {

        int exitFlag = 0;
        int[][] savedBoard = null;
        int[][] savedOriginalBoard = null;
        //  System.out.println(Arrays.toString(sudoku[2]));
        //sudoku[0][0] = 1;
        //sudoku[0][2] = 1;
        //printGrid(sudoku);

        //GAME PRESENTED TO USER WILL HAVE 3 VALUES REMOVED FROM EACH BLOCK
        while (exitFlag != 1) {

            int[][] sudokuAnswers = makeGrid(9);
            if(savedBoard != null){
                System.out.println("------------------------------------------------------------------------------------------------");
                System.out.println("You have a saved game in progress. Continue? [y]/[n]");
                System.out.println("------------------------------------------------------------------------------------------------");
            }else {
            System.out.println("----------------------------------------------------------------------------------");
            System.out.println("Generating Board...");
            generateBoard(sudokuAnswers);
            generateBoardComplete(sudokuAnswers);
            System.out.println("Auto-generated board:");
            printGrid(sudokuAnswers);
            int[] seed = getBoardSeed(sudokuAnswers);
            int[][] seedBoard = generateSeed(seed);
            System.out.println("Seed board:");
            printGrid(seedBoard);
            System.out.println("Generation complete");
            System.out.println("----------------------------------------------------------------------------------");
            System.out.println();
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------");
            System.out.println("Checking generated board... ");
            System.out.println("----------------------------------------------------------------------------------");
            System.out.println("Rows valid: " + validRows(sudokuAnswers));
            System.out.println("Columns valid: " + validColumns(sudokuAnswers));
            System.out.println("Blocks valid: " + validBlocks(sudokuAnswers));
            System.out.println("Board valid: " + validBoard(sudokuAnswers));

                System.out.println("Valid to add 1 to 2nd column in first row: " + validMove(sudokuAnswers, 0, 1, 1));
                System.out.println("------------------------------------------------------------------------------------------------");
                System.out.println("Choose your difficulty: \t [E]asy \t [M]edium \t [H]ard \t \t [Exit]");
                System.out.println("------------------------------------------------------------------------------------------------");
                System.out.println();
            }

            int[][] playerBoard = Arrays.stream(sudokuAnswers).map(int[]::clone).toArray(int[][]::new);
            int[][] originalBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
            Stack<String> undo = new Stack<>();
            boolean moveIsUndo = false;
            Stack<String> redo = new Stack<>();
            boolean moveIsRedo = false;
            String redoRowColumn = "";
            int redoValue = 0;
            boolean saveRedoValue = false;


            boolean validAnswer;
            boolean continueGame = true;
            do {
                /*if(savedBoard != null) {
                    boolean validContinueResponse = true;
                    do {
                        Scanner continueSavedResponse = new Scanner(System.in);
                        String inputContinueSaved = continueSavedResponse.nextLine();
                        System.out.print("Response: ");
                        if (inputContinueSaved.equals("y")) {
                            playerBoard = Arrays.stream(savedBoard).map(int[]::clone).toArray(int[][]::new);
                            originalBoard = Arrays.stream(savedOriginalBoard).map(int[]::clone).toArray(int[][]::new);
                        } else if (inputContinueSaved.equals("n") == false) {
                            validContinueResponse = false;
                            System.out.println("Invalid response, please try again.");
                        }
                    } while (validContinueResponse == false);


                }
            */
                validAnswer = true;
                Scanner inputDifficulty = new Scanner(System.in);
                System.out.print("Response: ");
                String difficulty = inputDifficulty.nextLine();
                System.out.println("Program received: " + difficulty);
                System.out.println();
                if(savedBoard != null && difficulty.equalsIgnoreCase("y")){
                    playerBoard = Arrays.stream(savedBoard).map(int[]::clone).toArray(int[][]::new);
                    originalBoard = Arrays.stream(savedOriginalBoard).map(int[]::clone).toArray(int[][]::new);
                    printGrid(playerBoard);
                }
                else if(savedBoard != null && difficulty.equalsIgnoreCase("n")){
                    continueGame = false;
                    savedBoard = null;
                    savedOriginalBoard = null;
                    break;

                }
                else if(difficulty.equalsIgnoreCase("exit")){
                    exitFlag = 1;
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                else if (difficulty.equalsIgnoreCase("e")) {
                    playerBoard = hideValues(Arrays.stream(sudokuAnswers).map(int[]::clone).toArray(int[][]::new), 3);
                    originalBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
                    printGrid(playerBoard);
                } else if (difficulty.equalsIgnoreCase("m")) {
                    playerBoard = hideValues(Arrays.stream(sudokuAnswers).map(int[]::clone).toArray(int[][]::new), 4);
                    originalBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
                    printGrid(playerBoard);
                } else if (difficulty.equalsIgnoreCase("h")) {
                    playerBoard = hideValues(Arrays.stream(sudokuAnswers).map(int[]::clone).toArray(int[][]::new), 5);
                    originalBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
                    printGrid(playerBoard);
                } else {
                    System.out.println("Invalid answer. Please only enter the character in the brackets to make your choice.");
                    validAnswer = false;
                }
            } while (validAnswer == false);


            //printGrid(sudokuAnswers);

            //printGrid(sudoku);
            //System.out.println("fin");

            while (continueGame == true) {
                System.out.println("Enter the move you would like to make, 'help' for a list of commands, or 'quit' to quit this puzzle");
                System.out.print("Move: ");
                Scanner inputMove = new Scanner(System.in);
                String move = inputMove.nextLine().toUpperCase();
                if (move.equalsIgnoreCase("quit")) {
                    System.out.println("Would you like to save your game? [y]/[n]");
                    boolean validateSaveResponse = true;
                    do {
                        System.out.print("Response: ");
                        move = inputMove.nextLine();
                        validateSaveResponse = true;
                        if (move.equals("y")) {
                            savedBoard = Arrays.stream(playerBoard).map(int[]::clone).toArray(int[][]::new);
                            savedOriginalBoard = Arrays.stream(originalBoard).map(int[]::clone).toArray(int[][]::new);
                        } else if (move.equals("n")) {
                            savedBoard = null;
                            savedOriginalBoard = null;
                            System.out.println("======================================================");
                            System.out.println("GAME OVER. Would you like to start a new game? [y]/[n]");
                            System.out.println("======================================================");
                            boolean newGameResponseValid = true;
                            do{
                                System.out.print("Response: ");
                                move = inputMove.nextLine();
                                if(move.equalsIgnoreCase("y")){
                                    break;
                                }
                                else if(move.equalsIgnoreCase("n")){
                                    exitFlag = 1;
                                    System.out.println("Thank you for playing, goodbye!");
                                }
                                else{
                                    System.out.println("Invalid input. Please enter your input again.");
                                    newGameResponseValid = false;
                                }
                            }while(newGameResponseValid == false);
                        }else{
                            System.out.println("Invalid input. Please enter your input again");
                            validateSaveResponse = false;
                        }
                    } while(validateSaveResponse == false);
                    break;
                }
                else if (move.equalsIgnoreCase("help")) {
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
                }

                else if(move.equalsIgnoreCase("undo")){
                    if(undo.empty() == false){
                        saveRedoValue = true;
                        if(saveRedoValue == true) {
                            redo.push(redoRowColumn + redoValue);
                        }
                        System.out.println("Saved redo move: " + redoRowColumn + String.valueOf(redoValue));
                        move = undo.pop();
                        System.out.println("Undoing move: " + move);
                        redoRowColumn = move.substring(0, 3);
                        redoValue = Character.getNumericValue(move.charAt(3));
                        System.out.println( "Move to make: " + move);
                        moveIsUndo = true;
                        //redo.push(move);
                    }
                    else{
                        System.out.println("\nNo moves left to undo.\n");
                        continue;
                    }
                }
                else if(move.equalsIgnoreCase("redo")){
                    if(redo.empty() == false) {
                        move = redo.pop();
                        moveIsRedo = true;
                    }
                    else{
                        System.out.println("\nNo moves left to redo.\n");
                        continue;
                    }
                }
                else if(move.length() < 4){
                    System.out.println("\n--------------------------------");
                    System.out.println("ERROR: Invalid input. Try again.");
                    System.out.println("--------------------------------\n");
                    continue;
                }

                int rowNum = Character.getNumericValue(move.charAt(1)) - 1;
                int columnNum = Character.getNumericValue(move.charAt(0)) - 10;
                int inputValue = Character.getNumericValue(move.charAt(3));
                boolean rowInRange = rowNum < playerBoard.length;
                boolean columnInRange = columnNum < playerBoard.length;
                boolean inputInRange = inputValue >= 0 && inputValue <= playerBoard.length;

                if (Character.isAlphabetic(move.charAt(0)) && columnInRange &&
                        Character.isDigit(move.charAt(1)) && rowInRange &&
                        Character.isSpaceChar(move.charAt(2)) &&
                        (Character.isDigit(move.charAt(3))) && inputInRange &&
                        move.length() == 4) {
                    if (originalBoard[rowNum][columnNum] != 0) {
                        System.out.println("\n-----------------------------------------------");
                        System.out.println("ERROR: Cannot change values of pre-set squares.");
                        System.out.println("-----------------------------------------------\n");
                    } else {
                        System.out.println("\n\n" + "~~~~~~~".repeat(playerBoard.length));
                        System.out.println("\n| Played move [" + move + "]... |\n");

                        if(moveIsRedo == false && moveIsUndo == false){
                            redo.clear();
                        }
                        else{
                            moveIsRedo = false;
                        }
                        if(moveIsUndo == false) {
                            System.out.println("move saved in undo");
                            undo.push(move.substring(0, 3) + String.valueOf(playerBoard[rowNum][columnNum]));
                        } else {
                            moveIsUndo = false;
                        }

                        playerBoard[rowNum][columnNum] = inputValue;
                        printGrid(playerBoard);
                        redoRowColumn = move.substring(0, 3);
                        redoValue = inputValue;
                        System.out.println("~~~~~~~".repeat(playerBoard.length) + "\n");
                    }
                } else {
                    System.out.println("\n--------------------------------");
                    System.out.println("ERROR: Invalid input. Try again.");
                    System.out.println("--------------------------------\n");
                }
                if(emptySpace(playerBoard) == false && validBoard(playerBoard)){
                    System.out.println("===============================================================");
                    System.out.println("CONGRATULATIONS! You have successfully solved the sudoku board.");
                    System.out.println("===============================================================");
                    continueGame = false;
                    inputMove.close();
                    break;
                }
            }


            System.out.println(validBoard(playerBoard));
            System.out.println(playerBoard[0][1]);
        }
    }


    static int[][] makeGrid(int size) {

        int[][] sudokuGrid = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                sudokuGrid[row][column] = 0;
            }
        }

        return sudokuGrid;
    }

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
                    if(num == 0){
                        System.out.print("X");
                    }
                    else{
                        System.out.print(num);
                    }
                } else {
                    if(num == 0){
                        System.out.print("X" + "-");
                    }else{
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

    private static boolean validRows(int[][] grid) {
        ArrayList<Integer> allValuesInRow = new ArrayList<>();
        HashSet<Integer> uniqueValuesInRow = new HashSet<>();
        int flag = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int columnIndex = 0; columnIndex < grid.length; columnIndex++) {
                if (grid[row][columnIndex] != 0) {
                    allValuesInRow.add(grid[row][columnIndex]);
                    uniqueValuesInRow.add(grid[row][columnIndex]);

                }
            }
            //System.out.println(allValuesInRow.toString());
            if (allValuesInRow.size() != uniqueValuesInRow.size()) {
                flag = 1;
            }
            allValuesInRow.clear();
            uniqueValuesInRow.clear();
        }
        if (flag == 1) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean validColumns(int[][] grid) {
        //int[] allValuesInColumn = new int[grid.length];
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

            //System.out.println(allValuesInColumn.toString());

            if (allValuesInColumn.size() != uniqueValuesInColumn.size()) {
                //System.out.println(Arrays.toString(allValuesInColumn));
                flag = 1;
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

    private static boolean validBlocks(int[][] grid) {
        ArrayList<Integer> allValuesInBlock = new ArrayList<>();
        HashSet<Integer> uniqueValuesInBlock = new HashSet<>();
        int flag = 0;
        int blockStartColumn = 0;
        int gridSqrt = (int) Math.sqrt(grid.length);
        int blockStartRow = 0;
        int blockArrayIndex = 0;
        int blockEndRow = gridSqrt;
        int blockEndColumn = gridSqrt;
        int blocksInRow = 0;
        int previousBlocksInRow = 0;


        for (int currentRow = blockStartRow; currentRow >= blockStartRow && currentRow < blockEndRow && currentRow != grid.length; currentRow++) {
            for (int currentColumn = blockStartColumn; currentColumn >= blockStartColumn && currentColumn < blockEndColumn && currentColumn != grid.length; currentColumn++) {
                if (grid[currentRow][currentColumn] != 0) {
                    allValuesInBlock.add(grid[currentRow][currentColumn]);
                    uniqueValuesInBlock.add(grid[currentRow][currentColumn]);
                }
                blockArrayIndex++;
                //System.out.println (currentRow + "," + currentColumn);

                if (blockArrayIndex == grid.length && blockEndColumn <= grid.length) {
                    if (allValuesInBlock.size() != uniqueValuesInBlock.size()) {
                        flag = 1;
                    }
                    blockStartColumn = currentColumn + 1;
                    //System.out.println("Block start column: " + blockStartColumn);
                    blockEndColumn += gridSqrt;
                    //System.out.println("block end column: " + blockEndColumn);
                    //System.out.println("block values: " + Arrays.toString(allValuesInBlock));
                    blockArrayIndex = 0;
                    //System.out.println("block array index : " + blockArrayIndex);
                    blocksInRow++;
                    allValuesInBlock.clear();
                    uniqueValuesInBlock.clear();
                }


                if (blocksInRow > previousBlocksInRow && blocksInRow < gridSqrt) {
                    currentRow = blockStartRow;
                    previousBlocksInRow = blocksInRow;
                } else if (blocksInRow == gridSqrt) {
                    blockStartRow = currentRow + 1;
                    blockStartColumn = 0;
                    //System.out.println("currentRow: " + currentRow);
                    //System.out.println("Block start row: " + blockStartRow);
                    blockEndRow += gridSqrt;
                    blockEndColumn = gridSqrt;
                    // System.out.println("Block end row: " + blockEndRow);
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

    private static boolean validBoard(int[][] grid) {
        if (validRows(grid) == true &&
                validColumns(grid) == true &&
                validBlocks(grid) == true) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean validMove(int[][] grid, int row, int column, int number) {
        int[][] testGrid = Arrays.stream(grid).map(int[]::clone).toArray(int[][]::new);
        testGrid[row][column] = number;

        if (validBoard(testGrid) == true) {
            return true;
        } else {
            return false;
        }
    }

    private static void generateBoard(int[][] grid) {
        Random random = new Random();
        int gridSqrt = (int) Math.sqrt(grid.length);
        int columnIndex = 0;
        int offsetCounter = 0;

        for (int row = 0; row < grid.length; row++) {

            if (row % gridSqrt == 0 && row != 0) {
                offsetCounter++;
                columnIndex = 0;
            }
            int currentColumn = offsetCounter + columnIndex;

            grid[row][currentColumn] = random.nextInt(grid.length) + 1;
            //System.out.println("grid[" + row + "][" + currentColumn + "]");
            columnIndex += gridSqrt;
        }
    }


    private static int[] getBoardSeed(int[][] grid){
        int gridSqrt = (int) Math.sqrt(grid.length);
        int[] seed = new int[grid.length];
        int seedIndex = 0;
        int columnIndex = 0;
        int offsetCounter = 0;

        for (int row = 0; row < grid.length; row++) {

            if (row % gridSqrt == 0 && row != 0) {
                offsetCounter++;
                columnIndex = 0;
            }
            int currentColumn = offsetCounter + columnIndex;

            seed[seedIndex] = grid[row][currentColumn];
            seedIndex++;
            //System.out.println("grid[" + row + "][" + currentColumn + "]");
            columnIndex += gridSqrt;
        }
        return seed;
    }


    private static boolean generateBoardComplete(int[][] grid) {
        long genStart = System.currentTimeMillis();

        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid.length; column++) {
                if (grid[row][column] == 0) {

                    for (int value = 1; value < 10; value++) {
                        if (validMove(grid, row, column, value)) {
                            grid[row][column] = value;
                        }
                        if (validMove(grid, row, column, value) == true) {
                            grid[row][column] = value;
                            if (generateBoardComplete(grid)) {
                                return true;

                            }
                        } else {
                            grid[row][column] = 0;
                        }

                        if (row != grid.length - 1 && column != grid.length - 1) {
                            long checkTime = System.currentTimeMillis();
                            long genTime = checkTime - genStart;
                            if (genTime > 7000) {
                                System.out.println("Error generating board, retrying...");
                                for (int resetRow = 0; resetRow < grid.length; resetRow++) {
                                    for (int resetColumn = 0; resetColumn < grid.length; resetColumn++) {
                                        grid[resetRow][resetColumn] = 0;
                                    }
                                }
                                generateBoard(grid);
                                generateBoardComplete(grid);
                                row = 0;
                                column = 0;
                                value = 0;
                                genStart = System.currentTimeMillis();
                            }

                        }
                    }
                    return false;
                }
                //System.out.print("\033[H\033[2J");
                //System.out.flush();
                //printGrid(grid);

            }

        }
        return true;
    }

    private static int[][] generateSeed(int[] seed){
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
            //System.out.println("grid[" + row + "][" + currentColumn + "]");
            columnIndex += gridSqrt;
        }
        generateBoardComplete(grid);
        return grid;
    }

    static int[][] hideValues(int[][] grid, int amountHidden) {
        Random hideIndex = new Random();
        HashSet<Integer> removeFromBlock = new HashSet<>();
        ArrayList<Integer> allValuesInBlock = new ArrayList<>();
        HashMap<Integer, Integer> gridLinkRow = new HashMap<>();
        HashMap<Integer, Integer> gridLinkColumn = new HashMap<>();


        int blockStartColumn = 0;
        int gridSqrt = (int) Math.sqrt(grid.length);
        int blockStartRow = 0;
        int blockArrayIndex = 0;
        int blockEndRow = gridSqrt;
        int blockEndColumn = gridSqrt;
        int blocksInRow = 0;
        int previousBlocksInRow = 0;

        for (int currentRow = blockStartRow; currentRow >= blockStartRow && currentRow < blockEndRow && currentRow != grid.length; currentRow++) {
            for (int currentColumn = blockStartColumn; currentColumn >= blockStartColumn && currentColumn < blockEndColumn && currentColumn != grid.length; currentColumn++) {
                if (grid[currentRow][currentColumn] != 0) {
                    int[] rowCol = {currentRow, currentColumn};
                    allValuesInBlock.add(grid[currentRow][currentColumn]);
                    gridLinkRow.put(grid[currentRow][currentColumn], currentRow);
                    gridLinkColumn.put(grid[currentRow][currentColumn], currentColumn);
                }
                blockArrayIndex++;
                //System.out.println (currentRow + "," + currentColumn);

                if (blockArrayIndex == grid.length && blockEndColumn <= grid.length) {
                    //System.out.println("Values in block before hiding: " + allValuesInBlock.toString());

                    while (removeFromBlock.size() != amountHidden) {
                        removeFromBlock.add(hideIndex.nextInt(9));
                    }
                    for (int removeIndex : removeFromBlock) {
                        //allValuesInBlock.set(removeIndex, 0);
                        grid[gridLinkRow.get(allValuesInBlock.get(removeIndex))][gridLinkColumn.get(allValuesInBlock.get(removeIndex))] = 0;
                    }
                    //System.out.println("Values in block after hiding: " + allValuesInBlock.toString());
                    blockStartColumn = currentColumn + 1;
                    //System.out.println("Block start column: " + blockStartColumn);
                    blockEndColumn += gridSqrt;
                    //System.out.println("block end column: " + blockEndColumn);
                    //System.out.println("block values: " + Arrays.toString(allValuesInBlock));
                    blockArrayIndex = 0;
                    //System.out.println("block array index : " + blockArrayIndex);
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
                    //System.out.println("currentRow: " + currentRow);
                    //System.out.println("Block start row: " + blockStartRow);
                    blockEndRow += gridSqrt;
                    blockEndColumn = gridSqrt;
                    // System.out.println("Block end row: " + blockEndRow);
                    blocksInRow = 0;
                    previousBlocksInRow = 0;
                }
            }


        }
        return grid;

    }

    private static boolean emptySpace(int[][] grid) {
        for (int[] row : grid) {
            for (int num : row) {
                if (num == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}

