import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ActionField extends RenderObject
{
	private JPanel P_Main, P_Current;
	public JPanel P_Logon, P_Lobby, P_Player, P_Viewer;
	private JButton Logon_Connect, Logon_Quit, Lobby_ChangePlayer, Lobby_ChangeReady, Lobby_Logout, Lobby_Quit;
	private JButton Player_GiveUp, Player_Logout, Player_Quit;
	private JButton Viewer_Logout, Viewer_Quit;
	public JButton[] GameButtons;
	
	public ActionField(JPanel Main, ActionListener Action)
	{
		super(Main);
		P_Main = Main;
		createPanels(Action);
		P_Main.add(P_Logon);
		P_Current = P_Logon;
	}
	
	public void setPanel(JPanel panel)
	{
		P_Main.remove(P_Current);
		P_Current = panel;
		P_Main.add(P_Current);
		NeedRepaint = true;
	}
	
	private void createPanels(ActionListener Action)
	{
		P_Logon = new JPanel();
		P_Logon.setLayout(new GridLayout(0, 2));
			Logon_Connect = new JButton("Verbinden");
				Logon_Connect.setActionCommand("CONNECT");
				Logon_Connect.addActionListener(Action);
			Logon_Quit = new JButton("Beenden");
				Logon_Quit.setActionCommand("QUIT");
				Logon_Quit.addActionListener(Action);
		P_Logon.add(Logon_Connect);
		P_Logon.add(Logon_Quit);
		
		P_Lobby = new JPanel();
		P_Lobby.setLayout(new GridLayout(0, 2));
			Lobby_ChangePlayer = new JButton("Mitspieler");
				Lobby_ChangePlayer.setActionCommand("SERVER CHANGEPLAYER");
				Lobby_ChangePlayer.addActionListener(Action);
			Lobby_ChangeReady = new JButton("Bereit");
				Lobby_ChangeReady.setEnabled(false);
				Lobby_ChangeReady.setActionCommand("SERVER CHANGEREADY");
				Lobby_ChangeReady.addActionListener(Action);
			Lobby_Logout = new JButton("Ausloggen");
				Lobby_Logout.setActionCommand("LOGOUT");
				Lobby_Logout.addActionListener(Action);
			Lobby_Quit = new JButton("Beenden");
				Lobby_Quit.setActionCommand("QUIT");
				Lobby_Quit.addActionListener(Action);
		P_Lobby.add(Lobby_ChangePlayer);
		P_Lobby.add(Lobby_ChangeReady);
		P_Lobby.add(Lobby_Logout);
		P_Lobby.add(Lobby_Quit);
		
		P_Player = new JPanel();
		P_Player.setLayout(new GridLayout(0, 2));
		GameButtons = new JButton[5];
			GameButtons[0] = new JButton("Würfeln");
				GameButtons[0].setActionCommand("GAME ROLL");
				GameButtons[0].addActionListener(Action);
			GameButtons[1] = new JButton("Weiter");
				GameButtons[1].setActionCommand("GAME NEXTPLAYER");
				GameButtons[1].addActionListener(Action);
			GameButtons[2] = new JButton("Anteile");
				GameButtons[2].setActionCommand("GAME STARTBUYING");
				GameButtons[2].addActionListener(Action);
			GameButtons[3] = new JButton("Auktion");
				GameButtons[3].setActionCommand("GAME STARTAUCTION");
				GameButtons[3].addActionListener(Action);
			GameButtons[4]	= new JButton("Bank");
				GameButtons[4].setActionCommand("GAME STARTSELLING");
				GameButtons[4].addActionListener(Action);
			Player_GiveUp = new JButton("Aufgeben");
				Player_GiveUp.setActionCommand("GAME GIVEUP");
				Player_GiveUp.addActionListener(Action);
			Player_Logout = new JButton("Ausloggen");
				Player_Logout.setActionCommand("LOGOUT");
				Player_Logout.addActionListener(Action);
			Player_Quit = new JButton("Beenden");
				Player_Quit.setActionCommand("QUIT");
				Player_Quit.addActionListener(Action);
		P_Player.add(GameButtons[0]);
		P_Player.add(GameButtons[1]);
		P_Player.add(GameButtons[2]);
		P_Player.add(GameButtons[3]);
		P_Player.add(GameButtons[4]);
		P_Player.add(Player_GiveUp);
		P_Player.add(Player_Logout);
		P_Player.add(Player_Quit);
		
		P_Viewer = new JPanel();
		P_Viewer.setLayout(new GridLayout(0, 2));
			Viewer_Logout = new JButton("Ausloggen");
				Viewer_Logout.setActionCommand("LOGOUT");
				Viewer_Logout.addActionListener(Action);
			Viewer_Quit = new JButton("Beenden");
				Viewer_Quit.setActionCommand("QUIT");
				Viewer_Quit.addActionListener(Action);
		P_Viewer.add(Viewer_Logout);
		P_Viewer.add(Viewer_Quit);
	}
	
	public void reArrange()
	{
		//nothing to do here
	}
	
	public void clear()
	{
		Lobby_ChangePlayer.setText("Mitspieler");
		Lobby_ChangeReady.setText("Bereit");
		Lobby_ChangeReady.setEnabled(false);
	}
	
	public void setChange(String Text)
	{
		Lobby_ChangePlayer.setText(Text);
		NeedRepaint = true;
	}
	
	public void setAction(int Index, boolean Enabled)
	{
		GameButtons[Index].setEnabled(Enabled);
		NeedRepaint = true;
	}
	
	public void setReadyChange(boolean Enabled, String Text)
	{
		Lobby_ChangeReady.setEnabled(Enabled);
		Lobby_ChangeReady.setText(Text);
		NeedRepaint = true;
	}
}