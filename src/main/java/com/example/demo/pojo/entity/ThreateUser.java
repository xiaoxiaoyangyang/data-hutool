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
 * @Date: 2019/6/18 10:18
 */
@Data
@TableName(value = "wf_user_mini_theatre")
public class ThreateUser extends Model<ThreateUser> {
    @TableId(value = "user_id")
    private Long userId;
    @TableField(value = "nick_name")
    private String nickName;
    @TableField(value = "union_id")
    private String unionId;
    @TableField(value = "open_id")
    private String openId;
    @TableField(value = "update_at")
    private Timestamp updateAt;
    @TableField(value = "create_at")
    private Timestamp createAt;
    private Integer locked;
    @TableField(value = "orin_channel")
    private Integer orinChannel;
    private Integer sex;
    private Integer disabled;
    @TableField(value = "locked_expired_at")
    private Timestamp lockedExpiredAt;
    @TableField(value = "user_reference_code")
    private String userReferenceCode;
    @TableField(value = "inviter_user_reference_code")
    private String inviterUserReferenceCode;
    private byte[] guid;
    private String language;
    private String city;
    private String province;
    private String country;
    private String phone;
    @TableField(value = "bright_guid")
    private String brightGuid;
    private String avatar;
    private String uuid;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
