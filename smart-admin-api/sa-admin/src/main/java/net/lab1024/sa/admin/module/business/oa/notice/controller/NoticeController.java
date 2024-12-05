package net.lab1024.sa.admin.module.business.oa.notice.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.extra.servlet.ServletUtil;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.mysql.cj.xdevapi.JsonArray;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import kotlin.collections.ArrayDeque;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.business.oa.notice.domain.form.*;
import net.lab1024.sa.admin.module.business.oa.notice.domain.vo.*;
import net.lab1024.sa.admin.module.business.oa.notice.service.NoticeEmployeeService;
import net.lab1024.sa.admin.module.business.oa.notice.service.NoticeService;
import net.lab1024.sa.admin.module.business.oa.notice.service.NoticeTypeService;
import net.lab1024.sa.admin.module.business.order.service.WaveHttpService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
import net.lab1024.sa.base.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

/**
 * 公告、通知、新闻等等
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-08-12 21:40:39
 * @Wechat 卓大1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Tag(name = AdminSwaggerTagConst.Business.OA_NOTICE)
@RestController
@OperateLog
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @Resource
    private NoticeTypeService noticeTypeService;

    @Resource
    private NoticeEmployeeService noticeEmployeeService;

    // --------------------- 通知公告类型 -------------------------

    @Operation(summary = "通知公告类型-获取全部 @author 卓大")
    @GetMapping("/oa/noticeType/getAll")
    public ResponseDTO<List<NoticeTypeVO>> getAll() {
        return ResponseDTO.ok(noticeTypeService.getAll());
    }

    @Operation(summary = "通知公告类型-添加 @author 卓大")
    @GetMapping("/oa/noticeType/add/{name}")
    public ResponseDTO<String> add(@PathVariable String name) {
        return noticeTypeService.add(name);
    }

    @Operation(summary = "通知公告类型-修改 @author 卓大")
    @GetMapping("/oa/noticeType/update/{noticeTypeId}/{name}")
    public ResponseDTO<String> update(@PathVariable Long noticeTypeId, @PathVariable String name) {
        return noticeTypeService.update(noticeTypeId, name);
    }

    @Operation(summary = "通知公告类型-删除 @author 卓大")
    @GetMapping("/oa/noticeType/delete/{noticeTypeId}")
    public ResponseDTO<String> deleteNoticeType(@PathVariable Long noticeTypeId) {
        return noticeTypeService.delete(noticeTypeId);
    }

    // --------------------- 【管理】通知公告-------------------------


    @Operation(summary = "【管理】通知公告-分页查询 @author 卓大")
    @PostMapping("/oa/notice/query")
    @SaCheckPermission("oa:notice:query")
    public ResponseDTO<PageResult<OrderVO>> query(@RequestBody @Valid NoticeQueryForm queryForm) throws JsonProcessingException {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        String keyword = queryForm.getKeywords();
        int offset = (int) ((queryForm.getPageNum() - 1) * queryForm.getPageSize());
        int limit = Math.toIntExact(queryForm.getPageSize());

        JSONObject jsonObject = WaveHttpService.getOrdersByKeyword(keyword, limit, offset);

        // 使用 Jackson 处理 JSON 数组
        ObjectMapper objectMapper = new ObjectMapper();

        JSONArray json = jsonObject.getJSONArray("orders");

        int count = jsonObject.getIntValue("count");

        page.setTotal(count);
        page.setPages(queryForm.getPageNum());

        List<OrderVO> orderVoList = objectMapper.readValue(json.toString(), new TypeReference<List<OrderVO>>(){});


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        for(OrderVO orderVO : orderVoList){
            if(orderVO.getCur_time() != null){
                long m =  Long.valueOf(orderVO.getCur_time());
                Date date = new Date(m);
                String formattedDate = sdf.format(date);
                orderVO.setCur_time(formattedDate);
            }
            if(orderVO.getPrint_time() != null){
                long m =  Long.valueOf(orderVO.getPrint_time());
                Date date = new Date(m);
                String formattedDate = sdf.format(date);
                orderVO.setPrint_time(formattedDate);
            }
            orderVO.setOrder_trace_arr(process(orderVO.getOrder_trace()));
        }

        // 现在 noticeList 中包含所有的 NoticeVO 对象

        return ResponseDTO.ok(SmartPageUtil.convert2PageResult(page, orderVoList));
    }

    @Operation(summary = "【管理】通知公告-添加 @author 卓大")
    @PostMapping("/oa/notice/add")
    @RepeatSubmit
    @SaCheckPermission("oa:notice:add")
    public ResponseDTO<String> add(@RequestBody @Valid NoticeAddForm addForm) {
        addForm.setCreateUserId(SmartRequestUtil.getRequestUserId());
        return noticeService.add(addForm);
    }

    @Operation(summary = "【管理】通知公告-更新 @author 卓大")
    @PostMapping("/oa/notice/update")
    @RepeatSubmit
    @SaCheckPermission("oa:notice:update")
    public ResponseDTO<String> update(@RequestBody @Valid NoticeUpdateForm updateForm) {
        return noticeService.update(updateForm);
    }

    @Operation(summary = "【管理】通知公告-更新详情 @author 卓大")
    @GetMapping("/oa/notice/getUpdateVO/{noticeId}")
    @SaCheckPermission("oa:notice:update")
    public ResponseDTO<NoticeUpdateFormVO> getUpdateFormVO(@PathVariable Long noticeId) {
        return ResponseDTO.ok(noticeService.getUpdateFormVO(noticeId));
    }

    @Operation(summary = "【管理】通知公告-删除 @author 卓大")
    @GetMapping("/oa/notice/delete/{noticeId}")
    @SaCheckPermission("oa:notice:delete")
    public ResponseDTO<String> delete(@PathVariable Long noticeId) {
        return noticeService.delete(noticeId);
    }

    // --------------------- 【员工】查看 通知公告 -------------------------


    @Operation(summary = "【员工】通知公告-查看详情 @author 卓大")
    @GetMapping("/oa/notice/employee/view/{noticeId}")
    public ResponseDTO<NoticeDetailVO> view(@PathVariable Long noticeId, HttpServletRequest request) {
        return noticeEmployeeService.view(
                SmartRequestUtil.getRequestUserId(),
                noticeId,
                ServletUtil.getClientIP(request),
                request.getHeader("User-Agent")
        );
    }

    @Operation(summary = "【员工】通知公告-查询全部 @author 卓大")
    @PostMapping("/oa/notice/employee/query")
    public ResponseDTO<PageResult<NoticeEmployeeVO>> queryEmployeeNotice(@RequestBody @Valid NoticeEmployeeQueryForm noticeEmployeeQueryForm) {
        return noticeEmployeeService.queryList(SmartRequestUtil.getRequestUserId(), noticeEmployeeQueryForm);
    }

    @Operation(summary = "【员工】通知公告-查询 查看记录 @author 卓大")
    @PostMapping("/oa/notice/employee/queryViewRecord")
    public ResponseDTO<PageResult<NoticeViewRecordVO>> queryViewRecord(@RequestBody @Valid NoticeViewRecordQueryForm noticeViewRecordQueryForm) {
        return ResponseDTO.ok(noticeEmployeeService.queryViewRecord(noticeViewRecordQueryForm));
    }

    public static List process(String str){
        // 依据 \n\n 分割字符串
        String[] elements = str.split("\n\n");

        // 创建 JSONArray 用于存储结果
        List list = new ArrayList();

        for (String element : elements) {
            // 依据，分隔元素
            String[] parts = element.split("，");
            if (parts.length < 2) {
                continue; // 如果没有足够的部分，跳过
            }

            // 处理第一个部分
            String[] x1Parts = parts[0].split("：");
            if (x1Parts.length < 2) {
                continue; // 如果分隔后没有足够的元素，跳过
            }
            String s1 = x1Parts[0].substring(0, 2); // 取前两个中文字符
            String s2 = x1Parts[1].trim(); // 取出第二个子元素

            // 处理第二个部分
            String[] x2Parts = parts[1].split("：");
            if (x2Parts.length < 2) {
                continue; // 如果分隔后没有足够的元素，跳过
            }
            String s3 = x2Parts[1].trim(); // 取第二个子元素

            // 构建 JSON 对象
            Map jsonObject = new HashMap();
            jsonObject.put("cur_status", s1);
            jsonObject.put("person", s2);
            jsonObject.put("time", s3);

            // 将 JSON 对象添加到数组中
            list.add(jsonObject);
        }
        return Lists.reverse(list);

    }



}