package iota;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manager class for an Iota game
 *
 * @author Michael Albert
 */
public class Manager {

    private ArrayList<PlayedCard> board = new ArrayList<>();
    private int playerNumber = 0;
    private HashMap<Player, ArrayList<Card>> hands = new HashMap<>();
    private HashMap<Player, Integer> score = new HashMap<>();
    private Deck deck;
    private int passCounter = 0;
    private int drawCounter = 0;
    boolean gameOver = false;
    private boolean gameStarted = false;
    ArrayList<Player> players = new ArrayList<>();
    private TreeMap<String, Integer> winRecord = new TreeMap<>();

    public Manager() {
    }

    private void addPlayer(Player player) {
        System.err.flush();
        System.out.flush();
        System.out.println(player.getName());

        this.players.add(player);
    }

    void addPlayers(Player... players) {
        for (Player player :
                players) {
            addPlayer(player);
            winRecord.put(player.getName(), 0);
        }
    }

    /**
     * Get (a copy of) the current state of the board.
     *
     * @return A copy of the board. Cards in the array list will be in the
     * order they were played.
     */
    public ArrayList<PlayedCard> getBoard() {
        ArrayList<PlayedCard> result = new ArrayList<>();
        for (PlayedCard c : board) {
            result.add(c.copy());
        }
        return result;
    }

    /**
     * Return the hand of the given player.
     *
     * @param p The player.
     * @return The hand of the given player.
     */
    public ArrayList<Card> getHand(Player p) {
        ArrayList<Card> handCopy = new ArrayList<>();
        for (Card c :
                hands.get(p)) {
            handCopy.add(new Card(c.colour, c.shape, c.value));
        }
        return handCopy;
    }

    /**
     * Compute the score of the given player.
     *
     * @param p the player.
     * @return The net score for the player.
     */

    public ArrayList<Integer> netScores(Player p) {
        ArrayList<Integer> scores = new ArrayList<>();
        for (Player opponent :
                players) {
            if (!opponent.equals(p))
                scores.add(score.get(p) - score.get(opponent));
        }
        return scores;
    }

    public int getRawScore(Player p) {
        return score.get(p);
    }

    /**
     * Compute the hand size of the player's opponent.
     *
     * @param p the player
     * @return the opponent's hand size.
     */
    public ArrayList<Integer> opponentsHandSize(Player p) {
        ArrayList<Integer> handSizes = new ArrayList<>();
        for (Player opponent :
                players) {
            if (!opponent.equals(p))
                handSizes.add(hands.get(opponent).size());
        }
        return handSizes;
    }

    private void dealHands() {
        for (Player player :
                players) {
            ArrayList<Card> h = new ArrayList<>();
            for (int i = 0; i < 4; i++) h.add(deck.dealCard());
            hands.put(player, h);
        }
    }

    private void seedBoard() {
        board.add(new PlayedCard(deck.dealCard(), null, 0, 0));
    }

    private void resetScores() {
        for (Player player :
                players) {
            score.put(player, 0);
        }
    }

    void setup() {
        deck = new Deck();
        board = new ArrayList<>();
        Collections.shuffle(players);
        resetScores();
        dealHands();
        seedBoard();
        playerNumber = 0;
        gameOver = false;
    }

    void printSummary() {
        System.out.println("Final Score");
        for (Player player :
                players) {
            System.out.println((player.getName() + "          ").substring(0, 10) + " " + score.get(player));
        }
    }

    public int getPlayerNumber(Player p) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) == p) {
                return i;
            }
        }
        return -1;
    }

    void play() {
        if (!gameStarted) {
            setup();
        }
        while (!gameOver) {
            step();
        }
    }

    private void deal_to_player(Player player) {
        ArrayList<Card> smallhand = hands.get(player);
        int sizediff = 4 - smallhand.size();
        for (int i = 0; i < sizediff; i++) {
            if (deck.hasCard())
                smallhand.add(deck.dealCard());
        }
    }

    private void discardStep(Player player, ArrayList<Card> discardPile) {
        deck.addCards(discardPile);
        hands.get(player).removeAll(discardPile);
    }

    private void playerStep(Player player) {
        ArrayList<PlayedCard> proposedMove = player.makeMove();
        if (proposedMove.isEmpty()) {
            // move empty, make player discard.
            drawCounter++;
            ArrayList<Card> discardPile = player.discard();
            HashSet<Card> d = new HashSet<>(discardPile);
            if (d.size() == discardPile.size() && hands.get(player).containsAll(d)) {
                discardStep(player, discardPile);
                deal_to_player(player);
            } else {
                passCounter++;
                System.err.println("Some cards are not in " + player.getName() + "'s hand. Failed to Discard");
            }
        } else {
            if (Utilities.isLegalMove(proposedMove, board)) {
                int moveScore = Utilities.scoreForMove(proposedMove, board);
                List cards = proposedMove.stream().map(i -> i.card).collect(Collectors.toList());
                if (hands.get(player).containsAll(cards)) {
                    int multiplier = 1;
                    hands.get(player).removeAll(cards);
                    if (!deck.hasCard() && hands.get(player).isEmpty()) {
                        multiplier = 2;
                        gameOver = true;
                    }
                    score.replace(player, score.get(player) + multiplier * moveScore);
                    deal_to_player(player);
                    for (PlayedCard pc : proposedMove)
                        board.add(new PlayedCard(pc.card, player, pc.x, pc.y));
                    passCounter = 0;
                    drawCounter = 0;
                } else {
                    passCounter++;
                    System.err.println("Some cards are not in " + player.getName() + "'s hand. Failed to Discard");
                    System.err.flush();
                }

            } else {
                passCounter++;
                System.err.println(player.getName() + " tried to play an illegal move.");
                System.err.flush();
            }
        }
        if (drawCounter >= 4 || passCounter >= 2) {
            System.err.println("Game over");
            System.err.flush();
            gameOver = true;
        }
    }

    void step() {
        if (!gameOver) {
            if (!gameStarted)
                gameStarted = true;
            Player current = players.get(playerNumber);
            playerStep(current);
            playerNumber = (playerNumber + 1) % players.size();
        }
        if (gameOver) {
            updateWinRecord();
        }
    }

    private void updateWinRecord() {
        ArrayList<Player> winners = new ArrayList<>();
        int highestScore = -1;
        for (Player p : players) {
            int currentPlayerScore = getRawScore(p);
            if (currentPlayerScore > highestScore)
                highestScore = currentPlayerScore;
        }
        for (Player p : players) {
            int currentPlayerScore = getRawScore(p);
            if (currentPlayerScore == highestScore)
                winners.add(p);
        }
        for (Player w : winners) {
            winRecord.put(w.getName(), winRecord.get(w.getName()) + 1);
        }
        if (winners.size() > 1) {
            System.err.print("Draw between players ");
        } else {
            System.err.print("Winner is ");
        }
        for (Player w : winners) {
            System.err.print(w.getName());
            if (!(winners.get(winners.size() - 1) == w))
                System.err.print(" and ");
        }
        System.err.println(".");
        System.err.flush();
    }

    public void printWinRecord() {
        System.err.flush();
        System.out.flush();
        for (Map.Entry currentWinRecord : winRecord.entrySet()) {
            System.out.println(currentWinRecord.getKey() + ": " + currentWinRecord.getValue());
        }
    }


}
