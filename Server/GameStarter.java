public class GameStarter extends Thread
{
	private Server CurrentServer;
	private int Counter;
	
	public GameStarter(Server CurrentServer)
	{
		this.CurrentServer = CurrentServer;
	}
	
	public void run()
	{
		Counter = CurrentServer.StartCountdown;
		while(true)
		{
			try
			{
				sleep(900);
			}
			catch(Exception e)
			{
				System.out.println(e.toString());
			}
			
			if(CurrentServer.CurrentGame == null)
			{
				if(CurrentServer.StartCountdown > -1 && CurrentServer.ConList.countPlayers() >= CurrentServer.MinimumPlayers)
				{
					if(allPlayersAreReady())
					{
						if(Counter < 1)
						{
							CurrentServer.startNewGame();
						}
						else
						{
							CurrentServer.ConList.broadcast("MESSAGE " + CurrentServer.Colors[3] + " " + Counter + "...");
							CurrentServer.ConList.broadcast("PLAYSOUND 4");
							CurrentServer.display(Counter + "...");
						}
						Counter--;
					}
					else
					{
						Counter = CurrentServer.StartCountdown;
					}
				}
			}
			else if(CurrentServer.CurrentGame.CurrentGamer != null)
			{
				if(CurrentServer.CurrentGame.AuctionStarted)
				{
					Counter = 0;
					Gamer CurGamer = CurrentServer.CurrentGame.First;
					while(CurGamer != null)
					{
						if(CurGamer.isInAuction && CurGamer != CurrentServer.CurrentGame.CurrentOfferer)
						{
							CurGamer.Counter++;
							if(CurGamer.Counter == CurrentServer.WaitForAction)
							{
								CurGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du bist zu langsam für eine Auktion.");
								CurrentServer.ConList.broadcastEx(CurGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurGamer.Con.Username + " ist zu langsam für eine Auktion.");
								CurrentServer.display(CurGamer.Con.Username + " is too slow for an auction and was removed.");
								CurrentServer.CurrentGame.removeFromAuction(CurGamer.Con);
							}
						}						
						CurGamer = CurGamer.After;
					}
				}
				else
				{
					Counter++;
					if(Counter == CurrentServer.WaitForChange)
					{
						CurrentServer.CurrentGame.forceNextPlayer();
					}
					else if(Counter == CurrentServer.WaitForAction)
					{
						CurrentServer.CurrentGame.CurrentGamer.Con.sendMessage("PLAYSOUND 7");
						CurrentServer.CurrentGame.CurrentGamer.Con.sendMessage("GETFOCUS");
					}
				}
			}
		}
	}
	
	public void resetCounter()
	{
		Counter = 0;
	}
	
	private boolean allPlayersAreReady()
	{
		Connection CurCon = CurrentServer.ConList.First;
		while(CurCon != null)
		{
			if(CurCon.isPlayer && CurCon.ReadyState != 3)
			{
				return false;
			}
			CurCon = CurCon.After;
		}
		return true;
	}
}