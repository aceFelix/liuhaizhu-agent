package com.itfelix.liuhaizhuaichat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itfelix.liuhaizhuaichat.pojo.entity.Conversation;
import com.itfelix.liuhaizhuaichat.pojo.vo.ConversationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 会话Mapper
 * @author aceFelix
 */
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {

    /**
     * 查询用户的会话列表（包含消息数量和最后一条消息）
     * 优化：使用子查询一次性获取，避免N+1问题
     * @param userId 用户ID
     * @return 会话VO列表
     */
    List<ConversationVO> selectConversationVOList(@Param("userId") String userId);

    /**
     * 根据用户ID删除所有会话
     * @param userId 用户ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM conversation WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") String userId);
}
