package it.unibo.ai.didattica.competition.tablut.AI;

import java.util.Comparator;

public class Mossa implements Comparator<Mossa>, Comparable<Mossa>{

	
	private Board b;
	private Piece piece;
	private Board boardAggiornata;
	private int oldPos;
	
	//Passo la nuova board con l'ultimo pezzo mosso
	/*public Mossa(Board b, Piece p) {
		this.b = b;
		this.piece = p;
		this.boardAggiornata = this.calcolaCatture();
	}*/
	
	public Mossa(Board board, Piece piece2, int oldPos) {
		this.b = board;
		this.piece = piece2;
		this.oldPos = oldPos;
		this.boardAggiornata = this.calcolaCatture();
	}
	
	public Mossa clone() {
		return new Mossa(this.b, this.piece, this.oldPos);
	}

	public Board getBoardSenzaCatture() {
		return this.b;
	}
	
	public int getOldPos() {
		return this.oldPos;
	}
	
	public Piece getPiece() {
		return this.piece;
	}
	
	public int getNewPos() {
		return this.piece.getPosition();
	}
	//Valuto la nuova board senza i pezzi mangiati
	//La vittoria viene calcolata nella eval
	public Board calcolaCatture() {
		Board board = new Board(this.b.clone());
		
		if(this.piece.getType() == Type.WHITE_ROOK || this.piece.getType() == Type.KING) {
			
			
			//controllo nord
			if(piece.getRow() < Board.nRow-1
					&& board.getCell(this.piece.getRow()+1, this.piece.getColumn()).getType() == Type.BLACK_ROOK 
					&& (board.getCell(this.piece.getRow()+2, this.piece.getColumn()).getType() == Type.WHITE_ROOK ||
						board.getCell(this.piece.getRow()+2, this.piece.getColumn()).getType() == Type.KING) ||
						Board.isForbidden(this.piece.getRow()+2, this.piece.getColumn())) {
				//Pezzo Catturato
				//elimino il pezzo
				board.removePiece(this.piece.getRow()+1, this.piece.getColumn(), Type.WHITE_ROOK);
				
			}
			//controllo sud
			if(piece.getRow() > 2
					&& board.getCell(this.piece.getRow()-1, this.piece.getColumn()).getType() == Type.BLACK_ROOK 
					&& (board.getCell(this.piece.getRow()-2, this.piece.getColumn()).getType() == Type.WHITE_ROOK ||
						board.getCell(this.piece.getRow()-2, this.piece.getColumn()).getType() == Type.KING) ||
						Board.isForbidden(this.piece.getRow()-2, this.piece.getColumn())) {
				//Pezzo Catturato
				board.removePiece(this.piece.getRow()-1, this.piece.getColumn(), Type.WHITE_ROOK);
				
			}
			
			//controllo est
			if(piece.getColumn() < Board.nColumns-1
					&& board.getCell(this.piece.getRow(), this.piece.getColumn()+1).getType() == Type.BLACK_ROOK 
					&& (board.getCell(this.piece.getRow(), this.piece.getColumn()+2).getType() == Type.WHITE_ROOK ||
						board.getCell(this.piece.getRow(), this.piece.getColumn()+2).getType() == Type.KING) ||
						Board.isForbidden(this.piece.getRow(), this.piece.getColumn()+2)) {
				//Pezzo Catturato
				board.removePiece(this.piece.getRow(), this.piece.getColumn()+1, Type.WHITE_ROOK);
				
			}
			
			//controllo ovest
			if(piece.getColumn() > 2
					&& board.getCell(this.piece.getRow(), this.piece.getColumn()-1).getType() == Type.BLACK_ROOK 
					&& (board.getCell(this.piece.getRow(), this.piece.getColumn()-2).getType() == Type.WHITE_ROOK ||
						board.getCell(this.piece.getRow(), this.piece.getColumn()-2).getType() == Type.KING) ||
						Board.isForbidden(this.piece.getRow(), this.piece.getColumn()-2)) {
				//Pezzo Catturato
				board.removePiece(this.piece.getRow(), this.piece.getColumn()-1, Type.WHITE_ROOK);
			}
			
			
			
		} //- ---------------------- BLACK ----------------------------------------------------
		
		else if(this.piece.getType() == Type.BLACK_ROOK) {
			if(piece.getRow() < Board.nRow-1
					&& board.getCell(this.piece.getRow()+1, this.piece.getColumn()).getType() == Type.WHITE_ROOK 
					&& (board.getCell(this.piece.getRow()+2, this.piece.getColumn()).getType() == Type.BLACK_ROOK ||
						Board.isForbidden(this.piece.getRow()+2, this.piece.getColumn()))) {
				//Pezzo Catturato
				//elimino il pezzo
				board.removePiece(this.piece.getRow()+1, this.piece.getColumn(), Type.BLACK_ROOK);
				
			}
			//controllo sud
			if(piece.getRow() > 2
					&& board.getCell(this.piece.getRow()+1, this.piece.getColumn()).getType() == Type.WHITE_ROOK 
					&& (board.getCell(this.piece.getRow()+2, this.piece.getColumn()).getType() == Type.BLACK_ROOK ||
						Board.isForbidden(this.piece.getRow()+2, this.piece.getColumn()))) {
				//Pezzo Catturato
				board.removePiece(this.piece.getRow()-1, this.piece.getColumn(), Type.BLACK_ROOK);
				
			}
			
			//controllo est
			if(piece.getColumn() < Board.nColumns-1
					&& board.getCell(this.piece.getRow()+1, this.piece.getColumn()).getType() == Type.WHITE_ROOK 
					&& (board.getCell(this.piece.getRow()+2, this.piece.getColumn()).getType() == Type.BLACK_ROOK ||
						Board.isForbidden(this.piece.getRow()+2, this.piece.getColumn()))) {
				//Pezzo Catturato
				board.removePiece(this.piece.getRow(), this.piece.getColumn()+1, Type.BLACK_ROOK);
				
			}
			
			//controllo ovest
			if(piece.getColumn() > 2
					&& board.getCell(this.piece.getRow()+1, this.piece.getColumn()).getType() == Type.WHITE_ROOK 
					&& (board.getCell(this.piece.getRow()+2, this.piece.getColumn()).getType() == Type.BLACK_ROOK ||
						Board.isForbidden(this.piece.getRow()+2, this.piece.getColumn()))) {
				//Pezzo Catturato
				board.removePiece(this.piece.getRow(), this.piece.getColumn()-1, Type.BLACK_ROOK);
			}
			
		}
		
		return board;
		
		
	}
	
	public Board getBoardAggiornata() {
		return this.boardAggiornata;
	}
	
	
	public static int eval(Board board) {
		//TODO
		//Guardo anche sulla trasposition table per eventuali pareggi
		//In minmax controllo anche se le mosse sono 0, in quel caso quel giocatore ha perso
		return (int) Double.NaN;
	}


	@Override
	public int compare(Mossa o1, Mossa o2) {
		return Integer.compare(o1.getBoardAggiornata().rookDifferenceCount(), o2.getBoardAggiornata().rookDifferenceCount());
	}


	@Override
	public int compareTo(Mossa o) {
		return Integer.compare(this.getBoardAggiornata().rookDifferenceCount(), o.getBoardAggiornata().rookDifferenceCount());

	}
	
	
	
	
	
}
