package banking;


public class Main {
    public static void main(String[] args) {
        String baseName = "db.s3db";
        if ((args.length == 2) && (args[0].equals("-fileName"))) {
            baseName = args[1];
        }

        new BankingSystem().run(baseName);

    }
}