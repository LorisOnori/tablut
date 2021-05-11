package it.unibo.ai.didattica.competition.tablut.AI;

public class Heuristic {
	
	private static final double DIFF  = 16/8;
	private static final int MAX = Integer.MAX_VALUE;
	private static final int MIN = Integer.MIN_VALUE;

	private static final int W_DIFFERENZA_PEZZI = 0;
	private static final int W_DIREZIONI_RE = 1;
	private static final int W_POSIZIONI_OCC_RE = 2;
	private static final int W_DISTANZA_GOAL = 3;
	private static final int W_POSIZIONI_OCCUPABILI_TORRI_BIANCO = 4;
	private static final int W_POSIZIONI_OCCUPABILI_TORRI_NERO = 5 ;
	private static final int W_DIREZIONI_TORRI_BIANCO = 6;
	private static final int W_DIREZIONI_TORRI_NERO = 7;
	private static final int W_BLACK_AROUND_KING = 8;
	private static final int WEIGHTS = 9;
	
//	private static final double W_DIFFERENZA_PEZZI = 300;
//	private static final double W_DIREZIONI_RE = 20;
//	private static final double W_POSIZIONI_OCC_RE = 3;
//	private static final double W_DISTANZA_GOAL = 10;
//	private static final double W_POSIZIONI_OCCUPABILI_TORRI_BIANCO = 4 * DIFF;
//	private static final double W_POSIZIONI_OCCUPABILI_TORRI_NERO = 4 ;
//	private static final double W_DIREZIONI_TORRI_BIANCO = 2 * DIFF;
//	private static final double W_DIREZIONI_TORRI_NERO = 2;
//	private static final double W_BLACK_AROUND_KING = 50;
	

	private static int [] weight = {300, 20,3,10,8,-4,4,-2,-50};
	
	public static void setWeight(int [] w) {
		System.out.println("Weight changed to " );
		for(int i : weight)
			System.out.print(i + " ");
		System.out.println();
		Heuristic.weight = w;
	}
	
	public static double eval(Board brd, Player player) {
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
		
		return weight[W_DIFFERENZA_PEZZI] * brd.rookDifferenceCount() 
				+ weight[W_DIREZIONI_RE] * brd.numeroDirezioniRe() 
				+ weight[W_POSIZIONI_OCC_RE] * brd.numeroCaselleDiponibiliKing() 
				+ weight[W_DISTANZA_GOAL] * brd.manhattamDistanceToClosestGoal() 
				+ weight[W_POSIZIONI_OCCUPABILI_TORRI_BIANCO] * brd.numeroCaselleDisponibiliRookByPlayer(Player.WHITE)
				+ weight[W_POSIZIONI_OCCUPABILI_TORRI_NERO] * brd.numeroCaselleDisponibiliRookByPlayer(Player.BLACK)
				+ weight[W_DIREZIONI_TORRI_BIANCO] * dirWhite 
				+ weight[W_DIREZIONI_TORRI_NERO] * dirBlack 
				+ weight[W_BLACK_AROUND_KING] * brd.blackAroundKing();

	}

}
