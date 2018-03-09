package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by zhengb on 2018-02-08.
 */

@Service("iShippingService")
@Slf4j
public class ShippingServiceImp implements IShippingService{

    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 增加地址
     * @param userId
     * @param shipping
     * @return
     */
    @Transactional
    @Override
    public ServerResponse<Map<String, Integer>> add(Integer userId, Shipping shipping) {
        Shipping addShipping = shipping;
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(addShipping);
        if (rowCount > 0) {
            Map<String, Integer> resultMap = Maps.newHashMap();
            resultMap.put("shippingId", addShipping.getId());
            return ServerResponse.createBySuccess("新建地址成功", resultMap);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    @Transactional
    @Override
    public ServerResponse<String> delete(Integer userId, Integer shippingId) {
        if (shippingId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        int resultCount = shippingMapper.deleteByIdAndUserId(userId, shippingId);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    @Transactional
    @Override
    public ServerResponse<String> update(Integer userId, Shipping shipping) {
        if (shipping == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        Shipping updateShipping = shipping;
        updateShipping.setUserId(userId);

        int resultCount = shippingMapper.updateByShipping(shipping);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");

    }

    @Transactional
    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        if(shippingId == null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        Shipping resultShipping = shippingMapper.selectByUserIdAndShippingId(userId,shippingId);
        return ServerResponse.createBySuccess(resultShipping);
    }

    @Transactional
    @Override
    public ServerResponse<PageInfo<Shipping>> getList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectListByUserId(userId);
        PageInfo<Shipping> pageResult = new PageInfo<>(shippingList);
        //pageResult.setList(shippingList);
        return ServerResponse.createBySuccess(pageResult);
    }
}
