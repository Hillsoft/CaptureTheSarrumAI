package AI;

import BaseAQAGame.Main;
import BaseAQAGame.Position;

import java.util.Vector;

public class AI
{
    static float TRIM_AMOUNT = 3.8172116f; // 2.0f;
    static int MAX_MOVE_DEPTH = 4; // 10;
    
    static float MARZAZ_PANI_VALUE = 5.9051347f; // 5.0f;
    static float MARZAZ_PANI_CENTER_VALUE = 5.8130927f; // 5.0f;
    static float MARZAZ_PANI_FORWARD_VALUE = 4.1728816f; // 5.0f;
    static float NABU_VALUE = 7.872682f; // 4.0f;
    static float NABU_CENTER_VALUE = 2.5738626f; // 4.0f;
    static float NABU_FORWARD_VALUE = 2.2674892f; // 4.0f;
    static float ETLU_VALUE = 6.972062f; // 6.0f;
    static float ETLU_CENTER_VALUE = 0.8595782f; // 6.0f;
    static float ETLU_FORWARD_VALUE = 6.8708687f; // 6.0f;
    static float GISGIGIR_VALUE = 9.035861f; // 8.0f;
    static float GISGIGIR_CENTER_VALUE = 5.398549f; // 8.0f;
    static float GISGIGIR_FORWARD_VALUE = 2.82567f; // 8.0f;
    static float REDUM_VALUE = 0.2923508f; // 1.0f;
    static float REDUM_CENTER_VALUE = 0.37392724f; // 1.0f;
    static float REDUM_FORWARD_VALUE = 0.5890716f; // 1.0f;
    /* public static final int MAX_PIECE_VALUE =
            MARZAZ_PANI_VALUE +
            2 * NABU_VALUE +
            2 * ETLU_VALUE +
            2 * GISGIGIR_VALUE +
            8 * REDUM_VALUE; */
    static float PIECE_MOD = 0.4663713f; // 1.0f;
    static float[] PIECE_PHASE_MOD = { 0.9532042f, 0.768793f, 0.07235641f }; // { 1.0f, 1.0f, 1.0f };
    static float CENTER_MOD = 0.085323974f; // 0.4f;
    static float[] CENTER_PHASE_MOD = { 0.22571117f, 0.24213408f, 0.13407926f }; // { 1.0f, 0.5f, 0.1f };
    static float FORWARD_MOD = 0.48287034f; // 0.2f;
    static float[] FORWARD_PHASE_MOD = { 0.06848762f, 0.0066441107f, 0.7967626f }; // { 1.0f, 0.0f, 0.0f };
    static float REDUM_FORWARD_MOD = 0.04217825f; // 0.6f;
    static float[] REDUM_FORWARD_PHASE_MOD = { 0.7630967f, 0.7867637f, 0.38618588f }; // { 0.5f, 0.5f, 1.0f };
    static float PIN_MOD = 0.8745739f; // 0.5f;
    static float[] PIN_PHASE_MOD = { 0.68790996f, 0.49320817f, 0.4705547f }; // { 1.0f, 1.0f, 1.0f };
    static float DEFENSE_MOD = 0.03991251f; // 0.1f;
    static float[] DEFENSE_PHASE_MOD = { 0.16867644f, 0.10819672f, 0.06282966f }; // { 1.0f, 0.3f, 0.1f };
    static float GISGIGIR_MOBILITY_MOD = 0.8389817f; // 0.2f;
    static float[] GISGIGIR_MOBILITY_PHASE_MOD = { 0.86129963f, 0.3545456f, 0.09168229f }; // { 0.5f, 0.7f, 1.0f };
    static float WIN_RANK = Float.MAX_VALUE;
    static float LOSE_RANK = -Float.MAX_VALUE;
    
    static float CAPTURE_INTEREST = 0.6358968f; // 0.25f;
    static float RANK_INTEREST = 0.72181135f; // 0.01f;
    static float FORK_INTEREST = 0.7413624f; // 0.05f;
    
    
    String[][] board;
    char colour;
    Vector<Move> moves;
    float curRank;
    
    public AI(String[][] board, char colour)
    {
        this.board = new String[9][9];
        
        for (int i = 1; i < board.length; i++)
        {
            for (int j = 1; j < board[i].length; j++)
            {
                this.board[i][j] = board[i][j];
            }
        }
        
        this.colour = colour;
        
        curRank = rankPosition(board);
        
        moves = getAllMoves(board, null, 'W');
        extendMoves();
    }

