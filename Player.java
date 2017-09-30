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
	
	
	
    public GameState bestGameState;
    public int bestScore;
    
    
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
       
        generateBestMove(pState);
        
        System.err.println(bestScore);
        
        return bestGameState;
        
        
    }    
    
    public void generateBestMove(GameState pState) {
    	
    	long startTime = System.currentTimeMillis();
    	long MaxAllowedTime = 2;
    	
        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);
    	
        int player = pState.getNextPlayer();
        
        GameState bestChild = new GameState();
        
        int score;
        
        
        // 
        for (int depth= 0; depth < Integer.MAX_VALUE; depth++) {
            //System.err.println(depth);
            //System.err.println(System.currentTimeMillis() - startTime);
        	if (System.currentTimeMillis() - startTime > MaxAllowedTime){
        		
        		break; 
        		
        	}
        	
        	int v = Integer.MIN_VALUE;

	        for (GameState child : lNextStates) {
	        	
	            score = alphaBeta(pState, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, player);
	
	        	if (score > v) {
	        		v = score;
	        		bestChild = child;
	        	}
	            
	        }
	        
	        this.bestScore = v;
	        
	        this.bestGameState = bestChild;  
        }
        

    }

    public int alphaBeta(GameState pState, int depth, int alpha, int beta, int player) {
    	
    	// State: the current state we are analysing
    	// alpha: the current best value achievable by A
    	// beta : the  current best value achievable by B
    	// player : the current player
    	
    	int v;
    	
        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);
    	    	
    	if (depth == 0 | lNextStates.size() == 0) {
    		
    		v = gamma(pState, player);
    		
    	}
    	
    	else {
    		if (player == Constants.CELL_RED) {
    			
    			v = Integer.MIN_VALUE;
    			
    			for (GameState child : lNextStates) {
    				v = Math.max(v, alphaBeta(child, depth-1, alpha, beta, Constants.CELL_WHITE ));
    				
    				
    				if (v > alpha) {
        				alpha = v;
        			}

        			if (beta <= alpha) {
        				break; // beta pruning
        			}
        		}
    			
    		}
    		
    		else {
    			
    			v = Integer.MAX_VALUE;
    			
    			for (GameState child : lNextStates) {
    				
    				v = Math.min(v, alphaBeta(child, depth-1, alpha, beta, Constants.CELL_RED));
    				
    				if (v > alpha) {
        				alpha = v;
        			}

        			if (beta <= alpha) {
        				break; // beta pruning
        			}
    				
    			}
    			
    			
    		}
    		
    	}
    	
    	
		return v;
    	
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
    
    public int locationScore(GameState pState, int player) {
    	
    	//2) Marks in Opponents area = 2p
    	//	 Marks in our bottomline = 0.5p
    	//	 Marks on the edge = 2p
    	
    	int locationScore = 0;
    	
    	if (player == Constants.CELL_WHITE){
    		
    		for (int i = 0; i < 7; i ++) {
    			

	        	int valueInCell = pState.get(i);
	        	
	        	if (valueInCell == Constants.CELL_WHITE | valueInCell == 5) {
    		
	        		locationScore += 3;
	        		
	        	}
    		}
    		
    		for (int i = 28; i < 31; i++) {
    			
	        	int valueInCell = pState.get(i);
	        	
	        	if (valueInCell == Constants.CELL_WHITE | valueInCell == 5) {
    		
	        		locationScore += 1;
	        		
	        	}
	        
    		}
    		
    		for (int row = 0; row < 7; row++) {
    		
    			for (int col = 0; col < 7 ; col = col+6 ) {
		        	int valueInCell = pState.get(row, col);
		        	
		        	if (valueInCell == Constants.CELL_WHITE | valueInCell == 5) {
	    		
		        		locationScore += 2;
	        		
		        	}
    			}

    		}
    		
    		
    	}
    	
    	else {
    		
    		for (int i = 20; i < 31; i ++) {
    			

	        	int valueInCell = pState.get(i);
	        	
	        	if (valueInCell == Constants.CELL_RED | valueInCell == 5) {
    		
	        		locationScore += 3;
	        		
	        	}
    		}
    		
    		for (int i = 0; i < 3; i++) {
    			
	        	int valueInCell = pState.get(i);
	        	
	        	if (valueInCell == Constants.CELL_RED | valueInCell == 5) {
    		
	        		locationScore += 1;
	        		
	        	}
	        
    		}
    		
    		for (int row = 0; row < 7; row++) {
    		
    			for (int col = 0; col < 7 ; col = col+6 ) {
		        	int valueInCell = pState.get(row, col);
		        	
		        	if (valueInCell == Constants.CELL_RED | valueInCell == 5) {
	    		
		        		locationScore += 2;
	        		
		        	}
    			}

    		}
    		
    		
    		
    	}
    		
    	return locationScore;
    	
    	}


    
    public int jumpingScore(GameState pState, int player) {
    	
    	//3) Player can jump over a king = 8p
    	//	 Player can jump over a normal mark = 3p
    	
    	int jumpingScore = 0;
    	
    	

    	return jumpingScore;
    	
    }
    
    
    public int gamma(GameState pState, int player) {
    	
    	//1) Normal, per mark = 5 p
    	//	 King, per mark = 5*3 p
    	
    	//2) Marks in Opponents area = 3p
    	//	 Marks in our bottomline = 1p
    	//	 Marks on the edge = 2p
    	
    	//3) Player can jump over a king = 8p
    	//	 Player can jump over a normal mark = 3p
    	
    	//4) Player has n marks among the four neighboring grid = 0.3*n
    	
    	int playerScore = totScore(pState, Constants.CELL_RED);
    	int otherPlayerScore = totScore(pState, Constants.CELL_WHITE);

    	return (playerScore - otherPlayerScore);
    	
    	
    }
    
    public int totScore(GameState pState, int player) {
    	int score = 0;
    	
    	int markScore = markScore(pState, player);
    	int locationScore = locationScore(pState, player);
    	int jumpingScore = jumpingScore(pState, player);
 	
    	
    	return markScore + locationScore + jumpingScore;
    }
    
    

    
    
}
