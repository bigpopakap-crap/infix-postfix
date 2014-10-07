
package infixtopostfix;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Converter {
    private static final String mismatchedParenthesesError = "There are mismatched parentheses.";
    private static final String insufficientOperandsError = "There are too many operators.";
    private static final String prompt = "------------------------------------------------------------------\n" +
                                         "Enter a valid math expression using only *, /, +, - and ^ (power).\n" +
                                         "Use only one-character vars, or an error will occur.\n" +
                                         "Variables must be defined in the following format, or an error will occur:\n" +
                                         "\t after expression: \",x=54, y = 6 ,z= 9\n" +
                                         "Remember that all operations are integer operations, so 1/2 evaluates 0.\n" +
                                         "Hit ENTER to exit.\n" +
                                         "\nInput: ";

    private static enum Operator {

        /********************USER MUST ALTER TO INCLUDE NEW OPERATORS***************************************/
        POWER       ('^', 2, true),
        MULTIPLY    ('*', 1, true),
        DIVIDE      ('/', 1, true),
        ADD         ('+', 0, true),
        SUBTRACT    ('-', 0, true);

        public static int eval(int second, char o, int first) {
            switch (toOperator(o)) {
                case    POWER:
                    return (int) Math.pow(first, second);
                case    MULTIPLY:
                    return first * second;
                case    DIVIDE:
                    return first / second;
                case    ADD:
                    return first + second;
                case    SUBTRACT:
                    return first - second;
                default:
                    return 0;
            }
        }
        /*****************************************************************************************************/

        private final char c;
        private final int rank;
        private final boolean isLeftAssociative;
        Operator(char c, int rank, boolean isLeftAssociative) {
            this.c = c;
            this.rank = rank;
            this.isLeftAssociative = isLeftAssociative;
        }
        
        private static Operator toOperator(char c) {
            for (Operator o : values()) {
                if (o.c == c) {
                    return o;
                }
            }
            return null;
        }

        private static boolean isOperator(char c) {
            for (Operator o : values()) {
                if (o.c == c) {
                    return true;
                }
            }
            return false;
        }

        private static int comparePrecedence(char c, char d) {
            Operator m = toOperator(c);
            Operator n = toOperator(d);
            return m == null || n == null ? 0 : m.rank - n.rank;
        }

        private static boolean equalPrecedence(char c, char d) {
            Operator m = toOperator(c);
            Operator n = toOperator(d);
            return m != null && n != null && m.rank == n.rank;
        }

        private static boolean isLeftAssociative(char c) {
            Operator o;
            return (o = toOperator(c)) != null ? o.isLeftAssociative : false;
        }
        
    }
    
    public static String toInfix(String str) {
        //removes all characters that are neither digits, parentheses, or valid operators
        StringBuilder out = new StringBuilder(str.length());
        boolean hasVars = false;
        for (int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if (!hasVars &&
                Character.isDigit(c) ||
                c == '(' ||
                c == ')' ||
                Operator.isOperator(c) ||
                Character.isLetter(c)) {
                out.append(c);
            } else if (!hasVars && c == ',') {
                out.append(c);
                hasVars = true;
            } else if (hasVars &&
                       Character.isLetter(c) ||
                       c == '=' ||
                       c == ',' ||
                       Character.isDigit(c)) {
                out.append(c);

            }
        }
        return out.toString();
    }

    public static String toPostfix(String infix) {
        //note: function tokens and function separators ignored
        RigidStack<String> opstack = new RigidStack<String>(infix.length());
        RigidQueue<String> outq = new RigidQueue<String>(infix.length());

        String varList = "";
        int commaIndex = infix.indexOf(',');
        if (commaIndex > -1) {
            varList = infix.substring(commaIndex);
            infix = infix.substring(0, commaIndex);
        }

        for (int i=0; i<infix.length(); i++) { //for each character in the valid infix string
            String c = "" + infix.charAt(i); //read it
            if (Character.isDigit(c.charAt(0)) || Character.isLetter(c.charAt(0))) { //if char is a digit, or variable name
                char next;
                while (i + 1 < infix.length() && Character.isDigit(next = infix.charAt(i + 1))) {
                    c += next;
                    i++;
                }
                outq.enqueue(c); //add it to the output queue
            } else if (Operator.isOperator(c.charAt(0))) { //if char is an operator
                while (opstack.peek() != null && Operator.isOperator(opstack.peek().charAt(0)) && //while there is an operator at the top of the operator stack
                                                   //(while not a parenthesis) and either:
                       ((Operator.isLeftAssociative(c.charAt(0)) && Operator.comparePrecedence(c.charAt(0), opstack.peek().charAt(0)) <= 0) || //the char is left-associative
                                                                                                      //and its precedence is equal to or lower than
                                                                                                      //that of the operator at the top of the stack
                        (!Operator.isLeftAssociative(c.charAt(0)) && Operator.comparePrecedence(c.charAt(0), opstack.peek().charAt(0)) < 0))) {//or the char is right-associative
                                                                                                      //and its precedence is strictly lower than
                                                                                                      //that of the operator at the top of the stck
                    outq.enqueue(opstack.pop()); //pop the operator from the top of the stack onto the output queue
                }
                opstack.push(c); //push the char onto the stack
            } else if (c.charAt(0) == '(') { //if char is a left parthensis
                opstack.push(c); //push it onto the stack
            } else if (c.charAt(0) == ')') { //if char is a right parenthesis
                while (opstack.peek() != null && opstack.peek().charAt(0) != '(') { //while the stack is not empty and the
                                                                          //top token on the stack is not a left parenthesis
                    outq.enqueue(opstack.pop()); //pop the top operator off the stack onto the queue
                }
                if (opstack.peek() == null) { //if the loop exited because the stack became empty
                    return mismatchedParenthesesError; //there were mismatched parentheses
                } else {
                    opstack.pop(); //remove the left parenthesis from the top of the stack
                }
            }
        } //end reading loop. all characters read
        while (opstack.peek() != null) { //while there are still operators int he stack
            if (opstack.peek().charAt(0) == '(' || opstack.peek().charAt(0) == ')') { //if there is a parenthesis
                return mismatchedParenthesesError; //there were mismatched parentheses
            }
            outq.enqueue(opstack.pop()); //pop the operator onto the queue
        }
        return outq.toString() + varList; //return the string
    }

    public static String evalPostfix(String postfix) {
        RigidStack<Integer> stack = new RigidStack<Integer>(postfix.length());
        RigidVarMap<Character, Integer> map = new RigidVarMap<Character, Integer>(postfix.length());

        int commaIndex = postfix.indexOf(',');
        if (commaIndex > -1) {
            String varList = postfix.substring(commaIndex);
            postfix = postfix.substring(0, commaIndex);
            //process vars
            char var = '\0';
            int val = 0;
            for (int i=varList.indexOf(',') + 1; i<varList.length(); i++) {
                char c = varList.charAt(i);
                if (Character.isLetter(c)) {
                    var = c;
                } else if (Character.isDigit(c)) {
                    val = 10*val + Integer.parseInt("" + c);
                } else if (c == ',') {
                    map.add(var, val);
                    var = '\0';
                    val = 0;
                }
            }
            map.add(var, val);
        }
        
        char c = '\0';
        char lastChar;
        for (int i=0; i<postfix.length(); i++) {
            lastChar = c;
            c = postfix.charAt(i);
            if (Character.isDigit(c)) {
                if (Character.isDigit(lastChar)) {
                    stack.push(10*stack.pop() + Integer.parseInt("" + c));
                } else {
                    stack.push(Integer.parseInt("" + c));
                }
            } else if (Character.isLetter(c)) {
                stack.push(map.valueOf(c));
            } else if (Operator.isOperator(c)) {
                try {
                    stack.push(Operator.eval(stack.pop(), c, stack.pop()));
                } catch (NullPointerException e) {
                    return insufficientOperandsError;
                }
            }
        }
        return stack.peek()!= null ? stack.pop().toString() : "";
    }

    public static void run(InputStream in, PrintStream out) {
        Scanner s = new Scanner(in);
        String input;
        String infix;
        String postfix;
        String result;
        while (true) {
            System.out.print(prompt);
            input = s.nextLine();
            if (input.isEmpty()) {
                break;
            }
            out.println("input:              " + input);
            out.println("cleaned input:      " + (infix = Converter.toInfix(input)));
            out.println("postfix version:    " + (postfix = Converter.toPostfix(infix)));
            out.println("result:             " + (result = Converter.evalPostfix(postfix)));
            System.out.println();
        }
    }

}