    public AI(String[][] board, char colour, float[] values)
    {
        TRIM_AMOUNT = values[0];
        MAX_MOVE_DEPTH = (int)values[1];
        MARZAZ_PANI_VALUE = values[2];
        MARZAZ_PANI_CENTER_VALUE = values[3];
        MARZAZ_PANI_FORWARD_VALUE = values[4];
        NABU_VALUE = values[5];
        NABU_CENTER_VALUE = values[6];
        NABU_FORWARD_VALUE = values[7];
        ETLU_VALUE = values[8];
        ETLU_CENTER_VALUE = values[9];
        ETLU_FORWARD_VALUE = values[10];
        GISGIGIR_VALUE = values[11];
        GISGIGIR_CENTER_VALUE = values[12];
        GISGIGIR_FORWARD_VALUE = values[13];
        REDUM_VALUE = values[14];
        REDUM_CENTER_VALUE = values[15];
        REDUM_FORWARD_VALUE = values[16];
        PIECE_MOD = values[17];
        PIECE_PHASE_MOD[0] = values[18];
        PIECE_PHASE_MOD[1] = values[19];
        PIECE_PHASE_MOD[2] = values[20];
        CENTER_MOD = values[21];
        CENTER_PHASE_MOD[0] = values[22];
        CENTER_PHASE_MOD[1] = values[23];
        CENTER_PHASE_MOD[2] = values[24];
        FORWARD_MOD = values[25];
        FORWARD_PHASE_MOD[0] = values[26];
        FORWARD_PHASE_MOD[1] = values[27];
        FORWARD_PHASE_MOD[2] = values[28];
        REDUM_FORWARD_MOD = values[29];
        REDUM_FORWARD_PHASE_MOD[0] = values[30];
        REDUM_FORWARD_PHASE_MOD[1] = values[31];
        REDUM_FORWARD_PHASE_MOD[2] = values[32];
        PIN_MOD = values[33];
        PIN_PHASE_MOD[0] = values[34];
        PIN_PHASE_MOD[1] = values[35];
        PIN_PHASE_MOD[2] = values[36];
        DEFENSE_MOD = values[37];
        DEFENSE_PHASE_MOD[0] = values[38];
        DEFENSE_PHASE_MOD[1] = values[39];
        DEFENSE_PHASE_MOD[2] = values[40];
        GISGIGIR_MOBILITY_MOD = values[41];
        GISGIGIR_MOBILITY_PHASE_MOD[0] = values[42];
        GISGIGIR_MOBILITY_PHASE_MOD[0] = values[43];
        GISGIGIR_MOBILITY_PHASE_MOD[0] = values[44];
        CAPTURE_INTEREST = values[45];
        RANK_INTEREST = values[46];
        FORK_INTEREST = values[47];


        this.board = new String[9][9];

        for (int i = 1; i < board.length; i++)
        {
            for (int j = 1; j < board[i].length; j++)
            {
                this.board[i][j] = board[i][j];
            }
        }

        this.colour = colour;

        curRank = rankPosition(board);

        moves = getAllMoves(board, null, 'W');
        extendMoves();
    }
    
    public static int outCalcMoves(Vector<Move> moves)
    {
        int calcMoves = 0;
        for (int i = 0; i < moves.size(); i++)
        {
            calcMoves++;
            if (moves.elementAt(i).moves != null)
                calcMoves += outCalcMoves(moves.elementAt(i).moves);
        }
        
        return calcMoves;
    }
    
    public void opponentMove(int startRank, int startFile, int finalRank, int finalFile)
    {
        for (int i = 0; i < moves.size(); i++)
        {
            if (moves.elementAt(i).startRank == startRank &&
                    moves.elementAt(i).startFile == startFile &&
                    moves.elementAt(i).finalRank == finalRank &&
                    moves.elementAt(i).finalFile == finalFile)
            {
                // board = moves.elementAt(i).getResult();
                Main.makeMove(board, startRank, startFile, finalRank, finalFile, colour);
                curRank = rankPosition(board);
                moves = moves.elementAt(i).moves;
                extendMoves();
                // System.out.println("CalcMoves: " + outCalcMoves(moves));
                return;
            }
        }
        
        System.err.println("Could not find move");
    }
    
