package player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Board {
	
	//TODO
	//Posso attraversare le forbiddenCells !!!!
	
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
	public final static Set<int[]> finalCellsSet = new HashSet<>(Arrays.asList(finalCells));
	
	
	public final static int [] center = {5,5};
	
	public final static int [][] forbiddenCells = {{1,4},{1,5},{1,6},{2,5},
										{4,1},{5,1},{6,1},{5,2},
										{4,9},{5,9},{6,9},{5,8},
										{9,4},{9,5},{9,6},{8,5}};
	
	public final static Set<int[]> forbiddenCellsSet = new HashSet<>(Arrays.asList(forbiddenCells));

	
	
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
		
		//Probabilmente facendo cos�, ogni volta che ricevo una nuova posizione aggiorno solamente quella precedente
		//e non ricreo Board da capo
		
		
		//TODO magari non stringa ma direttamente la mappa
		this.updateBoard(s);
		
		
		
		
	}
	
	public Board(HashMap<Integer,Piece> board) {
		this.board = board;
	
		//TODO magari non stringa ma direttamente la mappa
		this.updateBoard(null);
		
	}
	
	public static boolean isForbidden(int r, int c) {
		for(int[] f : Board.forbiddenCellsSet) {
			//System.out.println("R "+r + " C " +c+ " f[0] " + f[0] + " f[1] "+ f[1]);
			if(r == f[0] && c == f[1])
				return true;
		}
		return false;
	}
	
	public static boolean isForbbidden(int index) {
		int p[];
		p = Board.indexToCoordinate(index);
		return Board.isForbidden(p[0], p[1]);
	}
	
	//Magari non stringa ma direttamente la maooa
	public void updateBoard(String s) {
		
		///
		
		for(Piece p : this.board.values()) {
			if(p.getType() == Type.BLACK_ROOK) {
				this.blackPieces++;
			}else if(p.getType() == Type.WHITE_ROOK) {
				this.whitePieces++;
			}else {
				this.kindPosition = p.getPosition();
			}
		}
	}
	
	public Piece getCell(int i,int j) {
		int num = i * nColumns + j;
		if(this.board.containsKey(num))
			return board.get(num);
		else
			return new Piece(Type.EMPTY,i,j);
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
			else ret[index] = new Piece(Type.EMPTY,i);
			
			index++;
				
		}
		return ret;
	}
	
	//1 to Column
	public Piece[] getFullColumn(int c) {

		Piece[] ret = new Piece[nRow];
		int index = 0;
		int col = c;
		for(int i = 0 ; i<nRow; i++) {
			if(board.containsKey(c))
				ret[index] = board.get(c);
			else ret[index] = new Piece(Type.EMPTY,i,col);
			
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
	
	public int getWhiteRooksCount() {
		int val = 0;
		for(Piece p : this.board.values()) {
			if(p.getType() == Type.WHITE_ROOK)
				val++;
		}
		return val;
	}
	
	public int getBlackRooksCount() {
		int val = 0;
		for(Piece p : this.board.values()) {
			if(p.getType() == Type.BLACK_ROOK)
				val++;
		}
		return val;
	}
	
	public int rookDifferenceCount() { //Senza contare il re
		int val = 0;
		for(Piece p : board.values()) {
			if(p.getType() == Type.WHITE_ROOK)
				val ++;
			else val--;
		}
		return val+1; //Il altrimenti � contato come nero
 	}
	
	public int manhattamDistanceToClosestGoal() {
		int min = Board.nRow + Board.nColumns + 1;
		int val;
		int []kingPos = Board.indexToCoordinate(this.kindPosition);
		for(int [] pos : Board.finalCellsSet) {
			//xKing - xObj ---- yKing - yObj
			val = Math.abs(kingPos[1] - pos[1]) + Math.abs(kingPos[0] - kingPos[0]);
			min = val < min ? val : min;
		}
		return min;
	}
	
	
	public int numeroDirezioniRook(int r, int c) {
		int val = 0;
		//TODO
		//Posso muovermi quando non ho un ostacolo --> Posso attraversare le forbbidden Cells
		if( r > 1 && !board.containsKey(Board.coordinateToIndex(r-1, c)))
			val ++;
		
		if(r < nColumns && !board.containsKey(Board.coordinateToIndex(r+1, c)))
			val ++; 
		
		if (c > 1 && !board.containsKey(Board.coordinateToIndex(r, c-1)))
			val++;
		
		if (c < nRow && !board.containsKey(Board.coordinateToIndex(r, c+1)))
			val++;
		
		return val;
		
	}
	
	public int numeroDirezioniPezzo(Piece p) {
		return this.numeroDirezioniRook(p.getPosition());
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
		int spazi[] = new int[Board.DIREZIONI];
		int val = 0;
		if(player == Player.WHITE) {
			
			for(Piece p : this.board.values()) {
				if(p.getType() == Type.WHITE_ROOK) {
					int []pos = Board.indexToCoordinate(p.getPosition());
					spazi = this.getDistanceOfNextObstacle(pos[0], pos[1]);
					val += spazi[Board.NORD] + spazi[Board.SUD] + spazi[Board.EST] +spazi[Board.OVEST];
					
				}
			}
			
		}else {
			
			for(Piece p : this.board.values()) {
				if(p.getType() == Type.BLACK_ROOK) {
					int []pos = Board.indexToCoordinate(p.getPosition());
					spazi = this.getDistanceOfNextObstacle(pos[0], pos[1]);
					val += spazi[Board.NORD] + spazi[Board.SUD] + spazi[Board.EST] +spazi[Board.OVEST];
					
				}
			}
			
		}
		return val;
	}
	
	public int numeroCaselleDiponibiliKing() {
		int []kingPos = Board.indexToCoordinate(this.kindPosition);
		int []spazi = this.getDistanceOfNextObstacle(kingPos[0], kingPos[1]);
		return spazi[Board.NORD] + spazi[Board.SUD] + spazi[Board.EST] +spazi[Board.OVEST];
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
				if(p.getType() == Type.WHITE_ROOK) {
					position[index] = p.getPosition();
					index++;
				}
			}
		}else {
			position = new int[Board.BLACK_ROOK_NUMBER];
			for(Piece p : this.board.values()) {
				if(p.getType() == Type.BLACK_ROOK) {
					position[index] = p.getPosition();
					index++;
				}
			}
			
		}
		return position;
	}
	
	/*public int getNextPieceInColumn() {
		
	}
	*/
	
	//Distanza del prossimo ostacolo, amico o nemico che sia -> Numero di caselle occupabili per direzione
	public int[] getDistanceOfNextObstacle(int r, int c) {
		int[] ret = new int[Board.DIREZIONI];
		for(int i = 0; i < ret.length; i++) {
			ret[i] = 0;
		}
		
		//if(player == Player.BLACK) {
			//NORD
			
			int currentRowPosition = 1;
			int currentColumnPosition = 1;
			
			
			Piece pezzo = this.getCell(r, c);
			
			//NORD
			int nord = 0;
			for(int i = r+1 ; i <= Board.nRow; i++) {
				//Non posso andare se c'� un pezzo -> � nella board oppure se fa parte della caselle proibite e io non posso andarci
				if(board.containsKey(Board.coordinateToIndex(i, c)) || (Board.isForbidden(i, c) && !pezzo.canMooveInsideForbiddenArea()))
					break;
				else
					nord++;
			}
			ret[Board.NORD] = nord;
			//SUD
			int sud = 0;
			for(int i = r-1 ; i > 0; i--) {
				//Non posso andare se c'� un pezzo -> � nella board oppure se fa parte della caselle proibite e io non posso andarci
				if(board.containsKey(Board.coordinateToIndex(i, c)) || (Board.isForbidden(i, c) && !pezzo.canMooveInsideForbiddenArea()))
					break;
				else
					sud++;
			}
			ret[Board.SUD] = sud;
			
			//EST 
			int est = 0;
			for(int i = r+1 ; i <= Board.nColumns; i++) {
				//Non posso andare se c'� un pezzo -> � nella board oppure se fa parte della caselle proibite e io non posso andarci
				//boolean can = Board.forbiddenCellsSet.contains(new int[] {r,i});
				if(board.containsKey(Board.coordinateToIndex(r, i)) || ( Board.isForbidden(r, i) && !pezzo.canMooveInsideForbiddenArea()))
					break;
				else
					est++;
			}
			ret[Board.EST] = est;
			
			//OVEST
			int ovest = 0;
			for(int i = r-1 ; i > 0; i--) {
				//Non posso andare se c'� un pezzo -> � nella board oppure se fa parte della caselle proibite e io non posso andarci
				if(board.containsKey(Board.coordinateToIndex(r, i)) || (Board.isForbidden(r, i) && !pezzo.canMooveInsideForbiddenArea()))
					break;
				else
					ovest++;
			}
			ret[Board.OVEST] = ovest;
			
			/*
			for(Piece piece : this.getFullColumn(c)) {
			 
				
				
				if(currentRowPosition < r) {
					//SUD
					if(piece.getType() == Type.EMPTY && (piece.canMooveInsideForbiddenArea() || !Board.forbiddenCellsSet.contains(Board.indexToCoordinate(piece.getPosition())))) {
						numSud ++ ;
					}else {
						numSud = 0; //Ogni volta che incontro un ostacolo azzero il contatore
									//Quando arrivo  a 0 assegno le caselle dall'ultimo ostacolo fino alla mia fila
					}
				}else if(currentRowPosition > r){
					//NORD
					if(piece.getType() == Type.EMPTY && (piece.canMooveInsideForbiddenArea() || !Board.forbiddenCellsSet.contains(Board.indexToCoordinate(piece.getPosition())))) {
						ret[Board.NORD]++;
					}else break; 
				}else {
					//Posizione corrente
					ret[Board.SUD] = numSud; 
				}
				currentRowPosition++;
			}
			
			//EST
			
			
			//OVEST
			int numOvest = 0 ;
			
			for(Piece piece : this.getFullRow(r)) {
				
				
				if(currentColumnPosition < r) {
					//OVEST
					if(piece.getType() == Type.EMPTY && (piece.canMooveInsideForbiddenArea() || !Board.forbiddenCellsSet.contains(Board.indexToCoordinate(piece.getPosition())))) {
						numOvest ++ ;
					}else {
						numOvest = 0; //Ogni volta che incontro un ostacolo azzero il contatore
									//Quando arrivo  a 0 assegno le caselle dall'ultimo ostacolo fino alla mia fila
					}
				}else if(currentColumnPosition > r){
					//EST
					if(piece.getType() == Type.EMPTY && (piece.canMooveInsideForbiddenArea() || !Board.forbiddenCellsSet.contains(Board.indexToCoordinate(piece.getPosition())))) {
						ret[Board.EST]++;
					}else break; 
				}else {
					//Posizione corrente
					ret[Board.OVEST] = numOvest; 
				}
				currentColumnPosition++;
			}
			
			
			
		//}else {
			//WHITE
			
			
			
		//}
		 *
		 */
		 
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
