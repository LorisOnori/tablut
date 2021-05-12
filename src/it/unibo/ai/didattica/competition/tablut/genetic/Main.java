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

import it.unibo.ai.didattica.competition.tablut.AI.Client;
import it.unibo.ai.didattica.competition.tablut.AI.ClientBlack;
import it.unibo.ai.didattica.competition.tablut.AI.ClientGenetic;
import it.unibo.ai.didattica.competition.tablut.AI.ClientWhite;
import it.unibo.ai.didattica.competition.tablut.AI.Player;
import it.unibo.ai.didattica.competition.tablut.server.ServerGenetic;

public class Main {

	public final static int population = 10; //Deve essere pari
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
		
		List<int[]> pesiVincenti = new ArrayList<>();
		List<int[]> newPop = new ArrayList<>();
		
		StringBuilder sb = new StringBuilder();

		
		/*****
		 * 
		 * Devo selvare i pesi da passare ai clienti
		 * Quando finisco il primo ciclo creo 5 nuovi giocatori e lascio i 5 vincenti
		 * Inverto le parti di gioco --> Se era un vincente col bianco, ora gioca col nero e così via
		 * 
		 */
		
		
		for(int g = 0; g < generation; g++) {
			System.out.println("GENERAZIONE "+g);
			if(g == 0) {
				for(int i = 0; i<population; i++) {
					newPop.add(generaPesi());
				}
			}else {
				//MUTAZIONI VARIE
				Collections.shuffle(pesiVincenti);
				
				
				for(int i = 0 ; i< pesiVincenti.size() && pesiVincenti.size() > 1; i++) {
					int [] v1 = pesiVincenti.get(i);
					int [] v2 = null;
					int [] v3 = new  int[WEIGHTS];
					try {
						v2 = pesiVincenti.get(i+1);
					}catch(IndexOutOfBoundsException e) {
						newPop.add(v1);
						break;
					}
					
					for(int w = 0 ;  w < WEIGHTS; w++) {
						if(w <= 4)
							v3[w] = v1[w];
						else
							v3[w] = v2[w];
						
						Random r = new Random(System.currentTimeMillis());
						if(r.nextInt(100) +1 <= 3)
							if(w == 0 || w == 8)
								v3[w] = getRandom(v3[w],200);
							else
								v3[w] = getRandom(v3[w],20);	
						
					}
					newPop.add(v3);
				}
				
				//Fine mutazione
				for(int i = newPop.size() ; i< population; i ++) {
					newPop.add(generaPesi());
				}
				Collections.shuffle(newPop);
				pesiVincenti = new ArrayList<>();
				
			}
			
			for(int p = 0; p<population/2; p++) {
				
				//Match andata-ritorno
				int [] w1 = newPop.get(p);
				int [] w2 = newPop.get(p+1);
				
				boolean vintoAndata = false;
				for(int m = 0 ; m < 2 ; m++) {
					
					
					
					 ExecutorService executorService = Executors.newCachedThreadPool();
			
					 Future server = executorService.submit( () -> {
					         ServerGenetic.main(new String[0]);
					 });
					 
					 if(m == 0) {
						 executorService.submit( () -> {
						       try {
						    		   ClientGenetic.main(true, w1);    		   
						       } catch (Exception e) {
						           System.out.println("white exception");
						           e.printStackTrace();
						       }
					       });
					 }else {
						 executorService.submit( () -> {
						       try {
						   		   ClientGenetic.main(true, w2);	   
						       } catch (Exception e) {
						           System.out.println("white exception");
						           e.printStackTrace();
						       }
					       });
					 }
				      
					 
					if( m == 0) {
						 executorService.submit( () -> {
						       try {
						    		ClientGenetic.main(false, w2);
						       } catch (Exception e) {
						           System.out.println("black exception");
						           e.printStackTrace();
						       }
						       }).get(11, TimeUnit.MINUTES);
					}else {
						 executorService.submit( () -> {
						       try {
						    	ClientGenetic.main(false, w1);
						       } catch (Exception e) {
						           System.out.println("black exception");
						           e.printStackTrace();
						       }
						       }).get(11, TimeUnit.MINUTES);
					}

				   
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
						if(m == 0)
							vintoAndata = true;
						else { //secondo giro (ritorno)
							if(!vintoAndata) {//prima ha vinto sempre lui con il nero
								pesiVincenti.add(w2);
							}
						}
							
					}else if(ln.equalsIgnoreCase("BLACK")) {
						if(m == 0)
							vintoAndata = false;
						else { //secondo giro (ritorno)
							if(vintoAndata) {//prima ha vinto sempre lui con il bianco
								pesiVincenti.add(w1);
							}
						}
					}else {
						StringTokenizer st = new StringTokenizer(ln);
						st.nextToken();
						//W - 2*B > 0 vince white altrimenti black
						int diff = Integer.parseInt(st.nextToken());
						if(diff > 0) { //Bianco vince
							if(m == 0)
								vintoAndata = true;
							else { //secondo giro (ritorno)
								if(!vintoAndata) {//prima ha vinto sempre lui con il nero
									pesiVincenti.add(w2);
								}
							}
						}else {//Nero vince
							if(m == 0)
								vintoAndata = false;
							else { //secondo giro (ritorno)
								if(vintoAndata) {//prima ha vinto sempre lui con il nero
									pesiVincenti.add(w1);
								}
							}
						}
					}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				   try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  
					 
				}
			
			}
			//Dentro pesi vincenti ho i pesi che hanno vinto andata e ritorno
			sb.append("GENERAZIONE "+g+ "\n");
			for(int i = 0; i< pesiVincenti.size(); i++) {
				sb.append(i+"- ");
				for(int j = 0 ; j<WEIGHTS; j++) {
					sb.append(j+ " ");
				}
				sb.append("\n");
			}
		}

		PrintWriter pr = null;
		try {
			pr = new PrintWriter(new File(pesi));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pr.write(sb.toString());
		pr.close();
		
		
		
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
