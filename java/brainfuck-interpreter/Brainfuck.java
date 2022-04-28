import java.util.ArrayList;
import java.util.Scanner;
import java.io.InputStream;

import java.util.NoSuchElementException;

public class Brainfuck {
    private final byte[] memory = new byte[256];
    private final String code;
    private final Loops loops = new Loops();
    private byte pointer = 0;

    private class Loops {
        private final ArrayList<Integer> opens, closes;

        private Loops() {
            opens = new ArrayList<>();
            closes = new ArrayList<>();
        }

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
            int index = 0;
            for (int i = 0; i < opens.size(); i++) {
                if (opens.get(i) == cell) {
                    index = i;
                    break;
                }
            }
            return closes.get(index);
        }

        private int getOpen(int cell) {
            int index = 0;
            for (int i = 0; i < closes.size(); i++) {
                if (closes.get(i) == cell) {
                    index = i;
                    break;
                }
            }
            return opens.get(index);
        }
    }

    public Brainfuck(String code) {
        String bf = code.replaceAll("[^<>\\[\\].,+\\-]", "");
        int loop = 0;
        for (int i = 0; i < bf.length(); i++) {
            char cmd = bf.charAt(i);
            if (cmd == '[') {
                loop++;
                loops.addOpen(i);
            } else if (cmd == ']') {
                loop--;
                loops.addClose(i);
            }
            String errorMsg = null;
            if (loop < 0) errorMsg = "Unmatched ]";
            else if (loop > 256) errorMsg = "Deep looping (Max 256)";
            if (errorMsg != null) throw new IllegalArgumentException(errorMsg + " at %d" + i);
        }
        if (loop != 0) throw new IllegalArgumentException("Unmatched [");
        this.code = bf;
    }

    private void exec(Scanner inputSource, int inputLength) throws NoSuchElementException {
        inputSource.useDelimiter("\\n|");
        for (int i = 0; i < code.length(); i++) {
            char cmd = code.charAt(i);
            switch (cmd) {
                case '>':
                    if (pointer == 127)
                        throw new IndexOutOfBoundsException("Pointer out of bounds (256) at " + i);
                    pointer++;
                    break;
                case '<':
                    if (pointer == -128)
                        throw new IndexOutOfBoundsException("Pointer out of bounds (-1) at " + i);
                    pointer--;
                    break;
                case '+':
                    memory[pointerLoc()]++;
                    break;
                case '-':
                    memory[pointerLoc()]--;
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
                    if (inputLength == -1)
                        while (!inputSource.hasNext());
                    else {
                        if (inputLength == 0) throw new NoSuchElementException("Unfulfilled input at " + i);
                        inputLength--;
                    }
                    String input = inputSource.next();
                    if (inputLength != -1) System.out.print(input);
                    memory[pointerLoc()] = input.getBytes()[0];
                    break;
            }
        }
        System.out.println();
    }

    public void exec(InputStream inputSource) throws NoSuchElementException {
        exec(new Scanner(inputSource), -1);
    }

    public void exec() throws NoSuchElementException {
        if (code.contains(",")) throw new IllegalArgumentException("Code contains input");
        exec("");
    }

    public void exec(String input) throws NoSuchElementException {
        exec(new Scanner(input), input.length());
    }

    private int pointerLoc() {
        return Byte.toUnsignedInt(pointer);
    }

    private int currentCell() {
        return Byte.toUnsignedInt(memory[pointerLoc()]);
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
            IllegalArgumentException
            | IndexOutOfBoundsException
            | NoSuchElementException
            err
        ) {
            System.err.println(err);
            System.exit(-1);
        }
    }
}
