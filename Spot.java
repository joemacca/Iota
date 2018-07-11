package iota;

import java.util.ArrayList;

public class Spot{

	Spot n;
	Spot s;
	Spot w;
 	Spot e;

	PlayedCard pCard;
    int x;
    int y;

	public Spot(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void assignNeighbours(Spot n, Spot s, Spot w, Spot e) {
		this.n = n;
		this.s = s;
		this.w = w;
		this.e = e;
	}

	public void setCard(PlayedCard pCard) {
		this.pCard = pCard;
	}

	public boolean isEmpty() {
		return this.pCard == null;
	}

	public ArrayList<Spot> getNeighbours() {

		ArrayList<Spot> neighbours = new ArrayList<Spot>();
		if (n.isEmpty()) neighbours.add(n);
		if (s.isEmpty()) neighbours.add(s);
		if (e.isEmpty()) neighbours.add(e);
		if (w.isEmpty()) neighbours.add(w);
		return neighbours;

	}

}