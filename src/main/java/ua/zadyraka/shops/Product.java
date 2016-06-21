package ua.zadyraka.shops;

/**
 * Class for products
 * @author Zadyraka Sergey
 */
public class Product {
    private String id;
    private String title;
    private Double price;
    private String status;

    public Product(String id, String title, Double price, String status) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.status = status;
    }

    public Product() {
        this.id = null;
        this.title = null;
        this.price = null;
        this.status = null;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id = " + id +
                ", title = '" + title + '\'' +
                ", price = " + price +
                ", status = '" + status + '\'' +
                '}';
    }
}
