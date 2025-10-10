package com.icht.backfront.dao;

import com.icht.backfront.dataobject.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface UserDAO {
    List<UserDO> findAll();
    UserDO findById(@Param("id") String id);
    void save(UserDO user);
    void update(UserDO user);
    int delete(String id);
    UserDO findByName(@Param("name") String name);
    List<UserDO> findByUserIds(@Param("ids") List<String> ids);
}
