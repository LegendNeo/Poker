public class Player
{
    public Card[] playerCards;
    String playerName;
    private int handPower = 0;
    public int powerValue = 0;
    public int secondPowerValue = 0;
    public int highCardOne = 0;
    public int highCardTwo = 0;
    private int chips;
    boolean hasFolded = false;
    boolean hasRaised = false;
    boolean hasChecked = false;
    boolean hasCalled = false;
    boolean isAllIn;
    boolean isBot = false;

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public void substractChips(int c)
    {
        this.chips -= c;
    }

    public void addChips(int c)
    {
        this.chips += c;
    }

    public int getHandPower() {
        return handPower;
    }

    public void setHandPower(int handPower) {
        this.handPower = handPower;
    }

    public Player()
    {
        playerCards = new Card[2];
    }
    public void setCards(Deck d)
    {
        int cards = 0;
        do{
            int i = (int)(Math.random()*52);
            if(d.deck[i] != null)
            {
                playerCards[cards] = d.deck[i];
                d.deck[i] = null;
                cards++;

            }
        } while(cards < 2);
    }
}
