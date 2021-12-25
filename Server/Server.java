import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server extends Thread
{ 
    public ConnectionList ConList;
	public Game CurrentGame;
    public SimpleDateFormat Sdf;  
    public int Port, StartMoney, StartJoker, BonusMoney, StartCountdown, MinimumPlayers, AuctionMoney, AuctionMinimum, AuctionLimit, AuctionMaximum;
	public int WaitForAction, WaitForChange;
	public double AuctionChance;
    public boolean Listen, ShowTime;
	public UserInput Input;
	public ServerSocket CurrentSocket;
	public int Colors[];
	public URL URLs[];
	private int UniqueID;
	public GameStarter AutoStart;

    public static void main(String[] args)
    {    
        Server server = new Server();
    }
    
    public Server()
    {    
		URLs = new URL[6];
		
		URLs[0] = Server.class.getResource("data/Settings.conf");
		URLs[1] = Server.class.getResource("Settings.conf");
		URLs[2] = Server.class.getResource("data/PlayerColors.txt");
		URLs[3] = Server.class.getResource("data/Fields.txt");
		URLs[4] = Server.class.getResource("data/Properties.txt");
		URLs[5] = Server.class.getResource("data/Ressources.txt");
		
		System.out.println("For help type \"help\".");

		checkConfigFile();
		
		loadSettings();
		
		if(ShowTime) { Sdf = new SimpleDateFormat("HH:mm:ss"); }
        
		Input = new UserInput(this);
		Input.start();
		
		Listen = true;
		start();
		
		AutoStart = new GameStarter(this);
		AutoStart.start();
    }
	
	private void checkConfigFile()
	{
		try
		{
			if(URLs[1] == null)
			{
				System.out.print("Creating new Settings.conf...");
				
				InputStream SourceInput = URLs[0].openStream();
				
				File Target = new File("Settings.conf");
				Target.createNewFile();
				FileOutputStream TargetOutput = new FileOutputStream(Target);
				
				while(true)
				{
					int CurrentChar = SourceInput.read();
					if(CurrentChar == -1)
					{
						break;
					}
					else
					{
						TargetOutput.write(CurrentChar);
					}
				}
				SourceInput.close();
				TargetOutput.close();
				URLs[1] = Server.class.getResource("Settings.conf");
				System.out.println("done.");
			}
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	public void loadSettings()
	{
		System.out.print("Loading settings...");
		ConfReader Settings = new ConfReader(URLs[1]);
		Port = Settings.getInt("Port");
		ShowTime = Settings.getBoolean("ShowTime");
		StartMoney = Settings.getInt("StartMoney");
		StartJoker = Settings.getInt("StartJoker");
		BonusMoney = Settings.getInt("BonusMoney");
		AuctionChance = (double) Settings.getInt("AuctionChance");
		AuctionChance /= 100.0;
		AuctionMoney = Settings.getInt("AuctionMoney");
		AuctionMinimum = Settings.getInt("AuctionMinimum");
		AuctionLimit = Settings.getInt("AuctionLimit");
		AuctionMaximum = Settings.getInt("AuctionMaximum");
		WaitForAction = Settings.getInt("WaitForAction");
		WaitForChange = Settings.getInt("WaitForChange");
		StartCountdown = Settings.getInt("StartCountdown");
		MinimumPlayers = Settings.getInt("MinimumPlayers");
		if(MinimumPlayers < 2) { MinimumPlayers = 2; }
		
		Colors = new int[8];
		Colors[0] = Settings.getInt("ServerMessage");
		Colors[1] = Settings.getInt("ChatMessage");
		Colors[2] = Settings.getInt("PrivateMessage");
		Colors[3] = Settings.getInt("GameMessage");
		Colors[4] = Settings.getInt("PersonalMessage");
		Colors[5] = Settings.getInt("Viewer");
		Colors[6] = Settings.getInt("PlayerNotReady");
		Colors[7] = Settings.getInt("PlayerReady");
		
		System.out.println("done.");
	}
    
    public void run()
    {
        try  
        {  
            ConList = new ConnectionList(this);
            CurrentSocket = new ServerSocket(Port);
			display("Server waiting for clients on port " + Port + ".");
			display("IP-Address: " + InetAddress.getLocalHost()); 
            
            while(Listen)   
            {   
				Socket ClientSocket = CurrentSocket.accept();
				if(!Listen) { break; }

                Connection Con = new Connection(this, ClientSocket, ++UniqueID);  
                if(Con.Username.length() > 20)
				{
					Con.sendMessage("NAMETOOLONG");
                    Con.close();
                    display(Con.Username + " tried to connect. Name was too long, so no connection for him.");
				}				
				else if(ConList.getConnection(Con.Username) != null)
                {
					Con.sendMessage("NAMEEXISTS");
                    Con.close();
                    display(Con.Username + " tried to connect. Name already exists, so no connection for him.");
                }
                else
                {
                    Con.sendMessage("CONNECTIONACCEPTED " + Con.Username);
					if(CurrentGame != null)
					{
						Con.sendMessage("SETGAME");
						Con.sendMessage("SETVIEWER");
						Con.sendMessage("MESSAGE " + Colors[1] + " Das Spiel läuft bereits.");
						CurrentGame.CurrentFields.sendRessources(Con);
						Con.sendMessage("RELOADGAME");
						
						Gamer CurGamer = CurrentGame.First;
						while(CurGamer != null)
						{
							Con.sendMessage("ADDPLAYERFIGURE " + CurGamer.Con.ID + " " + CurGamer.Con.Col);
							Con.sendMessage("MOVEPLAYERFIGURE " + CurGamer.Con.ID + " " + CurGamer.CurrentField.FieldID);
							Con.sendMessage("SETPLAYERDATA 0 " + CurGamer.Con.ID + " " + CurGamer.Con.Username + " " + CurGamer.Properties.Counter + " " + CurGamer.Money + " " + CurGamer.Con.Col);
							CurGamer = CurGamer.After;
						}
						
						if(CurrentGame.AuctionStarted)
						{
							Con.sendMessage("SHOWOFFER");
							CurrentGame.sendAuctionPrice();
							CurGamer = CurrentGame.First;
							while(CurGamer != null)
							{
								if(CurGamer.isInAuction)
								{
									Con.sendMessage("SHOWPERCENT " + CurGamer.Con.ID + " True " + CurGamer.Properties.Properties[CurrentGame.AuctionItem.Ressource][0]);
								}
								CurGamer = CurGamer.After;
							}
						}
					}
					else
					{
						Con.sendMessage("SETGAME");
						Con.sendMessage("SETLOBBY");
						Con.sendMessage("MESSAGE " + Colors[1] + " Du befindest dich in der Lobby.");
					}
                    ConList.broadcast("ADDUSER " + Con.ID + " " + Con.Username + " " + Con.Col);
					ConList.broadcast("MESSAGE " + Colors[0] + " Server: " + Con.Username + " hat sich verbunden.");
					ConList.addConnection(Con);
					if(CurrentGame != null && CurrentGame.GameRuns)
					{
						Con.sendMessage("MARKUSER " + CurrentGame.CurrentGamer.Con.ID + " True");
					}
                    display(Con.Username + " just connected.");
                }  
            }  

            /*try
            {  
                CurrentSocket.close();  
                ConList.broadcast("SERVERCLOSED");
            }  
            catch(Exception e)
            {  
                display("Exception closing the server and clients: " + e);  
            } */ 
        }  
        catch (IOException e)
        {  
            display(e.toString());  
        }  

    }
	
	public synchronized void accessReadMessage(Connection Con, String Message)
	{
		Con.readMessage(Message);
	}

	public void startNewGame()
	{
		if(CurrentGame == null)
		{
			display("Starting new game...");
			ConList.broadcast("PLAYSOUND 5");
			CurrentGame = new Game(this);
			CurrentGame.startGame();
		}
		else
		{
			display("Error. A game is already running.");
		}
	}
	
	public void removePlayer(Connection Con)
	{
		if(CurrentGame != null)
		{
			CurrentGame.forceRemovePlayer(Con);
		}
		ConList.removeConnection(Con);
	}

    public void shutDown()
    {  
        display("Server shutdown...");
		
		ConList.broadcast("SERVERCLOSED");
		
		while(ConList.First != null) { } //Waiting for all Clients to quit
		
		Listen = false;
		Input.interrupt();		
		this.interrupt();
        
        try
        {  
            new Socket("localhost", Port);  
        }  
        catch(Exception e)
        {  
            display(e + "");
        }
		display("...done.");
		System.exit(0);
    }  

    public String lengthString(String Input, int Length)
	{
		if(Input.length() > Length)
		{
			return Input.substring(0, Length);
		}
		else
		{
			while(Input.length() < Length)
			{
				Input = " " + Input;
			}
			return Input;
		}
	}
	
	public void display(String Message)
    {  
        if(ShowTime) { Message = Sdf.format(new Date()) + " - " + Message; }  
        System.out.println(Message);  
    }
	
	public void display_(String Message)
    {  
        if(ShowTime) { Message = Sdf.format(new Date()) + " - " + Message; }  
        System.out.print(Message);  
    }
	
	public void _display(String Message)
    {   
        System.out.println(Message);  
    }  
}