package com.logiagent.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.logiagent.auth.entity.RolePermissionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermissionEntity> {

    @Select({
            "<script>",
            "SELECT permission_id FROM t_role_permission WHERE role_id IN",
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>#{roleId}</foreach>",
            "</script>"
    })
    List<Long> selectPermissionIdsByRoleIds(@Param("roleIds") List<Long> roleIds);
}
