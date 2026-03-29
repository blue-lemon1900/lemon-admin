package org.lemon.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Strings;
import org.lemon.commons.core.constant.CacheNames;
import org.lemon.commons.core.constant.SystemConstants;
import org.lemon.commons.core.constant.TenantConstants;
import org.lemon.commons.core.exceptions.ServiceException;
import org.lemon.commons.core.service.RoleService;
import org.lemon.commons.core.utils.MapstructUtils;
import org.lemon.commons.core.utils.StreamUtils;
import org.lemon.commons.core.utils.StringUtils;
import org.lemon.commons.mybatis.core.page.PageQuery;
import org.lemon.commons.mybatis.core.page.TableDataInfo;
import org.lemon.commons.redis.utils.RedisUtils;
import org.lemon.commons.security.data.LoginUserInfo;
import org.lemon.commons.security.data.model.RoleModel;
import org.lemon.commons.security.utils.SecurityUtil;
import org.lemon.system.domain.bo.SysRoleBo;
import org.lemon.system.domain.entity.SysRole;
import org.lemon.system.domain.entity.SysRoleDept;
import org.lemon.system.domain.entity.SysRoleMenu;
import org.lemon.system.domain.entity.SysUserRole;
import org.lemon.system.domain.vo.SysRoleVo;
import org.lemon.system.mapper.SysRoleDeptMapper;
import org.lemon.system.mapper.SysRoleMapper;
import org.lemon.system.mapper.SysRoleMenuMapper;
import org.lemon.system.mapper.SysUserRoleMapper;
import org.lemon.system.service.ISysRoleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import static org.lemon.commons.core.constant.GlobalConstants.ACCESS_TOKEN;
import static org.lemon.commons.core.constant.GlobalConstants.REFRESH_TOKEN;