    public void makeMove(Position startPosition, Position finishPosition)
    {
        Move chosenMove = chooseMove();
        
        startPosition.coordinates = chosenMove.startFile * 10 + chosenMove.startRank;
        finishPosition.coordinates = chosenMove.finalFile * 10 + chosenMove.finalRank;
        
        // board = chosenMove.getResult();
        Main.makeMove(board, chosenMove.startRank, chosenMove.startFile, chosenMove.finalRank, chosenMove.finalFile, colour);
        curRank = rankPosition(board);
        moves = chosenMove.moves;
        extendMoves();
        
        // System.out.println("CalcMoves: " + outCalcMoves(moves));
    }
    
    private void extendMoves()
    {
        for (int i = 0; i < moves.size(); i++)
        {
            moves.elementAt(i).prev = null;
            moves.elementAt(i).extend(1);
        }
    }
    
    private float phaseLerp(float phase, float[] phaseMod)
    {
        int arrayIndex = (int)((phaseMod.length - 1) * phase);
        
        phase -= arrayIndex / phaseMod.length;
        phase *= phaseMod.length - 1;
        
        return phaseMod[arrayIndex] * phase + phaseMod[arrayIndex + 1] * (1 - phase);
    }

    private float getPieceValue(char piece)
    {
        switch (piece)
        {
        case 'M':
            return MARZAZ_PANI_VALUE;
        case 'N':
            return NABU_VALUE;
        case 'E':
            return ETLU_VALUE;
        case 'G':
            return GISGIGIR_VALUE;
        case 'R':
            return REDUM_VALUE;
        case 'S':
            return Float.MAX_VALUE;
        }

        return 0.0f;
    }
    
