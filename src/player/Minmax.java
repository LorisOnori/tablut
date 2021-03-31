package player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Minmax {

	//Minmax
	
	//Iterative deepening 
	
	//AlphaBeta pruning
	
	//Transposition Table
	
	private Board board;
	private static final int MAX = Integer.MAX_VALUE;
	private static final int MIN = Integer.MIN_VALUE;
	
	//Posso svuotarla quando un pezzo viene mangiato
	private Map<Board, Integer> risultati;
	
	
	
	public Minmax() {
		risultati = new HashMap<>();
	}
	
	
	public Board iterative(Player player) {
		
		//Prendo il tempo adesso provo con profondità
		int depth = 10;

		//Data una board prendo tutte le mosse possibili
		//Chiamo il minmax per ognuna di queste mosse così da ottenere la valutazione
		//salvo i risultati
		
		List<Mossa> mosse = board.getNextMovesByPlayer(player);
		List<Board> possibiliBoard = new ArrayList<>();
		for(Mossa m : mosse) {
			possibiliBoard.add(m.calcolaCatture());
			
			//Attenzione al sort perchè calcolando la eval qui
			//rischio di sprecare tanto tempo
			
			//Uso un'eval più veloce
			//Adesso considero solo la differenza dei pezzi
			
			//Sort 
			Collections.sort(possibiliBoard); //Inizio a valutare le scelte più promettenti
			if(Player.WHITE == player) {
				Collections.reverse(possibiliBoard);
			}
		}
		int []valori = new int[mosse.size()];
		int index = 0;
		
		//Da parallelizzare e ottimizzare con l'ordinamento delle mosse e un db per i risultati parziali e finali
		for(Board b : possibiliBoard) {
			if(this.risultati.containsKey(b)) {
				valori[index] = this.risultati.get(b);
			}else {
				for(int i = 1; i< depth; i++) {
					valori[index] = this.minmax(b, i, MIN, MAX, player);
				}
				
			}
			index++;
			
		}

		//Devo prendedere il massimo o il minimo diependentemente dal giocatore
		
//		int min = MAX;
//		int ind = 0;
//		for(int i = 0 ; i<valori.length; i++) {
//			//prendo il minimo
//			if(valori[i] < min) {
//				ind = i;
//			}
//		}
		
		//Sbagliato --> Devo ritornare la board prima di aver eliminato i pezzi (Quella contenuta in mossa)
		//Ordinando le board mi perdo la mossa originale
		//Posso ordinarle dentro mossa
		
		return possibiliBoard.get(ind);
		
	}
	
	public int minmax(Board brd, int depth,int  alpha, int beta, Player player) {
	
		if(risultati.containsKey(brd) && depth == 0)
			return risultati.get(brd);
		else if (depth == 0)
			return Mossa.eval(brd);
		
		int value;
		//MAX
		if(player == Player.WHITE) {
			
			int bestVal = MIN;
			
			//Ordinamento Figli
			
			
			//
			for(Mossa m: brd.getNextMovesByPlayer(Player.WHITE)) {
				Board b = m.calcolaCatture();
				value = minmax(b, depth-1, alpha, beta, Player.BLACK);
				bestVal = Math.max(bestVal, value);
				alpha  = Math.max(alpha, bestVal);
				if(beta <= alpha)
					break;
			}
			return bestVal;
		}else {
			//BLACK MIN
			int bestVal = MAX;
			for(Mossa m: brd.getNextMovesByPlayer(Player.BLACK)) {
				Board b = m.calcolaCatture();
				value = minmax(b, depth-1, alpha, beta, Player.WHITE);
				bestVal = Math.max(bestVal, value);
				beta  = Math.max(beta, bestVal);
				if(beta <= alpha)
					break;
			}
			return bestVal;
			
		}
	
	
	
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//
}
