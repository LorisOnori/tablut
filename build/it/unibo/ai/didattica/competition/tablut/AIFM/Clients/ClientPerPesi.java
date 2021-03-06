package it.unibo.ai.didattica.competition.tablut.AIFM.Clients;

import it.unibo.ai.didattica.competition.tablut.AIFM.HeuristicFrittoMisto;

import java.io.IOException;

public class ClientPerPesi {

//  non dovrebbe servire
//    public static void main(String[] args) throws ClassNotFoundException, IOException {
//
//
//    }

    public ClientPerPesi(String player, // "BLACK" or "WHITE"
                         double manhattanKing,
                         double kingSidesThreatened,
                         double pawnsDifference,
                         double whitePaws,
                         double victoryPath,
                         double victory,
                         double blackPaws
                         ) throws IOException, ClassNotFoundException {

        double[] weights = new double[7];

        weights[HeuristicFrittoMisto.KING_MANHATTAN] = manhattanKing;
        weights[HeuristicFrittoMisto.KING_CAPTURED_SIDES] = kingSidesThreatened;
        weights[HeuristicFrittoMisto.PAWNS_DIFFERENCE] = pawnsDifference;
        weights[HeuristicFrittoMisto.PAWNS_WHITE] = whitePaws;
        weights[HeuristicFrittoMisto.VICTORY_PATH] = victoryPath;
        weights[HeuristicFrittoMisto.VICTORY] = victory;
        weights[HeuristicFrittoMisto.PAWNS_BLACK] = blackPaws;
        HeuristicFrittoMisto.setWeight(weights);

//        String[] array = new String[]{player};

        String[] array = new String[]{"BLACK"};


        TablutFrittoMistoClient.main(array);

    }

}
