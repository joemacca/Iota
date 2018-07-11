package iota;

import java.util.ArrayList;

public class Dave extends Player{

    private String name;

    public Dave(Manager m, String name) {
        super(m);

        this.name = name;
    }

    @Override
    public ArrayList<PlayedCard> makeMove() {
        int minX=1000;
        int maxX=-1000;
        int minY=1000;
        int maxY=-1000;
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
            for (int i = minX-1; i< maxX+1;i++){
                for (int j = minY-1; j < maxY+1; j++) {
                    ArrayList<PlayedCard> move = new ArrayList<>();
                    move.add(new PlayedCard(card, this, i, j));
                    if (Utilities.isLegalMove(move, board))
                        return move;
                }
            }
        }
        return Player.PASS;
    }

    @Override
    public ArrayList<Card> discard() {
        return m.getHand(this);
    }

    @Override
    public String getName() {
        return name;
    }
}
