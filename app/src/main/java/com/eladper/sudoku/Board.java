package com.eladper.sudoku;
import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Serializable {

    private int[][] gameCells = new int[9][9];

    public Board() {

    }

    public void setValue(int row, int column, int value) {
        gameCells[row][column] = value;
    }

    public int[][] getGameCells() {
        return gameCells;
    }

    public void copyValues(int[][] newGameCells) {
        for (int i = 0; i < newGameCells.length; i++) {
            for (int j = 0; j < newGameCells[i].length; j++) {
                gameCells[i][j] = newGameCells[i][j];
            }
        }
    }

    public boolean isBoardFull() {
        for (int i = 0; i < gameCells.length; i++) {
            for (int j = 0; j < gameCells[i].length; j++) {
                if (gameCells[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isBoardCorrect() {
        // Check horizontal
        for (int i = 0; i < gameCells.length; i++) {
            ArrayList<Integer> numbers = new ArrayList<>();
            for (int j = 0; j < gameCells[i].length; j++) {
                int number = gameCells[i][j];
                if (numbers.contains(number)) {
                    return false;
                } else {
                    numbers.add(number);
                }
            }
        }

        // Check vertical
        for (int i = 0; i < gameCells.length; i++) {
            ArrayList<Integer> numbers = new ArrayList<>();
            for (int j = 0; j < gameCells[i].length; j++) {
                int number = gameCells[j][i];
                if (numbers.contains(number)) {
                    return false;
                } else {
                    numbers.add(number);
                }
            }
        }

        // Check each group is in CellGroupFragment class for easier code
        // returns true if horizontal and vertical lines are correct
        return true;
    }

    //2 Methods To Resolve Sudoku Board

    public boolean isSafe(int[][] board, int row, int col, int num) {
        // row has the unique (row-clash)
        for (int d = 0; d < board.length; d++)
        {
            // if the number we are trying to
            // place is already present in
            // that row, return false;
            if (board[row][d] == num)
            {
                return false;
            }
        }

        // column has the unique numbers (column-clash)
        for (int r = 0; r < board.length; r++)
        {
            // if the number we are trying to
            // place is already present in
            // that column, return false;

            if (board[r][col] == num)
            {
                return false;
            }
        }

        // corresponding square has
        // unique number (box-clash)
        int sqrt = (int) Math.sqrt(board.length);
        int boxRowStart = row - row % sqrt;
        int boxColStart = col - col % sqrt;

        for (int r = boxRowStart;
             r < boxRowStart + sqrt; r++)
        {
            for (int d = boxColStart;
                 d < boxColStart + sqrt; d++)
            {
                if (board[r][d] == num)
                {
                    return false;
                }
            }
        }

        // if there is no clash, it's safe
        return true;
    }

    public boolean solveSudoku(int[][] board, int length){
        int row = -1;
        int col = -1;
        boolean isEmpty = true;
        for (int i = 0; i < length; i++)
        {
            for (int j = 0; j < length; j++)
            {
                if (board[i][j] == 0)
                {
                    row = i;
                    col = j;

                    // we still have some remaining
                    // missing values in Sudoku
                    isEmpty = false;
                    break;
                }
            }
            if (!isEmpty)
            {
                break;
            }
        }

        // no empty space left
        if (isEmpty)
        {
            return true;
        }

        // else for each-row backtrack
        for (int num = 1; num <= length; num++)
        {
            if (isSafe(board, row, col, num))
            {
                board[row][col] = num;
                if (solveSudoku(board, length))
                {
                    // print(board, n);
                    return true;
                }
                else
                {
                    board[row][col] = 0; // replace it
                }
            }
        }
        return false;
    }



    public int getValue(int row, int column) {
        return gameCells[row][column];
    }

    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < gameCells.length; i++) {
            for (int j = 0; j < gameCells[i].length; j++) {
                if (j == 0) {
                    temp.append("\n");
                }

                int currentNumber = gameCells[i][j];
                if (currentNumber == 0) {
                    temp.append("-");
                } else {
                    temp.append(currentNumber);
                }

                if (j != (gameCells[i].length-1)) {
                    temp.append(" ");
                }
            }
        }
        return temp.toString();
    }


}
