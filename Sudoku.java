import java.util.Arrays;

public class Sudoku {

    public static void main(String[] args) {
        int[][] sudoku = makeGrid(9);
        //  System.out.println(Arrays.toString(sudoku[2]));
        sudoku[2][5] = 1;
        printGrid(sudoku);
        //  System.out.println(validRows(sudoku));
        //System.out.println(validColumns(sudoku));
        validBlocks(sudoku);
    }


    static int[][] makeGrid(int size) {

        int[][] sudokuGrid = new int[size][size];
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                sudokuGrid[row][column] = column;
            }
        }

        return sudokuGrid;
    }

    static void printGrid(int[][] grid) {
        int rowLength = grid.length;
        for (int row = 0; row < rowLength; row++) {
            for (int num : grid[row]) {
                System.out.print(num);
            }
            System.out.println();
        }
        System.out.println();
    }

    private static boolean validRows(int[][] grid) {
        int[] allValuesInRow = new int[grid.length];
        int flag = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int num = 0; num < grid[row].length; num++) {
                allValuesInRow[num] = grid[row][num];
            }
            if (Arrays.stream(allValuesInRow).distinct().count() != allValuesInRow.length) {
                flag = 1;
                System.out.println(Arrays.toString(allValuesInRow));
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
        int flag = 0;

        for (int column = 0; column < grid.length; column++) {
            for (int rowIndex = 0; rowIndex < grid.length; rowIndex++) {
                allValuesInColumn[rowIndex] = grid[rowIndex][column];
            }

            if (Arrays.stream(allValuesInColumn).distinct().count() != allValuesInColumn.length) {
                System.out.println(Arrays.toString(allValuesInColumn));
                flag = 1;
            }
        }
        if (flag == 1) {
            return false;
        } else {
            return true;
        }
    }

    static void validBlocks(int[][] grid) {
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
                System.out.println (currentRow + "," + currentColumn);

                if(blockArrayIndex == grid.length && blockEndColumn <= grid.length){
                    if(Arrays.stream(allValuesInBlock).distinct().count() != allValuesInBlock.length)
                    {
                        flag = 1;
                    }
                    blockStartColumn = currentColumn + 1;
                    System.out.println("Block start column: " + blockStartColumn);
                    blockEndColumn += gridSqrt;
                    System.out.println("block end column: " + blockEndColumn);
                    System.out.println("block values: " + Arrays.toString(allValuesInBlock));
                    blockArrayIndex = 0;
                    System.out.println("block array index : " + blockArrayIndex);
                    blocksInRow++;
                }


                if(blocksInRow > previousBlocksInRow && blocksInRow < gridSqrt){
                    currentRow = blockStartRow;
                    previousBlocksInRow = blocksInRow;
                } else if(blocksInRow == gridSqrt){
                    blockStartRow = currentRow + 1;
                    blockStartColumn = 0;
                    System.out.println("currentRow: " + currentRow);
                    System.out.println("Block start row: " + blockStartRow);
                    blockEndRow += gridSqrt;
                    blockEndColumn = gridSqrt;
                    System.out.println("Block end row: " + blockEndRow);
                    blocksInRow = 0;
                    previousBlocksInRow = 0;
                }
            }


        }
        if(flag == 1)
        {
            
        }
    }
}


