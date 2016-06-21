package ua.zadyraka.shops;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarOutputStream;

/**
 * Created by raul on 20.06.16.
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
                //Add products to categories
                for (String idCategory : myFirstShop.getIdCategoryList()){
                    for (int i = 0; i < 4; i++) {
                        myFirstShop.addProduct(idCategory,
                                new Product("idProd" + i, "Product" + i, (double)10 + i, "Available"));
                    }
                }
                //Change status of the products in the category to «Absent»
                String idCategory = myFirstShop.getIdCategoryList().get(0);
                List<Product> productList = myFirstShop.getListProducts(idCategory);
                for (Product product : productList){
                    product.setStatus("Absent");
                    myFirstShop.updateProduct(idCategory, product);
                }

                int idCategoryListSize = myFirstShop.getIdCategoryList().size();
                Map<String, List<Product>> productListMap = new HashMap<String, List<Product>>();
                int i = 1;
                for ( ; i < idCategoryListSize; i++ ){
                    idCategory = myFirstShop.getIdCategoryList().get(i);
                    productList = myFirstShop.getListProducts(idCategory);
                    productListMap.put(idCategory, productList);
                }
                for (String key : productListMap.keySet()){
                    idCategory = key;
                    productList = productListMap.get(idCategory);
                    //Half of the products of the remaining categories, change the status to «Expected»
                    for (i = 0; i < productList.size()/2; i++){
                        Product product = productList.get(i);
                        product.setStatus("Expected");
                        myFirstShop.updateProduct(idCategory, product);
                    }
                    //For products that are available to increase the price by 20%
                    for (i = productList.size()/2; i < productList.size(); i++){
                        Product product = productList.get(i);
                        product.setPrice(product.getPrice() + product.getPrice() * 0.2);
                        myFirstShop.updateProduct(idCategory, product);
                    }
                }
            }
        });

        Thread secondThread = new Thread(new Runnable() {
            public void run() {
                //Add products to categories
                for (String idCategory : mySecondShop.getIdCategoryList()){
                    for (int i = 0; i < 4; i++) {
                        mySecondShop.addProduct(idCategory,
                                new Product("idProd" + i, "Product" + i, (double)10 + i, "Available"));
                    }
                }
                //Change status of the products in the category to «Absent»
                String idCategory = mySecondShop.getIdCategoryList().get(0);
                List<Product> productList = mySecondShop.getListProducts(idCategory);
                for (Product product : productList){
                    product.setStatus("Absent");
                    mySecondShop.updateProduct(idCategory, product);
                }

                int idCategoryListSize = mySecondShop.getIdCategoryList().size();
                Map<String, List<Product>> productListMap = new HashMap<String, List<Product>>();
                int i = 1;
                for ( ; i < idCategoryListSize; i++ ){
                    idCategory = mySecondShop.getIdCategoryList().get(i);
                    productList = mySecondShop.getListProducts(idCategory);
                    productListMap.put(idCategory, productList);
                }
                for (String key : productListMap.keySet()){
                    idCategory = key;
                    productList = productListMap.get(idCategory);
                    //Half of the products of the remaining categories, change the status to «Expected»
                    for (i = 0; i < productList.size()/2; i++){
                        Product product = productList.get(i);
                        product.setStatus("Expected");
                        mySecondShop.updateProduct(idCategory, product);
                    }
                    //For products that are available to increase the price by 20%
                    for (i = productList.size()/2; i < productList.size(); i++){
                        Product product = productList.get(i);
                        product.setPrice(product.getPrice() + product.getPrice() * 0.2);
                        mySecondShop.updateProduct(idCategory, product);
                    }
                }
            }
        });

        try {
            firstThread.start();
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
