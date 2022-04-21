import java.util.Arrays;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Random;

public class Sudoku {

    public static void main(String[] args) {
        int[][] sudoku = makeGrid(9);
        //  System.out.println(Arrays.toString(sudoku[2]));
        //sudoku[0][0] = 1;
        //sudoku[0][2] = 1;
        //printGrid(sudoku);

        //GAME PRESENTED TO USER WILL HAVE 3 VALUES REMOVED FROM EACH BLOCK
        sudoku = generateBoard(sudoku);

        System.out.println("Rows valid: " + validRows(sudoku));
        System.out.println("Columns valid: " + validColumns(sudoku));
        System.out.println("Blocks valid: " + validBlocks(sudoku));
        System.out.println("Board valid: " + validBoard(sudoku));
        System.out.println("Valid to add 1 to 2nd column in first row: " + validMove(sudoku, 0, 1, 1));

        //printGrid(sudoku);
        generateBoardComplete(sudoku);
        printGrid(sudoku);
        //System.out.println("fin");
        System.out.println(validBoard(sudoku));
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
        System.out.print("\t ");
        for(int i = 0; i < grid.length; i++){
            if(i % gridSqrt == 0 && i != 0){
                System.out.print(" ".repeat(gridSqrt));
            }
            if((i + 1) % gridSqrt ==0){
                System.out.print(alphabet.charAt(i));
            }else {
                System.out.print(alphabet.charAt(i) + " ");
            }
        }
        System.out.println();
        for (int[] row : grid) {
            if(rowNum % gridSqrt == 0 && rowNum != 0){
                System.out.println("---".repeat(row.length));
            }
            rowNum++;
            System.out.print("[" + rowNum + "]  ");

            for (int num : row) {
                if(columnNum % gridSqrt == 0 && columnNum != 0){
                    System.out.print(" | ");
                }
                if((columnNum + 1) % gridSqrt == 0){
                    System.out.print(num);
                }else{
                    System.out.print(num + "-");
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
            for(int columnIndex = 0; columnIndex < grid.length; columnIndex++)
            {
                if(grid[row][columnIndex] != 0)
                {
                    allValuesInRow.add(grid[row][columnIndex]);
                    uniqueValuesInRow.add(grid[row][columnIndex]);

                }
            }
            //System.out.println(allValuesInRow.toString());
            if(allValuesInRow.size() != uniqueValuesInRow.size()){
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
                if(grid[rowIndex][column] != 0){
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
        int previousBlocksInRow =0;


        for (int currentRow = blockStartRow; currentRow >= blockStartRow && currentRow < blockEndRow && currentRow != grid.length; currentRow++) {
            for (int currentColumn = blockStartColumn; currentColumn >= blockStartColumn && currentColumn < blockEndColumn && currentColumn != grid.length; currentColumn++) {
                if(grid[currentRow][currentColumn] != 0){
                    allValuesInBlock.add(grid[currentRow][currentColumn]);
                    uniqueValuesInBlock.add(grid[currentRow][currentColumn]);
                }
                blockArrayIndex++;
                //System.out.println (currentRow + "," + currentColumn);

                if(blockArrayIndex == grid.length && blockEndColumn <= grid.length){
                    if(allValuesInBlock.size() != uniqueValuesInBlock.size()){
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


                if(blocksInRow > previousBlocksInRow && blocksInRow < gridSqrt){
                    currentRow = blockStartRow;
                    previousBlocksInRow = blocksInRow;
                } else if(blocksInRow == gridSqrt){
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
        if(flag == 1) {
            return false;
        }
        else{
            return true;
        }
    }

    private static boolean validBoard(int[][] grid){
        if(validRows(grid) == true &&
           validColumns(grid) == true &&
           validBlocks(grid) == true){
            return true;
        }
        else{
            return false;
        }
    }

    private static boolean validMove(int[][] grid, int row, int column, int number){
        int[][] testGrid = Arrays.stream(grid).map(int[]::clone).toArray(int[][]::new);
        testGrid[row][column] = number;

        if(validBoard(testGrid) == true){
            return true;
        }
        else{
            return false;
        }
    }

    private static int[][] generateBoard(int[][] grid){
        Random random = new Random();
        int gridSqrt = (int) Math.sqrt(grid.length);
        int columnIndex = 0;
        int offsetCounter = 0;

        for(int row = 0; row < grid.length; row++){

            if(row % gridSqrt == 0 && row != 0){
                offsetCounter++;
                columnIndex = 0;
            }
            int currentColumn = offsetCounter + columnIndex;

            grid[row][currentColumn] = random.nextInt(grid.length) + 1;
            //System.out.println("grid[" + row + "][" + currentColumn + "]");
            columnIndex += gridSqrt;
        }
        return grid;

    }

    private static boolean generateBoardComplete(int[][] grid){
        long genStart = System.currentTimeMillis();

        for(int row = 0; row < grid.length; row++) {
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

                        if(row != grid.length - 1 && column != grid.length - 1){
                            long checkTime = System.currentTimeMillis();
                            long genTime = checkTime - genStart;
                            if(genTime > 7000){
                                System.out.println("Error generating board, retrying...");
                                for(int resetRow = 0; resetRow < grid.length; resetRow++){
                                    for(int resetColumn = 0; resetColumn < grid.length; resetColumn++){
                                        grid[resetRow][resetColumn] = 0;
                                    }
                                }
                                grid = generateBoard(grid);
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
}

