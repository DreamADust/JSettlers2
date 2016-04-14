package bot;

import java.util.concurrent.Callable;
import javax.swing.*;

public class CustomButton
{
	public CustomButton(String operation, Callable<Integer> action)
	{
		JFrame frame = new JFrame();
		JButton button = new JButton(operation);
		
		frame.setSize(300, 150);
		
		button.addActionListener(e ->
		{
			int result = -1;
			
			try{ result = action.call(); }
			catch(Exception ex)
			{
				System.out.println("Operation (" + operation + ") action thrown an exception =>");
				
				System.out.println("#### STACK TRACE ####");
				
				StackTraceElement[] elems = ex.getStackTrace();
				for(StackTraceElement elem : elems)
					System.out.println(elem.toString());
				
				System.out.println("#### ~STACK TRACE ####");
			}
			
			System.out.println("Operation (" + operation + ") => " + result);
			
			frame.dispose();
		});
		
		frame.add(button);
		frame.setVisible(true);
		
		System.out.println("#### STACK TRACE (" + operation + ") ####");
		
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		
		for(StackTraceElement element : elements)
			System.out.println(element.toString());
		
		System.out.println("#### ~STACK TRACE (" + operation + ") ####");
	}
}
