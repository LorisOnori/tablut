package it.unibo.ai.didattica.competition.tablut.genetic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import it.unibo.ai.didattica.competition.tablut.AI.Client;
import it.unibo.ai.didattica.competition.tablut.AI.ClientBlack;
import it.unibo.ai.didattica.competition.tablut.AI.ClientGenetic;
import it.unibo.ai.didattica.competition.tablut.AI.ClientWhite;
import it.unibo.ai.didattica.competition.tablut.AI.Player;
import it.unibo.ai.didattica.competition.tablut.server.ServerGenetic;

public class Main {

	public final static int population = 10;
	public final static int generation = 5;
	private final static String pesi = "/home/tablut/genetic/weight.txt";
	
	private static final double DIFF  = 16/8;
	
//	private static final double W_DIFFERENZA_PEZZI = 300;
//	private static final double W_DIREZIONI_RE = 20;
//	private static final double W_POSIZIONI_OCC_RE = 3;
//	private static final double W_DISTANZA_GOAL = 10;
//	private static final double W_POSIZIONI_OCCUPABILI_TORRI_BIANCO = 4 * DIFF;
//	private static final double W_POSIZIONI_OCCUPABILI_TORRI_NERO = 4 ;
//	private static final double W_DIREZIONI_TORRI_BIANCO = 2 * DIFF;
//	private static final double W_DIREZIONI_TORRI_NERO = 2;
//	private static final double W_BLACK_AROUND_KING = 50;
	
	//INDEX
	private static final int W_DIFFERENZA_PEZZI = 0;
	private static final int W_DIREZIONI_RE = 1;
	private static final int W_POSIZIONI_OCC_RE = 2;
	private static final int W_DISTANZA_GOAL = 3;
	private static final int W_POSIZIONI_OCCUPABILI_TORRI_BIANCO = 4;
	private static final int W_POSIZIONI_OCCUPABILI_TORRI_NERO = 5 ;
	private static final int W_DIREZIONI_TORRI_BIANCO = 6;
	private static final int W_DIREZIONI_TORRI_NERO = 7;
	private static final int W_BLACK_AROUND_KING = 8;
	private static final int WEIGHTS = 9;
	
	private static int [] weightBase = {300, 20,3,10,8,4,4,2,50};

	
	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		
		Client [] cw = new Client[population];
		Client [] cb = new Client[population];
		
		List<int[]> pesiVincentiWhite = new ArrayList<>();
		List<int[]> pesiVincentiBlack = new ArrayList<>();
		
		//Creo la popoplazione
		for(int i = 0 ; i< population; i++) {
			try {
				cw[i] = new Client("WHITE", generaPesi());
				cb[i] = new Client("BLACK", generaPesi());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		/*****
		 * 
		 * Devo selvare i pesi da passare ai clienti
		 * Quando finisco il primo ciclo creo 5 nuovi giocatori e lascio i 5 vincenti
		 * Inverto le parti di gioco --> Se era un vincente col bianco, ora gioca col nero e così via
		 * 
		 */
		
		
		for(int g = 0; g < generation; g++) {
			System.out.println("GENERAZIONE "+g);
			for(int p = 0; p<population; p++) {
				
				int[] w1 = generaPesi();
				int[] w2 = generaPesi();
				
				 ExecutorService executorService = Executors.newCachedThreadPool();
		
				 Future server = executorService.submit( () -> {
				         ServerGenetic.main(new String[0]);
				 });
				 
				 
			       executorService.submit( () -> {
			       try {
			           ClientGenetic.main(true, w1);
			       } catch (Exception e) {
			           System.out.println("Opponent exception");
			           e.printStackTrace();
			       }
			   });
				 
				 
			   executorService.submit( () -> {
		       try {
		           ClientGenetic.main(false, w2);
		       } catch (Exception e) {
		           System.out.println("Opponent exception");
		           e.printStackTrace();
		       }
		       }).get(11, TimeUnit.MINUTES);
			   
			   //Aspetto 10 minuti che finisce la partita
			   
			   
			   executorService.shutdownNow();
			   
			   //Controllo i risuolati sul file scritto dal server
			   BufferedReader br = null;
			   try {
					br = new BufferedReader(new FileReader(new File(ServerGenetic.resultPath)));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				   
			   try {
				String ln = br.readLine();
				if(ln.equalsIgnoreCase("WHITE")) {
					pesiVincentiWhite.add(w1);
				}else if(ln.equalsIgnoreCase("BLACK")) {
					pesiVincentiBlack.add(w2);
				}else {
					StringTokenizer st = new StringTokenizer(ln);
					st.nextToken();
					//W - 2*B > 0 vince white altrimenti black
					int diff = Integer.parseInt(st.nextToken());
					if(diff > 0)
						pesiVincentiWhite.add(w1);
					else
						pesiVincentiBlack.add(w2);
				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
			}
			//Tengo 5 vincenti e creo altri 5
			//Inverto i ruoli
			
		}
		//Proseguo con i due giocatori successivi
		
		
		
		/***********************/
		
		//Quando terminato il primo giro faccio tutte le mutazioni e continuo,
		//
		
		

	}
	
	public static int[] generaPesi() {
		int [] res = new int[WEIGHTS];
		res[0] = getRandom(weightBase[0], 200);
		res[1] = getRandom(weightBase[1], 20);
		res[2] = getRandom(weightBase[2], 20);
		res[3] = getRandom(weightBase[3], 20);
		res[4] = getRandom(weightBase[4], 20);
		res[5] = getRandom(weightBase[5], 20);
		res[6] = getRandom(weightBase[6], 20);
		res[7] = getRandom(weightBase[7], 20);
		res[8] = getRandom(weightBase[8], 20);
		res[9] = getRandom(weightBase[9], 200);
		
		return res;
	}
	
	public static int getRandom(int base, int range) {
		Random r = new Random(System.currentTimeMillis());
		
		int segno = r.nextBoolean() ? 1 : -1;
		
		return segno * (base + r.nextInt(range)+1 * 2 - range);
		
	}

}
