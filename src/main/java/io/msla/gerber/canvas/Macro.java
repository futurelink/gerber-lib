package io.msla.gerber.canvas;

import lombok.Getter;
import java.util.ArrayList;

@Getter
public class Macro {

    public final static class OperationResult {
        @Getter private final Operation.Type type;
        @Getter private final Boolean exposure;
        private final ArrayList<Double> values;

        OperationResult(Operation.Type type, boolean exposure) {
            this.type = type;
            this.exposure = exposure;
            this.values = new ArrayList<>();
        }

        public double getValue(int index) {
            return values.get(index);
        }

        @Override
        public String toString() {
            return String.format("Result: %s, %s", type, values);
        }
    }

    public final static class Operation {
        public enum Type { Circle, VectorLine, CenterLine, Outline, Polygon }
        private final Type type;
        private final Boolean exposure;
        private final ArrayList<String> params;

        private Operation(Type type, boolean exposure) {
            this.type = type;
            this.exposure = exposure;
            this.params = new ArrayList<>();
        }

        private void addParameter(String p) {
            params.add(p);
        }

        public OperationResult eval(final Double[] operationParams) {
            var res = new OperationResult(type, exposure);
            for (var p : params) {
                res.values.add(evaluate(substVars(p, operationParams)));
            }
            return res;
        }

        private String substVars(final String p, Double[] params) {
            var r = p;
            for (var i = 0; i < params.length; i++) r = r.replaceAll("\\$" + (i+1), String.format("%f", params[i]));
            return r;
        }


        public static Operation fromString(String paramsString) {
            if (!paramsString.startsWith("0")) {
                var params = paramsString.split(",");
                var i = 0;
                var type = switch (params[i].trim()) {
                    case "1" -> Type.Circle;
                    case "20" -> Type.VectorLine;
                    case "21" -> Type.CenterLine;
                    case "4" -> Type.Outline;
                    case "5" -> Type.Polygon;
                    default -> null;
                };
                var o = new Operation(type, params[++i].trim().equals("1"));
                while (++i < params.length) o.addParameter(params[i].trim());
                return o;
            }
            return null;
        }

        @Override
        public String toString() {
            return String.format("Operation: %s, %s", type, params);
        }
    }

    ArrayList<Operation> operations;

    public Macro(ArrayList<String> params) {
        operations = new ArrayList<>();
        params.forEach(p -> {
            var o = Operation.fromString(p);
            if (o != null) operations.add(o);
        });
    }

    public ArrayList<OperationResult> eval(Double[] params) {
        var res = new ArrayList<OperationResult>();
        for (var o : operations) {
            res.add(o.eval(params));
        }
        return res;
    }

    public static double evaluate(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('x')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } /*else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    x = switch (func) {
                        case "sqrt" -> Math.sqrt(x);
                        case "sin" -> Math.sin(Math.toRadians(x));
                        case "cos" -> Math.cos(Math.toRadians(x));
                        case "tan" -> Math.tan(Math.toRadians(x));
                        default -> throw new RuntimeException("Unknown function: " + func);
                    };
                }*/ else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

}