    protected float rankPosition(String[][] board)
    {
        int numPieces = 0;
        boolean whiteSarrum = false;
        boolean blackSarrum = false;
        float pieceValue = 0;
        float centerValue = 0;
        float forwardValue = 0;
        float redumForwardValue = 0;
        float pinValue = 0;
        float defenseValue = 0;
        float gisgigirMobilityValue = 0;
        
        for (int i = 1; i < board.length; i++)
        {
            for (int j = 1; j < board[i].length; j++)
            {
                if (!"  ".equals(board[i][j]))
                {
                    numPieces++;
                    
                    char pieceColour = board[i][j].charAt(0);
                    char piece = board[i][j].charAt(1);
                    
                    if (piece == 'S')
                    {
                        if (pieceColour == 'W')
                            whiteSarrum = true;
                        else
                            blackSarrum = true;
                    }
                    
                    float pieceMod = (pieceColour == colour) ? PIECE_MOD : -PIECE_MOD;
                    float centerMod = 0.0f;
                    
                    if (i >= 4 && i <= 5 && j >= 4 && j <= 5)
                        centerMod += (pieceColour == colour) ? CENTER_MOD * 0.5f : -CENTER_MOD * 0.5f;
                    if (i >= 3 && i <= 6 && j >= 3 && j <= 6)
                        centerMod += (pieceColour == colour) ? CENTER_MOD * 0.5f : -CENTER_MOD * 0.5f;
                    if (i >= 2 && i <= 7 && j >= 2 && j <= 7)
                        centerMod += (pieceColour == colour) ? CENTER_MOD * 0.5f : -CENTER_MOD * 0.5f;
                    
                    float forwardMod = ((pieceColour == colour) ? FORWARD_MOD * 0.125f : -FORWARD_MOD * 0.125f ) * ((pieceColour == 'B') ? i : 9 - i);
                    float redumForwardMod = ((pieceColour == colour) ? REDUM_FORWARD_MOD * 0.125f : -REDUM_FORWARD_MOD * 0.125f ) * ((pieceColour == 'B') ? i : 9 - i);
                    float pinMod = ((pieceColour == colour) ? PIN_MOD : -PIN_MOD);
                    float defenseMod = ((pieceColour == colour) ? DEFENSE_MOD : -DEFENSE_MOD);
                    float gisgigirMobilityMod = ((pieceColour == colour) ? GISGIGIR_MOBILITY_MOD / 12.0f : -GISGIGIR_MOBILITY_MOD / 12.0f);
                    
                    switch (piece)
                    {
                    case 'M':
                        pieceValue += MARZAZ_PANI_VALUE * pieceMod;
                        centerValue += MARZAZ_PANI_CENTER_VALUE * centerMod;
                        forwardValue += MARZAZ_PANI_FORWARD_VALUE * forwardMod;
                        defenseValue += getDefenceRank(board, getMarzazPaniTargets(board, i, j), pieceColour) * defenseMod;
                        break;
                    case 'N':
                        pieceValue += NABU_VALUE * pieceMod;
                        centerValue += NABU_CENTER_VALUE * centerMod;
                        forwardValue += NABU_FORWARD_VALUE * forwardMod;
                        defenseValue += getDefenceRank(board, getNabuTargets(board, i, j), pieceColour) * defenseMod;
                        break;
                    case 'E':
                        pieceValue += ETLU_VALUE * pieceMod;
                        centerValue += ETLU_CENTER_VALUE * centerMod;
                        forwardValue += ETLU_FORWARD_VALUE * forwardMod;
                        defenseValue += getDefenceRank(board, getEtluTargets(board, i, j), pieceColour) * defenseMod;
                        break;
                    case 'G':
                        pieceValue += GISGIGIR_VALUE * pieceMod;
                        centerValue += GISGIGIR_CENTER_VALUE * centerMod;
                        forwardValue += GISGIGIR_FORWARD_VALUE * forwardMod;
                        defenseValue += getDefenceRank(board, getGisgigirTargets(board, i, j), pieceColour) * defenseMod;
                        gisgigirMobilityValue += getGisgigirTargets(board, i, j).size() * gisgigirMobilityMod;

                        // Check for pins - this is the only piece that can pin
                        for (int pinI = 0; pinI <= 3; pinI++)
                        {
                            int xMod = 0;
                            int yMod = 0;

                            if (pinI == 0)
                                xMod = -1;
                            else if (pinI == 1)
                                xMod = 1;
                            else if (pinI == 2)
                                yMod = -1;
                            else
                                yMod = 1;

                            char pinnedPiece = ' ';

                            for (int rank = i, file = j;
                                 Main.isPositionValid(rank, file);
                                 rank += xMod, file += yMod)
                            {
                                if (board[i][j].charAt(0) == pieceColour)
                                    break;
                                else if (board[i][j].charAt(0) != ' ')
                                {
                                    if (pinnedPiece == ' ')
                                        pinnedPiece = board[i][j].charAt(1);
                                    else
                                    {
                                        if (getPieceValue(pinnedPiece) < getPieceValue(board[i][j].charAt(1)))
                                        {
                                            switch (pinnedPiece)
                                            {
                                            case 'M':
                                                pinValue += MARZAZ_PANI_VALUE * pinMod;
                                                break;
                                            case 'N':
                                                pinValue += NABU_VALUE * pinMod;
                                            case 'E':
                                                pinValue += ETLU_VALUE * pinMod;
                                            case 'G':
                                                pinValue += GISGIGIR_VALUE * pinMod;
                                            case 'R':
                                                pinValue += REDUM_VALUE * pinMod;
                                            }
                                        }

                                        break;
                                    }
                                }
                            }
                        }

                        break;
                    case 'R':
                        pieceValue += REDUM_VALUE * pieceMod;
                        centerValue += REDUM_CENTER_VALUE * centerMod;
                        forwardValue += REDUM_FORWARD_VALUE * forwardMod;
                        defenseValue += getDefenceRank(board, getRedumTargets(board, i, j), pieceColour) * defenseMod;
                        redumForwardValue += redumForwardMod;
                        break;
                    }
                }
            }
        }
        
        // TODO: better phase calculation
        float phase = 1.0f - ((float)numPieces / 32.0f);
        
        float rank = phaseLerp(phase, PIECE_PHASE_MOD) * pieceValue
                + phaseLerp(phase, CENTER_PHASE_MOD) * centerValue
                + phaseLerp(phase, FORWARD_PHASE_MOD) * forwardValue
                + phaseLerp(phase, REDUM_FORWARD_PHASE_MOD) * redumForwardValue
                + phaseLerp(phase, PIN_PHASE_MOD) * pinValue
                + phaseLerp(phase, DEFENSE_PHASE_MOD) * defenseValue
                + phaseLerp(phase, GISGIGIR_MOBILITY_PHASE_MOD) * gisgigirMobilityValue;
        
        if (!whiteSarrum)
        {
            if (colour == 'W')
                rank = LOSE_RANK;
            else
                rank = WIN_RANK;
        }
        if (!blackSarrum)
        {
            if (colour == 'B')
                rank = LOSE_RANK;
            else
                rank = WIN_RANK;
        }
        
        return rank;
    }
    
