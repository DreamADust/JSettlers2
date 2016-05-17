package bot;

import java.util.*;

import soc.client.*;
import soc.client.SOCPlayerClient.*;
import soc.game.*;
import soc.message.SOCPutPiece;
import bot.BitwiseOperations.*;
public class Bot
{
	public static Bot Instance;
	public SOCPlayer player;
	public SOCGame game;
	public SOCBoard board;
	public SOCPlayerClient client;
	public GameManager manager;
	public Position position;
	public Program program;

	public Bot(SOCPlayer player, SOCGame game, SOCPlayerClient client)
	{
		Instance = this;

		this.player = player;
		this.game = game;
		board = game.getBoard();
		this.client = client;
		manager = GameManager.Instance;

		int[] hexes = board.getLandHexCoords();
		
		for (int id : hexes)
		{
		  System.out.println( giveHexType(id) + " " + board.getNumberOnHexFromCoord(id));
		}
		 
		// System.out.println("Total " + hexes.length + " hexes get.");
	}
	
	public String giveHexType(int hexCoord){
		switch (board.getHexTypeFromCoord(hexCoord))
		{
		case SOCBoard.DESERT_HEX:
			return "Hex #" + hexCoord + " => Desert" ;
			
		case SOCBoard.CLAY_HEX:
			return "Hex #" + hexCoord + " => Clay";
			
		case SOCBoard.ORE_HEX:
			return "Hex #" + hexCoord + " => Ore";
			
		case SOCBoard.SHEEP_HEX:
			return "Hex #" + hexCoord + " => Sheep";
			
		case SOCBoard.WHEAT_HEX:
			return "Hex #" + hexCoord + " => Wheat";
			
		case SOCBoard.WOOD_HEX:
			return "Hex #" + hexCoord + " => Wood";
			
		case SOCBoard.WATER_HEX:
			return "Hex #" + hexCoord + " => Water";
			
		default:
		//	return "Hex #" + hexCoord + " => Unknown";
			return null;
		}
	}
	

	double calculateHexValue(int hexCoord){
		return calculateTypeValue(hexCoord) * calculateNumberValue(hexCoord) ;
	}
	
	double calculateTypeValue(int hexCoord){
		switch (board.getHexTypeFromCoord(hexCoord))
		{
		case SOCBoard.DESERT_HEX:
			return 0;
		case SOCBoard.CLAY_HEX:
			return 3;
		case SOCBoard.ORE_HEX:
			return 1;
			
		case SOCBoard.SHEEP_HEX:
			return 2;
			
		case SOCBoard.WHEAT_HEX:
			return 3;
			
		case SOCBoard.WOOD_HEX:
			return 3;
			
		case SOCBoard.WATER_HEX:
			return 0;
			
		default:
			return -1;
		}
	}
	
	double calculateNumberValue(int HexId){
		
		switch (board.getNumberOnHexFromCoord(HexId))
		{
		case 0:
			return 0;
		case 1:
			return 0;
		case 2 :
			return 1;
		case 3 :
			return 2;
		case 4 :
			return 3;
		case 5 :
			return 4;
		case 6 :
			return 5;
		case 12 :
			return 1;
		case 11 :
			return 2;
		case 10 :
			return 3;
		case 9 :
			return 4;
		case 8 :
			return 5;
		default:
			return 0;
		}
	}

	/** SOCGame.START3A */

	public void DoSettlementTurn()
	{
		
		CustomButton button = new CustomButton("DoSettlementTurn", () ->
		{
			
			List <Position> nodes = new ArrayList<Position>();
			
			int maxValue = 0;
			int maxValueSettlementNode = 0;
			Position maxValuePosition = new Position (0 , 0);
			for (int i = 1; i < 13; i++)
			{
				for (int j = 1; j < 13; j++)
				{
					System.out.println("AdjacentHexesToNode from Position : " + new Position(i,j));
					System.out.println("Hexes Near Position " + board.getAdjacentHexesToNode(Position.PositionToNode(new Position(i,j))));
					nodes.add(new Position(i,j));
					Vector<Integer> neighbourHexes = board.getAdjacentHexesToNode(Position.PositionToNode(new Position(i,j)));
					int totalValue = 0;
					totalValue = getNodeValue(new Position(i,j) , neighbourHexes);
					
					if (totalValue > maxValue)
					{
						maxValue = totalValue;
						maxValueSettlementNode = Position.PositionToNode(new Position(i,j));
						maxValuePosition = new Position (i , j);
					}
					System.out.println("### Total Value ### " + totalValue);
				}
			}
			System.out.println("\n ### Max Value ### " + maxValue + " on Node ### " +  maxValuePosition + " \n" );
		
			manager.putPiece(game, new SOCSettlement(player,  maxValueSettlementNode, board));
			
			return 0;
		});
	}

