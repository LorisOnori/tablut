package player;

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
	private static final int EVAL = 0;
	private static final int DEPTH = 1;
	private static final int TEST_DEPTH = 5;
	
	//Posso svuotarla quando un pezzo viene mangiato
	private Map<Board, int[]> risultati;
	
	
	
	public Minmax() {
		risultati = new HashMap<>();
	}
	
	public void resetTranspositionTable() {
		this.risultati = new HashMap<>();
	}
	
	public Board iterative(Player player) {
		
		//Prendo il tempo adesso provo con profondità
		int depth = TEST_DEPTH;

		//Data una board prendo tutte le mosse possibili
		//Chiamo il minmax per ognuna di queste mosse così da ottenere la valutazione
		//salvo i risultati
		
		List<Mossa> mosse = board.getNextMovesByPlayer(player);
		//Attenzione al sort perchè calcolando la eval qui
		//rischio di sprecare tanto tempo
		
		//Uso un'eval più veloce
		//Adesso considero solo la differenza dei pezzi
		
		//Sort --> Controllo se salvando la eval completa sulla mappa con livallo 0 guadagno
		Collections.sort(mosse);
		if(Player.WHITE == player)
			Collections.reverse(mosse);
		
		int val; 
		int best = Player.WHITE == player ? MIN : MAX; 
		Board res = null;
		
		//Da parallelizzare e ottimizzare con l'ordinamento delle mosse e un db per i risultati parziali e finali
		for(int i= 0; i<depth; i++) { // -----> Cambiare la profondità con il tempo
			for(Mossa m : mosse){
				
				val = this.minmax(m.getBoardAggiornata(), i, MIN, MAX, player, i);
				
				if(player == Player.WHITE) {
					if(val > best ) {
						best = val;
						res = m.getBoardSenzaCatture();
					}
				}else { //BLACK
					if(val < best) {
						best = val;
						res = m.getBoardSenzaCatture();
					}
				}
			
				
			}
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
		
		return res;
		
	}
	
	public int minmax(Board brd, int depth,int  alpha, int beta, Player player, int mainDepth) {
	
		if(risultati.containsKey(brd) && risultati.get(brd)[DEPTH] >= depth)
			return risultati.get(brd)[EVAL];
		else if (depth == 0) {
			int ev =  Mossa.eval(brd);
			risultati.put(brd, new int[] {ev, mainDepth});
			return ev;
		}
		int value;
		//MAX
		if(player == Player.WHITE) {
			
			int bestVal = MIN; //Giocatore ha perso se non ci sono mosse (Non si entra nel ciclo sotto)
			
			//Ordinamento Figli senza instanziare ancora
			
			
			List<Mossa> mosse= brd.getNextMovesByPlayer(Player.WHITE);
			Collections.sort(mosse);
			Collections.reverse(mosse);
			for(Mossa m: mosse) {
				//Board b = m.calcolaCatture();
				value = minmax(m.getBoardAggiornata(), depth-1, alpha, beta, Player.BLACK, mainDepth);
				bestVal = Math.max(bestVal, value);
				alpha  = Math.max(alpha, bestVal);
				if(beta <= alpha)
					break;
			}
			return bestVal;
		}else {
			//BLACK MIN
			int bestVal = MAX;
			List<Mossa> mosse= brd.getNextMovesByPlayer(Player.WHITE);
			Collections.sort(mosse);
			for(Mossa m: mosse) {
				//Board b = m.calcolaCatture();
				value = minmax(m.getBoardAggiornata(), depth-1, alpha, beta, Player.WHITE, mainDepth);
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
