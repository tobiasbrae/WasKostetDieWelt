import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;

public class Window extends JFrame
{
	private Main CurrentMain;
	private ActionListener AListener;
	private WindowRenderer WinRenderer;
	public AnimationRenderer AnimRenderer;
	
	public JPanel Main, Left, Right;
	public JPanel L_Top, L_Bottom;
	public JPanel LB_Left, LB_Middle, LB_Right;
	public JPanel R_Top, R_Middle, R_Bottom;
	
	public MainWindow Current;
	public MWLogon MW_Logon;
	public MWGame MW_Game;
	public MWTrade MW_Trade;
	public MWAuction MW_Auction;
	public MWOffer MW_Offer;
	public PropertyWindow Properties;
	public ViewerWindow Viewers;
	public PlayerData PData;
	public ActionField Action;
	public UserList UList;
	public MessageList MList;
	public MessageListener MListListener;
	private GridBagLayout GBL;
	private GridBagConstraints GBC;
	public JTextField UserInput;
	public int[][] Positions;
	public double ScaleFactor = 0.285;
	
	public Window(Main CurrentMain)
	{
		super("Was kostet die Welt?");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1280, 1000);
		setMinimumSize(new Dimension(1280, 1000));
		
		this.CurrentMain = CurrentMain;
		AListener = CurrentMain.CurrentIfListener;
		
		Positions = new int[66][2];
		Reader Data = new Reader(CurrentMain.URLs[0][0]);
		while(Data.hasNextLine())
		{
			Scanner CurrentLine = new Scanner(Data.nextLine());
			int Position = CurrentLine.nextInt();
			Positions[Position][0] = CurrentLine.nextInt();
			Positions[Position][1] = CurrentLine.nextInt();
		}
		
		createLayout();
	
		MW_Logon = new MWLogon(L_Top);
		SetWindow(MW_Logon);
		
		MW_Game = new MWGame(CurrentMain, this, L_Top);
		
		MW_Trade = new MWTrade(CurrentMain, L_Top, AListener);
		
		MW_Auction = new MWAuction(CurrentMain, L_Top, AListener);
		
		MW_Offer = new MWOffer(CurrentMain, L_Top, AListener);

		Properties = new PropertyWindow(CurrentMain, LB_Left);
		
		Viewers = new ViewerWindow(CurrentMain, LB_Left);
		
		PData = new PlayerData(CurrentMain, LB_Middle);
		
		Action = new ActionField(LB_Right, AListener);
		
		UList = new UserList(CurrentMain, R_Top);
		
		MList = new MessageList(CurrentMain, R_Middle, 32);
			MListListener = new MessageListener(MList);
		MList.addMessage(CurrentMain.Colors[0], "Willkommen!");
		
		UserInput = new JTextField(20);
			UserInput.setActionCommand("USERINPUT");
			UserInput.addActionListener(AListener);
		R_Bottom.add(UserInput);
		
		
		WinRenderer = new WindowRenderer(this);
			WinRenderer.addRenderObject(MW_Logon);
			WinRenderer.addRenderObject(MW_Game);
			WinRenderer.addRenderObject(MW_Trade);
			WinRenderer.addRenderObject(MW_Auction);
			WinRenderer.addRenderObject(MW_Offer);
			WinRenderer.addRenderObject(Properties);
			WinRenderer.addRenderObject(Viewers);
			WinRenderer.addRenderObject(PData);
			WinRenderer.addRenderObject(Action);
			WinRenderer.addRenderObject(UList);
			WinRenderer.addRenderObject(MList);
		
		AnimRenderer = new AnimationRenderer(CurrentMain);
		
