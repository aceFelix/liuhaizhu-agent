package com.itfelix.liuhaizhuaichat.mcp.bean;

import com.itfelix.liuhaizhuaichat.mcp.enums.ProductStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 修改商品
 * 商品请求参数
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ModifyProductRequest {
    @ToolParam(description = "商品编号",required = false)
    private String productId;
    @ToolParam(description = "商品名称",required = false)
    private String productName;
    @ToolParam(description = "商品品牌",required = false)
    private String brand;
    @ToolParam(description = "商品描述",required = false)
    private String description;
    @ToolParam(description = "商品价格",required = false)
    private Integer price;
    @ToolParam(description = "商品库存",required = false)
    private Integer stock;
    @ToolParam(description = "商品状态，ON_SALE为上架，OFF_SALE为下架，PRE_SALE为预售",required = false)
    private ProductStatusEnum status;
}
