package org.skyCloud.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by yq on 2016/05/24 16:41.
 */
@NoRepositoryBean
public interface BaseRepository<T,ID extends Serializable>extends JpaRepository<T,ID>,JpaSpecificationExecutor<T> {
}
