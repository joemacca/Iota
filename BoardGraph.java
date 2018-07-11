package iota;

import java.util.HashSet;
import java.util.ArrayList;

public class BoardGraph {

	private static final int SIZE = 65;

	private Spot[][] matrix;

	private ArrayList<ArrayList<Spot>> availableMoves;
	private HashSet<ArrayList<PlayedCard>> allMoves;

	public BoardGraph(Player p, ArrayList<PlayedCard> cards, ArrayList<Card> hand) {

		this.allMoves 		= new HashSet<ArrayList<PlayedCard>>();
		this.availableMoves = new ArrayList<ArrayList<Spot>>();
		this.matrix 		= new Spot[SIZE][SIZE];

		// initialize all spots in adjacency matrix.
		for (int col = 0; col < SIZE; col++) {
			for (int row = 0; row < SIZE; row++) {
				matrix[col][row] = new Spot(indexToCoord(row), indexToCoord(col));
			}
		}

		// assign all played cards into matrix.
		for (PlayedCard card : cards) {
			addToMatrix(card);
		} 

		// assign all existing neighbours.
		for (int col = 1; col < SIZE-1; col++) {
			for (int row = 1; row < SIZE-1; row++) {
					Spot n = matrix[col-1][row];
					Spot s = matrix[col+1][row];
					Spot w = matrix[col][row-1];
					Spot e = matrix[col][row+1]; 
					matrix[col][row].assignNeighbours(n, s, w, e);
			}
		}

		// find all possible moves.
		computeAvailableMoves(p, cards, hand);
	}

	private void addToMatrix(PlayedCard card){
		int x, y;
		x = card.x;
		y = card.y;

		// sets a played card in the matrix.
		matrix[coordToIndex(y)][coordToIndex(x)].setCard(card);
	}
	
	/**
	* Uses given state of board, and passed hand. Computes available moves,
	* and stores it in availableMoves.
	*/
	private void computeAvailableMoves(Player p, ArrayList<PlayedCard> board, ArrayList<Card> hand) {
		
		int max = 0;

		// for all played cards on the board.
		for (PlayedCard c : board) {
			int x = c.x;
			int y = c.y;

			Spot s = matrix[coordToIndex(y)][coordToIndex(x)];
			ArrayList<Spot> neighbours = s.getNeighbours();

			// for all neighbours of the played card, try place a card in neighbouring space.
			for (Spot neighbour : neighbours) {

				int nx = neighbour.x;
				int ny = neighbour.y;

				// permute through all possible combinations in hand.
				for(Card hc : hand) {
					
					ArrayList<Card> remainingCards = (ArrayList<Card>) hand.clone();
					remainingCards.remove(hc);

					ArrayList<PlayedCard> possibleMoveSet = new ArrayList<PlayedCard>();
					possibleMoveSet.add(new PlayedCard(hc, p, nx, ny));
					
					if(Utilities.isLegalMove(possibleMoveSet, board)) {

						ArrayList<PlayedCard> newPossibleMoveSet = (ArrayList<PlayedCard>) possibleMoveSet.clone();
						allMoves.add(newPossibleMoveSet);
						search(remainingCards, neighbour, possibleMoveSet, board, p);
					}
				}
			}
		}
	}
	
	private void search(ArrayList<Card> remainingHandCards, Spot recentlyPlayedSpot, ArrayList<PlayedCard> possibleMoveSet, ArrayList<PlayedCard> board, Player p) {

		if(remainingHandCards.size() != 0) {
			// search the north spot of the played card.
			ArrayList<PlayedCard> possibleSubsetsN = (ArrayList<PlayedCard>) possibleMoveSet.clone();
			searchN(remainingHandCards, recentlyPlayedSpot.n, possibleSubsetsN, board, p);

			// search the south spot of the played card.
			ArrayList<PlayedCard> possibleSubsetsS = (ArrayList<PlayedCard>) possibleMoveSet.clone();
			searchS(remainingHandCards, recentlyPlayedSpot.s, possibleSubsetsS, board, p);
			
			// search the east spot of the played card.
			ArrayList<PlayedCard> possibleSubsetsE = (ArrayList<PlayedCard>) possibleMoveSet.clone();
			searchE(remainingHandCards, recentlyPlayedSpot.e, possibleSubsetsE, board, p);

			// search the west spot of the played card.
			ArrayList<PlayedCard> possibleSubsetsW = (ArrayList<PlayedCard>) possibleMoveSet.clone();
			searchW(remainingHandCards, recentlyPlayedSpot.w, possibleSubsetsW, board, p);
		}
	}

