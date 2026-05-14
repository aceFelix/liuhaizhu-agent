package com.itfelix.liuhaizhuaichat.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("token_usage")
public class TokenUsage {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String userId;
    private LocalDate usageDate;
    private Integer tokenCount;
    private LocalDateTime createTime;
}
