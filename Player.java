import java.sql.Array;
import java.util.*;


public class Player {
	/**
     * Performs a move
     *
     * @param pState
     *            the current state of the board
     * @param pDue
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
	
	
	//Evaluation should be applied wen unlikley to exhibit swings in value in near future
	// Apply iterative deeping, helps with move ordering
	//Transposition tables cache information about  previous searches. The best move that was found.
	// Priori how to search on the best moves. Evaluating all moves that result in a king before
	// moves that don't 
	
	// Best moves need to be searched first. 
	
    public GameState bestGameState;
    public int bestScore = Integer.MAX_VALUE;
	public long startTime;
	public long MaxAllowedTime = 50;
	
	public HashMap<Integer, ArrayList<GameState>> unsortedMoves; 
    
    
    public GameState play(final GameState pState, final Deadline pDue) {

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */
        
        //Random random = new Random();
        //return lNextStates.elementAt(random.nextInt(lNextStates.size()));
               
        return iterativeDeepening(pState);
        
        
    }   
    
    public GameState iterativeDeepening(GameState pState) {
    	
    	Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);
        
        int score;
        GameState bestMove = null;
        
        this.startTime = System.currentTimeMillis();
        
        for (int depth= 0; depth < Integer.MAX_VALUE; depth++) {
        	
            if (System.currentTimeMillis() - this.startTime > this.MaxAllowedTime){
            	
        		break; 
        		
        	}
            
            int v = Integer.MIN_VALUE;
            
            
            
	        for (GameState child : lNextStates) {
	        	
	        	score = alphaBeta(child, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
	        	
	        	if (score > v) {
	        		v = score;
	        		bestMove = child;
	        		
	        	}
	        	
	        }
        }
                
    	return bestMove;
    }
    
    public int alphaBeta(GameState pState, int alpha, int beta, int depth) {
    	
    	// State: the current state we are analysing
    	// alpha: the current best value achievable by A
    	// beta : the  current best value achievable by B
    	// player : the current player
    	
        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);
    	
        //Check if terminal state
        if (pState.isEOG()) {
	
        	 if (pState.isRedWin()) {
             	return this.bestScore;
             }
        	 else {
        		 return -this.bestScore;
        	 }
        	
        }
       

        else if (depth == 0) {
    		
    		return gamma(pState);
    		
    	}
    	
	    	else {
	    		if (pState.getNextPlayer() == Constants.CELL_RED) {
	    			
	    			return max_value(pState, alpha, beta, depth);
	    			
	    		}
	    		
	    		else {
	  
	    			return min_value(pState, alpha, beta, depth);
	    			
	    		}
	    		
	    	}
    	
    
    	
    }
 
    public int markScore(GameState pState, int player) {
		    	
    	int normalMarks = 0;
    	int kingMarks = 0;
    	
    	for (int cell = 0; cell < pState.NUMBER_OF_SQUARES; cell++) {
    		    		
    		int valueInCell = pState.get(cell);
    		
    		if ( valueInCell == player) {
    			normalMarks += 1; 
    		}
    		
    		else if (valueInCell == player + 4) {
    			kingMarks += 1;
    		}
    		
    	}
    		
    	
    	return (normalMarks*5 + kingMarks*15);
	
    }
    
    public int locationScore(GameState pState) {
    	
    	//2) Marks in Opponents area = 2p
    	//	 Marks in our bottomline = 0.5p
    	//	 Marks on the edge = 2p
    	
    	int whiteLocationScore = 0;
    	int redLocationScore = 0;
    	

    		
		for (int i = 0; i < 7; i ++) {
			

        	int valueInCell = pState.get(i);
        	
        	if (valueInCell == Constants.CELL_WHITE | valueInCell == 5) {
		
        		whiteLocationScore += 3;
        		
        	}
		}
		
		for (int i = 28; i < 31; i++) {
			
        	int valueInCell = pState.get(i);
        	
        	if (valueInCell == Constants.CELL_WHITE | valueInCell == 5) {
		
        		whiteLocationScore += 1;
        		
        	}
        
		}
		
		for (int row = 0; row < 7; row++) {
		
			for (int col = 0; col < 7 ; col = col+6 ) {
	        	int valueInCell = pState.get(row, col);
	        	
	        	if (valueInCell == Constants.CELL_WHITE | valueInCell == 5) {
    		
	        		whiteLocationScore += 2;
        		
	        	}
			}

		}
    	

    		
		for (int i = 20; i < 31; i ++) {
			

        	int valueInCell = pState.get(i);
        	
        	if (valueInCell == Constants.CELL_RED | valueInCell == 5) {
		
        		redLocationScore += 3;
        		
        	}
		}
		
		for (int i = 0; i < 3; i++) {
			
        	int valueInCell = pState.get(i);
        	
        	if (valueInCell == Constants.CELL_RED | valueInCell == 5) {
		
        		redLocationScore += 1;
        		
        	}
        
		}
		
		for (int row = 0; row < 7; row++) {
		
			for (int col = 0; col < 7 ; col = col+6 ) {
	        	int valueInCell = pState.get(row, col);
	        	
	        	if (valueInCell == Constants.CELL_RED | valueInCell == 5) {
    		
	        		redLocationScore += 2;
        		
	        	}
			}

		}
    		
    		
    	
    		
    	return redLocationScore - whiteLocationScore;
    	
    	}

    public int jumpingScore(GameState pState) {
    	
    	//3) Player can jump over a king = 8p
    	//	 Player can jump over a normal mark = 3p
    	
    	
    	int jumpingScore = 0;
    	
    	int typeOfMove = pState.getMove().getType();
    	
    	if (typeOfMove == 1) {
    		
    		jumpingScore += 6;
    	
    	}
    	else if(typeOfMove == 2) {
    		
    		jumpingScore += 12;
    		
    	}
    	
    	else if(typeOfMove == 3) {
    		
    		jumpingScore +=18;
    	}
    	
    	

    	return jumpingScore;
    	
    }
     
    public int gamma(GameState pState) {
    	
    	//1) Normal, per mark = 5 p
    	//	 King, per mark = 5*3 p
    	
    	//2) Marks in Opponents area = 3p
    	//	 Marks in our bottomline = 1p
    	//	 Marks on the edge = 2p
    	
    	//3) Player can jump over a king = 8p
    	//	 Player can jump over a normal mark = 4p
    	
    	//4) Player has n marks among the four neighboring grid = 0.3*n
    	
    	
    	int redMarkScore = markScore(pState, Constants.CELL_RED);
    	int whiteMarkScore = markScore(pState, Constants.CELL_WHITE);
    	int locationScore = locationScore(pState);
    	
    	int jumpingScore;
    	if (pState.getNextPlayer() == Constants.CELL_WHITE) {
        	jumpingScore = jumpingScore(pState);
    	}
    	else {
    		jumpingScore = -jumpingScore(pState);
    	}

    	
		int totScore = redMarkScore - whiteMarkScore + locationScore + jumpingScore;
    	
    	return totScore;
    	
    	
    }
       
    public int max_value(GameState pState, int alpha, int beta, int depth) {
    	
    	int v =  Integer.MIN_VALUE;
    	
    	unsortedMoves = new HashMap<Integer, ArrayList<GameState>>();
    	
    	Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);
        
        for (GameState child: lNextStates) {
        	
        	int score = gamma(pState);
        	if (unsortedMoves.get(score) == null) {
        		unsortedMoves.put(score, new ArrayList<GameState>());
        	}
        	
        	unsortedMoves.get(score).add(child);
        	
        }
        
    	//Map<Integer, ArrayList<GameState>> sortedMoves = new TreeMap<Integer, ArrayList<GameState>>(unsortedMoves); 

        Object[] sortedMoves = unsortedMoves.keySet().toArray();
        Arrays.sort(sortedMoves);
        
		for (Object key : sortedMoves) {
			
			ArrayList<GameState> childList = unsortedMoves.get(key);
				
			Iterator<GameState> childListIterator = childList.iterator();
			
			while (childListIterator.hasNext()) {
			
				v = Math.max(v, alphaBeta(childListIterator.next(), alpha, beta, depth));
				
				alpha = Math.max(alpha,v);
				if (beta <= alpha) {
					break;
					
				}
			}

			
		}
    	
		return v;
    	
    }

    public int min_value(GameState pState, int alpha, int beta, int depth) {
    	
    	unsortedMoves = new HashMap<Integer, ArrayList<GameState>>();
    	
    	Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);
        
        for (GameState child: lNextStates) {
        	
        	int score = gamma(pState);
        	if (unsortedMoves.get(score) == null) {
        		unsortedMoves.put(score, new ArrayList<GameState>());
        	}
        	
        	unsortedMoves.get(score).add(child);
        	
        }
        
        Object[] sortedMoves = unsortedMoves.keySet().toArray();
        Arrays.sort(sortedMoves, Collections.reverseOrder());
    	
		int v = Integer.MAX_VALUE;
		
		for (Object key : sortedMoves) {
			
			ArrayList<GameState> childList = unsortedMoves.get(key);
				
			Iterator<GameState> childListIterator = childList.iterator();
		
			while (childListIterator.hasNext()) {
				
				v = Math.min(v, alphaBeta(childListIterator.next(), alpha, beta, depth-1));
				
				beta = Math.min(beta, v);
				
				if (beta <= alpha) {
					break;
				}
			}

			
		}
    	
		return v;
    	
    }
    

    
    
    
}
