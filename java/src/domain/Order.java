package domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oriency on 2022/6/7.
 */
public class Order {

    private Integer id;
    private BigDecimal total;                                       //total amount
    private BigDecimal discount = BigDecimal.ZERO;                  //total discount
    private List<OrderGoods> orderGoods = new ArrayList<>();        //goods for order
    private List<RewardGoods> rewards = new ArrayList<>();          //reward for order

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public List<OrderGoods> getOrderGoods() {
        return orderGoods;
    }

    public void setOrderGoods(List<OrderGoods> orderGoods) {
        this.orderGoods = orderGoods;
    }

    public List<RewardGoods> getRewards() {
        return rewards;
    }

    public void setRewards(List<RewardGoods> rewards) {
        this.rewards = rewards;
    }
}
