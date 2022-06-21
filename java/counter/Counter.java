import java.util.Scanner;

class Counter {
  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    System.out.print("Count: ");
    final int size = in.nextInt();
    in.nextLine();
    int set = 0;
    boolean[] state = new boolean[size];
    printState(state, set, size);
    while (true) {
      System.out.print("Op: ");
      String op = in.nextLine();
      if (op.equals("end")) break;
      System.out.print("\f");
      String res;
      if (!op.matches("[+-]\\d+")) res = String.format("INVALID CMD '%s'", op);
      else {
        boolean val = op.charAt(0) == '+';
        int pos = Integer.parseInt(op.substring(1));
        if (pos == 0 || pos > size) res = String.format("INVALID POS '%d'", pos);
        else {
          int index = pos - 1;
          boolean currVal = state[index];
          if (currVal == val) res = String.format("NOP (%d)", pos);
          else {
            state[index] = val;
            res = String.format("%s (%d)", val ? "ADDED" : "REMOVED", pos);
            if (val) set++;
            else set--;
          }
        }
      }
      printState(state, set, size);
      System.out.println(res);
    }
    System.out.println("Ended with state " + processFilled(set, size));
  }

  private static String processFilled(int set, int size) {
    return String.format("%s (%d/%d)", set == size ? "FILLED" : "INCOMPLETE", set, size);
  }

  private static void printState(boolean[] state, int set, int size) {
    System.out.println(processFilled(set, size));
    int padding = String.valueOf(size).length();
    String format = String.format("- [%%s] %% %dd\n", padding);
    for (int i = 0; i < state.length; i++)
      System.out.printf(format, state[i] ? "x" : " ", i + 1);
  }
}
