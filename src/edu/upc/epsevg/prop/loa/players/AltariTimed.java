package edu.upc.epsevg.prop.loa.players;


import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class AltariTimed  implements IPlayer, IAuto
{

	    private String name;
	    private GameStatus s;

	    public AltariTimed(String name) {
	        this.name = name;
	    }

	    @Override
	    public void timeout() {
	    	//He de pensar rapid :o
	    	System.out.println("Oh! In the end I'm slower...");
	    }

	    /**
	     * Decideix el moviment del jugador donat un tauler i un color de peça que
	     * ha de posar.
	     *
	     * @param s Tauler i estat actual de joc.
	     * @return el moviment que fa el jugador.
	     */
	    @Override
	    public Move move(GameStatus s) {

	        CellType color = s.getCurrentPlayer();
	        this.s = s;
	        int qn = s.getNumberOfPiecesPerColor(color);
	        ArrayList<Point> pendingAmazons = new ArrayList<>();
	        for (int q = 0; q < qn; q++) {
	            pendingAmazons.add(s.getPiece(color, q));
	        }
	        // Iterem aleatòriament per les peces fins que trobem una que es pot moure.
	        Point queenTo = null;
	        Point queenFrom = null;
	        while (queenTo == null) {
	            Random rand = new Random();
	            int q = rand.nextInt(pendingAmazons.size());
	            queenFrom = pendingAmazons.remove(q);
	            queenTo = posicioRandomAmazon(s, queenFrom);
	        }

	        // "s" és una còpia del tauler, per es pot manipular sense perill
	        s.movePiece(queenFrom, queenTo);

	        return new Move(queenFrom, queenTo, 0, 0, SearchType.RANDOM);
	    }

	    /**
	     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
	     * de joc.
	     */
	    @Override
	    public String getName() {
	        return name;
	    }

	    private Point posicioRandomAmazon(GameStatus s, Point pos) {
	                
	        ArrayList<Point> points =  s.getMoves(pos);
	        if (points.size() == 0) {
	            return null;//no es pot moure
	        }
	        
	        Random rand = new Random();
	        int p = rand.nextInt(points.size());
	        return points.get(p);
	    }

}
