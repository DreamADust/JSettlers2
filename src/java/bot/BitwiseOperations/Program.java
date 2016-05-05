package bot.BitwiseOperations;
import bot.BitwiseOperations.Test;

public class Program
{
	public static void main(String[] args)
	{
		Test[] arr =
			{
				new Test(123),
				new Test(123123)
			};
		
		Position pos1 = new Position(84);
		System.out.println(pos1);
		
		Position pos2 = new Position(4, 5);
		System.out.println(pos2);
		
		Position pos3 = new Position(Position.PositionToNode(pos1));
		System.out.println(pos3);
		
		for(int i = 35; i < 221; i++)
		{
			Position pos = new Position(i);
			System.out.println(pos);
		}
	}
	
	public static void mainX(String[] args)
	{
		
		int [] hexPositions =  {51,53,55,83,85,87,89,115,117,119,121,123,149,151,153,155,183,185,187};
		int [] nodePositions = {35,37,39,67,69,71,73,101,103,105,107,165,167,169,171,173, 182,184,186,188, 189, 201, 203, 205};
		int pos = 154;
	
		
		System.out.println("********** HEX POSITIONS ***************");
		for (int i = 0; i < hexPositions.length; i++)
		{
			splitInteger(i);
		}
		
		System.out.println("********** NODE POSITIONS ***************");
		for (int i = 0; i < nodePositions.length; i++)
		{
			splitInteger(i);
		}
		
		int a = 84;
		
		String binary= Integer.toBinaryString(a);
		
		
	
		if (binary.length() < 8)
		{
			int zeroCount = 8-binary.length();
			for (int i = 0; i < zeroCount; i++)
			{
				binary = "0"+ binary;
			}
			System.out.println(binary);
		}
		System.out.println(binary);
		
		
		String binaryX = binary.substring(4);
		String binaryY = binary.substring(0,4);
		
		System.out.println(binaryX);
		int x = Integer.parseInt(binaryX);
		System.out.println(binaryX+ " realValue = "+ x);
		int y = Integer.parseInt(binaryY);
		System.out.println(binaryY + "realValue =  " + y);


	} 
	
	
	
	public static void splitInteger( int pos){
		byte [] data = new byte [2];
		data [0] = (byte) (pos & 0xF);
		data [1] = (byte) ((pos >> 8) & 0xF);
		
		//data[0] = 
		System.out.println(" " + data[0] + " X ," + data[1] + " Y" );
	}
}
