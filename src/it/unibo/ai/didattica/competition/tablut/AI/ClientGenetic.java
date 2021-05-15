package it.unibo.ai.didattica.competition.tablut.AI;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class ClientGenetic {

	public static void main(String []args) throws UnknownHostException, ClassNotFoundException, IOException {
		//(boolean colore, int [] w) 
		StringTokenizer st = new StringTokenizer(args[1]);
		int []p = new int[Heuristic.WEIGHTS];
		
		for(int i = 0 ; i<Heuristic.WEIGHTS; i++) {
			p[i] = Integer.parseInt(st.nextToken());
		}
		
		Heuristic.setWeight(p);
		
		String []array = new String[]{args[0]};
		Client.main(array);

	}

}
