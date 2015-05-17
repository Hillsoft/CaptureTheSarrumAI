package BaseAQAGame;

import static java.lang.Math.*;
import AI.AI;

public class Main {

    public static final int BOARD_DIMENSION = 8;
    AQAConsole2015 console = new AQAConsole2015();

    public Main() {
        String[][] board = new String[BOARD_DIMENSION + 1][BOARD_DIMENSION + 1];
        boolean gameOver;
        int startSquare = 0;
        int finishSquare = 0;
        int startRank = 0;
        int startFile = 0;
        int finishRank = 0;
        int finishFile = 0;
        boolean moveIsLegal;
        char playAgain;
        char sampleGame;
        char whoseTurn;
        Position startPosition = new Position();
        Position finishPosition = new Position();

        playAgain = 'Y';
        do {
            whoseTurn = 'W';
            gameOver = false;
            console.print("Do you want to play the sample game (enter Y for Yes)? ");
            sampleGame = console.readChar();
            if ((int) sampleGame >= 97 && (int) sampleGame <= 122) {
                sampleGame = (char) ((int) sampleGame - 32);
            }
            initialiseBoard(board, sampleGame);
            
            AI aiPlayer = new AI(board, 'B');
            
            do {
                displayBoard(board);
                displayWhoseTurnItIs(whoseTurn);
                moveIsLegal = false;
                do {
                    if (whoseTurn == 'B')
                        aiPlayer.makeMove(startPosition, finishPosition);
                    else
                        getMove(startPosition, finishPosition);
                    startSquare = startPosition.coordinates;
                    finishSquare = finishPosition.coordinates;
                    startRank = startSquare % 10;
                    startFile = startSquare / 10;
                    finishRank = finishSquare % 10;
                    finishFile = finishSquare / 10;
                    moveIsLegal = checkMoveIsLegal(board, startRank, startFile, finishRank, finishFile, whoseTurn);
                    if (!moveIsLegal) {
                        System.err.println("That is not a legal move - please try again");
                    }
                } while (!moveIsLegal);
                gameOver = checkIfGameWillBeWon(board, finishRank, finishFile);
                makeMove(board, startRank, startFile, finishRank, finishFile, whoseTurn);
                if (whoseTurn == 'W')
                    aiPlayer.opponentMove(startRank, startFile, finishRank, finishFile);
                if (gameOver) {
                    displayWinner(whoseTurn);
                }
                if (whoseTurn == 'W') {
                    whoseTurn = 'B';
                } else {
                    whoseTurn = 'W';
                }
            } while (!gameOver);
            console.print("Do you want to play again (enter Y for Yes)? ");
            playAgain = console.readChar();
            if ((int) playAgain > 97 && (int) playAgain <= 122) {
                playAgain = (char) ((int) playAgain - 32);
            }
        } while (playAgain == 'Y');
    }
    
    int showMenu()
    {
        console.println("MENU\n----");
        console.println("1. Play sample game");
        console.println("2. Play single player game");
        console.println("3. Play two player game");
        console.println("4. Save game");
        console.println("5. Load game");
        console.println("9. Quit");
        
        return console.readInteger("");
    }
    
    void playGame(boolean sampleGame)
    {
        
    }

    void displayWhoseTurnItIs(char whoseTurn) {
        if (whoseTurn == 'W') {
            console.println("It is White's turn");
        } else {
            console.println("It is Black's turn");
        }
    }

    char gettypeOfGame() {
        char typeOfGame;
        console.print("Do you want to play the sample game (enter Y for Yes)? ");
        typeOfGame = console.readChar();
        return typeOfGame;
    }

    void displayWinner(char whoseTurn) {
        if (whoseTurn == 'W') {
            console.println("Black's Sarrum has been captured.  White wins!");
        } else {
            console.println("White's Sarrum has been captured.  Black wins!");
        }
        console.println();
    }

    boolean checkIfGameWillBeWon(String[][] board, int finishRank, int finishFile) {
        boolean gameWon;
        if (board[finishRank][finishFile].charAt(1) == 'S') {
            gameWon = true;
        } else {
            gameWon = false;
        }
        return gameWon;
    }

    public static void displayBoard(String[][] board) {
        int rankNo;
        int fileNo;
        System.out.println();
        for (rankNo = 1; rankNo <= BOARD_DIMENSION; rankNo++) {
            System.out.println("    _________________________");
            System.out.print(rankNo + "   ");
            for (fileNo = 1; fileNo <= BOARD_DIMENSION; fileNo++) {
                System.out.print("|" + board[rankNo][fileNo]);
            }
            System.out.println("|");
        }
        System.out.println("    _________________________");
        System.out.println();
        System.out.println("     1  2  3  4  5  6  7  8");
        System.out.println();
        System.out.println();
    }
    
