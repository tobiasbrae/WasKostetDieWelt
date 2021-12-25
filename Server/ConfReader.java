import java.net.URL;

public class ConfReader
{
	private String Filename_String;
	private URL Filename_URL;
	private boolean IsURL;
	
	public ConfReader(String Filename)
	{
		this.Filename_String = Filename;
	}
	
	public ConfReader(URL Filename)
	{
		this.Filename_URL = Filename;
		IsURL = true;
	}
	
	public boolean getBoolean(String Key)
	{
		if(getValue(Key).equals("true")) { return true; }
		return false;
	}
	
	public int getInt(String Key)
	{
		return Integer.parseInt(getValue(Key));
	}
	
	private String getValue(String Key)
	{
		Reader Data;
		
		if(IsURL)
		{
			Data = new Reader(Filename_URL);
		}
		else
		{
			Data = new Reader(Filename_String);
		}
		
		Key = Key + "=";
		while(Data.hasNextLine())
		{
			String Line = Data.nextLine();
			if(Line.startsWith(Key))
			{
				Line = Line.substring(Key.length());
				return Line;
			}
		}
		return "";
	}
}