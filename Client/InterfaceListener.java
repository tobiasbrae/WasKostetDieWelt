import java.awt.event.*;
import java.util.Scanner;

public class InterfaceListener implements ActionListener
{
	private Main CurrentMain;
	
	public InterfaceListener(Main CurrentMain)
	{
		this.CurrentMain = CurrentMain;
	}
	
	public void actionPerformed(ActionEvent Event)
	{
		Scanner CurrentLine = new Scanner(Event.getActionCommand());
		
		if(CurrentLine.hasNext())
		{
			String Command = CurrentLine.next();
			
			if(Command.equals("CONNECT"))
			{
				if(CurrentMain.CurrentWindow.MW_Logon.isDataCorrect(CurrentMain, CurrentMain.CurrentWindow.MList))
				{
					CurrentMain.Username = CurrentMain.CurrentWindow.MW_Logon.getName();
					CurrentMain.Server = CurrentMain.CurrentWindow.MW_Logon.getAddress();  
					CurrentMain.Port = CurrentMain.CurrentWindow.MW_Logon.getPort();  
        
					CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[0], "Baue Verbindung zum Server auf...");
            
					if(!CurrentMain.start())  
					{
					
					}
				}
			}
			else if(Command.equals("QUIT"))
			{
				CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[0], "Client wird geschlossen...");
				if(CurrentMain.KeepGoing)
				{
					CurrentMain.disconnect();
				}
				System.exit(0);
			}
			else if(Command.equals("LOGOUT"))
			{
				CurrentMain.disconnect();
			}
			else if(Command.equals("CHANGESOUND"))
			{
				CurrentMain.CurrentWindow.PData.changeSound();
			}
			else if(Command.equals("GAME"))
			{
				CurrentMain.sendMessage("GAME " + CurrentLine.nextLine());
			}
			else if(Command.equals("SERVER"))
			{
				CurrentMain.sendMessage(CurrentLine.next());
			}
			else if(Command.equals("CHANGESIDE"))
			{
				CurrentMain.CurrentWindow.MW_Trade.ChangeSide(CurrentMain, CurrentLine.nextInt());
			}
			else if(Command.equals("USERINPUT"))
			{
				String Input = CurrentMain.CurrentWindow.UserInput.getText();
				if(Input.charAt(0) == '/')
				{
					Scanner Line = new Scanner(Input);
					Command = Line.next();
					if(Command.equalsIgnoreCase("/QUIT"))
					{
						if(Line.hasNext())
						{
							CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[1], "Nutzung: \"/quit\"");
						}
						else
						{
							CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[0], "Client wird geschlossen...");
							if(CurrentMain.KeepGoing)
							{
								CurrentMain.disconnect();
							}
							System.exit(0);
						}
					}
					else if(Command.equalsIgnoreCase("/PN"))
					{
						if(CurrentMain.KeepGoing)
						{
							if(!Line.hasNext())
							{
								CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[1], "Nutzung: \"/pn <Nutzer> <Nachricht>\"");
							}
							else
							{
								String Name = Line.next();
								if(!Line.hasNext())
								{
									CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[1], "Nutzung: \"/pn <Nutzer> <Nachricht>\"");
								}
								else
								{
									String Message = Line.nextLine();
									CurrentMain.sendMessage("PRIVATEMESSAGE " + Name + Message);
								}
							}
						}
						else
						{
							CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[0], "Du bist mit keinem Server verbunden.");
						}
					}
					else
					{
						CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[0], "Unbekannter Befehl.");
					}
				}
				else if(!CurrentMain.KeepGoing)
				{
					CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[0], "Unbekannter Befehl.");
				}
				else if(!Input.equals(""))
				{
					CurrentMain.sendMessage("CHATMESSAGE " + Input);
				}
				CurrentMain.CurrentWindow.UserInput.setText("");
			}
			else
			{
				CurrentMain.CurrentWindow.MList.addMessage(CurrentMain.Colors[0], "Interface: " + Command);
			}
		}		
	}
}