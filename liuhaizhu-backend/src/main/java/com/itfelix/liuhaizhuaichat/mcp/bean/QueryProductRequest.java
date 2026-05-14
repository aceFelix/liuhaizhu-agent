package com.itfelix.liuhaizhuaichat.mcp.bean;

import com.itfelix.liuhaizhuaichat.mcp.enums.ListSortEnum;
import com.itfelix.liuhaizhuaichat.mcp.enums.PriceCompareEnum;
import com.itfelix.liuhaizhuaichat.mcp.enums.ProductStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 查询商品
 * 商品请求参数
 * @author aceFelix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QueryProductRequest {
    @ToolParam(description = "商品编号",required = false)
    private String productId;
    @ToolParam(description = "商品名称",required = false)
    private String productName;
    @ToolParam(description = "商品品牌",required = false)
    private String brand;
    @ToolParam(description = "商品价格",required = false)
    private Integer price;
    @ToolParam(description = "商品状态",required = false)
    private ProductStatusEnum status;
    @ToolParam(description = "查询列表排序",required = false)
    private ListSortEnum sort;
    @ToolParam(description = "价格比较",required = false)
    private PriceCompareEnum priceCompare;
}