	private int getNodeValue(Position node, Vector<Integer> neighbourHexes)
	{
		int id;
		int totalValue = 0;
		for (int k = 0; k < neighbourHexes.size(); k++)
		{
			id = neighbourHexes.get(k);
		if (giveHexType(id)!= null)
		{
			System.out.println( giveHexType(id) + " " + board.getNumberOnHexFromCoord(id) + " HexValue # " + calculateHexValue(id));
			totalValue += calculateHexValue(id)*isPotentialSettlement(Position.PositionToNode(node));
		}
		}
		return totalValue;
	}

	private int isPotentialSettlement (int location){
		HashSet<Integer> settlementPositions = player.getPotentialSettlements();
		if (settlementPositions.contains(location))
		{
			return 1;
		}else {
			return -1;
		}
	}

	/**
	 * SOCGame.START3B **** TODO CHANGE .get(0) to intelligent value
	 */
	public void DoRoadTurn()
	{
		
		getPlayerRoads(0);
		getPlayerRoads(1);
		getPlayerRoads(2);
		getPlayerRoads(3);
		testCalcRoads();
		testGetRoads();

		CustomButton button = new CustomButton("DoRoadTurn", () ->
		{
			int settlementCoord = player.getLastSettlementCoord();
			System.out.println("Last settlement coord : " + Position.NodeToPosition(settlementCoord));
			Vector<Integer> edgePositions = board.getAdjacentEdgesToNode(settlementCoord);
			System.out.println("Edge positions next to settlement : " + edgePositions);

			System.out.println("### Building Road on ### " + Position.NodeToPosition(edgePositions.get(0)));
			System.out.println("### Building Road on ### " + Position.NodeToPosition(edgePositions.get(1)));
			System.out.println("### Building Road on ### " + Position.NodeToPosition(edgePositions.get(2)));

			System.out.println("### " +findBestNodeInCircle(Position.NodeToPosition(settlementCoord))+ " " );
			int edgePositionFor2Nodes =
			board.getAdjacentEdgeToNode2Away(settlementCoord, Position.PositionToNode(findBestNodeInCircle(Position.NodeToPosition(settlementCoord)).Y));
		
			
			manager.putPiece(game, new SOCRoad(player, edgePositionFor2Nodes, board));
			/*
			if (board.isNodeOnLand(edgePositions.get(0)))
			{
				if (player.isPotentialRoad(edgePositions.get(0)))
				{
					manager.putPiece(game, new SOCRoad(player, edgePositions.get(0), board));
				}
			} else
			{
				manager.putPiece(game, new SOCRoad(player, edgePositions.get(1), board));
			}*/

			return 0;
		});
	}

	
	public Integer checkSettlementLocationMaxValue (){
		int maxValue = 0;
		int maxValueNode = 0;
		Position maxValuePosition = new Position(0,0);
		
		
		return 0;
	}
	
	public PositionPair findBestNodeInCircle(Position origin){
		
		int x = origin.X;
		int y = origin.Y;

		List <Position> positionList = new ArrayList <Position>();
		
		//check SouthEast
		Position posSE = new Position(x+2, y);
		positionList.add(posSE);
		
		//check SouthWest
		Position posSW = new Position(x-2, y);
		positionList.add(posSW);
		
		// check East
		Position posE = new Position(x+2, y+2);
		positionList.add(posE);
		
		//check West
		Position posW = new Position(x-2, y-2);
		positionList.add(posW);
		
		//check NorthEast
		Position posNE = new Position(x, y+2);
		positionList.add(posNE);
		
		//check NorthWest
		Position posNW = new Position(x, y-2);
		positionList.add(posNW);
		int maxValue = 0;
		Position maxValuePosition = new Position(0,0);
		for (int i = 0; i < positionList.size(); i++)
		{
			int totalValue = 0;
			
			Vector<Integer> neighbourHexes = board.getAdjacentHexesToNode(Position.PositionToNode( positionList.get(i)));
			
			totalValue = getNodeValue(positionList.get(i) , neighbourHexes);
			
			if (totalValue > maxValue)
			{
				maxValue = totalValue;
				maxValuePosition = positionList.get(i);
			}
			
		}
		System.out.println("### Best Node in Circle For " + origin + " is " + maxValuePosition + " Value is " + maxValue);
		
		return new PositionPair(origin, maxValuePosition);
	}
	
