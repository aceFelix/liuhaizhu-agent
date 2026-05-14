package com.itfelix.liuhaizhuaichat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itfelix.liuhaizhuaichat.pojo.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;

/**
 * 聊天消息Mapper
 * @author aceFelix
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    /**
     * 根据用户ID删除所有聊天消息
     * @param userId 用户ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM chat_message WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") String userId);
}
