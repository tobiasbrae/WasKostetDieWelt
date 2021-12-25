import javax.swing.JPanel;

public class MainWindow extends RenderObject
{
	private JPanel Main;
	protected JPanel Frame;
	
	public MainWindow(JPanel Main)
	{
		super(Main);
		this.Main = Main;
		Frame = new JPanel();
	}
	
	public void addToPanel()
	{
		Main.add(Frame);
		NeedRepaint = true;
	}
	
	public void removeFromPanel()
	{
		Main.remove(Frame);
	}
}