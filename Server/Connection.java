import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

class Connection extends Thread
{  
	Server CurrentServer;
	Socket ClientSocket; 
	Connection Before, After;	
	private ObjectInputStream StreamInput;  
	private ObjectOutputStream StreamOutput;     
	String Username, Date;   
	boolean KeepGoing, isPlayer, isReady; 
	public int ID, ReadyState, Col;
        
	Connection(Server CurrentServer, Socket ClientSocket, int ID)
	{     
		this.CurrentServer = CurrentServer;
		this.ClientSocket = ClientSocket;
		this.ID = ID;
		this.Col = CurrentServer.Colors[5];

		try 
		{  
			StreamOutput = new ObjectOutputStream(ClientSocket.getOutputStream());  
			StreamInput  = new ObjectInputStream(ClientSocket.getInputStream());    
                
			Username = (String) StreamInput.readObject();
			Date = new Date().toString(); 
		}  
		catch (IOException e)
		{  
			CurrentServer.display(ID + " - " + e);   
		}  
		catch (ClassNotFoundException e)
		{  
			CurrentServer.display(ID + " - " + e);
		}  
	}

	public void setColor(int Col)
	{
		this.Col = Col;
		CurrentServer.ConList.broadcast("SETPLAYERCOLOR " + ID + " " + Col); 
	}
 
	public void run()
	{  
		KeepGoing = true;
            
		while(KeepGoing)
		{    
			try
			{  
				CurrentServer.accessReadMessage(this, (String) StreamInput.readObject());
			}  
			catch (IOException e)
			{  
				CurrentServer.display(ID + " - " + e);
				CurrentServer.removePlayer(this);
			}  
			catch(ClassNotFoundException e)
			{  
				CurrentServer.display(ID + " - " + e);
				CurrentServer.removePlayer(this);  
			}  
		}    
	}

