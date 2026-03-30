package com.narxoz.rpg;

import com.narxoz.rpg.arena.*;
import com.narxoz.rpg.chain.*;
import com.narxoz.rpg.command.*;
import com.narxoz.rpg.tournament.TournamentEngine;

public class Main {
    public static void main(String[] args) {

        ArenaFighter hero = new ArenaFighter("Hero", 100, 0.20, 25, 5, 18, 3);
        ArenaOpponent opponent = new ArenaOpponent("Champion", 90, 14);

        System.out.println("--- Command Demo ---");
        ActionQueue queue = new ActionQueue();

        queue.enqueue(new AttackCommand(opponent, hero.getAttackPower()));
        queue.enqueue(new HealCommand(hero, 20));
        queue.enqueue(new DefendCommand(hero, 0.15));

        for (String s : queue.getCommandDescriptions()) {
            System.out.println(s);
        }

        queue.undoLast();
        System.out.println("After undo:");
        for (String s : queue.getCommandDescriptions()) {
            System.out.println(s);
        }

        queue.enqueue(new DefendCommand(hero, 0.15));
        queue.executeAll();

        System.out.println("\n--- Chain Demo ---");
        DefenseHandler dodge = new DodgeHandler(0.5, 99);
        DefenseHandler block = new BlockHandler(0.3);
        DefenseHandler armor = new ArmorHandler(5);
        DefenseHandler hp = new HpHandler();
        dodge.setNext(block).setNext(armor).setNext(hp);

        System.out.println("HP before: " + hero.getHealth());
        dodge.handle(20, hero);
        System.out.println("HP after: " + hero.getHealth());

        System.out.println("\n--- Tournament ---");
        ArenaFighter h = new ArenaFighter("Erlan", 120, 0.25, 25, 8, 22, 3);
        ArenaOpponent o = new ArenaOpponent("Iron Vane", 100, 16);

        TournamentResult result = new TournamentEngine(h, o)
                .setRandomSeed(42)
                .runTournament();

        System.out.println("Winner: " + result.getWinner());
        System.out.println("Rounds: " + result.getRounds());
        for (String line : result.getLog()) {
            System.out.println(line);
        }
    }
}