import java.awt.*;
import javax.imageio.*;
import java.io.File;
import javax.swing.*;

public class AnimationRenderer extends Thread
{
	private Main CurrentMain;
	private Image[][] Dices;
	private int Dices_X1, Dices_X2, Dices_Y, Dices_S;
	private int DiceRed, DiceWhite, DiceCounter, DiceTarget, DiceWaitCounter, DiceWaitTarget;
	private PlayerFigure First, Last;
	
	public AnimationRenderer(Main CurrentMain)
	{
		this.CurrentMain = CurrentMain;
		Dices = new Image[4][7];
		ImageIcon Ico;
		
		for(int i = 1; i < 7; i++)
		{
			Ico = new ImageIcon(CurrentMain.URLs[2][i]);
			Dices[0][i] = Ico.getImage();
			Ico = new ImageIcon(CurrentMain.URLs[2][i + 6]);
			Dices[1][i] = Ico.getImage();
		}
		
		DiceTarget = 1;
		DiceWaitTarget = 2;
		setDices(1, 1);
		DiceTarget = 15;
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				sleep(20);
			}
			catch(Exception e)
			{
				System.out.println(e.toString());
			}
			
			changeDices();
			
			if(First != null && First == Last)
			{
				First.moveForward();
			}
			else
			{
				PlayerFigure CurPlayerFigure = First;
				while(CurPlayerFigure != null)
				{
					CurPlayerFigure.moveForward();
					CurPlayerFigure = CurPlayerFigure.After;
				}
			}
		}
	}
	
	public void reArrange()
	{
		double DS = CurrentMain.CurrentWindow.ScaleFactor * 100.0;
		double DX1 = CurrentMain.CurrentWindow.ScaleFactor * 2000.0; 
		double DX2 = DX1 + DS + 50 * CurrentMain.CurrentWindow.ScaleFactor;
		double DY = CurrentMain.CurrentWindow.ScaleFactor * 1800.0; 
		
		Dices_X1 = (int) DX1;
		Dices_X2 = (int) DX2;
		Dices_Y = (int) DY;
		Dices_S = (int) DS;
		
		for(int i = 1; i < 7; i++)
		{
			Dices[2][i] = Dices[0][i].getScaledInstance(Dices_S, Dices_S, Image.SCALE_SMOOTH);
			Dices[3][i] = Dices[1][i].getScaledInstance(Dices_S, Dices_S, Image.SCALE_SMOOTH);
		}
		setDices(DiceRed, DiceWhite);
		
		PlayerFigure CurFigure = First;
		while(CurFigure != null)
		{
			loadPositions(CurFigure.Position);
			CurFigure = CurFigure.After;
		}
	}
	
	public void setDices(int DiceRed, int DiceWhite)
	{
		this.DiceRed = DiceRed;
		this.DiceWhite = DiceWhite;
		DiceCounter = 0;
		DiceWaitCounter = 0;
	}
	
	public void changeDices()
	{
		if(DiceWaitCounter != DiceWaitTarget)
		{
			DiceWaitCounter++;
		}
		else
		{
			if(DiceCounter == DiceTarget)
			{
				Dices[2][0] = Dices[2][DiceRed];
				Dices[3][0] = Dices[3][DiceWhite];
			}
			else
			{
				DiceCounter++;
				DiceWaitCounter = 0;
			
				double RandomDice = Math.random() * 6.0 + 1.0;
				Dices[2][0] = Dices[2][(int) RandomDice];
				RandomDice = Math.random() * 6.0 + 1.0;
				Dices[3][0] = Dices[3][(int) RandomDice];
			}
		}
	}
	
	public void addPlayerFigure(int ID, Color Col)
	{
		PlayerFigure Input = new PlayerFigure(ID, Col);
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
	
	public void movePlayerFigure(int ID, int Position)
	{
		getPlayerFigure(ID).TargetPosition = Position;
	}
	
	private void loadPositions(int Position)
	{
		PlayerFigure CurPlayerFigure = First;
		int Counter = 0;
		
		while(CurPlayerFigure != null)
		{
			if(CurPlayerFigure.Position == Position)
			{
				double ValueRad = 60.0;
				
				int RightSteps = Counter % 3;
				double DRightSteps = (double) RightSteps;
				
				double ValueX = (double) CurrentMain.CurrentWindow.Positions[Position][0] + 25.0 + DRightSteps * (20.0 + ValueRad);
				ValueX *= CurrentMain.CurrentWindow.ScaleFactor;
				CurPlayerFigure.PosX = (int) ValueX;
				
				int DownSteps = Counter / 3;
				double DDownSteps = (double) DownSteps;
				
				double ValueY = (double) CurrentMain.CurrentWindow.Positions[Position][1] + 15.0 + DDownSteps * (20.0 + ValueRad);
				ValueY *= CurrentMain.CurrentWindow.ScaleFactor;
				CurPlayerFigure.PosY = (int) ValueY;
				
				ValueRad *= CurrentMain.CurrentWindow.ScaleFactor;
				CurPlayerFigure.Rad = (int) ValueRad;
				
				Counter++;
			}
			CurPlayerFigure = CurPlayerFigure.After;
		}
	}
	
	private PlayerFigure getPlayerFigure(int ID)
	{
		PlayerFigure CurPlayerFigure = First;
		while(CurPlayerFigure != null)
		{
			if(CurPlayerFigure.ID == ID)
			{
				return CurPlayerFigure;
			}
			CurPlayerFigure = CurPlayerFigure.After;
		}
		return null;
	}
	
	public void removePlayerFigure(int ID)
	{
		PlayerFigure CurPlayerFigure = getPlayerFigure(ID);
		if(CurPlayerFigure != null)
		{
			if(First == Last)
			{
				First = null;
				Last = null;
			}
			else if(First == CurPlayerFigure)
			{
				First.After.Before = null;
				First = First.After;
			}
			else if(Last == CurPlayerFigure)
			{
				Last.Before.After = null;
				Last = Last.Before;
			}
			else
			{
				CurPlayerFigure.Before.After = CurPlayerFigure.After;
				CurPlayerFigure.After.Before = CurPlayerFigure.Before;
			}
		}
	}
	
	public void renderAnimations(Graphics g)
	{
		g.drawImage(Dices[2][0], Dices_X1, Dices_Y, null);
		g.drawImage(Dices[3][0], Dices_X2, Dices_Y, null);
		
		PlayerFigure CurPlayerFigure = First;
		while(CurPlayerFigure != null)
		{
			CurPlayerFigure.renderFigure(g);
			CurPlayerFigure = CurPlayerFigure.After;
		}
	}
	
	public void clear()
	{
		DiceRed = 1;
		DiceWhite = 1;		
		Dices[2][0] = Dices[2][1];
		Dices[3][0] = Dices[3][1];
		First = null;
		Last = null;
	}
	
	private class PlayerFigure
	{
		public PlayerFigure Before, After;
		public int ID, Position, TargetPosition, PosX, PosY, Rad;
		private int Counter, Target;
		private Color Col;
		
		public PlayerFigure(int ID, Color Col)
		{
			this.ID = ID;
			this.Col = Col;
			Position = 0;
			TargetPosition = 1;
			Rad = 25;
			Counter = 0;
			Target = 25;
		}
		
		public void moveForward()
		{
			if(Position != TargetPosition)
			{
				Counter++;
				if(Counter == Target)
				{
					int LastPosition = Position;
					if(Position == 65)
					{
						Position = 2;
					}
					else
					{
						Position++;
					}
					loadPositions(LastPosition);
					loadPositions(Position);
					Counter = 0;
					CurrentMain.playSound(3);
				}
			}
		}
		
		public void renderFigure(Graphics g)
		{
			g.setColor(Color.black);
			g.fillOval(PosX, PosY, Rad, Rad);			
			g.setColor(Col);
			g.fillOval(PosX + 1, PosY + 1, Rad - 2, Rad - 2);
		}
	}
}