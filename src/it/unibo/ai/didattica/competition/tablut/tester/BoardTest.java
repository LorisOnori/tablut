package it.unibo.ai.didattica.competition.tablut.tester;


import java.util.HashMap;

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
	
	
	//@BeforeEach
	public void setUp() throws Exception {
		//b.put(1, new Piece(Type.WHITE_ROOK,5));
		b.put(Board.coordinateToIndex(4,2), new Piece(Type.KING,Board.coordinateToIndex(4,2)));
		//b.put(Board.coordinateToIndex(9, 7), new Piece(Type.WHITE_ROOK,Board.coordinateToIndex(9, 7)));
		//b.put(Board.coordinateToIndex(7, 1), new Piece(Type.WHITE_ROOK,Board.coordinateToIndex(7, 1)));
		b.put(Board.coordinateToIndex(4, 6), new Piece(Type.BLACK_ROOK,Board.coordinateToIndex(4, 6)));
//		b.put(Board.coordinateToIndex(3, 1), new Piece(Type.BLACK_ROOK,Board.coordinateToIndex(3, 1)));

		
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
//		for(Piece p: board.getAllPieces())
//			System.out.println("pos "+ p.getPosition() + " r "+ p.getRow() +  " c "+ p.getColumn());
		Mossa m = mm.iterative(board, Player.WHITE);
		System.out.println(m.getOldPos() + " " + m.getNewPos());
		
//		for(int i: pos) {
//			System.out.println("Old Pos "+ board.getCell(9, 1).getPosition() +  " new "+i);
//		}

		//assertEquals
	}

}
