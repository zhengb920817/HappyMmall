package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVO;

/**
 * Created by zhengb on 2018-02-03.
 */
public interface IProdcutService {
    /**
     * 保存商品
     * @param product
     * @return
     */
    ServerResponse<String> saveOrUpdateProduct(Product product);

    /**
     * 设置商品后台状态 上架或下架
     * @param productId
     * @param status
     * @return
     */
    ServerResponse<String> setStatus(Integer productId,Integer status);

    /**
     * 根据商品id获取商品详情
     * @param productId 商品id
     * @return
     */
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    /**
     * 获取商品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo<ProductListVO>> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo<ProductListVO>> searchProduct(Integer productId, String productName,
                                                          int pageNum, int pageSize);

    /**
     * 前台获取商品列表
     * @param productId 商品id
     * @return
     */
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    /**
     * 前台搜索商品
     * @param keyword
     * @param categroryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    ServerResponse<PageInfo<ProductListVO>> getProductByKeyWordCategory(String keyword,
                                                                        Integer categroryId,
                                                                        int pageNum, int pageSize,
                                                                        String orderBy);
}
