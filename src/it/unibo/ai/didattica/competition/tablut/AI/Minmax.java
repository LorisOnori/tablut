package it.unibo.ai.didattica.competition.tablut.AI;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class Minmax {

	//Minmax
	
	//Iterative deepening 
	
	//AlphaBeta pruning
	
	//Transposition Table
	
	/*
	 * 
	 * Minmax -> Passo Board non aggiornata
	 * 		Iterative: Controllo se c'è un vincitore in quel caso return
	 * 					Calcolo mossa
	 * 					Valuto
	 * 
	 */
	
	//Adesso la mossa viene valutata prima
	
	
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
	private static Map<Board, Integer> traspositionTable;
	private static Map<Board, Integer> posizioniPassate;
	private static int torriBianche = Minmax.NUMERO_TORRI_BIANCHE;
	private static int torriNere = Minmax.NUMERO_TORRI_NERE;
	
	// HEURISTIC

	private static final double DIFF  = 16/8;
	
	private static final double W_DIFFERENZA_PEZZI = 300;
	private static final double W_DIREZIONI_RE = 20;
	private static final double W_POSIZIONI_OCC_RE = 3;
	private static final double W_DISTANZA_GOAL = 10;
	private static final double W_POSIZIONI_OCCUPABILI_TORRI_BIANCO = 4 * DIFF;
	private static final double W_POSIZIONI_OCCUPABILI_TORRI_NERO = 4 ;
	private static final double W_DIREZIONI_TORRI_BIANCO = 2 * DIFF;
	private static final double W_DIREZIONI_TORRI_NERO = 2;
	private static final double W_BLACK_AROUND_KING = 50;
	
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private static boolean devoRitornare = false;
    
    public int minmaxCount = 0;

	
	
	
	
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
	
	public static Map<Board, Integer> getTraspositionTable() {
		return traspositionTable;
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
	
	public Mossa iterative(Board board, Player player){
		
		//Genero figlio
		this.minmaxCount = 0;
		Minmax.devoRitornare = false;
		Callable<Mossa> callable = new Child(board, player);
		Future<Mossa> res = executorService.submit(callable);
		
		try {
			TimeUnit.SECONDS.sleep(Client.getTimeOut());
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Minmax.devoRitornare = true;
		//Interrupt callable
		//executorService.shutdownNow();
		//executorService = Executors.newCachedThreadPool();
		
	
		try {
			return res.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Minmax.devoRitornare = false;
		System.out.println("MINMAX NON HA FUNZIONATO");
		return this.board.getNextMovesByPlayer(player).get(0);
		
	}

	
	public int minmax(Board brd, int depth,int  alpha, int beta, Player player) {
		this.minmaxCount ++;
		if(depth == 0) {
			if(traspositionTable.containsKey(brd))
				return traspositionTable.get(brd);
			else {
				int ev = (int) eval(brd, player);
				
				traspositionTable.put(brd, ev);
				return ev;
			}
				
		}
		
		int value;
		//MAX
		if(player == Player.WHITE) {
			
			value = MIN; //Giocatore ha perso se non ci sono mosse (Non si entra nel ciclo sotto)
			
			//Ordinamento Figli senza instanziare ancora
			
			
			List<Mossa> mosse= brd.getNextMovesByPlayer(Player.WHITE);
			//nessuna mossa possibile --> NERO vince
			if(mosse == null)
				return MIN;
			
			Collections.sort(mosse);
			Collections.reverse(mosse);
			
			for(Mossa m: mosse) {
				
				if(m.getBoardAggiornata().whiteWin())
					return MAX;
				else if(m.getBoardAggiornata().blackWin())
					return MIN;
				
				value = Math.max(value, minmax(m.getBoardAggiornata(), depth-1, alpha, beta, Player.BLACK));
				alpha  = Math.max(alpha, value);
				if(alpha >= beta)
					break;
			}
			return value;
		}else {
			//BLACK MIN
			value = MAX;
			List<Mossa> mosse= brd.getNextMovesByPlayer(Player.BLACK);
			//Se mosse == null --> Non si può fare nessuna mossa quindi NERO perde
			if(mosse == null)
				return MAX;
			Collections.sort(mosse);
			
			for(Mossa m: mosse) {
				
				if(m.getBoardAggiornata().whiteWin())
					return MAX;
				else if(m.getBoardAggiornata().blackWin())
					return MIN;
				
				value = Math.min(value, minmax(m.getBoardAggiornata(), depth-1, alpha, beta, Player.WHITE));
				beta  = Math.min(beta, value);
				if(beta <= alpha)
					break;
			}
			return value;
			
		}
	}
	
	//Test

		
	//Nella genereazione di mosse (non qui) posso valutare tutte le mosse e ordinarle in base alla differenza pezzi che dovrebbe essere l'indice piï¿½ importate
	//Minmax quindi esplora prima queste mosse e poi le altre.
	//Valuta la posizione corrente
	public double eval(Board brd, Player player) {
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
		if(brd.blackAroundKing() == 4) {
			//System.out.println("QUATTROOOOOOO");
			return MIN;
		}
		else if(brd.blackAroundKing() == 3 && (brd.kindPosition == Board.NORD_CENTER_POS || brd.kindPosition == Board.EST_CENTER_POS || brd.kindPosition == Board.SUD_CENTER_POS || brd.kindPosition == Board.OVEST_CENTER_POS))
			return MIN;
		else if(k[0] > 1 && brd.getCell(k[0]+1, k[1]).getType() == Type.BLACK_ROOK && Board.isForbidden(k[0]-1, k[1])  
				||  k[0] < 9 && brd.getCell(k[0]-1, k[1]).getType() == Type.BLACK_ROOK && Board.isForbidden(k[0]+1, k[1]) 
				||  k[1] > 1 && brd.getCell(k[0], k[1]+1).getType() == Type.BLACK_ROOK && Board.isForbidden(k[0], k[1]-1)  
				||  k[1] < 9 && brd.getCell(k[0], k[1]-1).getType() == Type.BLACK_ROOK && Board.isForbidden(k[0], k[1]+1) ){
			return MIN;
		}
			
			//DRAW
		//if(posizioniPassate.containsKey(brd))
		//	return 0;
		
		
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
		
		//int whiteWin = brd.canWhiteWin() &&  player == Player.BLACK? Integer.MAX_VALUE : 0;
		
//		return (brd.getWhiteRooksCount() * DIFF - brd.getBlackRooksCount()) * W_DIFFERENZA_PEZZI;
		
		return W_DIFFERENZA_PEZZI * brd.rookDifferenceCount() 
				+ W_DIREZIONI_RE * brd.numeroDirezioniRe() 
				+ W_POSIZIONI_OCC_RE * brd.numeroCaselleDiponibiliKing() 
				//+ W_DISTANZA_GOAL * brd.manhattamDistanceToClosestGoal() 
				+ W_POSIZIONI_OCCUPABILI_TORRI_BIANCO * brd.numeroCaselleDisponibiliRookByPlayer(Player.WHITE)
				- W_POSIZIONI_OCCUPABILI_TORRI_NERO * brd.numeroCaselleDisponibiliRookByPlayer(Player.BLACK)
				//+ W_DIREZIONI_TORRI_BIANCO * dirWhite 
				//- W_DIREZIONI_TORRI_NERO * dirBlack 
				- W_BLACK_AROUND_KING * brd.blackAroundKing();
				//+ whiteWin;
	}
	
	
	public class Child implements Callable<Mossa>{
		
		private Board board = null;
		private Player player = null;
		
		public Child(Board board, Player player) {
			this.board = board;
			this.player = player;
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
		}
		
		@Override
		public Mossa call() throws Exception {
			
			
			//System.out.println("Parto");
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
//			Collections.sort(mosse);
//			if(Player.WHITE == player)
//				Collections.reverse(mosse);
			
			
			//RANDOM
			Collections.shuffle(mosse);
			
			int val; 
			int best = Player.WHITE == player ? MIN : MAX; 
			Mossa res = mosse.get(0);
			
			//Da parallelizzare e ottimizzare con l'ordinamento delle mosse e un db per i risultati parziali e finali
			//long inizio = System.currentTimeMillis();
			
//			for(int i = 0; i<TEST_DEPTH; i++) {
//				val = minmax(board, i, MIN, MAX, player);
//				
//				if(player == Player.WHITE) {
//					if(val > best ) {
//						best = val;
//						res = m;
//					}
//				}else { //BLACK
//					if(val < best) {
//						best = val;
//						res = m;
//					}
//				}
//			}
			
			for(int i= 1; ; i++) { // -----> Cambiare la profonditï¿½ con il tempo
				//System.out.println("Iterative depth "+i);
				
				//QUESTO SOTTO E PARZIALMENTE SBAGLIATO
				
				//Qua genero le prime mosse e dopo chiamo minmax che me le rigenera sempre le stesse mosse
				
				//Una soluzione potrebbe essere quella di chiamare direttamente minmax e vedere il pezzo che è stato mosso
				//Controllando le due board vedo il pezzo che non è in comune e rappresenta la partenza e l'arrivo.
				//Però adesso minmax ritorna solamente il valore di una mossa ma non so quale
				//Dovrei modificare minmax per far ritornare anche il pezzo relativo a tale mossa
				//Oppure far partire minmax dal giocatore opposto e il ciclo for dell'iterative deepening da 1 invece che da 0
				//Con 8 pezzi e TEST_DEPTH = 3 ci mette 1 secondo nella versione con due calcoli di mosse 
				
				
				
				
				for(Mossa m : mosse){
					//System.out.println("b");
					if(Minmax.devoRitornare) {
						System.out.println("Ritorno profondita "+ i + " minmax count "+minmaxCount);
						return res;
					}
					
					if(m.getBoardAggiornata().whiteWin())
						val = MAX;
					else if(m.getBoardAggiornata().blackWin()) {
						System.out.println(m.getPiece().getRow() + " "+m.getPiece().getColumn());
						val = MIN;
					}else {
						Player p = player == Player.WHITE ? Player.BLACK : Player.WHITE;
						val = minmax(m.getBoardAggiornata(), i, MIN, MAX, p);
					}
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
			//System.out.println("DURATA : " + (System.currentTimeMillis()-inizio));
			//return res;

		}
		
	}
	



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//
}
