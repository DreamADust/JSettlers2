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
