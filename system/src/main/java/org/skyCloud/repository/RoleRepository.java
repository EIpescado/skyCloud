package org.skyCloud.repository;

import org.skyCloud.base.BaseRepository;
import org.skyCloud.entity.Role;
import org.springframework.stereotype.Repository;

/**
 * Created by yq on 2016/06/16 14:38.
 * 角色
 */
@Repository
public interface RoleRepository extends BaseRepository<Role,String> {

    Role findByCode(String code);
}
