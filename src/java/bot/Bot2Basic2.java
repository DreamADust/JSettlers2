package bot;

import java.util.*;
import java.util.Map.Entry;

import soc.client.*;
import soc.client.SOCPlayerClient.*;
import soc.game.*;
import soc.message.SOCMessageForGame;
import soc.message.SOCPutPiece;
import bot.BitwiseOperations.*;

public class Bot2Basic2
{
	public static Bot2Basic2 Instance;
	public SOCPlayer player;
	public SOCGame game;
	public SOCBoard board;
	public SOCPlayerClient client;
	public GameManager manager;
	public Position position;
	public Program program;

	public Bot2Basic2(SOCPlayer player, SOCGame game, SOCPlayerClient client)
	{
		Instance = this;

		this.player = player;
		this.game = game;
		board = game.getBoard();
		this.client = client;
		manager = GameManager.Instance;

		List<Integer> roadNodes = findAllRoadNodes();
		for (int id : roadNodes)
		{
			int[] x = board.getAdjacentNodesToEdge_arr(id);
		}
		List<PositionPair> locationPairs = findAllBestLocations();

	}

	public String giveHexType(int hexCoord)
	{
		switch (board.getHexTypeFromCoord(hexCoord))
		{
		case SOCBoard.DESERT_HEX:
			return "Hex #" + hexCoord + " => Desert";

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
			return null;
		}
	}

	List<Integer> findAllRoadNodes()
	{
		List<Integer> edgeLocations = new ArrayList<Integer>();
		for (int i = 2; i < 13; i++)
		{
			for (int j = 2; j < 13; j++)
			{
				if (board.isNodeOnLand(Position.PositionToNode(new Position(i, j))))
				{
					Vector<Integer> adjacentNodes = board
							.getAdjacentEdgesToNode(Position.PositionToNode(new Position(i, j)));
					for (int k = 0; k < adjacentNodes.size(); k++)
					{
						if (!edgeLocations.contains(adjacentNodes.get(k)))
						{
							edgeLocations.add(adjacentNodes.get(k));
						}
					}
				}
			}
		}
		return edgeLocations;

	}

	List<PositionPair> findAllBestLocations()
	{

		List<PositionPair> bestLocations = new ArrayList<PositionPair>();
		for (int i = 2; i < 13; i++)
		{
			for (int j = 2; j < 13; j++)
			{
				if (board.isNodeOnLand(Position.PositionToNode(new Position(i, j))))
				{
					bestLocations.add(findBestNodeInCircle(new Position(i, j)));
				}
			}
		}
		return bestLocations;
	}

	double calculateHexValue(int hexCoord)
	{
		return calculateTypeValue(hexCoord) * calculateNumberValue(hexCoord);
	}

	double calculateTypeValue(int hexCoord)
	{
		switch (board.getHexTypeFromCoord(hexCoord))
		{
		case SOCBoard.DESERT_HEX:
			return 0;

		case SOCBoard.CLAY_HEX:
			return 4;

		case SOCBoard.ORE_HEX:
			return 2;

		case SOCBoard.SHEEP_HEX:
			return 3;

		case SOCBoard.WHEAT_HEX:
			return 4;

		case SOCBoard.WOOD_HEX:
			return 4;

		case SOCBoard.WATER_HEX:
			return 0;

		default:
			return -1;
		}
	}

	double calculateNumberValue(int HexId)
	{

		switch (board.getNumberOnHexFromCoord(HexId))
		{
		case 0:
			return 0;
		case 1:
			return 0;
		case 2:
			return 1;
		case 3:
			return 2;
		case 4:
			return 3;
		case 5:
			return 4;
		case 6:
			return 5;
		case 12:
			return 1;
		case 11:
			return 2;
		case 10:
			return 3;
		case 9:
			return 4;
		case 8:
			return 5;
		default:
			return 0;
		}
	}

