package org.skyCloud.repository;

import org.skyCloud.base.BaseRepository;
import org.skyCloud.entity.User;
import org.springframework.stereotype.Repository;

/**
 * Created by yq on 2016/06/16 14:38.
 * 用户
 */
@Repository
public interface UserRepository extends BaseRepository<User,String> {

    User findByEmailAndPassword(String email, String password);

    //查询用户名
    Long countByUserName(String userName);

    //查询用户名
    Long countByEmail(String email);

    //查询用户名
    Long countByPhone(String phone);
}
