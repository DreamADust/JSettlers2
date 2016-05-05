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
		  System.out.println("Number for hex #" + id + " => " +
		  board.getNumberOnHexFromCoord(id));
		  System.out.println( giveHexType(id));
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
			return "Hex #" + hexCoord + " => Unknown";
			
		}
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
	

	double calculateScoreMaterial(int Type, int Number)
	{
		int[] hexes = board.getLandHexCoords();

		List <Position> hexCoordinates = new ArrayList<Position>();
		for (int id : hexes)
		{
		//	hexCoordinates[id] = Position.NodeToPosition(id);
			System.out.println("Calculating Hex Number " + id);
			System.out.println(Position.NodeToPosition(id));
			hexCoordinates.add(Position.NodeToPosition(id));
			System.out.println(Position.NodeToPosition(board.getAdjacentNodeToHex(id, 0)));
		}
		
	 	Position a = new  Position(hexCoordinates.get(0).X+1, hexCoordinates.get(0).Y );
	 	System.out.println("This is A : " + a);
		return 0;
	}
	
	/** SOCGame.START3A */
	private int sPos1 = -1;
	private int sPos2 = -1;

	public void DoSettlementTurn()
	{
		calculateScoreMaterial(0,0);
		
		CustomButton button = new CustomButton("DoSettlementTurn", () ->
		{
			int[] settlementPositions = player.getPotentialSettlements_arr();

			int firstPosition = settlementPositions[0];
			System.out.println("getHexTypeFromCoord  " + board.getHexTypeFromCoord(settlementPositions[0]));
			System.out.println("Our first Position" + Position.NodeToPosition(settlementPositions[0]));
			
			List <Position> nodes = new ArrayList<Position>();
			for (int i = 1; i < 13; i++)
			{
				for (int j = 1; j < 13; j++)
				{
					board.getAdjacentHexesToNode(Position.PositionToNode(new Position(i,j)));
					System.out.println("AdjacentHexesToNode from Position : " + new Position(i,j));
					System.out.println("Hexes Near Position " + board.getAdjacentHexesToNode(Position.PositionToNode(new Position(i,j))));
					nodes.add(new Position(i,j));
					Vector<Integer> neighbourHexes = board.getAdjacentHexesToNode(Position.PositionToNode(new Position(i,j)));
					for (int k = 0; k < neighbourHexes.size(); k++)
					{
						System.out.println("Type Of Hex "+ neighbourHexes.get(k) + " Type : " + giveHexType(neighbourHexes.get(k)) + "  Number : " 
					+ board.getHexNumFromCoord(neighbourHexes.get(k)));
						
					}
				}
			}
			
			boolean first = false;
			if (sPos1 == -1)
			{
				sPos1 = settlementPositions[0];
				first = true;
			} else
				sPos2 = settlementPositions[0];

			manager.putPiece(game, new SOCSettlement(player, (first ? sPos1 : sPos2), board));

			System.out.println("***Free settlement built. => (Node: " + (first ? sPos1 : sPos2) + ") (Coord: "
					+ board.nodeCoordToString((first ? sPos1 : sPos2)) + ")");

			return 0;
		});
	}

	/**
	 * SOCGame.START3B **** TODO CHANGE .get(0) to intelligent value
	 */
	public void DoRoadTurn()
	{
		Test();
		
		getPlayerRoads(0);
		getPlayerRoads(1);
		getPlayerRoads(2);
		getPlayerRoads(3);
		testCalcRoads();
		testGetRoads();

		CustomButton button = new CustomButton("DoRoadTurn", () ->
		{
			int settlementCoord = player.getLastSettlementCoord();
			System.out.println("Last settlement coord : " + settlementCoord);
			Vector<Integer> edgePositions = board.getAdjacentEdgesToNode(settlementCoord);
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

			manager.buyDevCard(game);
			manager.endTurn(game);
			return 0;
		});
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

	/**
	 * SOCGame.PLACING_ROBBER TODO ***** ADD POSITION HEX
	 */
	public void DoWillMoveRobberTurn()
	{
		CustomButton button = new CustomButton("DoWillMoveRobberTurn", () ->
		{
			int robberPositionHex = 0;
			manager.moveRobber(game, player, robberPositionHex);
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
			int playerToSteal = 0;
			manager.choosePlayer(game, playerToSteal);
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
