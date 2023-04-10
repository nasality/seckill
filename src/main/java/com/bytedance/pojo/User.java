package com.bytedance.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
//使用lombok简化开发
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String nickname;
    private String password;
    private String slat;
    private String head;
    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCount;
}
