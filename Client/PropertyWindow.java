import java.util.Scanner;
import java.awt.*;
import javax.swing.*;

public class PropertyWindow extends RenderObject
{
	private Main CurrentMain;
	private JPanel Main;
	private JPanel Content;
	private JScrollPane Pane;
	private PropertyData First, Last;
	
	public PropertyWindow(Main CurrentMain, JPanel Main)
	{
		super(Main);
		this.CurrentMain = CurrentMain;
		this.Main = Main;
		
		Content = new JPanel();
		Content.setLayout(new GridLayout(0, 2));
		
		Pane = new JScrollPane(Content, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		Main.setLayout(new BorderLayout());
	}
	
	public void addToPanel()
	{
		Main.add(Pane);
		NeedRepaint = true;
	}
	
	public void removeFromPanel()
	{
		Main.remove(Pane);
	}
	
	public void reArrange()
	{
		Pane.setPreferredSize(Main.getSize());
	}
	
	public void setProperty(String Data)
	{
		Scanner Line = new Scanner(Data);
		int Ressource = Line.nextInt();
		String RessourceName = Line.next();
		int Percent = Line.nextInt();
		int Price = Line.nextInt();
		
		PropertyData Current = getPropertyData(Ressource);
		if(Current == null)
		{
			if(Percent == 0)
			{
				System.out.println("Anteilsschein hat 0%");
			}
			else
			{
				addPropertyData(new PropertyData(Ressource, RessourceName, Percent, Price));
			}
		}
		else
		{
			Current.setValues(Percent, Price);
		}
		NeedRepaint = true;
	}
	
	private PropertyData getPropertyData(int Ressource)
	{
		PropertyData Current = First;
		while(Current != null)
		{
			if(Current.getRessource() == Ressource)
			{
				return Current;
			}
			else
			{
				Current = Current.After;
			}
		}
		
		return null;
	}
	
	private void addPropertyData(PropertyData Input)
	{
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
			PropertyData CurPropData = First;
			while(CurPropData != null)
			{
				if(compareStrings(Input.RessourceName, CurPropData.RessourceName, 4) < 2)
				{
					if(CurPropData == First)
					{
						First.Before = Input;
						Input.After = First;
						First = Input;
					}
					else
					{
						Input.Before = CurPropData.Before;
						Input.After = CurPropData;
						Input.Before.After = Input;
						Input.After.Before = Input;
					}
					break;
				}
				else if(CurPropData == Last)
				{
					Last.After = Input;
					Input.Before = Last;
					Last = Input;
					break;
				}
				CurPropData = CurPropData.After;
			}
		}
		reloadContent();
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
	
	public void markProperty(int Ressource, boolean Mark)
	{
		getPropertyData(Ressource).setMarked(Mark);
		NeedRepaint = true;
	}
	
	private void removePropertyData(PropertyData Input)
	{
		if(First == Last)
		{
			First = null;
			Last = null;
		}		
		else if(First == Input)
		{
			First.After.Before = null;
			First = First.After;
		}
		else if(Last == Input)
		{
			Last.Before.After = null;
			Last = Last.Before;
		}
		else
		{
			Input.Before.After = Input.After;
			Input.After.Before = Input.Before;
		}
		reloadContent();
	}
	
	public void reloadContent()
	{
		Content.removeAll();
		if(First != null && First == Last)
		{
			Content.add(First);
		}
		else
		{
			PropertyData CurPropData = First;
			while(CurPropData != null)
			{
				Content.add(CurPropData);
				CurPropData = CurPropData.After;
			}
		}
	}
	
	private class PropertyData extends JPanel
	{
		public PropertyData Before, After;
		private int Ressource, Percent, Price;
		private JLabel L_Ressource, L_Percent, L_Price;
		private String RessourceName;
		
		public PropertyData(int Ressource, String RessourceName, int Percent, int Price)
		{
			this.Ressource = Ressource;
			this.RessourceName = RessourceName;
			this.Percent = Percent;
			this.Price = Price;
			
			setLayout(new GridLayout(0, 3));
			L_Ressource = new JLabel(CurrentMain.lengthString(false, RessourceName, 16));
				this.add(L_Ressource);
			L_Percent = new JLabel(Percent + "%");
				this.add(L_Percent);
			L_Price = new JLabel(CurrentMain.getPointedInt(Price) + "DM");
				this.add(L_Price);
		}
		
		public void setValues(int Percent, int Price)
		{
			if(Percent == 0)
			{
				removePropertyData(this);
			}
			else
			{
				this.Percent = Percent;
				this.Price = Price;
				L_Percent.setText(Percent + "%");
				L_Price.setText(CurrentMain.getPointedInt(Price) + "DM");
			}
		}
		
		public void setMarked(boolean Mark)
		{
			if(Mark)
			{
				L_Ressource.setForeground(Color.red);
				L_Percent.setForeground(Color.red);
				L_Price.setForeground(Color.red);
			}
			else
			{
				L_Ressource.setForeground(Color.black);
				L_Percent.setForeground(Color.black);
				L_Price.setForeground(Color.black);
			}
		}
		
		public int getRessource() { return Ressource; }
	}
	
	public void clear()
	{
		First = null;
		Last = null;
		Content.removeAll();
	}
}