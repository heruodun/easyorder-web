package net.lab1024.sa.admin.module.business.oa.notice.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 新闻、公告  VO
 *
 * @Author 1024创新实验室-主任: 卓大
 *   {"address":"444","content":"总条数：0\n\n规格：3.5\n\n长度和条数：\n\n备注：","cur_status":"打单",
 *                         "id":87,"order_id":20240430000087,"order_trace":"打单人：刘江红，" +
 *                         "打单时间：2024-04-30 01:02:24","print_time":1714410144729,
 *                         "printer":"刘江红","sync_status":1,"update_time":"2024-04-29 17:02:24"}
 */
@Data
public class OrderVO {

// 主键 
    @Schema(description = "id")
    private Long id;

    @Schema(description = "wave_id")
    private Long wave_id;

    @Schema(description = "订单id")
    private Long order_id;

    @Schema(description = "address")
    private String address;

    @Schema(description = "content")
    private String content;

    @Schema(description = "当前状态")
    private String cur_status;

    @Schema(description = "当前处理人")
    private String cur_man;

    @Schema(description = "当前处理时间")
    private String cur_time;

    @Schema(description = "order_trace")
    private String order_trace;

    @Schema(description = "print_time")
    private String print_time;

    @Schema(description = "printer")
    private String printer;

    @Schema(description = "sync_status")
    private String sync_status;


    @Schema(description = "更新时间")
    private String update_time;

}
