package it.unibo.ai.didattica.competition.tablut.AI;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class Minmax {

	//Minmax
	
	//Iterative deepening 
	
	//AlphaBeta pruning
	
	//Transposition Table
	
	private Board board;
	private static final int MAX = Integer.MAX_VALUE;
	private static final int MIN = Integer.MIN_VALUE;
	private static final int EVAL = 0;
	private static final int DEPTH_INDEX = 1;
	private static final int TEST_DEPTH = 3;
	private static Minmax INSTANCE = null;
	private static final int NUMERO_TORRI_BIANCHE = 8;
	private static final int NUMERO_TORRI_NERE = 16;
	//Posso svuotarla quando un pezzo viene mangiato
	private static Map<Board, int[]> traspositionTable;
	private static Map<Board, Integer> posizioniPassate;
	private static int torriBianche = Minmax.NUMERO_TORRI_BIANCHE;
	private static int torriNere = Minmax.NUMERO_TORRI_NERE;
	
	// HEURISTIC

	private static final int W_DIFFERENZA_PEZZI = 20;
	private static final int W_DIREZIONI_RE = 2;
	private static final int W_POSIZIONI_OCC_RE = 5;
	private static final int W_DISTANZA_GOAL = 3;
	private static final int W_POSIZIONI_OCCUPABILI_TORRI_BIANCO = 4;
	private static final int W_POSIZIONI_OCCUPABILI_TORRI_NERO = 4;
	private static final int W_DIREZIONI_TORRI_BIANCO = 4;
	private static final int W_DIREZIONI_TORRI_NERO = 4;
	private static final int W_BLACK_AROUND_KING = 5;
	
	
	
	
	public static Minmax getInstance() {
		if(INSTANCE == null)
			return new Minmax();
		else 
			return INSTANCE;
	}
	
	private Minmax() {
		traspositionTable = new HashMap<>();
		posizioniPassate = new HashMap<>();
	}
	
	public static void resetTranspositionTable() {
		traspositionTable = new HashMap<>();
	}
	
	public static void resetPosizioniPassate() {
		posizioniPassate = new HashMap<>();
	}
	
	public  Mossa iterative(Board b, Turn t) {
		if(t == Turn.BLACK)
			return iterative(b, Player.BLACK);
		else 
			return iterative(b, Player.WHITE);
	}
	
	public Mossa iterative(Board board, Player player) {
		
		int tb = board.getWhiteRooksCount();
		int tn = board.getBlackRooksCount();
		
		//Se catturo un pezzo allora le nuove posizioni saranno sicuramente diverse
		//Così come quelle valutate
		//Libero la memoria
		if(tb != torriBianche || tn != torriNere) {
			resetPosizioniPassate();
			resetTranspositionTable();
			torriBianche = tb;
			torriNere = tn;
		}
		
		posizioniPassate.put(board, 1);
		
		
		//Prendo il tempo adesso provo con profonditï¿½
		int depth = TEST_DEPTH;

		//Data una board prendo tutte le mosse possibili
		//Chiamo il minmax per ognuna di queste mosse cosï¿½ da ottenere la valutazione
		//salvo i risultati
		
		List<Mossa> mosse = board.getNextMovesByPlayer(player);
		//Attenzione al sort perchï¿½ calcolando la eval qui
		//rischio di sprecare tanto tempo
		
		//Uso un'eval piï¿½ veloce
		//Adesso considero solo la differenza dei pezzi
		
		//Sort --> Controllo se salvando la eval completa sulla mappa con livallo 0 guadagno
		//System.out.println();
		Collections.sort(mosse);
		if(Player.WHITE == player)
			Collections.reverse(mosse);
		
		int val; 
		int best = Player.WHITE == player ? MIN : MAX; 
		Mossa res = null;
		
		//Da parallelizzare e ottimizzare con l'ordinamento delle mosse e un db per i risultati parziali e finali
		for(int i= 0; i<depth; i++) { // -----> Cambiare la profonditï¿½ con il tempo
			System.out.println("Iterative depth "+i);
			for(Mossa m : mosse){
				
				val = minmax(m.getBoardAggiornata(), i, MIN, MAX, player, i);
				
				if(player == Player.WHITE) {
					if(val > best ) {
						best = val;
						res = m;
					}
				}else { //BLACK
					if(val < best) {
						best = val;
						res = m;
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
		//System.out.println(depth);
		if(traspositionTable.containsKey(brd) && traspositionTable.get(brd)[DEPTH_INDEX] >= depth)
			return traspositionTable.get(brd)[EVAL];
		else if (depth == 0) {
			int ev =  eval(brd);
			traspositionTable.put(brd, new int[] {ev, mainDepth});
			return ev;
		}
		int value;
		//MAX
		if(player == Player.WHITE) {
			
			int bestVal = MIN; //Giocatore ha perso se non ci sono mosse (Non si entra nel ciclo sotto)
			
			//Ordinamento Figli senza instanziare ancora
			
			
			List<Mossa> mosse= brd.getNextMovesByPlayer(Player.WHITE);
			//nessuna mossa possibile --> NERO vince
			if(mosse == null)
				return MIN;
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
			List<Mossa> mosse= brd.getNextMovesByPlayer(Player.BLACK);
			//Se mosse == null --> Non si può fare nessuna mossa quindi NERO perde
			if(mosse == null)
				return MAX;
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
	
	//Test

		
	//Nella genereazione di mosse (non qui) posso valutare tutte le mosse e ordinarle in base alla differenza pezzi che dovrebbe essere l'indice piï¿½ importate
	//Minmax quindi esplora prima queste mosse e poi le altre.
	//Valuta la posizione corrente
	public int eval(Board brd) {
		//TODO 
		
		//WHITE WIN
		for(int []p : Board.finalCellsSet) {
			if(brd.kindPosition == Board.coordinateToIndex(p[0], p[1]))
				return MAX;
		}
		//BLACK WIN
		
		//ultimo controllo se:
		//f k b
		//b k f
		//e dall'alto in basso
		int []k = Board.indexToCoordinate(brd.kindPosition);
		if(brd.blackAroundKing() == 4)
			return MIN;
		else if(brd.blackAroundKing() == 3 && (brd.kindPosition == Board.NORD_CENTER_POS || brd.kindPosition == Board.EST_CENTER_POS || brd.kindPosition == Board.SUD_CENTER_POS || brd.kindPosition == Board.OVEST_CENTER_POS))
			return MIN;
		else if(k[0] > 1 && brd.getCell(k[0]+1, k[1]).getType() == Type.BLACK_ROOK && Board.isForbidden(k[0]-1, k[1])  
				||  k[0] < 9 && brd.getCell(k[0]-1, k[1]).getType() == Type.BLACK_ROOK && Board.isForbidden(k[0]+1, k[1]) 
				||  k[1] > 1 && brd.getCell(k[0], k[1]+1).getType() == Type.BLACK_ROOK && Board.isForbidden(k[0], k[1]-1)  
				||  k[1] < 9 && brd.getCell(k[0], k[1]-1).getType() == Type.BLACK_ROOK && Board.isForbidden(k[0], k[1]+1) ){
			return MIN;
		}
			
			//DRAW
		if(posizioniPassate.containsKey(brd))
			return 0;
		
		
		
		
		
		/*
		 * 
		 * Una volta scelta una mossa controllo le catture possibili
		 * Quindi valuto questa seconda tabella con le catture o vincita di bianco o nero
		 * Quindi mando lo stato
		 * Quindi ricevo lo stato aggiornato senza la pedina catturata
		 */
		
		
		
		
		/*
		 * Numero di pezzi
		 * Direzioni del re
		 * Posizioni occupabili dal re
		 * Distanza del re dal goal
		 * Posizioni occupabili dalle torri
		 * Direzioni in cui le torri si possono muovere
		 */
		
		
		int []rooks = brd.getRooksByPlayer(Player.WHITE);
		int dirWhite = 0;
		int dirBlack = 0;
		for(int r : rooks) {
			dirWhite += brd.numeroDirezioniRook(r);
		}
		
		rooks = brd.getRooksByPlayer(Player.BLACK);
		for(int r : rooks) {
			dirBlack += brd.numeroDirezioniRook(r);
		}
		
		return W_DIFFERENZA_PEZZI * brd.rookDifferenceCount() + W_DIREZIONI_RE * brd.numeroDirezioniRe() + W_POSIZIONI_OCC_RE * brd.numeroCaselleDiponibiliKing() 
				+ W_DISTANZA_GOAL * brd.manhattamDistanceToClosestGoal() + W_POSIZIONI_OCCUPABILI_TORRI_BIANCO * brd.numeroCaselleDisponibiliRookByPlayer(Player.WHITE)
				+ W_POSIZIONI_OCCUPABILI_TORRI_NERO * brd.numeroCaselleDisponibiliRookByPlayer(Player.BLACK)+ W_DIREZIONI_TORRI_BIANCO * dirWhite 
				+ W_DIREZIONI_TORRI_NERO * dirBlack + W_BLACK_AROUND_KING * brd.blackAroundKing();
	}
	
	



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//
}
