package bot.BitwiseOperations;

public class IntegerPair
{
	public Integer X;
	public Integer Y;
	
	public IntegerPair(Integer x, Integer y){
		X = x;
		Y = y;
	}
	
	@Override
	public int hashCode() { return X  ^  Y ; }
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null) return false;
		if(!(obj instanceof IntegerPair)) return false;
		if(obj == this) return true;
		IntegerPair other = (IntegerPair)obj;
		return X.equals(other.X) && Y.equals(other.Y);
	}
	
	public Integer getX(){
		return X;
	}
		
	public Integer getY(){
		return Y;
	}
	
	@Override
	public String toString()
	{
		return "First: " + X + " - Second : " + Y ;
	}
}
