package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProdcutService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by zhengb on 2018-02-03.
 */
@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProdcutService iProdcutService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession httpSession, Product product) {


        User curLoginUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (curLoginUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录");
        }

        //非管理员
        if (!iUserService.checkIsAdmin(curLoginUser).isSuccess()) {
            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
        } else {
            //添加产品逻辑
            return iProdcutService.saveOrUpdateProduct(product);
        }
    }

    @RequestMapping(value = "set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession httpSession,
                                        @RequestParam("productId") Integer productId,
                                        @RequestParam("status") Integer status) {
        User curLoginUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (curLoginUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录");
        }

        //非管理员
        if (!iUserService.checkIsAdmin(curLoginUser).isSuccess()) {
            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
        } else {
            /*
            更新商品状态
             */
            return iProdcutService.setStatus(productId, status);
        }
    }

    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession httpSession,
                                    @RequestParam("productId") Integer productId) {
        User curLoginUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (curLoginUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录");
        }

        //非管理员
        if (!iUserService.checkIsAdmin(curLoginUser).isSuccess()) {
            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
        } else {
            return iProdcutService.manageProductDetail(productId);
        }
    }

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession httpSession,
                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User curLoginUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (curLoginUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录");
        }

        //非管理员
        if (!iUserService.checkIsAdmin(curLoginUser).isSuccess()) {
            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
        } else {
            return iProdcutService.getProductList(pageNum, pageSize);
        }
    }

    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession httpSession,
                                        @RequestParam("productName") String productName,
                                        @RequestParam("productId") Integer productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User curLoginUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (curLoginUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录");
        }

        //非管理员
        if (!iUserService.checkIsAdmin(curLoginUser).isSuccess()) {
            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
        } else {
            return iProdcutService.searchProduct(productId, productName, pageNum, pageSize);
        }
    }

    private String getImageTargetUrl(String fileName){
        return PropertiesUtils.getPropertyValue("ftp.server.http.prefix") + fileName;
    }

    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(@RequestParam(value = "upload_file",required = false) MultipartFile file,
                                 HttpSession httpSession,
                                 HttpServletRequest request) {

        User curLoginUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        //非管理员
        if (!iUserService.checkIsAdmin(curLoginUser).isSuccess()) {
            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
        }

        if (file.getSize() <= 0) {
            return ServerResponse.createByErrorMessage("文件大小为0，无效文件");
        }

        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.uploadToFtpServer(file, path);
        String url = getImageTargetUrl(targetFileName);
        Map<String, String> fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);
        return ServerResponse.createBySuccess(fileMap);
    }

    @RequestMapping(value = "richtext_img_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> richTextImageUpload(@RequestParam(value = "upload_file",required = false) MultipartFile file,
                                                   HttpSession httpSession,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
        Map<String, Object> resultMap = Maps.newHashMap();
        User curLoginUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        //非管理员
        if (!iUserService.checkIsAdmin(curLoginUser).isSuccess()) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登录管理员操作");
            return resultMap;
        }

        if (file.getSize() <= 0) {
            resultMap.put("success", false);
            resultMap.put("msg", "文件大小为0，请重新选择文件");
            return resultMap;
        }

        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.uploadToFtpServer(file, path);
        if (StringUtils.isBlank(targetFileName)) {
            resultMap.put("success", false);
            resultMap.put("msg", "上传失败");
            return resultMap;
        }
        String url = getImageTargetUrl(targetFileName);
        resultMap.put("success", true);
        resultMap.put("msg", "上传成功");
        resultMap.put("file_path", url);
        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
        return resultMap;
    }

}
