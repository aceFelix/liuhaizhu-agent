package com.itfelix.liuhaizhuaichat.mcp.tool;

import com.itfelix.liuhaizhuaichat.mapper.ProductMapper;
import com.itfelix.liuhaizhuaichat.mcp.enums.ListSortEnum;
import com.itfelix.liuhaizhuaichat.mcp.enums.PriceCompareEnum;
import com.itfelix.liuhaizhuaichat.mcp.enums.ProductStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ProductTool 单元测试
 * @author aceFelix
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductTool 单元测试")
class ProductToolTest {

    @Mock
    private ProductMapper productMapper;

    private ProductTool productTool;

    @BeforeEach
    void setUp() {
        productTool = new ProductTool();
        ReflectionTestUtils.setField(productTool, "productMapper", productMapper);
    }

    @Nested
    @DisplayName("枚举转换方法 — 纯逻辑")
    class EnumConversionTests {

        @Test
        @DisplayName("convertSort('升序') 应返回 ASC")
        void shouldConvertSortAsc() {
            assertEquals(ListSortEnum.ASC, productTool.convertSort("升序"));
        }

        @Test
        @DisplayName("convertSort('降序') 应返回 DESC")
        void shouldConvertSortDesc() {
            assertEquals(ListSortEnum.DESC, productTool.convertSort("降序"));
        }

        @Test
        @DisplayName("convertSort 对无效值应抛出异常")
        void shouldThrowForInvalidSort() {
            assertThrows(IllegalArgumentException.class,
                    () -> productTool.convertSort("invalid"));
        }

        @Test
        @DisplayName("convertPriceCompare('价格低于') 应返回 LOWER")
        void shouldConvertPriceLower() {
            assertEquals(PriceCompareEnum.LOWER, productTool.convertPriceCompare("价格低于"));
        }

        @Test
        @DisplayName("convertPriceCompare('价格等于') 应返回 EQUAL")
        void shouldConvertPriceEqual() {
            assertEquals(PriceCompareEnum.EQUAL, productTool.convertPriceCompare("价格等于"));
        }

        @Test
        @DisplayName("convertPriceCompare('价格高于') 应返回 HIGHER")
        void shouldConvertPriceHigher() {
            assertEquals(PriceCompareEnum.HIGHER, productTool.convertPriceCompare("价格高于"));
        }

        @Test
        @DisplayName("convertPriceCompare 对无效值应抛出异常")
        void shouldThrowForInvalidPriceCompare() {
            assertThrows(IllegalArgumentException.class,
                    () -> productTool.convertPriceCompare(">="));
        }

        @Test
        @DisplayName("convertStatus(1) 应返回 ON_SALE")
        void shouldConvertStatusOnSale() {
            assertEquals(ProductStatusEnum.ON_SALE, productTool.convertStatus(1));
        }

        @Test
        @DisplayName("convertStatus(0) 应返回 OFF_SALE")
        void shouldConvertStatusOffSale() {
            assertEquals(ProductStatusEnum.OFF_SALE, productTool.convertStatus(0));
        }

        @Test
        @DisplayName("convertStatus(2) 应返回 PRE_SALE")
        void shouldConvertStatusPreSale() {
            assertEquals(ProductStatusEnum.PRE_SALE, productTool.convertStatus(2));
        }

        @Test
        @DisplayName("convertStatus 对无效值应抛出异常")
        void shouldThrowForInvalidStatus() {
            assertThrows(IllegalArgumentException.class,
                    () -> productTool.convertStatus(99));
        }
    }

    @Nested
    @DisplayName("addProduct - 新增商品")
    class AddProductTests {

        @Test
        @DisplayName("应成功添加商品")
        void shouldAddProduct() {
            String result = productTool.addProduct(new com.itfelix.liuhaizhuaichat.mcp.bean.AddProductRequest());

            assertEquals("商品添加成功", result);
            verify(productMapper).insert(Mockito.<com.itfelix.liuhaizhuaichat.pojo.entity.Product>any());
        }
    }

    @Nested
    @DisplayName("deleteProduct - 删除商品")
    class DeleteProductTests {

        @Test
        @DisplayName("应成功删除商品")
        void shouldDeleteProduct() {
            when(productMapper.delete(any())).thenReturn(1);

            String result = productTool.deleteProduct("1234567890");

            assertEquals("商品删除成功", result);
            verify(productMapper).delete(any());
        }
    }
}
