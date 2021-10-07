package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设备管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    //注入service
    @Autowired
    private HospitalSetService hospitalSetService;

    //1：查询医院设置表里的所有信息
    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("/findAll")
    public Result findAllHospital() {
        //调用service中的方法
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    //2 逻辑删除医院设置
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("/{id}")
    public Result removeHospSet(@PathVariable Long id) {
        boolean flag = hospitalSetService.removeById(id);
        if(flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //3:条件查询分页
    @ApiOperation(value = "分页查询医院设置")
    @PostMapping("/findPageHospSet/{pageNum}/{pageSize}")
    public Result findPageHospSet(@PathVariable("pageNum")Long pageNum ,
                                  @PathVariable("pageSize")Long pageSize,
                                  @RequestBody(required = false) HospitalSetQueryVo vo //查询条件，为了照顾前端，返回json
                                  ){
        Page<HospitalSet> page=new Page<>(pageNum,pageSize); //当前的页数，每页的记录
        QueryWrapper<HospitalSet> wrapper=new QueryWrapper<>();
        String hosname = vo.getHosname(); //医院名称
        String hoscode = vo.getHoscode(); //医院编号
        //如果不为空 进行条件查询
        if(! StringUtils.isEmpty(hosname)){
            wrapper.like("hosname",hosname);
        }
        if(! StringUtils.isEmpty(hoscode)){
            wrapper.eq("hosname",hashCode());
        }
        //调用方法 实现分页查询
        Page<HospitalSet> pageHospitalSet = hospitalSetService.page(page,wrapper);
        //返回结果
        return Result.ok(pageHospitalSet);
    }

    //4:添加医院设置
    @ApiOperation(value = "添加医院设置")
    @PostMapping("/saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        //设置状态 1使用，0不使用
        hospitalSet.setStatus(1);
        //签名秘钥
        Random random=new Random();
        //通过当前时间加上随机数 设置签名秘钥
        hospitalSet.setSignKey(System.currentTimeMillis()+""+random.nextInt(1000));
        //调用Service
        boolean save = hospitalSetService.save(hospitalSet);
        if(save){
            return Result.ok();
        }
        return Result.fail();
    }

    //5 根据id获取医院设置
    @ApiOperation(value = "根据id获取医院设置")
    @GetMapping("/getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id) {
        HospitalSet byId = hospitalSetService.getById(id);
        return Result.ok(byId);
    }

    //6：修改医院配置
    @ApiOperation(value = "修改医院配置")
    @PostMapping("/updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean b = hospitalSetService.updateById(hospitalSet);
        if(b){
            return Result.ok();
        }
        return Result.fail();
    }

    //7：批量删除医院设置
    @ApiOperation(value = "批量删除医院设置")
    @PostMapping("/batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        hospitalSetService.removeByIds(idList);
        return Result.ok();
    }

    //8:医院设置锁定和解锁
    @ApiOperation(value = "医院设置锁定和解锁")
    @PutMapping("/lockHospitalSet/{id}/status")
    public Result lock(@PathVariable("id")Long id,
                       @PathVariable("status")Integer status){
        //先根据ID查询医院设置信息
        HospitalSet byId = hospitalSetService.getById(id);
        //设置状态
        byId.setStatus(status);
        //调用方法
        hospitalSetService.updateById(byId);
        return Result.ok();
    }


    //9:发送签名秘钥
    @PutMapping("/sendKey/{id}")
    public Result lockHospitalSet(@PathVariable("id")Long id){
        HospitalSet byId = hospitalSetService.getById(id);
        String signKey = byId.getSignKey();//秘钥
        String hoscode = byId.getHoscode();//编号
        // 发送短信
        return Result.ok();
    }



}