	public Integer checkSettlementLocation(Position origin){
		
		List <Position> nodes = new ArrayList <Position>();
		HashSet<Integer> settlementPositions = player.getPotentialSettlements();
		List <PositionPair> valueNodes = new ArrayList <PositionPair>();
		
		if (settlementPositions.contains(Position.PositionToNode(origin)))
		{
			
			Vector<Integer> neighbourHexes = board.getAdjacentHexesToNode(Position.PositionToNode(origin));
			int id;
			int totalValue = 0;
			for (int k = 0; k < neighbourHexes.size(); k++)
			{
				id = neighbourHexes.get(k);
			if (giveHexType(id)!= null)
			{
				System.out.println( giveHexType(id) + " " + board.getNumberOnHexFromCoord(id) + " HexValue # " + calculateHexValue(id));
				totalValue += calculateHexValue(id)*isPotentialSettlement(Position.PositionToNode(origin));
			}
			}
			
			/*
			if (totalValue > maxValue)
			{
				maxValue = totalValue;
				maxValueNode = Position.PositionToNode(origin);
				maxValuePosition = origin;
			}
			*/
			
		}
		
		int x = origin.X;
		int y = origin.Y;
		
				//check SouthEast
				Position posSE = new Position(x+2, y);
				checkSettlementLocation(posSE);
				
				//check SouthWest
				Position posSW = new Position(x-2, y);
				checkSettlementLocation(posSW);
				
				// check East
				Position posE = new Position(x+2, y+2);
				checkSettlementLocation(posE);
				
				//check West
				Position posW = new Position(x-2, y-2);
				
				//check NorthEast
				Position posNE = new Position(x, y+2);
				checkSettlementLocation(posNE);
				
				//check NorthWest
				Position posNW = new Position(x, y-2);
				checkSettlementLocation(posNW);
			
	
		return 0;
	}
	
	private void checkPositionToSettle(Position pos)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * SOCGame.PLAY ***TODO NORMAL TURN
	 */
	public void DoDiceTurn()
	{

		System.out.println();
		getPlayerRoads(0);
		getPlayerRoads(1);
		getPlayerRoads(2);
		getPlayerRoads(3);
		testCalcRoads();
		// testGetRoads();

		CustomButton button = new CustomButton("DoDiceTurn", () ->
		{
			// **** TODO ilk kartin seçilmesini saglamak
			if (game.canRollDice(player.getPlayerNumber()))
			{
				manager.rollDice(game);
				if (game.getCurrentDice() == 7)
				{
					manager.chooseRobber(game);
					manager.moveRobber(game, player, 121);

				}
			}
			if (player.hasUnplayedDevCards())
			{
				player.getInventory();
				manager.playDevCard(game, 0);

			}
			while (game.couldBuildRoad(player.getPlayerNumber()))
			{
				System.out.println("**** getLastRoadCoord : " + player.getLastRoadCoord());
				player.getLastRoadCoord();
				player.isConnectedByRoad(0, 0);
				List<Integer> roadNodes = player.getRoadNodes();

				for (int j = 0; j < roadNodes.size(); j++)
				{
					System.out.println("ITERATION " + j);
					
						game.buyRoad(player.getPlayerNumber());
						manager.putPiece(game, new SOCRoad(player, roadNodes.get(j), game.getBoard()));
					  if (!player.isLegalRoad(roadNodes.get(j)))
					{
						System.out.println("Legal Road lies in " + roadNodes.get(j));
					} else
					{
						System.out.println("Illegal Road lies in " + roadNodes.get(j));
					}
				}
			}
			while (game.couldBuildSettlement(player.getPlayerNumber()))
			{
				System.out.println("**** Roads of current player getRoads: " + player.getSettlements());
				System.out.println("**** getLastRoadCoord : " + player.getLastSettlementCoord());
				int[] sPositions = player.getPotentialSettlements_arr();
	
				for (int i = 0; i < sPositions.length; i++)
				{
					game.buySettlement(player.getPlayerNumber());
					game.putPiece(new SOCSettlement(player, sPositions[i], game.getBoard()));

				}
				

			}
			if (game.couldBuildCity(player.getPlayerNumber()))
			{
				System.out.println("*** Numbers Settlements touch " + player.getNumbers());
				game.buyCity(player.getPlayerNumber());
			
				
				//manager.putPiece(game, new City(player ,  ,game.getBoard()));
			
			}
			
			

			//manager.buyDevCard(game);
			manager.endTurn(game);
			return 0;
		});
	}
	
