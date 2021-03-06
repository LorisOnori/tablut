package it.unibo.ai.didattica.competition.tablut.AIFM;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 *
 * @author E.Cerulo, V.M.Stanzione
 *
 */

public final class Minmax implements Callable<Action> {

    /***executor***/
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /***logic***/
    protected int currDepthLimit;
    private final State.Turn player;
    private final Utils u;
    private final List<String> citadels;
    private final Heuristic heuristic;

    /***prints***/
    private static final DecimalFormat df2 = new DecimalFormat("#.##");
    private static final Random rand = new Random();
    private final boolean canPrint = true;

    // Come parametri per la call
    private State currentState;

    //risultati
    private static Action result;
    private static List<Action> possibleActions;


    /***costruttore***/
    public Minmax(int currDepthLimit, State.Turn player) {
        this.currDepthLimit = currDepthLimit;
        this.player = player;
        this.u = new Utils(true);
        this.heuristic = new HeuristicFrittoMisto(player, currDepthLimit);

        this.citadels = new ArrayList<>();
        possibleActions = new ArrayList<>();

        this.citadels.add("a4");
        this.citadels.add("a5");
        this.citadels.add("a6");
        this.citadels.add("b5");
        this.citadels.add("d1");
        this.citadels.add("e1");
        this.citadels.add("f1");
        this.citadels.add("e2");
        this.citadels.add("i4");
        this.citadels.add("i5");
        this.citadels.add("i6");
        this.citadels.add("h5");
        this.citadels.add("d9");
        this.citadels.add("e9");
        this.citadels.add("f9");
        this.citadels.add("e8");
    }



