package iota;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Derrick extends Player{

    public Derrick(Manager m) {
        super(m);
    }

    @Override
    public ArrayList<PlayedCard> makeMove() {
        int minX=1000;
        int maxX=-1000;
        int minY=1000;
        int maxY=-1000;
        int bestMoveScore = -1;
        ArrayList<PlayedCard> bestMove = new ArrayList<>();

        ArrayList<PlayedCard> board = m.getBoard();
        for (PlayedCard pc: board) {
            if (pc.x < minX)
                minX = pc.x;
            if (pc.x > maxX)
                maxX = pc.x;
            if (pc.y < minY)
                minY = pc.y;
            if (pc.y > maxY)
                maxY = pc.y;
        }
        for (Card card: m.getHand(this)){
            for (int i = maxX+1 ; i > minX-2; i--){
                for (int j = maxY+1 ; j > minY-2; j--) {
                    ArrayList<PlayedCard> move = new ArrayList<>();
                    move.add(new PlayedCard(card, this, i, j));
                    int moveScore = Utilities.scoreForMove(move, board);
                    if (moveScore > bestMoveScore){
                        bestMove = move;
                        bestMoveScore = moveScore;
                    }

                }
            }
        }
        if (bestMoveScore > 0){
            return bestMove;
        }
        return Player.PASS;
    }

    @Override
    public ArrayList<Card> discard() {
        ArrayList<Card> discardPile = new ArrayList<>(m.getHand(this));
        Collections.shuffle(discardPile);
        return discardPile;
    }

    @Override
    public String getName() {
        return "Derrick";
    }
}
