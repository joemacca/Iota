package iota;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class FalveyPlayer extends Player {
	
    private String name;

	private ArrayList<PlayedCard> boardCopy;
	private ArrayList<Card> hand;

	public FalveyPlayer(Manager m, String name) {
		
        super(m);
        this.name = name;
	}

	/**
     * Make a move as requested by the manager. Note that as a player you
     * will be able to query the manager about the current state of the board
     * and your hand.
     * 
     * If the move returned is invalid, you will be deemed to have passed 
     * (i.e., done nothing).
     * 
     * @return The move you intend to make.
     * 
     */
    public ArrayList<PlayedCard> makeMove() {

        hand = m.getHand(this);   // obtain new hand.
        boardCopy = m.getBoard(); // get current state of the board.

        BoardGraph b = new BoardGraph(this, boardCopy, hand); // generate state representation.
        HashSet<ArrayList<PlayedCard>> moves = b.getAvailableMoves();
        HashSet<ArrayList<PlayedCard>> copyOfMoves = (HashSet<ArrayList<PlayedCard>>) moves.clone();
        ArrayList<PlayedCard> bestMove = new ArrayList<PlayedCard>();
        int maxScore = 0;

        if(moves.size() == 0) {
            
            System.out.println("Hand Discarded");
            return new ArrayList<PlayedCard>();
        }

        for(ArrayList<PlayedCard> move : moves) {

            int score = Utilities.scoreForMove(move, boardCopy);
         //   System.out.println(move);
            if(score > maxScore) {
                maxScore = score;
                bestMove = move;
            } 
        }

        for(ArrayList<PlayedCard> moveOne : copyOfMoves) {

            for(ArrayList<PlayedCard> moveTwo : copyOfMoves) {
                ArrayList<PlayedCard> combinedMove = new ArrayList<PlayedCard>();

                combinedMove.addAll(moveOne);
                combinedMove.addAll(moveTwo);

                int score = Utilities.scoreForMove(combinedMove, boardCopy);

                if(score != -1) {
                    moves.add(combinedMove);
                }
            }
        }

        for(ArrayList<PlayedCard> move : moves) {

            int score = Utilities.scoreForMove(move, boardCopy);
         //   System.out.println(move);
            if(score > maxScore) {
                maxScore = score;
                bestMove = move;
            } 
        }
        return bestMove; // return all available moves.
    }

    /**
     * Announce your name.
     * 
     * @return The name of this player.
     */
    public ArrayList<Card> discard() {
        return hand;
    }
    
    /**
     * Announce your name.
     * 
     * @return The name of this player.
     */
    public String getName() {
    	return name;
    }

    public void printHand() {

    	String cards = "";
    	for(Card c : hand) {
    		cards += c.colour;
    		cards += c.shape;
    		cards += c.value;
    		cards += " ";
    	}

    	System.out.println(cards);
    }
}