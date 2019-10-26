import java.util.Arrays;

public class Deck
{
    public Card[] deck = new Card[52];
    public Deck()
    {
        Color cardColor = Color.DIAMONDS;
        Type cardType = Type.JACK;
        int count = 0;
        for (int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 13; j++)
            {
                switch (i)
                {
                    case 0: cardColor = Color.CLUBS;
                        break;
                    case 1: cardColor = Color.HEARTS;
                        break;
                    case 2: cardColor = Color.SPADES;
                        break;
                    case 3: cardColor = Color.DIAMONDS;
                        break;
                }
                switch (j)
                {
                    case 0: cardType = Type.DEUCE;
                        break;
                    case 1: cardType = Type.THREE;
                        break;
                    case 2: cardType = Type.FOUR;
                        break;
                    case 3: cardType = Type.FIVE;
                        break;
                    case 4: cardType = Type.SIX;
                        break;
                    case 5: cardType = Type.SEVEN;
                        break;
                    case 6: cardType = Type.EIGHT;
                        break;
                    case 7: cardType = Type.NINE;
                        break;
                    case 8: cardType = Type.TEN;
                        break;
                    case 9: cardType = Type.JACK;
                        break;
                    case 10: cardType = Type.QUEEN;
                        break;
                    case 11: cardType = Type.KING;
                        break;
                    case 12: cardType = Type.ACE;
                        break;
                }
               deck[count] = new Card(cardColor,cardType);
                count++;

            }
        }
    }
}
