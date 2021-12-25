import java.awt.*;
import javax.swing.*;
import java.util.Scanner;

public class MessageList extends RenderObject
{
	private Main CurrentMain;
	private JPanel Main, Content;
	private int MaxChars;
	protected JScrollPane Pane;
	
	public MessageList(Main CurrentMain, JPanel Main, int MaxChars)
	{
		super(Main);
		this.CurrentMain = CurrentMain;
		this.Main = Main;
		this.MaxChars = MaxChars;
		
		Content = new JPanel();
		Content.setLayout(new BoxLayout(Content, BoxLayout.Y_AXIS));
		
		Pane = new JScrollPane(Content, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		Main.setLayout(new BorderLayout());
		Main.add(Pane);
	}
	
	public void reArrange()
	{
		Pane.setPreferredSize(Main.getSize());
	}
	
	public void addMessage(Color col, String Message)
	{
		Scanner Data = new Scanner(Message);
		boolean FirstWord, HasRest;
		String Output, NextWord;
		
		FirstWord = true;
		HasRest = false;
		Output = "";
		NextWord = "";
		
		while(Data.hasNext() || HasRest)
		{	
			if(!HasRest)
			{
				NextWord = Data.next();
			}
			
			if(FirstWord && NextWord.length() <= MaxChars)
			{
				FirstWord = false;
				HasRest = false;
				Output = NextWord;
				if(!Data.hasNext())
				{
					createLabel(col, Output);
				}
			}
			else if(Output.length() + NextWord.length() < MaxChars)
			{
				Output = Output + " " + NextWord;
				HasRest = false;
				if(!Data.hasNext())
				{
					createLabel(col, Output);
				}
			}
			else if(Output.equals(""))
			{
				Output = NextWord.substring(0, MaxChars);
				NextWord = NextWord.substring(MaxChars);
				createLabel(col, Output);
				Output = "";
				HasRest = true;
			}
			else if(Output.equals(" "))
			{
				Output = Output + NextWord.substring(0, MaxChars - 1);
				NextWord = NextWord.substring(MaxChars - 1);
				createLabel(col, Output);
				Output = "";
				HasRest = true;
			}
			else
			{
				createLabel(col, Output);
				Output = "";
				HasRest = true;
			}
		}
		NeedRepaint = true;
	}
	
	public void createLabel(Color col, String Message)
	{
		JLabel Input = new JLabel(Message);
		Input.setForeground(col);
		Content.add(Input);
	}
}