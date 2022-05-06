package com.hao.service;

import com.hao.pojo.Admin;

public interface AdminService {
    /**
     * 查询管理员
     * @param username
     * @return
     */
    Admin getAdminByName(String username);
}
