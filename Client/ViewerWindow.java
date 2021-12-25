import java.util.Scanner;
import java.awt.*;
import javax.swing.*;

public class ViewerWindow extends RenderObject
{
	private Main CurrentMain;
	private JPanel Main;
	private JPanel Content;
	private JScrollPane Pane;
	private PlayerData First, Last;
	
	public ViewerWindow(Main CurrentMain, JPanel Main)
	{
		super(Main);
		this.CurrentMain = CurrentMain;
		this.Main = Main;
		
		Content = new JPanel();
		Content.setLayout(new GridLayout(0, 2));
		
		Pane = new JScrollPane(Content, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		Main.setLayout(new BorderLayout());
		Main.add(Pane);
	}
	
	public void addToPanel()
	{
		Main.add(Pane);
	}
	
	public void removeFromPanel()
	{
		Main.remove(Pane);
	}
	
	public void reArrange()
	{
		Pane.setPreferredSize(Main.getSize());
	}
	
	public void setViewer(String Data)
	{
		Scanner Line = new Scanner(Data);
		int Action = Line.nextInt();
		int ID = Line.nextInt();
		if(Action == 0)
		{
			String PlayerName = Line.next();
			int Properties = Line.nextInt();
			int Money = Line.nextInt();
			Color Col = new Color(Line.nextInt());
			addPlayerData(new PlayerData(ID, PlayerName, Properties, Money, Col));
		}
		else if(Action == 1)
		{
			PlayerData Current = getPlayerData(ID);
			Current.setProperties(Line.nextInt());
		}
		else if(Action == 2)
		{
			PlayerData Current = getPlayerData(ID);
			Current.setMoney(Line.nextInt());
		}
		else if(Action == 3)
		{
			PlayerData Current = getPlayerData(ID);
			removePlayerData(Current);
		}
		NeedRepaint = true;
	}
	
	private PlayerData getPlayerData(int ID)
	{
		PlayerData Current = First;
		while(Current != null)
		{
			if(Current.ID == ID)
			{
				return Current;
			}
			else
			{
				Current = Current.After;
			}
		}
		return null;
	}
	
	private void addPlayerData(PlayerData Input)
	{
		if(First == null)
		{
			First = Input;
			Last = Input;
		}
		else
		{
			Last.After = Input;
			Input.Before = Last;
			Last = Input;
		}
		Content.add(Input);
	}
	
	private void removePlayerData(PlayerData Input)
	{
		if(First == Last)
		{
			First = null;
			Last = null;
		}		
		else if(First == Input)
		{
			First.After.Before = null;
			First = First.After;
		}
		else if(Last == Input)
		{
			Last.Before.After = null;
			Last = Last.Before;
		}
		else
		{
			Input.Before.After = Input.After;
			Input.After.Before = Input.Before;
		}
		Content.remove(Input);
	}
	
	private class PlayerData extends JPanel
	{
		public PlayerData Before, After;
		int ID, Properties, Money;
		private Color Col;
		private JLabel L_Name, L_Properties, L_Money;
		private String PlayerName;
		
		public PlayerData(int ID, String PlayerName, int Properties, int Money, Color Col)
		{
			this.ID = ID;
			this.PlayerName = PlayerName;
			this.Properties = Properties;
			this.Money = Money;
			this.Col = Col;
			
			setLayout(new GridLayout(0, 3));
			L_Name = new JLabel(CurrentMain.lengthString(false, PlayerName, 15));
				L_Name.setForeground(Col);
				this.add(L_Name);
			L_Properties = new JLabel("Anteilsscheine: " + Properties);
				L_Properties.setForeground(Col);
				this.add(L_Properties);
			L_Money = new JLabel("Geld: " + CurrentMain.getPointedInt(Money) + "DM");
				L_Money.setForeground(Col);
				this.add(L_Money);
		}
		
		public void setProperties(int Value)
		{
			this.Properties = Value;
			L_Properties.setText("Anteilsscheine: " + Value);
		}
		
		public void setMoney(int Value)
		{
			this.Money = Value;
			L_Money.setText("Geld: " + CurrentMain.getPointedInt(Value) + "DM");
		}
	}
	
	public void clear()
	{
		First = null;
		Last = null;
		Content.removeAll();
	}
}