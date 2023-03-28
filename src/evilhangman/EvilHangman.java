/**
 *                      Revision History.
 * ***************************************************************
 * 08/31/19 - wrote original hangman game
 * 10/20/19 - updated with a set for guessed letters
 */
package evilhangman;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * This is the classic hangman game.
 *
 * @author aapplin
 */
public class EvilHangman {

    private final int MAX_WRONG_GUESSES = 7; // constant
    private ArrayList<String> dictionary; // all the words in the file
    private String gameWord; // the word that will be scored
    private char[] displayWord; // the array that starts as asterisks
    private TreeSet<Character> guessedLetters; // letters guessed by player
    private char[] gameWordArray; // just to make scoring easier
    private int wrongGuesses; // a count of incorrect guesses
    // each element in the gallows array represents the entire drawing for 
    // that number of wrong guesses.  
    private final String[] gallows = {"---------\n|       |\n|\n|\n|\n|\n|",
        "---------\n|       |\n|       O\n|\n|\n|\n|",
        "---------\n|       |\n|       O\n|       |\n|\n|\n|",
        "---------\n|       |\n|       O\n|      \\|\n|\n|\n|",
        "---------\n|       |\n|       O\n|      \\|/\n|\n|\n|",
        "---------\n|       |\n|       O\n|      \\|/\n|       |\n|\n|",
        "---------\n|       |\n|       O\n|      \\|/\n|       |\n|      / \n|",
        "---------\n|       |\n|       O\n|      \\|/\n|       |\n|      / \\ \n|"};
    private boolean debugging = true;

    /**
     * Standard file input using a command line argument.
     *
     * @param fileName the command line argument
     */
    public void readDictionary(String fileName) {
        try {
            Scanner inFile = new Scanner(new FileReader(fileName));
            if (!inFile.hasNext()) {
                System.err.println("The file is empty. ");
                System.exit(1); // stop the program
            }
            dictionary = new ArrayList<>(); // create a new ArrayList
            String word;
            while (inFile.hasNext()) {
                word = inFile.next();  // we can always read strings
                dictionary.add(word);
            }
            if (debugging) {
                System.out.println("The number of words read in is "
                        + dictionary.size()); // for debugging
            }
            inFile.close();
        } catch (FileNotFoundException ex) {
            System.out.println("The file " + fileName + " does not exist.");
            System.exit(1);   // stop the program         
        }
    }

    /**
     * This method generates a local random number between 1 and size-1. 
     * Then it sets the game word and removes that word from the dictionary.
     *
     */
    public void setRandomGameWord() {
        Random random = new Random();
        int index = random.nextInt(dictionary.size());
        gameWord = dictionary.remove(index);
    }

    /**
     * Initializes all object properties to play one game.
     */
    public void initializeForNewGame() {
        setRandomGameWord();  // find a word
        wrongGuesses = 0;     // set wrong guesses to 0
        // set up the word to be displayed
        displayWord = new char[gameWord.length()];
        Arrays.fill(displayWord, '*'); // fill with asterisks 
        // create a new TreeSet of guessed letters
        guessedLetters = new TreeSet<>();// always ordered
        // set up game word for scoring
        gameWordArray = new char[gameWord.length()];
        gameWordArray = gameWord.toCharArray(); // game word in an array
    }

    /**
     * Linear Search for an asterisk.
     * The loop will exit when one is found.  If the value of i is the 
     * same as the length of the array, there are none left.
     *
     * @return true if there are no *'s and false otherwise.
     */
    public boolean wordGuessed() {
        int i = 0;
        while(i < displayWord.length && displayWord[i] != '*') {
            i++;
        }
        return i == displayWord.length;
    }
    /**
     * Prints the string from the gallows array corresponding to the number
     * of wrong guesses.
     */
    public void printTheGallows() {
        System.out.println(gallows[wrongGuesses]);
    }

    /**
     * creates a string with the letters in it using a StringBuilder.
     *
     * @return the String represented by the char array.
     */
    public String printDisplayWord() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < displayWord.length; i++) {
            str.append(displayWord[i]);
        }
        return str.toString();
    }

    /**
     * Looks to see if the current guess has already been guessed.
     *
     * @param letter the player's current guess.
     * @return true if the letter has been guessed or false otherwise
     */
    public boolean letterGuessed(char letter) {
        return guessedLetters.contains(letter);
    }

    /**
     * Finds the current guess in the gameWordArray and replaces the
     * corresponding '*' with the letter in displayWord. If the letter is not
     * found it increments wrong guess counter.
     *
     * @param letter the current guess
     */
    public void scoreLetter(char letter) {
        boolean letterFound = false;
        guessedLetters.add(letter);
        for (int i = 0; i < gameWordArray.length; i++) {
            if (gameWordArray[i] == letter) {
                displayWord[i] = letter;
                letterFound = true;
            }
        }
        if (!letterFound) {
            wrongGuesses++;
        }
    }

    /**
     * The actual game engine
     *
     * @param fileName a command line argument
     */
    public void run(String fileName) {
        readDictionary(fileName); // load the dictionary one time
        char answer = 'y';  // lcv for the ask the user loop
        Scanner in = new Scanner(System.in);
        char letter;  // user input guess
        // ask the user loop for continuing game play
        while (answer == 'y' || answer == 'Y') {
            initializeForNewGame();
            if (debugging) {
                System.out.println("The word is " + gameWord); 
            }            
            // loop for a single game  
            while (wrongGuesses < MAX_WRONG_GUESSES && !wordGuessed()) {
                printTheGallows();
                System.out.print("Enter a letter in the word "
                        + printDisplayWord() + "  "); // user prompt
                letter = in.next().charAt(0); // read single char
                if (!letterGuessed(letter)) {
                    scoreLetter(letter);
                } else {
                    System.out.println("You already guessed that one!");
                }
            }
            // fall out of the loop 
            // either wrong guesses == max or they guessed the word
            if (wrongGuesses == MAX_WRONG_GUESSES) {
                printTheGallows();
                System.out.println("Sorry, you lose! The word was "
                        + gameWord);
            } else {
                System.out.println("Yay!  You won!!");
            }
            // update "ask the user" loop control variable
            System.out.println("Do you want to play again? Y/N ");
            answer = in.next().charAt(0);
        } // end Game loop
        System.out.println("That was fun!  See you next time.");
    }

    /**
     * main method of the class.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //checks to see if there is an argument 
        if (args.length < 1) {
            System.err.println("Usage: prog filename");
            System.exit(1);
        }
        // create instance of the class and run
        EvilHangman driver = new EvilHangman();
        driver.run(args[0]);
    }
}
