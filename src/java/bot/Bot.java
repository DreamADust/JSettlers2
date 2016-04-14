package bot;

import java.util.*;
import soc.client.*;
import soc.client.SOCPlayerClient.*;
import soc.game.*;
import soc.message.SOCPutPiece;

public class Bot
{
	public static Bot Instance;

	public SOCPlayer player;
	public SOCGame game;
	public SOCBoard board;
	public SOCPlayerClient client;
	public GameManager manager;
	
	
	public Bot(SOCPlayer player, SOCGame game, SOCPlayerClient client)
	{
		Instance = this;
		
		this.player = player;
		this.game = game;
		board = game.getBoard();
		this.client = client;
		manager = GameManager.Instance;
		
		int[] hexes = board.getLandHexCoords();
		for(int id : hexes)
		{
			switch(board.getHexTypeFromCoord(id))
			{
			case SOCBoard.DESERT_HEX:
				System.out.println("Hex #" + id + " => Desert");
				break;
			case SOCBoard.CLAY_HEX:
				System.out.println("Hex #" + id + " => Clay");
				break;
			case SOCBoard.ORE_HEX:
				System.out.println("Hex #" + id + " => Ore");
				break;
			case SOCBoard.SHEEP_HEX:
				System.out.println("Hex #" + id + " => Sheep");
				break;
			case SOCBoard.WHEAT_HEX:
				System.out.println("Hex #" + id + " => Wheat");
				break;
			case SOCBoard.WOOD_HEX:
				System.out.println("Hex #" + id + " => Wood");
				break;
			case SOCBoard.WATER_HEX:
				System.out.println("Hex #" + id + " => Water");
				break;
			default:
				System.out.println("Hex #" + id + " => Unknown");
				break;
			}
			
			System.out.println("Number for hex #" + id + " => " + board.getNumberOnHexFromCoord(id));
		}
		
		System.out.println("Total " + hexes.length + " hexes get.");
	}

	
	// SOCGame.START3A
	private int sPos1 = -1;
	private int sPos2 = -1;
	public void DoSettlementTurn()
	{
		System.out.println("****BOARD EFFECT***");
		boardEffect(0, 0, 0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0);
		
		CustomButton button = new CustomButton("DoSettlementTurn", () ->
		{
			int[] settlementPositions = player.getPotentialSettlements_arr();
						
			int firstPosition = settlementPositions[0];
			System.out.println("getHexTypeFromCoord  " +board.getHexTypeFromCoord(settlementPositions[0]) );
			
			System.out.println("getHexCoordinates " + board.getLandHexCoords());
			//board.getAdjacentNodeToNode(firstPosition, nodeDir);
		
		
			
			boolean first = false;
			if(sPos1 == -1)
			{
				sPos1 = settlementPositions[0];
				first = true;
			}
			else
				sPos2 = settlementPositions[0];
			
			manager.putPiece(game, new SOCSettlement(player, (first ? sPos1 : sPos2), board));
			
			System.out.println("***Free settlement built. => (Node: " + (first ? sPos1 : sPos2) + ") (Coord: " + board.nodeCoordToString((first ? sPos1 : sPos2)) + ")");
			
			return 0;
		});
	}
	
	public int getCoordinates(SOCPutPiece piece){
		return piece.getCoordinates();
		
	}
	
