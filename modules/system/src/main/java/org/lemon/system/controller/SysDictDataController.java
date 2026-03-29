package org.lemon.system.controller;

import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.excel.utils.ExcelUtil;
import org.lemon.commons.idempotent.annotation.RepeatSubmit;
import org.lemon.commons.log.annotation.Log;
import org.lemon.commons.log.enums.BusinessType;
import org.lemon.commons.mybatis.core.page.PageQuery;
import org.lemon.commons.mybatis.core.page.TableDataInfo;
import org.lemon.commons.web.core.BaseController;
import org.lemon.system.domain.bo.SysDictDataBo;
import org.lemon.system.domain.vo.SysDictDataVo;
import org.lemon.system.service.ISysDictDataService;
import org.lemon.system.service.ISysDictTypeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据字典信息
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController {

    private final ISysDictDataService dictDataService;
    private final ISysDictTypeService dictTypeService;

    /**
     * 查询字典数据列表
     */
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    @GetMapping("/list")
    public TableDataInfo<SysDictDataVo> list(SysDictDataBo dictData, PageQuery pageQuery) {
        return dictDataService.selectPageDictDataList(dictData, pageQuery);
    }

    /**
     * 导出字典数据列表
     */
    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermission('system:dict:export')")
    @PostMapping("/export")
    public void export(SysDictDataBo dictData, HttpServletResponse response) {
        List<SysDictDataVo> list = dictDataService.selectDictDataList(dictData);
        ExcelUtil.exportExcel(list, "字典数据", SysDictDataVo.class, response);
    }

    /**
     * 查询字典数据详细
     *
     * @param dictCode 字典code
     */
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    @GetMapping(value = "/{dictCode}")
    public R<SysDictDataVo> getInfo(@PathVariable Long dictCode) {
        return R.success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param dictType 字典类型
     */
    @GetMapping(value = "/type/{dictType}")
    public R<List<SysDictDataVo>> dictType(@PathVariable("dictType") String dictType) {
        List<SysDictDataVo> data = dictTypeService.selectDictDataByType(dictType);
        if (ObjectUtil.isNull(data)) {
            data = new ArrayList<>();
        }
        return R.success(data);
    }

    /**
     * 新增字典数据
     */
    @PreAuthorize("@ss.hasPermission('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysDictDataBo dict) {
        if (!dictDataService.checkDictDataUnique(dict)) {
            return R.fail("新增字典数据'" + dict.getDictValue() + "'失败，字典键值已存在");
        }
        dictDataService.insertDictData(dict);
        return R.success();
    }

    /**
     * 修改保存字典数据
     */
    @PreAuthorize("@ss.hasPermission('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysDictDataBo dict) {
        if (!dictDataService.checkDictDataUnique(dict)) {
            return R.fail("修改字典数据'" + dict.getDictValue() + "'失败，字典键值已存在");
        }
        dictDataService.updateDictData(dict);
        return R.success();
    }

    /**
     * 删除字典数据
     *
     * @param dictCodes 字典code串
     */
    @PreAuthorize("@ss.hasPermission('system:dict:remove')")
    @Log(title = "字典数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public R<Void> remove(@PathVariable Long[] dictCodes) {
        dictDataService.deleteDictDataByIds(Arrays.asList(dictCodes));
        return R.success();
    }
}
