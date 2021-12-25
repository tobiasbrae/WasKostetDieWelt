import java.util.Scanner;
import java.awt.*;
import java.io.*;

class ServerListener extends Thread
{  
	private Main CurrentMain;
	
	public ServerListener(Main CurrentMain)
	{
		this.CurrentMain = CurrentMain;
	}
	
	public void run()
	{  
		while(CurrentMain.KeepGoing)
		{  
			try
			{  
				String Message = (String) CurrentMain.StreamInput.readObject();
				if(!CurrentMain.KeepGoing) { break; }
				Scanner CurrentLine = new Scanner(Message);
				String Command = CurrentLine.next();
				
				if(Command.equals("NAMETOOLONG"))
				{
					CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[2], "Server: Name ist zu lang. (Maximal 20)");
					CurrentMain.closeConnection();
				}
				else if(Command.equals("NAMEEXISTS"))
				{
					CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[2], "Server: Spieler mit diesem Namen bereits verbunden.");
					CurrentMain.closeConnection();
				}
				else if(Command.equals("CONNECTIONACCEPTED"))
				{
					CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[0], "Verbindung erfolgreich.");
					CurrentMain.CurrentWindow.setTitle(CurrentLine.next() + " - Was kostet die Welt?");
				}
				else if(Command.equals("MESSAGE"))
				{
					CurrentMain.CurrentWindow.MList.addMessage(new Color(CurrentLine.nextInt()), CurrentLine.nextLine());
				}
				else if(Command.equals("MARKUSER"))
				{
					CurrentMain.CurrentWindow.UList.markUser(CurrentLine.nextInt(), CurrentLine.nextBoolean());
				}
				else if(Command.equals("SHOWPERCENT"))
				{
					CurrentMain.CurrentWindow.UList.showPercent(CurrentLine.nextInt(), CurrentLine.nextBoolean(), CurrentLine.nextInt());
				}
				else if(Command.equals("MARKPROPERTY"))
				{
					CurrentMain.CurrentWindow.Properties.markProperty(CurrentLine.nextInt(), CurrentLine.nextBoolean());
				}
				else if(Command.equals("PLAYSOUND"))
				{
					CurrentMain.playSound(CurrentLine.nextInt());
				}
				else if(Command.equals("GETFOCUS"))
				{
					CurrentMain.CurrentWindow.requestFocus();
				}
				else if(Command.equals("SERVERCLOSED"))
				{
					CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[2], "Server: Server wird geschlossen.");
					CurrentMain.disconnect();
				}
				else if(Command.equals("KICKED"))
				{
					CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[2], "Server: Du wurdest gekickt.");
					CurrentMain.closeConnection();
				}
				else if(Command.equals("SETACTION"))
				{
					CurrentMain.CurrentWindow.Action.setAction(CurrentLine.nextInt(), CurrentLine.nextBoolean());
				}
				else if(Command.equals("SETLOBBY"))
				{
					CurrentMain.CurrentWindow.Action.setPanel(CurrentMain.CurrentWindow.Action.P_Lobby);
					CurrentMain.CurrentWindow.Action.setChange("Mitspieler");
				}
				else if(Command.equals("SETCHANGEPLAYER"))
				{
					CurrentMain.CurrentWindow.Action.setChange(CurrentLine.next());
				}
				else if(Command.equals("SETGAME"))
				{
					CurrentMain.CurrentWindow.SetWindow(CurrentMain.CurrentWindow.MW_Game);
				}
				else if(Command.equals("DRAWRESSOURCE"))
				{
					CurrentMain.CurrentWindow.MW_Game.drawRessource(CurrentLine.nextInt(), CurrentLine.nextInt());
				}
				else if(Command.equals("SETDICES"))
				{
					CurrentMain.CurrentWindow.AnimRenderer.setDices(CurrentLine.nextInt(), CurrentLine.nextInt());
				}
				else if(Command.equals("ADDPLAYERFIGURE"))
				{
					CurrentMain.CurrentWindow.AnimRenderer.addPlayerFigure(CurrentLine.nextInt(), new Color(CurrentLine.nextInt()));
				}
				else if(Command.equals("MOVEPLAYERFIGURE"))
				{
					CurrentMain.CurrentWindow.AnimRenderer.movePlayerFigure(CurrentLine.nextInt(), CurrentLine.nextInt());
				}
				else if(Command.equals("REMOVEPLAYERFIGURE"))
				{
					CurrentMain.CurrentWindow.AnimRenderer.removePlayerFigure(CurrentLine.nextInt());
				}
				else if(Command.equals("RELOADGAME"))
				{
					CurrentMain.CurrentWindow.MW_Game.reArrange();
				}
				else if(Command.equals("SETVIEWER"))
				{
					CurrentMain.CurrentWindow.Action.setPanel(CurrentMain.CurrentWindow.Action.P_Viewer);
					CurrentMain.CurrentWindow.Properties.removeFromPanel();
					CurrentMain.CurrentWindow.Viewers.addToPanel();
				}
				else if(Command.equals("SETPLAYER"))
				{
					CurrentMain.CurrentWindow.Action.setPanel(CurrentMain.CurrentWindow.Action.P_Player);
					CurrentMain.CurrentWindow.Viewers.removeFromPanel();
					CurrentMain.CurrentWindow.Properties.addToPanel();
				}
				else if(Command.equals("ADDUSER"))
				{
					CurrentMain.CurrentWindow.UList.addUser(CurrentLine.nextInt(), CurrentLine.next(), new Color(CurrentLine.nextInt()));
				}
				else if(Command.equals("SETPLAYERCOLOR"))
				{
					CurrentMain.CurrentWindow.UList.setPlayerColor(CurrentLine.nextInt(), new Color(CurrentLine.nextInt()));
				}
				else if(Command.equals("REMOVEUSER"))
				{
					CurrentMain.CurrentWindow.UList.removeUser(CurrentLine.nextInt());
				}
				else if(Command.equals("SETREADYCHANGE"))
				{
					CurrentMain.CurrentWindow.Action.setReadyChange(CurrentLine.nextBoolean(), CurrentLine.nextLine());
				}
				else if(Command.equals("ADDPROPERTY"))
				{
					if(CurrentMain.CurrentWindow.Current == CurrentMain.CurrentWindow.MW_Trade)
					{
						CurrentMain.CurrentWindow.MW_Trade.addProperty(CurrentLine.nextLine());
					}
					else if(CurrentMain.CurrentWindow.Current == CurrentMain.CurrentWindow.MW_Auction)
					{
						CurrentMain.CurrentWindow.MW_Auction.addProperty(CurrentLine.nextLine());
					}						
				}
				else if(Command.equals("SHOWTRADE"))
				{
					CurrentMain.CurrentWindow.MW_Trade.clear();
					CurrentMain.CurrentWindow.MW_Trade.setTrade(CurrentLine.nextBoolean());
					CurrentMain.CurrentWindow.SetWindow(CurrentMain.CurrentWindow.MW_Trade);
				}
				else if(Command.equals("SHOWAUCTION"))
				{
					if(CurrentLine.nextBoolean()) { CurrentMain.CurrentWindow.MW_Auction.clearList(); }
					CurrentMain.CurrentWindow.SetWindow(CurrentMain.CurrentWindow.MW_Auction);
					CurrentMain.CurrentWindow.MW_Auction.showAuctionList(CurrentLine.nextBoolean());
				}
				else if(Command.equals("SHOWOFFER"))
				{
					CurrentMain.CurrentWindow.SetWindow(CurrentMain.CurrentWindow.MW_Offer);
				}
				else if(Command.equals("SETAUCTIONOFFER"))
				{
					CurrentMain.CurrentWindow.MW_Offer.setPrice(CurrentLine.nextBoolean(), CurrentLine.nextInt(), CurrentLine.next(), CurrentLine.nextLine());
				}
				else if(Command.equals("SETAUCTIONITEM"))
				{
					CurrentMain.CurrentWindow.MW_Auction.setProperty(CurrentLine.nextInt());
				}
				else if(Command.equals("SETAUCTIONSTARTED"))
				{
					CurrentMain.CurrentWindow.MW_Auction.setAuctionStarted();
				}
				else if(Command.equals("SETAUCTIONPRICE"))
				{
					CurrentMain.CurrentWindow.MW_Auction.setPrice(CurrentLine.nextInt(), CurrentLine.next());
				}
				else if(Command.equals("SETPROPERTY"))
				{
					CurrentMain.CurrentWindow.Properties.setProperty(CurrentLine.nextLine());
				}
				else if(Command.equals("SETPLAYERDATA"))
				{
					CurrentMain.CurrentWindow.Viewers.setViewer(CurrentLine.nextLine());
				}
				else if(Command.equals("SETMONEY"))
				{
					CurrentMain.CurrentWindow.PData.setMoney(CurrentLine.nextInt(), false);
				}
				else if(Command.equals("SETJOKER"))
				{
					CurrentMain.CurrentWindow.PData.setJoker(CurrentLine.nextInt(), false);
				}
				else if(Command.equals("SETPRICE"))
				{
					CurrentMain.CurrentWindow.MW_Trade.setPrice(CurrentLine.nextBoolean(), CurrentLine.nextInt());
				}
				else if(Command.equals("CLEARALL"))
				{
					CurrentMain.CurrentWindow.clearGame();
				}
				else
				{
					CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[2], "Server: " + Command);
				}
			}  
			catch(IOException e)
			{  
				if(CurrentMain.KeepGoing)
				{
					System.out.println(e.toString());
				}
				CurrentMain.closeConnection();
				break;
			}
			catch(ClassNotFoundException e2)
			{  
				if(CurrentMain.KeepGoing)
				{
					System.out.println(e2.toString());
				}
				CurrentMain.closeConnection();
				break;
			}
		}

		
		
	}  
}