import java.util.Scanner;

public class PropertyList
{
	Server CurrentServer;
	Property First, Last;
	int[][] Properties;
	String[] RessourceNames, CountryNames;
	int Price;
	int Counter;
	int[][] Prices;
	
	public PropertyList(Server CurrentServer)
	{
		this.CurrentServer = CurrentServer;
		Properties = new int[25][2];
	}
	
	public void loadFromFile()
	{
		CurrentServer.display_("Loading ressources...");
		RessourceNames = new String[25];
		Prices = new int[25][4];
		Reader Data = new Reader(CurrentServer.URLs[5]);
		while(Data.hasNextLine())
		{
			Scanner CurrentLine = new Scanner(Data.nextLine());
			int ID = CurrentLine.nextInt();
			RessourceNames[ID] = CurrentLine.next();
			Prices[ID][0] = CurrentLine.nextInt();
			Prices[ID][1] = CurrentLine.nextInt();
			Prices[ID][2] = CurrentLine.nextInt();
			Prices[ID][3] = CurrentLine.nextInt();
		}
		CurrentServer._display("done.");
		
		CurrentServer.display_("Loading fieldnames...");
		CountryNames = new String[30];
		Data = new Reader(CurrentServer.URLs[3]);
		while(Data.hasNextLine())
		{
			Scanner CurrentLine = new Scanner(Data.nextLine());
			CurrentLine.next();
			int CurrentID = CurrentLine.nextInt();
			if(CurrentID < 40)
			{
				CurrentLine.next();
				String Name = CurrentLine.next();
				if(CurrentLine.hasNext()) { Name = Name + " " + CurrentLine.next(); }
				CountryNames[CurrentID] = Name;
			}
		}
		CurrentServer._display("done.");

		Data = new Reader(CurrentServer.URLs[4]);
		CurrentServer.display_("Loading properties...");
		while(Data.hasNextLine())
		{
			Property Input = new Property(Data.nextLine());
			Input.RessourceName = RessourceNames[Input.Ressource];
			Input.CountryName = CountryNames[Input.Country];
			addProperty(Input);
		}
		CurrentServer._display("done.");
	}
	
	public int getPrice(int Ressource, int Percent)
	{
		if(Percent >= 90) { return Prices[Ressource][3]; }
		else if(Percent >= 70) { return Prices[Ressource][2]; }
		else if(Percent >= 50) { return Prices[Ressource][1]; }
		else if(Percent >= 30) { return Prices[Ressource][0]; }
		else { return 0; }
	}
	
	public void addProperty(Property Input)
	{
		Counter++;
		Price += Input.Price;
		Properties[Input.Ressource][0] += Input.Percent;
		Properties[Input.Ressource][1] = CurrentServer.CurrentGame.Bank.getPrice(Input.Ressource, Properties[Input.Ressource][0]);
		
		if(First == null)
		{
			First = Input;
			Last = Input;
		}
		else if(First == Last)
		{
			if(compareStrings(Input.RessourceName, First.RessourceName, 4) < 2)
			{
				First.Before = Input;
				Input.After = First;
				First = Input;
			}
			else
			{
				Last.After = Input;
				Input.Before = Last;
				Last = Input;
			}
		}
		else
		{
			Property CurProperty = First;
			while(CurProperty != null)
			{
				if(compareStrings(Input.RessourceName, CurProperty.RessourceName, 4) < 2)
				{
					if(CurProperty == First)
					{
						First.Before = Input;
						Input.After = First;
						First = Input;
					}
					else
					{
						Input.Before = CurProperty.Before;
						Input.After = CurProperty;
						Input.Before.After = Input;
						Input.After.Before = Input;
					}
					break;
				}
				else if(CurProperty == Last)
				{
					Last.After = Input;
					Input.Before = Last;
					Last = Input;
					break;
				}
				CurProperty = CurProperty.After;
			}
		}
	}
	
	public int compareStrings(String Input1, String Input2, int Depth)
	{
		if(Input1.equals(Input2))
		{
			return 0;
		}
		else
		{
			int Counter = 0;
			while(Counter < Depth)
			{
				if(Counter == Input1.length() || Counter == Input2.length())
				{
					return 0;
				}				
				else if(Input1.codePointAt(Counter) > Input2.codePointAt(Counter))
				{
					return 2;
				}
				else if(Input1.codePointAt(Counter) < Input2.codePointAt(Counter))
				{
					return 1;
				}
				Counter++;
			}
		}
		return 0;
	}
	
