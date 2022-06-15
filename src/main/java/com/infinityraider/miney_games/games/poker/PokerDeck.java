package com.infinityraider.miney_games.games.poker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import java.util.*;

public class PokerDeck {
    private final Set<PokerCard> all;

    private Deque<PokerCard> deck;
    private final List<PokerCard> drawn;
    private List<PokerCard> drawnAccess;

    public PokerDeck() {
        ImmutableSet.Builder<PokerCard> bob = ImmutableSet.builder();
        PokerCard.stream().forEach(bob::add);
        this.all = bob.build();
        this.drawn = Lists.newArrayList();
        this.shuffle();
    }

    public Set<PokerCard> allCards() {
        return this.all;
    }

    public PokerDeck shuffle() {
        this.drawn.clear();
        this.drawnAccess = ImmutableList.of();

        List<PokerCard> cards = new ArrayList<>(this.allCards());
        Collections.shuffle(cards);
        this.deck = new ArrayDeque<>(cards);
        return this;
    }

    public boolean canDraw() {
        return this.deck.size() > 0;
    }

    public PokerCard draw() {
        PokerCard card = this.deck.pop();
        this.drawn.add(card);
        this.drawnAccess = ImmutableList.copyOf(this.drawn);
        return card;
    }

    public List<PokerCard> getDrawn() {
        return this.drawnAccess;
    }
}
