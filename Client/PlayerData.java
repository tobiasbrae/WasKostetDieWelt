import java.awt.*;
import javax.swing.*;

public class PlayerData extends RenderObject
{
	private Main CurrentMain;
	private JPanel CurrentPanel, Frame, P_Money, P_Joker, P_Sound;
	private JLabel Label_Money, Value_Money, Label_Joker, Value_Joker;
	private JCheckBox SoundSetting;
	
	public PlayerData(Main CurrentMain, JPanel CurrentPanel)
	{
		super(CurrentPanel);
		this.CurrentMain = CurrentMain;
		this.CurrentPanel = CurrentPanel;
		
		Frame = new JPanel();
		Frame.setLayout(new BoxLayout(Frame, BoxLayout.Y_AXIS));
			P_Money = new JPanel();
			P_Money.setLayout(new BoxLayout(P_Money, BoxLayout.X_AXIS));
				Label_Money = new JLabel("Geld: ");
				Value_Money = new JLabel("0DM");
			P_Money.add(Label_Money);
			P_Money.add(Value_Money);
			P_Joker = new JPanel();
			P_Joker.setLayout(new BoxLayout(P_Joker, BoxLayout.X_AXIS));
				Label_Joker = new JLabel("Joker: ");
				Value_Joker = new JLabel("0");
			P_Joker.add(Label_Joker);
			P_Joker.add(Value_Joker);
			P_Sound = new JPanel();
				SoundSetting = new JCheckBox("Sound", CurrentMain.SoundActivated);
				changeSound();
				SoundSetting.setActionCommand("CHANGESOUND");
				SoundSetting.addActionListener(CurrentMain.CurrentIfListener);
			P_Sound.add(SoundSetting);
		Frame.add(P_Money);
		Frame.add(P_Joker);
		Frame.add(new JPanel());
		Frame.add(new JPanel());
		Frame.add(new JPanel());
		Frame.add(new JPanel());
		Frame.add(P_Sound);
		CurrentPanel.add(Frame);
	}
	
	public void changeSound()
	{
		CurrentMain.SoundActivated = SoundSetting.isSelected();
		if(CurrentMain.SoundActivated)
		{
			SoundSetting.setForeground(Color.black);
		}
		else
		{
			SoundSetting.setForeground(Color.gray);
		}
	}
	
	public void reArrange()
	{
		//nothing to do here
	}
	
	public void setMoney(int Value, boolean DrawBlack)
	{
		Value_Money.setText(CurrentMain.getPointedInt(Value) + "DM");
		if(Value > 0 || DrawBlack)
		{
			Value_Money.setForeground(Color.black);
		}
		else
		{
			Value_Money.setForeground(Color.red);
		}
		NeedRepaint = true;
	}
	
	public void setJoker(int Value, boolean DrawBlack)
	{
		Value_Joker.setText(Value + "");
		if(Value > 0 || DrawBlack)
		{
			Value_Joker.setForeground(Color.black);
		}
		else
		{
			Value_Joker.setForeground(Color.red);
		}
		NeedRepaint = true;
	}
}