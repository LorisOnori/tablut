package it.unibo.ai.didattica.competition.tablut.AI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class Board implements Comparable<Board>, Comparator<Board>{
	
	public final static int NO_PIECE = -1;
	public final static int BLACK_ROOK_NUMBER = 16;
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
	public final static int EST_CENTER_POS = 42;
	public final static int OVEST_CENTER_POS = 40;
	public final static int SUD_CENTER_POS = 32;
	public final static int NORD_CENTER_POS = 50;
	
	public final static int [][] forbiddenCells = {{1,4},{1,5},{1,6},{2,5},
										{4,1},{5,1},{6,1},{5,2},
										{4,9},{5,9},{6,9},{5,8},
										{9,4},{9,5},{9,6},{8,5},center};
	
	public final static int [][] wallCells = {{1,4},{1,6},{2,5},
												{4,1},{6,1},{5,2},
												{4,9},{6,9},{5,8},
												{9,4},{9,6},{8,5},center};
	
	public final static Set<int[]> wallCellsSet = new HashSet<>(Arrays.asList(wallCells));
	
	public final static int [][] zonaProibita1 = {{1,4},{1,5},{1,6},{2,5}};
	public final static int [][] zonaProibita2 = {{4,1},{5,1},{6,1},{5,2}};
	public final static int [][] zonaProibita3 = {{4,9},{5,9},{6,9},{5,8}};
	public final static int [][] zonaProibita4 = {{9,4},{9,5},{9,6},{8,5}};
	
	public final static Set<int[]> forbiddenCellsSet = new HashSet<>(Arrays.asList(forbiddenCells));
	
	public final static Set<int[]> zonaProibita1Set = new HashSet<>(Arrays.asList(zonaProibita1));
	public final static Set<int[]> zonaProibita2Set = new HashSet<>(Arrays.asList(zonaProibita2));
	public final static Set<int[]> zonaProibita3Set = new HashSet<>(Arrays.asList(zonaProibita3));
	public final static Set<int[]> zonaProibita4Set = new HashSet<>(Arrays.asList(zonaProibita4));

	
	
	public static int nColumns = 9;
	public static int nRow = 9;
	
	public HashMap<Integer,Piece> board;
	
	public int whitePieces = 0;
	public int blackPieces = 0;
	public int kindPosition = 0;
	
	
	
	
	public Board(State state) {
		this.board = new HashMap<>();
		
		for(int i = 0; i<nRow; i++) {
			for(int j = 0 ; j < nColumns ; j++) {
				Pawn p = state.getPawn(i, j);
				//System.out.println(p);
				if(p == Pawn.BLACK) {
					board.put(Board.coordinateToIndex(i+1, j+1), new Piece(Type.BLACK_ROOK, i+1, j+1));
				}else if(p == Pawn.WHITE) {
					board.put(Board.coordinateToIndex(i+1, j+1), new Piece(Type.WHITE_ROOK, i+1, j+1));
				}else if(p == Pawn.KING) {
					board.put(Board.coordinateToIndex(i+1, j+1), new Piece(Type.KING, i+1, j+1));
				}
				
			}
		}
		this.updateBoard();
		
	}
	
	
	public Board(String s, int nRow, int nColumns) {
		Board.nColumns = nColumns;
		Board.nRow = nRow;
		this.updateBoard();	
	}
	
	public Board(HashMap<Integer,Piece> board) {
		this.board = board;
		this.updateBoard();	
	}
	
	public static boolean isForbidden(int r, int c) {
		for(int[] f : Board.forbiddenCellsSet) {
			if(r == f[0] && c == f[1])
				return true;
		}
		return false;
	}
	
	public static boolean isForbidden(int index) {
		int p[];
		p = Board.indexToCoordinate(index);
		return Board.isForbidden(p[0], p[1]);
	}
	
	public static boolean isWall(int r, int c) {
		for(int[] f : Board.wallCellsSet) {
			if(r == f[0] && c == f[1])
				return true;
		}
		return false;
	}
	
	public static boolean isWall(int index) {
		int p[];
		p = Board.indexToCoordinate(index);
		return Board.isWall(p[0], p[1]);
	}
	
	public boolean removePiece(int p, Type t) {
		if(this.board.containsKey(p)) {
			this.board.remove(p);
			if(t == Type.BLACK_ROOK)
				this.blackPieces--;
			else
				this.whitePieces--;
			return true;
		}else
			return false;
	}
	
	public boolean removePiece(int r, int c, Type t) {
		return this.removePiece(Board.coordinateToIndex(r, c),t);
	}
	
	public boolean removePiece(Piece p, Type t) {
		return this.removePiece(p.getPosition(), t);
	}
	
	public boolean removePiece(Piece p) {
		return this.removePiece(p.getPosition(), p.getType());
	}
	
	public void updateBoard() {
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
		return this.getCell(Board.coordinateToIndex(i, j));
	}
	
	public Piece getCell(int p) {
		if(!this.board.containsKey(p))
			return new Piece(Type.EMPTY, p);
		else
			return this.board.get(p);
	}
	
	public Piece[] getFullRow(int r) {
		int inizio = r * nColumns + 1;
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
	

	public Piece[] getFullColumn(int c) {

		Piece[] ret = new Piece[nRow];
		int index = 0;
		int col = c;
		for(int i = 0 ; i<nRow; i++) {
			if(board.containsKey(c))
				ret[index] = board.get(c);
			else ret[index] = new Piece(Type.EMPTY,i,col);
			
			index ++; 
			c+=Board.nRow;
				
		}
		return ret;
	}
	
	public int blackAroundKing() {
		int []p = Board.indexToCoordinate(this.kindPosition);
		int r = p[0];
		int c = p[1];
		int val = 0;
		
		if(r > 1 && this.getCell(r-1, c).getType() == Type.BLACK_ROOK)
			val++;
		if(r < 9 && this.getCell(r+1, c).getType() == Type.BLACK_ROOK)
			val++;
		if(c > 1 && this.getCell(r, c-1).getType() == Type.BLACK_ROOK)
			val++;
		if(c < 9 && this.getCell(r, c+1).getType() == Type.BLACK_ROOK)
			val++;
		
		return val;
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
	
	
	public boolean canWhiteWin() {
		
		int []caselle = this.numberOfOccupiableCells(this.kindPosition);
		int kc, kr;
		kc = Board.indexToCoordinate(this.kindPosition)[0];
		kr = Board.indexToCoordinate(this.kindPosition)[1];
		
		int []nord = {kr + caselle[NORD], kc};
		int []sud ={kr - caselle[SUD], kc};
		int []est = {kr, caselle[EST] + kc};
		int []ovest = {kr,kc - caselle[OVEST]};
		
		

		for(int []cell : Board.finalCells) {
			if((cell[0] == nord[0] && cell[1] == nord[1]) || 
					(cell[0] == sud[0] && cell[1] == sud[1]) || 
					(cell[0] == est[0] && cell[1] == est[1]) || 
					(cell[0] == ovest[0] && cell[1] == ovest[1]))
				return true;
		}
		
		return false;
	}
	
	public int rookDifferenceCount() {
		return this.whitePieces-this.blackPieces;
	}
	
	public int manhattamDistanceToClosestGoal() {
		int min = Board.nRow + Board.nColumns + 1;
		int val;
		int []kingPos = Board.indexToCoordinate(this.kindPosition);
		for(int [] pos : Board.finalCellsSet) {
			
			val = Math.abs(kingPos[1] - pos[1]) + Math.abs(kingPos[0] - kingPos[0]);
			min = val < min ? val : min;
		}
		return min;
	}
	
	
	public int numeroDirezioniRook(int r, int c) {
		int val = 0;
		int []dir = this.numberOfOccupiableCells(r, c);
		for(int i : dir)
			val = i > 0 ? val + 1 : val;
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
		
		int spazi[] = new int[Board.DIREZIONI];
		int val = 0;
		if(player == Player.WHITE) {
			
			for(Piece p : this.board.values()) {
				if(p.getType() == Type.WHITE_ROOK) {
					int []pos = Board.indexToCoordinate(p.getPosition());
					spazi = this.numberOfOccupiableCells(pos[0], pos[1]);
					val += spazi[Board.NORD] + spazi[Board.SUD] + spazi[Board.EST] +spazi[Board.OVEST];
					
				}
			}
			
		}else {
			
			for(Piece p : this.board.values()) {
				if(p.getType() == Type.BLACK_ROOK) {
					int []pos = Board.indexToCoordinate(p.getPosition());
					spazi = this.numberOfOccupiableCells(pos[0], pos[1]);
					val += spazi[Board.NORD] + spazi[Board.SUD] + spazi[Board.EST] +spazi[Board.OVEST];
					
				}
			}
			
		}
		return val;
	}
	
	public int numeroCaselleDiponibiliKing() {
		int []kingPos = Board.indexToCoordinate(this.kindPosition);
		int []spazi = this.numberOfOccupiableCells(kingPos[0], kingPos[1]);
		return spazi[Board.NORD] + spazi[Board.SUD] + spazi[Board.EST] +spazi[Board.OVEST];
	}
	
	
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

	
	public int[] numberOfOccupiableCells(int pos) {
		int []p = Board.indexToCoordinate(pos);
		return this.numberOfOccupiableCells(p[0], p[1]);
	}
	

	public int[] numberOfOccupiableCells(int r, int c) {
		int[] ret = new int[Board.DIREZIONI];
		for(int i = 0; i < ret.length; i++) {
			ret[i] = 0;
		}
		
			
			
			Piece pezzo = this.getCell(r, c);
			
			//NORD
			int nord = 0;
			for(int i = r+1 ; i <= Board.nRow; i++) {

				if(board.containsKey(Board.coordinateToIndex(i, c)))
					break;//Pezzo presente non posso passare
				else if((Board.isForbidden(i, c) && !pezzo.canMooveInsideForbiddenArea()))
					break;
				else
					nord++;
			}
			ret[NORD] = nord;
			//SUD
			int sud = 0;
			for(int i = r-1 ; i > 0; i--) {
				if(board.containsKey(Board.coordinateToIndex(i, c)))
					break;
				else if((Board.isForbidden(i, c) && !pezzo.canMooveInsideForbiddenArea()))
					break;
				else
					sud++;
			}
			ret[SUD] = sud;
			
			//EST 
			int est = 0;
			for(int i = c+1 ; i <= Board.nColumns; i++) {
				if(board.containsKey(Board.coordinateToIndex(r, i)))
					break;
				else if((Board.isForbidden(r, i) && !pezzo.canMooveInsideForbiddenArea()))
					break;
				else
					est++;
			}
			ret[Board.EST] = est;
			
			//OVEST
			int ovest = 0;
			for(int i = c-1 ; i > 0; i--) {
				if(board.containsKey(Board.coordinateToIndex(r, i)))
					break;
				else if((Board.isForbidden(r, i) && !pezzo.canMooveInsideForbiddenArea()))
					break;
				else
					ovest++;
			}
			ret[Board.OVEST] = ovest;
			
		 
		return ret;
	}
	
	
	public int zonaProibita(int p) {
		if(Board.zonaProibita1Set.contains(Board.indexToCoordinate(p)))
			return 1;
		if(Board.zonaProibita2Set.contains(Board.indexToCoordinate(p)))
			return 2;
		if(Board.zonaProibita3Set.contains(Board.indexToCoordinate(p)))
			return 3;
		
		return 4;
	}
	
	public boolean muoveTraZonePoibiteDiverse(Piece p, int position) {
		if(zonaProibita(p.getPosition()) != zonaProibita(position))
			return true;
		else return false;
	}
	
	public boolean isLegalMove(Piece p, int position) {
		
		if(this.board.containsKey(position))
			return false;
		
		if(Board.isForbidden(position) && !p.canMooveInsideForbiddenArea()) {
			return false;
		}else if(Board.isForbidden(position) && p.canMooveInsideForbiddenArea() && muoveTraZonePoibiteDiverse(p, position)) { 
			return false;
		}else {
			return true;
		}
		
	}
	
	public boolean isLegalMove(Piece p, int row, int column) {
		return this.isLegalMove(p, Board.coordinateToIndex(row, column));
	}
	
	public int[] possibleMovesByPiece(Piece p) {
		List<Integer> moves = new ArrayList<>();
		
		int row = p.getRow();
		int column = p.getColumn();
				
		
		//Aumento le righe NORD
		for(int r = row+1 ; r<= Board.nRow; r++) {
			if(this.isLegalMove(p, r, column))
				moves.add(Board.coordinateToIndex(r, column));
			else
				break;
		}
		
		//Diminuisco le righe SUD
		for(int r = row-1 ; r>=1; r--) {
			if(this.isLegalMove(p, r, column))
				moves.add(Board.coordinateToIndex(r, column));
			else
				break;
		}
		
		//Aumento le colonne EST
		for(int c = column+1; c <= Board.nColumns; c++) {
			if(this.isLegalMove(p, row, c))
				moves.add(Board.coordinateToIndex(row, c));
			else
				break;
		}
		
		//Diminuisco le colonne OVEST
		for(int c = column-1; c >=1; c--) {
			if(this.isLegalMove(p, row, c))
				moves.add(Board.coordinateToIndex(row, c));
			else
				break;
		}
		
		int []res = new int[moves.size()];
		int i = 0;
		for(int e : moves) {
			res[i] = e;
			i++;
		}
		
		return res;
		
	}
	
	public HashMap<Integer, Piece> clone(){
		HashMap<Integer, Piece> res = new HashMap<>();
		for(int i: this.board.keySet()) {
			res.put(i, this.board.get(i));
		}
		return res;
	}
	

	public List<Mossa> getNextMovesByPlayer(Player player){
		HashMap<Integer, Piece> newBoard;
		List<Mossa> result = new ArrayList<>();
		int []moves;
		if(player == Player.WHITE) {

			for(Integer p: this.board.keySet()) {
				Piece piece = getCell(p);

				if(piece.getType() == Type.WHITE_ROOK || piece.getType() == Type.KING) {
					moves = this.possibleMovesByPiece(piece);

					for(int m : moves) {

						newBoard = this.clone();
						int oldPos = piece.getPosition();
						newBoard.remove(piece.getPosition());

						
						Piece newPiece = new Piece(piece.getType(), m);
						newBoard.put(m, newPiece);
						result.add(new Mossa(new Board(newBoard), newPiece, oldPos));

					}
				}
			}
		}else {
			for(Integer p: this.board.keySet()) {
				Piece piece = getCell(p);
				if(piece.getType() == Type.BLACK_ROOK) {
					moves = this.possibleMovesByPiece(piece);
					for(int m : moves) {

						newBoard = this.clone();
						int oldPos = piece.getPosition();
						Piece newPiece = new Piece(piece.getType(), m);
						newBoard.remove(piece.getPosition());

						newBoard.put(m, newPiece);
						result.add(new Mossa(new Board(newBoard), newPiece, oldPos));
					}
				}
			}
		}
		if(result.isEmpty())
			return null;
		
		return result;
	}
	
	public static int[] indexToCoordinate(int index) {
		index--;
		int r = index / nColumns + 1 ;
		int c = index - nRow * (r-1) + 1;
		
		return new int[]{r,c};
	}
	
	public static int coordinateToIndex(int r, int c) {
		return (r-1) * nColumns + c;
	}
	
	public List<Piece> getAllPieces() {
		List<Piece> ret = new ArrayList<>();
		for(Piece p : this.board.values())
			ret.add(p);
		return ret;
	}
	
	public boolean whiteWin() {
	//WHITE WIN
		for(int []p : Board.finalCellsSet) {
			if(this.kindPosition == Board.coordinateToIndex(p[0], p[1]))
				return true;
		}
		return false;
	}
	
	public boolean blackWin() {
			//BLACK WIN
		int []k = Board.indexToCoordinate(this.kindPosition);
		if(this.blackAroundKing() == 2 && !(this.kindPosition == Board.NORD_CENTER_POS || this.kindPosition == Board.EST_CENTER_POS || this.kindPosition == Board.SUD_CENTER_POS || this.kindPosition == Board.OVEST_CENTER_POS)) {
			return true;
		}
		else if(this.blackAroundKing() == 3 && (this.kindPosition == Board.NORD_CENTER_POS || this.kindPosition == Board.EST_CENTER_POS || this.kindPosition == Board.SUD_CENTER_POS || this.kindPosition == Board.OVEST_CENTER_POS))
			return true;
		else if(k[0] > 1 && this.getCell(k[0]+1, k[1]).getType() == Type.BLACK_ROOK && Board.isForbidden(k[0]-1, k[1])  
				||  k[0] < 9 && this.getCell(k[0]-1, k[1]).getType() == Type.BLACK_ROOK && Board.isForbidden(k[0]+1, k[1]) 
				||  k[1] > 1 && this.getCell(k[0], k[1]+1).getType() == Type.BLACK_ROOK && Board.isForbidden(k[0], k[1]-1)  
				||  k[1] < 9 && this.getCell(k[0], k[1]-1).getType() == Type.BLACK_ROOK && Board.isForbidden(k[0], k[1]+1) ){
			return true;
		}
		return false;
			
	}


	@Override
	public int compareTo(Board other) {
		return Integer.compare(this.rookDifferenceCount(), other.rookDifferenceCount());
	}


	@Override
	public int compare(Board o1, Board o2) {
		return Integer.compare(o1.rookDifferenceCount(), o2.rookDifferenceCount());

	}
	
	@Override
	public String toString() {
		String s = "";
		for(Piece p : this.board.values()) {
			if(p.getType() == Type.BLACK_ROOK) {
				s += "Black Rook R: "+ p.getRow()+ " C: "+p.getColumn() + " ";
			}else if(p.getType() == Type.WHITE_ROOK) {
				s += "White Rook R: "+ p.getRow()+ " C: "+p.getColumn() + " ";
			}else if(p.getType() == Type.KING) {
			 s += "King R: "+ p.getRow()+ " C: "+p.getColumn() + " ";
			}
		}
		return s;
	}
	
	
	

}
