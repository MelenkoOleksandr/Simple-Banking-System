package banking;

public class Main {
    public static void main(String[] args) {
        String dbName = "";
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-fileName")){
                dbName = args[i + 1];
            }
        }
        Bank bank = new Bank(dbName);
        bank.work();
    }
}