	public void moveToList(PropertyList List, boolean isTraded, String Trade)
	{
		while(First != null)
		{
			Property CurProperty = removeProperty(First);
			List.addProperty(CurProperty);
			if(isTraded)
			{
				CurrentServer.display(CurrentServer.CurrentGame.CurrentGamer.Con.Username + " " + Trade + " " + CurProperty.PropertyData());
				int Percent = CurrentServer.CurrentGame.CurrentGamer.Properties.Properties[CurProperty.Ressource][0];
				int Price = CurrentServer.CurrentGame.CurrentGamer.Properties.Properties[CurProperty.Ressource][1];
				CurrentServer.CurrentGame.CurrentGamer.Con.sendMessage("SETPROPERTY " + CurProperty.Ressource + " " + CurProperty.RessourceName + " " +  Percent + " " + Price);
			}
		}
		Price = 0;
	}
	
	public Property removeProperty(int ID)
	{
		return removeProperty(getProperty(ID));
	}
	
	public Property removeProperty(Property CurProperty)
	{
		Counter--;
		if(CurProperty != null)
		{
			Price -= CurProperty.Price;
			Properties[CurProperty.Ressource][0] -= CurProperty.Percent;
			Properties[CurProperty.Ressource][1] = CurrentServer.CurrentGame.Bank.getPrice(CurProperty.Ressource, Properties[CurProperty.Ressource][0]);
			
			if(First == Last)
			{
				First = null;
				Last = null;
			}			
			else if(CurProperty == First)
			{
				CurProperty.After.Before = null;
				First = CurProperty.After;
			}
			else if(CurProperty == Last)
			{
				CurProperty.Before.After = null;
				Last = CurProperty.Before;
			} 
			else
			{
				CurProperty.Before.After = CurProperty.After;
				CurProperty.After.Before = CurProperty.Before;
			}
			CurProperty.Before = null;
			CurProperty.After = null;
		}
		return CurProperty;
	}
	
	private Property getProperty(int ID)
	{
		Property CurProperty = First;
		while(CurProperty != null)
		{
			if(CurProperty.ID == ID)
			{
				return CurProperty;
			}
			CurProperty = CurProperty.After;
		}
		return null;
	}
	
	public boolean hasPropertiesByCountry(int CountryID)
	{
		Property CurProperty = First;
		while(CurProperty != null)
		{
			if(CurProperty.Country == CountryID)
			{
				return true;
			}
			CurProperty = CurProperty.After;
		}
		return false;
	}
	
	public boolean hasPropertiesBySelect(int SelectID)
	{
		Property CurProperty = First;
		while(CurProperty != null)
		{
			if(isInSelect(CurProperty, SelectID))
			{
				return true;
			}
			CurProperty = CurProperty.After;
		}
		return false;
	}
	
	public boolean hasPropertiesByWorld()
	{
		if(First != null) { return true; }
		return false;
	}
	
	public void sendProperties(Gamer CurGamer)
	{
		if(CurGamer.CurrentField.Type == 2) //Country
		{
			sendPropertiesByCountry(CurGamer.Con, CurGamer.CurrentField.CountryID);
		}
		else if(CurGamer.CurrentField.Type == 3) //Select
		{
			sendPropertiesBySelect(CurGamer.Con, CurGamer.CurrentField.CountryID - 40);
		}
		else if(CurGamer.CurrentField.Type == 4) //Select World
		{
			sendPropertiesByWorld(CurGamer.Con);
		}
	}
	
	public void sendAllProperties(Gamer CurGamer)
	{
		sendPropertiesByWorld(CurGamer.Con);
	}
	
	private void sendPropertiesByCountry(Connection Con, int Country)
	{
		Property CurProperty = First;
		while(CurProperty != null)
		{
			if(CurProperty.Country == Country)
			{
				Con.sendMessage("ADDPROPERTY " + CurProperty.PropertyData());
			}
			CurProperty = CurProperty.After;
		}
	}
	
	private void sendPropertiesBySelect(Connection Con, int Select)
	{
		Property CurProperty = First;
		while(CurProperty != null)
		{
			if(isInSelect(CurProperty, Select))
			{
				Con.sendMessage("ADDPROPERTY " + CurProperty.PropertyData());
			}
			CurProperty = CurProperty.After;
		}
	}
	
	private boolean isInSelect(Property CurProperty, int Select)
	{
		if(Select == 1 && CurProperty.Country > 0 && CurProperty.Country < 8)
		{
			return true;
		}
		else if(Select == 2 && CurProperty.Country > 8 && CurProperty.Country < 16)
		{
			return true;
		}
		else if(Select == 3 && CurProperty.Country > 16 && CurProperty.Country < 23)
		{
			return true;
		}
		else if(Select == 4 && CurProperty.Country > 22 && CurProperty.Country < 30)
		{
			return true;
		}
		return false;
	}
	
	private void sendPropertiesByWorld(Connection Con)
	{
		Property CurProperty = First;
		while(CurProperty != null)
		{
			Con.sendMessage("ADDPROPERTY " + CurProperty.PropertyData());
			CurProperty = CurProperty.After;
		}
	}
}