import java.util.*;

public class Main
{
	public static void main(String[] args)
	{
		Kws a = new Kws();
		Kws b = new Kws();
		
		Scanner input = new Scanner(System.in);
		while(true)
		{
			System.out.print("Enter a Event: ");
			String s = input.next();
			System.out.println();
			
			Kws.MSFEvent evt = Kws.MSFEvent.MesNull;
			
			switch(s)
			{
				case "setupreq": evt = Kws.MSFEvent.MesSetupReq;break;
				case "releasereq":evt=Kws.MSFEvent.MesReleaseReq;break;
				
				default:
					System.out.printf("unknown event %s\n",s);
				break;
			}
			if (evt != Kws.MSFEvent.MesNull)
			{
  	  	a.OnMessage(b,evt);
			}
		}
	}
}
