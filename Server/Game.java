import java.util.Scanner;

public class Game
{
	Server CurrentServer;
	Gamer First, Last, CurrentGamer, CurrentOfferer;
	Fields CurrentFields;
	PropertyList Bank, Cart;
	Property AuctionItem;
	int Dice, Amount, CurrentAction, CurrentOffer, Auctions, AuctionHigher;
	private Reader PlayerColors;
	boolean GameRuns, AuctionStarted;
	
	public Game(Server CurrentServer)
	{
		this.CurrentServer = CurrentServer;
	}
	
	public void startGame()
	{
		loadData();
		addPlayers();
		CurrentServer.ConList.sendStart();
		CurrentServer.display("All players have to roll.");
	}
	
	public void loadData()
	{
		CurrentFields = new Fields(CurrentServer);

		Bank = new PropertyList(CurrentServer);
			Bank.loadFromFile();
		Cart = new PropertyList(CurrentServer);
	}
	
	private void addPlayers()
	{
		Connection CurrentCon = CurrentServer.ConList.First;
		while(CurrentCon != null)
		{
			if(CurrentCon.isPlayer)
			{
				addPlayer(CurrentCon);
				CurrentServer.display(CurrentCon.Username + " plays.");
			}
			CurrentCon = CurrentCon.After;
		}
	}
	
	private void addPlayer(Connection Con)
	{
		Con.setReadyState(1);
		
		Con.setColor(giveNextColor());
		CurrentServer.ConList.broadcast("ADDPLAYERFIGURE " + Con.ID + " " + Con.Col);
		CurrentServer.ConList.broadcastexPlayers("SETPLAYERDATA 0 " + Con.ID + " " + Con.Username + " 0 " + CurrentServer.StartMoney + " " + Con.Col);
				
		Gamer NewGamer = new Gamer(CurrentServer, Con);
		NewGamer.CurrentField = CurrentFields.First;
		NewGamer.changeMoney(CurrentServer.StartMoney);
		NewGamer.changeJoker(CurrentServer.StartJoker);
		
		if(First == null)
		{
			First = NewGamer;
			Last = NewGamer;
		}
		else
		{
			Last.After = NewGamer;
			NewGamer.Before = Last;
			Last = NewGamer;
		}
	}
	
	private int giveNextColor()
	{
		if(PlayerColors == null || !PlayerColors.hasNextLine())
		{
			PlayerColors = new Reader(CurrentServer.URLs[2]);
		}
		Scanner Input = new Scanner(PlayerColors.nextLine());
		return Input.nextInt();
	}
	
