import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;

public class MWTrade extends MainWindow
{
	private Main CurrentMain;
	private JPanel Main;
	private ActionListener Listener;
	private JPanel Left, Right;
	private JPanel Content_Left, Content_Right, Left1, Right1, Right2;
	private JScrollPane Pane_Left, Pane_Right;
	private Property First, Last;
	private JButton Submit, Cancel;
	private JLabel L_Left, L_Right, Price;
	
	public MWTrade(Main CurrentMain, JPanel Main, ActionListener Listener)
	{		
		super(Main);
		RenderDepth = 5;
		this.CurrentMain = CurrentMain;
		this.Main = Main;
		this.Listener = Listener;
		
		Frame.setLayout(new BoxLayout(Frame, BoxLayout.X_AXIS));
		
		Left = new JPanel();
		Left.setLayout(new BorderLayout());
		Content_Left = new JPanel();
		Content_Left.setLayout(new BoxLayout(Content_Left, BoxLayout.Y_AXIS));		
		Pane_Left = new JScrollPane(Content_Left, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		Pane_Left.setPreferredSize(new Dimension(400, 500));
		
		Left.add(Pane_Left);
		
		Right = new JPanel();
		Right.setLayout(new BorderLayout());
		Content_Right = new JPanel();
		Content_Right.setLayout(new BoxLayout(Content_Right, BoxLayout.Y_AXIS));		
		Pane_Right = new JScrollPane(Content_Right, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		Pane_Right.setPreferredSize(new Dimension(400, 500));
		
		Right.add(Pane_Right);
		
		Left1 = new JPanel();
		Left1.setLayout(new BoxLayout(Left1, BoxLayout.Y_AXIS));
		L_Left = new JLabel("Left");
		Cancel = new JButton("Abbrechen");
		Cancel.setActionCommand("GAME CANCELACTION");
		Cancel.addActionListener(Listener);
		
		Left1.add(L_Left);
		Left1.add(Left);
		Left1.add(Cancel);
		
		Right1 = new JPanel();
		Right1.setLayout(new BoxLayout(Right1, BoxLayout.Y_AXIS));
		L_Right = new JLabel("Right");
		Right2 = new JPanel();
		Right2.setLayout(new BoxLayout(Right2, BoxLayout.X_AXIS));
		Price = new JLabel("0DM");
		Submit = new JButton("Submit");
		Submit.setActionCommand("GAME ACCEPTACTION");
		Submit.addActionListener(Listener);
		
		Right1.add(L_Right);
		Right1.add(Right);
			Right2.add(Price);
			Right2.add(Submit);
		Right1.add(Right2);
		
		Frame.add(Left1);
		Frame.add(Right1);
	}
	
	public void reArrange()
	{
		//nothing to do here
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
	
	public void setPrice(boolean Buyable, int Value)
	{
		Price.setText(CurrentMain.getPointedInt(Value) + "DM");
		Submit.setEnabled(Buyable);
		if(Buyable)
		{
			Price.setForeground(Color.black);
		}
		else
		{
			Price.setForeground(Color.red);
		}
		NeedRepaint = true;
	}
	
	public void setTrade(boolean isBuying)
	{
		if(isBuying)
		{
			L_Left.setText("Bank");
			L_Right.setText("Einkaufswagen");
			Submit.setText("Kaufen");
		}
		else
		{
			L_Left.setText("Eigene Anteilsscheine");
			L_Right.setText("Verkaufswagen");
			Submit.setText("Verkaufen");
		}
		NeedRepaint = true;
	}
	
	public void clear()
	{
		Content_Left.removeAll();
		Content_Right.removeAll();
		First = null;
		Last = null;
		setPrice(false, 0);
	}
	
	public void ChangeSide(Main main, int ID)
	{
		getProperty(First, ID).changeSide(main);
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
		boolean Right;
		int ID;
		
		public Property(ActionListener Listener, int ID, String Data)
		{
			super(Data);
			setSize(new Dimension(395, 20));
			this.ID = ID;
			
			setActionCommand("CHANGESIDE " + ID);
			addActionListener(Listener);
			Content_Left.add(this);
		}

		public void changeSide(Main main)
		{
			if(!Right)
			{
				Content_Left.remove(this);
				Content_Right.add(this);
				main.sendMessage("GAME ADDPROPERTY " + ID);
			}
			else
			{
				Content_Right.remove(this);
				Content_Left.add(this);
				main.sendMessage("GAME REMOVEPROPERTY " + ID);
			}
			Right = !Right;
		}
	}
}