	/**
	*	For all remaining cards in hard, recursively search and determine 
		if we can play cards in a row given a first valid card placement in the north direction.
	*/
	private void searchN(ArrayList<Card> remainingHandCards, Spot recentlyPlayedSpot, ArrayList<PlayedCard> possibleMoveSet,  ArrayList<PlayedCard> board, Player p) {
		
		for(Card c : remainingHandCards) {

			int x = recentlyPlayedSpot.x;
			int y = recentlyPlayedSpot.y; 
			
			PlayedCard newCard = new PlayedCard(c, p, x, y);
			
			possibleMoveSet.add(newCard);
			if (Utilities.isLegalMove(possibleMoveSet, board)) {
			
				ArrayList<Card> newRemainingCards = (ArrayList<Card>) remainingHandCards.clone();
				ArrayList<PlayedCard> newPossibleMoveSet = (ArrayList<PlayedCard>) possibleMoveSet.clone();
				newRemainingCards.remove(c);
			
				allMoves.add(newPossibleMoveSet);
			
				searchN(newRemainingCards, recentlyPlayedSpot.n, newPossibleMoveSet, board, p);
			} 
			possibleMoveSet.remove(newCard);
		}
	}

	/**
	*	For all remaining cards in hard, recursively search and determine 
		if we can play cards in a row given a first valid card placement in the south direction.
	*/
	private void searchS(ArrayList<Card> remainingHandCards, Spot recentlyPlayedSpot, ArrayList<PlayedCard> possibleMoveSet,  ArrayList<PlayedCard> board, Player p) {
		
		for(Card c : remainingHandCards) {
			int x = recentlyPlayedSpot.x;
			int y = recentlyPlayedSpot.y; 
			PlayedCard newCard = new PlayedCard(c, p, x, y);
			possibleMoveSet.add(newCard);
			if (Utilities.isLegalMove(possibleMoveSet, board)) {
				ArrayList<Card> newRemainingCards = (ArrayList<Card>) remainingHandCards.clone();
				ArrayList<PlayedCard> newPossibleMoveSet = (ArrayList<PlayedCard>) possibleMoveSet.clone();
				newRemainingCards.remove(c);
				allMoves.add(newPossibleMoveSet);
				searchN(newRemainingCards, recentlyPlayedSpot.s, newPossibleMoveSet, board, p);
			} 
			possibleMoveSet.remove(newCard);
		}
	}

	/**
	*	For all remaining cards in hard, recursively search and determine 
		if we can play cards in a row given a first valid card placement in the east direction.
	*/
	private void searchE(ArrayList<Card> remainingHandCards, Spot recentlyPlayedSpot, ArrayList<PlayedCard> possibleMoveSet,  ArrayList<PlayedCard> board, Player p) {
		
		for(Card c : remainingHandCards) {
			int x = recentlyPlayedSpot.x;
			int y = recentlyPlayedSpot.y; 
			PlayedCard newCard = new PlayedCard(c, p, x, y);
			possibleMoveSet.add(newCard);
			if (Utilities.isLegalMove(possibleMoveSet, board)) {
				ArrayList<Card> newRemainingCards = (ArrayList<Card>) remainingHandCards.clone();
				ArrayList<PlayedCard> newPossibleMoveSet = (ArrayList<PlayedCard>) possibleMoveSet.clone();
				newRemainingCards.remove(c);
				allMoves.add(newPossibleMoveSet);
				searchN(newRemainingCards, recentlyPlayedSpot.e, newPossibleMoveSet, board, p);
			} 
			possibleMoveSet.remove(newCard);
		}
	}

	/**
	*	For all remaining cards in hard, recursively search and determine 
		if we can play cards in a row given a first valid card placement in the west direction.
	*/
	private void searchW(ArrayList<Card> remainingHandCards, Spot recentlyPlayedSpot, ArrayList<PlayedCard> possibleMoveSet,  ArrayList<PlayedCard> board, Player p) {
		
		for(Card c : remainingHandCards) {
			int x = recentlyPlayedSpot.x;
			int y = recentlyPlayedSpot.y; 
			PlayedCard newCard = new PlayedCard(c, p, x, y);
			possibleMoveSet.add(newCard);
			if (Utilities.isLegalMove(possibleMoveSet, board)) {
				ArrayList<Card> newRemainingCards = (ArrayList<Card>) remainingHandCards.clone();
				ArrayList<PlayedCard> newPossibleMoveSet = (ArrayList<PlayedCard>) possibleMoveSet.clone();
				newRemainingCards.remove(c);
				allMoves.add(newPossibleMoveSet);
				searchN(newRemainingCards, recentlyPlayedSpot.w, newPossibleMoveSet, board, p);
			} 
			possibleMoveSet.remove(newCard);
		}
	}

	/**
	* Converts coordinate system to index.
	*/
	private int coordToIndex(int coord) { 
		return (SIZE/2)+coord; 
	}
	
	/**
	* Converts index to coordinate system.
	*/
	private int indexToCoord(int index) { 
		return index-(SIZE/2); 
	}

	/**
	* Returns all valid moves given the current state of the board and hand.
	*/
	public HashSet<ArrayList<PlayedCard>> getAvailableMoves() {


		return allMoves;
	}

	public void printMatrix(){
		/* Matrix prints left to right then descends.*/
		for(int cols = 0; cols < matrix.length; cols++){
			//System.out.print(matrix[i][i]);
			for(int rows = 0; rows < matrix.length; rows++){
				if(matrix[cols][rows].isEmpty()){
					System.out.printf("%4d", 0);
				} else {
					System.out.printf("%4d", 1);
				}
			}
			System.out.println();
		}
		System.out.println();
	}
}