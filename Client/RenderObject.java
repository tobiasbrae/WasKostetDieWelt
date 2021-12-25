import javax.swing.JComponent;
import java.awt.Image;
import java.awt.Graphics;

public class RenderObject
{
	public RenderObject Before, After;
	protected int RenderDepth;
	public int TimesRendered;
	protected boolean NeedRepaint, NeedContinousRepaint;
	private JComponent OwnerComponent;
	private Image BackBuffer;
	
	public RenderObject(JComponent OwnerComponent)
	{
		this.OwnerComponent = OwnerComponent;
		RenderDepth = 4;
	}
	
	public Image getRenderedImage()
	{
		BackBuffer = OwnerComponent.createImage(OwnerComponent.getWidth(), OwnerComponent.getHeight());
		OwnerComponent.paintAll(BackBuffer.getGraphics());
		return BackBuffer;
	}
	
	public int getPosX()
	{
		return OwnerComponent.getLocationOnScreen().x;
	}
	
	public int getPosY()
	{
		return OwnerComponent.getLocationOnScreen().y;
	}
}