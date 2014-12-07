public class MsgQueueItem
{
		Kws.MSFEvent evt;
		Kws orig;
		MsgQueueItem(Kws sender,Kws.MSFEvent evt)
		{
				this.orig = sender;
				this.evt = evt;
		}
}
