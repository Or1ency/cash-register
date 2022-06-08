# cash-register

## environment

JDK8+

## data

Mock data in class MockData

## config

**promotion**

see `mockData.mockPromotion`

there are three parameters

     * @param discount 折扣值 若为null不传递则随机产生一个数值
     * @param purchase 购买数 若为null不传递则随机产生一个数值
     * @param reward   赠送数 若为null不传递则随机产生一个数值

**current buy goods**

see `mockData.mockBarcodes`

there are three parameters

     * @param normalNum     普通商品总数量
     * @param normalMinimum 普通商品单个数量 用于模拟数据测试满赠
     * @param weightNum     称重商品数量

### test case 

see `Cashier.testCase`

it's design exactly as the homework.pdf

## run/debug

#### step 1. 
new Mock to initialize basic data such as goods info
#### step 2. 
new Cashier
#### step 3. 
set data into Cashier
#### step 4.
random or appoint parameters for mock promotion or barcode (see config)
#### step 5.
run or debug Cashier.main()