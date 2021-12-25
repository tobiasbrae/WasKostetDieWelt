import java.util.Scanner;

public class Field
{
	int FieldID, CountryID, Type, Property;
	String Name;
	Field Before, After;
	
	public Field(String Data)
	{
		Scanner Line = new Scanner(Data);
		
		FieldID = Line.nextInt();
		CountryID = Line.nextInt();
		Type = Line.nextInt();
		Name = Line.next();
		if(Line.hasNext()) { Name = Name + " " + Line.next(); }
	}
}