    protected Vector<Move> getAllMoves(String[][] board, Move prev, char curPlayer)
    {
        Vector<Move> moves = new Vector<Move>();
        
        for (int startRank = 1; startRank < board.length; startRank++)
        {
            for (int startFile = 1; startFile < board[startRank].length; startFile++)
            {
                if (board[startRank][startFile].charAt(0) == curPlayer)
                {
                    char piece = board[startRank][startFile].charAt(1);
                    
                    switch (piece)
                    {
                    case 'M':
                        moves.addAll(getMarzazPaniMoves(board, prev, startRank, startFile, curPlayer));
                        break;
                    case 'N':
                        moves.addAll(getNabuMoves(board, prev, startRank, startFile, curPlayer));
                        break;
                    case 'E':
                        moves.addAll(getEtluMoves(board, prev, startRank, startFile, curPlayer));
                        break;
                    case 'G':
                        moves.addAll(getGisgigirMoves(board, prev, startRank, startFile, curPlayer));
                        break;
                    case 'R':
                        moves.addAll(getRedumMoves(board, prev, startRank, startFile, curPlayer));
                        break;
                    case 'S':
                        moves.addAll(getSarrumMoves(board, prev, startRank, startFile, curPlayer));
                    }
                }
            }
        }
        
        return moves;
    }

    protected float getDefenceRank(String[][] board, Vector<Coordinate> targets, char pieceColour)
    {
        float rank = 0.0f;

        for (int i = 0; i < targets.size(); i++)
        {
            String piece = board[targets.get(i).rank][targets.get(i).file];
            if (piece.charAt(0) == pieceColour && piece.charAt(1) != 'S')
                rank += getPieceValue(piece.charAt(1));
        }

        return rank;
    }

    protected static Vector<Coordinate> getTargetsFrom(String[][] board, int startRank, int startFile)
    {
        char piece = board[startRank][startFile].charAt(1);

        switch (piece)
        {
        case 'M':
            return getMarzazPaniTargets(board, startRank, startFile);
        case 'N':
            return getNabuTargets(board, startRank, startFile);
        case 'E':
            return getEtluTargets(board, startRank, startFile);
        case 'G':
            return getGisgigirTargets(board, startRank, startFile);
        case 'R':
            return getRedumTargets(board, startRank, startFile);
        case 'S':
            return getSarrumTargets(board, startRank, startFile);
        default:
            return new Vector<Coordinate>();
        }
    }

    private static Vector<Coordinate> getEtluTargets(String[][] board, int startRank, int startFile)
    {
        Vector<Coordinate> targets = new Vector<Coordinate>();

        if (Main.isPositionValid(startRank - 2, startFile))
            targets.add(new Coordinate(startRank - 2, startFile));
        if (Main.isPositionValid(startRank + 2, startFile))
            targets.add(new Coordinate(startRank + 2, startFile));
        if (Main.isPositionValid(startRank, startFile - 2))
            targets.add(new Coordinate(startRank, startFile - 2));
        if (Main.isPositionValid(startRank, startFile + 2))
            targets.add(new Coordinate(startRank, startFile + 2));

        return targets;
    }

    private static Vector<Coordinate> getGisgigirTargets(String[][] board, int startRank, int startFile)
    {
        Vector<Coordinate> targets = new Vector<Coordinate>();

        for (int i = 0; i <= 3; i++)
        {
            int xMod = 0;
            int yMod = 0;

            if (i == 0)
                xMod = -1;
            else if (i == 1)
                xMod = 1;
            else if (i == 2)
                yMod = -1;
            else
                yMod = 1;

            for (int rank = startRank, file = startFile;
                    Main.isPositionValid(rank, file);
                    rank += xMod, file += yMod)
            {
                targets.add(new Coordinate(rank, file));

                if (!board[rank][file].equals("  "))
                    break;
            }
        }

        return targets;
    }