    public static boolean isPositionValid(int rank, int file)
    {
        if (rank < 1 || rank > 8 ||
                file < 1 || file > 8)
            return false;
        
        return true;
    }

    static boolean checkRedumMoveIsLegal(String[][] board, int startRank, int startFile, int finishRank, int finishFile, char colourOfPiece) {
        boolean redumMoveIsLegal = false;
        if (colourOfPiece == 'W') {
            if (finishRank == startRank - 1) {
                if ((finishFile == startFile) && (board[finishRank][finishFile].equals("  "))) {
                    redumMoveIsLegal = true;
                } else {
                    if ((abs(finishFile - startFile) == 1) && (board[finishRank][finishFile].charAt(0) == 'B')) {
                        redumMoveIsLegal = true;
                    }
                }
            }
        } else {
            if (finishRank == startRank + 1) {
                if ((finishFile == startFile) && (board[finishRank][finishFile].equals("  "))) {
                    redumMoveIsLegal = true;
                } else {
                    if ((abs(finishFile - startFile) == 1) && (board[finishRank][finishFile].charAt(0) == 'W')) {
                        redumMoveIsLegal = true;
                    }
                }
            }
        }
        return redumMoveIsLegal;
    }

    static boolean checkSarrumMoveIsLegal(String[][] board, int startRank, int startFile, int finishRank, int finishFile) {
        boolean sarrumMoveIsLegal = false;
        if ((abs(finishFile - startFile) <= 1) && (abs(finishRank - startRank) <= 1)) {
            sarrumMoveIsLegal = true;
        }
        return sarrumMoveIsLegal;
    }

    static boolean checkGisgigirMoveIsLegal(String[][] board, int startRank, int startFile, int finishRank, int finishFile) {
        boolean gisgigirMoveIsLegal;
        int count;
        int rankDifference;
        int fileDifference;
        gisgigirMoveIsLegal = false;

        rankDifference = finishRank - startRank;
        fileDifference = finishFile - startFile;
        if (rankDifference == 0) {
            if (fileDifference >= 1) {
                gisgigirMoveIsLegal = true;
                for (count = 1; count <= fileDifference - 1; count++) {
                    if (!board[startRank][startFile + count].equals("  ")) {
                        gisgigirMoveIsLegal = false;
                    }
                }
            } else {
                if (fileDifference <= -1) {
                    gisgigirMoveIsLegal = true;
                    for (count = -1; count >= fileDifference + 1; count--) {
                        if (!board[startRank][startFile + count].equals("  ")) {
                            gisgigirMoveIsLegal = false;
                        }
                    }
                }
            }
        } else {
            if (fileDifference == 0) {
                if (rankDifference >= 1) {
                    gisgigirMoveIsLegal = true;
                    for (count = 1; count <= rankDifference - 1; count++) {
                        if (!board[startRank + count][startFile].equals("  ")) {
                            gisgigirMoveIsLegal = false;
                        }
                    }
                } else {
                    if (rankDifference <= -1) {
                        gisgigirMoveIsLegal = true;
                        for (count = -1; count >= rankDifference + 1; count--) {
                            if (!board[startRank + count][startFile].equals("  ")) {
                                gisgigirMoveIsLegal = false;
                            }
                        }
                    }
                }
            }
        }

        return gisgigirMoveIsLegal;
    }

    static boolean checkNabuMoveIsLegal(String[][] board, int startRank, int startFile, int finishRank, int finishFile) {
        boolean nabuMoveIsLegal = false;
        if ((abs(finishFile - startFile) == 1) && (abs(finishRank - startRank) == 1)) {
            nabuMoveIsLegal = true;
        }
        return nabuMoveIsLegal;
    }

    static boolean checkMarzazPaniMoveIsLegal(String[][] board, int startRank, int startFile, int finishRank, int finishFile) {
        boolean marzazPaniMoveIsLegal = false;
        if ((abs(finishFile - startFile) == 1 && abs(finishRank - startRank) == 0) || (abs(finishFile - startFile) == 0 && (abs(finishRank - startRank)) == 1)) {
            marzazPaniMoveIsLegal = true;
        }
        return marzazPaniMoveIsLegal;
    }

    static boolean checkEtluMoveIsLegal(String[][] board, int startRank, int startFile, int finishRank, int finishFile) {
        boolean etluMoveIsLegal = false;
        if ((abs(finishFile - startFile) == 2 && abs(finishRank - startRank) == 0) || (abs(finishFile - startFile) == 0 && abs(finishRank - startRank) == 2)) {
            etluMoveIsLegal = true;
        }
        return etluMoveIsLegal;
    }

