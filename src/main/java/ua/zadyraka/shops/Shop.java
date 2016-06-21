package ua.zadyraka.shops;

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Class for products
 * @author Zadyraka Sergey
 */
public class Shop {
    private String name;
    private List<String> idCategoryList;
    private static Map<String, Shop> instances = new HashMap<String, Shop>();

    private static Mongo mongo = null;
    private static DB dataBase = null;

    private Shop(String name, List<String> idCategoryList) {
        this.name = name;
        this.idCategoryList = idCategoryList;
    }

    /**
     * Connect to MongoDB and load shops from database
     */
    static void loadShops(){
        mongo = new Mongo("localhost", 27017);
        dataBase = mongo.getDB("dbShops");
        DBCollection dbCollection = dataBase.getCollection("shops");
        DBCursor cursor = dbCollection.find();
        while (cursor.hasNext()){
            DBObject dboShop =  cursor.next();
            String shopName = (String)dboShop.get("name");
            List<String> idCategoryList = (List<String>)dboShop.get("category_id");
            new Shop(shopName, idCategoryList).store();
        }
    }

    private void store(){
        instances.put(this.getName(), this);
    }

    public static Shop getInstance(String shopName){
        return instances.get(shopName);
    }

    public String getName() {
        return name;
    }

    public List<String> getIdCategoryList() {
        return idCategoryList;
    }

    /**
     * Add product in the category
     * @param idCategory - category id
     * @param product - instance of Product class
     */
    public void addProduct(String idCategory, Product product){
        DBCollection dbCollection = dataBase.getCollection("categories");
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", idCategory);

        BasicDBObject dboProductAdd = new BasicDBObject();
        dboProductAdd.put("_id", product.getId() );
        dboProductAdd.put("title", product.getTitle());
        dboProductAdd.put("price", product.getPrice());
        dboProductAdd.put("status", product.getStatus());

        BasicDBObject dboUpdate = new BasicDBObject();
        dboUpdate.put("$push", new BasicDBObject( "products", dboProductAdd ));
        dbCollection.update(searchQuery, dboUpdate);
    }

    /**
     * Get products list from the category
     * @param idCategory - category id
     * @return products list
     */
    public List<Product> getListProducts(String idCategory){
        DBCollection dbCollection = dataBase.getCollection("categories");
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", idCategory);

        DBCursor cursor = dbCollection.find(searchQuery);
        List<Product> productList = new ArrayList<Product>();
        BasicDBObject category = null;

        if (cursor.hasNext()) {
            category = (BasicDBObject) cursor.next();
        } else {
            return productList;
        }

        BasicDBList products = (BasicDBList) category.get("products");
        for (Iterator<Object> it = products.iterator(); it.hasNext();){
            BasicDBObject dboProduct = (BasicDBObject) it.next();
            Product product = new Product();
            product.setId((String)dboProduct.get("_id"));
            product.setTitle((String)dboProduct.get("title"));
            product.setPrice((Double) dboProduct.get("price"));
            product.setStatus((String) dboProduct.get("status"));

            productList.add(product);
        }

        return productList;
    }
    /**
     * Update product in the category
     * @param idCategory - category id
     * @param product - instance of Product class
     */
    public void updateProduct(String idCategory, Product product){
        DBCollection dbCollection = dataBase.getCollection("categories");
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", idCategory);
        searchQuery.put("products._id", product.getId());

        BasicDBObject dboProductUpd = new BasicDBObject();
        dboProductUpd.put("products.$.title", product.getTitle());
        dboProductUpd.put("products.$.price", product.getPrice());
        dboProductUpd.put("products.$.status", product.getStatus());

        BasicDBObject dboUpdate = new BasicDBObject();
        dboUpdate.put("$set", dboProductUpd);
        dbCollection.update(searchQuery, dboUpdate);
    }

    /**
     * Close connection
     */
    static void closeConn(){
        mongo.close();
    }

    static void doOperation(Shop shop){
        //Add products to categories
        for (String idCategory : shop.getIdCategoryList()){
            for (int i = 0; i < 4; i++) {
                shop.addProduct(idCategory,
                        new Product("idProd" + i, "Product" + i, (double)10 + i, "Available"));
            }
        }
        //Change status of the products in the category to «Absent»
        String idCategory = shop.getIdCategoryList().get(0);
        List<Product> productList = shop.getListProducts(idCategory);
        for (Product product : productList){
            product.setStatus("Absent");
            shop.updateProduct(idCategory, product);
        }

        int idCategoryListSize = shop.getIdCategoryList().size();
        Map<String, List<Product>> productListMap = new HashMap<String, List<Product>>();
        int i = 1;
        for ( ; i < idCategoryListSize; i++ ){
            idCategory = shop.getIdCategoryList().get(i);
            productList = shop.getListProducts(idCategory);
            productListMap.put(idCategory, productList);
        }
        for (String key : productListMap.keySet()){
            idCategory = key;
            productList = productListMap.get(idCategory);
            //Half of the products of the remaining categories, change the status to «Expected»
            for (i = 0; i < productList.size()/2; i++){
                Product product = productList.get(i);
                product.setStatus("Expected");
                shop.updateProduct(idCategory, product);
            }
            //For products that are available to increase the price by 20%
            for (i = productList.size()/2; i < productList.size(); i++){
                Product product = productList.get(i);
                product.setPrice(product.getPrice() + product.getPrice() * 0.2);
                shop.updateProduct(idCategory, product);
            }
        }

    }

    @Override
    public String toString() {
        return "Shop{" +
                "name = '" + name + '\'' +
                ", idCategoryList = " + idCategoryList +
                '}';
    }
}
