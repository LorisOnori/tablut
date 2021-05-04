package it.unibo.ai.didattica.competition.tablut.tester;


import java.util.HashMap;
import java.util.Map;

//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;

import it.unibo.ai.didattica.competition.tablut.AI.Board;
import it.unibo.ai.didattica.competition.tablut.AI.Minmax;
import it.unibo.ai.didattica.competition.tablut.AI.Mossa;
import it.unibo.ai.didattica.competition.tablut.AI.Piece;
import it.unibo.ai.didattica.competition.tablut.AI.Player;
import it.unibo.ai.didattica.competition.tablut.AI.Type;

class BoardTest {
	
	private HashMap<Integer,Piece> b = new HashMap<>();
	private Board board;
	private static final int MAX = Integer.MAX_VALUE;
	private static final int MIN = Integer.MIN_VALUE;
	private static final int NUMERO_TORRI_BIANCHE = 8;
	private static final int NUMERO_TORRI_NERE = 16;

	
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
	
	
	//@BeforeEach
	public void setUp() throws Exception {
		//b.put(1, new Piece(Type.WHITE_ROOK,5));
		b.put(Board.coordinateToIndex(5,5), new Piece(Type.KING,Board.coordinateToIndex(5,5)));
		b.put(Board.coordinateToIndex(5, 4), new Piece(Type.BLACK_ROOK,Board.coordinateToIndex(5, 4)));
		b.put(Board.coordinateToIndex(5, 6), new Piece(Type.BLACK_ROOK,Board.coordinateToIndex(5, 6)));
		b.put(Board.coordinateToIndex(4, 5), new Piece(Type.BLACK_ROOK,Board.coordinateToIndex(4, 5)));
		b.put(Board.coordinateToIndex(9, 5), new Piece(Type.BLACK_ROOK,Board.coordinateToIndex(9, 5)));
		b.put(Board.coordinateToIndex(7, 1), new Piece(Type.WHITE_ROOK,Board.coordinateToIndex(7, 1)));
		b.put(Board.coordinateToIndex(4, 6), new Piece(Type.BLACK_ROOK,Board.coordinateToIndex(4, 6)));
		b.put(Board.coordinateToIndex(3, 1), new Piece(Type.BLACK_ROOK,Board.coordinateToIndex(3, 1)));

		
		board = new Board(b);
	}
	
	//@Test
	void testBoardDistances() {
		int nord,sud,est,ovest;
		//int []pos = board.possibleMovesByPiece(board.getCell(Board.coordinateToIndex(9, 1)));
//		nord = pos[Board.NORD];
//		sud = pos[Board.SUD];
//		est = pos[Board.EST];
//		ovest = pos[Board.OVEST];
		//assertEquals(2,nord); //1
		//assertEquals(2,sud);	//1
		//assertEquals(2,est); //3
		//assertEquals(2,ovest); //3
		//assertEquals(3,board.numeroDirezioniRe());
		//System.out.println("nord "+nord +" sud "+ sud+" est "+est+" ovest "+ovest );
		//System.out.println("numero celle disponibili rook: "+ board.numeroCaselleDisponibiliRookByPlayer(Player.BLACK));
		//System.out.println("numero direzioni disponibili rook: "+ board.numeroDirezioniRook(7, 3));
		
//		int [] a = new int[] {5, 17, 81, 75, 80, 79, 78, 77,76,75,74,73,72,71,70,69,1,2,3,4,11,12};
//		for(int i : a)
//			System.out.println(i+" = "+Board.indexToCoordinate(i)[0]+" "+ Board.indexToCoordinate(i)[1]);
//		
		
		Minmax mm = Minmax.getInstance();
		Mossa m = mm.iterative(board, Player.BLACK);
		int []newPos = Board.indexToCoordinate(m.getNewPos());
		int []oldPos = Board.indexToCoordinate(m.getOldPos());
		System.out.println("Old : "+oldPos[0]+  " "+oldPos[1] + " New : " + newPos[0]+ " "+ newPos[1]);
		
		//System.out.println(this.eval(board, Player.BLACK));
		
//		System.out.println(board.whiteWin());
//		for(int i: pos) {
//			System.out.println("Old Pos "+ board.getCell(9, 1).getPosition() +  " new "+i);
//		}

		//assertEquals
	}
	
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

}
