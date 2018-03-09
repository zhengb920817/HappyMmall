package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by zhengb on 2018-02-03.
 */

/**
 *
 */
@Getter
@Setter
public class ProductListVO {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private BigDecimal price;

    private Integer status;
    private String imageHost;
}
