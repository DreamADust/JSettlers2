package bot.BitwiseOperations;

public class PositionPair 
{
	public Position X;
	public Position Y;
	

	public PositionPair(Position x, Position y){
		X = x;
		Y = y;
	}
	
	@Override
	public int hashCode() { return Position.PositionToNode(X) ^ Position.PositionToNode(Y); }
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null) return false;
		if(!(obj instanceof PositionPair)) return false;
		if(obj == this) return true;
		PositionPair other = (PositionPair)obj;
		return Position.PositionToNode(X).equals(Position.PositionToNode(other.X)) &&
				Position.PositionToNode(Y).equals(Position.PositionToNode(other.Y));
	}
	
	@Override
	public String toString()
	{
		return "Node: " + X + " - Best Node in Circle : " + Y ;
	}
	
	public void setY(Position y)
	{
		Y = y;
	}
	
	public void setX(Position x)
	{
		X = x;
	}

	public Integer getY()
	{
		return Position.PositionToNode(Y);
	}
	
	public Integer getX()
	{
		return Position.PositionToNode(X);
	}


}