    private static Vector<Coordinate> getMarzazPaniTargets(String[][] board, int startRank, int startFile)
    {
        Vector<Coordinate> targets = new Vector<Coordinate>();

        if (Main.isPositionValid(startRank - 1, startFile))
            targets.add(new Coordinate(startRank - 1, startFile));
        if (Main.isPositionValid(startRank + 1, startFile))
            targets.add(new Coordinate(startRank + 1, startFile));
        if (Main.isPositionValid(startRank, startFile + 1))
            targets.add(new Coordinate(startRank, startFile + 1));
        if (Main.isPositionValid(startRank, startFile - 1))
            targets.add(new Coordinate(startRank, startFile - 1));

        return targets;
    }

    private static Vector<Coordinate> getNabuTargets(String[][] board, int startRank, int startFile)
    {
        Vector<Coordinate> targets = new Vector<Coordinate>();

        if (Main.isPositionValid(startRank - 1, startFile - 1))
            targets.add(new Coordinate(startRank - 1, startFile - 1));
        if (Main.isPositionValid(startRank + 1, startFile - 1))
            targets.add(new Coordinate(startRank + 1, startFile - 1));
        if (Main.isPositionValid(startRank - 1, startFile + 1))
            targets.add(new Coordinate(startRank - 1, startFile + 1));
        if (Main.isPositionValid(startRank + 1, startFile + 1))
            targets.add(new Coordinate(startRank + 1, startFile + 1));

        return targets;
    }

    private static Vector<Coordinate> getRedumTargets(String[][] board, int startRank, int startFile)
    {
        Vector<Coordinate> targets = new Vector<Coordinate>();

        int finalRank = startRank;
        if (board[startRank][startFile].charAt(0) == 'W')
            finalRank--;
        else
            finalRank++;

        if (Main.isPositionValid(finalRank, startFile - 1))
            targets.add(new Coordinate(finalRank, startFile - 1));
        if (Main.isPositionValid(finalRank, startFile + 1))
            targets.add(new Coordinate(finalRank, startFile + 1));

        return targets;
    }

    private static Vector<Coordinate> getSarrumTargets(String[][] board, int startRank, int startFile)
    {
        Vector<Coordinate> targets = new Vector<Coordinate>();

        if (Main.isPositionValid(startRank - 1, startFile - 1))
            targets.add(new Coordinate(startRank - 1, startFile - 1));
        if (Main.isPositionValid(startRank - 1, startFile))
            targets.add(new Coordinate(startRank - 1, startFile));
        if (Main.isPositionValid(startRank - 1, startFile + 1))
            targets.add(new Coordinate(startRank - 1, startFile + 1));
        if (Main.isPositionValid(startRank, startFile - 1))
            targets.add(new Coordinate(startRank, startFile - 1));
        if (Main.isPositionValid(startRank, startFile + 1))
            targets.add(new Coordinate(startRank, startFile + 1));
        if (Main.isPositionValid(startRank + 1, startFile - 1))
            targets.add(new Coordinate(startRank + 1, startFile - 1));
        if (Main.isPositionValid(startRank + 1, startFile))
            targets.add(new Coordinate(startRank + 1, startFile));
        if (Main.isPositionValid(startRank + 1, startFile + 1))
            targets.add(new Coordinate(startRank + 1, startFile + 1));

        return targets;
    }
    
