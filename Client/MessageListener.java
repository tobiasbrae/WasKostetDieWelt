import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MessageListener implements AdjustmentListener
{
	private BoundedRangeModel CurrentModel;
	private int LastMaximum;
	
	public MessageListener(MessageList CurrentList)
	{
		this.CurrentModel = CurrentList.Pane.getVerticalScrollBar().getModel();
		CurrentList.Pane.getVerticalScrollBar().addAdjustmentListener(this);
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		if(LastMaximum != CurrentModel.getMaximum())
		{
			for(int i = 0; i < 100; i++)
			{
				//Some idle
			}
			
			CurrentModel.setValue(CurrentModel.getMaximum() + CurrentModel.getExtent());
		}
		
		LastMaximum = CurrentModel.getMaximum();
	}
}