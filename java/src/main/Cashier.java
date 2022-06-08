package main;

import domain.*;
import enums.PromotionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Oriency on 2022/6/7.
 */
public class Cashier {

    private static final int SCALE = 2;
    private Map<String, Goods> GOODS_MAP = new HashMap<>();
    private List<Promotion> promotions = new ArrayList<>();

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public static void main(String[] args) {
        //step 1. new Mock to initialize basic data such as goods info
        MockData mockData = new MockData();
        //测试随机产生模拟数据是否出错
        testMockData(mockData, 10000);
        //step 2. new Cashier
        Cashier cashier = new Cashier();
        //step 3. set data into cashier
        cashier.GOODS_MAP = mockData.goodsMap();
        /*
         * 随机模拟购买场景
         */
        mockPurchase(cashier, mockData, 5);
        /*
         * 以下是按照题目的测试用例输出
         */
        System.out.println("#####################以下是按照题目的测试用例输出##################### \r\n");
        testCase(cashier);
    }

    /**
     * 模拟购买
     *
     * @param cashier  cashier
     * @param mockData mockData
     * @param loop     mock print times
     */
    public static void mockPurchase(Cashier cashier, MockData mockData, int loop) {
        //优惠折扣
        double mockDiscount = (int) ((Math.random() * (1 - 0.5) + 0.5) * 100.0) / 100.0;
        //purchase num
        int mockPurchase = 2;
        //free num
        int mockReward = 1;
        List<Promotion> mockPromotions = mockData.mockPromotion(mockDiscount, mockPurchase, mockReward);
        //设置收银机优惠活动
        cashier.setPromotions(mockPromotions);
        //模拟购买
        int i = 0;
        while (i < loop) {
            //模拟真实购买场景，购买一定数量的商品
            List<String> mockBarcodes = mockData.mockBarcodes(3, 5, 1);
            cashier.purchase(mockBarcodes);
            i++;
        }
    }

    /**
     * 输出题目测试结果
     *
     * @param cashier cashier
     */
    public static void testCase(Cashier cashier) {
        // case测试
        // 构造指定数据
        List<String> barcodes = Arrays.asList("ITEM000001", "ITEM000001", "ITEM000001", "ITEM000002", "ITEM000002",
                "ITEM000002", "ITEM000002", "ITEM000002", "ITEM000017-2");
        // 1.当购买的商品中，有符合“买二赠一”优惠条件的商品时
        Promotion promotion = new Promotion();
        promotion.setReward(1);
        promotion.setPurchase(2);
        promotion.setType(PromotionType.REWARD);
        HashSet<String> rewardBarcode = new HashSet<>();
        rewardBarcode.add("ITEM000001");
        rewardBarcode.add("ITEM000002");
        promotion.setBarcode(rewardBarcode);
        cashier.setPromotions(Collections.singletonList(promotion));
        cashier.purchase(barcodes);
        // 2.当购买的商品中，没有符合“买二赠一”优惠条件的商品时
        cashier.setPromotions(null);
        cashier.purchase(barcodes);
        // 3.当购买的商品中，有符合“95折”优惠条件的商品时
        Promotion discountPromotion = new Promotion();
        discountPromotion.setDiscount(0.95);
        discountPromotion.setType(PromotionType.DISCOUNT);
        HashSet<String> discountBarcode = new HashSet<>();
        discountBarcode.add("ITEM000017");
        discountPromotion.setBarcode(discountBarcode);
        cashier.setPromotions(Collections.singletonList(discountPromotion));
        cashier.purchase(barcodes);
        // 4.当购买的商品中，有符合“95折”优惠条件的商品，又有符合“买二赠一”优惠条件的商品时
        List<Promotion> promotionList = new ArrayList<>();
        //将普通商品同时设置折扣，测试优惠的优先级
        discountBarcode.add("ITEM000001");
        discountPromotion.setBarcode(discountBarcode);
        promotionList.add(promotion);
        promotionList.add(discountPromotion);
        cashier.setPromotions(promotionList);
        cashier.purchase(Arrays.asList("ITEM000001", "ITEM000001", "ITEM000001", "ITEM000002", "ITEM000002",
                "ITEM000002", "ITEM000002", "ITEM000002", "ITEM000002", "ITEM000017-2"));
    }

    public void purchase(List<String> items) {
        //统计不同商品的购买数量
        Map<String, Long> itemsMap = items.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Order order = new Order();
        List<OrderGoods> orderGoodsList = new ArrayList<>();
        itemsMap.forEach(
                (barcode, quantity) -> {
                    //计算并生成商品结算数据
                    OrderGoods orderGoods = calculate(barcode, quantity);
                    orderGoodsList.add(orderGoods);
                }
        );
        //构造满赠数据结构
        List<RewardGoods> rewardGoods = orderGoodsList.stream()
                .filter(
                        orderGoods -> (null != orderGoods.getBoolPromotion() && orderGoods.getBoolPromotion()
                                && orderGoods.getType().equals(PromotionType.REWARD)))
                .map(orderGoods -> {
                            RewardGoods rewardGood = new RewardGoods();
                            rewardGood.setPromotion(orderGoods.getPromotion());
                            rewardGood.setBarcode(orderGoods.getBarcode());
                            rewardGood.setName(orderGoods.getName());
                            rewardGood.setQuantity(orderGoods.getReward());
                            rewardGood.setUnit(orderGoods.getUnit());
                            return rewardGood;
                        }
                ).collect(Collectors.toList());
        order.setRewards(rewardGoods);
        order.setOrderGoods(orderGoodsList);
        List<BigDecimal> amountList = orderGoodsList.stream().map(OrderGoods::getAmount).collect(Collectors.toList());
        amountList.stream().reduce(BigDecimal::add).ifPresent(order::setTotal);
        List<BigDecimal> discountList = orderGoodsList.stream().map(OrderGoods::getDiscountAmount).collect(Collectors.toList());
        discountList.stream().reduce(BigDecimal::add).ifPresent(order::setDiscount);
        //封装打印数据
        Printer printer = new Printer();
        printer.setOrder(order);
        //调用打印
        print(printer);
    }

