import java.util.List;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.NoSuchFileException;

public class Whitespace {
    private final Command[] cmds;
    private final int[] labels;

    enum IMP {
        STACK (" "),
        ARITH ("\t "),
        HEAP ("\t\t"),
        FLOW ("\n"),
        IO ("\t\n");

        private final String pattern;
        IMP(String pattern) {
            this.pattern = pattern;
        }

        static IMP of(String pattern) {
            for (IMP imp: IMP.values()) {
                if (imp.pattern.equals(pattern)) {
                    return imp;
                }
            }
            return null;
        }
    }

    enum Instr {
        PUSH (IMP.STACK, " ", Param.NUMBER),
        DUP (IMP.STACK, "\n ", 1),
        COPY (IMP.STACK, "\t ", Param.NUMBER, -1),
        SWAP (IMP.STACK, "\n\t", 2),
        DROP (IMP.STACK, "\n\n", 1),
        SLIDE (IMP.STACK, "\t\n", Param.NUMBER, -1),
        ADD (IMP.ARITH, "  ", 2),
        SUB (IMP.ARITH, " \t", 2),
        MUL (IMP.ARITH, " \n", 2),
        DIV (IMP.ARITH, "\t ", 2),
        MOD (IMP.ARITH, "\t\t", 2),
        STORE (IMP.HEAP, " ", 2),
        RETRIEVE (IMP.HEAP, "\t", 1),
        LABEL (IMP.FLOW, "  ", Param.LABEL),
        CALL (IMP.FLOW, " \t", Param.LABEL),
        JMP (IMP.FLOW, " \n", Param.LABEL),
        JZ (IMP.FLOW, "\t ", Param.LABEL, 1),
        JN (IMP.FLOW, "\t\t", Param.LABEL, 1),
        RET (IMP.FLOW, "\t\n"),
        END (IMP.FLOW, "\n\n"),
        PRINTC (IMP.IO, "  ", 1),
        PRINTI (IMP.IO, " \t", 1),
        READC (IMP.IO, "\t ", 1),
        READI (IMP.IO, "\t\t", 1);

        private final IMP imp;
        private final String pattern;
        private final Param param;
        private final int args;

        Instr(IMP imp, String pattern, Param param, int args) {
            this.imp = imp;
            this.pattern = pattern;
            this.param = param;
            this.args = args;
        }

        Instr(IMP imp, String pattern, Param param) {
            this(imp, pattern, param, 0);
        }

        Instr(IMP imp, String pattern, int args) {
            this(imp, pattern, null, args);
        }

        Instr(IMP imp, String pattern) {
            this(imp, pattern, null);
        }

        static Instr of(IMP imp, String pattern) {
            for (Instr instr: Instr.of(imp)) {
                if (instr.pattern.equals(pattern)) {
                    return instr;
                }
            }
            return null;
        }
        static Instr[] of(IMP imp) {
            if (imp == null) return new Instr[0];
            return Stream.of(values()).filter((instr) -> instr.imp == imp).toArray(Instr[]::new);
        }
    }

    enum Param {
        NUMBER, LABEL;

        static int parseNumber(String in) {
            if (in.length() <= 1) return 0;
            int sign = in.charAt(0) == ' ' ? 1 : -1;
            String bin = Stream.of(in.substring(Math.max(1, in.length() - 31)).split("")).map((c) -> c.equals(" ") ? "0" : "1").collect(Collectors.joining());
            int mag = Integer.parseInt(bin, 2);
            return mag * sign;
        }
    }

    private static class Command {
        private final Instr instr;
        private int param;

        private Command(Instr instr) {
            this.instr = instr;
        }

        @Override
        public String toString() {
            return instr.param == null ? instr.name() : String.format("%s:%d", instr.name(), param);
        }
    }