/**
 * 角色 业务层处理
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysRoleServiceImpl implements ISysRoleService, RoleService {

    private final SysRoleMapper baseMapper;

    private final SysRoleMenuMapper roleMenuMapper;

    private final SysUserRoleMapper userRoleMapper;

    private final SysRoleDeptMapper roleDeptMapper;

    /**
     * 分页查询角色列表
     *
     * @param role      查询条件
     * @param pageQuery 分页参数
     * @return 角色分页列表
     */
    @Override
    public TableDataInfo<SysRoleVo> selectPageRoleList(SysRoleBo role, PageQuery pageQuery) {
        Page<SysRoleVo> page = baseMapper.selectPageRoleList(pageQuery.build(), this.buildQueryWrapper(role));
        return TableDataInfo.build(page);
    }

    /**
     * 根据条件查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
    public List<SysRoleVo> selectRoleList(SysRoleBo role) {
        return baseMapper.selectRoleList(this.buildQueryWrapper(role));
    }

    private Wrapper<SysRole> buildQueryWrapper(SysRoleBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysRole> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ObjectUtil.isNotNull(bo.getRoleId()), SysRole::getRoleId, bo.getRoleId())
                .like(StringUtils.isNotBlank(bo.getRoleName()), SysRole::getRoleName, bo.getRoleName())
                .eq(StringUtils.isNotBlank(bo.getStatus()), SysRole::getStatus, bo.getStatus())
                .like(StringUtils.isNotBlank(bo.getRoleKey()), SysRole::getRoleKey, bo.getRoleKey())
                .between(params.get("beginTime") != null && params.get("endTime") != null,
                        SysRole::getCreateTime, params.get("beginTime"), params.get("endTime"))
                .orderByAsc(SysRole::getRoleSort).orderByAsc(SysRole::getCreateTime);
        return wrapper;
    }

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRoleVo> selectRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    /**
     * 根据用户ID查询角色列表(包含被授权状态)
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRoleVo> selectRolesAuthByUserId(Long userId) {
        List<SysRoleVo> userRoles = baseMapper.selectRolesByUserId(userId);
        List<SysRoleVo> roles = selectRoleAll();
        // 使用HashSet提高查找效率
        Set<Long> userRoleIds = StreamUtils.toSet(userRoles, SysRoleVo::getRoleId);
        for (SysRoleVo role : roles) {
            if (userRoleIds.contains(role.getRoleId())) {
                role.setFlag(true);
            }
        }
        return roles;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<SysRoleVo> perms = baseMapper.selectRolesByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRoleVo perm : perms) {
            if (ObjectUtil.isNotNull(perm)) {
                permsSet.addAll(StringUtils.splitList(perm.getRoleKey().trim()));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<SysRoleVo> selectRoleAll() {
        return this.selectRoleList(new SysRoleBo());
    }

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    @Override
    public List<Long> selectRoleListByUserId(Long userId) {
        List<SysRoleVo> list = baseMapper.selectRolesByUserId(userId);
        return StreamUtils.toList(list, SysRoleVo::getRoleId);
    }

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRoleVo selectRoleById(Long roleId) {
        return baseMapper.selectRoleById(roleId);
    }

    /**
     * 通过角色ID串查询角色
     *
     * @param roleIds 角色ID串
     * @return 角色列表信息
     */
    @Override
    public List<SysRoleVo> selectRoleByIds(List<Long> roleIds) {
        return baseMapper.selectRoleList(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, SystemConstants.NORMAL)
                .in(CollUtil.isNotEmpty(roleIds), SysRole::getRoleId, roleIds));
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleNameUnique(SysRoleBo role) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleName, role.getRoleName())
                .ne(ObjectUtil.isNotNull(role.getRoleId()), SysRole::getRoleId, role.getRoleId()));
        return !exist;
    }

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleKeyUnique(SysRoleBo role) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleKey, role.getRoleKey())
                .ne(ObjectUtil.isNotNull(role.getRoleId()), SysRole::getRoleId, role.getRoleId()));
        return !exist;
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRoleBo role) {
        if (ObjectUtil.isNotNull(role.getRoleId()) && SecurityUtil.isSuperAdmin(role.getRoleId())) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
        String[] keys = new String[]{TenantConstants.SUPER_ADMIN_ROLE_KEY, TenantConstants.TENANT_ADMIN_ROLE_KEY};
        // 新增不允许使用 管理员标识符
        if (ObjectUtil.isNull(role.getRoleId())
                && Strings.CS.equalsAny(role.getRoleKey(), keys)) {
            throw new ServiceException("不允许使用系统内置管理员角色标识符!");
        }
        // 修改不允许修改 管理员标识符
        if (ObjectUtil.isNotNull(role.getRoleId())) {
            SysRole sysRole = baseMapper.selectById(role.getRoleId());
            // 如果标识符不相等 判断为修改了管理员标识符
            if (!Strings.CS.equals(sysRole.getRoleKey(), role.getRoleKey())) {
                if (Strings.CS.equalsAny(sysRole.getRoleKey(), keys)) {
                    throw new ServiceException("不允许修改系统内置管理员角色标识符!");
                } else if (Strings.CS.equalsAny(role.getRoleKey(), keys)) {
                    throw new ServiceException("不允许使用系统内置管理员角色标识符!");
                }
            }
        }
    }

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    @Override
    public void checkRoleDataScope(Long roleId) {
        if (ObjectUtil.isNull(roleId)) {
            return;
        }
        this.checkRoleDataScope(Collections.singletonList(roleId));
    }

    /**
     * 校验角色是否有数据权限
     *
     * @param roleIds 角色ID列表（支持传单个ID）
     */
    @Override
    public void checkRoleDataScope(List<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds) || SecurityUtil.isSuperAdmin()) {
            return;
        }
        long count = baseMapper.selectRoleCount(roleIds);
        if (count != roleIds.size()) {
            throw new ServiceException("没有权限访问部分角色数据！");
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public long countUserRoleByRoleId(Long roleId) {
        return userRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, roleId));
    }

    /**
     * 新增保存角色信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRoleBo bo) {
        SysRole role = MapstructUtils.convert(bo, SysRole.class);
        // 新增角色信息
        baseMapper.insert(role);
        bo.setRoleId(role.getRoleId());
        return insertRoleMenu(bo);
    }

    /**
     * 修改保存角色信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SysRoleBo bo) {
        SysRole role = MapstructUtils.convert(bo, SysRole.class);

        if (SystemConstants.DISABLE.equals(role.getStatus()) && this.countUserRoleByRoleId(role.getRoleId()) > 0) {
            throw new ServiceException("角色已分配，不能禁用!");
        }
        // 修改角色信息
        baseMapper.updateById(role);
        // 删除角色与菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, role.getRoleId()));
        return insertRoleMenu(bo);
    }

    /**
     * 修改角色状态
     *
     * @param roleId 角色ID
     * @param status 角色状态
     * @return 结果
     */
    @Override
    public int updateRoleStatus(Long roleId, String status) {
        if (SystemConstants.DISABLE.equals(status) && this.countUserRoleByRoleId(roleId) > 0) {
            throw new ServiceException("角色已分配，不能禁用!");
        }
        return baseMapper.update(null,
                new LambdaUpdateWrapper<SysRole>()
                        .set(SysRole::getStatus, status)
                        .eq(SysRole::getRoleId, roleId));
    }

    /**
     * 修改数据权限信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    @CacheEvict(cacheNames = CacheNames.SYS_ROLE_CUSTOM, key = "#bo.roleId")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int authDataScope(SysRoleBo bo) {
        SysRole role = MapstructUtils.convert(bo, SysRole.class);
        // 修改角色信息
        baseMapper.updateById(role);
        // 删除角色与部门关联
        roleDeptMapper.delete(new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, role.getRoleId()));
        // 新增角色和部门信息（数据权限）
        return insertRoleDept(bo);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    private int insertRoleMenu(SysRoleBo role) {
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (CollUtil.isNotEmpty(list)) {
            rows = roleMenuMapper.insertBatch(list) ? list.size() : 0;
        }
        return rows;
    }

    /**
     * 新增角色部门信息(数据权限)
     *
     * @param role 角色对象
     */
    private int insertRoleDept(SysRoleBo role) {
        int rows = 1;
        // 新增角色与部门（数据权限）管理
        List<SysRoleDept> list = new ArrayList<>();
        for (Long deptId : role.getDeptIds()) {
            SysRoleDept rd = new SysRoleDept();
            rd.setRoleId(role.getRoleId());
            rd.setDeptId(deptId);
            list.add(rd);
        }
        if (CollUtil.isNotEmpty(list)) {
            rows = roleDeptMapper.insertBatch(list) ? list.size() : 0;
        }
        return rows;
    }

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @CacheEvict(cacheNames = CacheNames.SYS_ROLE_CUSTOM, key = "#roleId")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleById(Long roleId) {
        // 删除角色与菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        // 删除角色与部门关联
        roleDeptMapper.delete(new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, roleId));
        return baseMapper.deleteById(roleId);
    }

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @CacheEvict(cacheNames = CacheNames.SYS_ROLE_CUSTOM, allEntries = true)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(List<Long> roleIds) {
        this.checkRoleDataScope(roleIds);
        List<SysRole> roles = baseMapper.selectByIds(roleIds);
        for (SysRole role : roles) {
            checkRoleAllowed(BeanUtil.toBean(role, SysRoleBo.class));
            if (countUserRoleByRoleId(role.getRoleId()) > 0) {
                throw new ServiceException(String.format("%1$s已分配，不能删除!", role.getRoleName()));
            }
        }
        // 删除角色与菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds));
        // 删除角色与部门关联
        roleDeptMapper.delete(new LambdaQueryWrapper<SysRoleDept>().in(SysRoleDept::getRoleId, roleIds));
        return baseMapper.deleteByIds(roleIds);
    }

    /**
     * 取消授权用户角色
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    @Override
    public int deleteAuthUser(SysUserRole userRole) {
        if (Objects.equals(SecurityUtil.getLoginUserId(), userRole.getUserId())) {
            throw new ServiceException("不允许修改当前用户角色!");
        }
        int rows = userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, userRole.getRoleId())
                .eq(SysUserRole::getUserId, userRole.getUserId()));
        if (rows > 0) {
            cleanOnlineUser(List.of(userRole.getUserId()));
        }
        return rows;
    }

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    @Override
    public int deleteAuthUsers(Long roleId, Long[] userIds) {
        List<Long> ids = List.of(userIds);
        if (ids.contains(SecurityUtil.getLoginUserId())) {
            throw new ServiceException("不允许修改当前用户角色!");
        }
        int rows = userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, roleId)
                .in(SysUserRole::getUserId, ids));
        if (rows > 0) {
            cleanOnlineUser(ids);
        }
        return rows;
    }

    /**
     * 批量选择授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要授权的用户数据ID
     * @return 结果
     */
    @Override
    public int insertAuthUsers(Long roleId, Long[] userIds) {
        // 新增用户与角色管理
        int rows = 1;
        List<Long> ids = List.of(userIds);
        if (ids.contains(SecurityUtil.getLoginUserId())) {
            throw new ServiceException("不允许修改当前用户角色!");
        }
        List<SysUserRole> list = StreamUtils.toList(ids, userId -> {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            return ur;
        });
        if (CollUtil.isNotEmpty(list)) {
            rows = userRoleMapper.insertBatch(list) ? list.size() : 0;
        }
        if (rows > 0) {
            cleanOnlineUser(ids);
        }
        return rows;
    }

    /**
     * 根据角色ID清除该角色关联的所有在线用户的登录状态（踢出在线用户）
     *
     * <p>
     * 先判断角色是否绑定用户，若无绑定则直接返回
     * 然后遍历当前所有在线Token，查找拥有该角色的用户并强制登出
     * 注意：在线用户量过大时，操作可能导致 Redis 阻塞，需谨慎调用
     * </p>
     *
     * @param roleId 角色ID
     */
    @Override
    public void cleanOnlineUserByRole(Long roleId) {
        doCleanOnlineUsers(
                () -> userRoleMapper.countByRoleId(roleId) > 0,
                roleId::equals
        );
    }

    /**
     * 根据用户ID列表清除对应在线用户的登录状态（踢出指定用户）
     *
     * <p>
     * 遍历当前所有在线Token，匹配用户ID列表中的用户，强制登出
     * 注意：在线用户量过大时，操作可能导致 Redis 阻塞，需谨慎调用
     * </p>
     *
     * @param userIds 需要清除的用户ID列表
     */
    @Override
    public void cleanOnlineUser(List<Long> userIds) {
        doCleanOnlineUsers(
                () -> userRoleMapper.countByUserIds(userIds) > 0,
                userIds::contains
        );
    }

    /**
     * 踢出在线用户的公共实现
     *
     * <p>
     * 先通过 hasBinding 检查是否存在关联记录，若无则跳过。
     * 再遍历 Redis 中所有在线 Token，对角色 ID 满足 roleMatcher 的用户强制登出。
     * 注意：在线用户量过大时，操作可能导致 Redis 阻塞，需谨慎调用
     * </p>
     *
     * @param hasBinding  判断是否存在绑定关系的条件（返回 false 则直接跳过）
     * @param roleMatcher 角色ID匹配断言，满足条件的用户将被踢出
     */
    private void doCleanOnlineUsers(BooleanSupplier hasBinding, Predicate<Long> roleMatcher) {
        if (!hasBinding.getAsBoolean()) {
            return;
        }

        // 查询缓存中所有的 token key
        Collection<String> keys = RedisUtils.keys("%s*".formatted(ACCESS_TOKEN));
        if (CollectionUtils.isNotEmpty(keys)) {
            return;
        }

        // 在线用户量过大会导致 Redis 阻塞卡顿，谨慎操作
        keys.parallelStream().forEach(token -> {
            LoginUserInfo userInfo = SecurityUtil.getLoginUser(token);

            if (userInfo != null && CollectionUtils.isNotEmpty(userInfo.getRoles())) {
                boolean match = userInfo.getRoles().stream()
                        .map(RoleModel::getRoleId)
                        .filter(Objects::nonNull)
                        .anyMatch(roleMatcher);

                // 如果匹配，注销用户 token
                if (match) {
                    // 删除访问令牌和刷新令牌
                    List<String> tokens = List.of(token, "%s%s".formatted(REFRESH_TOKEN, userInfo.getRefreshToken()));
                    RedisUtils.deleteObject(tokens);
                }
            }
        });
    }

    /**
     * 根据角色 ID 列表查询角色名称映射关系
     *
     * @param roleIds 角色 ID 列表
     * @return Map，其中 key 为角色 ID，value 为对应的角色名称
     */
    @Override
    public Map<Long, String> selectRoleNamesByIds(List<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return Collections.emptyMap();
        }
        List<SysRole> list = baseMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .select(SysRole::getRoleId, SysRole::getRoleName)
                        .in(SysRole::getRoleId, roleIds)
        );
        return StreamUtils.toMap(list, SysRole::getRoleId, SysRole::getRoleName);
    }

}
