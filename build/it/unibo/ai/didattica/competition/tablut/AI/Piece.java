package it.unibo.ai.didattica.competition.tablut.AI;

public class Piece {
	
	private Type type;
	private int position;
	private int row;
	private int column;
	private boolean canMooveInsideForbiddenArea = true;
	
	public Piece(Type type, int position) {
		this.type = type;
		if(this.type == Type.WHITE_ROOK || this.type == Type.KING)
			canMooveInsideForbiddenArea = false;
		this.setPosition(position);
	}
	
	public Piece(Type type, int r, int c) {
		this(type, Board.coordinateToIndex(r, c));
	}
	
	public Type getType() {
		return this.type;
	}
	
	
	
	public boolean canMooveInsideForbiddenArea() {
		return this.canMooveInsideForbiddenArea;
	}


	
	public int getPosition() {
		return this.position;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getColumn() {
		return this.column;
	}
	
	public void setPosition(int position) {
		this.position = position;
		int []poss = Board.indexToCoordinate(this.position);
		this.row = poss[0];
		this.column = poss[1];
		if(canMooveInsideForbiddenArea) {
			int pos[] = Board.indexToCoordinate(position);
			boolean dentro = false;
			for(int []p : Board.forbiddenCells) {
				if(p[0] == pos[0] && p[1] == pos[1]) { // Stessa riga e colonna
					dentro = true;
					break;
				}
			}
			this.canMooveInsideForbiddenArea = dentro;
		}
	}
	
	public Piece clone() {
		return new Piece(this.type, this.position);
	}
	
	

}
