package com.itfelix.liuhaizhuaichat.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserRoleEnum 枚举单元测试
 * @author aceFelix
 */
@DisplayName("UserRoleEnum 枚举单元测试")
class UserRoleEnumTest {

    @Nested
    @DisplayName("枚举值定义")
    class EnumValuesTests {

        @Test
        @DisplayName("应包含三个角色：ADMIN、VIP、USER")
        void shouldHaveThreeRoles() {
            assertEquals(3, UserRoleEnum.values().length);
        }

        @Test
        @DisplayName("ADMIN 的 code 应为 'ADMIN'，desc 应为 '管理员'")
        void adminShouldHaveCorrectCodeAndDesc() {
            assertEquals("ADMIN", UserRoleEnum.ADMIN.getCode());
            assertEquals("管理员", UserRoleEnum.ADMIN.getDesc());
        }

        @Test
        @DisplayName("VIP 的 code 应为 'VIP'，desc 应为 'VIP用户'")
        void vipShouldHaveCorrectCodeAndDesc() {
            assertEquals("VIP", UserRoleEnum.VIP.getCode());
            assertEquals("VIP用户", UserRoleEnum.VIP.getDesc());
        }

        @Test
        @DisplayName("USER 的 code 应为 'USER'，desc 应为 '普通用户'")
        void userShouldHaveCorrectCodeAndDesc() {
            assertEquals("USER", UserRoleEnum.USER.getCode());
            assertEquals("普通用户", UserRoleEnum.USER.getDesc());
        }
    }

    @Nested
    @DisplayName("valueOf 转换")
    class ValueOfTests {

        @Test
        @DisplayName("valueOf('ADMIN') 应返回 ADMIN")
        void shouldConvertAdminStringToEnum() {
            assertEquals(UserRoleEnum.ADMIN, UserRoleEnum.valueOf("ADMIN"));
        }

        @Test
        @DisplayName("valueOf('VIP') 应返回 VIP")
        void shouldConvertVipStringToEnum() {
            assertEquals(UserRoleEnum.VIP, UserRoleEnum.valueOf("VIP"));
        }

        @Test
        @DisplayName("valueOf('USER') 应返回 USER")
        void shouldConvertUserStringToEnum() {
            assertEquals(UserRoleEnum.USER, UserRoleEnum.valueOf("USER"));
        }

        @Test
        @DisplayName("valueOf 对无效值应抛出 IllegalArgumentException")
        void shouldThrowForInvalidValue() {
            assertThrows(IllegalArgumentException.class, () ->
                    UserRoleEnum.valueOf("INVALID"));
        }

        @Test
        @DisplayName("valueOf 对 null 应抛出异常")
        void shouldThrowForNull() {
            assertThrows(NullPointerException.class, () ->
                    UserRoleEnum.valueOf(null));
        }
    }

    @Nested
    @DisplayName("枚举唯一性")
    class UniquenessTests {

        @Test
        @DisplayName("所有枚举项的 code 应唯一")
        void eachCodeShouldBeUnique() {
            long uniqueCodes = java.util.Arrays.stream(UserRoleEnum.values())
                    .map(UserRoleEnum::getCode)
                    .distinct()
                    .count();
            assertEquals(UserRoleEnum.values().length, uniqueCodes);
        }

        @Test
        @DisplayName("所有枚举项的 desc 应唯一")
        void eachDescShouldBeUnique() {
            long uniqueDescs = java.util.Arrays.stream(UserRoleEnum.values())
                    .map(UserRoleEnum::getDesc)
                    .distinct()
                    .count();
            assertEquals(UserRoleEnum.values().length, uniqueDescs);
        }
    }
}
