package com.example.demo.pojo.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * @Author: guozhiyang
 * @Date: 2019/6/28 11:45
 */
@Data
@TableName(value = "ed_evaluationorder")
public class EvaluationOrder {
    @TableId(value = "id")
    private Long id;
    @TableField(value = "union_id")
    private String unionId;
    @TableField(value = "customer_id")
    private String customerId;
}
