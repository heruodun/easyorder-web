package net.lab1024.sa.admin.module.business.order.task.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 */
@AllArgsConstructor
@Getter
public enum TaskStatusEnum {

    UNDO("未开始", 0),
    DOING( "进行中", 10),
    DONE("已完成", 100),
   ;
    private final String description;

    private final int status;

}
