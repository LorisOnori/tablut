package player;

public class Piece {
	
	public Type type;
	public int position;
	public boolean canMooveInsideForbiddenArea = true;
	
	public Piece(Type type) {
		this.type = type;
		if(this.type == Type.WHITE_ROOK)
			canMooveInsideForbiddenArea = false;
			
	}
	
	public Type getType() {
		return this.type;
	}


	
	public int getPosition() {
		return this.position;
	}
	
	public void setPosition(int position) {
		this.position = position;
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
