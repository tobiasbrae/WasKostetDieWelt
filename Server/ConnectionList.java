public class ConnectionList
{
    Server CurrentServer;
	Connection First, Last;
        
    ConnectionList(Server CurrentServer)
    {
        this.CurrentServer = CurrentServer;
    }
        
    public void addConnection(Connection Con)
    {
        if(First == null)
        {
            First = Con;
            Last = Con;
        }
        else
        {
            Last.After = Con;
            Con.Before = Last;
            Last = Con;
        }
		sendUsers();
        Con.start();
    }
	
	public void sendUsers()
	{
		if(First == Last)
		{
			First.sendMessage("ADDUSER " + First.ID + " " + First.Username + " " + First.Col);
		}
		else
		{
			Connection CurrentCon = First;
			while(CurrentCon != null)
			{
				Last.sendMessage("ADDUSER " + CurrentCon.ID + " " + CurrentCon.Username + " " + CurrentCon.Col);
				CurrentCon = CurrentCon.After;
			}
		}
	}
        
    public void removeConnection(Connection Con)
    {
        CurrentServer.display("User " + Con.Username + " was removed.");
		if(First == Last && First == Con)
		{
			First = null;
			Last = null;
			CurrentServer.display("No clients are connected now.");
		}			
		else if(First == Con)
        {
            Con.After.Before = null;
            First = Con.After;
        }
        else if(Last == Con)
        {
            Con.Before.After = null;
            Last = Con.Before;
        }
        else
        {
            Con.Before.After = Con.After;
            Con.After.Before = Con.Before;
        }
		Con.close();
		
		broadcast("REMOVEUSER " + Con.ID);
    }
        
    public Connection getConnection(int UserID)
    {
        Connection CurrentCon = First;
		while(CurrentCon != null)
		{
			if(CurrentCon.ID == UserID)
			{
				return CurrentCon;
			}
			CurrentCon = CurrentCon.After;
		}
		return null;
    }
	
	public Connection getConnection(String Username)
    {
        Connection CurrentCon = First;
		while(CurrentCon != null)
		{
			if(CurrentCon.Username.equalsIgnoreCase(Username))
			{
				return CurrentCon;
			}
			CurrentCon = CurrentCon.After;
		}
		return null;
    }
        
    public synchronized void broadcastEx(Connection Con, String Message)
    {  
        if(First == null)
        {
            //CurrentServer.display("Error. No clients are connected.");
        }
        else if(First == Last)
        {
            if(First != Con)
			{
				First.sendMessage(Message);
			}
        }
		else
		{
			Connection CurrentCon = First;
			while(CurrentCon != null)
			{
				if(CurrentCon != Con)
				{
					CurrentCon.sendMessage(Message);
				}	
				CurrentCon = CurrentCon.After;
			}
		}
    }
	
	public synchronized void broadcastexPlayers(String Message)
    {  
        if(First == null)
        {
            //CurrentServer.display("Error. No clients are connected.");
        }
        else if(First == Last)
        {
            if(!First.isPlayer)
			{
				First.sendMessage(Message);
			}
        }
		else
		{
			Connection CurrentCon = First;
			while(CurrentCon != null)
			{
				if(!CurrentCon.isPlayer)
				{
					CurrentCon.sendMessage(Message);
				}
				CurrentCon = CurrentCon.After;
			}
		}
    }

	public synchronized void broadcast(String Message)
    {  
        if(First == null)
        {
            //CurrentServer.display("Error. No clients are connected.");
        }
        else if(First == Last)
        {
            First.sendMessage(Message);
        }
		else
		{
			Connection CurrentCon = First;
			while(CurrentCon != null)
			{
				CurrentCon.sendMessage(Message);
				CurrentCon = CurrentCon.After;
			}
		}
    }
		
	public synchronized void sendStart()
    {  
        Connection CurrentCon = First;
		while(CurrentCon != null)
		{
			CurrentServer.CurrentGame.CurrentFields.sendRessources(CurrentCon);
			CurrentCon.sendMessage("RELOADGAME");
			CurrentCon.sendMessage("MESSAGE " + CurrentServer.Colors[3] + " Das Spiel wurde gestartet.");
			CurrentCon.sendMessage("SETGAME");
			if(CurrentCon.isPlayer)
			{
				CurrentCon.sendMessage("SETACTION 0 True");
				for(int i = 1; i < 5; i++) { CurrentCon.sendMessage("SETACTION " + i + " False"); }
				CurrentCon.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du musst würfeln.");
				CurrentCon.sendMessage("SETPLAYER");
			}
			else
			{
				CurrentCon.sendMessage("SETVIEWER");
			}
			CurrentCon = CurrentCon.After;
		}
    }
        
    public void printClients()
	{		
		if(First == null)
		{
			CurrentServer.display("No Clients connected.");
		}
		else
		{
			CurrentServer.display("List of the users connected:");
			if(First == Last)
			{
				CurrentServer.display(First.ID + " - " + First.Username + " connected since " + First.Date);
			}
			else
			{
				Connection CurrentCon = First;
				while(CurrentCon != null)
				{
					CurrentServer.display(CurrentCon.ID + " - " + CurrentCon.Username + " connected since " + CurrentCon.Date);
					CurrentCon = CurrentCon.After;
				}
			}
		}
	}
		
	public int countPlayers()
	{
		int Players = 0;
		if(First == null)
		{
			Players = 0;
		}
		else if(First == Last)
		{
			Players = 1;
		}
		else
		{
			Connection CurrentCon = First;
			while(CurrentCon != null)
			{
				if(CurrentCon.isPlayer)
				{
					Players++;
				}
				CurrentCon = CurrentCon.After;
			}
		}
		return Players;
	}
}