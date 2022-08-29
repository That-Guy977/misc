import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.InputStream;

public class Brainfuck {
    private final String code;
    private final List<Byte> memory = new ArrayList<>();
    private int pointer = 0;
    private final Loops loops = new Loops();
    { memory.add((byte) 0); }

    private static class Loops {
        private final ArrayList<Integer> opens = new ArrayList<>();
        private final ArrayList<Integer> closes = new ArrayList<>();

        private void addOpen(int cell) {
            opens.add(cell);
        }

        private void addClose(int cell) {
            while (closes.size() < opens.size()) closes.add(null);
            for (int i = opens.size() - 1; i >= 0; i--) {
                if (closes.get(i) == null) {
                    closes.set(i, cell);
                    break;
                }
            }
        }

        private int getClose(int cell) {
            for (int i = 0; i < opens.size(); i++) {
                if (opens.get(i) == cell) {
                    return closes.get(i);
                }
            }
            return 0;
        }

        private int getOpen(int cell) {
            for (int i = 0; i < closes.size(); i++) {
                if (closes.get(i) == cell) {
                    return opens.get(i);
                }
            }
            return 0;
        }
    }

    public Brainfuck(String code) throws BrainfuckSyntaxErrorException {
        this.code = code.replaceAll("[^<>\\[\\].,+\\-]", "");
        int loopCount = 0;
        for (int i = 0; i < this.code.length(); i++) {
            switch (this.code.charAt(i)) {
                case '[':
                    loops.addOpen(i);
                    loopCount++;
                    break;
                case ']':
                    loops.addClose(i);
                    loopCount--;
                    break;
            }
            if (loopCount < 0) throw new BrainfuckSyntaxErrorException("Unmatched ]");
        }
        if (loopCount != 0) throw new BrainfuckSyntaxErrorException("Unmatched [");
    }

    private void exec(Scanner inputSource, int inputLength) {
        inputSource.useDelimiter("\\n?");
        for (int i = 0; i < code.length(); i++) {
            char cmd = code.charAt(i);
            switch (cmd) {
                case '>':
                    if (pointer == Integer.MAX_VALUE)
                        throw new IndexOutOfBoundsException("Pointer out of bounds at " + i);
                    if (++pointer == memory.size()) memory.add((byte) 0);
                    break;
                case '<':
                    if (pointer == 0)
                        throw new IndexOutOfBoundsException("Pointer out of bounds at " + i);
                    pointer--;
                    break;
                case '+':
                    memory.set(pointer, (byte) (memory.get(pointer) + 1));
                    break;
                case '-':
                    memory.set(pointer, (byte) (memory.get(pointer) - 1));
                    break;
                case '[':
                    if (currentCell() == 0)
                        i = loops.getClose(i);
                    break;
                case ']':
                    if (currentCell() != 0)
                        i = loops.getOpen(i);
                    break;
                case '.':
                    System.out.print(Character.toChars(currentCell())[0]);
                    break;
                case ',':
                    if (inputLength != -1) {
                        if (inputLength == 0) throw new NoSuchElementException("Unfulfilled input at " + i);
                        inputLength--;
                    }
                    String input = inputSource.next();
                    System.out.print(input);
                    memory.set(pointer, input.getBytes()[0]);
                    break;
            }
        }
        System.out.println();
    }

    public void exec(InputStream inputSource) {
        exec(new Scanner(inputSource), -1);
    }

    public void exec(String input) {
        exec(new Scanner(input), input.length());
    }

    private int currentCell() {
        return Byte.toUnsignedInt(memory.get(pointer));
    }

    public static void main(String[] args) {
        if (args.length == 0 || args.length > 2) {
            System.out.println("Usage: java Brainfuck.java \"<brainfuck>\" \"[input]\"");
            System.exit(-1);
        }
        try {
            Brainfuck bf = new Brainfuck(args[0]);
            if (args.length < 2) bf.exec(System.in);
            else bf.exec(args[1]);
        } catch (
            BrainfuckSyntaxErrorException
            | IndexOutOfBoundsException
            | NoSuchElementException
            err
        ) {
            System.err.println(err);
        }
    }

    public static class BrainfuckSyntaxErrorException extends Exception {
        public BrainfuckSyntaxErrorException(String message) {
            super(message);
        }
    }
}

