package com.itfelix.liuhaizhuaichat.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AceResult 统一响应结果类单元测试
 * @author aceFelix
 */
@DisplayName("AceResult 单元测试")
class AceResultTest {

    @Nested
    @DisplayName("success - 成功响应")
    class SuccessTests {

        @Test
        @DisplayName("success(data) 应返回 code=200, message=success, 携带数据")
        void shouldReturnSuccessWithData() {
            AceResult<String> result = AceResult.success("hello");

            assertEquals(200, result.getCode());
            assertEquals("success", result.getMessage());
            assertEquals("hello", result.getData());
        }

        @Test
        @DisplayName("success() 应返回 code=200, message=success, data=null")
        void shouldReturnSuccessWithoutData() {
            AceResult<Object> result = AceResult.success();

            assertEquals(200, result.getCode());
            assertEquals("success", result.getMessage());
            assertNull(result.getData());
        }

        @Test
        @DisplayName("应支持复杂对象作为 data")
        void shouldSupportComplexDataObject() {
            Map<String, Object> data = new HashMap<>();
            data.put("id", 1);
            data.put("name", "test");

            AceResult<Map<String, Object>> result = AceResult.success(data);

            assertEquals(200, result.getCode());
            assertEquals("success", result.getMessage());
            assertEquals(1, result.getData().get("id"));
            assertEquals("test", result.getData().get("name"));
        }

        @Test
        @DisplayName("应支持 List 作为 data")
        void shouldSupportListAsData() {
            List<String> list = Arrays.asList("a", "b", "c");
            AceResult<List<String>> result = AceResult.success(list);

            assertEquals(3, result.getData().size());
            assertTrue(result.getData().contains("b"));
        }

        @Test
        @DisplayName("应支持 null 作为 data")
        void shouldSupportNullData() {
            AceResult<String> result = AceResult.success(null);

            assertEquals(200, result.getCode());
            assertEquals("success", result.getMessage());
            assertNull(result.getData());
        }
    }

    @Nested
    @DisplayName("error - 失败响应")
    class ErrorTests {

        @Test
        @DisplayName("error(message) 应返回 code=500 和自定义消息")
        void shouldReturnErrorWithMessage() {
            AceResult<Object> result = AceResult.error("参数错误");

            assertEquals(500, result.getCode());
            assertEquals("参数错误", result.getMessage());
            assertNull(result.getData());
        }

        @Test
        @DisplayName("error() 应返回 code=500, message=error")
        void shouldReturnDefaultError() {
            AceResult<Object> result = AceResult.error();

            assertEquals(500, result.getCode());
            assertEquals("error", result.getMessage());
            assertNull(result.getData());
        }

        @Test
        @DisplayName("error(data) 应返回 code=500, message=error, 携带 data")
        void shouldReturnErrorWithData() {
            // 使用 <Object> 泛型调用 error(T data) 而非 error(String message)
            AceResult<Object> result = AceResult.<Object>error(42);

            assertEquals(500, result.getCode());
            assertEquals("error", result.getMessage());
            assertEquals(42, result.getData());
        }

        @Test
        @DisplayName("error(code, message) 应返回自定义状态码和消息")
        void shouldReturnCustomCodeAndMessage() {
            AceResult<Object> result = AceResult.error(404, "用户不存在");

            assertEquals(404, result.getCode());
            assertEquals("用户不存在", result.getMessage());
            assertNull(result.getData());
        }

        @Test
        @DisplayName("error(code, message, data) 应返回自定义状态码、消息和数据")
        void shouldReturnCustomCodeMessageAndData() {
            Map<String, String> errors = new HashMap<>();
            errors.put("username", "用户名已存在");
            errors.put("email", "邮箱格式不正确");

            AceResult<Map<String, String>> result = AceResult.error(422, "验证失败", errors);

            assertEquals(422, result.getCode());
            assertEquals("验证失败", result.getMessage());
            assertEquals(2, result.getData().size());
            assertEquals("用户名已存在", result.getData().get("username"));
        }

        @Test
        @DisplayName("error 方法泛型应正确传递")
        void shouldMaintainGenericType() {
            AceResult<Integer> result = AceResult.error(400, "数值超出范围", -1);

            assertEquals(400, result.getCode());
            assertEquals("数值超出范围", result.getMessage());
            assertEquals(-1, result.getData());
        }
    }

    @Nested
    @DisplayName("构造函数与 Lombok 行为")
    class ConstructorTests {

        @Test
        @DisplayName("无参构造 + setter 应正常工作")
        void shouldWorkWithNoArgsConstructorAndSetters() {
            AceResult<String> result = new AceResult<>();
            result.setCode(200);
            result.setMessage("OK");
            result.setData("test");

            assertEquals(200, result.getCode());
            assertEquals("OK", result.getMessage());
            assertEquals("test", result.getData());
        }

        @Test
        @DisplayName("全参构造应正常工作")
        void shouldWorkWithAllArgsConstructor() {
            AceResult<Integer> result = new AceResult<>(201, "创建成功", 42);

            assertEquals(201, result.getCode());
            assertEquals("创建成功", result.getMessage());
            assertEquals(42, result.getData());
        }

        @Test
        @DisplayName("equals 应比较所有字段")
        void shouldImplementEqualsCorrectly() {
            AceResult<String> r1 = AceResult.success("data");
            AceResult<String> r2 = AceResult.success("data");
            AceResult<String> r3 = AceResult.success("different");

            assertEquals(r1, r2);
            assertNotEquals(r1, r3);
        }

        @Test
        @DisplayName("toString 应包含所有字段")
        void shouldIncludeAllFieldsInToString() {
            AceResult<String> result = AceResult.error(403, "无权限");

            String str = result.toString();
            assertTrue(str.contains("403"));
            assertTrue(str.contains("无权限"));
        }
    }
}