    /***MAKE DECISION***/
    public Action makeDecision(int timeOut, State stato) throws IOException {

        currentState = stato;

        //metto in esecuzione il task (viene chiamata "call()")
        Future<Action> risultato = executorService.submit(this);

        result = null;
        possibleActions.clear();

        try {
            result = risultato.get(timeOut, TimeUnit.SECONDS);
            System.out.println("Selected: {" + result.toString() + "}");
        } catch (TimeoutException e) {

            boolean cancellato = risultato.cancel(true);
            System.out.println("####### time_out scattato #######");

            if(!possibleActions.isEmpty())
                result = possibleActions.get(rand.nextInt(possibleActions.size()));

            if(canPrint) System.out.println("----------------------------------------------------------------------------------\n");
            System.out.println("Selected: {" + result.toString() + "}");

            return result;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public Action call() throws Exception {

        //valore del risultato
        double resultValue = Double.NEGATIVE_INFINITY;

        //azioni iniziali
        List<Action> azioni = u.getSuccessors(currentState);
        Collections.shuffle(azioni);

        //INIZIALIZZO RESULT CON UNA MOSSA A CASO
        result = azioni.get(0);
        possibleActions.add(azioni.get(0));


        if(canPrint) System.out.println("----------------------------------------------------------------------------------");
        for (Action action : azioni) {

            double value = minValue(this.checkMove(currentState.clone(), action), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);

            // importante che sia prima dell' if (value > resultValue)
            if(Thread.interrupted()){
                System.out.println(Thread.currentThread() + "___ : Mi ?? stato chiesto di fermarmi----call()");
                gestisciTerminazione();
                System.out.println(Thread.currentThread() + "___ : Mi sono fermato--in--call()");
                return possibleActions.get(rand.nextInt(possibleActions.size()));
            }

                //salvo result
                if (value > resultValue) {
                    result = action;
                    possibleActions.clear();
                    possibleActions.add(action);
                    resultValue = value;
                }

                //per selezionare un valore random fra quelli di uguale value
                else if(value == resultValue){
                    possibleActions.add(action);
                    resultValue = value;
                }


            if(canPrint) System.out.println("A={" + action.toString() + "}; V=" + df2.format(value) + ".    CURRENT BESTS: " + possibleActions.toString() + "");
        }//for
        if(canPrint) System.out.println("----------------------------------------------------------------------------------\n");


        //torno un risultato random tra i migliori
        return possibleActions.get(rand.nextInt(possibleActions.size()));
    }

    /****cancel*****/
    //Questo ?? il codice che esegue la .cancel() (in FutureTask.java)

//            public boolean cancel(boolean mayInterruptIfRunning) {
//                if (!(state == NEW &&
//                        UNSAFE.compareAndSwapInt(this, stateOffset, NEW,
//                                mayInterruptIfRunning ? INTERRUPTING : CANCELLED)))
//                    return false;
//                try {    // in case call to interrupt throws exception
//                    if (mayInterruptIfRunning) {
//                        try {
//                            Thread t = runner;
//                            if (t != null)
//                                t.interrupt();
//                        } finally { // final state
//                            UNSAFE.putOrderedInt(this, stateOffset, INTERRUPTED);
//                        }
//                    }
//                } finally {
//                    finishCompletion();
//                }
//                return true;
//            }

    /***max***/
    public double maxValue(State state, double alpha, double beta, int depth) throws Exception{

        if(Thread.interrupted()){
            System.out.println(Thread.currentThread() + "___ : Mi ?? stato chiesto di fermarmi----maxValue()");
            gestisciTerminazione();
            System.out.println(Thread.currentThread() + "___ : Mi sono fermato----maxValue()");

            return 0;
        }

        if (state.getTurn() == State.Turn.BLACKWIN || state.getTurn() == State.Turn.WHITEWIN || depth >= currDepthLimit)
            return evaluate(state, player, depth);

        double value = Double.NEGATIVE_INFINITY;

        for (Action action : u.getSuccessors(state)) {
            value = Math.max(value, minValue(this.checkMove(state.clone(), action), alpha, beta, depth + 1));
            if (value >= beta)
                return value;
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    private void gestisciTerminazione() {
        Thread.currentThread().stop();
    }

    /***min***/
    public double minValue(State state, double alpha, double beta, int depth) throws Exception{

        if(Thread.interrupted()){
            System.out.println(Thread.currentThread() + "___ : Mi ?? stato chiesto di fermarmi----minValue()");
            gestisciTerminazione();
            System.out.println(Thread.currentThread() + "___ : Mi sono fermato----minValue()");
            return 0;
        }

        if (state.getTurn() == State.Turn.BLACKWIN || state.getTurn() == State.Turn.WHITEWIN || depth >= currDepthLimit)
            return evaluate(state, player, depth);

        double value = Double.POSITIVE_INFINITY;

        for (Action action : u.getSuccessors(state)) {
            value = Math.min(value, maxValue(this.checkMove(state.clone(), action), alpha, beta, depth + 1));
            if (value <= alpha)
                return value;
            beta = Math.min(beta, value);
        }
        return value;
    }



    private double evaluate(State state, State.Turn player, int depth){
        return heuristic.eval(state, depth);
    }


    /**************************************CHECK******************************************/

    private State movePawn(State state, Action a) {
        State.Pawn pawn = state.getPawn(a.getRowFrom(), a.getColumnFrom());
        State.Pawn[][] newBoard = state.getBoard();
        // State newState = new State();
        // libero il trono o una casella qualunque
        if (a.getColumnFrom() == 4 && a.getRowFrom() == 4) {
            newBoard[a.getRowFrom()][a.getColumnFrom()] = State.Pawn.THRONE;
        } else {
            newBoard[a.getRowFrom()][a.getColumnFrom()] = State.Pawn.EMPTY;
        }

        // metto nel nuovo tabellone la pedina mossa
        newBoard[a.getRowTo()][a.getColumnTo()] = pawn;
        // aggiorno il tabellone
        state.setBoard(newBoard);
        // cambio il turno
        if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
            state.setTurn(State.Turn.BLACK);
        } else {
            state.setTurn(State.Turn.WHITE);
        }

        return state;
    }

    private State checkMove(State state, Action a){

        //TODO ALTRO STATO State s?
        state = this.movePawn(state, a);

        if (state.getTurn().equalsTurn("W")) {
            state = this.checkCaptureBlack(state, a);
        } else if (state.getTurn().equalsTurn("B")) {
            state = this.checkCaptureWhite(state, a);
        }

        //TODO CONTROLLO PAREGGIO MANCANTE
        return state;
    }

    private State checkCaptureBlack(State state, Action a) {

        this.checkCaptureBlackPawnRight(state, a);
        this.checkCaptureBlackPawnLeft(state, a);
        this.checkCaptureBlackPawnUp(state, a);
        this.checkCaptureBlackPawnDown(state, a);
        this.checkCaptureBlackKingRight(state, a);
        this.checkCaptureBlackKingLeft(state, a);
        this.checkCaptureBlackKingDown(state, a);
        this.checkCaptureBlackKingUp(state, a);
//        this.movesWithutCapturing++;
        return state;
    }

    private State checkCaptureWhite(State state, Action a) {
        // controllo se mangio a destra
        if (a.getColumnTo() < state.getBoard().length - 2
                && state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("B")
                && (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W")
                || state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")
                || state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("K")
                || (this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))
                && !(a.getColumnTo() + 2 == 8 && a.getRowTo() == 4)
                && !(a.getColumnTo() + 2 == 4 && a.getRowTo() == 0)
                && !(a.getColumnTo() + 2 == 4 && a.getRowTo() == 8)
                && !(a.getColumnTo() + 2 == 0 && a.getRowTo() == 4)))) {
            state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
//            this.movesWithutCapturing = -1;
//            this.loggGame.fine("Pedina nera rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
        }
        // controllo se mangio a sinistra
        if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("B")
                && (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W")
                || state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
                || state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("K")
                || (this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
                && !(a.getColumnTo() - 2 == 8 && a.getRowTo() == 4)
                && !(a.getColumnTo() - 2 == 4 && a.getRowTo() == 0)
                && !(a.getColumnTo() - 2 == 4 && a.getRowTo() == 8)
                && !(a.getColumnTo() - 2 == 0 && a.getRowTo() == 4)))) {
            state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
//            this.movesWithutCapturing = -1;
//            this.loggGame.fine("Pedina nera rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() - 1));
        }
        // controllo se mangio sopra
        if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("B")
                && (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W")
                || state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
                || state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("K")
                || (this.citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
                && !(a.getColumnTo() == 8 && a.getRowTo() - 2 == 4)
                && !(a.getColumnTo() == 4 && a.getRowTo() - 2 == 0)
                && !(a.getColumnTo() == 4 && a.getRowTo() - 2 == 8)
                && !(a.getColumnTo() == 0 && a.getRowTo() - 2 == 4)))) {
            state.removePawn(a.getRowTo() - 1, a.getColumnTo());
//            this.movesWithutCapturing = -1;
//            this.loggGame.fine("Pedina nera rimossa in: " + state.getBox(a.getRowTo() - 1, a.getColumnTo()));
        }
        // controllo se mangio sotto
        if (a.getRowTo() < state.getBoard().length - 2
                && state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("B")
                && (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("W")
                || state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
                || state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("K")
                || (this.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
                && !(a.getColumnTo() == 8 && a.getRowTo() + 2 == 4)
                && !(a.getColumnTo() == 4 && a.getRowTo() + 2 == 0)
                && !(a.getColumnTo() == 4 && a.getRowTo() + 2 == 8)
                && !(a.getColumnTo() == 0 && a.getRowTo() + 2 == 4)))) {
            state.removePawn(a.getRowTo() + 1, a.getColumnTo());
//            this.movesWithutCapturing = -1;
//            this.loggGame.fine("Pedina nera rimossa in: " + state.getBox(a.getRowTo() + 1, a.getColumnTo()));
        }
        // controllo se ho vinto
        if (a.getRowTo() == 0 || a.getRowTo() == state.getBoard().length - 1 || a.getColumnTo() == 0
                || a.getColumnTo() == state.getBoard().length - 1) {
            if (state.getPawn(a.getRowTo(), a.getColumnTo()).equalsPawn("K")) {
                state.setTurn(State.Turn.WHITEWIN);
            }
        }
        // TODO: implement the winning condition of the capture of the last
        // black checker

//        this.movesWithutCapturing++;
        return state;
    }

    private State checkCaptureBlackKingLeft(State state, Action a) {
        // ho il re sulla sinistra
        if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("K")) {
            // re sul trono
            if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e5")) {
                if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 3).equalsPawn("B")
                        && state.getPawn(5, 4).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            // re adiacente al trono
            if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e4")) {
                if (state.getPawn(2, 4).equalsPawn("B") && state.getPawn(3, 3).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("f5")) {
                if (state.getPawn(5, 5).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e6")) {
                if (state.getPawn(6, 4).equalsPawn("B") && state.getPawn(5, 3).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            // sono fuori dalle zone del trono
            if (!state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e5")
                    && !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e6")
                    && !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e4")
                    && !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("f5")) {
                if (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
                        || this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
        }
        return state;
    }

    private State checkCaptureBlackKingRight(State state, Action a) {
        // ho il re sulla destra
        if (a.getColumnTo() < state.getBoard().length - 2
                && (state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("K"))) {
            // re sul trono
            if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e5")) {
                if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
                        && state.getPawn(5, 4).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            // re adiacente al trono
            if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e4")) {
                if (state.getPawn(2, 4).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e6")) {
                if (state.getPawn(5, 5).equalsPawn("B") && state.getPawn(6, 4).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("d5")) {
                if (state.getPawn(3, 3).equalsPawn("B") && state.getPawn(5, 3).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            // sono fuori dalle zone del trono
            if (!state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("d5")
                    && !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e6")
                    && !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e4")
                    && !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e5")) {
                if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")
                        || this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
        }
        return state;
    }

    private State checkCaptureBlackKingDown(State state, Action a) {
        // ho il re sotto
        if (a.getRowTo() < state.getBoard().length - 2
                && state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("K")) {
            //System.out.println("Ho il re sotto");
            // re sul trono
            if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e5")) {
                if (state.getPawn(5, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
                        && state.getPawn(4, 3).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            // re adiacente al trono
            if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e4")) {
                if (state.getPawn(3, 3).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("d5")) {
                if (state.getPawn(4, 2).equalsPawn("B") && state.getPawn(5, 3).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("f5")) {
                if (state.getPawn(4, 6).equalsPawn("B") && state.getPawn(5, 5).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            // sono fuori dalle zone del trono
            if (!state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("d5")
                    && !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e4")
                    && !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("f5")
                    && !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e5")) {
                if (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
                        || this.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
        }
        return state;
    }

    private State checkCaptureBlackKingUp(State state, Action a) {
        // ho il re sopra
        if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("K")) {
            // re sul trono
            if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e5")) {
                if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
                        && state.getPawn(4, 3).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            // re adiacente al trono
            if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e6")) {
                if (state.getPawn(5, 3).equalsPawn("B") && state.getPawn(5, 5).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("d5")) {
                if (state.getPawn(4, 2).equalsPawn("B") && state.getPawn(3, 3).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("f5")) {
                if (state.getPawn(4, 6).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
            // sono fuori dalle zone del trono
            if (!state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("d5")
                    && !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e4")
                    && !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("f5")
                    && !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e5")) {
                if (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
                        || this.citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))) {
                    state.setTurn(State.Turn.BLACKWIN);
                }
            }
        }
        return state;
    }

    private State checkCaptureBlackPawnRight(State state, Action a) {
        // mangio a destra
        if (a.getColumnTo() < state.getBoard().length - 2
                && state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W")) {
            if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")) {
                state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
//                this.movesWithutCapturing = -1;
//                this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
            }
            if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")) {
                state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
//                this.movesWithutCapturing = -1;
//                this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
            }
            if (this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))) {
                state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
//                this.movesWithutCapturing = -1;
//                this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
            }
            if (state.getBox(a.getRowTo(), a.getColumnTo() + 2).equals("e5")) {
                state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
//                this.movesWithutCapturing = -1;
//                this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() + 1));
            }

        }

        return state;
    }

    private State checkCaptureBlackPawnLeft(State state, Action a) {
        // mangio a sinistra
        if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
                && (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
                || state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
                || this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
                || (state.getBox(a.getRowTo(), a.getColumnTo() - 2).equals("e5")))) {
            state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
//            this.movesWithutCapturing = -1;
//            this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo(), a.getColumnTo() - 1));
        }
        return state;
    }

    private State checkCaptureBlackPawnUp(State state, Action a) {
        // controllo se mangio sopra
        if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
                && (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
                || state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
                || this.citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
                || (state.getBox(a.getRowTo() - 2, a.getColumnTo()).equals("e5")))) {
            state.removePawn(a.getRowTo() - 1, a.getColumnTo());
//            this.movesWithutCapturing = -1;
//            this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo() - 1, a.getColumnTo()));
        }
        return state;
    }

    private State checkCaptureBlackPawnDown(State state, Action a) {
        // controllo se mangio sotto
        if (a.getRowTo() < state.getBoard().length - 2
                && state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
                && (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
                || state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
                || this.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
                || (state.getBox(a.getRowTo() + 2, a.getColumnTo()).equals("e5")))) {
            state.removePawn(a.getRowTo() + 1, a.getColumnTo());
//            this.movesWithutCapturing = -1;
//            this.loggGame.fine("Pedina bianca rimossa in: " + state.getBox(a.getRowTo() + 1, a.getColumnTo()));
        }
        return state;
    }


}