	public void findResourceStats(){
	
		// Code For Finding Least Amount of ResourceType
		int leastAmount = 7;
		int leastResources = -1;
		int maxAmount = 4;
		int maxResources = -1;
		
		List <Integer> resourceList = new ArrayList<Integer>();
		resourceList.add(  SOCResourceConstants.CLAY);
		resourceList.add(  SOCResourceConstants.WHEAT);
		resourceList.add(  SOCResourceConstants.SHEEP);
		resourceList.add(  SOCResourceConstants.WOOD);

		for (int i = 0; i < resourceList.size(); i++)
		{
			if (player.getResources().getAmount(resourceList.get(i)) <= leastAmount)
			{
				leastAmount = player.getResources().getAmount(resourceList.get(i));
				leastResources = resourceList.get(i);
			}
			
			if (player.getResources().getAmount(resourceList.get(i)) >= maxAmount)
			{
				maxAmount = player.getResources().getAmount(resourceList.get(i));
				maxResources = resourceList.get(i);
			}
		}
		
		if(maxResources != -1 && leastResources != -1)
		{
			SOCResourceSet give = null;
			switch(maxResources)
			{
			case SOCResourceConstants.CLAY:
				give = new SOCResourceSet(4, 0, 0, 0, 0, 0);
				System.out.println("Give 4 Clay");
				break;
			case SOCResourceConstants.WHEAT:
				give = new SOCResourceSet(0, 0, 0, 4, 0, 0);
				System.out.println("Give 4 Wheat");
				break;
			case SOCResourceConstants.SHEEP:
				give = new SOCResourceSet(0, 0, 4, 0, 0, 0);
				System.out.println("Give 4 Sheep");
				break;
			case SOCResourceConstants.WOOD:
				give = new SOCResourceSet(0, 0, 0, 0, 4, 0);
				System.out.println("Give 4 Wood");
				break;
			}
			
			SOCResourceSet get = null;
			switch(leastResources)
			{
			case SOCResourceConstants.CLAY:
				get = new SOCResourceSet(1, 0, 0, 0, 0, 0);
				System.out.println("Get 1 Clay");
				break;
			case SOCResourceConstants.WHEAT:
				get = new SOCResourceSet(0, 0, 0, 1, 0, 0);
				System.out.println("Get 1 Wheat");
				break;
			case SOCResourceConstants.SHEEP:
				get = new SOCResourceSet(0, 0, 1, 0, 0, 0);
				System.out.println("Get 1 Sheep");
				break;
			case SOCResourceConstants.WOOD:
				get = new SOCResourceSet(0, 0, 0, 0, 1, 0);
				System.out.println("Get 1 Wood");
				break;
			}
			
			if(game.canMakeBankTrade(give, get))
			{
				game.makeBankTrade(give, get);
				System.out.println("***Make Bank Trade");
			}
		}
		
		/*SOCResourceSet give = player.getResources().setAmount(4, maxResources);
		SOCResourceSet get = player.getResources().setAmount(1, leastResources);
		
		if (game.canMakeBankTrade(give, get))
		{
			game.makeBankTrade(give, get);
		}
		
		if (game.canMakeTrade(offering, accepting))
		{
			game.makeTrade(offering, accepting);
		}*/
		
		player.getResources().getAmount(SOCResourceConstants.CLAY);
		player.getResources().getAmount(SOCResourceConstants.WHEAT);
		player.getResources().getAmount(SOCResourceConstants.SHEEP);
		player.getResources().getAmount(SOCResourceConstants.ORE);
		player.getResources().getAmount(SOCResourceConstants.WOOD);
		
		
		player.getResourceRollStats();
		player.getRolledResources();
		player.getResources().contains(SOCResourceConstants.CLAY);
		player.getResources().contains(SOCResourceConstants.WHEAT);
		player.getResources().contains(SOCResourceConstants.SHEEP);
		player.getResources().contains(SOCResourceConstants.WOOD);
		
		if (player.getResources().getTotal() >= 7)
		{
		
			if (!player.getResources().contains(SOCResourceConstants.CLAY))
			{
				
			}
			if (!player.getResources().contains(SOCResourceConstants.WHEAT))
			{
				
			}
			if (!player.getResources().contains(SOCResourceConstants.SHEEP))
			{
				
			}
			if (!player.getResources().contains(SOCResourceConstants.WOOD))
			{
				
			}
		}
		
	}
	

