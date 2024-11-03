import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Random;

public class TicTacToe extends JFrame {
    private static final char EMPTY = ' ';
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';
    private char[] board;
    private char currentPlayer;
    private boolean gameEnded;
    private boolean botMode;
    private int difficulty; // 0: Easy, 1: Medium, 2: Hard
    private int playerXScore;
    private int playerOScore;

    private JButton[] buttons;
    private JLabel statusLabel;
    private JLabel scoreLabel;

    public TicTacToe() {
        board = new char[9];
        initializeBoard();
        currentPlayer = PLAYER_X;
        gameEnded = false;
        botMode = false;
        difficulty = 2;
        playerXScore = 0;
        playerOScore = 0;

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

        statusLabel = new JLabel("Player X's turn", SwingConstants.CENTER);
        add(statusLabel, BorderLayout.NORTH);

        scoreLabel = new JLabel("X: 0 | O: 0", SwingConstants.CENTER);
        add(scoreLabel, BorderLayout.SOUTH);

        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> resetGame());
        controlPanel.add(newGameButton);

        JButton toggleModeButton = new JButton("Toggle Bot");
        toggleModeButton.addActionListener(e -> toggleBotMode());
        controlPanel.add(toggleModeButton);

        JButton changeDifficultyButton = new JButton("Change Difficulty");
        changeDifficultyButton.addActionListener(e -> changeDifficulty());
        controlPanel.add(changeDifficultyButton);

        JButton saveGameButton = new JButton("Save Game");
        saveGameButton.addActionListener(e -> saveGame());
        controlPanel.add(saveGameButton);

        JButton loadGameButton = new JButton("Load Game");
        loadGameButton.addActionListener(e -> loadGame());
        controlPanel.add(loadGameButton);

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
        int bestMove;
        switch (difficulty) {
            case 0: // Easy
                bestMove = findRandomMove();
                break;
            case 1: // Medium
                bestMove = new Random().nextBoolean() ? findBestMove(3) : findRandomMove();
                break;
            default: // Hard
                bestMove = findBestMove(Integer.MAX_VALUE);
                break;
        }
        makeMove(bestMove);
        updateButton(bestMove);
        checkGameState();
    }

    private int findRandomMove() {
        java.util.List<Integer> availableMoves = new java.util.ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (board[i] == EMPTY) {
                availableMoves.add(i);
            }
        }
        return availableMoves.get(new Random().nextInt(availableMoves.size()));
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
        statusLabel.setText("Player " + currentPlayer + "'s turn");
    }

    private void checkGameState() {
        if (checkWin(PLAYER_X)) {
            endGame("Player X wins!");
            playerXScore++;
        } else if (checkWin(PLAYER_O)) {
            endGame("Player O wins!");
            playerOScore++;
        } else if (isBoardFull()) {
            endGame("It's a draw!");
        }
        updateScoreLabel();
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

    private int findBestMove(int depth) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (board[i] == EMPTY) {
                board[i] = PLAYER_O;
                int score = minimax(board, depth, false);
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
        if (isBoardFull() || depth == 0) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == EMPTY) {
                    board[i] = PLAYER_O;
                    int score = minimax(board, depth - 1, false);
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
                    int score = minimax(board, depth - 1, true);
                    board[i] = EMPTY;
                    bestScore = Math.min(score, bestScore);
                }
            }
            return bestScore;
        }
    }

    private void resetGame() {
        initializeBoard();
        for (JButton button : buttons) {
            button.setText("");
        }
        currentPlayer = PLAYER_X;
        gameEnded = false;
        statusLabel.setText("Player X's turn");
    }

    private void toggleBotMode() {
        botMode = !botMode;
        JOptionPane.showMessageDialog(this, botMode ? "Bot mode activated" : "Human vs Human mode activated");
        resetGame();
    }

    private void changeDifficulty() {
        String[] options = {"Easy", "Medium", "Hard"};
        int choice = JOptionPane.showOptionDialog(this, "Select difficulty", "Difficulty",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[difficulty]);
        if (choice != JOptionPane.CLOSED_OPTION) {
            difficulty = choice;
            JOptionPane.showMessageDialog(this, "Difficulty set to " + options[difficulty]);
        }
    }

    private void updateScoreLabel() {
        scoreLabel.setText("X: " + playerXScore + " | O: " + playerOScore);
    }

    private void saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tictactoe.save"))) {
            oos.writeObject(board);
            oos.writeChar(currentPlayer);
            oos.writeBoolean(botMode);
            oos.writeInt(difficulty);
            oos.writeInt(playerXScore);
            oos.writeInt(playerOScore);
            JOptionPane.showMessageDialog(this, "Game saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tictactoe.save"))) {
            board = (char[]) ois.readObject();
            currentPlayer = ois.readChar();
            botMode = ois.readBoolean();
            difficulty = ois.readInt();
            playerXScore = ois.readInt();
            playerOScore = ois.readInt();

            for (int i = 0; i < 9; i++) {
                buttons[i].setText(board[i] == EMPTY ? "" : String.valueOf(board[i]));
                buttons[i].setForeground(board[i] == PLAYER_X ? Color.BLUE : Color.RED);
            }

            statusLabel.setText("Player " + currentPlayer + "'s turn");
            updateScoreLabel();
            JOptionPane.showMessageDialog(this, "Game loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToe().setVisible(true));
    }
}