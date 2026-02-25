package com.example.marketplace.model;
import java.util.Map;

public class Basket {

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
    public Map<Long, Integer> items;


    public Basket(Long userId){
      this.userId = userId;
    }

    public Long getUserId(){
      return this.userId;
    }



    
}
