public class Board
{
    public Card[] boardCards = new Card[5];
    public void Flop(Deck d)
    {
        int cards = 0;
        do{
            int i = (int)(Math.random()*52);
            if(d.deck[i] != null)
            {
                boardCards[cards] = d.deck[i];
                d.deck[i] = null;
                cards++;
            }
        } while(cards < 3);
    }
    public void Turn(Deck d)
    {
        int cards = 3;
        do{
            int i = (int)(Math.random()*52);
            if(d.deck[i] != null)
            {
                boardCards[cards] = d.deck[i];
                d.deck[i] = null;
                cards++;
            }
        } while(cards < 4);
    }
    public void River(Deck d)
    {
        int cards = 4;
        do{
            int i = (int)(Math.random()*52);
            if(d.deck[i] != null)
            {
                boardCards[cards] = d.deck[i];
                d.deck[i] = null;
                cards++;
            }
        } while(cards < 5);
    }
}
