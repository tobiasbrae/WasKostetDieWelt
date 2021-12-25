import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.File;
import java.util.Scanner;

public class MWGame extends MainWindow
{
	private Main CurrentMain;
	private JPanel Main;
	private Window CurrentWindow;
	private Image BasicImage, PropertyImage, FinalImage, ScaledImage, BackBuffer;
	private int PosX, PosY, SizeX, SizeY;
	GraphicsConfiguration CurrentConfiguration;
	
	public MWGame(Main CurrentMain, Window CurrentWindow, JPanel Main)
	{
		super(Main);
		this.CurrentMain = CurrentMain;
		this.Main = Main;
		this.CurrentWindow = CurrentWindow;
		
		ImageIcon Ico = new ImageIcon(CurrentMain.URLs[2][0]);
		BasicImage = Ico.getImage();
		
		JPanel Graphic = new JPanel();
		
		GraphicsEnvironment CurrentEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice CurrentDevice = CurrentEnvironment.getDefaultScreenDevice();
		CurrentConfiguration = CurrentDevice.getDefaultConfiguration();
		
		PropertyImage = CurrentConfiguration.createCompatibleImage(3300, 2700, Transparency.OPAQUE);
		PropertyImage.getGraphics().drawImage(BasicImage, 0, 0, null);
		
		NeedContinousRepaint = true;
	}
	
	public synchronized void drawRessource(int Position, int Ressource)
	{
		ImageIcon Ico = new ImageIcon(CurrentMain.URLs[2][14 + Ressource]);
		PropertyImage.getGraphics().drawImage(Ico.getImage(), CurrentWindow.Positions[Position][0] + 10, CurrentWindow.Positions[Position][1] + 140, null);
	}
	
	public void reArrange()
	{
		Frame.setPreferredSize(Main.getSize());
		PosX = 11;
		PosY = 33;
		double SizeX_D = 3300.0 * CurrentWindow.ScaleFactor;
		double SizeY_D = 2700.0 * CurrentWindow.ScaleFactor;
		
		SizeX = (int) SizeX_D;
		SizeY = (int) SizeY_D;
		PosX = Main.getWidth() - SizeX;
		PosX /= 2;
		PosX += 11;
		
		ScaledImage = PropertyImage.getScaledInstance(SizeX, SizeY, Image.SCALE_SMOOTH);
		BackBuffer = CurrentConfiguration.createCompatibleImage(SizeX, SizeY, Transparency.OPAQUE);
		
		CurrentWindow.AnimRenderer.reArrange();
	}
	
	public void clear()
	{
		ScaledImage = BasicImage.getScaledInstance(SizeX, SizeY, Image.SCALE_SMOOTH);
	}
	
	public void render(Graphics g)
	{
		BackBuffer.getGraphics().drawImage(ScaledImage, 0, 0, null);
		CurrentWindow.AnimRenderer.renderAnimations(BackBuffer.getGraphics());
		g.drawImage(BackBuffer, PosX, PosY, null);
	}
}