package com.example.demo.pojo.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Author: guozhiyang
 * @Date: 2019/6/15 11:16
 */
@Data
@TableName(value = "wf_user")
public class WfUser extends Model<WfUser> {
    @TableId(value = "user_id")
    private Long userId;
    @TableField(value = "union_id")
    private String unionId;

    private String phone;
    @TableField(value = "update_at")
    private Timestamp updateAt;
    @TableField(value = "create_at")
    private Timestamp createAt;
    private Integer level;
    private Integer locked;
    private Integer deleted;
    @TableField(value = "orin_channel")
    private Integer orinChannel;
    @TableField(value = "is_old_user")
    private Integer isOldUser;

    @TableField(exist = false)
    private String theatreUnionId;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
