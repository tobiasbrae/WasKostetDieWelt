import java.net.*;  
import java.io.*;  
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

public class Main
{       
    public Window CurrentWindow;
	public InterfaceListener CurrentIfListener;
	private ServerListener CurrentSvListener;
	public ObjectInputStream StreamInput; 
    private ObjectOutputStream StreamOutput; 
    private Socket CurrentSocket;  
    public String Server, Username;  
    public int Port;
    boolean Connected, KeepGoing, SoundActivated;
	public Color Colors[];
	public URL[][] URLs;
	private AudioClip Sounds[];

    public static void main(String[] args)
    {   
        Main CurrentMain = new Main(); 
    }  
    
    public Main()
    {
        Colors = new Color[3];
		Colors[0] = Color.gray;
		Colors[1] = Color.red;
		Colors[2] = Color.darkGray;
		
		URLs = new URL[3][50];
		
		//Data
		URLs[0][0] = Main.class.getResource("data/RessourcePositions.txt");
		
		//Sound
		URLs[1][0] = Main.class.getResource("sound/buttonclick.wav");
		URLs[1][1] = Main.class.getResource("sound/roll.wav");
		URLs[1][2] = Main.class.getResource("sound/message.wav");
		URLs[1][3] = Main.class.getResource("sound/step.wav");
		URLs[1][4] = Main.class.getResource("sound/countdown.wav");
		URLs[1][5] = Main.class.getResource("sound/countfinish.wav");
		URLs[1][6] = Main.class.getResource("sound/gamestarts.wav");
		URLs[1][7] = Main.class.getResource("sound/youron.wav");
		
		//Textures
		URLs[2][0] = Main.class.getResource("textures/pitch.png");
		for(int i = 1; i < 7; i++)
		{
			URLs[2][i] = Main.class.getResource("textures/dices/red" + i + ".png");
			URLs[2][i + 6] = Main.class.getResource("textures/dices/white" + i + ".png");
		}
		for(int i = 1; i < 25; i++)
		{
			URLs[2][i + 14] = Main.class.getResource("textures/ressources/" + i + ".png");
		}
		URLs[2][45] = Main.class.getResource("textures/playermark.png");
		
		
		Sounds = new AudioClip[8];
		for(int i = 0; i < 8; i++)
		{
			Sounds[i] = Applet.newAudioClip(URLs[1][i]);;
		}
		
		CurrentIfListener = new InterfaceListener(this);
		
		CurrentWindow = new Window(this);
    }
    
    public void playSound(int ID)
	{
		if(SoundActivated)
		{
			Sounds[ID].play();
		}
	}
	
	public String lengthString(boolean Before, int Input, int Length) { return lengthString(Before, Input + "", Length); }	
	
	public String lengthString(boolean Before, String Input, int Length)
	{
		if(Input.length() > Length)
		{
			return Input.substring(0, Length);
		}
		else
		{
			while(Input.length() < Length)
			{
				if(Before)
				{
					Input = " " + Input;
				}
				else
				{
					Input = Input + " ";
				}
			}
			return Input;
		}
	}
	
	public String getPointedInt(int Input)
	{
		String Number = Input + "";
		String Output = "";
		boolean startPoint = false;
		int Upper = 0;
		if(Number.length() % 3 == 2)
		{
			Output = Number.substring(0, 2);
			Upper = 2;
			startPoint = true;
		}
		else if(Number.length() % 3 == 1)
		{
			Output = Number.substring(0, 1);
			Upper = 1;
			startPoint = true;
		}
		for(int i = 0; i < Number.length() / 3; i++)
		{
			if(startPoint)
			{
				Output = Output + ".";
			}
			else
			{
				startPoint = true;
			}
			Output = Output + Number.substring(Upper + i * 3, Upper + i * 3 + 3);
		}
		return Output;
	}
    
    public boolean start()
    {   
        try
        {  
            CurrentSocket = new Socket(Server, Port);  
			
			StreamInput  = new ObjectInputStream(CurrentSocket.getInputStream());  
            StreamOutput = new ObjectOutputStream(CurrentSocket.getOutputStream()); 

			StreamOutput.writeObject(Username);
			
			CurrentSvListener = new ServerListener(this);
			KeepGoing = true;
			CurrentSvListener.start();
        
			return true;
        }  
        catch(ConnectException e)
		{
			CurrentWindow.MList.addMessage(Colors[0], "Verbindung zum Server fehlgeschlagen.");
			return false;
		}
		catch(Exception e)
        {  
            System.out.println(e.toString());
			closeConnection();
            return false;  
        }
    }  
 
    void sendMessage(String Message)
    {  
        try
        {  
            StreamOutput.writeObject(Message);  
        }  
        catch(IOException e)
        {  
            CurrentWindow.MList.addMessage(Colors[1], e.toString());
			closeConnection();
        }  
    }  

    public void disconnect()
    {  
		sendMessage("LOGOUT");
		closeConnection();
		CurrentWindow.MList.addMessage(Colors[0], "Erfolgreich ausgeloggt.");
	}
	
	public void closeConnection()
	{
		KeepGoing = false;
		//CurrentSvListener.interrupt();
		CurrentSvListener = null;
        
        try
        {   
            if(StreamInput != null)
			{
				StreamInput.close();  
			}
        }  
        catch(Exception e)
        {
            System.out.println(e.toString());
        }

        try
        {  
            if(StreamOutput != null)
            {
                StreamOutput.close();  
            }
        }  
        catch(Exception e)
        {
            System.out.println(e.toString());
        }

        try
        {  
            if(CurrentSocket != null)
            {
                CurrentSocket.close();
            }
        }  
        catch(Exception e)
        {
           System.out.println(e.toString());
        }
		
		CurrentWindow.clearAll();
		CurrentWindow.requestFocus();
    }  
}