package com.yin.gmall.user.mapper;

import com.yin.gmall.bean.UmsMember;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends tk.mybatis.mapper.common.Mapper<UmsMember> {
    //List<UmsMember> selectAllUser();

}
