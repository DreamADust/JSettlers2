package bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bot.BitwiseOperations.Position;
import soc.client.SOCPlayerClient;
import soc.client.SOCPlayerClient.GameManager;
import soc.game.SOCBoard;
import soc.game.SOCCity;
import soc.game.SOCGame;
import soc.game.SOCPlayer;
import soc.game.SOCResourceConstants;
import soc.game.SOCResourceSet;
import soc.game.SOCRoad;
import soc.game.SOCSettlement;

public class Tester
{
	public SOCPlayer player;
	public SOCGame game;
	public SOCBoard board;
	public SOCPlayerClient client;
	public GameManager manager;

	public static void main(String[] args)
	{

	}

	public Tester()
	{

		this.player = player;
		this.game = game;
		board = game.getBoard();
		this.client = client;
		manager = GameManager.Instance;

	}

	// board.getAdjacentNodeToNode(firstPosition, nodeDir);

	double calculateScoreMaterial(int Type, int Number)
	{
		int[] hexes = board.getLandHexCoords();

		for (int id : hexes)
		{

		}
		return 0;
	}

	double calculateScoreSettlements(int node)
	{

		return 0;
	}

	double calculateTotalScore(int Type, int Number)
	{
		int weightMaterial = 1;
		int weightPossibleSettlements = 1;
		return calculateScoreMaterial(Type, Number) * weightMaterial
				+ calculateScoreSettlements(Number) * weightPossibleSettlements;
	}

}

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
	private SOCBoard board;

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

	public void boardEffect(int edge, int coord, int nodeCoord, int nodeDir, int node, int node2away, int hexCoord,
			boolean includeWater, int edgeCoord, int facing, int nodeA, int nodeB, int hex)
	{
		System.out.println("edgeCoordToString   " + board.edgeCoordToString(edge) + "\n" + "getAdjacentEdgesToEdge    "
				+ board.getAdjacentEdgesToEdge(coord) + "\n" + "getAdjacentEdgesToNode    "
				+ board.getAdjacentEdgesToNode(coord) + "\n" + "getAdjacentEdgeToNode   "
				+ board.getAdjacentEdgeToNode(nodeCoord, nodeDir) + "\n" + "getAdjacentEdgeToNode2Away   "
				+ board.getAdjacentEdgeToNode2Away(node, node2away) + "\n" + "getAdjacentHexesToHex    "
				+ board.getAdjacentHexesToHex(hexCoord, includeWater) + "\n" + "getAdjacentHexesToHex   "
				+ board.getAdjacentHexesToHex(hexCoord, includeWater) + "\n" + "getAdjacentHexesToNode   "
				+ board.getAdjacentHexesToNode(hexCoord) + "\n" + "getAdjacentHexToEdge  "
				+ board.getAdjacentHexToEdge(edgeCoord, facing) + "\n" + "getAdjacentNodeFarEndOfEdge   "
				+ board.getAdjacentNodeFarEndOfEdge(edgeCoord, nodeCoord) + "\n" + "getAdjacentNodesToEdge_arr   "
				+ board.getAdjacentNodesToEdge_arr(edgeCoord) + "\n" + "getAdjacentNodesToEdge   "
				+ board.getAdjacentNodesToEdge(edgeCoord) + "\n" + "getAdjacentNodesToHex  "
				+ board.getAdjacentNodesToHex(hexCoord) + "\n" + "getAdjacentNodesToNode   "
				+ board.getAdjacentNodesToNode(edgeCoord) + "\n" +
				// board.getAdjacentNodeToNode2Away(nodeCoord, facing) + "/n"
				"getBoardEncodingFormat   " + board.getBoardEncodingFormat() + "getBoardHeight  "
				+ board.getBoardHeight() + "\n" + "getBoardHeight  " + board.getBoardHeight() + "\n"
				+ "getBoardWidth   " + board.getBoardWidth() + "\n" + "getEdgeBetweenAdjacentNodes  "
				+ board.getEdgeBetweenAdjacentNodes(nodeA, nodeB) + "\n" + "getLandHexCoords  "
				+ board.getLandHexCoords() + "\n" + "getHexNumFromCoord  " + board.getHexNumFromCoord(hexCoord) + "\n"
				+ "getHexTypeFromCoord  " + board.getHexTypeFromCoord(hex) + "\n" + "getNumberOnHexFromCoord  "
				+ board.getNumberOnHexFromCoord(hex) + "\n" + "getPortsEdges  " + board.getPortsEdges() + "\n"
				+ "isHexOnWater  " + board.isHexOnWater(hexCoord) + "\n" + "roadAtEdge  " + board.roadAtEdge(edgeCoord)
				+ "\n" + " hello ");
	}
	
	double calculateScoreMaterial(int Type, int Number)
	{
		int[] hexes = board.getLandHexCoords();

		List <Position> hexCoordinates = new ArrayList<Position>();
		for (int id : hexes)
		{
		//	hexCoordinates[id] = Position.NodeToPosition(id);
			System.out.println("Calculating Hex Number " + id);
			hexCoordinates.add(Position.NodeToPosition(id));
		}
		
	 	Position a = new  Position(hexCoordinates.get(0).X+1, hexCoordinates.get(0).Y );
	 	System.out.println("This is A : " + a);
		return 0;
	}
	

	int[] findNodes(int id)
	{
		int[] nodes = board.getAdjacentNodesToHex(id);
		for (int dir = 0; dir < 6; dir++)
		{
			switch (dir)
			{
			case 0:
				System.out.println("Node #" + nodes[dir] + " => North");
				break;
			case 1:
				System.out.println("Node #" + nodes[dir] + " => North East");
				break;
			case 2:
				System.out.println("Node #" + nodes[dir] + " => South East");
				break;
			case 3:
				System.out.println("Node #" + nodes[dir] + " => South ");
				break;
			case 4:
				System.out.println("Node #" + nodes[dir] + " => South West");
				break;
			case 5:
				System.out.println("Node #" + nodes[dir] + " => North West");
				break;
			default:
				System.out.println("Node #" + nodes[dir] + " => Unknown");
				break;
			}
		}
		return nodes;
	}
	

	public void Test(){
		int [] settlementPositions = player.getPotentialSettlements_arr();
	  
		System.out.println();
		System.out.println("Settlement Position 0 is " + 	Position.NodeToPosition(114) + "  " +Position.PositionToNode(Position.NodeToPosition(114)));
		Position.NodeToPosition(settlementPositions[0]);
		Position.PositionToNode(Position.NodeToPosition(settlementPositions[0]));
		System.out.println("");
		
	}
	
	void calculateMaxValue (int node, int totalValue, int maxValue){
		if (totalValue > maxValue)
		{
			maxValue = totalValue;
		}
	}
	
	private int isThereAnotherPlayer(int location)
	{
	  SOCPlayer[] playersList = game.getPlayers();
		int identifier = 1;

		for (int i = 0; i < playersList.length; i++)
		{
			if (playersList[i].getSettlements().contains(location))
			{
				System.out.println("Player " + playersList[i] + " have a settlement on " + location);
				identifier = identifier * -1;
			}
		}
		return identifier;
	}
	
	
}
