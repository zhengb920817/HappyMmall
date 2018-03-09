package com.mmall.pojo;

import lombok.*;

import java.util.Date;

@Getter
@Setter
//添加无参构造器
@NoArgsConstructor
//添加全参构造器
@AllArgsConstructor
public class Cart {
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Integer checked;

    private Date createTime;

    private Date updateTime;

}