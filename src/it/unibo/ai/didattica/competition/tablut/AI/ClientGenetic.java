package it.unibo.ai.didattica.competition.tablut.AI;

import java.io.IOException;
import java.net.UnknownHostException;

public class ClientGenetic {

	public static void main(boolean colore, int [] w) throws UnknownHostException, ClassNotFoundException, IOException {
		Heuristic.setWeight(w);
		String []array = colore ? new String[] {"WHITE"} : new String[] {"BLACK"};
		Client.main(array);

	}

}
