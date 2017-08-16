package org.skyCloud.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.skyCloud.common.base.BaseModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yq on 2016/06/16 14:17.
 * 用户
 */
@Entity
@Table(name = "MY_USER")
public class User extends BaseModel {

    @Column(name = "USER_NAME", nullable = false, unique = true,length = 40)
    private String userName;

    @Column(name = "EMAIL", nullable = false, unique = true,length = 50)
    private String email ;

    @Column(name = "PASSWORD", nullable = false,length = 150)
    private String password;

    @Column(name = "PHONE", nullable = true,unique = true,length = 15)
    private String phone;

/* //   @Enumerated(EnumType.STRING)
    @OneToMany(fetch = FetchType.EAGER)  //若只写该注解 则建立中间表
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "id")
    )
   // @JoinColumn(name="user_id",referencedColumnName ="id" ) //写这在多的一端加外键 referencedColumnName 参考User字段*/

    // @JoinTable描述了多对多关系的数据表关系。name属性指定中间表名称，joinColumns定义中间表与user表的外键关系。
    // 中间表user_roles的user_id列是user表的主键列对应的外键列，inverseJoinColumns属性定义了中间表与另外一端的外键关系。
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "id") })
    @JsonIgnore
    private Set<Role> roles;

    @Transient
    @JsonIgnore
    public Set<String> getRolesName() {
        Set<Role> roles = getRoles();
        Set<String> set = new HashSet<String>();
        for (Role role : roles) {
            set.add(role.getName());
        }
        return set;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return super.toString() + "User{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
