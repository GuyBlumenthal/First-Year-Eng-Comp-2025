package main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @org.junit.jupiter.api.Test
    void fiveCardBest() {
        Main.Card[] high = {
                new Main.Card(0, 1),
                new Main.Card(2, 1),
                new Main.Card(1, 0),
                new Main.Card(3, 1),
                new Main.Card(10, 1),
        };
        Main.Card[] onePair = {
                new Main.Card(0, 0),
                new Main.Card(2, 1),
                new Main.Card(2, 1),
                new Main.Card(3, 1),
                new Main.Card(4, 1),
        };
        Main.Card[] twoPair = {
                new Main.Card(3, 1),
                new Main.Card(2, 1),
                new Main.Card(2, 0),
                new Main.Card(3, 1),
                new Main.Card(4, 1),
        };
        Main.Card[] threeKind = {
                new Main.Card(1, 0),
                new Main.Card(2, 1),
                new Main.Card(1, 1),
                new Main.Card(1, 1),
                new Main.Card(4, 1),
        };
        Main.Card[] straight = {
                new Main.Card(0, 1),
                new Main.Card(1, 0),
                new Main.Card(2, 0),
                new Main.Card(3, 0),
                new Main.Card(4, 0),
        };
        Main.Card[] flush = {
                new Main.Card(0, 1),
                new Main.Card(2, 1),
                new Main.Card(2, 1),
                new Main.Card(3, 1),
                new Main.Card(4, 1),
        };
        Main.Card[] fullHouse = {
                new Main.Card(3, 1),
                new Main.Card(2, 1),
                new Main.Card(2, 0),
                new Main.Card(3, 1),
                new Main.Card(3, 1),
        };
        Main.Card[] fourKind = {
                new Main.Card(2, 0),
                new Main.Card(2, 1),
                new Main.Card(2, 1),
                new Main.Card(3, 1),
                new Main.Card(2, 1),
        };
        Main.Card[] straightFlush = {
                new Main.Card(0, 1),
                new Main.Card(1, 1),
                new Main.Card(2, 1),
                new Main.Card(3, 1),
                new Main.Card(4, 1),
        };
        Main.Card[] royalFlush = {
                new Main.Card(10, 1),
                new Main.Card(11, 1),
                new Main.Card(12, 1),
                new Main.Card(13, 1),
                new Main.Card(14, 1),
        };


        assertEquals(Main.fiveCardBest(high), Main.Hand.HIGH);
        assertEquals(Main.fiveCardBest(onePair), Main.Hand.O_PAIR);
        assertEquals(Main.fiveCardBest(twoPair), Main.Hand.T_PAIR);
        assertEquals(Main.fiveCardBest(threeKind), Main.Hand.T_KIND);
        assertEquals(Main.fiveCardBest(straight), Main.Hand.STRAIGHT);
        assertEquals(Main.fiveCardBest(flush), Main.Hand.FLUSH);
        assertEquals(Main.fiveCardBest(fullHouse), Main.Hand.F_HOUSE);
        assertEquals(Main.fiveCardBest(fourKind), Main.Hand.F_KIND);
        assertEquals(Main.fiveCardBest(straightFlush), Main.Hand.S_FLUSH);
        assertEquals(Main.fiveCardBest(royalFlush), Main.Hand.R_FLUSH);

    }

    @Test
    void permutations() {
        Main.Card[] handSizeTwo = {
                new Main.Card(10, 1),
                new Main.Card(11, 1)
        };
        Main.Card[] handSizeThree = {
                new Main.Card(10, 1),
                new Main.Card(11, 1),
                new Main.Card(12, 0)
        };


        Main.Card[] handSizeFour = {
                new Main.Card(10, 1),
                new Main.Card(11, 1),
                new Main.Card(12, 0),
                new Main.Card(13, 0)
        };

        Main.Card[][] pTwo = Main.permutations(new Main.Card[0], handSizeTwo);
        Main.Card[][] pThree = Main.permutations(new Main.Card[0], handSizeThree);
        Main.Card[][] pFour = Main.permutations(new Main.Card[0], handSizeFour);

        Main.Card[][] expPTwo = {
                {
                        new Main.Card(10, 1),
                        new Main.Card(11, 1)
                }
        };

        Main.Card[][] expPThree = {
                {
                        new Main.Card(10, 1),
                        new Main.Card(11, 1)
                },
                {
                        new Main.Card(10, 1),
                        new Main.Card(12, 0)
                },
                {
                        new Main.Card(11, 1),
                        new Main.Card(12, 0)
                }
        };

        Main.Card[][] expPFour = {
                {
                        new Main.Card(10, 1),
                        new Main.Card(11, 1)
                },
                {
                        new Main.Card(10, 1),
                        new Main.Card(12, 0)
                },
                {
                        new Main.Card(10, 1),
                        new Main.Card(13, 0)
                },
                {
                        new Main.Card(11, 1),
                        new Main.Card(12, 0)
                },
                {
                        new Main.Card(11, 1),
                        new Main.Card(13, 0)
                },
                {
                        new Main.Card(12, 0),
                        new Main.Card(13, 0)
                }
        };

        CompareCardArrays(pTwo, expPTwo);

        CompareCardArrays(pThree, expPThree);

        CompareCardArrays(pFour, expPFour);

    }

    private void CompareCardArrays(Main.Card[][] pThree, Main.Card[][] expPThree) {
        for (int i = 0; i < expPThree.length; i++) {
            Main.Card[] cArr = expPThree[i];
            for (int j = 0; j < cArr.length; j++) {
                assertEquals(cArr[j].val, pThree[i][j].val);
                assertEquals(cArr[j].suit, pThree[i][j].suit);
            }
        }
    }
}