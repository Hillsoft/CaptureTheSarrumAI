package GeneticOptimiser;

import BaseAQAGame.Main;

import java.io.*;

public class GeneticMain
{
    public static int NUM_SETS = 4;

    public static void main(String[] args)
    {
        int generation = 1;
        float[][] sets = new float[NUM_SETS][48];

        File file = new File("sets.dat");
        if (file.exists())
        {
            // Load sets from file
            try
            {
                ObjectInputStream reader = new ObjectInputStream(new FileInputStream(file));
                generation = reader.readInt();

                for (int set = 0; set < NUM_SETS; set++)
                {
                    for (int i = 0; i < sets[set].length; i++)
                    {
                        sets[set][i] = reader.readFloat();
                    }
                }

                reader.close();
            }
            catch (java.io.IOException ex)
            {
                System.err.println("Error reading file");
                System.exit(-1);
            }
        }
        else
        {
            // Generate new sets

            for (int set = 0; set < NUM_SETS; set++)
            {
                for (int i = 0; i < sets[set].length; i++)
                {
                    float min = 0.0f;
                    float max = 1.0f;

                    if (i == 0)
                        max = 5.0f;
                    else if (i == 1)
                    {
                        min = 2.0f;
                        max = 10.0f;
                    }
                    else if (i >= 2 && i <= 13)
                        max = 10.0f;


                    sets[set][i] = (float)Math.random() * (max - min) + min;
                }
            }

            writeSets(generation, sets);
        }

        // Lets play!
        while (true)
        {
            System.out.println("Processing generation " + generation + "...");

            int[] scores = new int[NUM_SETS];

            for (int i = 0; i < NUM_SETS; i++)
            {
                for (int j = i + 1; j < NUM_SETS; j++)
                {
                    try
                    {
                        int result = playGame(sets[i], sets[j]);
                        if (result == 1)
                            scores[i]++;
                        else if (result == 2)
                            scores[j]++;

                        result = playGame(sets[j], sets[i]);
                        if (result == 1)
                            scores[j]++;
                        else if (result == 2)
                            scores[i]++;
                    }
                    catch (IOException ex) { }
                }
            }

            // Get the next generation
            float[][] newSets = new float[NUM_SETS][48];

            // Keep best set
            int bestSet = -1;
            int bestScore = -1;
            int totalScore = 0;
            for (int i = 0; i < scores.length; i++)
            {
                totalScore += scores[i];
                if (scores[i] > bestScore)
                {
                    bestSet = i;
                    bestScore = scores[i];
                }
            }
            newSets[0] = sets[bestSet];

            for (int set = 1; set < NUM_SETS; set++)
            {
                for (int i = 0; i < sets[set].length; i++)
                {
                    // Random new value
                    if (Math.random() < 0.05)
                    {
                        float min = 0.0f;
                        float max = 1.0f;

                        if (i == 0)
                            max = 5.0f;
                        else if (i == 1)
                        {
                            min = 2.0f;
                            max = 10.0f;
                        }
                        else if (i >= 2 && i <= 13)
                            max = 10.0f;


                        newSets[set][i] = (float)Math.random() * (max - min) + min;
                    }
                    else
                    {
                        // Pick from a set
                        float targetScore = (float)Math.random() * totalScore;
                        int curScore = 0;
                        for (int j = 0; j < scores.length; j++)
                        {
                            curScore += scores[j];
                            if (curScore >= targetScore)
                            {
                                newSets[set][i] = sets[j][i];
                                break;
                            }
                        }
                    }
                }
            }

            sets = newSets;

            generation++;
            writeSets(generation, sets);
        }
    }

    public static int playGame(float[] set1, float[] set2) throws IOException
    {
        Runtime runtime = Runtime.getRuntime();

        String execString1 = "java -Xmx1g -jar C:\\Users\\hills_000\\Documents\\Dev\\SarrumAI\\out\\artifacts\\AIOnlyMain\\AIOnlyMain.jar W";
        for (int i = 0; i < set1.length; i++)
            execString1 += " " + set1[i];
        String execString2 = "java -Xmx1g -jar C:\\Users\\hills_000\\Documents\\Dev\\SarrumAI\\out\\artifacts\\AIOnlyMain\\AIOnlyMain.jar B";
        for (int i = 0; i < set2.length; i++)
            execString2 += " " + set2[i];

        Process ai1 = runtime.exec(execString1);
        BufferedReader ai1Input = new BufferedReader(new InputStreamReader(ai1.getInputStream()));
        OutputStreamWriter ai1Output = new OutputStreamWriter(ai1.getOutputStream());
        Process ai2 = runtime.exec(execString2);
        BufferedReader ai2Input = new BufferedReader(new InputStreamReader(ai2.getInputStream()));
        OutputStreamWriter ai2Output = new OutputStreamWriter(ai2.getOutputStream());

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
        char turn = 'W';

        boolean gameOver = false;
        int moves = 0;
        while (!gameOver && moves < 300)
        {
            BufferedReader currentIn;
            OutputStreamWriter currentOut;
            if (turn == 'W')
            {
                currentIn = ai1Input;
                currentOut = ai2Output;
            }
            else
            {
                currentIn = ai2Input;
                currentOut = ai1Output;
            }

            int startPosition;
            int finishPosition;
            long curTime = System.currentTimeMillis();
            while (!currentIn.ready())
            {
                if (!ai1.isAlive() || !ai2.isAlive() || System.currentTimeMillis() > curTime + 10000)
                {
                    ai1.destroy();
                    ai2.destroy();
                    return (turn == 'W') ? 2 : 1;
                }
            }
            startPosition = Integer.parseInt(currentIn.readLine());
            finishPosition = Integer.parseInt(currentIn.readLine());

            if (board[finishPosition % 10][finishPosition / 10].charAt(1) == 'S')
            {
                ai1.destroy();
                ai2.destroy();
                return (turn == 'W') ? 1 : 2;
            }

            Main.makeMove(board, startPosition % 10, startPosition / 10, finishPosition % 10, finishPosition / 10, turn);

            currentOut.write(String.valueOf(startPosition) + "\n");
            currentOut.write(String.valueOf(finishPosition) + "\n");
            currentOut.flush();

            // Main.displayBoard(board);

            turn = (turn == 'W') ? 'B' : 'W';
            moves++;
        }

        ai1.destroy();
        ai2.destroy();
        return 0;
    }

    public static void writeSets(int generation, float[][] sets)
    {
        File file = new File("sets.dat");
        file.delete();
        try
        {
            ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(file));
            writer.writeInt(generation);

            for (int set = 0; set < NUM_SETS; set++)
            {
                for (int i = 0; i < sets[set].length; i++)
                {
                    writer.writeFloat(sets[set][i]);
                }
            }

            writer.close();
        }
        catch (java.io.IOException ex)
        {
            System.err.println("Error writing file");
            System.exit(-1);
        }

        File bestFile = new File("best.txt");
        bestFile.delete();
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(bestFile));

            for (int i = 0; i < sets[0].length; i++)
            {
                writer.write(String.valueOf(sets[0][i]) + "\n");
            }

            writer.close();
        }
        catch (java.io.IOException ex) { }
    }
}
