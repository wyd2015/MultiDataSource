package com.ethan.datasource.service;

import com.ethan.datasource.annotation.TargetDataSource;
import com.ethan.datasource.common.DataSourceKey;
import com.ethan.datasource.dao.UserMapper;
import com.ethan.datasource.model.po.User;
import com.ethan.datasource.model.po.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userDAO;

    public int insertUser(User user){
        return userDAO.insert(user);
    }

    @TargetDataSource(dataSourceKey = DataSourceKey.DB_MASTER)
    public List<User> getUserList(){
        UserExample example = new UserExample();
        example.createCriteria().andUsernameIsNotNull();
        return userDAO.selectByExample(example);
    }

}
