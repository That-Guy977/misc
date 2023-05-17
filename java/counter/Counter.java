import java.util.Arrays;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Counter {
    private int count, cap;
    private int[] state;
    
    @FunctionalInterface
    private interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }
    
    enum Operation {
        ADD(Counter::add, Counter::add),
        REM(Counter::rem, Counter::rem),
        SET(Counter::set, Counter::set);
        
        private BiFunction<Counter, Integer, Boolean> unary;
        private TriFunction<Counter, Integer, Integer, Boolean> binary;
        
        Operation(BiFunction<Counter, Integer, Boolean> unary, TriFunction<Counter, Integer, Integer, Boolean> binary) {
            this.unary = unary;
            this.binary = binary;
        }
        
        boolean apply(Counter counter, int pos) {
            return unary.apply(counter, pos);
        }
        
        boolean apply(Counter counter, int pos, int val) {
            return binary.apply(counter, pos, val);
        }
        
        static Operation of(String opcode) {
            return switch (opcode) {
                case "+" -> ADD;
                case "-" -> REM;
                case "=" -> SET;
                default -> null;
            };
        }
    }

    public Counter(int count, int cap) {
        if (count <= 0) {
            throw new IllegalArgumentException("'count' must be positive");
        }
        if (cap <= 0) {
            throw new IllegalArgumentException("'cap' must be positive");
        }
        this.count = count;
        this.cap = cap;
        this.state = new int[count];
    }

    public Counter(int count) {
        this(count, 1);
    }

    public int get(int pos) {
        return state[pos];
    }

    public boolean add(int pos, int val) {
        if (val < 1 || val > cap) {
            throw new IllegalArgumentException(String.format("Invalid value '%d'", val));
        }
        if (state[pos] + val <= cap) {
            state[pos] += val;
            return true;
        }
        return false;
    }
    
    public boolean add(int pos) {
        return add(pos, 1);
    }
    
    public boolean rem(int pos, int val) {
        if (val < 1 || val > cap) {
            throw new IllegalArgumentException(String.format("Invalid value '%d'", val));
        }
        if (state[pos] - val >= 0) {
            state[pos] -= val;
            return true;
        }
        return false;
    }

    public boolean rem(int pos) {
        return rem(pos, 1);
    }

    public boolean set(int pos, int val) {
        if (val < 0 || val > cap) {
            throw new IllegalArgumentException(String.format("Invalid value '%d'", val));
        }
        if (state[pos] != val) {
            state[pos] = val;
            return true;
        }
        return false;
    }

    public boolean set(int pos) {
        return set(pos, 0);
    }

    public String state() {
        String[] result = new String[count + 1];
        result[0] = "== State ==";
        String format = String.format("[%%-%ds] %%%dd", cap, String.valueOf(count).length());
        for (int i = 0; i < count; i++) {
            result[i + 1] = String.format(format, "x".repeat(state[i]), i + 1);
        }
        return String.join("\n", result);
    }

    public String info() {
        String[] result = new String[cap + 5];
        result[0] = "== Info ==";
        result[1] = String.format("Count: %d", count);
        result[2] = String.format("Cap: %d", cap);
        int[] set = new int[cap + 1];
        int total = 0;
        for (int i = 0; i < count; i++) {
            set[state[i]]++;
            total += state[i];
        }
        String format = String.format("  %%-%ds: %%%2$dd/%%%2$dd (%%6.2f%%%%)", String.valueOf(cap).length(), String.valueOf(count).length());
        for (int i = 0; i <= cap; i++) {
            result[i + 3] = String.format(format, i, set[i], count, set[i] * 100d / count);
        }
        result[cap + 4] = String.format("Total: %d/%d (%6.2f%%)", total, count * cap, total * 100d / cap / count);
        return String.join("\n", result);
    }

    public void reset() {
        for (int i = 0; i < count; i++) {
            state[i] = 0;
        }
    }

    public static void main(String[] args) {
        validateArgs(args);
        Counter counter = Counter.from(args);
        Scanner in = new Scanner(System.in);
        boolean hidden = false;
        System.out.println(counter.state());
        System.out.println();
        while (true) {
            System.out.print("Operation: ");
            String cmd = in.nextLine();
            switch (cmd) {
                case "help" -> {
                    System.out.println("""
                    Operations:
                        help - show this help message
                        info - show detailed status
                        show - enables automatic printing
                        hide - disables automatic printing
                        print - print state
                        reset - reset counter
                        end - end counter
                        n+[v] - add at pos 'n' value 'v' (default 1)
                        n-[v] - rem at pos 'n' value 'v' (default 1)
                        n=[v] - set at pos 'n' value 'v' (default 0)
                    """);
                }
                case "info" -> {
                    System.out.println(counter.info());
                    System.out.println();
                }
                case "show" -> {
                    hidden = false;
                    System.out.println(counter.state());
                }
                case "hide" -> {
                    hidden = true;
                }
                case "print" -> {
                    System.out.println(counter.state());
                }
                case "reset" -> {
                    counter.reset();
                    System.out.println(counter.state());
                }
                case "end" -> {
                    in.close();
                    System.exit(0);
                }
                default -> {
                    Matcher matcher = Pattern.compile("(-?\\d+)([+-=])(-?\\d+)?").matcher(cmd);
                    if (matcher.find()) {
                        Operation op = Operation.of(matcher.group(2));
                        int pos = Integer.parseInt(matcher.group(1));
                        int val = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : -1;
                        try {
                            boolean res = val == -1 ? op.apply(counter, pos - 1) : op.apply(counter, pos - 1, val);
                            System.out.printf("%s %d (%d)\n", res ? op.name() : "NOP", pos, counter.get(pos - 1));
                            if (!hidden) {
                                System.out.println(counter.state());
                                System.out.println();
                            }
                        } catch (ArrayIndexOutOfBoundsException err) {
                            System.out.printf("Invalid position '%d'\n", pos);
                        } catch (IllegalArgumentException err) {
                            System.out.printf("Invalid value '%d'\n", val);
                        }
                    } else {
                        System.out.printf("Invalid command '%s'\n", cmd);
                    }
                }
            }
        }
    }

    private static Counter from(String[] args) {
        int count = Integer.parseInt(args[0]);
        if (args.length < 2) {
            return new Counter(count);
        } else {
            return new Counter(count, Integer.parseInt(args[1]));
        }
    }

    private static void validateArgs(String[] args) {
        boolean allPositive = Arrays.stream(args).allMatch((arg) -> arg.matches("\\d+") && Integer.parseInt(arg) > 0);
        if (args.length == 0 || args.length > 2 || !allPositive) {
            System.out.println("Usage: java Counter.java <element count> [count per element]");
            System.exit(-1);
        }
    }
}
