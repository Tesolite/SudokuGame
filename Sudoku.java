import java.util.Arrays;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Random;

public class Sudoku {

    public static void main(String[] args) {
        int[][] sudoku = makeGrid(9);
        //  System.out.println(Arrays.toString(sudoku[2]));
        //sudoku[0][1] = 1;
        //sudoku[0][2] = 1;
        printGrid(sudoku);
        sudoku = generateBoard(sudoku);

        System.out.println("Rows valid: " + validRows(sudoku));
        System.out.println("Columns valid: " + validColumns(sudoku));
        System.out.println("Blocks valid: " + validBlocks(sudoku));
        System.out.println("Board valid: " + validBoard(sudoku));

        printGrid(generateBoard(sudoku));
    }


    static int[][] makeGrid(int size) {

        int[][] sudokuGrid = new int[size][size];
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                sudokuGrid[row][column] = 0;
            }
        }

        return sudokuGrid;
    }

    static void printGrid(int[][] grid) {
        for (int[] row : grid) {
            for (int num : row) {
                System.out.print(num);
            }
            System.out.println();
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
/*
        grid[0][0] = random.nextInt(9) + 1;
        grid[1][3] = random.nextInt(9) + 1;
        grid[2][6] = random.nextInt(9) + 1;
        grid[3][1] = random.nextInt(9) + 1;
        grid[4][4] = random.nextInt(9) + 1;
        grid[5][7] = random.nextInt(9) + 1;
        grid[6][2] = random.nextInt(9) + 1;
        grid[7][5] = random.nextInt(9) + 1;
        grid[8][8] = random.nextInt(9) + 1;


 */
        return grid;

    }
}


