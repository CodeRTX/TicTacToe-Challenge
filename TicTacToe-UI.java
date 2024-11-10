import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class TicTacToe_UI extends JFrame {
    private static final char EMPTY = ' ';
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';
    private char[] board;
    private char currentPlayer;
    private boolean gameEnded;
    private boolean botMode;
    private JButton[] buttons;
    private JLabel statusLabel;

    public TicTacToe_UI() {
        board = new char[9];
        initializeBoard();
        currentPlayer = PLAYER_X;
        gameEnded = false;
        botMode = false;

        setTitle("Tic-Tac-Toe");
        setSize(300, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        buttons = new JButton[9];
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.BOLD, 40));
            int index = i;
            buttons[i].addActionListener(e -> humanMove(index));
            boardPanel.add(buttons[i]);
        }
        add(boardPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Human vs Human", SwingConstants.CENTER);
        add(statusLabel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> resetGame());
        controlPanel.add(newGameButton);

        JButton toggleModeButton = new JButton("Toggle Bot");
        toggleModeButton.addActionListener(e -> toggleBotMode());
        controlPanel.add(toggleModeButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void initializeBoard() {
        for (int i = 0; i < 9; i++) {
            board[i] = EMPTY;
        }
    }

    private void humanMove(int position) {
        if (!gameEnded && board[position] == EMPTY) {
            makeMove(position);
            updateButton(position);
            checkGameState();
            if (!gameEnded && botMode) {
                botMove();
            }
        }
    }

    private void botMove() {
        int bestMove = findBestMove();
        makeMove(bestMove);
        updateButton(bestMove);
        checkGameState();
    }

    private int findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (board[i] == EMPTY) {
                board[i] = PLAYER_O;
                int score = minimax(board, 0, false);
                board[i] = EMPTY;

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }

        return bestMove;
    }

    private int minimax(char[] board, int depth, boolean isMaximizing) {
        if (checkWin(PLAYER_O)) return 10;
        if (checkWin(PLAYER_X)) return -10;
        if (isBoardFull()) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == EMPTY) {
                    board[i] = PLAYER_O;
                    int score = minimax(board, depth + 1, false);
                    board[i] = EMPTY;
                    bestScore = Math.max(score, bestScore);
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == EMPTY) {
                    board[i] = PLAYER_X;
                    int score = minimax(board, depth + 1, true);
                    board[i] = EMPTY;
                    bestScore = Math.min(score, bestScore);
                }
            }
            return bestScore;
        }
    }

    private void makeMove(int position) {
        board[position] = currentPlayer;
        switchPlayer();
    }

    private void updateButton(int position) {
        buttons[position].setText(String.valueOf(board[position]));
        buttons[position].setForeground(board[position] == PLAYER_X ? Color.BLUE : Color.RED);
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
    }

    private void checkGameState() {
        if (checkWin(PLAYER_X)) {
            endGame("Player 1 wins!");
        } else if (checkWin(PLAYER_O)) {
            endGame(botMode ? "You lost!" : "Player 2 wins!");
        } else if (isBoardFull()) {
            endGame("It's a draw!");
        }
    }

    private void endGame(String message) {
        gameEnded = true;
        statusLabel.setText(message);
        JOptionPane.showMessageDialog(this, message);
    }

    private boolean checkWin(char player) {
        int[][] winCombos = {{0,1,2}, {3,4,5}, {6,7,8}, {0,3,6}, {1,4,7}, {2,5,8}, {0,4,8}, {2,4,6}};
        for (int[] combo : winCombos) {
            if (board[combo[0]] == player && board[combo[1]] == player && board[combo[2]] == player) {
                return true;
            }
        }
        return false;
    }

    private boolean isBoardFull() {
        for (char cell : board) {
            if (cell == EMPTY) {
                return false;
            }
        }
        return true;
    }

    private void resetGame() {
        initializeBoard();
        for (JButton button : buttons) {
            button.setText("");
        }
        currentPlayer = PLAYER_X;
        gameEnded = false;
        statusLabel.setText(botMode ? "Human vs Bot" : "Human vs Human");
    }

    private void toggleBotMode() {
        botMode = !botMode;
        statusLabel.setText(botMode ? "Human vs Bot" : "Human vs Human");
        resetGame();
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new TicTacToe_UI().setVisible(true));
    }
}
