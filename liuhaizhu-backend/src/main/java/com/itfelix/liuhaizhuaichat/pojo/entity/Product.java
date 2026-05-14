package com.itfelix.liuhaizhuaichat.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itfelix.liuhaizhuaichat.mcp.enums.ProductStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 商品实体类
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("product")
public class Product {
    @TableId
    // 商品ID
    private String productId;
    // 商品名称
    private String productName;
    // 商品品牌
    private String brand;
    // 商品描述
    private String description;

    // 商品价格
    private Integer price;
    // 商品库存
    private Integer stock;
    // 商品状态
    private ProductStatusEnum status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
