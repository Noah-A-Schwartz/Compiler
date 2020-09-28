//Matt McNulty
//Noah Schwartz
//Jawad Aborshed

import java.io.*;

import java.nio.charset.StandardCharsets;


//This Scanner implements a small subset of the PASCAL language. It cam recognize arithmetic operations and identifiers/declarations and keywords
public class Scanner {

    //static variables of Scanner class
    public static String charClass;
    public static char[] lexeme = new char[100];
    public static char nextChar;
    public static int lexLen = 0;
    public static String nextToken = "";
    public static BufferedReader buffer;
    public static String KEYWORD = "KEYWORD";

    //Global variables of Scanner class(never change)
    public final static String INT = "INTEGER";
    public final static String ADD_OP = "PLUS";
    public final static String MINUS_OP = "MINUS";
    public final static String END_OF_FILE = "EOF";
    public final static String SEMI = "SEMI";
    public final static String L_PAREN = "LPAREN";
    public final static String R_PAREN = "RPAREN";
    public final static String ASSIGN = "ASSIGN";
    public final static String IDENT = "IDENT";
    public final static String DIV = "DIV";
    public final static String MULT = "MULT";
    public final static String PERIOD = "PERIOD";
    public final static String DIGIT = "DIGIT";
    public final static String LETTER = "LETTER";
    public final static String UNKNOWN = "UNKNOWN";
    public static final String[] KEYWORDS = {
            "and", "array", "begin", "case", "constant",
            "div", "do", "else", "end",
            "file", "for", "function", "goto", "if",
            "in", "mod", "not", "of", "or", "procedure",
            "record", "then", "type", "until", "while", "with", "is"};

    //Determines the token type of current lex
    public static String lookUp(char ch) throws IOException {
        switch (ch) {
            case '(':
                addChar();
                nextToken = L_PAREN;
                break;

            case '+':
                addChar();
                nextToken = ADD_OP;
                break;

            case '-':
                addChar();
                nextToken = MINUS_OP;
                break;

            case '/':
                addChar();
                nextToken = DIV;
                break;

            case ')':
                addChar();
                nextToken = R_PAREN;
                break;

            case ';':
                addChar();
                nextToken = SEMI;
                break;

            case '*':
                addChar();
                nextToken = MULT;
                break;

            //This calls add char a second time to get the = after it. (although this would need a work around if i want to add in other tokens like variable types declaration Since the colon in that case is not followed by an =.
            case ':':
                addChar();
                getChar();
                addChar();
                nextToken = ASSIGN;
                break;

            case '.':
                addChar();
                nextToken = PERIOD;
                break;


            default:
                addChar();
                nextToken = END_OF_FILE;
                break;
        }
        return nextToken;
    }

    //Adds the next char to the lexeme
    public static void addChar() {
        if (lexLen <= 98) {
            //Once char is added to the lexeme, increase length by 1. then set that index to empty.
            lexeme[lexLen++] = nextChar;
            lexeme[lexLen] = 0;
        } else System.out.println("Lexeme is too long!");

    }

    //gets the next char from the Scanner/Buffer
    public static void getChar() throws IOException {

        //read next char(returns unicode of char, not actual char)
        int c = buffer.read();

        //If next char is end of file(-1), assign class of char as such
        if (c != -1) {
            nextChar = (char) c;
            if (Character.isLetter(nextChar))
                charClass = LETTER;
            else if (Character.isDigit(nextChar))
                charClass = DIGIT;
                //assignment class since := is the assignment token in Ada
            else if (nextChar == '=' || nextChar == ':')
                charClass = ASSIGN;
                //Unknown Class
            else charClass = UNKNOWN;
        } else charClass = END_OF_FILE;
    }

    //Lexical Analyzer. Works for arithmetic Expressions and assignments and Keywords
    public static String Lex() throws IOException {
        //Start of new Lexeme so reset array index to 0
        lexLen = 0;
        //Move Read to nonwhitespace
        getNonBlank();

        //Add to lexeme based on what the class of the current class of the char is
        //Add char adds to the lexeme, then does get char and checks its class
        switch (charClass) {
            case LETTER:
                addChar();
                getChar();
                while (charClass.equals(LETTER) || charClass.equals(DIGIT)) {
                    addChar();
                    getChar();
                }
                nextToken = IDENT;
                break;

            case DIGIT:
                addChar();
                getChar();
                while (charClass.equals(DIGIT)) {
                    addChar();
                    getChar();
                }
                nextToken = INT;
                break;

            case ASSIGN:
                addChar();
                getChar();
                while (charClass.equals("ASSIGN")) {
                    addChar();
                    getChar();
                }
                nextToken = ASSIGN;
                break;
            //Unknown calls lookUp to determine what token a char is if its not part of a variable
            case UNKNOWN:
                lookUp(nextChar);
                getChar();
                break;

            case END_OF_FILE:
                nextToken = END_OF_FILE;
                lexeme[0] = 'E';
                lexeme[1] = 'O';
                lexeme[2] = 'F';
                lexLen = 3;
                break;
        }

        if (isKeyword(lexeme) == true)
            nextToken = KEYWORD;
        System.out.print("Next token is: " + nextToken + "\t\t Next lexeme is: ");
        for (int i = 0; i <= lexLen; i++) {
            if (lexeme[i] == ' ')
                break;
            else System.out.print(lexeme[i]);
        }
        System.out.println();
        return nextToken;
    }

    public static void getNonBlank() throws IOException {
        while (Character.isWhitespace(nextChar))
            getChar();
    }

    //Simply checks if a lexeme matches any of the keywords once it is complete.
    public static boolean isKeyword(char [] lexeme) {
        String temp = "";
        for (char c : lexeme) {
            if (!Character.isLetter(c))
                break;
            temp += (Character.toString(c));

        }
        for (String keyword : KEYWORDS)
            if (temp.equals(keyword))
                return true;
        return false;
    }
}


class Main {
    public static void main(String[] args) throws IOException {
        //Create new BufferReader from file
        try {

            File file = new File("src\\Ada");
            Scanner.buffer = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file),
                            StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            System.out.println("File Not found");
        }
        //Get first char
        Scanner.getChar();
        //Do lexical Analysis until End of File is reached.
        do {
            Scanner.Lex();
        } while (!Scanner.nextToken.equals(Scanner.END_OF_FILE));


    }
}


