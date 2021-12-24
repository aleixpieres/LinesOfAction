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
	public int NodesExplorats;
    
    public AltariTimed(String name) {
        this.name = name;
    }

    @Override
    public void timeout() {
        // Bah! Humans do not enjoy timeouts, oh, poor beasts !
    	timeout = true;
    	System.out.println("Nodes Explorats : " + NodesExplorats);
        System.out.println("Fins aquí hem arribat...");
        return;
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
    	timeout = false;
    	this.NodesExplorats = 0;
        Move BestMove =  MinMax(s);
        return BestMove;
    }
    
    private Move MinMax(GameStatus s) 
    {	
    	 int millorMoviment = 0;
    	 int profunditat = this.depth;
    	 int profunditat_iterada = 1;
    	 int Alpha = Integer.MIN_VALUE;
    	 int Beta = Integer.MAX_VALUE;
    	 Move CaseNoTime = new Move(null, null, 0, 0, SearchType.MINIMAX_IDS);
    	 int i;
    	 
    	 Move Solucio = new Move(null,null,0,0,SearchType.MINIMAX_IDS);	    	 
    	 CellType color = s.getCurrentPlayer();
    	 
    	 while(profunditat_iterada <= profunditat) 
    	 {
    		 System.out.println("Profunditat -> " + profunditat_iterada);
    		 
    		 int qn = s.getNumberOfPiecesPerColor(color);
             ArrayList<Point> pendingAmazons = new ArrayList<>();
             for (int q = 0; q < qn; q++) {
                 pendingAmazons.add(s.getPiece(color, q));
             }
             
             for (i = 0; i< pendingAmazons.size();i++) 
             {
            	 ArrayList<Point> pendingMovements = new ArrayList<>();
            	 pendingMovements = s.getMoves(pendingAmazons.get(i));
            	 for(int j = 0; j< pendingMovements.size();j++) 
            	 {
            		 ++NodesExplorats;
            		 if(i == 0 && j == 0) CaseNoTime = new Move(pendingAmazons.get(i), pendingMovements.get(j), NodesExplorats, profunditat_iterada, SearchType.MINIMAX_IDS);
            		 GameStatus NewTauler = new GameStatus(s);
            		 Point QueenFrom = pendingAmazons.get(i);
            		 Point QueenTo = pendingMovements.get(j);
            		 CellType contrari  = CellType.opposite(color);
            		 NewTauler.movePiece(QueenFrom,QueenTo);
            		 int eval = Minimitzador(NewTauler,contrari,profunditat_iterada-1,QueenTo,Alpha,Beta);
            		 Move NouMove = new Move(QueenFrom, QueenTo, NodesExplorats, profunditat_iterada, SearchType.MINIMAX_IDS);
    	        	 if(millorMoviment < eval) //S'HAN DE COMPARAR EL VALOR DE LES HEURISTIQUES
    	        	 {
    	        		 millorMoviment = eval;
    	        		 Solucio = NouMove;
    	        	 }
    	        	 
            	 }
             }             
             if(timeout) 
             {
            	if(i  == 0) Solucio = CaseNoTime; 
            	 break; //quan s'acaba el temps parem la busqueda
             }
             ++profunditat_iterada;
    	 }
    	 System.out.println("NodesExplorats ->" + NodesExplorats);
    	 System.out.println(Solucio.getMaxDepthReached());
         return Solucio;
         
    	
    }
    
    private int Minimitzador(GameStatus s, CellType color, int profunditat, Point posicio, int Alpha, int Beta) 
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
    		++NodesExplorats;
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
	        		 int eval = Maximitzador(NewTauler,CellType.opposite(color),profunditat-1,QueenTo,Alpha,Beta);
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
    
    private int Maximitzador(GameStatus s, CellType color, int profunditat, Point posicio, int Alpha, int Beta) 
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
    		++NodesExplorats;
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
	        		 int eval = Minimitzador(NewTauler,CellType.opposite(color),profunditat-1,QueenTo,Alpha,Beta);
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
    //ESTRATEGIA VEINS
    {
    	int heuristica = 0;
    	int qn = s.getNumberOfPiecesPerColor(color);
        ArrayList<Point> pendingAmazons = new ArrayList<>();
        for (int q = 0; q < qn; q++) {
            pendingAmazons.add(s.getPiece(color, q));
        }
    	for(int i = 0; i < qn; i++)
    	{
    		Point p = pendingAmazons.get(i);
    		heuristica += veins(p,color,s);
    	}
    	return (heuristica / qn);
    }
    
    private int veins(Point p, CellType color, GameStatus s) 
    {
    	int valor = 0;
    	
    	Point aux = new Point(p.x + 1, p.y);		// (1,0)
    	if( (aux.x > 0) && (aux.y > 0) && (s.getSize() > aux.x) && (s.getSize() > aux.y)) 
    	{
    		if(s.getPos(aux) ==  color) valor+=100;	    		
    	} 	
    	
    	aux = new Point(p.x - 1, p.y);  			//(-1,0)
       	if( (aux.x > 0) && (aux.y > 0) && (s.getSize() > aux.x) && (s.getSize() > aux.y)) 
    	{
       		if(s.getPos(aux) ==  color) valor+=100;	    		   		
    	} 	
    	
    	aux = new Point(p.x, p.y +1);  				//(0,1)
       	if( (aux.x > 0) && (aux.y > 0) && (s.getSize() > aux.x) && (s.getSize() > aux.y)) 
    	{
       		if(s.getPos(aux) ==  color) valor+=100;	    		   		
    	} 	
    	
    	aux = new Point(p.x, p.y -1);  				//(0,-1)
       	if( (aux.x > 0) && (aux.y > 0) && (s.getSize() > aux.x) && (s.getSize() > aux.y)) 
    	{
       		if(s.getPos(aux) ==  color) valor+=100;	    		   		
    	} 	
    	
    	aux = new Point(p.x +1, p.y +1);  			//(1,1)
       	if( (aux.x > 0) && (aux.y > 0) && (s.getSize() > aux.x) && (s.getSize() > aux.y)) 
    	{
       		if(s.getPos(aux) ==  color) valor+=100;	    		   		
    	} 	
    	
    	aux = new Point(p.x -1, p.y +1);  			//(-1,1)
       	if( (aux.x > 0) && (aux.y > 0) && (s.getSize() > aux.x) && (s.getSize() > aux.y)) 
    	{
       		if(s.getPos(aux) ==  color) valor+=100;	    		   		
    	} 	
    	
    	aux = new Point(p.x +1, p.y -1);  			//(1,-1)
       	if( (aux.x > 0) && (aux.y > 0) && (s.getSize() > aux.x) && (s.getSize() > aux.y)) 
    	{
       		if(s.getPos(aux) ==  color) valor+=100;	    		   		
    	} 	
    	
    	aux = new Point(p.x -1, p.y -1);  			//(-1,-1)
       	if( (aux.x > 0) && (aux.y > 0) && (s.getSize() > aux.x) && (s.getSize() > aux.y)) 
    	{
       		if(s.getPos(aux) ==  color) valor+=100;	    		   		
    	} 	
    	

    	return valor;
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
