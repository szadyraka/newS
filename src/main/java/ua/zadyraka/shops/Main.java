package ua.zadyraka.shops;

import javax.swing.*;

/**
 * Main class
 * @author Zadyraka Sergey
 */
public class Main {

    public final static String MY_FIRST_SHOP = "myFirstShop";
    public final static String MY_SECOND_SHOP = "mySecondShop";

    public static void main(String[] args) {
        Shop.loadShops();
        final Shop myFirstShop = Shop.getInstance(MY_FIRST_SHOP);
        final Shop mySecondShop = Shop.getInstance(MY_SECOND_SHOP);

        Thread firstThread = new Thread(new Runnable() {
            public void run() {
                Shop.doOperation(myFirstShop);
            }
        });

        Thread secondThread = new Thread(new Runnable() {
            public void run() {
                Shop.doOperation(mySecondShop);
            }
        });

        firstThread.start();

        try {
            Thread.sleep(10000);
            secondThread.start();
            firstThread.join();
            secondThread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        Shop.closeConn();

        JOptionPane.showMessageDialog(null, "Program has finished!", "Message", JOptionPane.INFORMATION_MESSAGE );
        System.exit(0);
    }
}
