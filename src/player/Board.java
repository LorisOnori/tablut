package player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Board {
	
	public final static int NO_PIECE = -1;
	public final static int BLACK_ROOK_NUMBER = 12;
	public final static int WHITE_ROOK_NUMBER = 8;
	public final static int NORD = 0;
	public final static int SUD = 1;
	public final static int EST = 2;
	public final static int OVEST = 3;
	public final static int DIREZIONI = 4;
	
	public final static int [][] finalCells = {{1,1},{1,2},{1,3},{1,7},{1,8},{1,9},
								{2,1},		{7,1},
								{3,1},		{7,9},
								{2,9},		{8,1},
								{3,9},		{8,9},
								{9,1},{9,2},{9,3},{9,7},{9,8},{9,9}};
	public final static Set<int[]> finalCellsSet = new HashSet<>();
	
	
	public final static int [] center = {5,5};
	
	public final static int [][] forbiddenCells = {{1,4},{1,5},{1,6},{2,5},
										{4,1},{5,1},{6,1},{5,2},
										{4,9},{5,9},{6,9},{5,8},
										{9,4},{9,5},{9,6},{8,5}};
	
	public final static Set<int[]> forbiddenCellsSet = new HashSet<>();
	
	
	public static int nColumns = 9;
	public static int nRow = 9;
	
	public HashMap<Integer,Piece> board;
	
	public int whitePieces = 0;
	public int blackPieces = 0;
	public int kindPosition = 0;
	
	
	public Board(String s, int nRow, int nColumns) {
		
		//Get all the infos from the data
		//riempo board
		
		this.nColumns = nColumns;
		this.nRow = nRow;
		
		//Probabilmente facendo così, ogni volta che ricevo una nuova posizione aggiorno solamente quella precedente
		//e non ricreo Board da capo
		for(int []p : finalCells) {
			finalCellsSet.add(p);
		}
		
		for(int []p : forbiddenCells) {
			forbiddenCellsSet.add(p);
		}
		
		this.kindPosition = ;
		
		whitePieces = ;
		blackPieces = ;
		
		
		
		
	}
	
	public Piece getCell(int i,int j) {
		int num = i * nColumns + j;
		if(this.board.containsKey(num))
			return board.get(num);
		else
			return new Piece(Type.EMPTY);
	}
	
	// 1 to Row
	public Piece[] getFullRow(int r) {
		int inizio = r * nColumns + 1; //Se riga 4 -> pezzi dal 37 al 45 compresi
		int fine = inizio + nColumns - 1;
		
		Piece[] ret = new Piece[nColumns];
		int index = 0;
		
		for(int i = inizio ; i<=fine; i++) {
			if(board.containsKey(i))
				ret[index] = board.get(i);
			else ret[index] = new Piece(Type.EMPTY);
			
			index++;
				
		}
		return ret;
	}
	
	//1 to Column
	public Piece[] getFullColumn(int c) {

		Piece[] ret = new Piece[nColumns];
		int index = 0;
		for(int i = 0 ; i<nColumns; i++) {
			
			if(board.containsKey(c))
				ret[index] = board.get(c);
			else ret[index] = new Piece(Type.EMPTY);
			
			index ++; 
			c+=this.nRow;
				
		}
		return ret;
	}
	
	//public getAllPieces() 	
	
	
	public int eval() {
		//TODO 
		return 0;
	}
	
	public int numeroPezzi() { //Senza contare il re
		int val = 0;
		for(Piece p : board.values()) {
			if(p.type == Type.WHITE_ROOK)
				val ++;
			else val--;
		}
		return val;
	}
	
	public int distanzaGoal() {
		//TODO
	}
	
	public int numeroDirezioniRook(int r, int c) {
		int val = 0;
		
		if( r > 1 && board.containsKey(this.coordinateToIndex(r-1, c)))
			val ++;
		
		if(r < nColumns && board.containsKey(this.coordinateToIndex(r+1, c)))
			val ++; 
		
		if (c > 1 && board.containsKey(this.coordinateToIndex(r, c-1)))
			val++;
		
		if (c < nRow && board.containsKey(this.coordinateToIndex(r, c+1)))
			val++;
		
		return val;
		
	}
	
	public int numeroDirezioniRook(int index) {
		int []v = Board.indexToCoordinate(index);
		return this.numeroDirezioniRook(v[0], v[1]);
	}
	
	public int numeroDirezioniRe(){
		return numeroDirezioniRook(this.kindPosition);
	}
	
	public int numeroCaselleDisponibiliRookByPlayer(Player player) {
		//TODO
		
		if(player == Player.WHITE) {
			
		}else {
			
		}
	}
	
	public int numeroCaselleDiponibiliKing() {
		
	}
	
	/*public int getNextPieceInRaw(int r) {
		
	}
	*/
	
	public int[] getRooksByPlayer(Player player) {
		
		int index = 0;
		int[] position = null;
		if(player == Player.WHITE) {
			position = new int[Board.WHITE_ROOK_NUMBER];
			for(Piece p : this.board.values()) {
				if(p.type == Type.WHITE_ROOK) {
					position[index] = p.position;
					index++;
				}
			}
		}else {
			position = new int[Board.BLACK_ROOK_NUMBER];
			for(Piece p : this.board.values()) {
				if(p.type == Type.BLACK_ROOK) {
					position[index] = p.position;
					index++;
				}
			}
			
		}
		return position;
	}
	
	/*public int getNextPieceInColumn() {
		
	}
	*/
	
	//Search for pieces of a given color
	public int[] getDistanceOfNextObstacleByPlayer(int r, int c, Player player) {
		int[] ret = new int[Board.DIREZIONI];
		
		if(player == Player.BLACK) {
			//NORD
			int numNord = 0;
			int numSud = 0;
			
			for(Piece piece : this.getFullColumn(c)) {
				int currentRowPosition = Board.indexToCoordinate(piece.position)[0];
				
				if(currentRowPosition < r) {
					//SUD
					if(piece.getType() == Type.EMPTY && !Board.forbiddenCellsSet.contains(Board.indexToCoordinate(piece.getPosition()))) {
						numSud ++ ;
					}else {
						numSud = 0; //Ogni volta che incontro un ostacolo azzero il contatore
									//Quando arrivo  a 0 assegno le caselle dall'ultimo ostacolo fino alla mia fila
					}
				}else if(currentRowPosition > r){
					//NORD
					if(piece.getType() == Type.EMPTY && !Board.forbiddenCellsSet.contains(Board.indexToCoordinate(piece.getPosition()))) {
						ret[Board.NORD]++;
					}else break; 
				}else {
					//Posizione corrente
					ret[Board.SUD] = numSud; 
				}
			}
			
			//SUD
			
			
			//EST
			
			
			//OVEST
			
		}else {
			
		}
		return ret;
	}
	
	/*
	public int getDistanceOfNextObstacleInRawByPlayer(int r, Player player) {
		
	}
	*/
	
	public static int[] indexToCoordinate(int index) {
		int r = index / nColumns;
		int c = index - nRow - r;
		
		return new int[]{r,c};
	}
	
	public static int coordinateToIndex(int r, int c) {
		return r * nColumns + c;
	}
	
	
	

}
