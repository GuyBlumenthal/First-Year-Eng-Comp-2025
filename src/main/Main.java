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

    /**
     *  The function will take a fixed flop and shuffle through a variable size hand to
     *  select the best  possible hand for each poker hand type.
     * @param flop The fixed size, 3 card flop
     * @param hand This is the hand that can be shuffled to select 2 cards
     */
    public static void multiCardBest(Card[] flop, Card[] hand) {
        for (Hand h : Hand.values()) {
            // We will loop through each poker hand type and evaluate the flop & hand
            // Combinations most suited for the target poker hand
            Card[] bestCards = {};

            // Access the permutation func to get and loop through all possible five card hands
            Card[][] perms = permutations(flop, hand);
            for (Card[] cHand : perms) {
                if (maxHand(cHand, h) == h) {
                    // Check to see if when target our desired poker hand, we can actually make
                    // it work with the current permuation and if so, find the "prio" of the cards for this
                    // poker hand type - See static Card[] keyCardVals(cards, hand)
                    Card[] goodCards = keyCardVals(cHand, h);

                    if (bestCards.length == 0)
                        // If the best cards are nonexistent, then what we have now is the best
                        bestCards = goodCards;
                    else {
                        for (int c = 0; c < bestCards.length; c++) {
                            // Otherwise, we loop through the good and best cards and compare one
                            // By one in order of their priority for the target poker hand
                            if (bestCards[c].val > goodCards[c].val) {
                                // If at any point, the best cards have a higher val than good cards,
                                // They are still best
                                break;
                            } else if (goodCards[c].val > bestCards[c].val) {
                                // If at any point, the good cards have a higher val than best cards,
                                // They are now the best
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

    /**
     * The perumations function returns all combinations of the flop with two hand cards
     * @param flop the fixed set of cards that is in each permutation
     * @param hand the set of cards that will shuffle in the permutations
     * @return an array of card arrays representing all permutations of flop with two hand cards
     */
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

    // Sort the cards in order and find the best/max poker hand for the cards
    public static Hand fiveCardBest (Card[] cards) {
        Arrays.sort(cards, Comparator.comparingInt((c) -> c.val));
        return maxHand(cards);
    }

    // Creates a random card, with ace as 14 instead of 1
    public static Card randomCard () {
        int randVal = (int) Math.floor(Math.random() * 13) + 2;
        int suit = (int) Math.floor(Math.random() * 4);
        return new Card(randVal, suit);
    }

    // Runs the max hand with a target of the royal flush, the highest possible
    // Poker hand
    public static Hand maxHand (Card[] cards) {
        return maxHand(cards, Hand.R_FLUSH);
    }

    // Card array is sent in sorted based on value (Low -> High)
    public static Hand maxHand (Card[] cards, Hand targetHand) {
        // Check for flush
        boolean sameSuit = true;
        // Check for straight
        boolean sequential = true;
        // Pair flag: 1 -> xx1,xx2, --> 1 or 2 pairs, x1x three of a kind, 1xx four of a kind
        int pairs = 0;

        // Sort the array on num
        Arrays.sort(cards, Comparator.comparingInt((c) -> c.val));

        // pVal is the initial card here
        int pVal = cards[0].val;
        // cSuit is the suit of the theoretical flush
        int cSuit = cards[0].suit;

        for (Card card : cards) {
            // Loop through cards
            if (card.val != pVal && !(cards[0].val == 2 && card.val == 14)) {
                // If the card is not the expected next value, its not sequential
                // HOWEVER, if the first card is 2 and the last card is ace (14), it could be sequential
                sequential = false;
            }
            if (card.suit != cSuit) {
                // If the suit doesn't match, the cards ain't a batch (I.e. not a flush :P)
                sameSuit = false;
            }
            // Increment the pVal, looking for in sequence
            pVal++;
        }
        // The pair "calculator", remember the cards are in order
        for (int i = 0; i < cards.length - 1; ) {
            // Set the initial value when looking for the pairs, threes and four of a kinds
            int cVal = cards[i].val;
            // Increment the counter to check the next card
            i++;
            if (cVal == cards[i].val) {
                // If the next card is a match, we are looking at a pair so xx1 or xx2, i.e. +1
                pairs++;
                i++;
                if (i < cards.length && cVal == cards[i].val) {
                    // If the next card is a match, we are looking at a three of a kind so +10
                    pairs += 10;
                    i++;
                    if (i < cards.length && cVal == cards[i].val) {
                        // Finally if we are looking at a four of kind, 1xx, add +100 and +1 as this is another pair
                        pairs += 101;
                        i++;
                    }
                }
            }
        }

        // Assume the lowest hand first
        Hand bestHand = Hand.HIGH;

        // Note we are using Hand.compare(Hand, target) to make sure we get what we look for
        // -> Always higher unless its target
        if (sequential) {
            // Seq means straight of some kind
            if (sameSuit) {
                // Flush of some kind
                if (cards[cards.length - 2].val == 13)
                    // We have a king and not the highest card, i.e. we have ace
                    // Therefore, royal flush
                    bestHand = Hand.R_FLUSH;
                // Otherwise its a straight flush
                bestHand = bestHand.compare(Hand.S_FLUSH, targetHand);
            }
            // No same suit, normal straight
            bestHand = bestHand.compare(Hand.STRAIGHT, targetHand);
        }

        if (sameSuit) {
            // No straight but flush so normal flush
            bestHand = bestHand.compare(Hand.FLUSH, targetHand);
        }

        // Break down the pair flag to find the kinds of poker hands we have
        if (pairs > 0) {
            if (pairs >= 100)
                // Looking for 1xx
                bestHand = bestHand.compare(Hand.F_KIND, targetHand);
            if (pairs % 100 > 11)
                // Looking for x12
                // A full house is a three of a kind and a pair but a three of a kind is also a pair,
                // So looking for two pairs and a three kind
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

    /**
     * Takes a set of cards and a poker hand type and determines their importance in deciding tie breakers
     *  E.g. a one pair will have its pairs first and the rest of the cards in decreasing order
     * @param cards the set of 5 cards in hand
     * @param hand the poker hand we are looking for prio on
     * @return the cards in prio order
     */
    public static Card[] keyCardVals (Card[] cards, Hand hand) {
        // For these, the key cards are just in decreasing order
        switch (hand) {
            case R_FLUSH, S_FLUSH, STRAIGHT, FLUSH, HIGH -> {
                Arrays.sort(cards, Comparator.comparingInt((c) -> -c.val));
                return cards;
            }
            default -> {
                // Sort by the pairs
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
