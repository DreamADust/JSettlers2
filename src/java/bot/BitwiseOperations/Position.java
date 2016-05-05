package bot.BitwiseOperations;
public class Position
{
	public Integer X;
	public Integer Y;
	
	public Position(Integer node)
	{
		Position pos = NodeToPosition(node);
		X = pos.X;
		Y = pos.Y;
	}
	
	public Position(Integer x, Integer y)
	{
		X = x;
		Y = y;
	}
	
	public static Position NodeToPosition(int node)
	{
		String binary = String.format("%08d", Integer.parseInt(Integer.toBinaryString(node)));
		return new Position(Integer.parseInt(binary.substring(0, 4), 2), Integer.parseInt(binary.substring(4, 8), 2));
	}
	
	public static Integer PositionToNode(Position position)
	{
		return Integer.parseInt(String.format("%04d", Integer.parseInt(Integer.toBinaryString(position.X))) + String.format("%04d", Integer.parseInt(Integer.toBinaryString(position.Y))), 2);
	}
	
	@Override
	public String toString()
	{
		return "X: " + X + " - Y: " + Y + " - Node: " + PositionToNode(this);
	}

}