	public void boardEffect(int edge, int coord, int nodeCoord,int nodeDir, int node, int node2away, int hexCoord, boolean includeWater, int edgeCoord, int facing, int nodeA, int nodeB, int hex){
		System.out.println(
		 "edgeCoordToString   "+  board.edgeCoordToString(edge) +  "\n"+
		 "getAdjacentEdgesToEdge    "+board.getAdjacentEdgesToEdge(coord) +  "\n"+
		 "getAdjacentEdgesToNode    "+board.getAdjacentEdgesToNode(coord) + "\n"+
		 "getAdjacentEdgeToNode   "+board.getAdjacentEdgeToNode(nodeCoord, nodeDir) + "\n"+
		 "getAdjacentEdgeToNode2Away   "+board.getAdjacentEdgeToNode2Away(node, node2away) + "\n"+
		 "getAdjacentHexesToHex    "+board.getAdjacentHexesToHex(hexCoord, includeWater) + "\n"+
		 "getAdjacentHexesToHex   "+board.getAdjacentHexesToHex(hexCoord, includeWater) + "\n"+
		 "getAdjacentHexesToNode   "+board.getAdjacentHexesToNode(hexCoord) + "\n"+
		 "getAdjacentHexToEdge  "+board.getAdjacentHexToEdge(edgeCoord, facing) + "\n"+
		 "getAdjacentNodeFarEndOfEdge   "+	board.getAdjacentNodeFarEndOfEdge(edgeCoord, nodeCoord) + "\n"+
		 "getAdjacentNodesToEdge_arr   "+	board.getAdjacentNodesToEdge_arr(edgeCoord) + "\n"+
		 "getAdjacentNodesToEdge   "+board.getAdjacentNodesToEdge(edgeCoord) + "\n"+
		 "getAdjacentNodesToHex  "+board.getAdjacentNodesToHex(hexCoord) + "\n"+
		 "getAdjacentNodesToNode   "+	board.getAdjacentNodesToNode(edgeCoord) + "\n"+
		//board.getAdjacentNodeToNode2Away(nodeCoord, facing) + "/n"
		 "getBoardEncodingFormat   "+board.getBoardEncodingFormat()+
		 "getBoardHeight  "+	board.getBoardHeight() + "\n"+
		 "getBoardHeight  "+	board.getBoardHeight() + "\n"+
		 "getBoardWidth   "+	board.getBoardWidth() + "\n"+
		 "getEdgeBetweenAdjacentNodes  "+	board.getEdgeBetweenAdjacentNodes(nodeA, nodeB) + "\n"+
		 "getLandHexCoords  "+	board.getLandHexCoords() + "\n"+
		 "getHexNumFromCoord  " +	board.getHexNumFromCoord(hexCoord)+ "\n"+
		 "getHexTypeFromCoord  "+	board.getHexTypeFromCoord(hex) + "\n"+
		 "getNumberOnHexFromCoord  "+	board.getNumberOnHexFromCoord(hex)+ "\n"+
		 "getPortsEdges  " + board.getPortsEdges()+  "\n"+
		 "isHexOnWater  " + board.isHexOnWater(hexCoord) + "\n"+
		 "roadAtEdge  "+	board.roadAtEdge(edgeCoord)+ "\n"+ " hello " );
		
	}
	
	// SOCGame.START3B
	private int rPos1 = -1;
	private int rPos2 = -1;
	public void DoRoadTurn()
	{
		CustomButton button = new CustomButton("DoRoadTurn", () ->
		{
			
			
			int settlementCoord = player.getLastSettlementCoord();
			System.out.println("Last settlement coord : " + settlementCoord);
			Vector <Integer> edgePositions = board.getAdjacentEdgesToNode(settlementCoord);
			System.out.println("Edge positions next to settlement : " + edgePositions);
			
			if (board.isNodeOnLand(edgePositions.get(0)))
			{
				if (player.isPotentialRoad(edgePositions.get(0)))
				{
					manager.putPiece(game, new SOCRoad(player, edgePositions.get(0), board));
				}
			} else
			{
				manager.putPiece(game, new SOCRoad(player, edgePositions.get(1), board));
			}
			
			
//			List<Integer> roadPositions = new ArrayList<Integer>();
//			for(int i = 0; i < 1000; i++)
//				if(player.isPotentialRoad(i))
//					roadPositions.add(i);
//			
//			boolean first = false;
//			if(rPos1 == -1)
//			{
//				rPos1 = roadPositions.get(0);
//				first = true;
//			}
//			else
//				rPos2 = roadPositions.get(0);
//			
//			manager.putPiece(game, new SOCRoad(player, (first ? rPos1 : rPos2), board));
//			
//			System.out.println("***Free road built. => (Edge: " + (first ? rPos1 : rPos2) + ") (Coord: " + board.edgeCoordToString((first ? rPos1 : rPos2)) + ")");
//			
			return 0;
		});
	}

	// SOCGame.PLAY
	public void DoDiceTurn()
	{
		CustomButton button = new CustomButton("DoDiceTurn", () ->
		{
			return 0;
		});
	}

	// SOCGame.WAITING_FOR_DISCARDS
	public void DoDiscardTurn()
	{
		CustomButton button = new CustomButton("DoDiscardTurn", () ->
		{
			return 0;
		});
	}

	// SOCGame.WAITING_FOR_ROBBER_OR_PIRATE
	public void DoMoveRobberTurn()
	{
		CustomButton button = new CustomButton("DoMoveRobberTurn", () ->
		{
			return 0;
		});
	}

