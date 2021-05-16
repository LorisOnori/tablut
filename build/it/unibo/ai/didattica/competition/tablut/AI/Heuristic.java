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
	public static final int WEIGHTS = 9;
	
	//400 20 2 10 3 -2 2 -1 -100 
	private static int [] weight = {400,20,2,10,3,-2,2,-1,-100};
	
	public static void setWeight(int [] w) {
		System.out.println("Weight changed to " );
		for(int i : w)
			System.out.print(i + " ");
		System.out.println();
		Heuristic.weight = w;
	}
	
	public static double eval(Board brd, Player player) {
		
		//WHITE WIN
		for(int []p : Board.finalCellsSet) {
			if(brd.kindPosition == Board.coordinateToIndex(p[0], p[1]))
				return MAX;
		}
		//BLACK WIN

		int []k = Board.indexToCoordinate(brd.kindPosition);
		if(brd.blackAroundKing() == 2 && !(brd.kindPosition == Board.NORD_CENTER_POS || brd.kindPosition == Board.EST_CENTER_POS || brd.kindPosition == Board.SUD_CENTER_POS || brd.kindPosition == Board.OVEST_CENTER_POS)) {
			return MIN;
		}
		else if(brd.blackAroundKing() == 3 && (brd.kindPosition == Board.NORD_CENTER_POS || brd.kindPosition == Board.EST_CENTER_POS || brd.kindPosition == Board.SUD_CENTER_POS || brd.kindPosition == Board.OVEST_CENTER_POS))
			return MIN;
		else if(k[0] > 1 && brd.getCell(k[0]+1, k[1]).getType() == Type.BLACK_ROOK && Board.isWall(k[0]-1, k[1])  
				||  k[0] < 9 && brd.getCell(k[0]-1, k[1]).getType() == Type.BLACK_ROOK && Board.isWall(k[0]+1, k[1]) 
				||  k[1] > 1 && brd.getCell(k[0], k[1]+1).getType() == Type.BLACK_ROOK && Board.isWall(k[0], k[1]-1)  
				||  k[1] < 9 && brd.getCell(k[0], k[1]-1).getType() == Type.BLACK_ROOK && Board.isWall(k[0], k[1]+1) ){
			return MIN;
		}
			


		
		
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
