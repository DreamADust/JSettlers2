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
	
	public void PlayerEffect(SOCResourceSet rolled, String desc, int svp, boolean checkAdjacents, int node, int ptype, int pieceType, int portType, boolean all, SOCPlayingPiece piece, String na, int slas, SOCTradeOffer of, int node1, int node2){
	    player.calcLongestRoad2() ;
	    player.addRolledResources(rolled) ;
	    player.addSpecialVPInfo(svp, desc) ;
	    player.addLegalSettlement(node, checkAdjacents) ;
	    player.getFaceId() ;
	    player.getLastRoadCoord() ;
	    player.getLastSettlementCoord() ;
	    player.getName();
	    player.getNeedToPickGoldHexResources();
	    player.getNumKnights() ;
	    player.getNumPieces(ptype);
	    player.getPlayerNumber() ;
	    player.getPublicVP() ;
	    player.getScenarioPlayerEvents() ;
	    player.getScenarioSVPLandAreas() ;
	    player.getSpecialVP() ;
	    player.getStartingLandAreasEncoded() ;
	    player.getTotalVP() ;
	    player.toString();
	    player.canBuildInitialPieceType(pieceType);
	    player.canPlaceSettlement(node);
	    player.getCities();
	    player.getCurrentOffer() ;
	    player.getLRPaths();
	    player.getNeedToDiscard();
	    player.getPieces() ;
	    player.getPortFlag(portType) ;
	    player.getPortFlags() ;
	    player.getPortMovePotentialLocations(all) ;
	    player.getResourceRollStats();
	    player.updatePotentials(piece);
	    player.setStartingLandAreasEncoded(slas);
	    player.setName(na);
	    player.setCurrentOffer(of);
	    player.isConnectedByRoad(node1, node2);
	    player.incrementNumKnights();
	}
	
	/*
	 * 	if (player.hasLongestRoad())
				{
					
				}
	 */
	
	// SOCGame.START3B 
	//**** TODO CHANGE .get(0) to intelligent value
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
			
			return 0;
		});
	}

	// SOCGame.PLAY 
	//***TODO NORMAL TURN
	public void DoDiceTurn()
	{
		CustomButton button = new CustomButton("DoDiceTurn", () ->
		{
			// **** TODO ilk kartin seçilmesini saglamak
			if (game.canRollDice(player.getPlayerNumber()))
			{
				manager.rollDice(game);
				if (game.getCurrentDice() == 7)
				{
					
				}
			}else if (player.hasUnplayedDevCards())
			{
				player.getInventory();
				manager.playDevCard(game, 0);
				
			} else if ( game.couldBuildRoad(player.getPlayerNumber()))
			{
				System.out.println("**** Roads of current player getRoads: " + player.getRoads());
				System.out.println("**** getLastRoadCoord : " + player.getLastRoadCoord()); 
				System.out.println("*** getRoad "+ player.getRoadNodes().toString());
				List<Integer> roadPositions = player.getRoadNodes();
				
				for (int i = 0; i < player.getRoadNodes().size(); i++)
				{
					System.out.println("**** Roads of current Player GetRoads  : " + player.getRoadNodes().get(i));
				}
				
				
				for (int i = 0; i < roadPositions.size(); i++)
				{
					if (player.isPotentialRoad(roadPositions.get(i)))
					{
						game.buyRoad(player.getPlayerNumber());
						manager.putPiece(game, new SOCRoad(player, roadPositions.get(i), game.getBoard()));		
					}
				}
				
			
			} else if (game.couldBuildSettlement(player.getPlayerNumber()))
			{
				System.out.println("**** Roads of current player getRoads: " + player.getSettlements());
				System.out.println("**** getLastRoadCoord : " + player.getLastSettlementCoord()); 
				int[] settlementPositions = player.getPotentialSettlements_arr();
				int location = settlementPositions[0];
				if (game.couldBuildSettlement(player.getPlayerNumber()))
				{
					game.buySettlement(player.getPlayerNumber());
					game.putPiece(new SOCSettlement(player, location, game.getBoard()));
				}
				
			}else if (game.couldBuildCity(player.getPlayerNumber()))
			{
				System.out.println("*** Numbers Settlements touch " + player.getNumbers());
			    Vector<SOCSettlement > settlements = player.getSettlements();
				
			}
			
			manager.buyDevCard(game);			
			manager.endTurn(game);
			return 0;
		});
	}

	// SOCGame.WAITING_FOR_DISCARDS 
	//****TODO SELECT DISCARD INSTEAD OF NULL
	public void DoDiscardTurn()
	{
		CustomButton button = new CustomButton("DoDiscardTurn", () ->
		{
			SOCResourceSet set = null;
			if (player.getNeedToDiscard())
			{
			manager.discard(game, null);	
			}
			return 0;
		});
	}
	
	
	// SOCGame.WAITING_FOR_ROBBER_OR_PIRATE
	public void DoMoveRobberTurn()
	{
		CustomButton button = new CustomButton("DoMoveRobberTurn", () ->
		{
			manager.chooseRobber(game);
			return 0;
		});
	}

	// SOCGame.PLACING_ROBBER
	//TODO  ***** ADD POSITION HEX
	public void DoWillMoveRobberTurn()
	{
		CustomButton button = new CustomButton("DoWillMoveRobberTurn", () ->
		{
			int robberPositionHex = 0;
			manager.moveRobber(game, player, robberPositionHex);
			return 0;
		});
	}

	// SOCGame.WAITING_FOR_ROB_CHOOSE_PLAYER
	// TODO **** ADD PLAYERTOSTEAL NUMBER
	public void DoSelectPlayerToStealTurn()
	{
		CustomButton button = new CustomButton("DoSelectPlayerToStealTurn", () ->
		{
			int playerToSteal = 0;
			manager.choosePlayer(game, playerToSteal);
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


	public void StartGame(GameManager GM)
	{
		CustomButton button = new CustomButton("StartGame", () ->
		{
			GM.startGame(game);
			return 0;
		});
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