    public Whitespace(String code) throws WhitespaceSyntaxErrorException {
        List<Command> cmds = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Integer> labelMap = new ArrayList<>();
        String impBuffer = "";
        String instrBuffer = "";
        String paramBuffer = "";
        IMP imp = null;
        Instr instr = null;
        for (int i = 0; i < code.length(); i++) {
            boolean fullInstr = false;
            char c = code.charAt(i);
            if (!" \n\t".contains(String.valueOf(c))) continue;
            if (imp == null) {
                impBuffer += c;
                imp = IMP.of(impBuffer);
            } else if (instr == null) {
                String instrNext = instrBuffer + c;
                if (Stream.of(Instr.of(imp)).noneMatch((instrCheck) -> instrCheck.pattern.startsWith(instrNext)))
                    throw new WhitespaceSyntaxErrorException(
                        String.format("Unexpected %s at %d", switch (c) {
                            case ' ' -> "Space";
                            case '\t' -> "Tab";
                            case '\n' -> "LF";
                            default -> "character";
                        }, i)
                    );
                instrBuffer = instrNext;
                instr = Instr.of(imp, instrBuffer);
                if (instr != null && instr.param == null) {
                    fullInstr = true;
                }
            } else if (c != '\n') {
                paramBuffer += c;
            } else {
                fullInstr = true;
            }
            if (fullInstr) {
                Command cmd = new Command(instr);
                if (instr.param == Param.NUMBER) {
                    cmd.param = Param.parseNumber(paramBuffer);
                    if (instr.args < 0 && cmd.param < 0) {
                        throw new WhitespaceSyntaxErrorException(String.format("%s requires a non-negative argument, found '%d'", instr.name(), cmd.param));
                    }
                } else if (instr.param == Param.LABEL) {
                    int label = labels.indexOf(paramBuffer);
                    if (label != -1) {
                        cmd.param = label;
                    } else {
                        cmd.param = labels.size();
                        labels.add(paramBuffer);
                        labelMap.add(-1);
                    }
                    if (instr == Instr.LABEL) {
                        if (labelMap.get(cmd.param) != -1)
                            throw new WhitespaceSyntaxErrorException(String.format("Duplicate label '%s'", showWS(paramBuffer)));
                        labelMap.set(cmd.param, cmds.size());
                    }
                }
                cmds.add(cmd);
                impBuffer = "";
                instrBuffer = "";
                paramBuffer = "";
                imp = null;
                instr = null;
            }
        }
        if (!impBuffer.isEmpty() || !instrBuffer.isEmpty() || !paramBuffer.isEmpty())
            throw new WhitespaceSyntaxErrorException("Unexpected EOF");
        for (int i = 0; i < labels.size(); i++) {
            if (labelMap.get(i) == -1)
                throw new WhitespaceSyntaxErrorException(String.format("Undefined label '%s'", showWS(labels.get(i))));
        }
        this.cmds = cmds.toArray(Command[]::new);
        this.labels = labelMap.stream().mapToInt(Integer::intValue).toArray();
    }

