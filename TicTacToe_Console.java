import java.util.*;

class TicTacToe_Console {
    private char[] board;
    private char currentPlayer;

    TicTacToe_Console() {
        this.board = new char[9];
        resetBoard();
        this.currentPlayer = 'X';
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TicTacToe_Console game = new TicTacToe_Console();

        while (true) {
            boolean playerFirst = game.promptPlayerFirst(sc);
            game.resetBoard();
            game.printBoard();

            while (true) {
                if (playerFirst) {
                    game.playerMove(sc);
                    if (game.isGameOver()) break;
                    game.printBoard();
                    game.aiMove();
                    if (game.isGameOver()) break;
                    game.printBoard();
                } else {
                    game.aiMove();
                    if (game.isGameOver()) break;
                    game.printBoard();
                    game.playerMove(sc);
                    if (game.isGameOver()) break;
                    game.printBoard();
                }
            }

            if (!game.promptPlayAgain(sc)) {
                break;
            }
        }

        sc.close();
    }

    private boolean promptPlayerFirst(Scanner scanner) {
        while (true) {
            System.out.print("Do you want to go first? (yes/no): ");
            String choice = scanner.next().toLowerCase();
            if (choice.equals("yes")) {
                return true;
            } else if (choice.equals("no")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            }
        }
    }

    private boolean promptPlayAgain(Scanner scanner) {
        while (true) {
            System.out.print("\nDo you want to play again? (yes/no): ");
            String choice = scanner.next().toLowerCase();
            if (choice.equals("yes")) {
                return true;
            } else if (choice.equals("no")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            }
        }
    }

    private void resetBoard() {
        for (int i = 0; i < 9; i++) {
            board[i] = ' ';
        }
    }

    private void printBoard() {
        System.out.println(" " + board[0] + " | " + board[1] + " | " + board[2]);
        System.out.println("---+---+---");
        System.out.println(" " + board[3] + " | " + board[4] + " | " + board[5]);
        System.out.println("---+---+---");
        System.out.println(" " + board[6] + " | " + board[7] + " | " + board[8]);
    }

    private void playerMove(Scanner scanner) {
        int move;
        while (true) {
            System.out.print("Enter your move (1-9): ");
            move = scanner.nextInt() - 1;
            if (move >= 0 && move < 9 && board[move] == ' ') {
                board[move] = currentPlayer;
                break;
            }
            System.out.println("This move is not valid");
        }
    }

    private void aiMove() {
        int bestScore = Integer.MIN_VALUE;
        int move = 0;
        for (int i = 0; i < 9; i++) {
            if (board[i] == ' ') {
                board[i] = 'O';
                int score = minimax(board, 0, false);
                board[i] = ' ';
                if (score > bestScore) {
                    bestScore = score;
                    move = i;
                }
            }
        }
        board[move] = 'O';
    }

    private boolean isGameOver() {
        if (hasPlayerWon('X')) {
            printBoard();
            System.out.println("Player X wins!");
            return true;
        }
        if (hasPlayerWon('O')) {
            printBoard();
            System.out.println("Player O wins!");
            return true;
        }
        if (isBoardFull(board)) {
            printBoard();
            System.out.println("The game is a draw!");
            return true;
        }
        return false;
    }

    private boolean hasPlayerWon(char player) {
        int[][] winPatterns = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
        };
        for (int[] pattern : winPatterns) {
            if (board[pattern[0]] == player && board[pattern[1]] == player && board[pattern[2]] == player) {
                return true;
            }
        }
        return false;
    }

    private int minimax(char[] board, int depth, boolean isMaximizing) {
        if (hasPlayerWon('O')) return 1;
        if (hasPlayerWon('X')) return -1;
        if (isBoardFull(board)) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == ' ') {
                    board[i] = 'O';
                    int score = minimax(board, depth + 1, false);
                    board[i] = ' ';
                    bestScore = Math.max(score, bestScore);
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == ' ') {
                    board[i] = 'X';
                    int score = minimax(board, depth + 1, true);
                    board[i] = ' ';
                    bestScore = Math.min(score, bestScore);
                }
            }
            return bestScore;
        }
    }

    private boolean isBoardFull(char[] board) {
        for (char c : board) {
            if (c == ' ') {
                return false;
            }
        }
        return true;
    }
}
