import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MWLogon extends MainWindow
{
	private JLabel L_Name, L_Address, L_Port;
	private JTextField I_Name, I_Address, I_Port;

	public MWLogon(JPanel Main)
	{
		super(Main);
		RenderDepth = 1;
		Frame.setLayout(new GridLayout(3, 2));
		Frame.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		L_Name = new JLabel("Name:");
		I_Name = new JTextField(10);
		
		L_Address = new JLabel("Server-Adresse:");
		I_Address = new JTextField(10);
		I_Address.setText("localhost");
		
		L_Port = new JLabel("Server-Port:");
		I_Port = new JTextField(10);
		I_Port.setText("5000");
		
		Frame.add(L_Name);
		Frame.add(I_Name);
		Frame.add(L_Address);
		Frame.add(I_Address);
		Frame.add(L_Port);
		Frame.add(I_Port);
	}
	
	public void reArrange()
	{
		//nothing to do here
	}
	
	public boolean isDataCorrect(Main CurrentMain, MessageList MsgL)
	{
		boolean Correct = true;
		if(I_Name.getText().isEmpty())
		{
			Correct = false;
			MsgL.addMessage(CurrentMain.Colors[1], "Du musst einen Namen angeben.");
		}
		else if(!I_Name.getText().matches("[a-zA-Z0-9]+")) //Enthält Sonderzeichen
		{
			Correct = false;
			MsgL.addMessage(CurrentMain.Colors[1], "Dein Name darf keine Sonderzeichen enthalten.");
		}
		
		if(I_Address.getText().isEmpty())
		{
			Correct = false;
			MsgL.addMessage(CurrentMain.Colors[1], "Du musst eine Server-Adresse angeben.");
		}
		
		if(I_Port.getText().isEmpty())
		{
			Correct = false;
			MsgL.addMessage(CurrentMain.Colors[1], "Du musst einen Port angeben.");
		}
		else
		{
			try
			{
				Integer.parseInt(I_Port.getText());
			}
			catch(Exception ex)
			{
				Correct = false;
				MsgL.addMessage(CurrentMain.Colors[1], "Der Port muss eine ganze Zahl sein.");
			}
		}
		return Correct;
	}
	
	public String getName()
	{
		return I_Name.getText();
	}
	
	public String getAddress()
	{
		return I_Address.getText();
	}
	
	public int getPort()
	{
		return Integer.parseInt(I_Port.getText());
	}

}