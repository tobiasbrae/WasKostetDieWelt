public class Gamer
{
	Gamer Before, After;
	PropertyList Properties;
	int Money, Joker, DiceRed, DiceWhite, DiceFull, Color, Round = 1, Counter;
	boolean isInAuction;
	Connection Con;
	Field CurrentField;
	
	public Gamer(Server CurrentServer, Connection Con)
	{
		this.Con = Con;
		Properties = new PropertyList(CurrentServer);
	}
	
	public void roll()
	{
		double Base = Math.random() * 6.0 + 1.0;
		DiceRed = (int) (Base);
		
		Base = Math.random() * 6.0 + 1.0;
		DiceWhite = (int) (Base);
		
		DiceFull = DiceRed + DiceWhite;
		
		Con.sendMessage("SETDICES " + DiceRed + " " + DiceWhite);
		Con.sendMessage("PLAYSOUND 1");
	}
	
	public void changeMoney(int Amount)
	{
		Money += Amount;
		Con.sendMessage("SETMONEY " + Money);
		Con.CurrentServer.ConList.broadcastexPlayers("SETPLAYERDATA 2 " + Con.ID + " " + Money);
	}
	
	public void changeJoker(int Amount)
	{
		Joker += Amount;
		Con.sendMessage("SETJOKER " + Joker);
	}
	
	public boolean hasJoker()
	{
		if(Joker > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void sendPrice(int CurrentAction, int Price)
	{
		if(CurrentAction == 1)
		{
			if(Price > Money)
			{
				Con.sendMessage("SETPRICE False " + Price);
			}
			else
			{
				Con.sendMessage("SETPRICE True " + Price);
			}
		}
		else if(CurrentAction == 2)
		{
			if(Price > 0)
			{
				Con.sendMessage("SETPRICE True " + Price);
			}
			else
			{
				Con.sendMessage("SETPRICE False " + Price);
			}
		}
	}
	
	public int getMaxAuction(int Value)
	{
		if(Properties.First == null)
		{
			return 0;
		}
		else if(Properties.First == Properties.Last)
		{
			return 1;
		}
		else
		{
			int Counter = 0;
			Property CurProperty = Properties.First;
			while(CurProperty != null)
			{
				Counter++;
				if(Counter == Value) { return Counter; }
				CurProperty = CurProperty.After;
			}
			return Counter;
		}
	}
	
	public boolean hasProperty(int Ressource)
	{
		Property CurProperty = Properties.First;
		while(CurProperty != null)
		{
			if(CurProperty.Ressource == Ressource)
			{
				return true;
			}
			CurProperty = CurProperty.After;
		}
		return false;
	}
}