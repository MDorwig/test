import java.util.*;

public class Main
{
	static MsgQueue msgqueue;	
	public static void main(String[] args)
	{
		Kws a = new Kws("a");
		Kws b = new Kws("b");
		
	 	a.start();
		b.start();
		
		Scanner input = new Scanner(System.in);
		while(true)
		{
			System.out.print("Enter Command: ");
			String s = input.next();
			System.out.println();
			
			switch(s)
			{
				case "connect": 
						a.Connect(b);
				break;
				
				case "disc":
						b.Disconnect(a);
				break;
				case "disp":
						a.SendMessage(b,Kws.MSFEvent.MesDisplReq);
				case "exit":
				return;
				
				default:
					System.out.printf("unknown command %s\n",s);
				break;
			}
		}
	}
}
