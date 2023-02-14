/**
 * MiniMax for midgame/endgame phase
 */

import java.io.*;
import java.util.ArrayList;

public class MiniMaxGame {
    public static BoardOut MiniMax(PosList board, int depth, boolean isMax) {
        BoardOut out = new BoardOut();

        // depth = 0 is leaf node which is static estimation, also base case of the recursion
        if (depth == 0) {
            out.count++;
            out.val = Generate.staticEstimationMidGameEndGame(board);
            return out;
        }

        ArrayList<PosList> moves;
        // initialising the value based on max or min round
        if (isMax) {
            out.val = Integer.MIN_VALUE;
            moves = Generate.generateMovesMidgameEndgame(board);
        }
        else {
            out.val = Integer.MAX_VALUE;
            moves = Generate.generateMovesMidgameEndgameForBlack(board);
        }
        BoardOut out2 = new BoardOut();
        for (PosList list: moves) {
            //recursive mini max for max node
            if (isMax) {
                out2 = MiniMax(list, depth -1, false);
                out.count += out2.count; //TODO: check logic
                if (out2.val > out.val) {
                    out.list = list;
                    out.val = out2.val;
                }
            }
            //recursive minimax for min node
            else {
                out2 = MiniMax(list, depth -1, true);
                out.count += out2.count; //TODO: check logic
                if (out2.val < out.val) {
                    out.list = list;
                    out.val = out2.val;
                }
            }
        }
        return out;
    }
    public static void main(String[] args) throws IOException {
//        String in = "xBBBxWBWWBWWWBBBWW";
//        String in = "xBBxxxWxxWxxBxBxxW";
//        String in = "WWBBBBxWxxxxWBWxWx";
        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
        String startingBoard = reader.readLine();
        reader.close();
        int depth = Integer.parseInt(args[1]);
        ArrayList<Character> ip = new ArrayList<>();
        for (char c : startingBoard.toCharArray())
            ip.add(c);
        BoardOut op = MiniMax(new PosList(ip), depth, true);

        String result = "Board Position: "+op.list.toString()+"\nPositions evaluated by static estimation: "+op.count
                +"\nMINIMAX estimate: "+op.val+"\ndepth: "+depth;
        BufferedWriter writer = new BufferedWriter(new FileWriter(args[2]));
        writer.write(result);
        writer.close();

//        System.out.println("Minimax val = "+op.val+" Minimax count = "+op.count+" Minimax position = "+op.list.toString());
    }
}