	public void getPlayerRoads(int x)
	{
		for (int i = 0; i < game.getPlayer(x).getRoadNodes().size(); i++)
		{
			System.out.println("**** Roads of Player " + x + " GetRoadNodes " + i + " :  "
					+ game.getPlayer(x).getRoadNodes().get(i));
			System.out.println("##### IsLegalRoad : " + player.isLegalRoad(game.getPlayer(x).getRoadNodes().get(i)));
		}
	}

	public void testGetRoads()
	{
		List<SOCRoad> realRoadPositions = player.getRoads();
		List<Integer> roadNodes = player.getRoadNodes();

		for (int i = 0; i < realRoadPositions.size(); i++)
		{
			int[] nearNodes = realRoadPositions.get(i).getAdjacentNodes();
			for (int j = 0; j < nearNodes.length; j++)
			{
				System.out.println("##### Adjacent Nodes real RoadPos : " + nearNodes[i]);

			}
			System.out.println(
					"##### Adjacent Nodes real RoadPos length : " + realRoadPositions.get(i).getAdjacentNodes().length);

		}

	}

	public void testCalcRoads()
	{
		System.out.println("##### LongestRoadPath :" + player.getLRPaths());
		System.out.println("##### CALCLongestRoad : " + player.calcLongestRoad2());
		System.out.println("##### HasPotentialRoad : " + player.hasPotentialRoad());
	}

	/**
	 * SOCGame.WAITING_FOR_DISCARDS ****TODO SELECT DISCARD INSTEAD OF NULL
	 */
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

	/** SOCGame.WAITING_FOR_ROBBER_OR_PIRATE */
	public void DoMoveRobberTurn()
	{
		CustomButton button = new CustomButton("DoMoveRobberTurn", () ->
		{
			manager.chooseRobber(game);
			return 0;
		});
	}

	
	public List <SOCSettlement> getAllSettlements(){
		List <SOCSettlement> settlements = new ArrayList <SOCSettlement>();
		
		for (int i = 0; i < game.getPlayers().length; i++)
		{
			settlements.addAll( game.getPlayer(i).getSettlements());
		}
		
		settlements.removeAll (player.getSettlements());
		return settlements;
	}
	
	private int playerToSteal = 0;
	/**
	 * SOCGame.PLACING_ROBBER TODO ***** ADD POSITION HEX --- Added Random Player settlement Hex coord Other than "Player"
	 */
	public void DoWillMoveRobberTurn()
	{
		CustomButton button = new CustomButton("DoWillMoveRobberTurn", () ->
		{
			int robberPositionHex = 0;
				
			 Vector<Integer> hexList = board.getAdjacentHexesToNode(getAllSettlements().get(0).getCoordinates());
		
			 int robberHex = hexList.get(0);
			Vector<SOCPlayer> players = game.getPlayersOnHex(robberHex);
			
			
			playerToSteal = 0;
			for (int i = 0; i < players.size(); i++)
			{
				if (!(players.get(i).getPlayerNumber() == player.getPlayerNumber()))
				{
					playerToSteal = players.get(i).getPlayerNumber();

				} else {
					System.out.println("### Choose Another Player to Steal ###");
				}
			}
			manager.moveRobber(game, player, robberHex);

			return 0;
		});
	}

	/**
	 * SOCGame.WAITING_FOR_ROB_CHOOSE_PLAYER TODO **** ADD PLAYERTOSTEAL NUMBER
	 */
	public void DoSelectPlayerToStealTurn()
	{
		CustomButton button = new CustomButton("DoSelectPlayerToStealTurn", () ->
		{
			
			manager.choosePlayer(game, playerToSteal);
 
//			manager.choosePlayer(game, playerToSteal, robberHex);
			return 0;
		});
	}

	/** SOCGame.OVER */
	public void DoOver()
	{
		CustomButton button = new CustomButton("DoOver", () ->
		{
			return 0;
		});
	}

	public void StartGame(GameManager GM)
	{
		GM.startGame(game);
	}

}