	// SOCGame.PLACING_ROBBER
	public void DoWillMoveRobberTurn()
	{
		CustomButton button = new CustomButton("DoWillMoveRobberTurn", () ->
		{
			return 0;
		});
	}

	// SOCGame.WAITING_FOR_ROB_CHOOSE_PLAYER
	public void DoSelectPlayerToStealTurn()
	{
		CustomButton button = new CustomButton("DoSelectPlayerToStealTurn", () ->
		{
			return 0;
		});
	}

	// SOCGame.OVER
	public void DoOver()
	{
		CustomButton button = new CustomButton("DoOver", () ->
		{
			return 0;
		});
	}

	public void RollDice(GameManager GM)
	{
		CustomButton button = new CustomButton("RollDice", () ->
		{
			return 0;
		});
	}

	public void EndTurn(GameManager GM)
	{
		CustomButton button = new CustomButton("EndTurn", () ->
		{
			return 0;
		});
	}

	public void StartGame(GameManager GM)
	{
		CustomButton button = new CustomButton("StartGame", () ->
		{
			return 0;
		});
	}
}

/*

abstract class GameComponents
{
	public abstract boolean hasResource();

}

abstract class Pieces extends GameComponents
{

	protected SOCResourceSet resource;

	public Pieces(SOCResourceSet resource)
	{
		this.resource = resource;
	}

	public int Build()
	{
		return 1;
	}

	public int Buy(SOCPlayer player, int pieceType, GameManager GM)
	{
		int leftPieces = player.getNumPieces(pieceType);
		System.out.println("***Yasin " + leftPieces + "For " + pieceType + " Left to Buy.");

		return 59;
	}

	public boolean containsResource(int resourceType)
	{
		return resource.contains(resourceType);
	}

	abstract List<Integer> findPossibleTemp();

}

class Cards extends GameComponents
{
	private SOCResourceSet resource;
	private SOCGame game;
	private SOCPlayer player;

	public Cards(SOCResourceSet resource, SOCGame game, SOCPlayer player)
	{
		this.resource = resource;
		this.game = game;
		this.player = player;
	}

	public void Buy(GameManager GM)
	{
		CustomButton button = new CustomButton("Buy", () ->
		{
			GM.buyDevCard(game);
			return 0;
		});
	}

	public void Play(GameManager GM, int devCardType)
	{
		CustomButton button = new CustomButton("Play", () ->
		{
			if (!player.hasPlayedDevCard())
			{
				GM.playDevCard(game, devCardType);
			}
			return 0;
		});
	}

	public boolean contains(final int[] array, final int key)
	{
		return Arrays.asList(array).contains(key);
	}

	public boolean hasResource()
	{
		return containsResource(SOCResourceConstants.WHEAT) && containsResource(SOCResourceConstants.SHEEP)
				&& containsResource(SOCResourceConstants.ORE);
	}

	public boolean containsResource(int resourceType)
	{
		return resource.contains(resourceType);
	}

	public SOCPlayer getPlayerInventory()
	{
		player.getInventory();
		return player;
	}

	public int CanPlayItemFromHand(int InventoryItemType)
	{
		return game.canPlayInventoryItem(player.getPlayerNumber(), InventoryItemType);
	}

}

class Roads extends Pieces
{
	public Bot bot;

	public Roads(Bot bot)
	{
		super(bot.player.getResources());
		this.bot = bot;
	}

	public void BuildFree()
	{
		List<Integer> roadPositions = findPossibleTemp();
		int location = roadPositions.get(0);
		bot.client.getGameManager().putPiece(bot.game, new SOCRoad(bot.player, location, bot.game.getBoard()));
	}

	public void Build(int GameState, SOCGame game, SOCPlayer player, GameManager GM)
	{
		List<Integer> roadPositions = player.getRoadNodes();
		int location = roadPositions.get(0);
		if (game.couldBuildRoad(player.getPlayerNumber()))
		{
			game.buyRoad(player.getPlayerNumber());
			GM.putPiece(game, new SOCRoad(player, location, game.getBoard()));
		}
	}

	@Override
	public boolean hasResource()
	{
		return containsResource(SOCResourceConstants.WOOD) && containsResource(SOCResourceConstants.CLAY);
	}

	@Override
	List<Integer> findPossibleTemp()
	{
		List<Integer> position = new ArrayList<Integer>();
		for (int edge = 0; edge < 1000; edge++)
		{
			if (bot.player.isPotentialRoad(edge))
			{
				position.add(edge);
			}
		}
		return position;
	}
}

class Settlements extends Pieces
{
	public Bot bot;

	public Settlements(Bot bot)
	{
		super(bot.player.getResources());
		this.bot = bot;
	}

	@Override
	public boolean hasResource()
	{
		return containsResource(SOCResourceConstants.CLAY) && containsResource(SOCResourceConstants.WOOD)
				& containsResource(SOCResourceConstants.WHEAT) & containsResource(SOCResourceConstants.SHEEP);
	}

	public void BuildFree()
	{
		int[] settlementPositions = bot.player.getPotentialSettlements_arr();
		bot.game.putPiece(new SOCSettlement(bot.player, settlementPositions[0], bot.game.getBoard()));
	}

	public void Build(SOCGame game, SOCPlayer player, GameManager GM)
	{
		int[] settlementPositions = player.getPotentialSettlements_arr();
		int location = settlementPositions[0];
		if (game.couldBuildSettlement(player.getPlayerNumber()))
		{
			game.buySettlement(player.getPlayerNumber());
			game.putPiece(new SOCSettlement(player, location, game.getBoard()));
		}
	}

	@Override
	List<Integer> findPossibleTemp()
	{
		// TODO Auto-generated method stub
		return null;
	}

}

class Cities extends Pieces
{
	SOCGame game;

	public Cities(SOCGame game, SOCResourceSet resource)
	{
		super(resource);
		this.game = game;
	}

	public int Build(SOCGame game, SOCPlayer player, GameManager GM)
	{
		List<Integer> cityPositions = new ArrayList<Integer>();
		int location = cityPositions.get(0);
		if (game.couldBuildCity(player.getPlayerNumber()))
		{
			game.buyCity(player.getPlayerNumber());
			game.putPiece(new SOCCity(player, location, game.getBoard()));

		}
		return 0;
	}

	@Override
	public boolean hasResource()
	{
		return containsResource(SOCResourceConstants.ORE) && containsResource(SOCResourceConstants.WHEAT)
				&& (resource.getAmount(SOCResourceConstants.ORE) >= 3)
				&& (resource.getAmount(SOCResourceConstants.WHEAT) >= 2);
	}

	@Override
	List<Integer> findPossibleTemp()
	{

		return null;
	}
}

abstract class Robber extends GameComponents
{
	SOCGame game;
	SOCPlayer player;

	public Robber(SOCGame game, SOCPlayer player)
	{
		this.game = game;
		this.player = player;
	}

	public void chooseRobber(GameManager GM)
	{
		CustomButton button = new CustomButton("chooseRobber", () ->
		{
			GM.chooseRobber(game);
			return 0;
		});
	}

	public void moveRobber(GameManager GM, int coordEdge)
	{
		CustomButton button = new CustomButton("moveRobber", () ->
		{
			GM.moveRobber(game, player, coordEdge);
			return 0;
		});
	}

	public void choosePlayer(GameManager GM, int playerToSteal)
	{
		CustomButton button = new CustomButton("choosePlayer", () ->
		{
			GM.choosePlayer(game, playerToSteal);
			return 0;
		});
	}
}

abstract class Trade extends GameComponents
{
	SOCPlayer player;
	SOCGame game;

	public Trade(SOCPlayer player, SOCGame game)
	{
		this.player = player;
		this.game = game;
	}

	public void rejectOffer(GameManager GM)
	{
		CustomButton button = new CustomButton("rejectOffer", () ->
		{
			GM.rejectOffer(game);
			return 0;
		});
	}

	public void clearOffer(GameManager GM)
	{
		CustomButton button = new CustomButton("clearOffer", () ->
		{
			GM.clearOffer(game);
			return 0;
		});
	}

	public void acceptOffer(int playerThatOffers, GameManager GM)
	{
		CustomButton button = new CustomButton("acceptOffer", () ->
		{
			GM.acceptOffer(game, playerThatOffers);
			System.out.println("***Yasin: Accepted Offer.");
			return 0;
		});
	}

	public void tradeWithBank(final int[] give, final int[] get, GameManager GM)
	{
		CustomButton button = new CustomButton("tradeWithBank", () ->
		{
			SOCResourceSet giveSet = new SOCResourceSet(give);
			SOCResourceSet getSet = new SOCResourceSet(get);
			GM.bankTrade(game, giveSet, getSet);
			return 0;
		});
	}
}
*/
