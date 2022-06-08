package domain;

import enums.PromotionType;

import java.math.BigDecimal;

/**
 * Created by Oriency on 2022/6/7.
 */
public class OrderGoods {

    private Integer id;
    private String name;
    private String barcode;
    private Double quantity;
    private String unit;
    private Promotion promotion;
    private Boolean boolPromotion;                      //goods Whether or not join promotion
    private PromotionType type;                         //promotion type
    private Double discount;                            //promotion discount
    private Integer reward;                             //x for free
    private BigDecimal price;
    private BigDecimal amount;
    private boolean weight = false;
    private BigDecimal discountAmount = BigDecimal.ZERO; //if promotion is discount that value is price * discount else is reward price

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public Boolean getBoolPromotion() {
        return boolPromotion;
    }

    public void setBoolPromotion(Boolean boolPromotion) {
        this.boolPromotion = boolPromotion;
    }

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Integer getReward() {
        return reward;
    }

    public void setReward(Integer reward) {
        this.reward = reward;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public boolean isWeight() {
        return weight;
    }

    public void setWeight(boolean weight) {
        this.weight = weight;
    }
}
