package com.itfelix.liuhaizhuaichat.mcp.tool;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itfelix.liuhaizhuaichat.mapper.ProductMapper;
import com.itfelix.liuhaizhuaichat.mcp.bean.AddProductRequest;
import com.itfelix.liuhaizhuaichat.mcp.bean.ModifyProductRequest;
import com.itfelix.liuhaizhuaichat.mcp.bean.QueryProductRequest;
import com.itfelix.liuhaizhuaichat.mcp.enums.ListSortEnum;
import com.itfelix.liuhaizhuaichat.mcp.enums.PriceCompareEnum;
import com.itfelix.liuhaizhuaichat.mcp.enums.ProductStatusEnum;
import com.itfelix.liuhaizhuaichat.pojo.entity.Product;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author aceFelix
 */
@Component
@Slf4j
public class ProductTool {
    @Resource
    private ProductMapper productMapper;
    @Tool(description = "新增商品")
    public String addProduct(AddProductRequest productRequest) {
        log.info("===================新增商品addProduct===================");
        log.info("商品参数：{}", productRequest.toString());

        Product product = new Product();
        BeanUtils.copyProperties(productRequest, product);
        product.setProductId(RandomUtil.randomNumbers(10));
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insert(product);
        return "商品添加成功";
    }

    @Transactional
    @Tool(description = "删除商品")
    public String deleteProduct(String productId) {
        log.info("===================删除商品deleteProduct===================");
        log.info("商品ID：{}", productId);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        productMapper.delete(queryWrapper);
        return "商品删除成功";
    }

    @Transactional
    @Tool(description = "根据条件查询商品信息")
    public List<Product> queryProductListByCOndition(QueryProductRequest queryProductRequest) {
        log.info("===================根据条件查询商品信息queryProductListByCOndition===================");
        log.info("查询参数：{}", queryProductRequest.toString());
        String productId = queryProductRequest.getProductId();
        String productName = queryProductRequest.getProductName();
        String brand = queryProductRequest.getBrand();
        Integer price = queryProductRequest.getPrice();
        ProductStatusEnum status = queryProductRequest.getStatus();
        ListSortEnum sort = queryProductRequest.getSort();
        PriceCompareEnum priceCompare = queryProductRequest.getPriceCompare();
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(productId)){
            queryWrapper.eq("product_id", productId);
        }
        if (StringUtils.isNotBlank(productName)){
            queryWrapper.like("product_name", productName);
        }
        if (StringUtils.isNotBlank(brand)){
            queryWrapper.like("brand", brand);
        }
        if (price != null && priceCompare != null){
            if (Objects.equals(priceCompare.type, PriceCompareEnum.LOWER.type)){
                queryWrapper.le("price", price);
            }else if (Objects.equals(priceCompare.type, PriceCompareEnum.EQUAL.type)){
                queryWrapper.eq("price", price);
            }else if (Objects.equals(priceCompare.type, PriceCompareEnum.HIGHER.type)){
                queryWrapper.ge("price", price);
            }
        }
        if (status != null){
            queryWrapper.eq("status", status.type);
        }
        if (sort != null && sort.type.equals(ListSortEnum.ASC.type)){
            queryWrapper.orderByAsc("price");
        }
        if (sort != null && sort.type.equals(ListSortEnum.DESC.type)){
            queryWrapper.orderByDesc("price");
        }
        return productMapper.selectList(queryWrapper);
    }
    @Transactional
    @Tool(description = "根据商品编号更新商品信息")
    public String modifyProduct(ModifyProductRequest productRequest) {
        log.info("===================修改商品modifyProduct===================");
        log.info("修改商品参数：{}", productRequest.toString());
        Product product = new Product();
        BeanUtils.copyProperties(productRequest, product);
        product.setUpdateTime(LocalDateTime.now());
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productRequest.getProductId());
        int update = productMapper.update(product, queryWrapper);
        if (update <= 0){
            return "商品信息更新失败";
        }
        return "商品信息更新成功";
    }

    @Transactional
    @Tool(description = "把排序转换成对应的枚举")
    public ListSortEnum convertSort(String sort) {
        log.info("===================把排序转换成对应的枚举convertSort===================");
        log.info("排序参数：{}", sort);
        for (ListSortEnum sortEnum : ListSortEnum.values()) {
            if (sortEnum.value.equals(sort)) {
                return sortEnum;
            }
        }
        throw new IllegalArgumentException("无效的排序: " + sort);
        /*if (sort.equalsIgnoreCase(ListSortEnum.ASC.value)){
            return ListSortEnum.ASC;
        }else {
            return ListSortEnum.DESC;
        }*/
        // return null;
    }

    @Transactional
    @Tool(description = "把商品价格的比较换成对应的枚举")
    public PriceCompareEnum convertPriceCompare(String priceCompare) {
        log.info("===================把商品价格的比较换成对应的枚举convertPriceCompare===================");
        log.info("价格比较参数：{}", priceCompare);
        for (PriceCompareEnum priceCompareEnum : PriceCompareEnum.values()) {
            if (priceCompareEnum.value.equals(priceCompare)) {
                return priceCompareEnum;
            }
        }
        throw new IllegalArgumentException("无效的价格比较: " + priceCompare);
    }

    @Transactional
    @Tool(description = "把商品状态转换成对应的枚举类型")
    public ProductStatusEnum convertStatus(int status) {
        log.info("===================把商品状态转换成对应的枚举类型convertStatus===================");
        log.info("商品状态参数：{}", status);
        for (ProductStatusEnum statusEnum : ProductStatusEnum.values()) {
            if (statusEnum.type == status) {
                return statusEnum;
            }
        }
        throw new IllegalArgumentException("无效的商品状态: " + status);
    }
}
