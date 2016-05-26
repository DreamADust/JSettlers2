package bot;

import soc.game.SOCBoard;
import soc.game.SOCGame;

public class Edge
{
	public SOCBoard board;
	public SOCGame game;

	
	int EndNodeA = 0;
	int EndNodeB = 0;
	int Weight;
	String Code;
	public Edge(int EdgeNode){
		int[] nodes = board.getAdjacentNodesToEdge_arr(EdgeNode);
		board = game.getBoard();
		EndNodeA = nodes[0];
		EndNodeB = nodes[1];
		if (board.roadAtEdge(EdgeNode)!= null)
		{
			Code = board.roadAtEdge(EdgeNode).toString();
		}else{
			Code = "Empty";
		}

	}
	
	public int WeightedEdge(){
		if (EndNodeA == 0 | EndNodeB == 0)
		{
			return Weight;
		} else{
			
		//	Weight = NodeWeight(EndNodeA) + NodeWeight(EndNodeB);
		}
		
		return Weight;
	}
	
}
