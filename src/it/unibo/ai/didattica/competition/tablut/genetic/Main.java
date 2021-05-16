package it.unibo.ai.didattica.competition.tablut.genetic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import it.unibo.ai.didattica.competition.tablut.AI.Client;
import it.unibo.ai.didattica.competition.tablut.AI.ClientBlack;
import it.unibo.ai.didattica.competition.tablut.AI.ClientGenetic;
import it.unibo.ai.didattica.competition.tablut.AI.ClientWhite;
import it.unibo.ai.didattica.competition.tablut.AI.Player;
import it.unibo.ai.didattica.competition.tablut.server.ServerGenetic;

public class Main {

	public final static int population = 2; //Deve essere pari
	public final static int generation = 2;
	private final static String pesi = "weight.txt";
	
	private static final double DIFF  = 16/8;
	
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
	
	private static final int SQUADRE = 8;
	
	private static int [] weightBase = {300, 20,3,10,8,-4,4,-2,-50};
	

	
	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException, IOException {
//		
//		//jar location Server - Client
//		if(args.length != 2) {
//			System.out.println("Exit");
//			System.exit(1);
//		}else {
//			System.out.println(args[0] + " \n"+ args[1]);
//		}
//		
//		
//		
//		//TORNEO
//		List<int[]> pesiSquadre = new ArrayList<>();
//		List<int []> toPlay = new ArrayList<>();
//		List<int []> winners = new ArrayList<>();
//		
//		pesiSquadre.add(weightBase);
//		
//		for(int i = 0 ; i< SQUADRE-1; i++) {
//			pesiSquadre.add(generaPesi());
//			
//		}
//		
//		for(int []i : pesiSquadre)
//			toPlay.add(i);
//		
//		ProcessBuilder pb = new ProcessBuilder();
//		int turno = SQUADRE/2;
//		BufferedReader br;
//		
//		while(turno >= 1) {
//			
//			Collections.shuffle(toPlay);
//			for(int i  =0 ; i< turno; i++) {
//				int j = i*2;
//				System.out.println("Turno " + turno + " round "+ i+ "\n"+getPesiStringa(toPlay.get(j))+ "\n		VS\n"+getPesiStringa(toPlay.get(j+1))	);
//
//				Process s = Runtime.getRuntime().exec("java -jar "+args[0]);
//				//pb.command("java", "-jar ", args[0]);
//				//Process s = pb.start();
//				TimeUnit.MILLISECONDS.sleep(2000);
//				Runtime.getRuntime().exec("java -jar "+args[1] + " WHITE "+getPesiStringa(toPlay.get(j)));
//				//pb.command("java", "-jar ", args[1] , "WHITE", getPesiStringa(toPlay.get(i)));
//				//pb.start();
//				TimeUnit.MILLISECONDS.sleep(2000);
//				Runtime.getRuntime().exec("java -jar "+args[1] + " BLACK "+getPesiStringa(toPlay.get(j+1)));
//				//pb.command("java", "-jar", args[1] , "BLACK", getPesiStringa(toPlay.get(i+1)));
//				//pb.start();
//				
//				
//				int val = s.waitFor();
//				System.out.println("VAL : "+val);
//				//Leggo il risultato
//				//br = new BufferedReader(new FileReader(ServerGenetic.resultPath));
//				//String res = br.readLine();
//				
//				if(val == ServerGenetic.WHITE_WON) {
//					winners.add(toPlay.get(j));
//					System.out.println("Turno " + turno + " round "+ i+ " ha vinto WHTIE con pesi "+getPesiStringa(toPlay.get(j)));
//				}else if(val == ServerGenetic.BLACK_WON){
//					winners.add(toPlay.get(j+1));
//					System.out.println("Turno " + turno + " round "+ i+ " ha vinto BLACK con pesi "+getPesiStringa(toPlay.get(j+1)));
//
//				}else {
//					System.out.println("ERRORE");
//					System.exit(-1);
//				}
//				
//				
//				
//				
//			
//			}
//			
//			
//			turno = turno /2;
//			toPlay = new ArrayList<>();
//			for(int []i : winners)
//				toPlay.add(i);
//				
//			
//			
//		}
		
		
		for(int i = 0; i< 7; i++) {
			System.out.println(getPesiStringa(generaPesi()));
		}
		
		
		
		
		
		
		

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
		res[8] = getRandom(weightBase[8], 200);
		
		return res;
	}
	
	public static int getRandom(int base, int range) {
		Random r = new Random(System.nanoTime());
		
		int segno = r.nextBoolean() ? 1 : -1;
		
		return base + (segno * (r.nextInt(range)+1));
		
	}
	
	public static String getPesiStringa(int []p) {
		String s = "";
		for(int i : p)
			s += i + " ";
		return s;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
