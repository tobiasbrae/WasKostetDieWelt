import java.util.Scanner;
import java.io.File; 
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class Reader
{
	private Scanner Data;
	private String CurrentLine;
	private boolean HasNextLine;
	
	public Reader(String Filename)
	{
		try
		{
			Data = new Scanner(new File(Filename));
			getNextLine();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Error: " + e);
		}
	}
	
	public Reader(URL Filename)
	{
		try
		{
			Data = new Scanner(Filename.openStream());
			getNextLine();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Error: " + e);
		}
		catch(IOException e2)
		{
			System.out.println(e2.toString());
		}
	}
	
	private void getNextLine()
	{
		if(Data.hasNextLine())
		{
			while(Data.hasNextLine())
			{
				CurrentLine = Data.nextLine();
				if(CurrentLine.charAt(0) == ';')
				{
					if(!Data.hasNextLine())
					{
						CurrentLine = "";
						HasNextLine = false;
					}
				}
				else
				{
					HasNextLine = true;
					break;
				}
			}
		}
		else
		{
			CurrentLine = "";
			HasNextLine = false;
		}
	}
	
	public boolean hasNextLine()
	{
		return HasNextLine;
	}
	
	public String nextLine()
	{
		String Output = CurrentLine;
		getNextLine();
		return Output;
	}
}