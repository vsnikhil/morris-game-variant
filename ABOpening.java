/**
 * α β pruning for opening phase
 */

import java.io.*;
import java.util.ArrayList;

public class ABOpening {
    public static BoardOut ABPruning(PosList board, int depth, boolean isMax, int a, int b) {
        BoardOut out = new BoardOut();

        // depth = 0 is leaf node which is static estimation, also base case of the recursion
        if (depth == 0) {
            out.count++;
            out.val = Generate.staticEstimationForOpening(board);
            return out;
        }

        ArrayList<PosList> moves;
        if (isMax) moves = Generate.generateMovesOpening(board);
        else moves = Generate.generateMovesOpeningForBlack(board);
        BoardOut out2 = new BoardOut();
        for(PosList list: moves) {
            //recursive alpha beta for max node
            if (isMax) {
                out2 = ABPruning(list, depth-1, false, a, b);
                out.count += out2.count;
                if (out2.val > a) {
                    out.list = list;
                    a = out2.val;
                }
            }
            //recursive alpha beta for min node
            else {
                out2 = ABPruning(list, depth-1, true, a, b);
                out.count += out2.count;
//                out.count++;
                //TODO: check count logic
                if (out2.val < b) {
                    out.list = list;
                    b = out2.val;
                }
            }
            //alpha is greater than beta skip evaluating the other leaf nodes
            if (a >= b)
                break;
        }
        if (isMax) out.val = a;
        else out.val = b;
        return out;
    }
    public static void main(String[] args) throws IOException {
//        String in = "xxxxxxWxxxxxxBxxxx";
//        String in = "xWxxxWxxBxxxxxBxxx";
        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
        String startingBoard = reader.readLine();
        reader.close();
        int depth = Integer.parseInt(args[1]);
        ArrayList<Character> ip = new ArrayList<>();
        for (char c : startingBoard.toCharArray())
            ip.add(c);
        BoardOut op = ABPruning(new PosList(ip), depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE);

        String result = "Board Position: "+op.list.toString()+"\nPositions evaluated by static estimation: "+op.count
                +"\nαβ estimate: "+op.val+"\ndepth: "+depth;
        BufferedWriter writer = new BufferedWriter(new FileWriter(args[2]));
        writer.write(result);
        writer.close();

//        System.out.println("Minimax val = "+op.val+" Minimax count = "+op.count+" Minimax position = "+op.list.toString());
    }
}