    private Vector<Move> getEtluMoves(String[][] board, Move prev, int startRank, int startFile, char color)
    {
        Vector<Move> moves = new Vector<Move>();
        
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank - 2, startFile, color))
            moves.add(new Move(prev, startRank, startFile, startRank - 2, startFile, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank + 2, startFile, color))
            moves.add(new Move(prev, startRank, startFile, startRank + 2, startFile, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank, startFile + 2, color))
            moves.add(new Move(prev, startRank, startFile, startRank, startFile + 2, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank, startFile - 2, color))
            moves.add(new Move(prev, startRank, startFile, startRank, startFile - 2, color, this));
        
        return moves;
    }
    
    private Vector<Move> getGisgigirMoves(String[][] board, Move prev, int startRank, int startFile, char color)
    {
        Vector<Move> moves = new Vector<Move>();
        
        for (int file = 1; file < board[startRank].length; file++)
        {
            if (Main.checkMoveIsLegal(board, startRank, startFile, startRank, file, color))
                moves.add(new Move(prev, startRank, startFile, startRank, file, color, this));
        }
        
        for (int rank = 1; rank < board.length; rank++)
        {
            if (Main.checkMoveIsLegal(board, startRank, startFile, rank, startFile, color))
                moves.add(new Move(prev, startRank, startFile, rank, startFile, color, this));
        }
        
        return moves;
    }
    
    private Vector<Move> getMarzazPaniMoves(String[][] board, Move prev, int startRank, int startFile, char color)
    {
        Vector<Move> moves = new Vector<Move>();
        
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank - 1, startFile, color))
            moves.add(new Move(prev, startRank, startFile, startRank - 1, startFile, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank + 1, startFile, color))
            moves.add(new Move(prev, startRank, startFile, startRank + 1, startFile, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank, startFile + 1, color))
            moves.add(new Move(prev, startRank, startFile, startRank, startFile + 1, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank, startFile - 1, color))
            moves.add(new Move(prev, startRank, startFile, startRank, startFile - 1, color, this));
        
        return moves;
    }
    
    private Vector<Move> getNabuMoves(String[][] board, Move prev, int startRank, int startFile, char color)
    {
        Vector<Move> moves = new Vector<Move>();
        
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank - 1, startFile - 1, color))
            moves.add(new Move(prev, startRank, startFile, startRank - 1, startFile - 1, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank + 1, startFile - 1, color))
            moves.add(new Move(prev, startRank, startFile, startRank + 1, startFile - 1, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank - 1, startFile + 1, color))
            moves.add(new Move(prev, startRank, startFile, startRank - 1, startFile + 1, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank + 1, startFile + 1, color))
            moves.add(new Move(prev, startRank, startFile, startRank + 1, startFile + 1, color, this));
        
        return moves;
    }
    
    private Vector<Move> getRedumMoves(String[][] board, Move prev, int startRank, int startFile, char color)
    {
        Vector<Move> moves = new Vector<Move>();
        
        int finalRank = startRank;
        if (color == 'W')
            finalRank--;
        else
            finalRank++;
        
        if (Main.checkMoveIsLegal(board, startRank, startFile, finalRank, startFile - 1, color))
            moves.add(new Move(prev, startRank, startFile, finalRank, startFile - 1, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, finalRank, startFile, color))
            moves.add(new Move(prev, startRank, startFile, finalRank, startFile, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, finalRank, startFile + 1, color))
            moves.add(new Move(prev, startRank, startFile, finalRank, startFile + 1, color, this));
        
        return moves;
    }
    
    private Vector<Move> getSarrumMoves(String[][] board, Move prev, int startRank, int startFile, char color)
    {
        Vector<Move> moves = new Vector<Move>();
        
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank - 1, startFile - 1, color))
            moves.add(new Move(prev, startRank, startFile, startRank - 1, startFile - 1, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank - 1, startFile, color))
            moves.add(new Move(prev, startRank, startFile, startRank - 1, startFile, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank - 1, startFile + 1, color))
            moves.add(new Move(prev, startRank, startFile, startRank - 1, startFile + 1, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank, startFile - 1, color))
            moves.add(new Move(prev, startRank, startFile, startRank, startFile - 1, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank, startFile + 1, color))
            moves.add(new Move(prev, startRank, startFile, startRank, startFile + 1, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank + 1, startFile - 1, color))
            moves.add(new Move(prev, startRank, startFile, startRank + 1, startFile - 1, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank + 1, startFile, color))
            moves.add(new Move(prev, startRank, startFile, startRank + 1, startFile, color, this));
        if (Main.checkMoveIsLegal(board, startRank, startFile, startRank + 1, startFile + 1, color))
            moves.add(new Move(prev, startRank, startFile, startRank + 1, startFile + 1, color, this));
        
        return moves;
    }
    
    private Move chooseMove()
    {
        Vector<Move> bestMoves = new Vector<Move>();
        
        float bestRank = LOSE_RANK;
        for (int i = 0; i < moves.size(); i++)
        {
            if (moves.elementAt(i).resultValue > bestRank)
            {
                bestMoves = new Vector<Move>();
                bestMoves.add(moves.elementAt(i));
                bestRank = moves.elementAt(i).resultValue;
            }
            else if (moves.elementAt(i).resultValue == bestRank)
            {
                bestMoves.add(moves.elementAt(i));
            }
        }
        
        return bestMoves.get((int)(Math.random() * bestMoves.size()));
    }
}
