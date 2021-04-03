package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class Main {

    static FileOutputStream oStream;
    static boolean fileOut = true;

    // Enum declaring poker hands
    public enum Hand {
        HIGH(0, "high card"), O_PAIR(1, "one pair"),
        T_PAIR(2, "two pair"), T_KIND(3, "three of a kind"),
        STRAIGHT(4, "straight"), FLUSH(5, "flush"), F_HOUSE(6, "full house"),
        F_KIND(7, "four of a kind"), S_FLUSH(8, "straight flush"), R_FLUSH(9, "royal flush");
        public int strength;
        public String loc_name;
        Hand(int s, String n) {
            this.strength = s;
            this.loc_name = n;
        }
        // Compare and return the higher strength hand, prio for the target hand
        public Hand compare(Hand hand, Hand target) {
            if (hand == target)
                return hand;
            if (this == target)
                return this;
            if (this.strength > hand.strength)
                return this;
            return hand;
        }
        @Override
        public String toString() {
            return loc_name;
        }
    }

    // The Unit Card class
    // Used to store card data
    static class Card {
        int val;
        int suit;
        public Card (int val, int suit) {
            this.val = val;
            this.suit = suit;
        }
        private String valString () {
            return switch (val) {
                case 14 -> "Ace";
                case 13 -> "King";
                case 12 -> "Queen";
                case 11 -> "Jack";
                default -> Integer.toString(val);
            };
        }
        private String suitString(){
            return switch (suit) {
                case 0 -> "Clubs";
                case 1 -> "Spades";
                case 2 -> "Hearts";
                case 3 -> "Diamonds";
                default -> "NO SUIT";
            };
        }
        @Override
        public String toString() {
            return valString() + " of " + suitString();
        }
    }

    public static void main (String[] args) {

        // Open the output stream in case we are looking to output to a file
        try {
            oStream = new FileOutputStream(new File("output.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // The flop, this will not change as we draw new cards
        Card[] flop = {
            randomCard(),
            randomCard(),
            randomCard()
        };

        // The initial two card hand
        Card[] hand = {
            randomCard(),
            randomCard()
        };

        // Here we will output the flop and hand for clarity

        output("Flop (Does not Change)");
        outputCards(flop);

        output ("With a hand of size 2");
        outputCards(hand);

        // Analysing the two card hand
        output("The result of this hand is a " + fiveCardBest(combine(hand,flop)) + '\n');

        // Add a card to the hand
        hand = combine(hand, randomCard());

        output("With a hand of size 3");
        outputCards(hand);

        // Analyse the 3 card hand
        multiCardBest(flop, hand);

        // Add a card to the hand
        hand = combine(hand, randomCard());

        output("With a hand of size 4");
        outputCards(hand);

        // Analyse the 4 card hand
        multiCardBest(flop, hand);

    }

    public static void multiCardBest(Card[] flop, Card[] hand) {
        for (Hand h : Hand.values()) {
            Card[] bestCards = {};

            Card[][] perms = permutations(flop, hand);
            for (Card[] cHand : perms) {
                if (maxHand(cHand, h) == h) {
                    Card[] goodCards = keyCardVals(cHand, h);

                    if (bestCards.length == 0)
                        bestCards = goodCards;
                    else {
                        for (int c = 0; c < bestCards.length; c++) {
                            if (bestCards[c].val > goodCards[c].val) {
                                break;
                            } else if (goodCards[c].val > bestCards[c].val) {
                                bestCards = goodCards;
                                break;
                            }
                        }
                    }
                }
            }

            output("Best hand of a " + h + ": ");
            outputCards(bestCards);
        }
    }

    static Card[][] permutations(Card[] flop, Card[] hand) {
        Card[][] perms = new Card[sum(hand.length - 1)][];
        int index = 0;
        for (int i = 0; i < hand.length - 1; i++) {
            for (int j = i + 1; j < hand.length; j++) {
                Card[] cHand = combine(flop, combine(hand[i], hand[j]));
                perms [index] = cHand;
                index++;
            }
        }
        return perms;
    }

    public static Hand fiveCardBest (Card[] cards) {
        Arrays.sort(cards, Comparator.comparingInt((c) -> c.val));
        return maxHand(cards);
    }

    public static Card randomCard () {
        int randVal = (int) Math.floor(Math.random() * 13) + 2;
        int suit = (int) Math.floor(Math.random() * 4);
        return new Card(randVal, suit);
    }

    public static Hand maxHand (Card[] cards) {
        return maxHand(cards, Hand.R_FLUSH);
    }

    // Card array is sent in sorted based on value (Low -> High)
    public static Hand maxHand (Card[] cards, Hand targetHand) {
        boolean sameSuit = true;
        boolean sequential = true;
        // 1 -> xx1,xx2, --> 1 or 2 pairs, x1x three of a kind, 1xx four of a kind
        int pairs = 0;

        Arrays.sort(cards, Comparator.comparingInt((c) -> c.val));

        int pVal = cards[0].val;
        int cSuit = cards[0].suit;
        for (Card card : cards) {
            if (card.val != pVal && !(cards[0].val == 2 && card.val == 14)) {
                sequential = false;
            }
            if (card.suit != cSuit) {
                sameSuit = false;
            }
            pVal++;
        }
        for (int i = 0; i < cards.length - 1; ) {
            int cVal = cards[i].val;
            i++;
            if (cVal == cards[i].val) {
                pairs++;
                i++;
                if (i < cards.length && cVal == cards[i].val) {
                    pairs += 9;
                    i++;
                    if (i < cards.length && cVal == cards[i].val) {
                        pairs += 102;
                        i++;
                    }
                }
            }
        }

        Hand bestHand = Hand.HIGH;

        if (sequential) {
            if (sameSuit) {
                if (cards[cards.length - 2].val == 13)
                    bestHand = Hand.R_FLUSH;
                bestHand = bestHand.compare(Hand.S_FLUSH, targetHand);
            }
            bestHand = bestHand.compare(Hand.STRAIGHT, targetHand);
        }

        if (sameSuit) {
            bestHand = bestHand.compare(Hand.FLUSH, targetHand);
        }

        if (pairs > 0) {
            if (pairs >= 100)
                // Looking for 1xx
                bestHand = bestHand.compare(Hand.F_KIND, targetHand);
            if (pairs % 100 > 10)
                // Looking for x11
                bestHand = bestHand.compare(Hand.F_HOUSE, targetHand);
            if (pairs % 100 >= 10)
                // Looking for x1x
                bestHand = bestHand.compare(Hand.T_KIND, targetHand);
            if (pairs % 10 > 1)
                // Looking for xx2
                bestHand = bestHand.compare(Hand.T_PAIR, targetHand);
            if (pairs % 10 >= 1)
                bestHand = bestHand.compare(Hand.O_PAIR, targetHand);
        }

        return bestHand;
    }

    public static Card[] keyCardVals (Card[] cards, Hand hand) {
        // For these, the key cards are just in decreasing order
        switch (hand) {
            case R_FLUSH, S_FLUSH, STRAIGHT, FLUSH, HIGH -> {
                Arrays.sort(cards, Comparator.comparingInt((c) -> -c.val));
                return cards;
            }
            default -> {
                Arrays.sort(cards, (c1, c2) -> {
                    int c1Count = 0;
                    int c2Count = 0;
                    for (Card card : cards) {
                        if (card.val == c1.val)
                            c1Count++;
                        if (card.val == c2.val)
                            c2Count++;
                    }
                    int comp = c2Count - c1Count;
                    if (comp == 0) {
                        return c2.val - c1.val;
                    }
                    return comp;
                });
                return cards;
            }
        }
    }

    public static Card[] combine(Card c1, Card c2) {
        return new Card[]{c1, c2};
    }

    public static Card[] combine(Card[] c1, Card c2) {
        Card[] cf = {c2};
        return combine(c1, cf);
    }

    public static Card[] combine(Card[] c1, Card[] c2) {
        Card[] cf = new Card[c1.length + c2.length];
        System.arraycopy(c1, 0, cf, 0, c1.length);
        System.arraycopy(c2, 0, cf, c1.length, c2.length);
        return cf;
    }

    static void output (Object o) {
        if (fileOut) {
            try {
                for (char c : o.toString().toCharArray()) {
                    oStream.write(c);
                }
                oStream.write('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            System.out.println(o.toString());
    }

    static void outputCards (Card[] cards) {
        for (Card c : cards) {
            output('\t' + c.toString());
        }
        output(' ');
    }

    static int sum (int n) {
        int s = 0;
        for (int i = 1; i <= n; i++) {
            s += i;
        }
        return s;
    }

}
