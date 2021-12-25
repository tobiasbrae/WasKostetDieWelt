import java.awt.Image;

public class WindowRenderer extends Thread
{
	private Window CurrentWindow;
	private Image BackBuffer;
	private int SizeX, SizeY, PosX, PosY;
	private RenderObject First, Last;
	
	public WindowRenderer(Window CurrentWindow)
	{
		this.CurrentWindow = CurrentWindow;
	}
	
	public void addRenderObject(RenderObject Input)
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
	}
	
	public void run()
	{
		reArrange();
		
		while(true)
		{			
			if(CurrentWindow.getWidth() != SizeX || CurrentWindow.getHeight() != SizeY)
			{
				reArrange();
			}
			
			SizeX = CurrentWindow.getWidth();
			SizeY = CurrentWindow.getHeight();
			
			if(CurrentWindow.getExtendedState() == Window.MAXIMIZED_BOTH)
			{
				PosX = 8;
				PosY = 8;
			}
			else
			{
				PosX = 0;
				PosY = 0;
			}
			
			try
			{
				sleep(20);
			}
			catch(InterruptedException e)
			{
				System.out.println(e + "");
			}
			
			RenderObject CurrentObject = First;
			while(CurrentObject != null)
			{
				if(CurrentObject.NeedRepaint || CurrentObject.NeedContinousRepaint)
				{
					if(CurrentObject == CurrentWindow.MW_Game)
					{
						if(CurrentWindow.Current == CurrentWindow.MW_Game)
						{
							CurrentWindow.MW_Game.render(CurrentWindow.getGraphics());
						}
					}
					else
					{
						CurrentWindow.getGraphics().drawImage(CurrentObject.getRenderedImage(), CurrentObject.getPosX() + PosX, CurrentObject.getPosY() + PosY, null);
					}
					
					CurrentObject.TimesRendered++;
					if(CurrentObject.TimesRendered == CurrentObject.RenderDepth)
					{
						CurrentObject.NeedRepaint = false;
						CurrentObject.TimesRendered = 0;
					}
				}
				CurrentObject = CurrentObject.After;
			}
		}
	}
	
	public void reArrange()
	{
		BackBuffer = CurrentWindow.createImage(CurrentWindow.getWidth(), CurrentWindow.getHeight());
		CurrentWindow.reArrange();		
		try
		{
			sleep(200);
		}
		catch(InterruptedException e)
		{
			System.out.println(e + "");
		}
		CurrentWindow.paintAll(BackBuffer.getGraphics());
		CurrentWindow.getGraphics().drawImage(BackBuffer, 0, 0, null);
	}
}