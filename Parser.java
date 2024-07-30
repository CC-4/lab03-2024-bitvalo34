/*
    Laboratorio No. 3 - Recursive Descent Parsing
    CC4 - Compiladores

    Clase que representa el parser

    Actualizado: agosto de 2021, Luis Cu
*/

import java.util.LinkedList;
import java.util.Stack;

public class Parser {

    // Puntero next que apunta al siguiente token
    private int next;
    // Stacks para evaluar en el momento
    private Stack<Double> operandos;
    private Stack<Token> operadores;
    // LinkedList de tokens
    private LinkedList<Token> tokens;

    // Funcion que manda a llamar main para parsear la expresion
    public boolean parse(LinkedList<Token> tokens) {
        this.tokens = tokens;
        this.next = 0;
        this.operandos = new Stack<Double>();
        this.operadores = new Stack<Token>();

        // Recursive Descent Parser
        // Imprime si el input fue aceptado
        System.out.println("Aceptada? " + S());

        // Shunting Yard Algorithm
        // Imprime el resultado de operar el input
        System.out.println("Resultado: " + this.operandos.peek());

        // Verifica si terminamos de consumir el input
        return this.next == this.tokens.size();
    }

    // Verifica que el id sea igual que el id del token al que apunta next
    // Si si avanza el puntero es decir lo consume.
    private boolean term(int id) {
        if(this.next < this.tokens.size() && this.tokens.get(this.next).equals(id)) {
            
	    // Code for the Shunting Yard Algorithm
            if (id == Token.NUMBER) {
                // We found a number
                // We must store it in the operand stack
                operandos.push(this.tokens.get(this.next).getVal());
            } else if (id == Token.SEMI) {
                // We found a semicolon
                // We must operate on everything that was left pending
                while (!this.operadores.empty()) {
                    popOp();
                }
            } else {
                // We find some other token, i.e. an operator
                // We store it in the operator stack
                // Let pushOp do the work, I don't want to do it here
                pushOp(this.tokens.get(this.next));
            }

            this.next++;
            return true;
        }
        return false;
    }

    // Function that checks the precedence of an operator
    private int pre(Token op) {
        switch (op.getId()) {
            case Token.PLUS:
            case Token.MINUS:
                return 1;
            case Token.MULT:
            case Token.DIV:
            case Token.MOD:
                return 2;
            case Token.EXP:
                return 3;
            case Token.UNARY:
                return 4;
            case Token.LPAREN:
            case Token.RPAREN:
                return 5;
            default:
                return -1;
        }
    }

    private void popOp() {
        Token op = this.operadores.pop();

        if (op.equals(Token.PLUS)) {
            double b = this.operandos.pop();
            double a = this.operandos.pop();
            this.operandos.push(a + b);
        } else if (op.equals(Token.MINUS)) {
            double b = this.operandos.pop();
            double a = this.operandos.pop();
            this.operandos.push(a - b);
        } else if (op.equals(Token.MULT)) {
            double b = this.operandos.pop();
            double a = this.operandos.pop();
            this.operandos.push(a * b);
        } else if (op.equals(Token.DIV)) {
            double b = this.operandos.pop();
            double a = this.operandos.pop();
            this.operandos.push(a / b);
        } else if (op.equals(Token.MOD)) {
            double b = this.operandos.pop();
            double a = this.operandos.pop();
            this.operandos.push(a % b);
        } else if (op.equals(Token.EXP)) {
            double b = this.operandos.pop();
            double a = this.operandos.pop();
            this.operandos.push(Math.pow(a, b));
        } else if (op.equals(Token.UNARY)) {
            double a = this.operandos.pop();
            this.operandos.push(-a);
        }
    }

    private void pushOp(Token op) {
        while (!this.operadores.isEmpty() && pre(op) <= pre(this.operadores.peek())) {
            popOp();
        }
        this.operadores.push(op);
    }

    private boolean S() {
        return E() && term(Token.SEMI);
    }

    private boolean E() {
        if (T()) {
            while (this.next < this.tokens.size() && (this.tokens.get(this.next).equals(Token.PLUS) || this.tokens.get(this.next).equals(Token.MINUS))) {
                Token op = this.tokens.get(this.next);
                this.next++;
                if (!T()) return false;
                pushOp(op);
            }
            return true;
        }
        return false;
    }

    private boolean T() {
        if (F()) {
            while (this.next < this.tokens.size() && (this.tokens.get(this.next).equals(Token.MULT) || this.tokens.get(this.next).equals(Token.DIV) || this.tokens.get(this.next).equals(Token.MOD))) {
                Token op = this.tokens.get(this.next);
                this.next++;
                if (!F()) return false;
                pushOp(op);
            }
            return true;
        }
        return false;
    }

    private boolean F() {
        if (this.next < this.tokens.size() && this.tokens.get(this.next).equals(Token.LPAREN)) {
            this.next++;
            if (!E()) return false;
            if (this.next >= this.tokens.size() || !this.tokens.get(this.next).equals(Token.RPAREN)) return false;
            this.next++;
            return true;
        } else if (this.next < this.tokens.size() && this.tokens.get(this.next).equals(Token.NUMBER)) {
            return term(Token.NUMBER);
        } else if (this.next < this.tokens.size() && this.tokens.get(this.next).equals(Token.MINUS)) {
            this.next++;
            if (!F()) return false;
            pushOp(new Token(Token.UNARY));
            return true;
        }
        return false;
    }
}
