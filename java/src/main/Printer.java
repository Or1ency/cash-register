package main;

import domain.Order;
import enums.PromotionType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

/**
 * Created by Oriency on 2022/6/7.
 */
public class Printer {

    private String title = "***<没钱赚商店>购物清单***";
    private String split = "----------------------";
    private String endSplit = "**********************";
    private Order order;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getEndSplit() {
        return endSplit;
    }

    public void setEndSplit(String endSplit) {
        this.endSplit = endSplit;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void print() {
        //print title
        System.out.println(this.title);
        //print goods info
        printGoodsInfo();
        System.out.println(this.split);
        printGoodsPromotion();
        //print total
        printTotal();
        //print end
        System.out.println(this.endSplit);
    }

    private void printGoodsInfo() {
        order.getOrderGoods().forEach(
                orderGoods -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    String quantity = orderGoods.getQuantity().toString();
                    if (!orderGoods.isWeight()) {
                        DecimalFormat df = new DecimalFormat("#");
                        quantity = df.format(orderGoods.getQuantity());
                    }
                    stringBuilder.append("名称:").append(orderGoods.getName()).append(",").append("数量:")
                            .append(quantity).append(orderGoods.getUnit()).append(",").append("单价:")
                            .append(orderGoods.getPrice()).append("(元),").append("小计:").append(orderGoods.getAmount())
                            .append("(元)");
                    if (null != orderGoods.getPromotion() && orderGoods.getPromotion()
                            && orderGoods.getType().equals(PromotionType.DISCOUNT)) {
                        stringBuilder.append(",").append("节省").append(orderGoods.getDiscountAmount()).append("(元)");
                    }
                    System.out.println(stringBuilder.toString());
                }
        );
    }

    private void printTotal() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("总计:").append(order.getTotal()).append("(元)");
        if (order.getDiscount().compareTo(new BigDecimal(BigInteger.ZERO)) > 0) {
            stringBuilder.append("\n").append("节省:").append(order.getDiscount()).append("(元)");
        }
        System.out.println(stringBuilder.toString());
    }

    private void printGoodsPromotion() {
        if (null != order.getRewards() && order.getRewards().size() > 0) {
            System.out.println("买二赠一商品:");
            order.getRewards().forEach(
                    reward -> {
                        String stringBuilder = "名称:" + reward.getName() + "," + "数量:" +
                                reward.getQuantity() + reward.getUnit();
                        System.out.println(stringBuilder);
                    }
            );
            System.out.println(this.split);
        }
    }

}
