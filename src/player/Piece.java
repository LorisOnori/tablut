package player;

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
	/*
	public boolean canMoove(int r, int c) {
		
	}
	
	public boolean canMoove(int d) {
		int []pos = Board.indexToCoordinate(d);
		return this.canMoove(pos[0], pos[1]);
	}*/


	
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
			//check if he can no long enter in the forbidden area
			int pos[] = Board.indexToCoordinate(position);
			boolean dentro = false;
			for(int []p : Board.forbiddenCells) {
				if(p[0] == pos[0] && p[1] == pos[1]) { // Stessa riga e colonna
					dentro = true; //Si è mosso ma è ancora dentro quindi può ancora muoversi
					break;
				}
			}
			this.canMooveInsideForbiddenArea = dentro;
		}
	}
	
	

}
