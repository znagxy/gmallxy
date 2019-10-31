package com.yin.gmall.user.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yin.gmall.bean.UmsMember;
import com.yin.gmall.bean.UmsMemberReceiveAddress;
import com.yin.gmall.service.UserService;
import com.yin.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.yin.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;




    @Override
    public List<UmsMember> getAllUser() {
        //return userMapper.selectAllUser();
        return userMapper.selectAll();
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
        Example e = new Example(UmsMemberReceiveAddress.class);
        e.createCriteria().andEqualTo("memberId",memberId);
        return umsMemberReceiveAddressMapper.selectByExample(e);
    }
}
