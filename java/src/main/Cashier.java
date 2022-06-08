package main;

import domain.*;
import enums.PromotionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Oriency on 2022/6/7.
 */
public class Cashier {

    public static void main(String[] args) {
        //Cashier initial
        Cashier cashier = new Cashier();
        //优惠折扣
        double mockDiscount = (int) ((Math.random() * (1 - 0.5) + 0.5) * 100.0) / 100.0;
        //买二增一
        int mockPurchase = 2;
        int mockReward = 1;
        List<Promotion> mockPromotions = mockPromotion(mockDiscount, mockPurchase, mockReward);
        //设置收银机优惠活动
        cashier.setPromotions(mockPromotions);
        //模拟真实购买场景，购买一定数量的商品
        List<String> mockBarcodes = mockBarcodes(3, 5, 2);
        //模拟购买
        cashier.purchase(mockBarcodes);
    }

    private static final Map<String, Goods> GOODS_MAP = new HashMap<>();
    private static final List<String> MOCK_GOODS = Arrays.asList("饮料:可口可乐:瓶:3.00", "体育用品:羽毛球:个:1.00",
            "饮料:雪碧:瓶:3.00", "体育用品:乒乓球:个:0.50", "体育用品:篮球:个:32.00", "体育用品:足球:个:68.00",
            "家具:沙发:件:1325.00", "家具:椅子:件:999.00", "家具:床:件:4500.00", "家具:柜子:件:2233.00", "体育用品:橄榄球:个:98.00",
            "体育用品:高尔夫球:个:88.00", "饮料:脉动:瓶:5.00", "饮料:营养快线:瓶:4.50", "饮料:芬达:瓶:3.50", "饮料:加多宝:瓶:5.50",
            "水果:苹果:斤:5.50", "水果:香蕉:斤:7.50", "水果:火龙果:斤:7.68", "水果:樱桃:斤:23.55", "水果:荔枝:斤:12.55", "水果:桃:斤:9.88");

    private static final int SCALE = 2;
    private List<Promotion> promotions;
    private static List<String> NORMAL_BARCODES;
    private static List<String> WEIGHT_BARCODES;

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    /*
     * 商品数据初始化
     */
    static {
        Goods goods;
        System.out.println("系统商品:");
        List<String> normalBarcodes = new ArrayList<>();
        List<String> weightBarcodes = new ArrayList<>();
        for (int i = 0; i < MOCK_GOODS.size(); i++) {
            String[] item = MOCK_GOODS.get(i).split(":");
            goods = new Goods();
            goods.setId(i + 1);
            goods.setCategory(item[0]);
            goods.setName(item[1]);
            String unit = item[2];
            goods.setUnit(unit);
            goods.setPrice(new BigDecimal(item[3]));
            String barcode = "ITEM" + String.format("%06d", i + 1);
            goods.setBarcode(barcode);
            if (unit.equals("斤")) {
                goods.setWeight(true);
                weightBarcodes.add(barcode);
            } else {
                normalBarcodes.add(barcode);
            }
            System.out.println(goods.toString());
            GOODS_MAP.put(barcode, goods);
        }
        Cashier.NORMAL_BARCODES = normalBarcodes;
        Cashier.WEIGHT_BARCODES = weightBarcodes;
        System.out.println();
    }

