public class MsgQueueItem
{
		FSM.Event evt;
		FSM orig;
		MsgQueueItem(FSM sender,FSM.Event evt)
		{
				this.orig = sender;
				this.evt = evt;
		}
}
