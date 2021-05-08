package banking;

import java.util.Random;

public class BankCard {
    final static Random random = new Random();
    private  String cardNumber;
    private String password;
    private int ballance;

    BankCard(String cardNumber1, String password1, int ballance1) {
        cardNumber = cardNumber1;
        password = password1;
        ballance = ballance1;
    }

    BankCard() {
        String accountIdentifier = createIdentifier();
        cardNumber = "400000" + accountIdentifier;
        password = createPassword();
        ballance = 0;

    }

    //Method for creating cardNumber
    protected String createIdentifier() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            str.append(random.nextInt(9));
        }
        char lastChar = luchAlgoritm("400000" + str.toString());
        return str.toString() + lastChar;
    }

    //Algoritm to find last number
    public static char luchAlgoritm(String str) {
        char[] strArr = str.toCharArray();
        int sum = 0;
        char res = '0';
        for (int i = 0; i < strArr.length; i++) {
            int current = strArr[i] - '0';
            if ((i + 1) % 2 != 0) {
                current *= 2;
            }
            if (current > 9) {
                current -= 9;
            }
            sum += current;
        }
        for (int i = 0; i < 10; i++) {
            if ((sum + i) % 10 == 0) {
                res = (char)(i + '0');
            }
        }
        return res;
    }

    //Method for creating password
    protected String createPassword() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            str.append(random.nextInt(9));
        }
        return str.toString();
    }

    //checkInBankSys

    public boolean checkCardPassword(String password1) {
        return password1.equals(this.password);
    }

    public boolean checkCardNumber(String num) { return num.equals(this.cardNumber); }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public int getBallance() {
        return this.ballance;
    }

    public void addIncome(int income) {
        this.ballance += income;
    }

    @Override
    public String toString() {
        return "Your card number:\n" + cardNumber + "\n" +"Your card PIN:\n" + password + "\n";
    }

    //Method for adding card to DB
    public String stringForDB() {
        //System.out.println("(" + cardNumber + ", " + password + ", " + ballance + ")");
        return "(" + cardNumber + ", " + password + ", " + ballance + ");";
    }




}
