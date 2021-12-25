import java.awt.*;
import javax.swing.*;

public class UserList extends RenderObject
{
	private Main CurrentMain;
	private JPanel Main, Content;
	private JScrollPane Pane;
	public User First, Last;
	
	public UserList(Main CurrentMain, JPanel main)
	{
		super(main);
		this.CurrentMain = CurrentMain;
		Main = main;
		
		Content = new JPanel();
		Content.setLayout(new BoxLayout(Content, BoxLayout.Y_AXIS));
		
		Pane = new JScrollPane(Content, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		Main.setLayout(new BorderLayout());
		Main.add(Pane);
	}
	
	public void reArrange()
	{
		Pane.setPreferredSize(Main.getSize());
	}
	
	public void addUser(int UserID, String UserName, Color Col)
	{
		User Current = new User(UserID, UserName, Col);
		if(First == null)
		{
			First = Current;
			Last = Current;
		}
		else
		{
			Last.After = Current;
			Current.Before = Last;
			Last = Current;
		}
		Content.add(Current);
		NeedRepaint = true;
	}
	
	public void setPlayerColor(int UserID, Color col)
	{
		User CurUser = getUser(UserID);
		if(CurUser != null)
		{
			CurUser.setColor(col);
			NeedRepaint = true;
		}
	}
	
	public void markUser(int ID, boolean Mark)
	{
		getUser(ID).setMarked(Mark);
		NeedRepaint = true;
	}
	
	public void showPercent(int ID, boolean Show, int Value)
	{
		getUser(ID).showPercent(Show, Value);
		NeedRepaint = true;
	}
	
	public void removeUser(int UserID)
	{
		User CurUser = getUser(UserID);
		
		if(CurUser != null)
		{
			if(First == Last)
			{
				First = null;
				Last = null;
			}
			else if(First == CurUser)
			{
				First.After.Before = null;
				First = First.After;
			}
			else if(Last == CurUser)
			{
				Last.Before.After = null;
				Last = Last.Before;
			}
			else
			{
				CurUser.After.Before = CurUser.Before;
				CurUser.Before.After = CurUser.After;
			}
			Content.remove(CurUser);
		}
		NeedRepaint = true;
	}
	
	private User getUser(int UserID)
	{
		User CurUser = First;
		while(CurUser != null)
		{
			if(CurUser.UserID == UserID)
			{
				return CurUser;
			}
			CurUser = CurUser.After;
		}
		return null;
	}
	
	public void clear()
	{
		First = null;
		Last = null;
		Content.removeAll();
		NeedRepaint = true;
	}
	
	public class User extends JLabel
	{
		public User Before, After;
		private String Username;
		private int UserID;
		
		public User(int UserID, String Username, Color Col)
		{
			super(UserID + " - " + Username);
			setForeground(Col);
			this.UserID = UserID;
			this.Username = Username;
		}
		
		public void setColor(Color col)
		{
			setForeground(col);
		}
		
		public void setMarked(boolean Mark)
		{
			if(Mark)
			{
				ImageIcon Ico = new ImageIcon(CurrentMain.URLs[2][45]);
				Ico.setImage(Ico.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
				
				setHorizontalTextPosition(LEFT);
				setIcon(Ico);
			}
			else
			{
				setIcon(null);
			}
		}
		
		public void showPercent(boolean Show, int Value)
		{
			if(Show)
			{
				setText(UserID + " - " + Username + " - " + Value + "%");
			}
			else
			{
				setText(UserID + " - " + Username);
			}
		}
	}
}