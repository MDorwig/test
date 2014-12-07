

public class FSM extends Thread
{
		MsgQueue msgq;
		
		public enum Event {
				EVT_NULL,
				EVT_SETUPREQ,
				EVT_RELEASEREQ,
				EVT_RELEASECFM,
				EVT_SETUPIND,
				EVT_SETUPCFM,
				EVT_DISPLREQ
		}
		
		enum State {
				IDLE,
				SETUPREQ,
				CONNECTED,
				DISPLAY,
				RELEASEREQ,
				SETUPIND,
				}
				
		Event evt;
		State [] state = { State.IDLE, State.IDLE, State.IDLE};
		int sp;
		String name;
		FSM(String n){
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
		
		void OnMesSetupReq(FSM peer)
		{
				switch(GetState())
				{
						case IDLE:
								SetState(State.CONNECTED);
								SendMessage(peer,Event.EVT_SETUPCFM);
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
		
		void SendMessage(FSM peer,Event evt)
		{
				MsgQueueItem i = new MsgQueueItem(this,evt);
				Trace("SendMessage %s to %s\n",evt.toString(),peer.name);
				peer.msgq.Post(i);
		}
		
		void OnReleaseReq(FSM peer)
		{
				switch(GetState())
				{
						case IDLE:
						break;
						
						case DISPLAY:
							if (PopState() == State.CONNECTED)
							{
								SetState(State.IDLE);	
								SendMessage(peer,Event.EVT_RELEASECFM);
							}
						break ;
						
						case RELEASEREQ:
						case SETUPIND:
						case SETUPREQ:
						case CONNECTED:
								SetState(State.IDLE);	
						  	SendMessage(peer,Event.EVT_RELEASECFM);
						break;
						
				}
		}
		
		void OnSetupCfm(FSM peer)
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
		
		void OnMessage(FSM peer,Event evt)
		{
				Trace("OnMessage %s in State %s\n",evt.toString(),GetState().toString());
				switch(evt)
				{
						case EVT_SETUPREQ:
								OnMesSetupReq(peer);
						break;
						
						case EVT_SETUPCFM:
								OnSetupCfm(peer);
						break;
						
						case EVT_RELEASEREQ:
								OnReleaseReq(peer);
						break;
						
						case EVT_RELEASECFM:
								OnReleaseCfm();
						break;
						
						case EVT_DISPLREQ:
								OnDisplReq();
						break;
						
						case EVT_SETUPIND:
								OnSetupInd();
						break ;
						
						case EVT_NULL:
						break ;
				}
		}
		
		void Connect(FSM peer)
		{
				SetState(State.SETUPREQ);
				SendMessage(peer,Event.EVT_SETUPREQ);
		}
		
		void Disconnect(FSM peer)
		{
				switch(GetState())
				{
						case IDLE:
						case RELEASEREQ:
						case SETUPIND:
						case SETUPREQ:
						break;

						case DISPLAY:
							if (PopState() == State.CONNECTED)
								peer.SendMessage(this, Event.EVT_RELEASEREQ);
						break ;
						
						case CONNECTED:
								peer.SendMessage(this,Event.EVT_RELEASEREQ);
						break;
				}
		}

		public void OnTimer()
		{
		}
}
