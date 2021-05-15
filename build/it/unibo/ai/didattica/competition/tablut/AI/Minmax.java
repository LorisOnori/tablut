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
	 * 		Iterative: Controllo se c'� un vincitore in quel caso return
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
				int ev = (int) Heuristic.eval(brd, player);
				
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
			//Se mosse == null --> Non si pu� fare nessuna mossa quindi NERO perde
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

		
	//Nella genereazione di mosse (non qui) posso valutare tutte le mosse e ordinarle in base alla differenza pezzi che dovrebbe essere l'indice pi� importate
	//Minmax quindi esplora prima queste mosse e poi le altre.
	//Valuta la posizione corrente

	
	
	public class Child implements Callable<Mossa>{
		
		private Board board = null;
		private Player player = null;
		
		public Child(Board board, Player player) {
			this.board = board;
			this.player = player;
			int tb = board.getWhiteRooksCount();
			int tn = board.getBlackRooksCount();
			
			//Se catturo un pezzo allora le nuove posizioni saranno sicuramente diverse
			//Cos� come quelle valutate
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
			//Prendo il tempo adesso provo con profondit�
			int depth = TEST_DEPTH;

			//Data una board prendo tutte le mosse possibili
			//Chiamo il minmax per ognuna di queste mosse cos� da ottenere la valutazione
			//salvo i risultati
			
			List<Mossa> mosse = board.getNextMovesByPlayer(player);
			//Attenzione al sort perch� calcolando la eval qui
			//rischio di sprecare tanto tempo
			
			//Uso un'eval pi� veloce
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
			
			for(int i= 1; ; i++) { // -----> Cambiare la profondit� con il tempo
				//System.out.println("Iterative depth "+i);
				
				//QUESTO SOTTO E PARZIALMENTE SBAGLIATO
				
				//Qua genero le prime mosse e dopo chiamo minmax che me le rigenera sempre le stesse mosse
				
				//Una soluzione potrebbe essere quella di chiamare direttamente minmax e vedere il pezzo che � stato mosso
				//Controllando le due board vedo il pezzo che non � in comune e rappresenta la partenza e l'arrivo.
				//Per� adesso minmax ritorna solamente il valore di una mossa ma non so quale
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
