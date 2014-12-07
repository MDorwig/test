

public class Kws extends Thread
{
		MsgQueue msgq;
		public enum MSFEvent {
				MesNull(0),
				MesSetupReq(0x2180),
				MesReleaseReq(0x2181),
				MesReleaseCfm(0x2184),
				MesSetupInd(0x2182),
				MesSetupCfm(0x2183),
				MesDisplReq(0x2160);
				
				final int value;
				MSFEvent(int v) { value = v;}
		}
		
		enum State {
				IDLE,
				SETUPREQ,
				CONNECTED,
				DISPLAY,
				RELEASEREQ,
				SETUPIND,
				}
				
		MSFEvent evt;
		State [] state = { State.IDLE, State.IDLE, State.IDLE};
		int sp;
		String name;
		Kws(String n){
				msgq = new MsgQueue();
				name = n ;
				SetState(State.IDLE);
		}

		@Override
		public void run()
		{
			while(true)
			{
				MsgQueueItem item =	msgq.Get();
				OnMessage(item.orig,item.evt);
			}
		}
		static long lt;
		void Trace(String fmt,Object... args)
		{
				
				long now ;
				
				if (lt == 0)
						lt = System.currentTimeMillis();
				now = System.currentTimeMillis() - lt;
				
				System.out.printf("%8d %s:",now, name);
				System.out.printf(fmt,args);
		}
		
		void SetState(State st)
		{
				state[sp] = st;
				Trace("[%d] Enter State %s\n",sp,GetState().toString());
		}
		
		State GetState()
		{
				return state[sp];
		}
		
		void PushState(State st)
		{
				sp++;
				SetState(st);
		}
		
		State PopState()
		{
				State st ;
				sp--;
				st = GetState();
				Trace("[%d] Enter State %s\n",sp,st.toString());
				return st;
		}
		
		void OnMesSetupReq(Kws peer)
		{
				switch(GetState())
				{
						case IDLE:
								SetState(State.CONNECTED);
								SendMessage(peer,MSFEvent.MesSetupCfm);
						break;
						
						case SETUPREQ:
						break;
						
						case CONNECTED:
						case SETUPIND:
						case RELEASEREQ:
						case DISPLAY:
						break ;
				}
		}
		
		void SendMessage(Kws peer,MSFEvent evt)
		{
				MsgQueueItem i = new MsgQueueItem(this,evt);
				Trace("SendMessage %s to %s\n",evt.toString(),peer.name);
				peer.msgq.Post(i);
		}
		
		void OnReleaseReq(Kws peer)
		{
				switch(GetState())
				{
						case IDLE:
								Trace("Invalid State %s\n",GetState().toString());
						break;
						
						case SETUPREQ:
								SetState(State.IDLE);
						break;
						
						case CONNECTED:
								SetState(State.IDLE);	
						  	SendMessage(peer,MSFEvent.MesReleaseCfm);
						break;
						
						case DISPLAY:
						case RELEASEREQ:
						case SETUPIND:
						break ;
				}
		}
		
		void OnSetupCfm(Kws peer)
		{
				switch(GetState())
				{
						case SETUPREQ:
								SetState(State.CONNECTED);
						break;
						
						case CONNECTED:
						case DISPLAY:
						case RELEASEREQ:
						case IDLE:
						case SETUPIND:
						break ;
				}
		}
		
		void OnReleaseCfm()
		{
				switch(GetState())
				{
						case CONNECTED:
								SetState(State.IDLE);
						break;
						case DISPLAY:
						case RELEASEREQ:
						case SETUPREQ:
						case IDLE:
						case SETUPIND:
						break ;
				}
		}
		
		void OnDisplReq()
		{
				switch(GetState())
				{
						case CONNECTED:
								PushState(State.DISPLAY);
						break;
						
						case DISPLAY:
							switch(PopState())
							{
								default:
								break ;
							}
						break ;
						
						case IDLE:
						case RELEASEREQ:
						case SETUPREQ:
						case SETUPIND:
						break;
				}
		}
		
		void OnSetupInd()
		{
			
		}
		
		void OnMessage(Kws peer,MSFEvent evt)
		{
				Trace("OnMessage %s in State %s\n",evt.toString(),GetState().toString());
				switch(evt)
				{
						case MesSetupReq:
								OnMesSetupReq(peer);
						break;
						
						case MesSetupCfm:
								OnSetupCfm(peer);
						break;
						
						case MesReleaseReq:
								OnReleaseReq(peer);
						break;
						
						case MesReleaseCfm:
								OnReleaseCfm();
						break;
						
						case MesDisplReq:
								OnDisplReq();
						break;
						
						case MesSetupInd:
							OnSetupInd();
						break ;
						
						case MesNull:
						break ;
				}
		}
		
		void Connect(Kws peer)
		{
				SetState(State.SETUPREQ);
				SendMessage(peer,MSFEvent.MesSetupReq);
		}
		
		void Disconnect(Kws peer)
		{
				switch(GetState())
				{
						case IDLE:
						case DISPLAY:
						case RELEASEREQ:
						case SETUPIND:
						case SETUPREQ:
						break;

						case CONNECTED:
								peer.SendMessage(this,MSFEvent.MesReleaseReq);
						break;
				}
		}
}
