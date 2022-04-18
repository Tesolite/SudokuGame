import java.util.Arrays;
import java.util.Random;

public class Sudoku {

    public static void main(String[] args) {
        int[][] sudoku = makeGrid(9);
        //  System.out.println(Arrays.toString(sudoku[2]));
        sudoku[2][5] = 1;
        printGrid(sudoku);

        System.out.println(validRows(sudoku));
        System.out.println(validColumns(sudoku));
        System.out.println(validBlocks(sudoku));
        System.out.println(validBoard(sudoku));

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
        int flag = 0;

        for (int[] row : grid) {
            if (Arrays.stream(row).distinct().count() != row.length) {
                flag = 1;
            }

        }
        if (flag == 1) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean validColumns(int[][] grid) {
        int[] allValuesInColumn = new int[grid.length];
        int valueCounter = 0;
        int flag = 0;

        for (int column = 0; column < grid.length; column++) {
            for (int rowIndex = 0; rowIndex < grid.length; rowIndex++) {
                allValuesInColumn[rowIndex] = grid[rowIndex][column];
                if(grid[rowIndex][column] != 0){
                    valueCounter++;
                }
            }
            System.out.println(Arrays.toString(allValuesInColumn) + ": " + "Values:" + valueCounter + " Distinct:" + Arrays.stream(allValuesInColumn).distinct().count() + "," + "Total:" + allValuesInColumn.length);

            if (Arrays.stream(allValuesInColumn).distinct().count() != allValuesInColumn.length) {
                //System.out.println(Arrays.toString(allValuesInColumn));
                flag = 1;
            }
        }
        if (flag == 1) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean validBlocks(int[][] grid) {
        int[] allValuesInBlock = new int[grid.length];
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

                allValuesInBlock[blockArrayIndex] = grid[currentRow][currentColumn];
                blockArrayIndex++;
                //System.out.println (currentRow + "," + currentColumn);

                if(blockArrayIndex == grid.length && blockEndColumn <= grid.length){
                    if(Arrays.stream(allValuesInBlock).distinct().count() != allValuesInBlock.length)
                    {
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

        grid[0][0] = random.nextInt(9) + 1;
        grid[1][4] = random.nextInt(9) + 1;
        grid[2][8] = random.nextInt(9) + 1;
        grid[3][1] = random.nextInt(9) + 1;
        grid[4][5] = random.nextInt(9) + 1;
        grid[5][6] = random.nextInt(9) + 1;
        grid[6][2] = random.nextInt(9) + 1;
        grid[7][3] = random.nextInt(9) + 1;
        grid[8][7] = random.nextInt(9) + 1;

        return grid;

    }
}


