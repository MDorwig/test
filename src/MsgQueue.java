import java.util.*;

public class MsgQueue 
{
		List<MsgQueueItem> queue;
		MsgQueue()
		{
				queue = new ArrayList<MsgQueueItem>();
		}
		
		MsgQueueItem Get()
		{
				while(true)
				{
						if (queue.isEmpty())
						{
								try{
									synchronized(queue)
									{
										queue.wait();
									}
								}
								catch(Exception e)
								{
										System.out.printf("oops exception %s\n",e.toString());
								}
						}
						else
						{
								MsgQueueItem item = queue.get(0);
								queue.remove(item);
								return item;
						}
				}
		}
		
		void Post(MsgQueueItem item)
		{
			synchronized(queue)
			{
				queue.add(item);
				queue.notify();
			}
		}
}
