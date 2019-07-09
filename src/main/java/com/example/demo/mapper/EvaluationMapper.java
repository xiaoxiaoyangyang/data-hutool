package com.example.demo.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.demo.pojo.entity.EvaluationOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: guozhiyang
 * @Date: 2019/6/27 18:18
 */
@Mapper
public interface EvaluationMapper extends BaseMapper {
    List<EvaluationOrder> selectCustomerList(List<String> list);
}
