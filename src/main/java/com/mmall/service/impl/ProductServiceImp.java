package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProdcutService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtils;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhengb on 2018-02-03.
 */
@Service("iProductService'")
public class ProductServiceImp implements IProdcutService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Transactional
    public ServerResponse saveOrUpdateProduct(Product product){
        if(product != null){
            String subImages = product.getSubImages();
            if(!StringUtils.isBlank(subImages)){
                String[] subImageArray = subImages.split(",");
                if (subImageArray.length > 0){
                    String mainImage = subImageArray[0];
                    product.setMainImage(mainImage);
                }
            }

            Integer productId = product.getId();
            int resultConut = 0;
            if(productId != null){
                resultConut = productMapper.updateByPrimaryKey(product);
                if(resultConut > 0)
                    return ServerResponse.createBySuccessMessage("更新商品成功");
            }
            else{
                resultConut = productMapper.insert(product);
                if(resultConut > 0)
                    return ServerResponse.createBySuccessMessage("插入商品信息成功");
            }
        }

        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    @Transactional
    public ServerResponse<String> setStatus(Integer productId,Integer status){
        if(productId == null || status == null){
           return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int resultCount = productMapper.updateByPrimaryKeySelective(product);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("修改产品状态成功");
        }

        return  ServerResponse.createByErrorMessage("更新产品状态失败");
    }

    @Transactional
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }

        ProductDetailVo productDetailVo = assembleProductDetailVO(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVO(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        //imagehost
        productDetailVo.setImageHost(PropertiesUtils.getPropertyValue(
                "ftp.server.http.prefix"));
        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getId());
        if (category == null) {
            productDetailVo.setParentCategoryId(0);
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        return productDetailVo;
    }

    @Transactional
    public ServerResponse<PageInfo<ProductListVO>> getProductList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectProductList();

        PageInfo<ProductListVO> pageResult = getPageResult(productList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVO = new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setName(product.getName());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setSubtitle(product.getSubtitle());
        productListVO.setImageHost(PropertiesUtils.getPropertyValue("ftp.server.http.prefix"));

        return productListVO;
    }

    @Transactional
    public ServerResponse<PageInfo<ProductListVO>> searchProduct(Integer productId, String productName,
                                                                 int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        String searcheProductName = "";
        if (StringUtils.isNotBlank(productName)) {
            searcheProductName =
                    new StringBuilder().append('%').append(productName).append('%').toString();
        }

        List<Product> productList = productMapper.selectByNameAndProductId(searcheProductName, productId);
        PageInfo pageResult = getPageResult(productList);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 转换产品对象为分页信息
     * @param productList
     * @return
     */
    private PageInfo<ProductListVO> getPageResult(List<Product> productList) {
        List<ProductListVO> productListVOList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVO productListVO = assembleProductListVO(productItem);
            productListVOList.add(productListVO);
        }

        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVOList);
        return pageResult;
    }

    @Transactional
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        /**
         * 判断产品是否已下架 已下架产品不返回
         */
        Product product = productMapper.selectByPrimaryKey(productId);
        if((product == null) || (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode())){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }

        ProductDetailVo productDetailVo = assembleProductDetailVO(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }


    @Transactional
    public ServerResponse<PageInfo<ProductListVO>> getProductByKeyWordCategory(String keyword,
                                                                                 Integer categroryId,
                                                                                 int pageNum, int pageSize,
                                                                                 String orderBy) {

        if(StringUtils.isBlank(keyword) && categroryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        List<Integer> categoryIdList = Lists.newArrayList();

        if(categroryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categroryId);
            if(category == null && StringUtils.isBlank(keyword)){
                //没有该分类 而且没有关键字，返回空的结果集，不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductDetailVo> productDetailVoList = Lists.newArrayList();
                PageInfo<ProductListVO> pageInfo = new PageInfo(productDetailVoList);
                return ServerResponse.createBySuccess(pageInfo);

            }

            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categroryId).getData();
        }

        String searcheKeyword = null;
        if(StringUtils.isNotBlank(keyword)){
            searcheKeyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum,pageSize);

        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.RRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                //排序
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }


        List<Product> productList = productMapper.selectByNameAndCategoryIds(
                StringUtils.isBlank(searcheKeyword) ? null: searcheKeyword,
                categoryIdList.size() == 0 ? null : categoryIdList);

        PageInfo<ProductListVO> pageResult = getPageResult(productList);
        return ServerResponse.createBySuccess(pageResult);
    }
}
