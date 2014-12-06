
public class Kws
{
		public enum MSFEvent {
				MesNull(0),
				MesSetupReq(0x2180),
				MesReleaseReq(0x2181),
				MesReleaseCfm(0x2184),
				MesSetupInd(0x2182),
				MesSetupCfm(0x2183);
				
				final int value;
				MSFEvent(int v) { value = v;}
		}
		
		enum State {
				IDLE,
				SETUPREQ,
				CONNECTED,
				RELEASEREQ,
				SETUPIND,
				}
				
		MSFEvent evt;
		State [] state = { State.IDLE, State.IDLE, State.IDLE};
		int sp;
		
		Kws(){
				SetState(State.IDLE);
		}
		
		void SetState(State st)
		{
				state[sp] = st;
				System.out.printf("[%d] Enter State %s\n",sp,GetState().toString());
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
				sp--;
				return GetState();
		}
		
		void OnMesSetupReq()
		{
				switch(GetState())
				{
						case IDLE:
								SetState(State.SETUPREQ);
								SendMessage(MSFEvent.MesSetupCfm);
						break;
						
						case SETUPREQ:
						break;
						
				}
		}
		
		void SendMessage(MSFEvent evt)
		{
				System.out.printf("SendMessage %s\n",evt.toString());
		}
		
		void OnReleaseReq()
		{
				switch(GetState())
				{
						case IDLE:
								System.out.printf("Invalid State %s\n",GetState().toString());
						break;
						
						case SETUPREQ:
								SetState(State.IDLE);
						break;
						
						case CONNECTED:
						   SendMessage(MSFEvent.MesReleaseCfm);
						break;
				}
		}
		
		void OnMessage(MSFEvent evt)
		{
				System.out.printf("OnMessage %s in State %s\n",evt.toString(),GetState().toString());
				switch(evt)
				{
						case MesSetupReq:
								OnMesSetupReq();
						break;
						
						case MesReleaseReq:
								OnReleaseReq();
						break;
				}
		}
}
