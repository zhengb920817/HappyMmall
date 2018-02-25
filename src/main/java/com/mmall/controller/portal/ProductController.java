package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProdcutService;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zhengb on 2018-02-06.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProdcutService iProdcutService;

    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(Integer productId){
        return iProdcutService.getProductDetail(productId);
    }

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo<ProductListVO>> getProductList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
            @RequestParam(value = "orderBy") String orderBy) {


        return iProdcutService.getProductByKeyWordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }
}
