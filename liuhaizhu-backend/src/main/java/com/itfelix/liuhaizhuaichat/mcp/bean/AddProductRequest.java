package com.itfelix.liuhaizhuaichat.mcp.bean;

import com.itfelix.liuhaizhuaichat.mcp.enums.ProductStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 新增商品
 * 商品请求参数
 * @author aceFelix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddProductRequest{
    @ToolParam(description = "商品名称")
    private String productName;
    @ToolParam(description = "商品品牌")
    private String brand;
    @ToolParam(description = "商品描述",required = false)
    private String description;
    @ToolParam(description = "商品价格")
    private Integer price;
    @ToolParam(description = "商品库存")
    private Integer stock;
    @ToolParam(description = "商品状态，1为上架，0为下架，2为预售")
    private ProductStatusEnum status;
}
