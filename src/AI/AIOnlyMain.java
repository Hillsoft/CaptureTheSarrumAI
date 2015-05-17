package AI;

import BaseAQAGame.Position;

import java.util.Scanner;

public class AIOnlyMain
{
    public static void main(String[] args)
    {
        String[][] board =
                {
                        { null, null, null, null, null, null, null, null, null },
                        { null, "BG", "BE", "BN", "BM", "BS", "BN", "BE", "BG" },
                        { null, "BR", "BR", "BR", "BR", "BR", "BR", "BR", "BR" },
                        { null, "  ", "  ", "  ", "  ", "  ", "  ", "  ", "  " },
                        { null, "  ", "  ", "  ", "  ", "  ", "  ", "  ", "  " },
                        { null, "  ", "  ", "  ", "  ", "  ", "  ", "  ", "  " },
                        { null, "  ", "  ", "  ", "  ", "  ", "  ", "  ", "  " },
                        { null, "WR", "WR", "WR", "WR", "WR", "WR", "WR", "WR" },
                        { null, "WG", "WE", "WN", "WM", "WS", "WN", "WE", "WG" }
                };

        char colour = args[0].charAt(0);

        float[] values = new float[48];
        for (int i = 0; i < 48; i++)
        {
            values[i] = Float.parseFloat(args[i + 1]);
        }

        AI ai = new AI(board, colour, values);

        Scanner input = new Scanner(System.in);

        char curTurn = 'W';
        while (true)
        {
            if (curTurn == colour)
            {
                Position startPosition = new Position();
                Position finishPosition = new Position();
                ai.makeMove(startPosition, finishPosition);
                System.out.println(startPosition.coordinates + "\n" + finishPosition.coordinates);
            }
            else
            {
                int startPosition = input.nextInt();
                int finishPosition = input.nextInt();
                ai.opponentMove(startPosition % 10, startPosition / 10, finishPosition % 10, finishPosition / 10);
            }

            curTurn = (curTurn == 'W') ? 'B' : 'W';
        }
    }
}
