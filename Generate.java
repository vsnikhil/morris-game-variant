/**
 * defines move generator functions in opening, midgame/endgame phase
 * includes static estimation functions for opening and mid/end game phase
 * few helper methods for neighbors and checking mills
 */

import java.util.ArrayList;
import java.util.List;

public class Generate {

    public static ArrayList<PosList> generateAdd(PosList board) {
        ArrayList<PosList> list = new ArrayList<>();
        for (int i = 0; i < board.posList.size(); i++) {
            if (board.posList.get(i) == posType.x) {
                PosList copy = board.getCopy();
                copy.posList.set(i, posType.W);
                if (isCloseMill(i, copy))
                    list = generateRemove(copy, list);
                else
                    list.add(copy);
            }
        }
        return list;
    }

    private static ArrayList<PosList> generateRemove(PosList board, ArrayList<PosList> l) {
        int count = 0;
        for (int i = 0; i < board.posList.size(); i++) {
            if (board.posList.get(i) == posType.B) {
                if (!isCloseMill(i, board)) {
                    PosList copy = board.getCopy();
                    copy.posList.set(i, posType.x);
                    l.add(copy);
                    count++;
                }
            }
        }
        // when all the black pieces are part of mills nothing can be removed
        if (count == 0)
            l.add(board);
        return l;
    }

    public static ArrayList<PosList> generateHopping(PosList board) {
        ArrayList<PosList> list = new ArrayList<>();
        for (int i = 0; i < board.posList.size(); i++) {
            if (board.posList.get(i) == posType.W) {
                for (int j = 0; j < board.posList.size(); j++) {
                    if (board.posList.get(j) == posType.x) {
                        PosList copy = board.getCopy();
                        copy.posList.set(i, posType.x);
                        copy.posList.set(j, posType.W);
                        if (isCloseMill(j, copy))
                            generateRemove(copy, list);
                        else list.add(copy);
                    }
                }
            }
        }
        return list;
    }

    public static ArrayList<PosList> generateMove(PosList board) {
        ArrayList<PosList> list = new ArrayList<>();
        for (int i = 0; i < board.posList.size(); i++) {
            if (board.posList.get(i) == posType.W) {
                List<Integer> n = getNeighbors(i);
                for (int j : n) {
                    if (board.posList.get(j) == posType.x) {
                        PosList copy = board.getCopy();
                        copy.posList.set(i, posType.x);
                        copy.posList.set(j, posType.W);
                        if (isCloseMill(j, copy))
                            generateRemove(copy, list);
                        else list.add(copy);
                    }
                }
            }
        }

        return list;
    }

    public static ArrayList<PosList> generateMovesOpening(PosList board) {
        return generateAdd(board);
    }

    public static int staticEstimationForOpening(PosList board) {
        return board.getPieceCount(posType.W) - board.getPieceCount(posType.B);
    }

    public static ArrayList<PosList> generateMovesMidgameEndgame(PosList board) {
        if (board.getPieceCount(posType.W) == 3)
            return generateHopping(board);
        else
            return generateMove(board);
    }

    public static int staticEstimationMidGameEndGame(PosList board) {
        int whiteNum = board.getPieceCount(posType.W);
        int blackNum = board.getPieceCount(posType.B);
        ArrayList<PosList> list = generateMovesMidgameEndgameForBlack(board);
        int numBlackMoves = list.size();
        if (blackNum <= 2) return 10000;
        else if (whiteNum <= 2) return -10000;
        else if (numBlackMoves == 0) return 10000;
        else return 1000*(whiteNum - blackNum) - numBlackMoves;
    }

    public static ArrayList<PosList> generateMovesMidgameEndgameForBlack(PosList board) {
        PosList tempBoard = board.swap();
        ArrayList<PosList> bMoves = generateMovesMidgameEndgame(tempBoard);
        for (int i = 0; i < bMoves.size(); i++) {
            PosList move = bMoves.get(i);
            bMoves.set(i, move.swap());
        }
        return bMoves;
    }

    public static ArrayList<PosList> generateMovesOpeningForBlack(PosList board) {
        PosList tempBoard = board.swap();
        ArrayList<PosList> bMoves = generateMovesOpening(tempBoard);
        for (int i = 0; i < bMoves.size(); i++) {
            PosList move = bMoves.get(i);
            bMoves.set(i, move.swap());
        }
        return bMoves;
    }

    // static estimation function is improved by considering the possible mill counts in future
    // so the new static estimation function becomes
    // 1000*(whiteNum + possibleWhiteMills - blackNum) - numOfBlackMoves in midgame/endgame phase
    // whiteNum + possibleWhiteMills - blackNum for opening phase of the game

    public static int staticEstimationOpeningImproved(PosList board) {
        return board.getPieceCount(posType.W) + possibleMillCount(board, posType.W) - board.getPieceCount(posType.B);
    }

