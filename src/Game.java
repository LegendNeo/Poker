import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class Game {

    // setup JPanel
    private JPanel MainPanel;
    private JPanel buttonPanel;
    private JPanel playerPanel;
    private JPanel boardPanel;
    private JLabel boardCard1;
    private JLabel playerChips1;
    private JLabel player1Card2;
    private JLabel potV;
    private JButton foldButton;
    private JButton checkButton;
    private JButton callButton;
    private JButton raiseButton;
    private JTextField sumTextField;
    private JLabel playersTurn;
    private JButton roundButton;
    private JLabel player2Card2;
    private JLabel playerChips2;
    private JLabel announcements;
    private JLabel betLabel;
    private JLabel blindLabel;
    private JButton allInButton;
    private JLabel player1Card1;
    private JLabel player2Card1;
    private JLabel boardCard2;
    private JLabel boardCard3;
    private JLabel boardCard4;
    private JLabel boardCard5;

    // needed variables
    private int[] streetValues = new int[5];
    private Player one;
    private Player two;
    private Board b;
    private Deck d;
    private int pot;
    private int actualRaise;
    private int countRounds = 2;
    private int moveCount;
    private int actionsCount;
    private int bigBlind;
    private int smallBlind;
    private int countRaises = 0;
    private int j = 2;
    private int playerRaise;
    private int botRaise;
    private boolean mustReact;
    private boolean playerOneTurn = true;
    private boolean isFirstMove;
    private boolean winnerIsChosen;

    // constructor
    public Game()
    {
        playGame();
        // ActionListeners
        // check
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(playerOneTurn)
                {
                    check(one);
                    if((!mustReact && moveCount > 1 && !isFirstMove) ||( one.isAllIn || two.isAllIn)) nextAction(b,d);
                    else if(two.isBot) handleBot();
                }
                else check(two);
                if(!winnerIsChosen)
                {
                    if(!mustReact && moveCount > 1) nextAction(b,d);
                }
            }
        });
        // call
        callButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(playerOneTurn)
                {
                    call(one);
                    //if((!mustReact && moveCount > 1 && !isFirstMove) ||( one.isAllIn || two.isAllIn)) nextAction(b,d);
                    if(two.isBot) handleBot();
                }
                else call(two);
                if((!mustReact && moveCount > 1 && !isFirstMove) ||( one.isAllIn || two.isAllIn)) nextAction(b,d);
                isFirstMove = false;

            }
        });
        // raise
        raiseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isFirstMove = false;
                announcements.setText("");
                String s = sumTextField.getText();
                // BB raise
                if(s.equals(""))
                {
                    // tests if bb raise high enough
                    if(bigBlind > actualRaise)
                    {
                        if(one.getChips() >= bigBlind)
                        {
                            raise(one);
                            if(two.isBot) handleBot();
                        }
                        else announcements.setText("not enough chips!");
                    }
                    else
                    {
                        announcements.setText("Your raise is too small");
                    }
                }
                // bet Raise
                else if(s.matches("[0-9]+"))
                {
                    int i = Integer.parseInt(s);
                    // tests if bet high enough
                    if(i >= actualRaise && i >= bigBlind)
                    {
                        if(one.getChips() >= i + actualRaise)
                        {
                            if(i < two.getChips())
                            {
                                raise(one);
                            }
                            else{
                                allIn(one);
                            }
                            handleBot();
                        }
                        else announcements.setText("not enough chips!");
                    }
                    else
                    {
                        announcements.setText("Your raise is too small");
                    }
                }
                else announcements.setText("No Letters!");
            }
        });
        // all-in
        allInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    allIn(one);
                    if(two.isBot) handleBot();
                }
        });
        // fold
        foldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(playerOneTurn)
                {
                    fold(one);
                    if(two.isBot) handleBot();
                }
                else fold(two);
                nextAction(b,d);
            }
        });

        roundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isGameOver();
            }
        });

    }

    // Button methods
    public void check(Player p)
    {
        if(!p.isBot)
        {
            p.hasChecked = true;
            p.hasCalled = false;
            p.hasRaised = false;
            p.hasFolded = false;
            p.isAllIn = false;
        }
        sumTextField.setText("");
        moveCount++;
        p.hasRaised = false;
        mustReact = false;
        potV.setText("Pot: " + pot);
        checkPlayerTurn();
        callButton.setEnabled(false);
    }

    public void call(Player p)
    {
        if(!p.isBot)
        {
            p.hasChecked = false;
            p.hasCalled = true;
            p.hasRaised = false;
            p.hasFolded = false;
            p.isAllIn = false;
        }
        sumTextField.setText("");
        betLabel.setText("");
        moveCount++;
        p.substractChips(actualRaise);
        pot += actualRaise;
        actualRaise = 0;
        p.hasRaised = false;
        mustReact = false;
        potV.setText("Pot: " + pot);
        playerChips1.setText("" + one.getChips());
        playerChips2.setText("" + two.getChips());
        checkPlayerTurn();
        announcements.setText("");
        checkButton.setEnabled(true);
        callButton.setEnabled(false);
    }

    public void raise(Player p)
    {
        p.hasRaised = true;
        p.hasCalled = false;
        p.hasFolded = false;
        p.hasChecked = false;
        p.isAllIn = false;
        int sum;
        moveCount++;
        String sumS = sumTextField.getText();
        // if first move
        if(isFirstMove) sum = smallBlind;
        // if "500"
        else if(!sumS.equals("")){
            if(Integer.parseInt(sumS) > actualRaise) sum = Integer.parseInt(sumS);
            else sum = actualRaise;
        }
        // if ""
        else{
            sum = bigBlind;
            if(actualRaise > bigBlind) sum = actualRaise;
        }
        // if player is Bot
        if(p.isBot) sum = handleBotRaise();
        if(sum == 0)
        {
            if(actualRaise > 0){
                call(two);
                announcements.setText("CALL");
                if ((!mustReact && moveCount > 1 && !isFirstMove)) nextAction(b, d);
            }
            else{
                check(two);
                announcements.setText("CHECK");
                if ((!mustReact && moveCount > 1 && !isFirstMove) || (one.isAllIn || two.isAllIn)) nextAction(b, d);
            }
        }
        else
            {
                p.substractChips(sum);
                pot += actualRaise;
                pot += sum;
                p.substractChips(actualRaise);
                actualRaise = sum;
                mustReact = true;
                //sumTextField.setText("");
                betLabel.setText("Bet: " + sum);
                playerChips1.setText("" + one.getChips());
                playerChips2.setText("" + two.getChips());
                checkPlayerTurn();
                checkButton.setEnabled(false);
                // check if player can call
                if(playerOneTurn)
                {
                    if(one.getChips() < actualRaise) raiseButton.setEnabled(false);
                    if(one.getChips() > actualRaise) callButton.setEnabled(true);
                    else callButton.setEnabled(false);
                }
                else
                {
                    if(two.getChips() < actualRaise) raiseButton.setEnabled(false);
                    if(two.getChips() > actualRaise) callButton.setEnabled(true);
                    else callButton.setEnabled(false);
                }
                countRaises++;
                if(countRaises > 1) isFirstMove = false;
            }
    }

    public int handleBotRaise()
    {
        int raiseSum = 0;
        // case no raise
        if(actualRaise <= bigBlind)
        {
            raiseSum = bigBlind+10;
        }
        else
            {
                raiseSum = actualRaise+100;
            }
        if(two.getChips() < raiseSum+actualRaise) raiseSum = 0;
        announcements.setText("RAISE");
        if(isFirstMove) raiseSum = smallBlind;

        return raiseSum;
    }

    public void allIn(Player p)
    {
        if(!p.isBot)
        {
            p.hasChecked = false;
            p.hasCalled = false;
            p.hasRaised = false;
            p.hasFolded = false;
        }
        moveCount++;
        if(!one.isAllIn && !two.isAllIn)
        {
            p.substractChips(actualRaise);
            pot += actualRaise;
        }
        actualRaise = p.getChips();
        p.hasRaised = true;
        checkPlayerTurn();
        // set Buttons
        raiseButton.setEnabled(false);
        checkButton.setEnabled(false);
        if(!one.isAllIn && !two.isAllIn)
        {
            // P2 All in
            //test3
            if(playerOneTurn)
            {
                two.isAllIn = true;
                if(one.getChips() > actualRaise)
                {
                    pot += p.getChips();
                    p.substractChips(p.getChips());
                    callButton.setEnabled(true);
                    allInButton.setEnabled(false);
                }
                // case not enough money
                else
                {
                    pot += one.getChips();
                    p.substractChips(one.getChips());
                    callButton.setEnabled(false);
                    allInButton.setEnabled(true);
                }
            }
            // P1 All in
            else
            {
                one.isAllIn = true;
                if(two.getChips() > actualRaise)
                {
                    pot += p.getChips();
                    p.substractChips(p.getChips());
                    callButton.setEnabled(true);
                    allInButton.setEnabled(false);
                }
                else
                {
                    actualRaise = two.getChips();
                    pot += two.getChips();
                    p.substractChips(two.getChips());
                    callButton.setEnabled(false);
                    allInButton.setEnabled(true);
                }
            }
        }
        else
        {
            pot += p.getChips();
            p.substractChips(p.getChips());
        }
        potV.setText(""+ pot);
        sumTextField.setText("");
        playerChips1.setText("" + one.getChips());
        playerChips2.setText("" + two.getChips());
    }

    public void fold(Player p)
    {
        p.hasChecked = false;
        p.hasCalled = false;
        p.hasRaised = false;
        p.hasFolded = true;
        mustReact = false;
        actualRaise = 0;
        playerOneTurn = !playerOneTurn;
        sumTextField.setText("");
        allInButton.setEnabled(false);
    }

    // play game methods

    public void playGame()
    {
        JFrame frame = new JFrame("Poker");
        frame.setBounds(350,250,0,0);
        frame.setContentPane(MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(700,470));
        frame.pack();
        frame.setVisible(true);
        checkButton.setEnabled(false);
        callButton.setEnabled(false);
        raiseButton.setEnabled(false);
        foldButton.setEnabled(false);
        allInButton.setEnabled(false);
        sumTextField.setEnabled(false);
        showBoardCards();
        one = new Player();
        two = new Player();
        one.setChips(3000);
        two.setChips(3000);
        one.playerName = "one";
        two.playerName = "two";
        two.isBot = true;
    }

    public void round()
    {
        // set Buttons enable
        checkButton.setEnabled(true);
        callButton.setEnabled(true);
        raiseButton.setEnabled(true);
        foldButton.setEnabled(true);
        allInButton.setEnabled(true);
        sumTextField.setEnabled(true);
        roundButton.setEnabled(false);
        // set deck, board and new player cards
        d = new Deck();
        b = new Board();
        one.setCards(d);
        two.setCards(d);
        one.hasFolded = false;
        two.hasFolded = false;
        one.isAllIn = false;
        two.isAllIn = false;
        // set visible setup
        roundButton.setText("START");
        announcements.setText("");
        playerChips1.setText("" + one.getChips());
        potV.setText("0");
        playerChips2.setText("" + two.getChips());
        showPlayerCards(one,1);
        showPlayerCards(two,2);
        if(countRounds > 2) j = (int)(Math.random()*10);
        showBoardCards();
        // set new round variables
        countRaises = 0;
        actualRaise = 0;
        pot = 0;
        mustReact = true;
        moveCount = 0;
        actionsCount = 0;
        winnerIsChosen = false;
        upgradeBlind();
        blindLabel.setText("BB: " + bigBlind);
        if(countRounds % 2 == 0)
        {
            playersTurn.setText("PlayerTurn: 1");
            playerOneTurn = true;
            isFirstMove = true;
            raise(one);
            raise(two);
            moveCount = 0;
        }
        else
        {
            /*check(two); // bot-move
            announcements.setText("CHECK");
            playersTurn.setText("PlayerTurn: 1");*/
            //playerOneTurn = true;
            isFirstMove = true;

            raise(two);
            raise(one);
            call(two);
            playerOneTurn = true;
            announcements.setText("CALL");
            playersTurn.setText("PlayerTurn: 1");
        }
        countRounds++;
    }

    public void nextAction(Board b, Deck d)
    {
        isFirstMove = false;
        if(!one.hasFolded && !two.hasFolded && !one.isAllIn && !two.isAllIn)
        {
            resetOne();
            if(actionsCount == 0)
            {
                b.Flop(d);
                showBoardCards();
                moveCount = 0;
                actionsCount++;
                setPlayerTurn();
                reactivateButtons();
                if(countRounds % 2 == 0)handleBot();
            }
            else if(actionsCount == 1)
            {
                b.Turn(d);
                showBoardCards();
                moveCount = 0;
                actionsCount++;
                setPlayerTurn();
                reactivateButtons();
                if(countRounds % 2 == 0)handleBot();
            }
            else if(actionsCount == 2)
            {
                b.River(d);
                showBoardCards();
                moveCount = 0;
                actionsCount++;
                setPlayerTurn();
                reactivateButtons();
                if(countRounds % 2 == 0)handleBot();
            }
            else resetButtons();
        }
        // case 1 Player has Folded
        else if(one.hasFolded || two.hasFolded) resetButtons();
        // case 1 Player is All in
        else
        {
            allInButton.setEnabled(false);
            if(actionsCount == 0)
            {
                b.Flop(d);
                b.Turn(d);
                b.River(d);
                showBoardCards();
                moveCount = 0;
                resetButtons();
            }
            else if(actionsCount == 1)
            {
                b.Turn(d);
                b.River(d);
                showBoardCards();
                moveCount = 0;
                resetButtons();
            }
            else if(actionsCount == 2)
            {
                b.River(d);
                showBoardCards();
                moveCount = 0;
                resetButtons();
            }
            else
                {
                    resetButtons();
                }
        }
    }

    // bot methods
    public void handleBot()
    {
        upgradeHandPower(b,two);
        if(mustReact || moveCount <= 1 || isFirstMove || one.isAllIn) {
            // case Player checks
            if (one.hasChecked || one.hasCalled) {
                if (two.getHandPower() > 1) {
                    raise(two);
                } else {
                    check(two);
                    announcements.setText("CHECK");
                    if ((!mustReact && moveCount > 1 && !isFirstMove)) nextAction(b, d);
                }
                // case Player has Raised
            } else if (one.hasRaised && !one.isAllIn) {
                if(two.getHandPower() > 1) {
                    raise(two);
                }
                else{
                    call(two);
                    announcements.setText("CALL");
                    if ((!mustReact && moveCount > 1 && !isFirstMove)) nextAction(b, d);
                }
                // case Player has folded
            } else if (one.hasFolded) {
                check(two);
                announcements.setText("CHECK");
                // case Player is all in
            } else if (one.isAllIn) {
                call(two);
                nextAction(b,d);

                // case Bot comes out
            } else {
                if(two.getHandPower() > 1) {
                    raise(two);
                }
                else {
                    check(two);
                    announcements.setText("BotKommtRaus");
                }
            }
        }
    }

    // getStrongerPlayer methods
    public Player getStrongerPlayer(Player one, Player two, Board b)
    {
        Player three = new Player();
        // hand power check
        if(one.getHandPower() > two.getHandPower()) return one;
        else if(one.getHandPower() < two.getHandPower()) return two;
        else
        {
            // powerValue check
            if(one.powerValue > two.powerValue) return one;
            else if(one.powerValue < two.powerValue)return two;
            else
            {
                // straight flush
                if((isStraightFlush(b,one) && isStraightFlush(b,two))|| isRoyalFlush(b,one)) return three;
                // four of a kind
                if(isQuadruple(b,one) && isQuadruple(b,two))
                {
                    if(one.highCardOne > two.highCardOne) return one;
                    if(one.highCardOne < two.highCardOne) return two;
                    else return three;
                }
                if(isFlush(b,one) && isFlush(b,two)) return three;
                if(isStreet(b,one) && isStreet(b,two)) return three;
                // full house
                if(isFullhouse(b,one) && isFullhouse(b,two))
                {
                    if(one.secondPowerValue > two.secondPowerValue) return one;
                    else if(one.secondPowerValue < two.secondPowerValue)return two;
                    else return three;
                }
                // triple
                if(isTriple(b,one) && isTriple(b,two))
                {
                    if(one.highCardOne > two.highCardOne) return one;
                    else if(one.highCardOne < two.highCardOne) return two;
                    else
                    {
                        if(one.highCardTwo > two.highCardTwo) return one;
                        else if(one.highCardTwo < two.highCardTwo) return two;
                        else return three;
                    }
                }
                // double pair
                if(isDoublePair(b,one) && isDoublePair(b,two))
                {
                    if(one.secondPowerValue > two.secondPowerValue) return one;
                    else if(one.secondPowerValue < two.secondPowerValue) return two;
                    else
                    {
                        if(one.highCardOne > two.highCardOne) return one;
                        else if(one.highCardOne < two.highCardOne) return two;
                        else return three;
                    }
                }
                // pair
                if(isPair(b,one) && isPair(b,two))
                {
                    if(one.highCardOne > two.highCardOne) return one;
                    else if(one.highCardOne < two.highCardOne) return two;
                    else
                    {
                        if(one.highCardTwo > two.highCardTwo) return one;
                        else if(one.highCardTwo < two.highCardTwo) return two;
                        else return three;
                    }
                }
                // high card
                if(isHighCard(b,one) && isHighCard(b,two))
                {
                    if(one.highCardOne > two.highCardOne) return one;
                    else if(one.highCardOne < two.highCardOne) return two;
                    else
                    {
                        if(one.highCardTwo > two.highCardTwo) return one;
                        else if(one.highCardTwo < two.highCardTwo) return two;
                        else return three;
                    }
                }
                return three;
            }
        }
    }

    public void upgradeHandPower(Board b, Player p)
    {
        if(isHighCard(b,p)) p.setHandPower(1);
        if(isPair(b,p)) p.setHandPower(2);
        if(isDoublePair(b,p)) p.setHandPower(3);
        if(isTriple(b,p)) p.setHandPower(4);
        if(isStreet(b,p)) p.setHandPower(5);
        if(isStraightFlush(b,p)) p.setHandPower(9);
        if(isRoyalFlush(b,p)) p.setHandPower(10);
        if(!isStraightFlush(b,p))
        {
            if(isFlush(b,p)) p.setHandPower(6);
            if(isFullhouse(b,p)) p.setHandPower(7);
            if(isQuadruple(b,p)) p.setHandPower(8);
        }
    }

    public void chooseWinner()
    {
        // choose winning player
        if(!one.hasFolded && !two.hasFolded)
        {
            upgradeHandPower(b,one);
            upgradeHandPower(b,two);
            if(getStrongerPlayer(one,two,b).equals(one))
            {
                playersTurn.setText("Player 1 has won " + pot + " chips.");
                one.addChips(pot);
            }
            else if(getStrongerPlayer(one,two,b).equals(two))
            {
                playersTurn.setText("Player 2 has won " + pot + " chips.");
                two.addChips(pot);
            }
            else playersTurn.setText("Nobody has won.");
        }
        else if(one.hasFolded)
        {
            playersTurn.setText("Player 2 has won " + pot + " chips.");
            two.addChips(pot);
        }
        else
        {
            playersTurn.setText("Player 1 has won " + pot + " chips.");
            one.addChips(pot);
        }
        winnerIsChosen = true;
    }

    public void isGameOver()
    {
        if(one.getChips() > 0 && two.getChips() > 0)
        {
            round();
        }
        else
            {
                if(one.getChips() == 0) announcements.setText("Player 2 has won the Game!");
                else if(two.getChips() == 0) announcements.setText("Player 1 has won the Game!");
                playerChips1.setText("" + one.getChips());
                playerChips2.setText("" + two.getChips());
                one.setChips(3000);
                two.setChips(3000);
                countRounds = 2;
                potV.setText("0");
                roundButton.setText("RESET");
            }
    }

    // helper methods
        // visible helpers

        public void showBoardCards()
        {
            for(int i = 0; i < 5; i++)
            {
                String filename = "";
                if(b == null)
                {
                    if(j == 1) filename += "Blackhole";
                    else filename += "Karte-V";
                }
                else if(b.boardCards[i] == null)
                {
                    if(j == 1) filename += "Blackhole";
                    else filename += "Karte-V";
                }
                else
                    {
                        if (b.boardCards[i].color == Color.CLUBS) filename += "Pik-";
                        if (b.boardCards[i].color == Color.HEARTS) filename += "Herz-";
                        if (b.boardCards[i].color == Color.SPADES) filename += "Kreuz-";
                        if (b.boardCards[i].color == Color.DIAMONDS) filename += "Karo-";
                        if (b.boardCards[i].type == Type.DEUCE) filename += "2";
                        if (b.boardCards[i].type == Type.THREE) filename += "3";
                        if (b.boardCards[i].type == Type.FOUR) filename += "4";
                        if (b.boardCards[i].type == Type.FIVE) filename += "5";
                        if (b.boardCards[i].type == Type.SIX) filename += "6";
                        if (b.boardCards[i].type == Type.SEVEN) filename += "7";
                        if (b.boardCards[i].type == Type.EIGHT) filename += "8";
                        if (b.boardCards[i].type == Type.NINE) filename += "9";
                        if (b.boardCards[i].type == Type.TEN) filename += "10";
                        if (b.boardCards[i].type == Type.JACK) filename += "J";
                        if (b.boardCards[i].type == Type.QUEEN) filename += "Q";
                        if (b.boardCards[i].type == Type.KING) filename += "K";
                        if (b.boardCards[i].type == Type.ACE) filename += "A";
                    }
                filename += ".jpg";
                ImageIcon imageIcon = new ImageIcon(filename);
                Image image = imageIcon.getImage();
                Image newimg = image.getScaledInstance(69, 101, Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(newimg);
                if (i == 0) boardCard1.setIcon(imageIcon);
                else if(i == 1) boardCard2.setIcon(imageIcon);
                else if(i == 2) boardCard3.setIcon(imageIcon);
                else if(i == 3) boardCard4.setIcon(imageIcon);
                else boardCard5.setIcon(imageIcon);
            }
        }

        public void showPlayerCards(Player p, int player)
        {
            for(int i = 0; i < 2; i++)
            {
                String filename = "";
                if(p.playerCards[i].color == Color.CLUBS) filename += "Pik-";
                if(p.playerCards[i].color == Color.HEARTS) filename += "Herz-";
                if(p.playerCards[i].color == Color.SPADES) filename += "Kreuz-";
                if(p.playerCards[i].color == Color.DIAMONDS) filename += "Karo-";
                if(p.playerCards[i].type == Type.DEUCE) filename += "2";
                if(p.playerCards[i].type == Type.THREE) filename += "3";
                if(p.playerCards[i].type == Type.FOUR) filename += "4";
                if(p.playerCards[i].type == Type.FIVE) filename += "5";
                if(p.playerCards[i].type == Type.SIX) filename += "6";
                if(p.playerCards[i].type == Type.SEVEN) filename += "7";
                if(p.playerCards[i].type == Type.EIGHT) filename += "8";
                if(p.playerCards[i].type == Type.NINE) filename += "9";
                if(p.playerCards[i].type == Type.TEN) filename += "10";
                if(p.playerCards[i].type == Type.JACK) filename += "J";
                if(p.playerCards[i].type == Type.QUEEN) filename += "Q";
                if(p.playerCards[i].type == Type.KING) filename += "K";
                if(p.playerCards[i].type == Type.ACE) filename += "A";
                filename += ".jpg";
                ImageIcon imageIcon = new ImageIcon(filename);
                Image image = imageIcon.getImage();
                Image newimg = image.getScaledInstance(69, 101,  Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(newimg);
                if(player == 1)
                {
                    if(i == 0) player1Card1.setIcon(imageIcon);
                    else player1Card2.setIcon(imageIcon);
                }
                else
                    {
                        if(i == 0) player2Card1.setIcon(imageIcon);
                        else player2Card2.setIcon(imageIcon);
                    }
            }
        }

    public void testHand(int i)
    {
        for(int x = 0; x < i; x++)
        {
            Board b = new Board();
            Deck d = new Deck();
            one = new Player();
            two = new Player();
            one.setCards(d);
            two.setCards(d);
            b.Flop(d);
            b.Turn(d);
            b.River(d);
            upgradeHandPower(b,one);
            upgradeHandPower(b,two);
            if(one.getHandPower() == two.getHandPower() && one.getHandPower() == 2)
            {
                System.out.println(Arrays.toString(one.playerCards));
                System.out.println(Arrays.toString(two.playerCards));
                System.out.println(Arrays.toString(b.boardCards));
                System.out.println(one.powerValue + " | " + two.powerValue);
                System.out.println(one.highCardOne + " | " + two.highCardOne);
                System.out.println(one.highCardTwo + " | " + two.highCardTwo);
                if(getStrongerPlayer(one,two,b).equals(one))
                {
                    System.out.println("Player 1 has won");
                }
                else if(getStrongerPlayer(one,two,b).equals(two))
                {
                    System.out.println("Player 2 has won");
                }
                else
                {
                    System.out.println("Nobody has won");
                }
            }
        }
    }

    public Card[] allCards(Board b, Player p)
    {
        Card[] allCards = new Card[7];
        for(int i = 0; i < 5; i++)
        {
            allCards[i] = b.boardCards[i];
        }
        for(int i = 5; i < 7; i++)
        {
            allCards[i] = p.playerCards[i-5];
        }
        return allCards;
    }

    public int[] getValues(Board b, Player p)
    {
        int[] values = new int[7];
        for(int i = 0; i < allCards(b,p).length; i++)
        {
            if(allCards(b,p)[i] == null) values[i] = 0 - i;
            else
            {
                values[i] = allCards(b,p)[i].getVALUE();
            }
        }
        Arrays.sort(values);
        return values;
    }

    public int[] getBoardValues(Board b)
    {
        int[] values = new int[5];
        for(int i = 0; i < b.boardCards.length; i++)
        {
            if(b.boardCards[i] == null) values[i] = 0 - i;
            else
            {
                values[i] = b.boardCards[i].getVALUE();
            }
        }
        Arrays.sort(values);
        return values;
    }

    public void resetOne(){
        one.hasFolded = false;
        one.hasRaised = false;
        one.hasChecked = false;
        one.hasCalled = false;
    }

    public void reactivateButtons()
    {
        checkButton.setEnabled(true);
        callButton.setEnabled(false);
        raiseButton.setEnabled(true);
        foldButton.setEnabled(true);
        sumTextField.setEnabled(true);
        allInButton.setEnabled(true);
        roundButton.setEnabled(false);
    }

    public void resetButtons()
    {
        checkButton.setEnabled(false);
        callButton.setEnabled(false);
        raiseButton.setEnabled(false);
        foldButton.setEnabled(false);
        sumTextField.setEnabled(false);
        allInButton.setEnabled(false);
        roundButton.setEnabled(true);
        chooseWinner();
    }

    public void setPlayerTurn()
    {
        if(countRounds % 2 != 0)
        {
            playerOneTurn = true;
            playersTurn.setText("PlayerTurn: 1");
        }
        else
        {
            playerOneTurn = false;
            playersTurn.setText("PlayerTurn: 2");
        }
    }

    public void checkPlayerTurn()
    {
        playerOneTurn = !playerOneTurn;
        if(playerOneTurn) playersTurn.setText("PlayerTurn: 1");
        else playersTurn.setText("PlayerTurn: 2");
    }

    public void upgradeBlind()
    {
        bigBlind = 20;
        smallBlind = 10;
        if(countRounds >= 10 && countRounds < 20)
        {
            bigBlind = 50;
            smallBlind = 25;
        }
        else if(countRounds >= 20 && countRounds < 30)
        {
            bigBlind = 100;
            smallBlind = 50;
        }
        else  if(countRounds >= 30)
        {
            bigBlind = 200;
            smallBlind = 100;
        }
    }

    // isHandPower Checkups
    public boolean isHighCard(Board b, Player p)
    {
        if(p.playerCards[0].getVALUE() > p.playerCards[1].getVALUE())
        {
            p.highCardOne = p.playerCards[0].getVALUE();
            p.highCardTwo = p.playerCards[1].getVALUE();
        }
        else
        {
            p.highCardOne = p.playerCards[1].getVALUE();
            p.highCardTwo = p.playerCards[0].getVALUE();
        }
        if(getBoardValues(b)[0] > p.highCardOne)
        {
            p.highCardOne = 0;
            p.highCardTwo = 0;
        }
        if(getBoardValues(b)[1] > p.highCardTwo)
        {
            p.highCardTwo = 0;
        }
        return true;
    }

    public boolean isPair(Board b, Player p)
    {
        int pairValue;
        int lowestBoardCard;
        int secondLowestBoardCard;
        int[] values = getValues(b,p);
        for(int i = 0; i < values.length-1; i++)
        {
            if(values[i] == values[i + 1])
            {
                p.highCardOne = 0;
                p.highCardTwo = 0;
                pairValue = values[i];
                p.powerValue = values[i];
                // set lowest + second lowest board card
                if(getBoardValues(b)[0] != pairValue)
                {
                    lowestBoardCard = getBoardValues(b)[0];
                    if(getBoardValues(b)[1] != pairValue) secondLowestBoardCard = getBoardValues(b)[1];
                    else
                    {
                        if(getBoardValues(b)[2] != pairValue) secondLowestBoardCard = getBoardValues(b)[2];
                        else secondLowestBoardCard = getBoardValues(b)[3];
                    }
                }
                else
                {
                    if(getBoardValues(b)[1] != pairValue)
                    {

                        lowestBoardCard = getBoardValues(b)[1];
                        secondLowestBoardCard = getBoardValues(b)[2];
                    }
                    else
                    {
                        lowestBoardCard = getBoardValues(b)[2];
                        secondLowestBoardCard = getBoardValues(b)[3];
                    }
                }
                // case BoardPair P0 > P1
                if(p.playerCards[0].getVALUE() > lowestBoardCard
                        && p.playerCards[0].getVALUE() != pairValue && p.playerCards[1].getVALUE() != pairValue)
                {
                    p.highCardOne = p.playerCards[0].getVALUE();
                    if(p.playerCards[1].getVALUE() > secondLowestBoardCard) p.highCardTwo = p.playerCards[1].getVALUE();
                }
                // case BoardPair P0 < P1
                if(p.playerCards[1].getVALUE() > p.playerCards[0].getVALUE() && p.playerCards[0].getVALUE() != pairValue
                        && p.playerCards[1].getVALUE() > lowestBoardCard
                        && p.playerCards[1].getVALUE() != pairValue)
                {
                    p.highCardOne = p.playerCards[1].getVALUE();
                    if(p.playerCards[0].getVALUE() > secondLowestBoardCard) p.highCardTwo = p.playerCards[0].getVALUE();
                    else if(p.playerCards[0].getVALUE() < secondLowestBoardCard) p.highCardTwo = 0;
                }
                // case P0 == pairValue
                if(p.playerCards[0].getVALUE() == pairValue)
                {
                    if(p.playerCards[1].getVALUE() > secondLowestBoardCard) p.highCardOne = p.playerCards[1].getVALUE();
                }
                // case P1 == pairValue
                if(p.playerCards[1].getVALUE() == pairValue)
                {
                    if(p.playerCards[0].getVALUE() > secondLowestBoardCard) p.highCardOne = p.playerCards[0].getVALUE();
                }
                // case handPair
                if(p.playerCards[0].getVALUE() == p.playerCards[1].getVALUE())
                {
                    p.highCardOne = 0;
                    p.highCardTwo = 0;
                }

                return true;
            }
        }
        return false;
    }

    public boolean isDoublePair(Board b, Player p)
    {
        int lowestBoardCard = getBoardValues(b)[0];
        int pairValue = 0;
        int secondPairValue;
        int[] values = getValues(b,p);
        for(int i = 0; i < values.length-1; i++)
        {
            if(values[i] == values[i + 1])
            {
                pairValue = values[i];
            }
            if(pairValue > 0) i+=10;
        }
        for(int i = 0; i < values.length -1; i++)
        {
            if(values[i] == values[i + 1] && values[i] != pairValue)
            {
                p.highCardOne = 0;
                secondPairValue = values[i];
                p.powerValue = values[i];
                p.secondPowerValue = pairValue;
                // set lowest Card
                if(getBoardValues(b)[1] != pairValue && getBoardValues(b)[1] != secondPairValue) lowestBoardCard = getBoardValues(b)[1];
                if(getBoardValues(b)[2] != pairValue && getBoardValues(b)[2] != secondPairValue) lowestBoardCard = getBoardValues(b)[2];
                if(getBoardValues(b)[3] != pairValue && getBoardValues(b)[3] != secondPairValue) lowestBoardCard = getBoardValues(b)[3];
                if(getBoardValues(b)[4] != pairValue && getBoardValues(b)[4] != secondPairValue) lowestBoardCard = getBoardValues(b)[4];
                if(p.playerCards[0].getVALUE() > lowestBoardCard
                        && p.playerCards[0].getVALUE() != pairValue
                        && p.playerCards[0].getVALUE() != secondPairValue) p.highCardOne = p.playerCards[0].getVALUE();
                if(p.playerCards[1].getVALUE() > lowestBoardCard
                        && p.playerCards[1].getVALUE() != pairValue
                        && p.playerCards[1].getVALUE() != secondPairValue)
                {
                    if(p.playerCards[0].getVALUE() == pairValue
                            || p.playerCards[0].getVALUE() == secondPairValue) p.highCardOne = p.playerCards[1].getVALUE();
                    if(p.playerCards[1].getVALUE() > p.playerCards[0].getVALUE()) p.highCardOne = p.playerCards[1].getVALUE();
                }
                for(int j = 0; j < values.length-1; j++)
                {
                    // case 3 pairs
                    if (values[j] == values[j + 1] && values[j] != pairValue && values[j] != secondPairValue)
                    {
                        p.powerValue = values[j];
                        p.secondPowerValue = secondPairValue;
                        p.highCardOne = 0;
                        // set lowest card again (case 3 pairs)
                        lowestBoardCard = getBoardValues(b)[0];
                        if(getBoardValues(b)[1] != values[j] && getBoardValues(b)[1] != secondPairValue) lowestBoardCard = getBoardValues(b)[1];
                        if(getBoardValues(b)[2] != values[j] && getBoardValues(b)[2] != secondPairValue) lowestBoardCard = getBoardValues(b)[2];
                        if(getBoardValues(b)[3] != values[j] && getBoardValues(b)[3] != secondPairValue) lowestBoardCard = getBoardValues(b)[3];
                        if(getBoardValues(b)[4] != values[j] && getBoardValues(b)[4] != secondPairValue) lowestBoardCard = getBoardValues(b)[4];
                        if(p.playerCards[0].getVALUE() > lowestBoardCard
                                && p.playerCards[0].getVALUE() != values[j]
                                && p.playerCards[0].getVALUE() != secondPairValue) p.highCardOne = p.playerCards[0].getVALUE();
                        if(p.playerCards[1].getVALUE() > lowestBoardCard
                                && p.playerCards[1].getVALUE() != values[j]
                                && p.playerCards[1].getVALUE() != secondPairValue)
                        {
                            if(p.playerCards[0].getVALUE() == values[j]
                                    || p.playerCards[0].getVALUE() == secondPairValue) p.highCardOne = p.playerCards[1].getVALUE();
                            if(p.playerCards[1].getVALUE() > p.playerCards[0].getVALUE()) p.highCardOne = p.playerCards[1].getVALUE();
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean isTriple(Board b, Player p)
    {
        int[] values = getValues(b,p);
        int tripleValue;
        int lowestBoardCard;
        int secondLowestBoardCard;
        for(int i = 0; i < values.length-2; i++)
        {
            if(values[i] == values[i + 1] && values[i] == values[i + 2])
            {
                tripleValue = values[i];
                p.highCardOne = 0;
                p.highCardTwo = 0;
                p.powerValue = tripleValue;
                // setting lowest and second lowest board card
                // [3,6,6,6,8] checking if first number != triple
                if(getBoardValues(b)[0] != tripleValue)
                {
                    lowestBoardCard = getBoardValues(b)[0];
                    // [3,4,6,6,8]
                    if(getBoardValues(b)[1] != tripleValue) secondLowestBoardCard = getBoardValues(b)[1];
                    else
                    {
                        // [3,4,5,6,6]
                        if(getBoardValues(b)[2] != tripleValue) secondLowestBoardCard = getBoardValues(b)[2];
                        else
                        {
                            // [3,4,5,6,8]
                            if(getBoardValues(b)[3] != tripleValue) secondLowestBoardCard = getBoardValues(b)[3];
                            else secondLowestBoardCard = getBoardValues(b)[4];
                        }
                    }
                }
                // [3,3,3,5,6]
                else
                {
                    // [3,4,6,8,9]
                    if(getBoardValues(b)[1] != tripleValue)
                    {
                        lowestBoardCard = getBoardValues(b)[1];
                        secondLowestBoardCard = getBoardValues(b)[2];
                    }
                    else
                    {
                        // [3,3,6,8,9]
                        if(getBoardValues(b)[2] != tripleValue)
                        {
                            lowestBoardCard = getBoardValues(b)[2];
                            secondLowestBoardCard = getBoardValues(b)[3];
                        }
                        // [3,3,3,6,9]
                        else
                        {
                            lowestBoardCard = getBoardValues(b)[3];
                            secondLowestBoardCard = getBoardValues(b)[4];
                        }
                    }
                }
                // checking if player cards are higher than lowest board cards
                // case triple on board
                if(p.playerCards[0].getVALUE() > lowestBoardCard
                        && p.playerCards[0].getVALUE() != tripleValue && p.playerCards[1].getVALUE() != tripleValue)
                {
                    p.highCardOne = p.playerCards[0].getVALUE();
                    // case P0 > P1
                    if(p.playerCards[1].getVALUE() != tripleValue && p.playerCards[1].getVALUE() < p.playerCards[0].getVALUE()
                            && p.playerCards[1].getVALUE() > secondLowestBoardCard)
                    {
                        p.highCardTwo = p.playerCards[1].getVALUE();
                    }
                    // case P1 > P0
                    else if(p.playerCards[1].getVALUE() != tripleValue && p.playerCards[1].getVALUE() > p.playerCards[0].getVALUE())
                    {
                        p.highCardOne = p.playerCards[1].getVALUE();
                        if(p.playerCards[0].getVALUE() > secondLowestBoardCard) p.highCardTwo = p.playerCards[0].getVALUE();
                    }
                }
                else if(p.playerCards[1].getVALUE() != tripleValue && p.playerCards[1].getVALUE() > lowestBoardCard
                        && p.playerCards[0].getVALUE() != tripleValue)
                {
                    p.highCardOne = p.playerCards[1].getVALUE();
                }
                // case triple with P1
                else if(p.playerCards[0].getVALUE() > secondLowestBoardCard && p.playerCards[0].getVALUE() != tripleValue)
                {
                    p.highCardOne = p.playerCards[0].getVALUE();
                }
                // case triple with P0
                else if(p.playerCards[1].getVALUE() > secondLowestBoardCard && p.playerCards[1].getVALUE() != tripleValue)
                {
                    p.highCardOne = p.playerCards[1].getVALUE();
                }
                return true;
            }
        }
        return false;
    }

    public boolean isStreet(Board b, Player p)
    {
        int[] values = getValues(b,p);
        for(int i = 0; i < values.length-1; i++)
        {
            if (values[i] == values[i+1]) values[i] = 0;
        }
        Arrays.sort(values);
        for(int i = 0; i < 3; i++)
        {
            // if isStreet = true
            if(     values[i] > 0 &&
                    values[i] + 1 == values[i+1] &&
                    values[i+1] +1 == values[i+2] &&
                    values[i+2]+1 == values[i+3] &&
                    values[i+3]+1 == values[i+4])
            {
                // [3,4,5,6,7,8,10]
                if(i == 0 && values[4] +1 == values[5] && values[5] + 1 != values[6])
                {
                    for(int j = 0; j < 5; j++)
                    {
                        streetValues[j] = values[i]+j + 1;
                    }
                    p.powerValue = streetValues[4];
                    return true;
                }
                // [3,4,5,6,7,8,9]
                else if(i == 0 && values[4] +1 == values[5] && values[5] + 1 == values[6])
                {
                    for(int j = 0; j < 5; j++)
                    {
                        streetValues[j] = values[i]+j + 2;
                    }
                    p.powerValue = streetValues[4];
                    return true;
                }
                // [3,5,6,7,8,9,10]
                if(i == 1 && values[5] + 1 == values[6])
                {
                    for(int j = 0; j < 5; j++)
                    {
                        streetValues[j] = values[i]+j + 1;
                    }
                    p.powerValue = streetValues[4];
                    return true;
                }
                else
                {
                    for(int j = 0; j < 5; j++)
                    {
                        streetValues[j] = values[i]+j;
                    }
                    p.powerValue = streetValues[4];
                    return true;
                }
            }
            // [2,3,4,5,9,10,14]
            if(     values[i] == 2 &&
                    values[i] + 1 == values[i+1] &&
                    values[i+1] +1 == values[i+2] &&
                    values[i+2]+1 == values[i+3] && values[6] == 14)
            {
                streetValues[0] = 14;
                for(int j = 0; j < 4; j++)
                {
                    streetValues[j + 1] = values[i]+j;
                }
                p.powerValue = streetValues[4];
                return true;
            }
        }
        return false;
    }

    public boolean isFlush(Board b, Player p)
    {
        int valueRN = 0;
        int clubsC = 0;
        int heartsC = 0;
        int spadesC = 0;
        int diamondsC = 0;
        int boardClubsC = 0;
        int boardHeartsC = 0;
        int boardSpadesC = 0;
        int boardDiamondsC = 0;

        int count = 0;
        int[] boardValues = getBoardValues(b);
        for(Card c :allCards(b,p))
        {

            if(allCards(b,p)[count] != null)
            {
                switch (c.color)
                {
                    case CLUBS: clubsC++;
                        break;
                    case HEARTS: heartsC++;
                        break;
                    case SPADES: spadesC++;
                        break;
                    case DIAMONDS: diamondsC++;
                        break;
                }
            }
            count ++;
        }
        if(clubsC > 4)
        {
            for(Card c :p.playerCards)
            {
                if (c.color.equals(Color.CLUBS))
                {
                    if(valueRN < c.getVALUE()) valueRN = c.getVALUE();
                }
            }
            for(Card c :b.boardCards)
            {
                if (c.color.equals(Color.CLUBS))
                {
                    boardClubsC++;
                    if(boardClubsC > 4)
                    {
                        if(boardValues[0] > valueRN)
                        {
                            valueRN = 0;
                        }
                    }
                }
            }
            p.powerValue = valueRN;
            return true;
        }
        if(heartsC > 4)
        {
            for(Card c :p.playerCards)
            {
                if (c.color.equals(Color.HEARTS))
                {
                    if(valueRN < c.getVALUE()) valueRN = c.getVALUE();
                }
            }
            for(Card c :b.boardCards)
            {
                if (c.color.equals(Color.HEARTS))
                {
                    boardHeartsC++;
                    if(boardHeartsC > 4)
                    {
                        if(boardValues[0] > valueRN)
                        {
                            valueRN = 0;
                        }
                    }
                }
            }
            p.powerValue = valueRN;
            return true;
        }
        if(spadesC > 4)
        {
            for(Card c :p.playerCards)
            {
                if (c.color.equals(Color.SPADES))
                {
                    if(valueRN < c.getVALUE()) valueRN = c.getVALUE();
                }
            }
            for(Card c :b.boardCards)
            {
                if (c.color.equals(Color.SPADES))
                {
                    boardSpadesC++;
                    if(boardSpadesC > 4)
                    {
                        if(boardValues[0] > valueRN)
                        {
                            valueRN = 0;
                        }
                    }
                }
            }
            p.powerValue = valueRN;
            return true;
        }
        if(diamondsC > 4)
        {
            for(Card c :p.playerCards)
            {
                if (c.color.equals(Color.DIAMONDS))
                {
                    if(valueRN < c.getVALUE()) valueRN = c.getVALUE();
                }
            }
            for(Card c :b.boardCards)
            {
                if (c.color.equals(Color.DIAMONDS))
                {
                    boardDiamondsC++;
                    if(boardDiamondsC > 4)
                    {
                        if(boardValues[0] > valueRN)
                        {
                            valueRN = 0;
                        }
                    }
                }
            }
            p.powerValue = valueRN;
            return true;
        }

        return false;
    }

    public boolean isFullhouse(Board b, Player p)
    {
        boolean triple = false;
        int tripleValue = 0;
        int[] values = getValues(b,p);
        for(int i = 0; i < values.length-2; i++)
        {
            if(values[i] == values[i + 1] && values[i] == values[i + 2])
            {
                tripleValue = values[i];
                triple = true;
            }
        }
        if(triple)
        {
            for(int i = 0; i < values.length -1; i++)
            {
                // if is triple + double pair
                if(values[i] == values[i + 1] && values[i] != tripleValue)
                {
                    p.powerValue = tripleValue;
                    p.secondPowerValue = values[i];
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isQuadruple(Board b, Player p)
    {
        int[] values = getValues(b,p);
        int quadrupleValue;
        int lowestCardOnBoard = 0;
        for(int i = 0; i < values.length-3; i++)
        {
            // if isQuadruple = true
            if(values[i] == values[i + 1] && values[i] == values[i + 2]
                    && values[i] == values[i + 3])
            {
                p.powerValue = values[i];
                quadrupleValue = values[i];
                p.highCardOne = 0;
                p.highCardTwo = 0;
                // [5,7,7,7,7]
                if(getBoardValues(b)[0] != quadrupleValue) lowestCardOnBoard = getBoardValues(b)[0];
                    // [5,5,5,5,7]
                else if(getBoardValues(b)[4] != quadrupleValue) lowestCardOnBoard = getBoardValues(b)[4];
                // case P0 = high card
                if(p.playerCards[0].getVALUE() > lowestCardOnBoard && p.playerCards[0].getVALUE() != quadrupleValue
                        && p.playerCards[1].getVALUE() != quadrupleValue)
                {
                    p.highCardOne = p.playerCards[0].getVALUE();
                    // case P1 > P0
                    if(p.playerCards[1].getVALUE() > p.playerCards[0].getVALUE()) p.highCardOne = p.playerCards[1].getVALUE();
                }
                // case P1 = high card
                else if(p.playerCards[1].getVALUE() > lowestCardOnBoard && p.playerCards[1].getVALUE() != quadrupleValue
                        && p.playerCards[0].getVALUE() != quadrupleValue) p.highCardOne = p.playerCards[1].getVALUE();
                return true;
            }
        }
        return false;
    }

    public boolean isStraightFlush(Board b, Player p)
    {
        int clubsC = 0;
        int heartsC = 0;
        int spadesC = 0;
        int diamondsC = 0;
        Card[] allCards = allCards(b,p);
        if(isStreet(b,p))
        {
            for(int i = 0; i < 5; i++)
            {
                for(int j = 0; j < 7; j++)
                {
                    if (allCards[j] != null && streetValues[i] == allCards[j].getVALUE())
                    {
                        switch (allCards[j].color)
                        {
                            case CLUBS: clubsC++;
                                break;
                            case HEARTS: heartsC++;
                                break;
                            case SPADES: spadesC++;
                                break;
                            case DIAMONDS: diamondsC++;
                                break;
                        }
                    }
                }
            }
            if(clubsC>4 || heartsC>4 || spadesC>4 || diamondsC>4)
            {
                return true;
            }
        }
        return false;
    }

    public boolean isRoyalFlush(Board b, Player p)
    {
        if(isStraightFlush(b,p)&& streetValues[4] == 14)
        {
            return true;
        }
        return false;
    }
}
