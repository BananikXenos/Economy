package xyz.synse.economy.manager.users;

public class Wallet {
    private double balance;

    public void deposit(double money){
        this.balance += money;
    }

    public void withdraw(double money){
        this.balance -= money;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean hasAtLeast(double amount){
        return balance >= amount;
    }
}
