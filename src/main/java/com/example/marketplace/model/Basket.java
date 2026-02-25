package com.example.marketplace.model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Basket implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class BasketItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Product product;
        private int quantity;

        public BasketItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return this.product;
        }

        public int getQuantity() {
            return this.quantity;
        }

        public void increment() {
            this.quantity++;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getSubtotal() {                                                                                                                                                                           
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));                                                                                                                                       
        }
    }

    /*
                                                                                                                                                                                                                         
  basket_item                                                                                                                                                                                                                          
                                                                                                                                                                                                                                                                                                                                                                                                                            
  - product_id BIGINT NOT NULL (FK -> product.id)                                                                                                                                                                                      
  - quantity INT NOT NULL                                                                                                                                                                                                              
                                                                                                                                                                                                                                                                                                                                                                                                                      
  Constraints:                                                                                                                                                                                                                         
                                                                                                                                                                                                                                                                                                                                                                                                                                         
  - check quantity > 0                                                                                                                                                                                                                 
  - unique (basket_id, product_id) to prevent duplicate rows for same product in same basket                                                                                                                                           
                                                                                                                                                                                                                                       
  Relationship summary:                                                                                                                                                                                                                
                                                                                                                                                                                                                                       
  - User 1 -> many Basket (or 1 -> 1 active basket)                                                                                                                                                                                    
  - Basket 1 -> many BasketItem                                                                                                                                                                                                        
  - BasketItem many -> 1 Product
    
    */

    private Long userId;
    public Map<Long, BasketItem> items;



    public Basket(Long userId){
      this.userId = userId;
      this.items = new HashMap<>();
    }

    public Long getUserId(){
      return this.userId;
    }

    public BigDecimal getSubtotal() {
      BigDecimal subtotal = BigDecimal.ZERO;
      for (BasketItem item : items.values()) {
        subtotal = subtotal.add(item.getSubtotal());
      }
      return subtotal;
    }

    public BigDecimal getShipping() {
      return BigDecimal.TWO;
    }

    public BigDecimal getTotal() {
      return getSubtotal().add(getShipping());
    }


    
}