    /**
     * 根据商品二维码返回对应的商品所参与的活动信息 没有则返回Null
     *
     * @param barcode 商品二维码
     * @return promotion
     */
    public Promotion goodsHasPromotion(String barcode) {
        if (null != promotions) {
            Optional<Promotion> discountPromotion = promotions.stream().filter(promotion ->
                    promotion.getType().equals(PromotionType.DISCOUNT)).findFirst();
            Optional<Promotion> rewardPromotion = promotions.stream().filter(promotion ->
                    promotion.getType().equals(PromotionType.REWARD)).findFirst();
            //reward promotion is priority
            if (rewardPromotion.isPresent() && rewardPromotion.get().getBarcode().contains(barcode)) {
                return rewardPromotion.get();
            } else if (discountPromotion.isPresent() && discountPromotion.get().getBarcode().contains(barcode)) {
                return discountPromotion.get();
            }
        }
        return null;
    }

    /**
     * 计算商品价钱
     *
     * @param barcodes 商品二维码
     * @param quantity 商品数量
     * @return 商品结算信息
     */
    private OrderGoods calculate(String barcodes, double quantity) {
        String[] barcodesArray = barcodes.split("-");
        String barcode = barcodesArray[0];
        //查找对应商品
        Goods goods = GOODS_MAP.get(barcodesArray[0]);
        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setName(goods.getName());
        orderGoods.setBarcode(barcode);
        orderGoods.setPrice(goods.getPrice());
        orderGoods.setUnit(goods.getUnit());
        if (goods.isWeight()) {
            quantity = Double.parseDouble(barcodesArray[1]);
        }
        orderGoods.setWeight(goods.isWeight());
        orderGoods.setQuantity(quantity);
        //get promotion config
        Promotion promotion = goodsHasPromotion(barcode);
        BigDecimal amount = BigDecimal.valueOf(quantity).multiply(goods.getPrice())
                .setScale(SCALE, RoundingMode.HALF_UP);
        if (null != promotion) {
            orderGoods.setBoolPromotion(true);
            orderGoods.setPromotion(promotion);
            orderGoods.setType(promotion.getType());
            switch (promotion.getType()) {
                case DISCOUNT:
                    BigDecimal beforeDiscount = BigDecimal.valueOf(quantity).multiply(goods.getPrice())
                            .setScale(SCALE, RoundingMode.HALF_UP);
                    BigDecimal afterDiscount = beforeDiscount.multiply(BigDecimal.valueOf(promotion.getDiscount()))
                            .setScale(SCALE, RoundingMode.HALF_UP);
                    BigDecimal discountAmount = beforeDiscount.subtract(afterDiscount)
                            .setScale(SCALE, RoundingMode.HALF_UP);
                    orderGoods.setDiscountAmount(discountAmount);
                    amount = afterDiscount;
                    break;
                case REWARD:
                    //计算当前优惠下满赠
                    int promotionPurchase = promotion.getPurchase();
                    int promotionReward = promotion.getReward();
                    BigDecimal[] remainders = BigDecimal.valueOf(quantity).divideAndRemainder(
                            BigDecimal.valueOf(promotionPurchase + promotionReward));
                    int quotient = remainders[0].intValue();
                    int remainder = remainders[1].intValue();
                    int buyNum;
                    int rewardNum;
                    if (remainder > promotionPurchase) {
                        buyNum = (quotient + 1) * promotionPurchase;
                        rewardNum = quotient * promotionReward + remainder - promotionPurchase;
                    } else {
                        buyNum = quotient * promotionPurchase + remainder;
                        rewardNum = quotient * promotionReward;
                    }
                    //用户实际支付小计金额
                    amount = orderGoods.getPrice().multiply(BigDecimal.valueOf(buyNum))
                            .setScale(SCALE, RoundingMode.HALF_UP);
                    BigDecimal rewardDiscount = orderGoods.getPrice().multiply(BigDecimal.valueOf(rewardNum))
                            .setScale(SCALE, RoundingMode.HALF_UP);
                    orderGoods.setAmount(amount);
                    orderGoods.setReward(rewardNum);
                    orderGoods.setDiscountAmount(rewardDiscount);
                    break;
                default:
                    break;
            }
        }
        orderGoods.setAmount(amount);
        return orderGoods;
    }

    /**
     * print receipt
     *
     * @param printer print data
     */
    private void print(Printer printer) {
        printer.print();
    }

    /**
     * 测试随机产生的模拟数据是否出错
     *
     * @param loop 循环模拟次数
     */
    public static void testMockData(MockData mockData, int loop) {
        int i = 0;
        while (i < loop) {
            double randomDiscount = (int) ((Math.random() * (1 - 0.5) + 0.5) * 100.0) / 100.0;
            int randomPurchase = 1 + new Random().nextInt(5);
            int randomReward = 1 + new Random().nextInt(3);
            mockData.mockPromotion(randomDiscount, randomPurchase, randomReward);
            int randomGoodsNum = 1 + new Random().nextInt(10);
            int randomWeightNum = 1 + new Random().nextInt(5);
            int normalMinimum = 1 + new Random().nextInt(3);
            mockData.mockBarcodes(randomGoodsNum, normalMinimum, randomWeightNum);
            i++;
        }
    }

}
