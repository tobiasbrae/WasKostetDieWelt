import java.util.Scanner;
	
public class UserInput extends Thread
{
    Server CurrentServer;
	   
	public UserInput(Server CurrentServer)
	{
		this.CurrentServer = CurrentServer;
	}
	   
	public void run()
    {  
		Scanner Data = new Scanner(System.in);
		Scanner CurrentLine;
		String Command;

		while(CurrentServer.Listen)
		{    
			CurrentLine = new Scanner(Data.nextLine());   
			if(!CurrentLine.hasNext())
			{
				CurrentServer.display("Unknown Command");
				continue;
			}
			Command = CurrentLine.next();
               
			if(Command.equalsIgnoreCase("HELP"))
			{  
				CurrentServer.display("Available Commands:");
				CurrentServer.display("exit - shuts the server down");
				CurrentServer.display("whoisin - shows all connected clients");
				CurrentServer.display("broadcast <Message> - sends a message to all clients.");
				CurrentServer.display("message <ID/Username> <Message> - sends a message to a client.");
				CurrentServer.display("kick <ID/Username> - kicks a client.");
				CurrentServer.display("startgame - starts the game(needs at least 2 connected players)");
				CurrentServer.display("loadsettings - reloads the settings");
			}
			else if(Command.equalsIgnoreCase("EXIT"))
			{  
				CurrentServer.shutDown();
			}  
			else if(Command.equalsIgnoreCase("WHOISIN"))
			{      
				CurrentServer.ConList.printClients();                  
			}
			else if(Command.equalsIgnoreCase("BROADCAST"))
			{  
				if(CurrentLine.hasNext())
				{
					if(CurrentServer.ConList.First == null)
					{
						CurrentServer.display("Error. No clients are connected.");
					}
					else
					{
						String Message = CurrentLine.next();
						if(CurrentLine.hasNext()) { Message = Message + CurrentLine.nextLine(); }
						CurrentServer.display("Server: " + Message);
						CurrentServer.ConList.broadcast("MESSAGE " + CurrentServer.Colors[0] + " Server: " + Message);
					}
				}
				else
				{
					CurrentServer.display("Error. Usage: \"broadcast <message>\"");
				}
			}
			else if(Command.equalsIgnoreCase("MESSAGE"))
			{      
				Connection Receiver = null;
				String CurrentUsername = "";
				String Type = "";
				if(CurrentLine.hasNextInt())
				{
					Type = "ID";
					int UserID = CurrentLine.nextInt();
					CurrentUsername = UserID + "";
					Receiver = CurrentServer.ConList.getConnection(UserID);
				}
				else if(CurrentLine.hasNext())
				{				
					Type = "username";
					CurrentUsername = CurrentLine.next();
					Receiver = CurrentServer.ConList.getConnection(CurrentUsername);
				}
				else
				{
					CurrentServer.display("Error. Usage: \"message <ID/Username> <Message>\"");
					continue;
				}
				
				if(CurrentLine.hasNext())
				{
					if(Receiver == null)
					{
						CurrentServer.display("No client with given " +  Type + " <" + CurrentUsername + "> is logged in.");
					}
					else
					{
						String CurMessage = CurrentLine.nextLine();
						Receiver.sendMessage("MESSAGE " + CurrentServer.Colors[0] + " Server:" + CurMessage);
						CurrentServer.display("PM to " + Receiver.Username + ":" + CurMessage);
					}
				}
				else
				{
					CurrentServer.display("Error. Usage: \"message <ID/Username> <Message>\"");
				}
			}
			else if(Command.equalsIgnoreCase("STARTGAME"))
			{
				if(CurrentServer.ConList.countPlayers() < CurrentServer.MinimumPlayers)
				{
					CurrentServer.display("Not enough players connected.");
				}
				else
				{
					CurrentServer.startNewGame();
				}
			}
			else if(Command.equalsIgnoreCase("LOADSETTINGS"))
			{
				CurrentServer.loadSettings();
			}
			else if(Command.equalsIgnoreCase("KICK"))
			{
				Connection CurCon = null;
				if(CurrentLine.hasNextInt())
				{
					int ID = CurrentLine.nextInt();
					CurCon = CurrentServer.ConList.getConnection(ID);
					if(CurCon == null)
					{
						CurrentServer.display("No client with given ID <" + ID + "> connected.");
					}
				}
				else if(CurrentLine.hasNext())
				{
					String Name = CurrentLine.next();
					CurCon = CurrentServer.ConList.getConnection(Name);
					if(CurCon == null)
					{
						CurrentServer.display("No client with given username <" + Name + "> connected.");
					}
				}
				else
				{
					CurrentServer.display("Error. Usage: \"kick <ID/Username>\"");
				}
				if(CurCon != null)
				{
					CurrentServer.display("You successfully kicked " + CurCon.Username + ".");
					CurCon.sendMessage("KICKED");
					CurrentServer.removePlayer(CurCon);
					CurrentServer.ConList.broadcast("MESSAGE " + CurrentServer.Colors[0] + " Server: " + CurCon.Username + " gekickt.");
				}
			}
			else
			{
				CurrentServer.display("Unknown Command");  
			}  
		}
	}
}