    public static int staticEstimationMidGameEndGameImproved(PosList board) {
        int whiteNum = board.getPieceCount(posType.W);
        int blackNum = board.getPieceCount(posType.B);
        ArrayList<PosList> list = generateMovesMidgameEndgameForBlack(board);
        int numBlackMoves = list.size();
        int possibleMillCount = possibleMillCount(board, posType.W);
        if (blackNum <= 2) return 10000;
        else if (whiteNum <= 2) return -10000;
        else if (numBlackMoves == 0) return 10000;
        else return 1000*(whiteNum + possibleMillCount - blackNum) - numBlackMoves;
    }

    public static boolean checkPossibleMill = false;

    public static int possibleMillCount(PosList board, posType t) {
        int count = 0;
        for (int i = 0; i < board.posList.size(); i++) {
            posType p = board.posList.get(i);
            if (p == t) {
                checkPossibleMill = true;
                if (checkForMills(board, p , i))
                    count++;
            }
        }
        return count;
    }

    private static List<Integer> getNeighbors(int i) {
        switch (i) {
            case 0:
                return new ArrayList<>(List.of(1, 2, 15));
            case 1:
                return new ArrayList<>(List.of(0, 3, 8));
            case 2:
                return new ArrayList<>(List.of(0, 3, 4, 13));
            case 3:
                return new ArrayList<>(List.of(1, 2, 5, 7));
            case 4:
                return new ArrayList<>(List.of(2, 5, 9));
            case 5:
                return new ArrayList<>(List.of(3, 4, 6));
            case 6:
                return new ArrayList<>(List.of(5, 7, 11));
            case 7:
                return new ArrayList<>(List.of(3, 6, 8, 14));
            case 8:
                return new ArrayList<>(List.of(1, 7, 17));
            case 9:
                return new ArrayList<>(List.of(4, 10, 12));
            case 10:
                return new ArrayList<>(List.of(9, 11, 13));
            case 11:
                return new ArrayList<>(List.of(6, 10, 14));
            case 12:
                return new ArrayList<>(List.of(2, 9, 13, 15));
            case 13:
                return new ArrayList<>(List.of(10, 12, 14, 16));
            case 14:
                return new ArrayList<>(List.of(7, 11, 13, 17));
            case 15:
                return new ArrayList<>(List.of(0, 12, 16));
            case 16:
                return new ArrayList<>(List.of(13, 15, 17));
            case 17:
                return new ArrayList<>(List.of(8, 14, 16));
            default:
                return new ArrayList<>();
        }
    }

    private static boolean isCloseMill(int i, PosList b) {
        posType t = b.posList.get(i);
        if (t == posType.x)
            return false;
        else return checkForMills(b, t, i);
    }

    private static boolean checkForMills(PosList b, posType t, int i) {
        switch (i) {
            case 0:
                return isAMill(b, t, 2, 4);
            case 1:
                return isAMill(b, t, 3, 5) || isAMill(b, t, 8, 17);
            case 2:
                return isAMill(b, t, 0, 4);
            case 3:
                return isAMill(b, t, 1, 5) || isAMill(b, t, 7, 14);
            case 4:
                return isAMill(b, t, 0, 2);
            case 5:
                return isAMill(b, t, 1, 3) || isAMill(b, t, 6, 11);
            case 6:
                return isAMill(b, t, 5, 11) || isAMill(b, t, 7, 8);
            case 7:
                return isAMill(b, t, 6, 8) || isAMill(b, t, 3, 14);
            case 8:
                return isAMill(b, t, 6, 7) || isAMill(b, t, 1, 17);
            case 9:
                return isAMill(b, t, 10, 11) || isAMill(b, t, 12, 15);
            case 10:
                return isAMill(b, t, 9, 11) || isAMill(b, t, 13, 16);
            case 11:
                return isAMill(b, t, 9, 10) || isAMill(b, t, 14, 17) || isAMill(b, t, 5, 6);
            case 12:
                return isAMill(b, t, 13, 14) || isAMill(b, t, 9, 15);
            case 13:
                return isAMill(b, t, 12, 14) || isAMill(b, t, 10, 16);
            case 14:
                return isAMill(b, t, 12, 13) || isAMill(b, t, 11, 17) || isAMill(b, t, 3, 7);
            case 15:
                return isAMill(b, t, 16, 17) || isAMill(b, t, 9, 12);
            case 16:
                return isAMill(b, t, 15, 17) || isAMill(b, t, 10, 13);
            case 17:
                return isAMill(b, t, 15, 16) || isAMill(b, t, 11, 14) || isAMill(b, t, 1, 8);
            default:
                return false;
        }
    }

    private static boolean isAMill(PosList b, posType t, int i, int i1) {
        if (checkPossibleMill) {
            checkPossibleMill = false;
            return (b.posList.get(i) == posType.x || b.posList.get(i) == t) &&
                    (b.posList.get(i1) == posType.x || b.posList.get(i1) == t);
        }
        return (b.posList.get(i) == t) && (b.posList.get(i1) == t);
    }
}