	public void readMessage(String Message)
	{
		Scanner CurrentLine = new Scanner(Message);
			
		if(CurrentLine.hasNext())
		{
			String Command = CurrentLine.next();
				
			if(Command.equals("LOGOUT"))
			{
				CurrentServer.display(Username + " disconnected with a LOGOUT message.");  
				CurrentServer.removePlayer(this); 
				CurrentServer.ConList.broadcast("MESSAGE " + CurrentServer.Colors[0] + " Server: " + Username + " hat sich ausgeloggt.");
			}
			else if(Command.equals("CHANGEPLAYER"))
			{  
				if(CurrentServer.CurrentGame == null)
				{
					isPlayer = !isPlayer;
					
					if(isPlayer)
					{
						sendMessage("MESSAGE " + CurrentServer.Colors[1] + " Du bist nun Mitspieler.");
						sendMessage("SETCHANGEPLAYER Zuschauer");
						setReadyState(2);
						CurrentServer.display(Username + " is now player.");
					}
					else
					{
						sendMessage("MESSAGE " + CurrentServer.Colors[1] + " Du bist nun Zuschauer.");
						sendMessage("SETCHANGEPLAYER Mitspieler");
						setReadyState(1);
						CurrentServer.display(Username + " is now viewer.");
					}
				}
			}
			else if(Command.equals("CHANGEREADY"))
			{  
				if(CurrentServer.CurrentGame == null && isPlayer)
				{					
					if(ReadyState ==  2)
					{
						setReadyState(3);
						CurrentServer.display(Username + " is now ready.");
					}
					else
					{
						setReadyState(2);
						CurrentServer.display(Username + " is not longer ready.");
					}
				}
			}
			else if(Command.equals("CHATMESSAGE"))
			{  
				String CurMessage = CurrentLine.nextLine();
				CurrentServer.display(Username + ":" + CurMessage);
				CurrentServer.ConList.broadcast("MESSAGE " + CurrentServer.Colors[1] + " " + Username + ":" + CurMessage);
			}
			else if(Command.equals("PRIVATEMESSAGE"))
			{  
				Connection Receiver;
				String CurrentUsername;
				String Type;
				if(CurrentLine.hasNextInt())
				{
					Type = "ID";
					int UserID = CurrentLine.nextInt();
					CurrentUsername = UserID + "";
					Receiver = CurrentServer.ConList.getConnection(UserID);
				}
				else
				{				
					Type = "Namen";
					CurrentUsername = CurrentLine.next();
					Receiver = CurrentServer.ConList.getConnection(CurrentUsername);
				}
				if(Receiver == null)
				{
					sendMessage("MESSAGE " + CurrentServer.Colors[0] + " Kein Spieler mit " +  Type + " " + CurrentUsername + " eingeloggt.");
				}
				else
				{
					if(CurrentLine.hasNext())
					{
						String CurMessage = CurrentLine.nextLine();
						sendMessage("MESSAGE " + CurrentServer.Colors[2] + " PN an " + Receiver.Username + ":" + CurMessage);
						Receiver.sendMessage("MESSAGE " + CurrentServer.Colors[2] + " PN von " + Username + ":" + CurMessage);
						CurrentServer.display("PM from " + Username + " to " + Receiver.Username + ":" + CurMessage);
					}
					else
					{
						sendMessage("MESSAGE " + CurrentServer.Colors[0] + " Deine Nachricht besitzt keinen Inhalt.");
					}
				}
			}
			else if(Command.equals("GAME") && CurrentServer.CurrentGame != null)
			{
				Command = CurrentLine.next();
					
				if(Command.equals("ROLL"))
				{
					CurrentServer.CurrentGame.roll(this);
					if(CurrentServer.CurrentGame.CurrentGamer != null && CurrentServer.CurrentGame.CurrentGamer == CurrentServer.CurrentGame.getGamer(this))
					{
						CurrentServer.AutoStart.resetCounter();
					}
				}
				else if(Command.equals("GIVEUP"))
				{
					CurrentServer.CurrentGame.requestGiveUp(this);
				}
				else if(Command.equals("HIGHEROFFER"))
				{
					CurrentServer.CurrentGame.higherOffer(this);
				}
				else if(Command.equals("QUITAUCTION"))
				{
					CurrentServer.CurrentGame.quitAuction(this);
				}
				else if(CurrentServer.CurrentGame.CurrentGamer != null && CurrentServer.CurrentGame.CurrentGamer == CurrentServer.CurrentGame.getGamer(this))
				{
					CurrentServer.AutoStart.resetCounter();
					if(Command.equals("NEXTPLAYER"))
					{
						CurrentServer.CurrentGame.requestNextPlayer(this);
					}
					else if(Command.equals("STARTBUYING"))
					{
						CurrentServer.CurrentGame.startAction(this, 1);
					}
					else if(Command.equals("STARTSELLING"))
					{
						CurrentServer.CurrentGame.startAction(this, 2);
					}
					else if(Command.equals("STARTAUCTION"))
					{
						CurrentServer.CurrentGame.startAction(this, 3);
					}
					else if(Command.equals("GIVEJOKER"))
					{
						CurrentServer.CurrentGame.giveJoker(this);
					}
					else if(Command.equals("ADDPROPERTY"))
					{
						CurrentServer.CurrentGame.addToCart(CurrentLine.nextInt());
					}
					else if(Command.equals("REMOVEPROPERTY"))
					{
						CurrentServer.CurrentGame.removeFromCart(CurrentLine.nextInt());
					}
					else if(Command.equals("ACCEPTACTION"))
					{
						int ID = 0;
						if(CurrentLine.hasNextInt()) { ID = CurrentLine.nextInt(); }
						CurrentServer.CurrentGame.acceptAction(this, ID);
					}
					else if(Command.equals("CANCELACTION"))
					{
						CurrentServer.CurrentGame.cancelAction(this);
					}
					else
					{
						CurrentServer.display(ID + " - " + Command);
					}
				}
				else
				{
					CurrentServer.display(ID + " - " + Command);
				}
			}
			else
			{
				CurrentServer.display(ID + " - " + Command);
			}
		}
	}
 
	public void sendMessage(String message)
	{  
		if(!ClientSocket.isConnected())
		{  
			CurrentServer.removePlayer(this);   
		}  

		try
		{  
			StreamOutput.writeObject(message);  
		}  
		catch(IOException e)
		{  
			CurrentServer.display(ID + " - " + e);
		}
	}  
	
	public void setReadyState(int State)
	{
		ReadyState = State;
		if(State == 1)
		{
			sendMessage("SETREADYCHANGE False Bereit");
			setColor(CurrentServer.Colors[5]);
		}
		else if(State == 2)
		{
			sendMessage("SETREADYCHANGE True Bereit");
			setColor(CurrentServer.Colors[6]);
		}
		else if(State == 3)
		{
			sendMessage("SETREADYCHANGE True Nicht Bereit");
			setColor(CurrentServer.Colors[7]);
		}
	}
	
	public void close()
	{  
		KeepGoing = false;
			
		try
		{  
			if(StreamOutput != null) StreamOutput.close();  
		}  
		catch(Exception e)
		{
            CurrentServer.display(ID + " - " + e);
		}  

		try
		{  
			if(StreamInput != null) StreamInput.close();  
		}  
		catch(Exception e)
		{
            CurrentServer.display(ID + " - " + e);
		}  

		try
		{  
			if(ClientSocket != null) ClientSocket.close();  
		}  
		catch (Exception e)
		{
            CurrentServer.display(ID + " - " + e);
		}
	}  
}