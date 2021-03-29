package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import player.Board;
import player.Piece;
import player.Player;
import player.Type;

class BoardTest {
	
	private HashMap<Integer,Piece> b = new HashMap<>();
	private Board board;
	
	
	
	@BeforeEach
	public void setUp() throws Exception {
		//b.put(1, new Piece(Type.WHITE_ROOK,5));
		b.put(Board.coordinateToIndex(9,1), new Piece(Type.KING,Board.coordinateToIndex(9,1)));
		b.put(Board.coordinateToIndex(9, 7), new Piece(Type.WHITE_ROOK,Board.coordinateToIndex(9, 7)));
		b.put(Board.coordinateToIndex(7, 1), new Piece(Type.WHITE_ROOK,Board.coordinateToIndex(7, 1)));
		b.put(Board.coordinateToIndex(7, 3), new Piece(Type.BLACK_ROOK,Board.coordinateToIndex(7, 3)));
//		b.put(Board.coordinateToIndex(3, 1), new Piece(Type.BLACK_ROOK,Board.coordinateToIndex(3, 1)));

		
		board = new Board(b);
	}
	
	@Test
	void testBoardDistances() {
		int nord,sud,est,ovest;
		int []pos = board.numberOfOccupiableCells(7, 1);
		nord = pos[Board.NORD];
		sud = pos[Board.SUD];
		est = pos[Board.EST];
		ovest = pos[Board.OVEST];
		//assertEquals(2,nord); //1
		//assertEquals(2,sud);	//1
		//assertEquals(2,est); //3
		//assertEquals(2,ovest); //3
		//assertEquals(3,board.numeroDirezioniRe());
		System.out.println("nord "+nord +" sud "+ sud+" est "+est+" ovest "+ovest );
		System.out.println("numero celle disponibili rook: "+ board.numeroCaselleDisponibiliRookByPlayer(Player.BLACK));
		System.out.println("numero direzioni disponibili rook: "+ board.numeroDirezioniRook(7, 3));

		//assertEquals
	}

}
