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
										{9,4},{9,5},{9,6},{8,5},center};
	
	public final static Set<int[]> forbiddenCellsSet = new HashSet<>(Arrays.asList(forbiddenCells));

	
	
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
				if(p == Pawn.BLACK) {
					board.put(Board.coordinateToIndex(i, j), new Piece(Type.BLACK_ROOK, i, j));
				}else if(p == Pawn.WHITE) {
					board.put(Board.coordinateToIndex(i, j), new Piece(Type.WHITE_ROOK, i, j));
				}else if(p == Pawn.KING) {
					board.put(Board.coordinateToIndex(i, j), new Piece(Type.KING, i, j));
				}
				
			}
		}
		this.updateBoard();
		
	}
	
	
	public Board(String s, int nRow, int nColumns) {
		
		//Get all the infos from the data
		//riempo board
		
		Board.nColumns = nColumns;
		Board.nRow = nRow;
		
		//Probabilmente facendo cos�, ogni volta che ricevo una nuova posizione aggiorno solamente quella precedente
		//e non ricreo Board da capo
		
		
		//TODO magari non stringa ma direttamente la mappa
		this.updateBoard();
		
		
		
		
	}
	
	public Board(HashMap<Integer,Piece> board) {
		this.board = board;
	
		//TODO magari non stringa ma direttamente la mappa
		this.updateBoard();
		
	}
	
	public static boolean isForbidden(int r, int c) {
		for(int[] f : Board.forbiddenCellsSet) {
			//System.out.println("R "+r + " C " +c+ " f[0] " + f[0] + " f[1] "+ f[1]);
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
	
	//Magari non stringa ma direttamente la maooa
	public void updateBoard() {
		
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
	
	public Piece getCell(int p) {
		int []pos = Board.indexToCoordinate(p);
		return this.getCell(pos[0], pos[1]);
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
	
	//public getAllPieces() 	
	
	//Nella genereazione di mosse (non qui) posso valutare tutte le mosse e ordinarle in base alla differenza pezzi che dovrebbe essere l'indice pi� importate
	//Minmax quindi esplora prima queste mosse e poi le altre.
	//Valuta la posizione corrente
	public int eval(int wDifferenzaPezzi, int wDirezioniRe, int wPosizioniOccRe, int wDistanzaDalGoal, int wPosizioniOccupabiliTorriBianco,
					int wPosizioniOccupabiliTorriNero , int wDirezioniTorriBianco, int wDirezioniTorriNero, int wBlackAroundKing) {
		//TODO 
		
		
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
		
		
		int []rooks = this.getRooksByPlayer(Player.WHITE);
		int dirWhite = 0;
		int dirBlack = 0;
		for(int r : rooks) {
			dirWhite += this.numeroDirezioniRook(r);
		}
		
		rooks = this.getRooksByPlayer(Player.BLACK);
		for(int r : rooks) {
			dirBlack += this.numeroDirezioniRook(r);
		}
		
		return wDifferenzaPezzi * this.rookDifferenceCount() + wDirezioniRe * this.numeroDirezioniRe() + wPosizioniOccRe * this.numeroCaselleDiponibiliKing() 
				+ wDistanzaDalGoal * this.manhattamDistanceToClosestGoal() + wPosizioniOccupabiliTorriBianco * this.numeroCaselleDisponibiliRookByPlayer(Player.WHITE)
				+ wPosizioniOccupabiliTorriNero * this.numeroCaselleDisponibiliRookByPlayer(Player.BLACK)+ wDirezioniTorriBianco * dirWhite 
				+ wDirezioniTorriNero * dirBlack + wBlackAroundKing * this.blackAroundKing();
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
	
/*	public int rookDifferenceCount() { //Senza contare il re
		int val = 0;
		for(Piece p : board.values()) {
			if(p.getType() == Type.WHITE_ROOK)
				val ++;
			else val--;
		}
		return val+1; //Il re altrimenti � contato come nero
 	}*/
	
	public int rookDifferenceCount() {
		return this.whitePieces-this.blackPieces;
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
	
	public int[] numberOfOccupiableCells(int pos) {
		int []p = Board.indexToCoordinate(pos);
		return this.numberOfOccupiableCells(p[0], p[1]);
	}
	
	//Distanza del prossimo ostacolo, amico o nemico che sia -> Numero di caselle occupabili per direzione
	//Non posso attraversare le zone proibite
	public int[] numberOfOccupiableCells(int r, int c) {
		int[] ret = new int[Board.DIREZIONI];
		for(int i = 0; i < ret.length; i++) {
			ret[i] = 0;
		}
		
			
			
			Piece pezzo = this.getCell(r, c);
			
			//NORD
			int nord = 0;
			for(int i = r+1 ; i <= Board.nRow; i++) {
				//Non posso andare se c'� un pezzo -> � nella board oppure se fa parte della caselle proibite e io non posso andarci
				//Posso eventualmente attravesrare le zone proibite
				//Ma non posso se c'� un pezzo
				if(board.containsKey(Board.coordinateToIndex(i, c)))
					break;//Pezzo presente non posso passare
				else if((Board.isForbidden(i, c) && !pezzo.canMooveInsideForbiddenArea()))
					break;
				else
					nord++;
			}
			ret[Board.NORD] = nord;
			//SUD
			int sud = 0;
			for(int i = r-1 ; i > 0; i--) {
				//Non posso andare se c'� un pezzo -> � nella board oppure se fa parte della caselle proibite e io non posso andarci
				if(board.containsKey(Board.coordinateToIndex(i, c)))
					break;
				else if((Board.isForbidden(i, c) && !pezzo.canMooveInsideForbiddenArea()))
					break;
				else
					sud++;
			}
			ret[Board.SUD] = sud;
			
			//EST 
			int est = 0;
			for(int i = c+1 ; i <= Board.nColumns; i++) {
				//Non posso andare se c'� un pezzo -> � nella board oppure se fa parte della caselle proibite e io non posso andarci
				//boolean can = Board.forbiddenCellsSet.contains(new int[] {r,i});
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
				//Non posso andare se c'� un pezzo -> � nella board oppure se fa parte della caselle proibite e io non posso andarci
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
	
	/*
	public int getDistanceOfNextObstacleInRawByPlayer(int r, Player player) {
		
	}
	*/
	
	//Considero se la destinazione del pezzo va bene (non quello che c'� di mezzo)
	public boolean isLegalMove(Piece p, int position) {
		
		if(this.board.containsKey(position))
			return false;
		
		if(Board.isForbidden(position) && p.canMooveInsideForbiddenArea())
			return true;
		else 
			return false;
		
	}
	
	public boolean isLegalMove(Piece p, int row, int column) {
		return this.isLegalMove(p, Board.coordinateToIndex(row, column));
	}
	
	//Attento se ritorna null
	//Ritorna tutte le coordinate di arrivo del possibile pezzo
	public int[] possibleMovesByPiece(Piece p) {
		List<Integer> moves = new ArrayList<>();
		
		//da 1 a 9, l'incremento pu� essere solo di riga o di colonna
		int row = p.getRow();
		int column = p.getColumn();
				
		
		//Aumento le righe NORD
		for(int r = row+1 ; r<= Board.nRow; r++) {
			if(this.isLegalMove(p, r, column))
				moves.add(Board.coordinateToIndex(r, column));
		}
		
		//Diminuisco le righe SUD
		for(int r = row-1 ; r<= Board.nRow; r--) {
			if(this.isLegalMove(p, r, column))
				moves.add(Board.coordinateToIndex(r, column));
		}
		
		//Aumento le colonne EST
		for(int c = column+1; c <= Board.nColumns; c ++) {
			if(this.isLegalMove(p, row, c))
				moves.add(Board.coordinateToIndex(row, c));
		}
		
		//Diminuisco le colonne OVEST
		for(int c = column-1; c <= Board.nColumns; c ++) {
			if(this.isLegalMove(p, row, c))
				moves.add(Board.coordinateToIndex(row, c));
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
	
	//TODO
	//Devo controllare se al seguito di una mossa un pezzo viene mangiato
	//In quel caso devo rimuoverlo
	//Per� quando mando la risposta il mezzo FORSE deve rimanere
	//Poi il server mi risponde senza il pezzo catturato
	
	//Ritorno tutte le mosse possibili
	//Valuto tutte le mosse 
	//Ordino le mosse
	//Minmax
	public List<Mossa> getNextMovesByPlayer(Player player){
		HashMap<Integer, Piece> newBoard = this.clone();
		List<Mossa> result = new ArrayList<>();
		int []moves;
		if(player == Player.WHITE) {
			for(Integer p: this.board.keySet()) {
				Piece piece = getCell(p);
				if(piece.getType() == Type.WHITE_ROOK || piece.getType() == Type.KING) {
					moves = this.possibleMovesByPiece(piece);
					for(int m : moves) {
						int oldPos = piece.getPosition();
						
						newBoard.remove(piece.getPosition());
						piece.setPosition(m);
						newBoard.put(m, piece);
						result.add(new Mossa(new Board(newBoard), piece, oldPos));
					}
				}
			}
		}else {
			for(Integer p: this.board.keySet()) {
				Piece piece = getCell(p);
				if(piece.getType() == Type.BLACK_ROOK) {
					moves = this.possibleMovesByPiece(piece);
					for(int m : moves) {
						int oldPos = piece.getPosition();
						newBoard.remove(piece.getPosition());
						piece.setPosition(m);
						newBoard.put(m, piece);
						result.add(new Mossa(new Board(newBoard), piece, oldPos));
					}
				}
			}
		}
		if(result.isEmpty())
			//Nessuno mossa possibile, quel giocatore ha perso
			//TODO
			return null;
		
		return result;
	}
	
	public static int[] indexToCoordinate(int index) {
		int r = index / nColumns + 1 ;
		int c = index - nRow * (r-1);
		
		return new int[]{r,c};
	}
	
	public static int coordinateToIndex(int r, int c) {
		return (r-1) * nColumns + c;
	}


	@Override
	public int compareTo(Board other) {
		return Integer.compare(this.rookDifferenceCount(), other.rookDifferenceCount());
	}


	@Override
	public int compare(Board o1, Board o2) {
		return Integer.compare(o1.rookDifferenceCount(), o2.rookDifferenceCount());

	}
	
	
	

}