	public void roll(Connection Con)
	{		
		if(GameRuns)
		{
			if(CurrentGamer.Con == Con && CurrentGamer.DiceRed == 0)
			{
				disableButtons();
				CurrentGamer.roll();
				CurrentServer.ConList.broadcastexPlayers("SETDICES " + CurrentGamer.DiceRed + " " + CurrentGamer.DiceWhite);
				CurrentServer.ConList.broadcastexPlayers("PLAYSOUND 1");
				CurrentServer.ConList.broadcastEx(Con, "MESSAGE " + CurrentServer.Colors[3] + " " + Con.Username + " hat eine " + CurrentGamer.DiceFull + " gewürfelt.");
				Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast eine " + CurrentGamer.DiceFull + " gewürfelt.");
				CurrentServer.display(Con.Username + " rolled a " + CurrentGamer.DiceFull + ". (red: " + CurrentGamer.DiceRed + "  white: " + CurrentGamer.DiceWhite + ")");
				
				int LastID = CurrentGamer.CurrentField.FieldID;
				CurrentGamer.CurrentField = CurrentFields.giveNextField(CurrentGamer.CurrentField, CurrentGamer.DiceFull);
				if(LastID > CurrentGamer.CurrentField.FieldID)
				{
					CurrentGamer.Round++;
				}
				CurrentServer.ConList.broadcast("MOVEPLAYERFIGURE " + CurrentGamer.Con.ID + " " + CurrentGamer.CurrentField.FieldID);
				
				String Property = "";
				if(CurrentGamer.CurrentField.Property > 0) { Property =  " (" + Bank.RessourceNames[CurrentGamer.CurrentField.Property] + ")"; }
				CurrentServer.ConList.broadcastEx(Con, "MESSAGE " + CurrentServer.Colors[3] + " " + Con.Username + " ist auf das Feld " + CurrentGamer.CurrentField.Name + Property + " vorgerückt.");
				Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du bist auf das Feld " + CurrentGamer.CurrentField.Name + Property + " vorgerückt.");
				CurrentServer.display(Con.Username + " goes to " + CurrentGamer.CurrentField.Name + Property + ".");
				
				
				if(CurrentGamer.CurrentField.Type == 2) //Country
				{
					if(!Bank.hasPropertiesByCountry(CurrentGamer.CurrentField.CountryID))
					{
						CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Es sind keine Anteilsscheine mehr vorhanden.");
					}
				}
				else if(CurrentGamer.CurrentField.Type == 3) //Select
				{
					if(!Bank.hasPropertiesBySelect(CurrentGamer.CurrentField.CountryID - 40))
					{
						CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Es sind keine Anteilsscheine mehr vorhanden.");
					}
				}
				else if(CurrentGamer.CurrentField.Type == 4) //Select World
				{
					if(!Bank.hasPropertiesByWorld())
					{
						CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Es sind keine Anteilsscheine mehr vorhanden.");
					}
				}
				else if(CurrentGamer.CurrentField.Type == 5) //Auktion
				{
					CurrentServer.ConList.broadcast("MESSAGE " + CurrentServer.Colors[3] + " Auktion!");
					CurrentServer.display("Auction!");
					doAuction();
				}
				else if(CurrentGamer.CurrentField.Type == 6) //Bonus
				{
					CurrentGamer.changeMoney(CurrentGamer.DiceFull * CurrentServer.BonusMoney);
					Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Bonus! " + getPointedInt(CurrentGamer.DiceFull * CurrentServer.BonusMoney) + "DM gewonnen.");
					CurrentServer.ConList.broadcastEx(Con, "MESSAGE " + CurrentServer.Colors[3] + " Bonus! " + Con.Username + " hat " + getPointedInt(CurrentGamer.DiceFull * CurrentServer.BonusMoney) + "DM gewonnen.");
					CurrentServer.display("Bonus! " + Con.Username + " gained " + getPointedInt(CurrentGamer.DiceFull * CurrentServer.BonusMoney) + "DM.");
				}
				else if(CurrentGamer.CurrentField.Type == 7) //Telex
				{
					if(Math.random() < CurrentServer.AuctionChance)
					{
						CurrentServer.ConList.broadcast("MESSAGE " + CurrentServer.Colors[3] + " Telex -> Auktion!");
						CurrentServer.display("Telex -> Auction!");
						doAuction();
					}
					else
					{
						CurrentGamer.changeMoney(CurrentGamer.DiceFull * CurrentServer.BonusMoney);
						Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Telex -> Bonus! " + getPointedInt(CurrentGamer.DiceFull * CurrentServer.BonusMoney) + "DM gewonnen.");
						CurrentServer.ConList.broadcastEx(Con, "MESSAGE " + CurrentServer.Colors[3] + " Telex -> Bonus! " + Con.Username + " hat " + getPointedInt(CurrentGamer.DiceFull * CurrentServer.BonusMoney) + "DM gewonnen.");
						CurrentServer.display("Telex -> Bonus! " + Con.Username + " gained " + getPointedInt(CurrentGamer.DiceFull * CurrentServer.BonusMoney) + "DM.");
					}
				}
				else if(CurrentGamer.CurrentField.Type == 8) //Joker
				{
					CurrentGamer.changeJoker(1);
					Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast einen Joker erhalten.");
					CurrentServer.ConList.broadcastEx(Con, "MESSAGE " + CurrentServer.Colors[3] + " " + Con.Username + " hat einen Joker erhalten.");
					CurrentServer.display(Con.Username + " gained a joker.");
				}
				
				if(CurrentGamer.CurrentField.Property > 0)
				{
					Gamer CurGamer = First;
					while(CurGamer != null)
					{
						if(CurGamer != CurrentGamer)
						{
							int Price = CurGamer.Properties.Properties[CurrentGamer.CurrentField.Property][1];
							
							if(Price > 0)
							{
								CurrentGamer.changeMoney(-1 * Price);
								CurGamer.changeMoney(Price);
								Connection CurCon = CurrentServer.ConList.First;
								while(CurCon != null)
								{
									if(CurCon.isPlayer)
									{
										Gamer ConGamer = getGamer(CurCon);
										if(ConGamer == CurrentGamer)
										{
											CurCon.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast " + getPointedInt(Price) + "DM an " + CurGamer.Con.Username + " gezahlt."); 
										}
										else if(ConGamer == CurGamer)
										{
											CurCon.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " " + Con.Username + " hat " + getPointedInt(Price) + "DM an dich gezahlt.");
										}
										else
										{
											CurCon.sendMessage("MESSAGE " + CurrentServer.Colors[3] + " " + Con.Username + " hat " + getPointedInt(Price) + "DM an " + CurGamer.Con.Username + " gezahlt.");
										}
									}
									else
									{
										CurCon.sendMessage("MESSAGE " + CurrentServer.Colors[3] + " " + Con.Username + " hat " + getPointedInt(Price) + "DM an " + CurGamer.Con.Username + " gezahlt.");
									}
									CurCon = CurCon.After;
								} 
								CurrentServer.display(Con.Username + " payed " + getPointedInt(Price) + "DM to " + CurGamer.Con.Username + ".");
							}
						}
						CurGamer = CurGamer.After;
					}
				}
				enableButtons();
			}
		}
		else
		{
			Gamer CurGamer = getGamer(Con);
			
			if(CurGamer.DiceRed == 0)
			{
				Con.sendMessage("SETACTION 0 False");
				CurGamer.roll();
				CurrentServer.ConList.broadcastEx(Con, "MESSAGE " + CurrentServer.Colors[3] + " " + Con.Username + " hat eine " + CurGamer.DiceFull + " gewürfelt.");
				Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast eine " + CurGamer.DiceFull + " gewürfelt.");
				CurrentServer.display(Con.Username + " rolled a " + CurGamer.DiceFull + ". (red: " + CurGamer.DiceRed + "  white: " + CurGamer.DiceWhite + ")");
				
				if(CurGamer.DiceFull > Dice)
				{
					Dice = CurGamer.DiceFull;
					Amount = 1;
				}
				else if(CurGamer.DiceFull == Dice)
				{
					Amount++;
				}
			
				if(allPlayersHaveRolled())
				{
					if(Amount > 1)
					{
						CurrentServer.display("The players have to roll again.");
						CurGamer = First;
						
						while(CurGamer != null)
						{
							if(CurGamer.DiceFull == Dice)
							{
								CurGamer.Con.sendMessage("SETACTION  0 True");
								CurrentServer.ConList.broadcastEx(CurGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurGamer.Con.Username + " muss noch einmal würfeln.");
								CurGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du musst noch einmal würfeln.");
								CurrentServer.display(CurGamer.Con.Username + " has to roll again.");
								CurGamer.DiceRed = 0;
							}
							else
							{
								CurGamer.DiceRed = -1;
							}
							CurGamer = CurGamer.After;
						}
						Dice = 0;
						Amount = 0;
					}
					else
					{
						GameRuns = true;
						CurrentServer.display("The game begins!");
						CurrentServer.ConList.broadcast("PLAYSOUND 6");
						setCurrentPlayer(getHighRoller());
					}
				}
			}
		}
	}
	
	private void doAuction()
	{
		if(CurrentGamer.Round > 1)
		{		
			if(CurrentServer.ConList.countPlayers() < 3)
			{
				if(CurrentGamer.hasJoker())
				{
					CurrentGamer.changeJoker(-1);
					CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[3] + " Für eine Auktion werden mindestens 2 Mitbieter benötigt. Dein Joker wurde gesetzt.");
					CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " Für eine Auktion werden mindestens 2 Mitbieter benötigt. " + CurrentGamer.Con.Username + " wurde ein Joker abgenommen.");
					CurrentServer.display("For an auction there have to be at least 2 offerers. " + CurrentGamer.Con.Username + " got ripped of a joker.");
				}
				else
				{
					int AuctionPrice = CurrentGamer.DiceRed * CurrentServer.AuctionMoney;
					CurrentGamer.changeMoney(-1 * AuctionPrice);
					CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[3] + " Für eine Auktion werden mindestens 2 Mitbieter benötigt. Du musst " + getPointedInt(AuctionPrice) + "DM zahlen.");
					CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " Für eine Auktion werden mindestens 2 Mitbieter benötigt. " + CurrentGamer.Con.Username + " muss " + getPointedInt(AuctionPrice) + "DM zahlen.");
					CurrentServer.display("For an auction there have to be at least 2 offerers. " + CurrentGamer.Con.Username + " has to pay " + getPointedInt(AuctionPrice) + "DM.");
				}
			}
			else if(CurrentGamer.getMaxAuction(CurrentGamer.DiceRed) == 0)
			{
				int AuctionPrice = CurrentGamer.DiceRed * CurrentServer.AuctionMoney;
				CurrentGamer.changeMoney(-1 * AuctionPrice);
				CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du besitzt keine Anteilsscheine. Du musst " + getPointedInt(AuctionPrice) + "DM zahlen.");
				CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurrentGamer.Con.Username + " besitzt keine Anteilsscheine. Er muss " + getPointedInt(AuctionPrice) + "DM zahlen.");
				CurrentServer.display(CurrentGamer.Con.Username + " does not own any properties. He has to pay " + getPointedInt(AuctionPrice) + "DM.");
			}
			else
			{
				Auctions = CurrentGamer.getMaxAuction(CurrentGamer.DiceRed);
				String Auct = "Anteilsschein";
				String Auct2 = "property";
				if(Auctions > 1) { Auct = "Anteilsscheine"; Auct2 = "properties";}
				if(Auctions < CurrentGamer.DiceRed)
				{
					int AuctionPrice = (CurrentGamer.DiceRed - Auctions) * CurrentServer.AuctionMoney;
					CurrentGamer.changeMoney(-1 * AuctionPrice);
					CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du besitzt nur " + Auctions + " " + Auct + ". Du musst " + getPointedInt(AuctionPrice) + "DM zahlen.");
					CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurrentGamer.Con.Username + " besitzt nur " + Auctions + " " + Auct + ". Er muss " + getPointedInt(AuctionPrice) + "DM zahlen.");
					CurrentServer.display(CurrentGamer.Con.Username + " owns only " + Auctions + " " + Auct2 + ". He has to pay " + getPointedInt(AuctionPrice) + "DM.");
				}
				CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurrentGamer.Con.Username + " muss " + Auctions + " " + Auct + " versteigern.");
				CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du musst " + Auctions + " " + Auct + " versteigern.");
				CurrentServer.display(CurrentGamer.Con.Username + " has to auction " + Auctions + " " + Auct2 + ".");
			}
		}
		else
		{
			CurrentServer.ConList.broadcast("MESSAGE " + CurrentServer.Colors[3] + " Gilt erst ab der zweiten Runde.");
			CurrentServer.display("Not before the second round.");
		}
	}
	
	private boolean allPlayersHaveRolled()
	{
		Gamer CurGamer = First;
		while(CurGamer != null)
		{
			if(CurGamer.DiceRed == 0)
			{
				return false;
			}
			CurGamer = CurGamer.After;
		}
		return true;
	}
	
	private Gamer getHighRoller()
	{
		Gamer CurGamer = First;
		while(CurGamer != null)
		{
			if(CurGamer.DiceFull == Dice)
			{
				return CurGamer;
			}
			CurGamer = CurGamer.After;
		}
		return null;
	}
	
	public void disableButtons()
	{
		for(int i = 0; i < 5; i++)
		{
			CurrentGamer.Con.sendMessage("SETACTION " + i + " False");
		}
	}
	
	public void enableButtons()
	{
		if(Auctions == 0 && CurrentGamer.Money >= 0)
		{
			CurrentGamer.Con.sendMessage("SETACTION 1 True");
		}
		if(CurrentGamer.Money >= 0)
		{
			if(CurrentGamer.CurrentField.Type == 2) //Land
			{
				if(Bank.hasPropertiesByCountry(CurrentGamer.CurrentField.CountryID))
				{
					CurrentGamer.Con.sendMessage("SETACTION 2 True");
				}
			}
			else if(CurrentGamer.CurrentField.Type == 3) //Auswahl
			{
				if(Bank.hasPropertiesBySelect(CurrentGamer.CurrentField.CountryID - 40))
				{
					CurrentGamer.Con.sendMessage("SETACTION 2 True");
				}
			}
			else if(CurrentGamer.CurrentField.Type == 4) //Auswahl Welt
			{
				if(Bank.hasPropertiesByWorld())
				{
					CurrentGamer.Con.sendMessage("SETACTION 2 True");
				}
			}
		}
		if(CurrentGamer.Properties.hasPropertiesByWorld() && CurrentServer.ConList.countPlayers() > 2)
		{
			CurrentGamer.Con.sendMessage("SETACTION 3 True");
		}
		if(CurrentGamer.Properties.hasPropertiesByWorld())
		{
			CurrentGamer.Con.sendMessage("SETACTION 4 True");
		}
	}
	
	public void startAction(Connection Con, int Action)
	{
		if(CurrentAction == 0 && CurrentGamer.Con == Con)
		{
			CurrentAction = Action;
			
			if(Action == 1)
			{
				Con.sendMessage("SHOWTRADE True");
				disableButtons();
				Bank.sendProperties(CurrentGamer);
				CurrentServer.display(Con.Username + " starts buying...");
			}
			else if(Action == 2)
			{
				if(CurrentGamer.Properties.First == null)
				{
					Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du besitzt keine Anteilsscheine.");
					CurrentAction = 0;
				}
				else
				{
					Con.sendMessage("SHOWTRADE False");
					disableButtons();
					CurrentGamer.Properties.sendAllProperties(CurrentGamer);
					CurrentServer.display(Con.Username + " starts selling...");
				}
			}
			else if(Action == 3)
			{
				if(CurrentGamer.Properties.First == null)
				{
					Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du besitzt keine Anteilsscheine.");
					CurrentAction = 0;
				}
				else if(CurrentServer.ConList.countPlayers() < 3)
				{
					Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Eine Auktion benötigt mindestens 2 Mitspieler.");
					CurrentServer.display(Con.Username + " tried to auction, but there have to be at least 3 players.");
					CurrentAction = 0;
				}
				else
				{
					if(Auctions > 0 && CurrentGamer.Joker > 0)
					{
						Con.sendMessage("SHOWAUCTION True True");
					}
					else
					{
						Con.sendMessage("SHOWAUCTION True False");
					}
					disableButtons();
					CurrentGamer.Properties.sendAllProperties(CurrentGamer);
					CurrentServer.display(Con.Username + " starts an auction...");
				}
			}
		}
	}
	
	public void giveJoker(Connection Con)
	{
		if(CurrentGamer.Con == Con && Auctions > 0 && CurrentGamer.Joker > 0)
		{
			Con.sendMessage("SETGAME");
			CurrentAction = 0;
			CurrentGamer.changeJoker(-1);
			Auctions = 0;
			enableButtons();
			CurrentServer.ConList.broadcastEx(Con, "MESSAGE " + CurrentServer.Colors[3] + " " + Con.Username + " hat einen Joker gesetzt.");
			Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast einen Joker gesetzt.");
			CurrentServer.ConList.broadcastEx(Con, "MESSAGE " + CurrentServer.Colors[3] + " " + Con.Username + " muss keine Anteilsscheine mehr verkaufen.");
			Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du musst keine Anteilsscheine mehr verkaufen.");
			CurrentServer.display(Con.Username + " gave a joker. He does not have to do one more auction.");
		}
	}
	
	public void addToCart(int ID)
	{
		if(CurrentAction == 1)
		{
			Cart.addProperty(Bank.removeProperty(ID));
			CurrentGamer.sendPrice(CurrentAction, Cart.Price);
		}
		else if(CurrentAction == 2)
		{
			Cart.addProperty(CurrentGamer.Properties.removeProperty(ID));
			CurrentGamer.sendPrice(CurrentAction, Cart.Price / 2);
		}
	}
	
	public void removeFromCart(int ID)
	{
		if(CurrentAction == 1)
		{
			Bank.addProperty(Cart.removeProperty(ID));
			CurrentGamer.sendPrice(CurrentAction, Cart.Price);
		}
		else if(CurrentAction == 2)
		{
			CurrentGamer.Properties.addProperty(Cart.removeProperty(ID));
			CurrentGamer.sendPrice(CurrentAction, Cart.Price / 2);
		}
	}

	public void acceptAction(Connection Con, int ID)
	{
		if(CurrentAction == 1)
		{
			if(Cart.Price < 1)
			{
				Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast keine Anteilsscheine im Warenkorb.");
			}
			else if(CurrentGamer.Money < Cart.Price)
			{
				Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast nicht genug Geld.");
			}
			else
			{
				CurrentGamer.changeMoney(-1 * Cart.Price);
				Cart.moveToList(CurrentGamer.Properties, true, "bought");
				CurrentServer.ConList.broadcastexPlayers("SETPLAYERDATA 1 " + Con.ID + " " + CurrentGamer.Properties.Counter);
				Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Kauf erfolgreich.");
				CurrentServer.ConList.broadcastEx(Con, "MESSAGE " + CurrentServer.Colors[3] + " " + Con.Username + " hat Anteilsscheine eingekauft.");
				enableButtons();
				Con.sendMessage("SETGAME");
				CurrentServer.display(Con.Username + " finished buying.");
				CurrentAction = 0;
				
				if(!Bank.hasPropertiesByWorld())
				{
					CurrentServer.ConList.broadcast("MESSAGE " + CurrentServer.Colors[3] + " Die Bank ist nun leergekauft.");
					CurrentServer.display("The bank is empty now.");
				}
			}
		}
		else if(CurrentAction == 2)
		{
			if(Cart.Price < 1)
			{
				Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast keine Anteilsscheine im Warenkorb.");
			}
			else
			{
				CurrentGamer.changeMoney(Cart.Price / 2);
				Cart.moveToList(Bank, true, "sold");
				CurrentServer.ConList.broadcastexPlayers("SETPLAYERDATA 1 " + Con.ID + " " + CurrentGamer.Properties.Counter);
				Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Verkauf erfolgreich.");
				CurrentServer.ConList.broadcastEx(Con, "MESSAGE " + CurrentServer.Colors[3] + " " + Con.Username + " hat Anteilsscheine an die Bank verkauft.");
				enableButtons();
				Con.sendMessage("SETGAME");
				CurrentServer.display(Con.Username + " finished selling.");
				CurrentAction = 0;
				checkGiveUp();
			}
		}
		else if(CurrentAction == 3)
		{
			CurrentAction = 4;
			AuctionItem = CurrentGamer.Properties.removeProperty(ID);
			Con.sendMessage("SETAUCTIONITEM " + ID);
		}
		else if(CurrentAction == 4)
		{
			if(CurrentServer.AuctionMinimum >= CurrentServer.AuctionLimit)
			{
				AuctionHigher = CurrentServer.AuctionMaximum;
			}
			else
			{
				AuctionHigher = CurrentServer.AuctionMinimum;
			}			
			
			CurrentServer.display(Con.Username + " sets " + AuctionItem.PropertyData() + " to auction.");
			CurrentOffer = AuctionItem.Price / 2;
			AuctionStarted = true;
			setAllInAuction();
			checkOfferer();
			if(isNoOffererLeft())
			{
				CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " Niemand kann auf den Anteilsschein bieten. Er wurde an die Bank verkauft.");
				CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Niemand kann auf deinen Anteilsschein bieten. Er wurde an die Bank verkauft.");
				CurrentServer.display("Nobody was able to buy the property. It was sold to the bank.");
				finishAuction();
			}
			else
			{
				sendAuctionPrice();
			}
		}
	}
	
	public void setAllInAuction()
	{
		Connection CurCon = CurrentServer.ConList.First;
		while(CurCon != null)
		{
			if(!CurCon.isPlayer)
			{
				CurCon.sendMessage("SHOWOFFER");
			}
			else
			{
				CurCon.sendMessage("GETFOCUS");
				Gamer CurGamer = getGamer(CurCon);
				if(CurGamer == CurrentGamer)
				{
					CurCon.sendMessage("SETAUCTIONSTARTED");
				}
				else
				{
					CurGamer.isInAuction = true;
					CurCon.sendMessage("SHOWOFFER");
					CurrentServer.ConList.broadcast("SHOWPERCENT " + CurGamer.Con.ID + " True " + CurGamer.Properties.Properties[AuctionItem.Ressource][0]);
					if(CurGamer.hasProperty(AuctionItem.Ressource))
					{
						CurCon.sendMessage("MARKPROPERTY " + AuctionItem.Ressource + " True");
					}
				}
			}
			CurCon = CurCon.After;
		}
	}
	
	public void cancelAction(Connection Con)
	{
		if(CurrentAction == 1)
		{
			Cart.moveToList(Bank, false, "");
			CurrentServer.display(Con.Username + " cancelled buying.");
			enableButtons();
			Con.sendMessage("SETGAME");
			CurrentAction = 0;
		}
		else if(CurrentAction == 2)
		{
			Cart.moveToList(CurrentGamer.Properties, false, "");
			CurrentServer.display(Con.Username + " cancelled selling.");
			enableButtons();
			Con.sendMessage("SETGAME");
			CurrentAction = 0;
		}
		else if(CurrentAction == 3)
		{
			CurrentServer.display(Con.Username + " cancelled the auction.");
			enableButtons();
			Con.sendMessage("SETGAME");
			CurrentAction = 0;
		}
		else if(CurrentAction == 4 && !AuctionStarted)
		{
			if(Auctions > 0 && CurrentGamer.Joker > 0)
			{
				Con.sendMessage("SHOWAUCTION False True");
			}
			else
			{
				Con.sendMessage("SHOWAUCTION False False");
			}
			CurrentGamer.Properties.addProperty(AuctionItem);
			AuctionItem = null;
			CurrentAction = 3;
		}
	}
	
	public void higherOffer(Connection Con)
	{
		if(CurrentAction == 4 && Con.isPlayer)
		{
			Gamer CurGamer = getGamer(Con);
			if(CurGamer.isInAuction)
			{
				if(CurGamer.Money >= (CurrentOffer + AuctionHigher))
				{
					CurrentOfferer = CurGamer;
					CurrentOffer += AuctionHigher;
					CurrentOfferer.Counter = 0;
					
					if(CurrentOffer >= CurrentServer.AuctionLimit)
					{
						AuctionHigher = CurrentServer.AuctionMaximum;
					}
					else
					{
						AuctionHigher = CurrentServer.AuctionMinimum;
					}
					
					checkOfferer();
					
					if(isLastOfferer())
					{
						finishAuction();
					}
					else
					{
						sendAuctionPrice();
					}
				}
			}
		}
	}
	
	public void checkOfferer()
	{
		Gamer CurGamer = First;
		while(CurGamer != null)
		{
			if(CurGamer.isInAuction && CurGamer != CurrentOfferer && CurGamer.Money < (CurrentOffer + AuctionHigher))
			{
				CurGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast nicht genug Geld, um weiter mitzubieten.");
				CurrentServer.ConList.broadcastEx(CurGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurGamer.Con.Username + " hat nicht genug Geld, um weiter mitzubieten.");
				CurrentServer.display(CurGamer.Con.Username + " has not enough money to higher and was removed from the auction.");
				removeFromAuction(CurGamer.Con);
			}
			CurGamer = CurGamer.After;
		}
	}
	
	public void sendAuctionPrice()
	{
		Connection CurCon = CurrentServer.ConList.First;
		String COName = "(Niemand)";
		if(CurrentOfferer != null) { COName = CurrentOfferer.Con.Username; }
		
		while(CurCon != null)
		{
			if(!CurCon.isPlayer)
			{
				CurCon.sendMessage("SETAUCTIONOFFER False " + CurrentOffer + " " + COName + " " + AuctionItem.PropertyData()); 
			}
			else
			{
				Gamer CurGamer = getGamer(CurCon);
				if(CurGamer == CurrentGamer)
				{
					CurCon.sendMessage("SETAUCTIONPRICE " + CurrentOffer + " " + COName);
				}
				else if(CurGamer == CurrentOfferer)
				{
					CurCon.sendMessage("SETAUCTIONOFFER False " + CurrentOffer + " " + COName + " " + AuctionItem.PropertyData());
				}
				else if(CurGamer.isInAuction)
				{
					CurCon.sendMessage("SETAUCTIONOFFER True " + CurrentOffer + " " + COName + " " + AuctionItem.PropertyData());
					CurCon.sendMessage("GETFOCUS");
				}
				else if(!CurGamer.isInAuction)
				{
					CurCon.sendMessage("SETAUCTIONOFFER False " + CurrentOffer + " " + COName + " " + AuctionItem.PropertyData());
				}
			}
			CurCon = CurCon.After;
		}
	}
	
	public void quitAuction(Connection Con)
	{
		if(CurrentAction == 4 && Con.isPlayer)
		{
			Gamer CurGamer = getGamer(Con);
			if(CurGamer.isInAuction && CurGamer != CurrentOfferer)
			{
				removeFromAuction(Con);
				
				if(isNoOffererLeft())
				{
					CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " Niemand möchte " + CurrentGamer.Con.Username + "'s Anteilsschein haben. Er wurde an die Bank verkauft.");
					CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Niemand möchte deinen Anteilsschein haben. Er wurde an die Bank verkauft.");
					CurrentServer.display("Nobody wants to buy " + CurrentGamer.Con.Username + "'s property. It was sold to the bank.");
					finishAuction();
				}				
				else if(isLastOfferer())
				{
					finishAuction();
				}
				else
				{
					sendAuctionPrice();
				}
			}
		}
	}
	
	public void removeFromAuction(Connection Con)
	{
		if(CurrentAction == 4 && Con.isPlayer)
		{
			removeFromAuction(getGamer(Con));
		}
	}
	
	public void removeFromAuction(Gamer CurGamer)
	{
		if(CurGamer.isInAuction && CurGamer != CurrentOfferer)
		{
			CurGamer.isInAuction = false;
			CurGamer.Counter = 0;
			CurrentServer.ConList.broadcast("SHOWPERCENT " + CurGamer.Con.ID + " False 0");
			if(CurGamer.hasProperty(AuctionItem.Ressource))
			{
				CurGamer.Con.sendMessage("MARKPROPERTY " + AuctionItem.Ressource + " False");
			}
			CurrentServer.ConList.broadcast("MESSAGE " + CurrentServer.Colors[3] + " " + CurGamer.Con.Username +  " steigt aus der Auktion aus.");
			CurrentServer.display(CurGamer.Con.Username + " quit the auction.");
		}
	}
	
	public boolean isNoOffererLeft()
	{
		Gamer CurGamer = First;
		while(CurGamer != null)
		{
			if(CurGamer.isInAuction) { return false; }
			CurGamer = CurGamer.After;
		}
		return true;
	}
	
	public boolean isLastOfferer()
	{
		Gamer CurGamer = First;
		while(CurGamer != null)
		{
			if(CurGamer.isInAuction && CurGamer != CurrentOfferer) { return false; }
			CurGamer = CurGamer.After;
		}
		return true;
	}
	
	public void finishAuction()
	{					
		if(CurrentOfferer == null)
		{
			Bank.addProperty(AuctionItem);
		}
		else
		{
			CurrentOfferer.changeMoney(-1 * CurrentOffer);
			CurrentOfferer.Properties.addProperty(AuctionItem);
			CurrentOfferer.isInAuction = false;
			CurrentOfferer.Counter = 0;
			int Percent = CurrentOfferer.Properties.Properties[AuctionItem.Ressource][0];
			int Price = CurrentOfferer.Properties.Properties[AuctionItem.Ressource][1];
			CurrentOfferer.Con.sendMessage("SETPROPERTY " + AuctionItem.Ressource + " " + AuctionItem.RessourceName + " " +  Percent + " " + Price);
			CurrentServer.ConList.broadcast("SHOWPERCENT " + CurrentOfferer.Con.ID + " False 0");
			CurrentOfferer.Con.sendMessage("MARKPROPERTY " + AuctionItem.Ressource + " False");
			CurrentServer.ConList.broadcastexPlayers("SETPLAYERDATA 1 " + CurrentOfferer.Con.ID + " " + CurrentOfferer.Properties.Counter);
		}
					
		CurrentGamer.changeMoney(CurrentOffer);
		int Percent = CurrentGamer.Properties.Properties[AuctionItem.Ressource][0];
		int Price = CurrentGamer.Properties.Properties[AuctionItem.Ressource][1];
		CurrentGamer.Con.sendMessage("SETPROPERTY " + AuctionItem.Ressource + " " + AuctionItem.RessourceName + " " +  Percent + " " + Price);
		CurrentServer.ConList.broadcastexPlayers("SETPLAYERDATA 1 " + CurrentGamer.Con.ID + " " + CurrentGamer.Properties.Counter);
					
		Connection CurCon = CurrentServer.ConList.First;
		while(CurCon != null)
		{
			CurCon.sendMessage("SETGAME");
			if(CurrentOfferer != null)
			{
				if(CurCon.isPlayer)
				{
					CurCon.sendMessage("GETFOCUS");
					if(CurrentGamer.Con == CurCon)
					{
						CurCon.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast den Anteilsschein für " + getPointedInt(CurrentOffer) + "DM an " + CurrentOfferer.Con.Username + " verkauft.");
					}
					else if(CurrentOfferer.Con == CurCon)
					{
						CurCon.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast den Anteilsschein für " + getPointedInt(CurrentOffer) + "DM gekauft.");
					}
					else
					{
						CurCon.sendMessage("MESSAGE " + CurrentServer.Colors[3] + " " + CurrentGamer.Con.Username + " hat den Anteilsschein für " + getPointedInt(CurrentOffer) + "DM an " + CurrentOfferer.Con.Username + " verkauft.");
					}
				}
				else
				{
					CurCon.sendMessage("MESSAGE " + CurrentServer.Colors[3] + " " + CurrentGamer.Con.Username + " hat den Anteilsschein für " + getPointedInt(CurrentOffer) + "DM an " + CurrentOfferer.Con.Username + " verkauft.");
				}
			}
			CurCon = CurCon.After;
		}
					
		if(CurrentOfferer != null)
		{
			CurrentServer.display(CurrentGamer.Con.Username + " sold the property for " + getPointedInt(CurrentOffer) + "DM to " + CurrentOfferer.Con.Username + ".");
		}

		if(Auctions > 0)
		{
			Auctions--;
			if(Auctions == 0)
			{
				CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurrentGamer.Con.Username + " muss keine Anteilsscheine mehr verkaufen.");
				CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du musst keine Anteilsscheine mehr verkaufen.");
				CurrentServer.display(CurrentGamer.Con.Username + " does not have to do more auctions.");
			}
			else
			{
				String Auct = "Anteilsschein";
				String Auct2 = "auction";
				if(Auctions > 1) { Auct = "Anteilsscheine"; Auct2 = "auctions"; }
				CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurrentGamer.Con.Username + " muss noch " + Auctions + " " + Auct + " verkaufen.");
				CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du musst noch " + Auctions + " " + Auct + " verkaufen.");
				CurrentServer.display(CurrentGamer.Con.Username + " has to do " + Auctions + " more " + Auct2 + ".");
			}
		}
					
		AuctionStarted = false;
		AuctionItem = null;
		CurrentOfferer = null;
		CurrentOffer = 0;
		CurrentAction = 0;
		enableButtons();
		checkGiveUp();
	}
	
	public void clearCart()
	{
		Cart.moveToList(Bank, false, "");
	}
	
	public Gamer getGamer(Connection Con)
	{
		Gamer CurGamer = First;
		while(CurGamer != null)
		{
			if(CurGamer.Con == Con)
			{
				return CurGamer;
			}
			CurGamer = CurGamer.After;
		}
		return null;
	}
	
	public void requestGiveUp(Connection Con)
	{
		if(GameRuns && getGamer(Con) != null)
		{
			CurrentServer.ConList.broadcastEx(Con, "MESSAGE " + CurrentServer.Colors[3] + " " + Con.Username + " hat aufgegeben.");
			Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast aufgegeben.");
			CurrentServer.display(Con.Username + " gave up.");
			giveUp(Con);
		}
	}
	
	public void checkGiveUp()
	{
		if(CurrentGamer.Money <= 0 && CurrentGamer.Properties.Counter == 0)
		{
			CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du bist nicht weiter spielfähig. Du wurdest aus dem Spiel geworfen.");
			CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurrentGamer.Con.Username + " ist nicht weiter spielfähig. Er wurde aus dem Spiel geworfen.");
			CurrentServer.display(CurrentGamer.Con.Username + " was not able to play on. He was kicked.");
			giveUp(CurrentGamer.Con);
		}
	}
	
	public void giveUp(Connection Con)
	{
		Con.sendMessage("SETVIEWER");
		Gamer CurGamer = CurrentServer.CurrentGame.First;
		while(CurGamer != null)
		{
			Con.sendMessage("SETPLAYERDATA 0 " + CurGamer.Con.ID + " " + CurGamer.Con.Username + " " + CurGamer.Properties.Counter + " " + CurGamer.Money + " " + CurGamer.Con.Col);
			CurGamer = CurGamer.After;
		}		
		removePlayer(Con);
	}
	
	public void forceRemovePlayer(Connection Con)
	{
		CurrentServer.ConList.broadcastEx(Con, "MESSAGE " + CurrentServer.Colors[3] + " " + Con.Username + " wurde aus dem Spiel entfernt.");
		CurrentServer.display(Con.Username + " was removed from the game.");
		removePlayer(Con);
	}
	
	public void requestNextPlayer(Connection Con)
	{
		if(CurrentGamer.Con == Con)
		{
			if(CurrentAction == 0 && Auctions == 0 && CurrentGamer.Money >= 0)
			{
				CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurrentGamer.Con.Username + " hat die Runde beendet.");
				CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast die Runde beendet.");
				CurrentServer.display(CurrentGamer.Con.Username + " completed the round.");
				nextPlayer();
			}
		}
	}
	
	public void forceNextPlayer()
	{
		if(CurrentGamer.DiceRed == 0)
		{
			roll(CurrentGamer.Con);
		}
		if(Auctions > 0)
		{
			int AuctionPrice = Auctions * CurrentServer.AuctionMoney;
			CurrentGamer.changeMoney(-1 * AuctionPrice);
			CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[3] + " Du musst " + getPointedInt(AuctionPrice) + "DM zahlen.");
			CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurrentGamer.Con.Username + " muss " + getPointedInt(AuctionPrice) + "DM zahlen.");
			CurrentServer.display(CurrentGamer.Con.Username + " has to pay " + getPointedInt(AuctionPrice) + "DM.");
		}
		CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurrentGamer.Con.Username + " war zu lange inaktiv.");
		CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du warst zu lange inaktiv.");
		CurrentServer.display(CurrentGamer.Con.Username + " was inactive for too long.");
		
		if(CurrentGamer.Money >= 0)
		{
			nextPlayer();
		}
		else
		{
			CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du bist nicht weiter spielfähig. Du wurdest aus dem Spiel geworfen.");
			CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurrentServer.CurrentGame.CurrentGamer.Con.Username + " ist nicht weiter spielfähig. Er wurde aus dem Spiel geworfen.");
			CurrentServer.display(CurrentGamer.Con.Username + " was not able to play on. He was kicked.");
			giveUp(CurrentGamer.Con);
		}
	}
	
	public void nextPlayer()
	{
		if(CurrentGamer == Last)
		{
			setCurrentPlayer(First);
		}
		else
		{
			setCurrentPlayer(CurrentGamer.After);
		}
	}
	
	private void setCurrentPlayer(Gamer CurGamer)
	{
		if(CurrentGamer != null)
		{
			disableButtons();
			CurrentGamer.Con.sendMessage("SETGAME");
			CurrentServer.ConList.broadcast("MARKUSER " + CurrentGamer.Con.ID + " False");
			
			if(AuctionStarted)
			{				
				if(CurGamer == CurrentGamer || CurGamer == CurrentOfferer)
				{				
					CurrentGamer.Properties.addProperty(AuctionItem);
					
					CurrentServer.ConList.broadcast("SETGAME");
					CurrentServer.ConList.broadcast("MESSAGE " + CurrentServer.Colors[3] + " Die Auktion wurde beendet.");
					CurrentServer.display("The auction was terminated.");
					
					AuctionStarted = false;
					AuctionItem = null;
					CurrentOfferer = null;
					CurrentOffer = 0;
					CurrentAction = 0;
				}
				else if(CurGamer.isInAuction)
				{
					removeFromAuction(CurGamer);
				}
			}
			else if(CurrentAction > 0)
			{
				cancelAction(CurGamer.Con);
				if(CurrentAction > 0)
				{
					cancelAction(CurGamer.Con);
				}
			}
		}
		Cart.moveToList(Bank, false, "");
		CurrentGamer = CurGamer;
		CurrentServer.ConList.broadcast("MARKUSER " + CurGamer.Con.ID + " True");
		CurGamer.Con.sendMessage("PLAYSOUND 7");
		CurGamer.Con.sendMessage("GETFOCUS");
		CurrentServer.ConList.broadcastexPlayers("PLAYSOUND 7");
		CurrentGamer.DiceRed = 0;
		CurrentGamer.Con.sendMessage("SETACTION 0 True");
		CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + CurrentGamer.Con.Username + " ist dran.");
		CurrentGamer.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du bist dran.");
		CurrentServer.display("It's " + CurrentGamer.Con.Username + "'s turn.");
		CurrentServer.AutoStart.resetCounter();
	}
	
	public void removePlayer(Connection Con)
	{
		removePlayer(getGamer(Con));
	}
	
	public void removePlayer(Gamer CurGamer)
	{
		if(CurGamer != null)
		{
			if(CurrentGamer.Con == CurGamer.Con)
			{
				nextPlayer();
			}
			
			if(First == CurGamer)
			{
				First.After.Before = null;
				First = First.After;
			}
			else if(Last == CurGamer)
			{
				Last.Before.After = null;
				Last = Last.Before;
			}
			else
			{
				CurGamer.Before.After = CurGamer.After;
				CurGamer.After.Before = CurGamer.Before;
			}
			
			CurrentServer.ConList.broadcastexPlayers("SETPLAYERDATA 3 " + CurGamer.Con.ID);
			CurrentServer.ConList.broadcast("SETPLAYERCOLOR " + CurGamer.Con.ID + " " + CurrentServer.Colors[5]);
			CurrentServer.ConList.broadcast("REMOVEPLAYERFIGURE " + CurGamer.Con.ID);
			
			CurGamer.Properties.moveToList(Bank, false, "");
						
			CurGamer.Con.isPlayer = false;
			
			if(GameRuns)
			{
				if(First == Last)
				{
					CurrentServer.ConList.broadcast("GETFOCUS");
					First.Con.isPlayer = false;
					GameRuns = false;
					CurrentServer.ConList.broadcast("CLEARALL");
					CurrentServer.ConList.broadcast("SETLOBBY");
					CurrentServer.ConList.broadcast("SETPLAYERCOLOR " + First.Con.ID + " " + CurrentServer.Colors[5]);
					CurrentServer.ConList.broadcast("MARKUSER " + First.Con.ID + " False");
					CurrentServer.ConList.broadcast("MESSAGE " + CurrentServer.Colors[3] + " Das Spiel ist vorbei.");
					CurrentServer.ConList.broadcastEx(First.Con, "MESSAGE " + CurrentServer.Colors[3] + " " + First.Con.Username + " hat das Spiel gewonnen.");
					First.Con.sendMessage("MESSAGE " + CurrentServer.Colors[4] + " Du hast das Spiel gewonnen.");
					CurrentServer.display("The game is over. " + First.Con.Username + " won.");
					CurrentServer.CurrentGame = null;
				}
			}
			else if(Dice != 0)
			{
				CurrentServer.ConList.broadcast("MESSAGE " + CurrentServer.Colors[3] + " Alle Spieler müssen noch einmal wüfeln.");
				CurrentServer.display("All players have to roll again.");
				CurGamer = First;
				while(CurGamer != null)
				{
					CurGamer.Con.sendMessage("SETACTION  0 True");
					CurGamer.DiceRed = 0;
					CurGamer = CurGamer.After;
				}
				Dice = 0;
				Amount = 0;
			}
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