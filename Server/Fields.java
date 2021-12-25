import java.util.Scanner;

public class Fields
{
	Field First, Last;
	Server CurrentServer;
	int[] Properties;

	public Fields(Server CurrentServer)
	{
		this.CurrentServer = CurrentServer;
		CurrentServer.display_("Loading fields...");
		loadProperties();
		addFields();
		CurrentServer._display("done.");
	}
	
	public void loadProperties()
	{			
		Properties = new int[48];
		
		for(int i = 1; i < 25; i++)
		{
			Properties[getFreePlace()] = i;
			Properties[getFreePlace()] = i;
		}
	}
	
	public int getFreePlace()
	{
		int Place = 0;
		while(Properties[Place] != 0)
		{
			double Number = Math.random() * 48.0;
			Place = (int) Number;
		}
		return Place;
	}
	
	public void addFields()
	{
		Reader Data = new Reader(CurrentServer.URLs[3]);
		int Counter = 0;
		while(Data.hasNextLine())
		{
			addField(new Field(Data.nextLine()));
			
			if(Last.Type == 2 || Last.Type == 3 || Last.Type == 6)
			{
				Last.Property = Properties[Counter]; // 2 3 6
				Counter++;
			}
		}
	}
	
	public void sendRessources(Connection Con)
	{
		Field CurField = First;
		while(CurField != null)
		{
			if(CurField.Type == 2 || CurField.Type == 3 || CurField.Type == 6)
			{
				Con.sendMessage("DRAWRESSOURCE " + CurField.FieldID + " " + CurField.Property);
			}
			CurField = CurField.After;
		}
	}
	
	public void addField(Field field)
	{
		if(First == null)
		{
			First = field;
			Last = field;
		}
		else if(First == Last)
		{
			Last = field;
			First.After = Last;
			Last.Before = First;
		}
		else
		{
			Last.After = field;
			field.Before = field;
			Last = field;
		}
	}
	
	public Field giveNextField(Field CurField, int Steps)
	{
		for(int i = 0; i < Steps; i++)
		{
			if(CurField != Last)
			{
				CurField = CurField.After;
			}
			else
			{
				CurField = First.After;
			}
		}
		return CurField;
	}

}