    public static boolean checkMoveIsLegal(String[][] board, int startRank, int startFile, int finishRank, int finishFile, char whoseTurn) {
        char pieceType;
        char pieceColour;
        boolean moveIsLegal = true;
        if (!isPositionValid(startRank, startFile) ||
                !isPositionValid(finishRank, finishFile))
            return false;
        if ((finishFile == startFile) && (finishRank == startRank)) {
            moveIsLegal = false;
        }
        pieceType = board[startRank][startFile].charAt(1);
        pieceColour = board[startRank][startFile].charAt(0);
        if (whoseTurn == 'W') {
            if (pieceColour != 'W') {
                moveIsLegal = false;
            }
            if (board[finishRank][finishFile].charAt(0) == 'W') {
                moveIsLegal = false;
            }
        } else {
            if (pieceColour != 'B') {
                moveIsLegal = false;
            }
            if (board[finishRank][finishFile].charAt(0) == 'B') {
                moveIsLegal = false;
            }
        }
        if (moveIsLegal) {
            switch (pieceType) {
                case 'R':
                    moveIsLegal = checkRedumMoveIsLegal(board, startRank, startFile, finishRank, finishFile, pieceColour);
                    break;
                case 'S':
                    moveIsLegal = checkSarrumMoveIsLegal(board, startRank, startFile, finishRank, finishFile);
                    break;
                case 'M':
                    moveIsLegal = checkMarzazPaniMoveIsLegal(board, startRank, startFile, finishRank, finishFile);
                    break;
                case 'G':
                    moveIsLegal = checkGisgigirMoveIsLegal(board, startRank, startFile, finishRank, finishFile);
                    break;
                case 'N':
                    moveIsLegal = checkNabuMoveIsLegal(board, startRank, startFile, finishRank, finishFile);
                    break;
                case 'E':
                    moveIsLegal = checkEtluMoveIsLegal(board, startRank, startFile, finishRank, finishFile);
                    break;
                default:
                    moveIsLegal = false;
                    break;
            }
        }
        return moveIsLegal;
    }

    void initialiseBoard(String[][] board, char sampleGame) {
        int rankNo;
        int fileNo;
        if (sampleGame == 'Y') {
            for (rankNo = 1; rankNo <= BOARD_DIMENSION; rankNo++) {
                for (fileNo = 1; fileNo <= BOARD_DIMENSION; fileNo++) {
                    board[rankNo][fileNo] = "  ";
                }
            }
            board[1][2] = "BG";
            board[1][4] = "BS";
            board[1][8] = "WG";
            board[2][1] = "WR";
            board[3][1] = "WS";
            board[3][2] = "BE";
            board[3][8] = "BE";
            board[6][8] = "BR";
        } else {
            for (rankNo = 1; rankNo <= BOARD_DIMENSION; rankNo++) {
                for (fileNo = 1; fileNo <= BOARD_DIMENSION; fileNo++) {
                    if (rankNo == 2) {
                        board[rankNo][fileNo] = "BR";
                    } else {
                        if (rankNo == 7) {
                            board[rankNo][fileNo] = "WR";
                        } else {
                            if ((rankNo == 1) || (rankNo == 8)) {
                                if (rankNo == 1) {
                                    board[rankNo][fileNo] = "B";
                                }
                                if (rankNo == 8) {
                                    board[rankNo][fileNo] = "W";
                                }
                                switch (fileNo) {
                                    case 1:
                                    case 8:
                                        board[rankNo][fileNo] = board[rankNo][fileNo] + "G";
                                        break;
                                    case 2:
                                    case 7:
                                        board[rankNo][fileNo] = board[rankNo][fileNo] + "E";
                                        break;
                                    case 3:
                                    case 6:
                                        board[rankNo][fileNo] = board[rankNo][fileNo] + "N";
                                        break;
                                    case 4:
                                        board[rankNo][fileNo] = board[rankNo][fileNo] + "M";
                                        break;
                                    case 5:
                                        board[rankNo][fileNo] = board[rankNo][fileNo] + "S";
                                        break;
                                }
                            } else {
                                board[rankNo][fileNo] = "  ";
                            }
                        }
                    }
                }
            }
        }
    }

    void getMove(Position startPosition, Position finishPosition) {
        startPosition.coordinates = console.readInteger("Enter cooordinates of square containing piece to move (file first): ");
        finishPosition.coordinates = console.readInteger("Enter cooordinates of square to move piece to (file first): ");
    }

    public static void makeMove(String[][] board, int startRank, int startFile, int finishRank, int finishFile, char whoseTurn) {
        if ((whoseTurn == 'W') && (finishRank == 1) && (board[startRank][startFile].charAt(1) == 'R')) {
            board[finishRank][finishFile] = "WM";
            board[startRank][startFile] = "  ";
        } else {
            if ((whoseTurn == 'B') && (finishRank == 8) && (board[startRank][startFile].charAt(1) == 'R')) {
                board[finishRank][finishFile] = "BM";
                board[startRank][startFile] = "  ";
            } else {
                board[finishRank][finishFile] = board[startRank][startFile];
                board[startRank][startFile] = "  ";
            }
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}