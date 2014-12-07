import java.util.*;

class Timer extends Thread
{
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		while(true)
		{
			try
			{
				synchronized(this)
				{
					wait(10);
					for(FSM e : Main.members)
					{
						e.OnTimer();
					}
				}
			} 
			catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}
		}
	}
}

public class Main
{
	//static MsgQueue msgqueue;	
	static List<FSM> members; 
	
	public static void main(String[] args)
	{
		members = new ArrayList<FSM>();
		
		FSM a = new FSM("a");
		FSM b = new FSM("b");
		Timer tmr = new Timer();
		members.add(a);
		members.add(b);
		
		
	 	a.start();
		b.start();
		tmr.start();
		
		Scanner input = new Scanner(System.in);
		boolean ex = false ;
		while(!ex)
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
						a.SendMessage(b,FSM.Event.EVT_DISPLREQ);
				break ;
				
				case "exit":
					ex = true;
				break;
				
				default:
					System.out.printf("unknown command %s\n",s);
				break;
			}
		}
		input.close();
	}
}