    /**
     * mock barcodes
     * 模拟购买商品的条形码列表
     *
     * @param normalNum     普通商品总数量
     * @param normalMinimum 普通商品单个数量 用于模拟数据测试满赠
     * @param weightNum     称重商品数量
     * @return barcodes
     */
    public static List<String> mockBarcodes(int normalNum, int normalMinimum, int weightNum) {
        List<String> barcodes = new ArrayList<>();
        if (normalNum >= NORMAL_BARCODES.size()) {
            normalNum = 1;
        }
        if (weightNum >= WEIGHT_BARCODES.size()) {
            weightNum = 1;
        }
        for (int i = 0; i < normalNum && normalNum < NORMAL_BARCODES.size(); i++) {
            int random = (int) (Math.random() * NORMAL_BARCODES.size());
            String barcode = NORMAL_BARCODES.get(random);
            for (int j = 0; j < normalMinimum; j++) {
                barcodes.add(barcode);
            }
        }
        Set<String> weightBarCodesSet = new HashSet<>();
        for (int i = 0; i < weightNum && weightNum < WEIGHT_BARCODES.size(); i++) {
            int random = (int) (Math.random() * WEIGHT_BARCODES.size());
            String barcode = WEIGHT_BARCODES.get(random);
            weightBarCodesSet.add(barcode);
        }
        List<String> weightBarCodes = weightBarCodesSet.stream().map(
                barcode -> {
                    double randomWeight = 1 + (int) ((Math.random() * 5) * 100.0) / 100.0;
                    DecimalFormat df = new DecimalFormat("#.00");
                    return barcode + "-" + df.format(randomWeight);
                }
        ).collect(Collectors.toList());
        barcodes.addAll(weightBarCodes);
        System.out.println("当前购买商品：\n" + barcodes);
        System.out.println();
        return barcodes;
    }

    /**
     * 模拟设置折扣和满赠的优惠活动
     *
     * @param discount 折扣值 若为null不传递则随机产生一个数值
     * @param purchase 购买数 若为null不传递则随机产生一个数值
     * @param reward   赠送数 若为null不传递则随机产生一个数值
     * @return 活动列表
     */
    public static List<Promotion> mockPromotion(Double discount, Integer purchase, Integer reward) {
        List<Promotion> promotions = new ArrayList<>();
        Promotion promotion;
        System.out.println("当前优惠信息:");
        for (int i = 0; i < PromotionType.values().length; i++) {
            promotion = new Promotion();
            promotion.setId(i + 1);
            PromotionType type = PromotionType.values()[i];
            promotion.setType(type);
            Set<String> barcodes = new HashSet<>();
            if (type.equals(PromotionType.DISCOUNT)) {
                //the barcodes who can join the promotions
                int bound = 1 + new Random().nextInt(WEIGHT_BARCODES.size());
                for (int j = 0; j < bound; j++) {
                    int random = (int) (Math.random() * WEIGHT_BARCODES.size());
                    barcodes.add(WEIGHT_BARCODES.get(random));
                }
                promotion.setDiscount(null == discount ? (int) ((Math.random() * (1 - 0.5) + 0.5) * 100.0) / 100.0 : discount);
            } else {
                promotion.setPurchase(null == purchase ? 1 + new Random().nextInt(6) : purchase);
                promotion.setReward(null == reward ? 1 + new Random().nextInt(4) : reward);
                int bound = 1 + new Random().nextInt(NORMAL_BARCODES.size());
                for (int j = 0; j < bound; j++) {
                    int random = (int) (Math.random() * NORMAL_BARCODES.size());
                    barcodes.add(NORMAL_BARCODES.get(random));
                }
            }
            //the barcodes who can join the promotions
            promotion.setBarcode(barcodes);
            promotions.add(promotion);
            System.out.println(promotion.toString());
        }
        System.out.println();
        return promotions;
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
                        orderGoods -> (null != orderGoods.getPromotion() && orderGoods.getPromotion()
                                && orderGoods.getType().equals(PromotionType.REWARD)))
                .map(orderGoods -> {
                            RewardGoods rewardGood = new RewardGoods();
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
        return null;
    }

    /**
     * 计算商品价钱
     *
     * @param barcodes 商品二维码
     * @param quantity 商品数量
     * @return 商品结算信息
     */
    public OrderGoods calculate(String barcodes, double quantity) {
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
        Promotion promotion = goodsHasPromotion(barcode);
        BigDecimal amount = BigDecimal.valueOf(quantity).multiply(goods.getPrice())
                .setScale(SCALE, RoundingMode.HALF_UP);
        if (null != promotion) {
            orderGoods.setPromotion(true);
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
    public void print(Printer printer) {
        printer.print();
    }

}
