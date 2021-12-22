package edu.upc.epsevg.prop.loa.players;


import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
import java.awt.Point;
import java.util.ArrayList;

public class AltariTimed  implements IPlayer, IAuto
{
	private boolean timeout = false;
	private String name;
    private int depth = 4;
	public int NodesExplorats = 0;
    
    public AltariTimed(String name) {
        this.name = name;
    }

    @Override
    public void timeout() {
        // Bah! Humans do not enjoy timeouts, oh, poor beasts !
        System.out.println("To bad! I'm pretty slow...");
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

        Move BestMove =  MinMax(s);
        return BestMove;
    }
    
    private Move MinMax(GameStatus s) 
    {	
    	 timeout = false;
    	 int millorMoviment = 0;
    	 int profunditat = this.depth;
    	 int profunditat_iterada = 0;
    	 int Alpha = Integer.MIN_VALUE;
    	 int Beta = Integer.MAX_VALUE;
    	 
    	 Move Solucio = new Move(null,null,0,0,SearchType.MINIMAX_IDS);	    	 
    	 CellType color = s.getCurrentPlayer();
    	 while(profunditat_iterada <= profunditat) 
    	 {
    		 ++profunditat_iterada;
    		 int qn = s.getNumberOfPiecesPerColor(color);
             ArrayList<Point> pendingAmazons = new ArrayList<>();
             for (int q = 0; q < qn; q++) {
                 pendingAmazons.add(s.getPiece(color, q));
             }
             
             for (int i = 0; i< pendingAmazons.size();i++) 
             {
            	 ArrayList<Point> pendingMovements = new ArrayList<>();
            	 pendingMovements = s.getMoves(pendingAmazons.get(i));
            	 for(int j = 0; j< pendingMovements.size();j++) 
            	 {
            		 if(i == 0 && j == 0) Solucio = new Move(pendingAmazons.get(i), pendingMovements.get(j), NodesExplorats, profunditat_iterada, SearchType.MINIMAX);
            		 GameStatus NewTauler = new GameStatus(s);
            		 Point QueenFrom = pendingAmazons.get(i);
            		 Point QueenTo = pendingMovements.get(j);
            		 CellType contrari  = CellType.opposite(color);
            		 NewTauler.movePiece(QueenFrom,QueenTo);
            		 int eval = Minimitzador(NewTauler,contrari,profunditat_iterada-1,QueenTo,++NodesExplorats,Alpha,Beta);
            		 Move NouMove = new Move(QueenFrom, QueenTo, NodesExplorats, profunditat_iterada, SearchType.MINIMAX_IDS);
    	        	 if(millorMoviment < eval) //S'HAN DE COMPARAR EL VALOR DE LES HEURISTIQUES
    	        	 {
    	        		 millorMoviment = eval;
    	        		 Solucio = NouMove;
    	        	 }
    	        	 if(timeout) 
    	        	 { 
    	        		 System.out.println(profunditat_iterada);
    	        		 return Solucio;
    	        	 }
            	 }
             }
    	 }
         return Solucio;
         
    	
    }
    
    private int Minimitzador(GameStatus s, CellType color, int profunditat, Point posicio, int NodesExplorats, int Alpha, int Beta) 
    {
    	
    	int valor = Integer.MAX_VALUE;
		ArrayList<Point> pendingAmazons = new ArrayList<>();
    	ArrayList<Point> pendingMovements = new ArrayList<>();
    	pendingMovements = s.getMoves(posicio);
    	if(s.isGameOver()) 
    	{
    		++NodesExplorats;
    		return Integer.MAX_VALUE;
    	} else if(profunditat == 0) 
    	{
    		return Heuristica(s, color);
    		
    	}else {
    		 int qn = s.getNumberOfPiecesPerColor(color);
	         for (int q = 0; q < qn; q++) {
	             pendingAmazons.add(s.getPiece(color, q));
	         }
	         for (int i = 0; i< pendingAmazons.size();i++) 
	         {
	        	 pendingMovements = s.getMoves(pendingAmazons.get(i));
	        	 for(int j = 0; j< pendingMovements.size();j++) 
	        	 {
	        		 Point QueenFrom = pendingAmazons.get(i);
	        		 Point QueenTo = pendingMovements.get(j);
	        		 GameStatus NewTauler = new GameStatus(s);
	        		 NewTauler.movePiece(QueenFrom,QueenTo);
	        		 int eval = Maximitzador(NewTauler,CellType.opposite(color),profunditat-1,QueenTo,++NodesExplorats,Alpha,Beta);
	        		 valor =  Math.min(valor,eval);
	        		 
	        		 if(valor <= Alpha) 
	                    {
	                        return valor;
	                    }
	        		 Beta = Math.min(valor, Beta);
	        	 }
	         }
    		return valor;
    	}
    }
    
    private int Maximitzador(GameStatus s, CellType color, int profunditat, Point posicio, int NodesExplorats, int Alpha, int Beta) 
    {
    	int valor = Integer.MIN_VALUE;
		 	ArrayList<Point> pendingAmazons = new ArrayList<>();
    	ArrayList<Point> pendingMovements = new ArrayList<>();
    	pendingMovements = s.getMoves(posicio);
    	if(s.isGameOver()) 
    	{
    		++NodesExplorats;
    		return Integer.MIN_VALUE;
    	} else if(profunditat == 0) 
    	{
    		return Heuristica(s, color);
    		
    	} else {
    		 int qn = s.getNumberOfPiecesPerColor(color);
	         for (int q = 0; q < qn; q++) {
	             pendingAmazons.add(s.getPiece(color, q));
	         }
	         for (int i = 0; i< pendingAmazons.size();i++) 
	         {
	        	 pendingMovements = s.getMoves(pendingAmazons.get(i));
	        	 for(int j = 0; j< pendingMovements.size();j++) 
	        	 {
	        		 Point QueenFrom = pendingAmazons.get(i);
	        		 Point QueenTo = pendingMovements.get(j);
	        		 GameStatus NewTauler = new GameStatus(s);
	        		 NewTauler.movePiece(QueenFrom,QueenTo);
	        		 int eval = Minimitzador(NewTauler,CellType.opposite(color),profunditat-1,QueenTo,++NodesExplorats,Alpha,Beta);
	        		 valor =  Math.max(valor,eval);
	                 
	        		 if(Beta <= valor) //es compleix la condicio de la poda
	                 {
	        			 return valor;
	                 }
	        		 Alpha = Math.max(valor, Alpha); 
	        	 }
	         }
    		return valor;
    	}
    }
    
    
    private int Heuristica(GameStatus s, CellType color) 
    {
    	return 1;
    }
    
    
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return ("Timed " + name);
    }

   

}