    private void exec(Scanner inputSource) {
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        Map<Integer, Integer> heap = new HashMap<>();
        ArrayDeque<Integer> callstack = new ArrayDeque<>();
        for (int pt = 0; pt < cmds.length; pt++) {
            Command cmd = cmds[pt];
            int args = cmd.instr.args >= 0 ? cmd.instr.args : cmd.param - cmd.instr.args;
            if (stack.size() < args)
                throw new WhitespaceStackException(String.format("Operation %s requires %d element(s); Found %d", cmd, args, stack.size()));
            switch (cmd.instr) {
                case PUSH -> {
                    stack.push(cmd.param);
                }
                case DUP -> {
                    stack.push(stack.peek());
                }
                case COPY -> {
                    ArrayDeque<Integer> top = new ArrayDeque<>();
                    for (int i = 0; i < cmd.param; i++) {
                        top.push(stack.pop());
                    }
                    int copy = stack.peek();
                    for (int i = 0; i < cmd.param; i++) {
                        stack.push(top.pop());
                    }
                    stack.push(copy);
                }
                case SWAP -> {
                    int top = stack.pop();
                    int next = stack.pop();
                    stack.push(top);
                    stack.push(next);
                }
                case DROP -> {
                    stack.pop();
                }
                case SLIDE -> {
                    int top = stack.pop();
                    for (int i = 0; i < cmd.param; i++) {
                        stack.pop();
                    }
                    stack.push(top);
                }
                case ADD -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(left + right);
                }
                case SUB -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(left - right);
                }
                case MUL -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(left * right);
                }
                case DIV -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(left / right);
                }
                case MOD -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(left % right);
                }
                case STORE -> {
                    int value = stack.pop();
                    int key = stack.pop();
                    heap.put(key, value);
                }
                case RETRIEVE -> {
                    int key = stack.pop();
                    stack.push(heap.getOrDefault(key, 0));
                }
                case LABEL -> {}
                case CALL -> {
                    callstack.push(pt);
                    pt = labels[cmd.param];
                }
                case JMP -> {
                    pt = labels[cmd.param];
                }
                case JZ -> {
                    if (stack.pop() == 0) {
                        pt = labels[cmd.param];
                    }
                }
                case JN -> {
                    if (stack.pop() < 0) {
                        pt = labels[cmd.param];
                    }
                }
                case RET -> {
                    if (callstack.size() == 0)
                        throw new WhitespaceRuntimeException("Invalid RET; Callstack empty");
                    pt = callstack.pop();
                }
                case END -> {
                    return;
                }
                case PRINTC -> {
                    int ch = stack.pop();
                    if (ch < Character.MIN_VALUE || ch > Character.MAX_VALUE)
                        throw new WhitespaceRuntimeException(String.format("Invalid character value %s%X", ch < 0 ? "-" : "", Math.abs(ch)));
                    System.out.print((char) ch);
                }
                case PRINTI -> {
                    System.out.print(stack.pop());
                }
                case READC -> {
                    inputSource.useDelimiter("");
                    if (!inputSource.hasNext())
                        throw new WhitespaceRuntimeException("Unfulfilled READC input");
                    char input = inputSource.next().charAt(0);
                    int key = stack.pop();
                    heap.put(key, Integer.valueOf(input));
                }
                case READI -> {
                    inputSource.useDelimiter("\\p{javaWhitespace}+");
                    if (!inputSource.hasNext())
                        throw new WhitespaceRuntimeException("Unfulfilled READI input");
                    if (!inputSource.hasNextInt())
                        throw new WhitespaceRuntimeException("Illegal READI input");
                    int input = inputSource.nextInt();
                    int key = stack.pop();
                    heap.put(key, input);
                    inputSource.skip("\\p{javaWhitespace}?");
                }
            }
        }
        throw new WhitespaceRuntimeException("Program terminated without END");
    }

    public void exec(InputStream inputSource) {
        exec(new Scanner(inputSource));
    }

    public void exec(String input) {
        exec(new Scanner(input));
    }

    public static String showWS(String ws) {
        return ws.chars().mapToObj((ch) -> switch (ch) {
            case ' ' -> "S";
            case '\t' -> "T";
            case '\n' -> "L";
            default -> "";
        }).collect(Collectors.joining());
    }

    public static void main(String[] args) {
        if (args.length == 0 || args.length > 2) {
            System.out.println("Usage: java Whitespace.java \"<file.ws>\" \"[input]\"");
            System.exit(-1);
        }
        try {
            String code = Files.readString(Path.of(args[0]));
            Whitespace ws = new Whitespace(code);
            if (args.length < 2) {
                ws.exec(System.in);
            } else {
                ws.exec(args[1]);
            }
            return;
        } catch (WhitespaceSyntaxErrorException | WhitespaceRuntimeException err) {
            System.err.println(err);
        } catch (NoSuchFileException err) {
            System.err.printf("File '%s' not found.\n", args[0]);
        } catch (OutOfMemoryError err) {
            System.err.printf("File '%s' is too large.\n", args[0]);
        } catch (IOException err) {
            System.err.printf("Could not read file '%s'.\n", args[0]);
        }
        System.exit(-1);
    }

    public static class WhitespaceSyntaxErrorException extends Exception {
        public WhitespaceSyntaxErrorException(String message) {
            super(message);
        }
    }
    public static class WhitespaceRuntimeException extends RuntimeException {
        public WhitespaceRuntimeException(String message) {
            super(message);
        }
    }
    public static class WhitespaceStackException extends WhitespaceRuntimeException {
        public WhitespaceStackException(String message) {
            super(message);
        }
    }
}
