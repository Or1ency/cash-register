package domain;

import enums.PromotionType;

import java.util.List;
import java.util.Set;

/**
 * Created by Oriency on 2022/6/7.
 * goods promotion
 */
public class Promotion {

    private Integer id;
    private Set<String> barcode;            //to find goods which in promotion
    private PromotionType type;             //promotion type
    private Double discount;                //promotion discount
    private Integer purchase;               //Buy x
    private Integer reward;                 //y for free

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<String> getBarcode() {
        return barcode;
    }

    public void setBarcode(Set<String> barcode) {
        this.barcode = barcode;
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

    public Integer getPurchase() {
        return purchase;
    }

    public void setPurchase(Integer purchase) {
        this.purchase = purchase;
    }

    public Integer getReward() {
        return reward;
    }

    public void setReward(Integer reward) {
        this.reward = reward;
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", barcode=" + barcode +
                ", type=" + type +
                ", discount=" + discount +
                ", purchase=" + purchase +
                ", reward=" + reward +
                '}';
    }
}
