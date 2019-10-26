public class Card
{
    Color color;
    Type type;
    private final int VALUE;
    public Card(Color c, Type t)
    {
        color = c;
        type = t;
        switch (type)
        {
            case DEUCE: VALUE = 2;
                break;
            case THREE: VALUE = 3;
                break;
            case FOUR: VALUE = 4;
                break;
            case FIVE: VALUE = 5;
                break;
            case SIX: VALUE = 6;
                break;
            case SEVEN: VALUE = 7;
                break;
            case EIGHT: VALUE = 8;
                break;
            case NINE: VALUE = 9;
                break;
            case TEN: VALUE = 10;
                break;
            case JACK: VALUE = 11;
                break;
            case QUEEN: VALUE = 12;
                break;
            case KING: VALUE = 13;
                break;
            case ACE: VALUE = 14;
                break;
                default: VALUE = 0;
        }

    }

    public int getVALUE() {
        return VALUE;
    }

    @Override
    public String toString()
    {
        String cardString = "";
        switch (color)
        {
            case CLUBS:
                cardString += "\u2663";
                break;
            case HEARTS:
                cardString += "\u2764";
                break;
            case SPADES:
                cardString += "\u2660";
                break;
            case DIAMONDS:
                cardString += "\u2666";
                break;
        }
        switch (type)
        {
            case DEUCE: cardString += "2";
                break;
            case THREE: cardString += "3";
                break;
            case FOUR: cardString += "4";
                break;
            case FIVE: cardString += "5";
                break;
            case SIX: cardString += "6";
                break;
            case SEVEN: cardString += "7";
                break;
            case EIGHT: cardString += "8";
                break;
            case NINE: cardString += "9";
                break;
            case TEN: cardString += "10";
                break;
            case JACK: cardString += "J";
                break;
            case QUEEN: cardString += "Q";
                break;
            case KING: cardString += "K";
                break;
            case ACE: cardString += "A";
                break;
        }
            return cardString;
    }
}
