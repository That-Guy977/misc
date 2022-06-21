import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class Whitespace {
    private final Command[] cmds;
    private final int[] labels;
    private final Stack<Integer> stack = new Stack<>();
    private final Map<Integer, Integer> heap = new HashMap<>();

    static enum IMP {
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
            for (IMP imp: values()) {
                if (imp.pattern.equals(pattern))
                    return imp;
            }
            return null;
        }
    }

    static enum Instr {
        PUSH (" ", IMP.STACK, Param.NUMBER),
        DUP ("\n ", IMP.STACK, null),
        COPY ("\t ", IMP.STACK, Param.NUMBER),
        SWAP ("\n\t", IMP.STACK, null),
        DROP ("\n\n", IMP.STACK, null),
        SLIDE ("\t\n", IMP.STACK, Param.NUMBER),
        ADD ("  ", IMP.ARITH, null),
        SUB (" \t", IMP.ARITH, null),
        MUL (" \n", IMP.ARITH, null),
        DIV ("\t ", IMP.ARITH, null),
        MOD ("\t\t", IMP.ARITH, null),
        STORE (" ", IMP.HEAP, null),
        RETRIEVE ("\t", IMP.HEAP, null),
        LABEL ("  ", IMP.FLOW, Param.LABEL),
        CALL (" \t", IMP.FLOW, Param.LABEL),
        JMP (" \n", IMP.FLOW, Param.LABEL),
        JZ ("\t ", IMP.FLOW, Param.LABEL),
        JN ("\t\t", IMP.FLOW, Param.LABEL),
        RET ("\t\n", IMP.FLOW, null),
        END ("\n\n", IMP.FLOW, null),
        PRINTC ("  ", IMP.IO, null),
        PRINTI (" \t", IMP.IO, null),
        READC ("\t ", IMP.IO, null),
        READI ("\t\t", IMP.IO, null);


        private final String pattern;
        private final IMP imp;
        private final Param param;
        Instr(String pattern, IMP imp, Param param) {
            this.pattern = pattern;
            this.imp = imp;
            this.param = param;
        }

        static Instr of(IMP imp, String pattern) {
            for (Instr instr: of(imp)) {
                if (instr.pattern.equals(pattern))
                    return instr;
            }
            return null;
        }
        static Instr[] of(IMP imp) {
            if (imp == null) return new Instr[0];
            return Stream.of(values()).filter((instr) -> instr.imp == imp).toArray(Instr[]::new);
        }
    }

    static enum Param {
        NUMBER, LABEL;

        static int parseNumber(String in) {
            if (in.length() <= 1) return 0;
            int sign = in.charAt(0) == ' ' ? 1 : -1;
            String trimmed = in.replaceFirst("^. *", "");
            String bin = Stream.of(trimmed.split("")).map((c) -> c == " " ? "0" : "1").collect(Collectors.joining());
            int mag = bin.length() <= 31 ? Integer.parseInt(bin, 2) : Integer.MAX_VALUE;
            return mag * sign;
        }
    }

    private static class Command {
        private final Instr instr;
        private String param;
        private int paramValue;

        private Command(Instr instr) {
            this.instr = instr;
        }

        private void setParam(String param) {
            this.param = param;
        }

        private void setParamValue(int value) {
            paramValue = value;
        }
    }

    public Whitespace(String code) throws WhitespaceSyntaxErrorException {
        List<Command> cmds = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Map<String, Integer> labelKey = new HashMap<>();
        String impBuffer = "";
        String instrBuffer = "";
        String paramBuffer = "";
        for (int i = 0; i <= code.length(); i++) {
            char c = i != code.length() ? code.charAt(i) : '\0';
            IMP impCollected = IMP.of(impBuffer);
            Instr instrCollected = Instr.of(impCollected, instrBuffer);
            if (!" \n\t\0".contains(String.valueOf(c))) continue;
            if (impCollected == null) impBuffer += c;
            else if (instrCollected == null) {
                String instrPartial = instrBuffer;
                if (!Stream.of(Instr.of(impCollected)).anyMatch((instr) -> instr.pattern.startsWith(instrPartial + c))) {
                    throw new WhitespaceSyntaxErrorException(
                        String.format("Unexpected %s at %d", switch (c) {
                            case ' ' -> "Space";
                            case '\t' -> "Tab";
                            case '\n' -> "LF";
                            default -> "character";
                        }, i)
                    );
                }
                instrBuffer += c;
            }
            else if (instrCollected.param != null && c != '\n')
                paramBuffer += c;
            impCollected = IMP.of(impBuffer);
            instrCollected = Instr.of(impCollected, instrBuffer);
            if (instrCollected != null && (instrCollected.param == null || c == '\n')) {
                Command cmd = new Command(instrCollected);
                if (instrCollected.param != null)
                    cmd.setParam(paramBuffer);
                cmds.add(cmd);
                if (instrCollected == Instr.LABEL) {
                    if (labels.contains(cmd.param))
                        throw new WhitespaceSyntaxErrorException(String.format("Duplicate label '%s'", replaceWS(cmd.param)));
                    labels.add(cmd.param);
                }
                impBuffer = instrBuffer = paramBuffer = "";
            }
        }
        if (!(impBuffer + instrBuffer + paramBuffer).matches("\\x00?"))
            throw new WhitespaceSyntaxErrorException("Unexpected EOF");
        this.cmds = cmds.toArray(Command[]::new);
        this.labels = new int[labels.size()];
        for (int i = 0; i < cmds.size(); i++) {
            Command cmd = cmds.get(i);
            if (cmd.instr.param == Param.NUMBER) {
                cmd.setParamValue(Param.parseNumber(cmd.param));
            } else if (cmd.instr.param == Param.LABEL) {
                if (!labels.contains(cmd.param))
                    throw new WhitespaceSyntaxErrorException(String.format("Undefined label '%s'", replaceWS(cmd.param)));
                if (cmd.instr == Instr.LABEL)
                    labelKey.put(cmd.param, i);
                cmd.setParamValue(labels.indexOf(cmd.param));
            }
        }
        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            this.labels[i] = labelKey.get(label);
        }
    }

    private void exec(Scanner inputSource, int inputLength) {
        inputSource.useDelimiter("\\n?");
        for (int i = 0; i < cmds.length; i++) {
            Command cmd = cmds[i];
            switch (cmd.instr) {
                case PUSH -> stack.push(cmd.paramValue); //--
                case DUP -> stack.push(stack.peek()); //--
                case COPY -> {
                    
                } //--
                case SWAP -> {
                    int top = stack.pop();
                    int next = stack.pop();
                    stack.push(top);
                    stack.push(next);
                } //--
                case DROP -> stack.pop(); //--
                case SLIDE -> {
                    for (int j = 0; j < cmd.paramValue; j++) {
                        stack.pop();
                    }
                } //--
                case ADD -> {
                    int top = stack.pop();
                    int next = stack.pop();
                    stack.push(next + top);
                } //--
                case SUB -> {
                    int top = stack.pop();
                    int next = stack.pop();
                    stack.push(next - top);
                } //--
                case MUL -> {
                    int top = stack.pop();
                    int next = stack.pop();
                    stack.push(next * top);
                } //--
                case DIV -> {
                    int top = stack.pop();
                    int next = stack.pop();
                    stack.push(next / top);
                } //--
                case MOD -> {
                    int top = stack.pop();
                    int next = stack.pop();
                    stack.push(next % top);
                } //--
                case STORE -> {
                    int value = stack.pop();
                    int key = stack.pop();
                    heap.put(key, value);
                } //--
                case RETRIEVE -> {
                    int key = stack.pop();
                    stack.push(heap.get(key));
                } //--
                case LABEL -> {}
                case CALL -> {} //--
                case JMP -> {
                    i = labels[cmd.paramValue];
                } //--
                case JZ -> {
                    int top = stack.pop();
                    if (top == 0)
                        i = labels[cmd.paramValue];
                } //--
                case JN -> {
                    int top = stack.pop();
                    if (top < 0)
                        i = labels[cmd.paramValue];
                } //--
                case RET -> {} //--
                case END -> {
                    return;
                } //--
                case PRINTC -> {} //--
                case PRINTI -> {} //--
                case READC -> {} //--
                case READI -> {} //--
            }
        }
    }

    public void exec(InputStream inputSource) {
        exec(new Scanner(inputSource), -1);
    }

    public void exec(String input) {
        exec(new Scanner(input), input.length());
    }

    private String replaceWS(String ws) {
        return ws.replace(' ', 'S').replace('\t', 'T').replace('\n', 'N');
    }

    public static void main(String[] args) {
        // if (args.length == 0 || args.length > 2) {
        //     System.out.println("Usage: java Whitespace.java \"<file.ws>\" \"[input]\"");
        //     System.exit(-1);
        try {
            String code =
            // "\n\n\n\n  \n";
            Files.readString(Path.of(
                // args[0]
                "java/whitespace-polyglot/HelloWorld.java"
            ));
            Whitespace ws = new Whitespace(code);
            if (args.length < 2) ws.exec(System.in);
            else ws.exec(args[1]);
        } catch (
            WhitespaceSyntaxErrorException
            err
        ) {
            System.err.println(err);
        } catch (NoSuchFileException err) {
            System.err.printf("File '%s' not found.\n", args[0]);
        } catch (OutOfMemoryError err) {
            System.err.printf("File '%s' is too large.\n", args[0]);
        } catch (IOException err) {
            System.err.printf("Could not read file '%s'.\n", args[0]);
        }
    }

    public static class WhitespaceSyntaxErrorException extends Exception {
        public WhitespaceSyntaxErrorException(String message) {
            super(message);
        }
    }
    public static class WhitespaceStackException extends RuntimeException {
        public WhitespaceStackException(String message) {
            super(message);
        }
    }
}

