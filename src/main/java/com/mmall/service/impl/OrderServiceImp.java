package com.mmall.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.*;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.service.IPayService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FtpUtil;
import com.mmall.util.PropertiesUtils;
import com.mmall.vo.OrderItemVO;
import com.mmall.vo.OrderProductVO;
import com.mmall.vo.OrderVO;
import com.mmall.vo.ShippingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by zhengb on 2018-02-10.
 */
@Service("iOrderService")
@Slf4j
public class OrderServiceImp implements IOrderService{

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private IPayService iPayService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    /**
     * 支付接口
     */
    public ServerResponse<Map<String,String>> pay(Long orderNo, Integer userId, String savePath) {

        Map<String, String> resultMap = new HashMap<>();

        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单信息不存在");
        }

        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));

        AliPayInfo aliPayInfo = new AliPayInfo();
        aliPayInfo.setTradeNo(order.getOrderNo().toString());
        aliPayInfo.setSubject(new StringBuilder().append("happymmall扫码支付，订单号：").
                append(order.getOrderNo()).toString());
        aliPayInfo.setTotalAmout(order.getPayment());
        aliPayInfo.setUndiscountAmout(new BigDecimal("0"));
        aliPayInfo.setSellerId("");
        aliPayInfo.setBody(new StringBuilder().append("订单").append(order.getOrderNo()).append("购买商品共").
                append(order.getPayment()).append("元").toString());
        aliPayInfo.setOpratortId("test_oprater_id");
        aliPayInfo.setStoreId("test_store_id");
        aliPayInfo.setTimoeOutExpress("120m");

        List<OrderItem> orderItemList = orderItemMapper.getListByOrderNoAndUserId(orderNo, userId);
        aliPayInfo.setOrderItemList(orderItemList);

        String callBackUrl = PropertiesUtils.getPropertyValue("alipay.callback.url");

        aliPayInfo.setCallbackUrl(callBackUrl);

        //调用支付服务接口 生成二维码图片
        PayResult<PayPrecreateResponse> payResult = iPayService.trade_precreate(aliPayInfo);

        if (payResult.getTradeStatus() == PayResultEnum.SUCCESS) {
            File folder = new File(savePath);
            if (!folder.exists()) {
                folder.setWritable(true);
                folder.mkdirs();
            }

            //二维码保存路径
            String qrPath = String.format(savePath + "/qt-%s.png", payResult.getRespMsg().getTradeNo());
            //保存的文件名
            String qrFileName = String.format("qt-%s.png", payResult.getRespMsg().getTradeNo());
            ZxingUtils.getQRCodeImge(payResult.getRespMsg().getQrCode(), 256, qrPath);

            File targetFile = new File(savePath, qrFileName);

            //上传至ftp服务器
            FtpUtil.uploadFiles(Lists.newArrayList(targetFile));

            log.info("qrPath:" + qrPath);

            String qrUrl = PropertiesUtils.getPropertyValue("ftp.server.http.prefix") + targetFile.getName();

            resultMap.put("qrUrl", qrUrl);

            return ServerResponse.createBySuccess(resultMap);
        } else {
            return ServerResponse.createByErrorMessage(payResult.getTradeStatus().getDesc());
        }
    }

    /**
     * 验证支付宝回到的正确性
     * @param params
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public ServerResponse<String> alipayCallBack(Map<String, String> params){
        try {
            boolean alipaySiganaturCheck =
                    AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),
                            "utf-8", Configs.getSignType());
            if (!alipaySiganaturCheck) {
                return ServerResponse.createByErrorMessage("非法请求，验证不通过");
            }
        } catch (AlipayApiException e) {
            log.error("验证支付宝回调异常" + e);
        }

        //校验各种参数
        Long orderNo = Long.parseLong(params.get("out_trade_no"));

        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");

        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("非商城订单");
        }
        //不是未支付状态
        if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccessMessage("支付宝重复调用");
        }

        if(tradeStatus.equals(Const.AliPayCallBack.TRADE_STUAS_TRADE_SUCCESS)){
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            //更新支付宝时间
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformStatus(tradeStatus);
        payInfo.setPlatformNumber(tradeNo);

        payInfoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public ServerResponse<Boolean> queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess();
        }

        return ServerResponse.createByError();
    }

    @Transactional
    @Override
    public ServerResponse<OrderVO> createOrder(Integer userId, Integer shippingId) {
        List<Cart> cartList = cartMapper.selectCheckedCaryByUserId(userId);

        ServerResponse<List<OrderItem>> serverResponse = getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("创建订单失败");
        }

        List<OrderItem> orderItemList = serverResponse.getData();
        BigDecimal payment = getOrderTotalPrice(orderItemList);

        //生成订单
        Order order = assembleOrder(userId, shippingId, payment);

        if (order == null) {
            return ServerResponse.createByErrorMessage("生成订单失败");
        }

        if (CollectionUtils.isEmpty(orderItemList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        orderItemMapper.bacthInsert(orderItemList);
        //生成成功 减库存
        reduceProductStock(orderItemList);
        //清空下购物车
        cleanCart(cartList);

        //返回前端数据
        OrderVO returnVo = assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(returnVo);
    }

    /**
     * 减库存
     * @param orderItemList
     */
    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem item : orderItemList) {
            Integer productId = item.getProductId();
            Integer quantity = item.getQuantity();
            productMapper.reduceStock(productId, quantity);
        }
    }

    /**
     * 清空购物车
     * @param cartList
     */
    private void cleanCart(List<Cart> cartList){
        for(Cart cart:cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private OrderVO assembleOrderVo(Order order,List<OrderItem> orderItemList){

        OrderVO orderVO = new OrderVO();

        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setPayment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
        orderVO.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVO.setPostage(order.getPostage());
        orderVO.setStatus(order.getStatus());
        orderVO.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVO.setShippingId(order.getShippingId());

        ShippingVO shippingVO = this.assembleShippingVo(order.getShippingId());
        if (shippingVO != null) {
            orderVO.setReceiverName(shippingVO.getReceiverName());
            orderVO.setShippingVo(shippingVO);
        }

        orderVO.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVO.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVO.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVO.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVO.setImageHost(PropertiesUtils.getPropertyValue("ftp.server.http.prefix"));

        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for(OrderItem orderItem:orderItemList){
            OrderItemVO orderItemVO = assembleOrderItemVo(orderItem);
            orderItemVOList.add(orderItemVO);
        }

        orderVO.setOrderItemVoList(orderItemVOList);
        return orderVO;
    }

    private OrderItemVO assembleOrderItemVo(OrderItem orderItem){
        OrderItemVO orderItemVO = new OrderItemVO();
        orderItemVO.setOrderNo(orderItem.getOrderNo());
        orderItemVO.setProductId(orderItem.getProductId());
        orderItemVO.setProductName(orderItem.getProductName());
        orderItemVO.setProductImage(orderItem.getProductImage());
        orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVO.setQuantity(orderItem.getQuantity());
        orderItemVO.setTotalPrice(orderItem.getTotalPrice());
        orderItemVO.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));

        return orderItemVO;
    }

    private ShippingVO assembleShippingVo(Integer shippingId) {
        ShippingVO shippingVO = new ShippingVO();
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping == null) {
            return null;
        }
        shippingVO.setReceiverName(shipping.getReceiverName());
        shippingVO.setReceiverAddress(shipping.getReceiverAddress());
        shippingVO.setReceiverCity(shipping.getReceiverCity());
        shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVO.setReceiverProvince(shipping.getReceiverProvince());
        shippingVO.setReceiverZip(shipping.getReceiverZip());
        shippingVO.setReceiverMobile(shipping.getReceiverMobile());
        shippingVO.setReceiverPhone(shipping.getReceiverPhone());

        return shippingVO;
    }

    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        long orderNo = generateOrderNo();
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE.getCode());
        order.setPostage(0);
        order.setPayment(payment);

        order.setUserId(userId);
        order.setShippingId(shippingId);
        //发货时间
        //付款时间

        int rowCount = orderMapper.insert(order);
        if(rowCount > 0){
            return order;
        }
        return null;
    }

    private long generateOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal result = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            result = BigDecimalUtil.add(result.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }

        return result;
    }

    @Transactional
    public ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId, List<Cart> cartList) {

        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        List<OrderItem> orderItemList = new ArrayList<>();

        //遍历购物车列表
        for (Cart cartItem : cartList) {
            OrderItem orderItem = new OrderItem();
            //根据购物车中的产品id从产品表里取出具体产品信息
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "不是在售状态");
            }

            //校验库存
            if (cartItem.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "库存不足");
            }

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartItem.getQuantity()));
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProductImage(product.getMainImage());
            orderItemList.add(orderItem);
        }

        return ServerResponse.createBySuccess(orderItemList);
    }

    @Transactional
    @Override
    public ServerResponse<String> cancelOrder(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该用户次订单不存在");
        }

        if (!order.getStatus().equals(Const.OrderStatusEnum.NO_PAY.getCode())) {
            return ServerResponse.createByErrorMessage("已付款，无法取消订单");
        }

        int rowCount = orderMapper.updateOrderStatusByOrderNoAndUserId(order.getUserId(),
                order.getOrderNo(), Const.OrderStatusEnum.Cancel.getCode());
        if (rowCount > 0) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Transactional
    @Override
    public ServerResponse<OrderProductVO> getOrderCarProduct(Integer userId) {

        OrderProductVO orderProductVO = new OrderProductVO();

        List<Cart> checkedCartList = cartMapper.selectCheckedCaryByUserId(userId);
        ServerResponse<List<OrderItem>> serverResponse =
                this.getCartOrderItem(userId, checkedCartList);
        if (!serverResponse.isSuccess()) {
            return ServerResponse.createByError();
        }

        List<OrderItem> orderItemList = serverResponse.getData();

        List<OrderItemVO> orderItemVOList = new ArrayList<>();

        BigDecimal totalPrice = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVOList.add(assembleOrderItemVo(orderItem));
        }

        orderProductVO.setProductTotalPrice(totalPrice);
        orderProductVO.setOrderItemVoList(orderItemVOList);
        orderProductVO.setImageHost(PropertiesUtils.getPropertyValue("alipay.callback.url"));

        return ServerResponse.createBySuccess(orderProductVO);
    }

    @Transactional
    @Override
    public ServerResponse<OrderVO> getOderDetail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.getListByOrderNoAndUserId(orderNo, userId);
            OrderVO returnVo = assembleOrderVo(order, orderItemList);
            return ServerResponse.createBySuccess(returnVo);
        }

        return ServerResponse.createByErrorMessage("没有找到该订单信息");
    }

    @Transactional
    @Override
    public ServerResponse<PageInfo<OrderVO>> getOrderList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVO> orderVOList = assembleOrderVoList(orderList,userId);

        PageInfo<OrderVO> pageResult = new PageInfo<>();
        pageResult.setList(orderVOList);

        return ServerResponse.createBySuccess(pageResult);
    }

    private List<OrderVO> assembleOrderVoList(List<Order> orderList, Integer userId) {
        List<OrderVO> result = new ArrayList<>();
        for (Order order : orderList) {
            List<OrderItem> orderItemList = new ArrayList<>();
            if (userId == null) {
                orderItemList = orderItemMapper.getListByOrderNoAndUserId(order.getOrderNo(),null);
            } else {
                orderItemList = orderItemMapper.getListByOrderNoAndUserId(order.getOrderNo(),
                        userId);

            }
            OrderVO orderVO = assembleOrderVo(order,orderItemList);
            result.add(orderVO);
        }

        return result;
    }

    @Transactional
    @Override
    public ServerResponse<PageInfo<OrderVO>> manageList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> allOrderList = orderMapper.selectAllOrder();
        List<OrderVO> orderVOList = this.assembleOrderVoList(allOrderList, null);
        PageInfo<OrderVO> pageResult = new PageInfo<>();
        pageResult.setList(orderVOList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Transactional
    @Override
    public ServerResponse<OrderVO> manageDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.getListByOrderNo(order.getOrderNo());
            OrderVO returnVo = assembleOrderVo(order, orderItemList);
            return ServerResponse.createBySuccess(returnVo);
        }

        return ServerResponse.createByErrorMessage("订单不存在");
    }

    @Transactional
    @Override
    public ServerResponse<PageInfo<OrderVO>> manageSearch(Long orderNo,int pageSize,int pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.getListByOrderNo(order.getOrderNo());
            OrderVO returnVo = assembleOrderVo(order, orderItemList);

            PageInfo pageResult = new PageInfo(Lists.newArrayList(order));
            pageResult.setList(Lists.newArrayList(returnVo));
            return ServerResponse.createBySuccess(pageResult);
        }

        return ServerResponse.createByErrorMessage("订单不存在");
    }

    @Transactional
    @Override
    public ServerResponse<String> manageSendGoods(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            if (order.getStatus().equals(Const.OrderStatusEnum.PAID.getCode())) {
                Order updateOrder = new Order();
                updateOrder.setOrderNo(orderNo);
                updateOrder.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                updateOrder.setSendTime(new Date());
                int rowCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccessMessage("发货成功");
                }
            } else {
                return ServerResponse.createByErrorMessage("不是已支付状态，发货失败");
            }

        }
        return ServerResponse.createByErrorMessage("订单信息不存在，发货失败");
    }


    @Override
    public void closeOrder(int hour){
        Date closeDate = org.apache.commons.lang.time.DateUtils.addHours(new Date(),-hour);

        List<Order> closerOrderList = orderMapper.selectOrderStatusByCreateTime(Const.OrderStatusEnum.NO_PAY.getCode(),
                DateTimeUtil.dateToStr(closeDate));

        for(Order order:closerOrderList){
            List<OrderItem> orderItemList = orderItemMapper.getListByOrderNo(order.getOrderNo());
            for(OrderItem orderItem:orderItemList){
                int stock = productMapper.selectByPrimaryKey(orderItem.getProductId()).getStock();
                if(stock == 0){
                    continue;
                }
                Product product = new Product();
                product.setId(orderItem.getProductId());
                product.setStock(orderItem.getQuantity());
            }

            orderMapper.closeOrderByOrderId(order.getId());
            log.info("关闭订单OrderNo:{}",order.getOrderNo());
        }
    }
}
