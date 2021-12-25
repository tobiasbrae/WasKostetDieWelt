import java.util.Scanner;

public class Property
{
	Property Before, After;
	int ID, Ressource, Percent, Price, Country;
	String RessourceName, CountryName;
	
	public Property(String Data)
	{
		Scanner Line = new Scanner(Data);
		
		ID = Line.nextInt();
		Ressource = Line.nextInt();
		Percent = Line.nextInt();
		Price = Line.nextInt();
		Country = Line.nextInt();
	}
	
	public String PropertyData()
	{
		return ID + lengthString(RessourceName, 16) + lengthString(Percent, 5) + "%" + lengthString(getPointedInt(Price), 11) + "DM" + lengthString(CountryName, 20);
	}
	
	public String lengthString(int Input, int Length) { return lengthString(Input + "", Length); }	
	
	public String lengthString(String Input, int Length)
	{
		if(Input.length() > Length)
		{
			return Input.substring(0, Length);
		}
		else
		{
			while(Input.length() < Length)
			{
				Input = " " + Input;
			}
			return Input;
		}
	}
	
	public String getPointedInt(int Input)
	{
		String Number = Input + "";
		String Output = "";
		boolean startPoint = false;
		int Upper = 0;
		if(Number.length() % 3 == 2)
		{
			Output = Number.substring(0, 2);
			Upper = 2;
			startPoint = true;
		}
		else if(Number.length() % 3 == 1)
		{
			Output = Number.substring(0, 1);
			Upper = 1;
			startPoint = true;
		}
		for(int i = 0; i < Number.length() / 3; i++)
		{
			if(startPoint)
			{
				Output = Output + ".";
			}
			else
			{
				startPoint = true;
			}
			Output = Output + Number.substring(Upper + i * 3, Upper + i * 3 + 3);
		}
		return Output;
	}
}