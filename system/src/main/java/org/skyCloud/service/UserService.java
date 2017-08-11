package org.skyCloud.service;

import org.skyCloud.entity.User;
import org.skyCloud.exception.UserException;
import org.skyCloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yq on 2016/06/23 17:39.
 * 用户service
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository ;

    public void register(User user){
       if(userRepository.countByUserName(user.getUserName()) > 0){
           throw new UserException(UserException.ErrorEnum.USER_NAME_REPEAT);
       }
        if(userRepository.countByEmail(user.getEmail()) > 0){
            throw new UserException(UserException.ErrorEnum.EMAIL_REPEAT);
        }
        if(userRepository.countByPhone(user.getPhone()) > 0){
            throw new UserException(UserException.ErrorEnum.PHONE_REPEAT);
        }
        userRepository.save(user);
    }

}
