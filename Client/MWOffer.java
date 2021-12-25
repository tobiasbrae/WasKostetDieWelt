import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

public class MWOffer extends MainWindow
{
	private Main CurrentMain;
	private JPanel Main;
	private JLabel PropertyData, CurrentPrice, Offerer;
	private JButton Higher, Quit;
	
	public MWOffer(Main CurrentMain, JPanel Main, ActionListener Listener)
	{
		super(Main);
		this.CurrentMain = CurrentMain;
		this.Main = Main;
		
		Frame.setLayout(new BoxLayout(Frame, BoxLayout.Y_AXIS));
			PropertyData = new JLabel(" ");
			CurrentPrice = new JLabel("0DM");
			Offerer = new JLabel(" ");
			Higher = new JButton("Steigern");
			Higher.setActionCommand("GAME HIGHEROFFER");
			Higher.addActionListener(Listener);
			Quit = new JButton("Aussteigen");
			Quit.setActionCommand("GAME QUITAUCTION");
			Quit.addActionListener(Listener);
		Frame.add(PropertyData);
		Frame.add(CurrentPrice);
		Frame.add(Offerer);
		Frame.add(Higher);
		Frame.add(Quit);
	}
	
	public void reArrange()
	{
		//nothing to do here
	}
	
	public void setPrice(boolean canAction, int Price, String Offerer, String PropertyData)
	{
		Higher.setEnabled(canAction);
		Quit.setEnabled(canAction);
		Scanner CurrentLine = new Scanner(PropertyData);
		CurrentLine.next();
		this.PropertyData.setText(CurrentLine.next() + CurrentLine.nextLine());
		CurrentPrice.setText("Höchstgebot: " + CurrentMain.getPointedInt(Price) + "DM");
		this.Offerer.setText("Höchstbietender: " + Offerer);
		NeedRepaint = true;
	}
}