		setVisible(true);
		WinRenderer.start();
		AnimRenderer.start();
	}
	
	private void createLayout()
	{
		Main = new JPanel();
			Main.setLayout(new BoxLayout(Main, BoxLayout.X_AXIS));
		Left = new JPanel();
			Left.setLayout(new BoxLayout(Left, BoxLayout.Y_AXIS));
			Left.setBorder(BorderFactory.createLineBorder(Color.black));
		Right = new JPanel();
			Right.setLayout(new BoxLayout(Right, BoxLayout.Y_AXIS));
			Right.setBorder(BorderFactory.createLineBorder(Color.black));
		
		L_Top = new JPanel();
			GBL = new GridBagLayout();
			GBC = new GridBagConstraints();
			GBC.anchor = GridBagConstraints.CENTER;
			L_Top.setLayout(GBL);
			L_Top.setBorder(BorderFactory.createLineBorder(Color.black));
		L_Bottom = new JPanel();
			L_Bottom.setLayout(new BoxLayout(L_Bottom, BoxLayout.X_AXIS));
			L_Bottom.setBorder(BorderFactory.createLineBorder(Color.black));
		
		LB_Left = new JPanel();
			LB_Left.setBorder(BorderFactory.createLineBorder(Color.black));
		LB_Middle = new JPanel();
			LB_Middle.setBorder(BorderFactory.createLineBorder(Color.black));
		LB_Right = new JPanel();
			LB_Right.setBorder(BorderFactory.createLineBorder(Color.black));
		
		R_Top = new JPanel();
			R_Top.setBorder(BorderFactory.createLineBorder(Color.black));
		R_Middle = new JPanel();
			R_Middle.setBorder(BorderFactory.createLineBorder(Color.black));
		R_Bottom = new JPanel();
			R_Bottom.setBorder(BorderFactory.createLineBorder(Color.black));
		
		
	add(Main);
		Main.add(Left);
				Left.add(L_Top);
				Left.add(L_Bottom);
					L_Bottom.add(LB_Left);
					L_Bottom.add(LB_Middle);
					L_Bottom.add(LB_Right);
		Main.add(Right);
				Right.add(R_Top);
				Right.add(R_Middle);
				Right.add(R_Bottom);
	}

	public void reArrange()
	{		
		int SizeX = getWidth();
		int SizeY = getHeight();
		Main.setPreferredSize(new Dimension(SizeX, SizeY));
		Left.setPreferredSize(new Dimension(SizeX - 250, SizeY));
			L_Top.setPreferredSize(new Dimension(SizeX - 250, SizeY - 170));
			L_Bottom.setPreferredSize(new Dimension(SizeX - 250, 170));
				LB_Left.setPreferredSize(new Dimension(SizeX - 130 - 210 - 250, 170));
				LB_Middle.setPreferredSize(new Dimension(130, 170));
				LB_Right.setPreferredSize(new Dimension(210, 170));
		Right.setPreferredSize(new Dimension(250, SizeY));
			R_Top.setPreferredSize(new Dimension(250, 300));
			R_Middle.setPreferredSize(new Dimension(250, SizeY - 300 - 30));
			R_Bottom.setPreferredSize(new Dimension(250, 30));
			
		double X1, X2, XS, Y1, Y2, YS;
		X1 = (double) (L_Top.getWidth() - 4);
		X2 = 3300.0;
		XS = X1 / X2;
		
		Y1 = (double) (L_Top.getHeight() - 4);
		Y2 = 2700.0;
		YS = Y1 / Y2;
		
		if(XS < YS)
		{
			ScaleFactor = XS;
		}
		else
		{
			ScaleFactor = YS;
		}
			
		MW_Logon.reArrange();
		MW_Game.reArrange();
		AnimRenderer.reArrange();
		MW_Trade.reArrange();
		MW_Auction.reArrange();
		MW_Offer.reArrange();
		Properties.reArrange();
		Viewers.reArrange();
		PData.reArrange();
		Action.reArrange();
		UList.reArrange();
		MList.reArrange();
	}
	
	public void clearAll()
	{
		clearGame();
		SetWindow(MW_Logon);
		Action.clear();
		Action.setPanel(Action.P_Logon);
		UList.clear();
		setTitle("Was kostet die Welt?");
	}
	
	public void clearGame()
	{
		SetWindow(MW_Game);
		MW_Game.clear();
		AnimRenderer.clear();
		Properties.clear();
		Viewers.clear();
		PData.setMoney(0, true);
		PData.setJoker(0, true);
	}
	
	public void SetWindow(MainWindow MWindow)
	{
		if(Current != null)
		{
			Current.removeFromPanel();
		}
		Current = MWindow;
		GBL.setConstraints(Current.Frame, GBC);
		Current.addToPanel();
	}
}