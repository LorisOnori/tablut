package it.unibo.ai.didattica.competition.tablut.AI;

import java.io.IOException;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class Client extends TablutClient{
	
	private static final String NAME = "Trent";
	private static int timeOut = 10;
	private final int currentDepthLimit;
	private static int [] weight = null;


	public Client(String player, int [] weight) throws UnknownHostException, IOException {
		super(player, NAME);
		Client.weight = weight;
		currentDepthLimit = 4;
	}


	public Client(String player, int timeout, String ipAddress) throws UnknownHostException, IOException {
		super(player, NAME , timeout, ipAddress);
		Client.timeOut = timeout -3;
		currentDepthLimit = 4;
	}

	public Client(String player, int timeout, String ipAddress, int depth) throws UnknownHostException, IOException {
		super(player, NAME , timeout, ipAddress);
		Client.timeOut = timeout - 2;  //Changed
		this.currentDepthLimit = depth;
	}

	public Client(String player) throws IOException {
		super(player, NAME);
		Client.timeOut = 8;
		currentDepthLimit = 4;
	}

	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {

		if (args.length == 0) {
			System.out.println("You must specify which player you are (WHITE or BLACK)!");
			System.exit(-1);
		}
		System.out.println("Selected this: " + args[0]);

		TablutClient client = null;
		if(args.length == 1)
			//COLORE
			client = new Client(args[0]);
		else if(args.length == 3)
			//gli argomenti devono essere il ruolo (White or Black), il timeout in secondi, e l'indirizzo IP del server.
			client = new Client(args[0].toUpperCase(), Math.round(Float.parseFloat(args[1])) - 2, args[2]);
		else if (args.length == 4)
			//gli argomenti devono essere il ruolo (White or Black), il timeout in secondi, e l'indirizzo IP del server, depth
			client = new Client(args[0].toUpperCase(), Math.round(Float.parseFloat(args[1])) - 2, args[2], Integer.parseInt(args[3]));
		else {System.out.println("Usage: role timeout IP ; role"); System.exit(1);}

		client.run();

	}
	

	@Override
	public void run() {
		System.out.println("You are player " + this.getPlayer().toString() + "!");
		Action action;
		//Minmax minmax = new Minmax(currentDepthLimit, getPlayer());

		try {
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (this.getPlayer() == Turn.WHITE) {
			faiIlClient(Turn.WHITE, Turn.BLACK, "YOU WIN!", "YOU LOSE!");
			return;
		} else {

			faiIlClient(Turn.BLACK, Turn.WHITE, "YOU LOSE!", "YOU WIN!");
		}
	}

	private void faiIlClient(Turn turnoMio, Turn turnoNemico, String s, String s2) {

		Action action;
		Minmax mm = Minmax.getInstance();
		while (true) {

			try{
				this.read();
				System.out.println("Current state:");
				System.out.println(this.getCurrentState().toString());
			} catch (Exception e) {
				System.out.println("Errore nella lettura nuovo stato");
				e.printStackTrace();
			}

			try {
				if (this.getCurrentState().getTurn().equals(turnoMio)) {

					/*****ACTION*****/
					long start = System.currentTimeMillis();
					
					System.out.println("START");
					Mossa res = mm.iterative(new Board(getCurrentState().clone()), turnoMio);
					System.out.println("MOSSA DA "+res.getOldPos()+ " A "+res.getNewPos());
					//a1 - a9 --- i1 - i9
					int []old = Board.indexToCoordinate(res.getOldPos());
					int []n = Board.indexToCoordinate(res.getNewPos());
					action = new Action(this.getCurrentState().getBox(old[0]-1, old[1]-1), this.getCurrentState().getBox(n[0]-1, n[1]-1), this.getCurrentState().getTurn());

					this.write(action);

					long end = System.currentTimeMillis();
					System.out.println("Ci ho messo " + (end - start) + " millisecs");
					/**************/

				} else if (this.getCurrentState().getTurn().equals(turnoNemico)) {
					System.out.println("Waiting for your opponent move... ");
				} else if (this.getCurrentState().getTurn().equals(Turn.WHITEWIN)) {
					System.out.println(s);
					System.exit(0);
				} else if (this.getCurrentState().getTurn().equals(Turn.BLACKWIN)) {
					System.out.println(s2);
					System.exit(0);
				} else if (this.getCurrentState().getTurn().equals(Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	
	}

	public static int getTimeOut() {
		return timeOut;
	}
}
	
	//Connect
	
	//Send infos (name)
	
	//Get color(white/black) and time
	
	//Loop
		//Get state
		
		//Evaluate the state e return the best state
	
		
		//Send answer state
		
	
