/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Stack;

import java.util.Arrays;
import java.util.Objects;

public class Board {
    private final int[][] board;

    public Board(int[][] tiles) {
        int boardSize = tiles.length;
        board = new int[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = tiles[i][j];
            }
        }
    }

    public String toString() {
        StringBuilder boardString = new StringBuilder();
        int dim = dimension();
        boardString.append(dim);
        boardString.append("\n");
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                boardString.append(" ");
                boardString.append(board[i][j]);
            }
            boardString.append("\n");
        }
        return boardString.toString();
    }

    public int dimension() {
        return board.length;
    }

    public int hamming() {
        int dim = dimension();
        int outOfPlaceCount = 0;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                int tileNum = board[i][j];
                if (tileNum != 0 && !(i == (tileNum - 1) / dim && j == (tileNum - 1) % dim)) {
                    outOfPlaceCount++;
                }
            }
        }
        return outOfPlaceCount;
    }

    public int manhattan() {
        int dim = dimension();
        int distance = 0;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                int tileNum = board[i][j];
                if (tileNum == 0) continue;
                int correctRow = (tileNum - 1) / dim;
                int correctCol = (tileNum - 1) % dim;
                distance += (Math.abs(correctCol - j) + Math.abs(correctRow - i));
            }
        }
        return distance;
    }

    public boolean isGoal() {
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                int tileNum = board[i][j];
                if (tileNum != 0 && !(i == (tileNum - 1) / dimension()
                        && j == (tileNum - 1) % dimension())) return false;
            }
        }
        return true;
    }

    public boolean equals(Object y) {
        if (this == y) return true;
        if (y == null) return false;
        if (getClass() != y.getClass()) return false;
        Board otherBoard = (Board) y;
        return Objects.equals(dimension(), otherBoard.dimension()) &&
                Arrays.deepEquals(this.board, otherBoard.board);
    }

    public Iterable<Board> neighbors() {
        Stack<Board> neighborBoards = new Stack<>();
        int[] zeroIdx = findZeroTile();
        int[][] boardCopy;
        if (zeroIdx[0] - 1 >= 0) {
            boardCopy = boardCopy();
            exch(boardCopy, zeroIdx[0], zeroIdx[1], zeroIdx[0] - 1, zeroIdx[1]);
            neighborBoards.push(new Board(boardCopy));
        }
        if (zeroIdx[0] + 1 < dimension()) {
            boardCopy = boardCopy();
            exch(boardCopy, zeroIdx[0], zeroIdx[1], zeroIdx[0] + 1, zeroIdx[1]);
            neighborBoards.push(new Board(boardCopy));
        }
        if (zeroIdx[1] - 1 >= 0) {
            boardCopy = boardCopy();
            exch(boardCopy, zeroIdx[0], zeroIdx[1], zeroIdx[0], zeroIdx[1] - 1);
            neighborBoards.push(new Board(boardCopy));
        }
        if (zeroIdx[1] + 1 < dimension()) {
            boardCopy = boardCopy();
            exch(boardCopy, zeroIdx[0], zeroIdx[1], zeroIdx[0], zeroIdx[1] + 1);
            neighborBoards.push(new Board(boardCopy));
        }

        return neighborBoards;

    }

    private int[][] boardCopy() {
        int[][] boardcopy = new int[dimension()][];
        for (int i = 0; i < dimension(); i++) {
            boardcopy[i] = new int[dimension()];
            System.arraycopy(board[i], 0, boardcopy[i], 0, dimension());
        }
        return boardcopy;
    }

    private int[] findZeroTile() {
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (board[i][j] == 0) return new int[] { i, j };
            }
        }
        return null;
    }

    private void exch(int[][] tiles, int i, int j, int r, int c) {
        int temp = tiles[i][j];
        tiles[i][j] = tiles[r][c];
        tiles[r][c] = temp;
    }

    public Board twin() {
        int[][] twinCopy = boardCopy();
        boolean done = false;
        for (int i = 0; i < twinCopy.length; i++) {
            for (int j = 0; j < twinCopy[0].length - 1; j++) {
                if (twinCopy[i][j] != 0 & twinCopy[i][j + 1] != 0) {
                    exch(twinCopy, i, j, i, j + 1);
                    done = true;
                    break;
                }
            }
            if (done) break;
        }
        return new Board(twinCopy);
    }

    public static void main(String[] args) {
        /*
        int[][] tiles1 = {
                { 2, 0, 8 },
                { 1, 4, 6 },
                { 7, 3, 5 }
        };
        int[][] tiles2 = {
                { 5, 2, 3 },
                { 4, 7, 1 },
                { 0, 8, 6 }
        };
        int[][] goalTiles = {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 7, 8, 0 }
        };
        Board b1 = new Board(tiles1);
        Board b2 = new Board(tiles2);
        Board goal = new Board(goalTiles);

        StdOut.println(b1.toString());
        StdOut.println(b2.toString());

        StdOut.println("board 1 neighbors");
        for (Board board : b1.neighbors()) {
            StdOut.println(board);
        }
        StdOut.println("board 2 neighbors");
        for (Board board : b2.neighbors()) {
            StdOut.println(board);
        }

        StdOut.println("board 1 twin");
        StdOut.println(b1.twin());
        StdOut.println("board 2 twin");
        StdOut.println(b2.twin());

        StdOut.println("Should say true: " + goal.isGoal());
        StdOut.println("board1 == board2: " + b1.equals(b2));
        Board b1prime = new Board(tiles1);
        StdOut.println("board1 == board1prime: " + b1prime.equals(b1));

        StdOut.println("board1 hamming: " + b1.hamming());
        StdOut.println("board1 manhattan: " + b1.manhattan());
        StdOut.println("board2 hamming: " + b2.hamming());
        StdOut.println("board2 manhattan: " + b2.manhattan());
        StdOut.println("goal hamming: " + goal.hamming());
        StdOut.println("goal manhattan: " + goal.manhattan());
        */
    }
}
