import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

public class MWAuction extends MainWindow
{
	private Main CurrentMain;
	private JPanel Main;
	private Property First, Last;
	private JPanel JP_Select, JP_Auction, S_Content, S_Frame, A_Buttons;
	private JButton S_Cancel, S_Joker, A_Start, A_Cancel;
	private JLabel CurrentProperty, Price, Offerer;
	private JScrollPane S_Pane;
	private ActionListener Listener;
	
	public MWAuction(Main CurrentMain, JPanel Main, ActionListener Listener)
	{
		super(Main);
		RenderDepth = 5;
		this.CurrentMain = CurrentMain;
		this.Main = Main;
		this.Listener = Listener;
		
		JP_Select = new JPanel();
		JP_Select.setLayout(new BoxLayout(JP_Select, BoxLayout.Y_AXIS));
			S_Frame = new JPanel();
			S_Frame.setLayout(new BorderLayout());
				S_Content = new JPanel();
				S_Content.setLayout(new BoxLayout(S_Content, BoxLayout.Y_AXIS));
				S_Pane = new JScrollPane(S_Content);
				S_Pane.setPreferredSize(new Dimension(400, 500));
			S_Frame.add(S_Pane);
			S_Cancel = new JButton("Abbrechen");
			S_Cancel.setActionCommand("GAME CANCELACTION");
			S_Cancel.addActionListener(Listener);
			S_Joker = new JButton("Joker");
			S_Joker.setActionCommand("GAME GIVEJOKER");
			S_Joker.addActionListener(Listener);
		JP_Select.add(S_Frame);
		JP_Select.add(S_Cancel);
		JP_Select.add(S_Joker);
		
		JP_Auction = new JPanel();
		JP_Auction.setLayout(new BoxLayout(JP_Auction, BoxLayout.Y_AXIS));
			CurrentProperty = new JLabel("CurrentProperty");
			Price = new JLabel("");
			Offerer = new JLabel("");
			A_Buttons = new JPanel();
			A_Buttons.setLayout(new BoxLayout(A_Buttons, BoxLayout.X_AXIS));
				A_Start = new JButton("Verkaufen");
				A_Start.setActionCommand("GAME ACCEPTACTION");
				A_Start.addActionListener(Listener);
				A_Cancel = new JButton("Abbrechen");
				A_Cancel.setActionCommand("GAME CANCELACTION");
				A_Cancel.addActionListener(Listener);
			A_Buttons.add(A_Start);
			A_Buttons.add(A_Cancel);
		JP_Auction.add(CurrentProperty);
		JP_Auction.add(Price);
		JP_Auction.add(Offerer);
		JP_Auction.add(A_Buttons);
		
		Frame.add(JP_Select);
	}
	
	public void reArrange()
	{
		//nothing to do here
	}
	
	public void clearList()
	{
		S_Content.removeAll();
	}
	
	public void addProperty(String Data)
	{
		Scanner Line = new Scanner(Data);
		Property Prop = new Property(Listener, Line.nextInt(), Line.nextLine());
		
		if(First == null)
		{
			First = Prop;
			Last = First;
		}
		else
		{
			Prop.Before = Last;
			Last.After = Prop;
			Last = Prop;
		}
		NeedRepaint = true;
	}
	
	public void showAuctionList(boolean canJoker)
	{
		Frame.removeAll();
		S_Joker.setEnabled(canJoker);
		Frame.add(JP_Select);
		NeedRepaint = true;
	}
	
	public void setProperty(int ID)
	{
		Frame.removeAll();
		CurrentProperty.setText(getProperty(First, ID).Data);
		setPrice(0, "");
		A_Start.setEnabled(true);
		A_Cancel.setEnabled(true);
		Frame.add(JP_Auction);
		NeedRepaint = true;
	}
	
	public void setAuctionStarted()
	{
		A_Start.setEnabled(false);
		A_Cancel.setEnabled(false);
		NeedRepaint = true;
	}
	
	public void setPrice(int Value, String Username)
	{
		Price.setText("Höchstgebot: " + CurrentMain.getPointedInt(Value) + "DM");
		Offerer.setText("Höchstbietender: " + Username);
		NeedRepaint = true;
	}
	
	private Property getProperty(Property Current, int ID)
	{
		if(Current.ID == ID) { return Current; }
		if(Current.After != null) { return getProperty(Current.After, ID); }
		return null;
	}
	
	private class Property extends JButton
	{
		Property Before, After;
		int ID;
		String Data;
		
		public Property(ActionListener Listener, int ID, String Data)
		{
			super(Data);
			setSize(new Dimension(395, 20));
			this.ID = ID;
			Scanner CurrentLine = new Scanner(Data);
			this.Data = CurrentLine.next() + CurrentLine.nextLine();
			
			setActionCommand("GAME ACCEPTACTION " + ID);
			addActionListener(Listener);
			S_Content.add(this);
			NeedRepaint = true;
		}	
	}
}