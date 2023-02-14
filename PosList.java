/**
 * represents the pieces of the board and its position using an array list
 * included are constructor and few helper methods
 */

import java.util.ArrayList;

public class PosList {
    public ArrayList<posType> posList;

    public PosList() {
        posList = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            posList.add(posType.x);
        }
    }

    public PosList(ArrayList<Character> ip) {
        posList = new ArrayList<>();
        for (char c : ip) {
            if (c == 'W')
                posList.add(posType.W);
            else if (c == 'B')
                posList.add(posType.B);
            else
                posList.add(posType.x);
        }
    }

    public PosList getCopy() {
        ArrayList<Character> arr = new ArrayList<>();
        for (posType pos: posList)
            arr.add(pos.val);
        return new PosList(arr);
    }

    public int getPieceCount(posType type) {
        int count = 0;
        for (posType t : posList) {
            if(t == type) count ++;
        }
        return count;
    }

    public PosList swap() {
        PosList swapped = new PosList();
        for (int i = 0; i < posList.size(); i++) {
            posType t = posList.get(i);
            if (t == posType.W)
                swapped.posList.set(i, posType.B);
            else if (t == posType.B)
                swapped.posList.set(i, posType.W);
        }
        return swapped;
    }

    public String toString() {
        char[] op = new char[posList.size()];
        for (int i = 0; i < posList.size(); i++) {
            op[i] = posList.get(i).val;
        }
        String out = new String(op);
        return out;
    }
}
