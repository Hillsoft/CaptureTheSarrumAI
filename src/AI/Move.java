package AI;

import BaseAQAGame.Main;

import java.util.Vector;

class Move
{
    int startFile;
    int startRank;
    int finalFile;
    int finalRank;
    char turn;
    Move prev;
    float resultValue;
    Vector<Move> moves;
    AI owner;
    String[][] cache;
    
    float interest;

    public Move(Move prev, int startRank, int startFile, int finalRank, int finalFile, char color, AI owner)
    {
        this.startFile = startFile;
        this.startRank = startRank;
        this.finalFile = finalFile;
        this.finalRank = finalRank;
        this.turn = color;
        this.prev = prev;
        this.owner = owner;
        this.cache = null;

        String[][] current = getResult();
        String[][] before = getBefore();

        resultValue = owner.rankPosition(current);

        interest = getInterest(current, before);

        moves = null;
    }

    float getInterest(String[][] current, String[][] before)
    {
        if (resultValue == AI.WIN_RANK || resultValue == AI.LOSE_RANK)
            return 0.0f;

        float interest = 0.0f;
        interest += (resultValue - owner.curRank) * AI.RANK_INTEREST;

        if (!before[finalRank][finalFile].equals("  "))
            interest += AI.CAPTURE_INTEREST;

        for (int i = 1; i < current.length; i++)
        {
            for (int j = 1; j < current[i].length; j++)
            {
                int attackNum = getAttackNum(current, i, j);

                if (attackNum > 1)
                    interest += AI.FORK_INTEREST;
            }
        }

        interest = (interest + 1.0f) / 2.0f;
        interest = Math.min(Math.max(interest, 0.1f), 0.9f);

        return interest;
    }
    
    String[][] getBefore()
    {
        if (prev != null)
            return prev.getResult();
        else
            return owner.board;
    }
    
    String[][] getResult()
    {
        if (cache == null)
        {
            String[][] before = getBefore();

            String[][] after = new String[9][9];
            for (int i = 1; i < before.length; i++)
            {
                for (int j = 1; j < before[i].length; j++)
                {
                    after[i][j] = before[i][j];
                }
            }

            Main.makeMove(after, startRank, startFile, finalRank, finalFile, turn);

            return after;
        }
        else
        {
            return cache;
        }
    }
    
    void cache()
    {
        cache = getResult();
    }
    
    void uncache()
    {
        cache = null;
    }

    void extend(int depth)
    {
        moves = null;
        
        boolean further = true;

        if (resultValue == AI.WIN_RANK || resultValue == AI.LOSE_RANK)
        {
            further = false;
        }
        else if (depth > AI.MAX_MOVE_DEPTH)
        {
            further = false;
        }
        else if (depth <= 2)
        {
            further = true;
        }
        else if (Math.random() > Math.pow(interest, AI.TRIM_AMOUNT * depth) /* (1 - interest) * interest * Math.pow(depth, -AI.AI.TRIM_AMOUNT) + interest */)
        {
            further = false;
        }
        
        if (further)
        {
            cache();
            
            resultValue = (turn == owner.colour) ? AI.WIN_RANK : AI.LOSE_RANK;
            moves = owner.getAllMoves(getResult(), this, (turn == 'W') ? 'B' : 'W');

            Vector<ExtendThread> threads = new Vector<ExtendThread>(moves.size());

            for (int i = 0; i < moves.size(); i++)
            {
                /* ExtendThread newThread = new ExtendThread(moves.elementAt(i), depth + 1);
                threads.add(newThread);
                newThread.start(); */

                moves.elementAt(i).extend(depth + 1);

                // Note: MinMax moved to AI.ExtendThread
                if (turn == owner.colour && moves.elementAt(i).resultValue < resultValue)
                    resultValue = moves.elementAt(i).resultValue;
                else if (turn != owner.colour && moves.elementAt(i).resultValue > resultValue)
                    resultValue = moves.elementAt(i).resultValue;
            }

            // Wait until all threads are done
            /* boolean threadsDone = false;
            while (!threadsDone)
            {
                threadsDone = true;
                try {
                    Thread.sleep(100);
                } catch (java.lang.InterruptedException ex) { }

                for (int i = 0; i < threads.size(); i++)
                {
                    if (threads.get(i).isAlive())
                    {
                        threadsDone = false;
                        break;
                    }
                }
            } */

            
            uncache();
        }
        else
        {
            resultValue = owner.rankPosition(getResult());
        }
        
        /* if (moves != null)
        {
            for (int i = 0; i < moves.size(); i++)
            {
                moves.elementAt(i).extend(depth + 1);
            }
        }
        else
        {
            resultValue = (turn == owner.colour) ? AI.AI.LOSE_RANK : AI.AI.WIN_RANK;
            moves = owner.getAllMoves(result, (turn == 'W') ? 'B' : 'W');

            for (int i = 0; i < moves.size(); i++)
            {
                if (turn == owner.colour && moves.elementAt(i).resultValue > resultValue)
                    resultValue = moves.elementAt(i).resultValue;
                else if (turn != owner.colour && moves.elementAt(i).resultValue < resultValue)
                    resultValue = moves.elementAt(i).resultValue;
            }
        } */
    }

    int getAttackNum(String[][] board, int rank, int file)
    {
        if (board[rank][file].equals("  "))
            return 0;

        Vector<Coordinate> targets = AI.getTargetsFrom(board, rank, file);

        int numAttacks = 0;

        for (int i = 0; i < targets.size(); i++)
        {
            char targetColour = board[targets.get(i).rank][targets.get(i).file].charAt(0);
            if (targetColour != turn && targetColour != ' ')
                numAttacks++;
        }

        return numAttacks;
    }
}