	public void DoSettlementTurn()
	{

		CustomButton button = new CustomButton("DoSettlementTurn", () ->
		{

			List<Position> nodes = new ArrayList<Position>();

			int maxValue = 0;
			int maxValueSettlementNode = 0;
			Position maxValuePosition = new Position(0, 0);
			for (int i = 1; i < 13; i++)
			{
				for (int j = 1; j < 13; j++)
				{
					nodes.add(new Position(i, j));
					Vector<Integer> neighbourHexes = board
							.getAdjacentHexesToNode(Position.PositionToNode(new Position(i, j)));
					int totalValue = 0;
					totalValue = getNodeValue(new Position(i, j), neighbourHexes);

					if (totalValue > maxValue)
					{
						maxValue = totalValue;
						maxValueSettlementNode = Position.PositionToNode(new Position(i, j));
						maxValuePosition = new Position(i, j);
					}
				}
			}
			manager.putPiece(game, new SOCSettlement(player, maxValueSettlementNode, board));

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
			if (giveHexType(id) != null)
			{
				totalValue += calculateHexValue(id) * isPotentialSettlement(Position.PositionToNode(node));
			}
		}
		return totalValue;
	}

	private int isPotentialSettlement(int location)
	{
		HashSet<Integer> settlementPositions = player.getPotentialSettlements();
		if (settlementPositions.contains(location))
		{
			return 1;
		} else
		{
			return -1;
		}
	}
	
	public void DoRoadTurn()
	{
		
		CustomButton button = new CustomButton("DoRoadTurn", () ->
		{
			int settlementCoord = player.getLastSettlementCoord();
			Vector<Integer> edgePositions = board.getAdjacentEdgesToNode(settlementCoord);
			int bestNode = Position.PositionToNode(findBestNodeInCircle(Position.NodeToPosition(settlementCoord)).Y);
			int edgePositionFor2Nodes = board.getAdjacentEdgeToNode2Away(settlementCoord, bestNode);

			board.getAdjacentNodeFarEndOfEdge(edgePositionFor2Nodes, bestNode);
			manager.putPiece(game, new SOCRoad(player, edgePositionFor2Nodes, board));

			return 0;
		});
	}

	public PositionPair findBestNodeInCircle(Position origin)
	{

		int x = origin.X;
		int y = origin.Y;

		List<Position> positionList = new ArrayList<Position>();

		// check SouthEast
		Position posSE = new Position(x + 2, y);
		positionList.add(posSE);

		// check SouthWest
		Position posSW = new Position(x - 2, y);
		positionList.add(posSW);

		// check East
		Position posE = new Position(x + 2, y + 2);
		positionList.add(posE);

		// check West
		Position posW = new Position(x - 2, y - 2);
		positionList.add(posW);

		// check NorthEast
		Position posNE = new Position(x, y + 2);
		positionList.add(posNE);

		// check NorthWest
		Position posNW = new Position(x, y - 2);
		positionList.add(posNW);
		int maxValue = 0;
		Position maxValuePosition = new Position(0, 0);
		for (int i = 0; i < positionList.size(); i++)
		{
			int totalValue = 0;

			Vector<Integer> neighbourHexes = board.getAdjacentHexesToNode(Position.PositionToNode(positionList.get(i)));

			totalValue = getNodeValue(positionList.get(i), neighbourHexes);

			if (totalValue > maxValue)
			{
				maxValue = totalValue;
				maxValuePosition = positionList.get(i);
			}

		}
		return new PositionPair(origin, maxValuePosition);
	}

	public Integer checkSettlementLocation(Position origin)
	{

		List<Position> nodes = new ArrayList<Position>();
		HashSet<Integer> settlementPositions = player.getPotentialSettlements();
		List<PositionPair> valueNodes = new ArrayList<PositionPair>();

		if (settlementPositions.contains(Position.PositionToNode(origin)))
		{

			Vector<Integer> neighbourHexes = board.getAdjacentHexesToNode(Position.PositionToNode(origin));
			int id;
			int totalValue = 0;
			for (int k = 0; k < neighbourHexes.size(); k++)
			{
				id = neighbourHexes.get(k);
				if (giveHexType(id) != null)
				{
					System.out.println(giveHexType(id) + " " + board.getNumberOnHexFromCoord(id) + " HexValue # "
							+ calculateHexValue(id));
					totalValue += calculateHexValue(id) * isPotentialSettlement(Position.PositionToNode(origin));
				}
			}
		}
		return 0;
	}
	
	public void DoLargestArmy()
	{
		if (!player.hasLargestArmy())
		{
			int currentLargestArmy = game.getPlayerWithLargestArmy().getNumKnights();

			if (player.getInventory().hasPlayable(SOCDevCardConstants.KNIGHT))
			{

			}

		}
	}

	public void DoLongestRoad()
	{
		if (!player.hasLongestRoad())
		{
			int currentLongestRoad = game.getPlayerWithLongestRoad().getLongestRoadLength();
			if (player.getInventory().hasPlayable(SOCDevCardConstants.ROADS))
			{
			}
			if (player.getInventory().hasPlayable(SOCDevCardConstants.MONO))
			{
				int res = 0;
				manager.monopolyPick(game, res);
			}
		}
	}

	public void DoTrade()
	{
		
		
		if (game.hasTradeOffers())
		{
			SOCTradeOffer offer = player.getCurrentOffer();
			SOCResourceSet get = offer.getGetSet();
			SOCResourceSet give = offer.getGiveSet();

			System.out.println("### Offer to player ###" + offer);
			player.setCurrentOffer(null);
			manager.rejectOffer(game);

		}
	}
	
	public void getVictoryPoints(){
		player.getTotalVP();
	}
	
	 int possibleSettlementLocation = 0;

	public void DoDiceTurn()
	{
		CustomButton button = new CustomButton("DoDiceTurn", () ->
		{

			if (player.getResources().getTotal() >= 7)
			{
				DoBankTrade();
			}
			
			if (game.couldBuildRoad(player.getPlayerNumber()))
			{
				List<Integer> roadNodes = player.getRoadNodes();

				if (player.getResources().contains(SOCGame.ROAD_SET))
				{
					int j = 0;
					while (player.getResources().contains(SOCGame.ROAD_SET) && (player.getNumPieces(SOCPlayingPiece.ROAD)>0 ))
					{
						if (board.isNodeOnLand(roadNodes.get(j)))
						{
							game.buyRoad(player.getPlayerNumber());
							manager.putPiece(game, new SOCRoad(player, roadNodes.get(j), game.getBoard()));
						}
						j++;
					}
				}
				int settlementCoord = player.getLastSettlementCoord();
				Vector<Integer> edgePositions = board.getAdjacentEdgesToNode(settlementCoord);
				int bestNode = Position.PositionToNode(findBestNodeInCircle(Position.NodeToPosition(settlementCoord)).Y);
				calculateRoad(settlementCoord, bestNode, bestNode);
				
				player.isConnectedByRoad(0, 0);

			}
			if (game.couldBuildSettlement(player.getPlayerNumber()))
			{
				if (player.getResources().contains(game.SETTLEMENT_SET))
				{
					int[] sPositions = player.getPotentialSettlements_arr();

					game.buySettlement(player.getPlayerNumber());
					if (possibleSettlementLocation!= 0)
					{
						game.putPiece(new SOCSettlement(player, possibleSettlementLocation, game.getBoard()));

					}
					game.putPiece(new SOCSettlement(player, sPositions[0], game.getBoard()));
					
				}
				
				else if (player.hasUnplayedDevCards())
				{
					if (player.getInventory().hasPlayable(SOCDevCardConstants.MONO))
					{
						manager.playInventoryItem(game, SOCDevCardConstants.DISC);
						manager.monopolyPick(game, 0);
					}
					if (player.getInventory().hasPlayable(SOCDevCardConstants.DISC))
					{
						manager.playInventoryItem(game, SOCDevCardConstants.DISC);
						manager.discoveryPick(game, null);
					}
				} 
					
			}
			
			if (game.couldBuyDevCard(player.getPlayerNumber()))
			{
				manager.buyDevCard(game);
			}
			
			if (game.couldBuildCity(player.getPlayerNumber()))
			{
				if (player.getResources().contains(game.CITY_SET))
				{
					game.buyCity(player.getPlayerNumber());
				}
				else if (player.hasUnplayedDevCards())
				{
					if (player.getInventory().hasPlayable(SOCDevCardConstants.MONO))
					{
						manager.playInventoryItem(game, SOCDevCardConstants.DISC);
						manager.monopolyPick(game, 0);
					}
					if (player.getInventory().hasPlayable(SOCDevCardConstants.DISC))
					{
						manager.playInventoryItem(game, SOCDevCardConstants.DISC);
						manager.discoveryPick(game, null);
					}
				} 

			}
			manager.endTurn(game);
			return 0;
		});
	}

	private void calculateRoad(int settlementCoord, int bestNode, int counter)
	{
		int roadLocation;
		if (player.getResources().contains(SOCGame.ROAD_SET) && (player.getNumPieces(SOCPlayingPiece.ROAD) > 0))
		{
			if (!player.isConnectedByRoad(settlementCoord, bestNode))
			{

				int firstEdge = board.getAdjacentEdgeToNode2Away(settlementCoord, bestNode);
				int inBetweenNode = board.getAdjacentNodeFarEndOfEdge(firstEdge, settlementCoord);
				int secondEdge = board.getEdgeBetweenAdjacentNodes(inBetweenNode, bestNode);

				if (!player.getPieces().contains(new SOCRoad(player, firstEdge, board)))
				{
					if (board.roadAtEdge(firstEdge) == null)
					{
						game.buyRoad(player.getPlayerNumber());
						manager.putPiece(game, new SOCRoad(player, firstEdge, board));
					} else
					{
						if (counter > player.getSettlements().size())
						{
							counter = 0;
							int sCoord = player.getSettlements().get(counter).getCoordinates();
							Position bCoord = findBestNodeInCircle(Position.NodeToPosition(sCoord)).Y;
							calculateRoad(sCoord, Position.PositionToNode(bCoord), counter);
						} else
						{
							counter++;
							int sCoord = player.getSettlements().get(counter).getCoordinates();
							Position bCoord = findBestNodeInCircle(Position.NodeToPosition(sCoord)).Y;
							calculateRoad(sCoord, Position.PositionToNode(bCoord), counter);
						}

					}
				} else if (player.getPieces().contains(new SOCRoad(player, firstEdge, board)))
				{
					if (!player.getPieces().contains(new SOCRoad(player, secondEdge, board)))
					{

						if (board.roadAtEdge(secondEdge) == null)
						{
							game.buyRoad(player.getPlayerNumber());
							manager.putPiece(game, new SOCRoad(player, secondEdge, board));
							if (player.isPotentialSettlement(bestNode))
							{
								possibleSettlementLocation = bestNode;
							} else
							{
								roadLocation = bestNode;
								bestNode = Position
										.PositionToNode(findBestNodeInCircle(Position.NodeToPosition(roadLocation)).Y);
							}
						} else
						{
							Random random = new Random();
							int randomNum = random.nextInt(player.getRoadNodes().size());
							game.buyRoad(player.getPlayerNumber());
							manager.putPiece(game,
									new SOCRoad(player, player.getRoadNodes().get(randomNum), game.getBoard()));

						}
					} else
					{
						Random random = new Random();
						int randomNum = random.nextInt(player.getRoadNodes().size());
						game.buyRoad(player.getPlayerNumber());
						manager.putPiece(game,
								new SOCRoad(player, player.getRoadNodes().get(randomNum), game.getBoard()));
					}
				}
			}

		}

	}

	public void DoBankTrade()
	{
		int leastAmount = 7;
		int leastResources = -1;
		int maxAmount = 4;
		int maxResources = -1;

		List<Integer> getResourceList = new ArrayList<Integer>();
		getResourceList.add(SOCResourceConstants.CLAY);
		getResourceList.add(SOCResourceConstants.WHEAT);
		getResourceList.add(SOCResourceConstants.SHEEP);
		getResourceList.add(SOCResourceConstants.WOOD);

		List<Integer> giveResourceList = new ArrayList<Integer>();
		giveResourceList.add(SOCResourceConstants.CLAY);
		giveResourceList.add(SOCResourceConstants.WHEAT);
		giveResourceList.add(SOCResourceConstants.SHEEP);
		giveResourceList.add(SOCResourceConstants.WOOD);
		giveResourceList.add(SOCResourceConstants.ORE);

		
		for (int i = 0; i < getResourceList.size(); i++)
		{
			if (player.getResources().getAmount(getResourceList.get(i)) <= leastAmount)
			{
				leastAmount = player.getResources().getAmount(getResourceList.get(i));
				leastResources = getResourceList.get(i);
			}
		}

		for (int i = 0; i < giveResourceList.size(); i++)
		{
			if (player.getResources().getAmount(giveResourceList.get(i)) >= maxAmount)
			{
				maxAmount = player.getResources().getAmount(giveResourceList.get(i));
				maxResources = giveResourceList.get(i);
			}
		}

		if (maxResources != -1 && leastResources != -1)
		{
			SOCResourceSet give = null;
			switch (maxResources)
			{
			case SOCResourceConstants.CLAY:
				give = new SOCResourceSet(4, 0, 0, 0, 0, 0);
				break;
			case SOCResourceConstants.WHEAT:
				give = new SOCResourceSet(0, 0, 0, 4, 0, 0);
				break;
			case SOCResourceConstants.SHEEP:
				give = new SOCResourceSet(0, 0, 4, 0, 0, 0);
				break;
			case SOCResourceConstants.WOOD:
				give = new SOCResourceSet(0, 0, 0, 0, 4, 0);
				break;
			case SOCResourceConstants.ORE:
				give = new SOCResourceSet(0, 4, 0, 0, 0, 0);
				break;
			}

			SOCResourceSet get = null;
			switch (leastResources)
			{
			case SOCResourceConstants.CLAY:
				get = new SOCResourceSet(1, 0, 0, 0, 0, 0);
				break;
			case SOCResourceConstants.WHEAT:
				get = new SOCResourceSet(0, 0, 0, 1, 0, 0);
				break;
			case SOCResourceConstants.SHEEP:
				get = new SOCResourceSet(0, 0, 1, 0, 0, 0);
				break;
			case SOCResourceConstants.WOOD:
				get = new SOCResourceSet(0, 0, 0, 0, 1, 0);
				break;
			}

			if (game.canMakeBankTrade(give, get))
			{
				game.makeBankTrade(give, get);
				manager.bankTrade(game, give, get);
			}
		}
	}

	public void getPortType(int nodeCoord)
	{
		int portType = board.getPortTypeFromNodeCoord(nodeCoord);
		switch (portType)
		{
		case SOCBoard.CLAY_PORT:
			break;
		case SOCBoard.WOOD_PORT:
			break;
		case SOCBoard.SHEEP_PORT:
			break;
		case SOCBoard.ORE_PORT:
			break;
		case SOCBoard.WHEAT_PORT:
			break;
		case SOCBoard.MISC_PORT:
			break;
		}
	}

	/**
	 * SOCGame.WAITING_FOR_DISCARDS ****TODO SELECT DISCARD INSTEAD OF NULL
	 */
	public void DoDiscardTurn()
	{
		if (game.getGameState() == SOCGame.WAITING_FOR_DISCARDS)
		{
			SOCResourceSet set = null;
			if (player.getNeedToDiscard())
			{
				set = discardHand();

				game.discard(player.getPlayerNumber(), set);
				manager.discard(game, set);
			}

			return;
		}
	}

	private static Map<Integer, Integer> ValueSortedMap(Map<Integer, Integer> source)
	{
		List<Map.Entry<Integer, Integer>> values = new ArrayList<Map.Entry<Integer, Integer>>(source.entrySet());
		Collections.sort(values, new Comparator<Map.Entry<Integer, Integer>>()
		{
			@Override
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2)
			{
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		Map<Integer, Integer> sortedMap = new HashMap<Integer, Integer>();

		for (int i = 0; i < values.size(); i++)
		{
			Map.Entry<Integer, Integer> entry = values.get(i);
			sortedMap.put(entry.getKey(), entry.getValue());
			}
		return sortedMap;
	}

	private static List<Map.Entry<Integer, Integer>> SortMapToList(Map<Integer, Integer> source)
	{
		List<Map.Entry<Integer, Integer>> values = new ArrayList<Map.Entry<Integer, Integer>>(source.entrySet());
		Collections.sort(values, new Comparator<Map.Entry<Integer, Integer>>()
		{
			@Override
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2)
			{
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		return values;
	}

	private SOCResourceSet discardHand()
	{
		List<Integer> giveResourceList = new ArrayList<Integer>();
		giveResourceList.add(SOCResourceConstants.SHEEP);
		giveResourceList.add(SOCResourceConstants.CLAY);
		giveResourceList.add(SOCResourceConstants.WHEAT);
		giveResourceList.add(SOCResourceConstants.WOOD);
		giveResourceList.add(SOCResourceConstants.ORE);

		int maxAmount = 0;
		int maxAmount2 = 0;
		SOCResourceSet give = new SOCResourceSet();
		;
		SOCResourceSet SET = new SOCResourceSet();

		Map<Integer, Integer> discardMap = new HashMap<Integer, Integer>();
		List<IntegerPair> discardList = new ArrayList<IntegerPair>();

		for (int i = 0; i < giveResourceList.size(); i++)
		{

			discardList.add(
					new IntegerPair(giveResourceList.get(i), player.getResources().getAmount(giveResourceList.get(i))));
			discardMap.put(giveResourceList.get(i), player.getResources().getAmount(giveResourceList.get(i)));

			if (player.getResources().getAmount(giveResourceList.get(i)) > maxAmount)
			{
				maxAmount = player.getResources().getAmount(giveResourceList.get(i));
			} else if (player.getResources().getAmount(giveResourceList.get(i)) <= maxAmount)
			{
				maxAmount2 = player.getResources().getAmount(giveResourceList.get(i));
			}

		}

		int discardAmount = player.getResources().getKnownTotal() / 2;

		List<Map.Entry<Integer, Integer>> sortedList = SortMapToList(discardMap);

		for (int i = 0; i < sortedList.size(); i++)
		{

			System.out.println("Type: " + convertIntToResource(sortedList.get(i).getKey()) + " - Value: "
					+ sortedList.get(i).getValue());
		}

		int remainderAmount = 0;
		if (discardAmount > (sortedList.get(0).getValue()) - 1)
		{
			if (discardAmount > ((sortedList.get(0).getValue() - 1) + (sortedList.get(1).getValue() - 1)))
			{
				if (discardAmount > ((sortedList.get(0).getValue() - 1) + (sortedList.get(1).getValue() - 1)
						+ (sortedList.get(2).getValue() - 1)))
				{
					remainderAmount = sortedList.get(3).getValue() - (discardAmount - (sortedList.get(0).getValue() - 1)
							- (sortedList.get(1).getValue() - 1) - (sortedList.get(2).getValue() - 1));
					SET.add(sortedList.get(0).getValue() - 1, sortedList.get(0).getKey());
					SET.add(sortedList.get(1).getValue() - 1, sortedList.get(1).getKey());
					SET.add(sortedList.get(2).getValue() - 1, sortedList.get(2).getKey());
					SET.add(discardAmount - (sortedList.get(0).getValue() + sortedList.get(1).getValue()
							+ sortedList.get(2).getValue() - 3), sortedList.get(3).getKey());

				} else
				{
					remainderAmount = sortedList.get(2).getValue()
							- (discardAmount - (sortedList.get(0).getValue() - 1) - (sortedList.get(1).getValue() - 1));
					SET.add(sortedList.get(0).getValue() - 1, sortedList.get(0).getKey());
					SET.add(sortedList.get(1).getValue() - 1, sortedList.get(1).getKey());
					SET.add(discardAmount - ((sortedList.get(0).getValue() - 1) + (sortedList.get(1).getValue() - 1)),
							sortedList.get(2).getKey());
				}
			} else
			{
				remainderAmount = sortedList.get(1).getValue() - (discardAmount - (sortedList.get(0).getValue() - 1));
				SET.add(sortedList.get(0).getValue() - 1, sortedList.get(0).getKey());
				SET.add(discardAmount - (sortedList.get(0).getValue() - 1), sortedList.get(1).getKey());
			}
		} else
		{
			remainderAmount = sortedList.get(0).getValue() - discardAmount;
			SET.add(discardAmount, sortedList.get(0).getKey());

		}

		give = SET;
				
		return give;
	}

	public static String convertIntToResource(int number)
	{
		switch (number)
		{
		case SOCResourceConstants.CLAY:
			return "CLAY";
		case SOCResourceConstants.WHEAT:
			return "WHEAT";
		case SOCResourceConstants.SHEEP:
			return "SHEEP";
		case SOCResourceConstants.WOOD:
			return "WOOD";
		case SOCResourceConstants.ORE:
			return "ORE";
		default:
			return "UNKNOWN";
		}
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

	public List<SOCSettlement> getAllSettlements()
	{
		List<SOCSettlement> settlements = new ArrayList<SOCSettlement>();

		for (int i = 0; i < game.getPlayers().length; i++)
		{
			settlements.addAll(game.getPlayer(i).getSettlements());
		}

		settlements.removeAll(player.getSettlements());
		return settlements;
	}

	private int playerToSteal = 0;

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
		if (true)
		{
			manager.choosePlayer(game, playerToSteal);
			return;
		}
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
