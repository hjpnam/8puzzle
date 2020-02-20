/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    //private final Queue<Board> solutionQ = new Queue<>();
    private boolean solvable = true;
    private SearchNode solutionNode;

    private class SearchNode implements Comparable<SearchNode> {
        public Board board;
        public int moveCount;
        public SearchNode prev;
        private int priority = -1;

        public SearchNode(Board board, int moveCount, SearchNode prev) {
            this.board = board;
            this.moveCount = moveCount;
            this.prev = prev;
            this.priority = priority();
        }

        public int priority() {
            if (priority == -1) priority = moveCount + board.manhattan();
            return priority;
        }

        public int compareTo(SearchNode node) {
            return this.priority() - node.priority();
        }
    }

    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("Initial board is empty");
        MinPQ<SearchNode> pq = new MinPQ<>();
        MinPQ<SearchNode> twinpq = new MinPQ<>();
        SearchNode deqdNode;
        SearchNode twindeqdNode;
        SearchNode initialNode = new SearchNode(initial, 0, null);
        SearchNode twininitialNode = new SearchNode(initial.twin(), 0, null);
        pq.insert(initialNode);
        twinpq.insert(twininitialNode);

        do {
            deqdNode = pq.delMin();
            twindeqdNode = twinpq.delMin();
            solutionNode = deqdNode;
            if (twindeqdNode.board.isGoal()) {
                solvable = false;
                break;
            }
            for (Board board : deqdNode.board.neighbors()) {
                if (deqdNode.prev == null || !deqdNode.prev.board.equals(board))
                    pq.insert(new SearchNode(board, deqdNode.moveCount + 1, deqdNode));
            }
            for (Board twinboard : twindeqdNode.board.neighbors()) {
                if (twindeqdNode.prev == null || !twindeqdNode.prev.board.equals(twinboard))
                    twinpq.insert(
                            new SearchNode(twinboard, twindeqdNode.moveCount + 1, twindeqdNode));
            }
        } while (!pq.isEmpty() && !deqdNode.board.isGoal());
    }

    public boolean isSolvable() {
        return solvable;
    }

    public int moves() {
        if (solvable) {
            return solutionNode.moveCount;
        }
        return -1;
    }

    public Iterable<Board> solution() {
        if (solvable) {
            Stack<Board> solutionStack = new Stack<Board>();
            SearchNode curr = solutionNode;
            while (curr != null) {
                solutionStack.push(curr.board);
                curr = curr.prev;
            }
            return solutionStack;
        }
        return null;
    }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output

        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
