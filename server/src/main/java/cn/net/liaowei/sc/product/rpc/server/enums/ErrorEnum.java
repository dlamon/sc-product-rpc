package cn.net.liaowei.sc.product.rpc.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorEnum {
    /**
     * 公共错误码枚举
     */
    SYSTEM_INTERNAL_ERROR("EEEEEEE", "系统内部错误"),
    PARAM_CHECK_ERROR("PRM0001", "参数检查错误"),
    /**
     * 服务错误码枚举
     */
    PRODUCT_NOT_EXIST("PRD0001", "产品不存在"),
    PRODUCT_SALE_OFF("PRD0002", "产品已停售"),
    PRODUCT_SALE_NOT_STARTED("PRD0003", "产品未开始售卖"),
    PRODUCT_SALE_EXCEEDED("PRD0004", "已超过产品售卖时间"),
    PRODUCT_LESS_MINI_AMT("PRD0005", "购买金额小于产品最小购买金额"),
    PRODUCT_GREATER_MAX_AMT("PRD0006", "购买金额大于产品最大购买金额"),
    PRODUCT_REMAIN_NOT_ENOUGH("PRD0007", "产品剩余额度不足"),
    PRODUCT_HYSTRIX_TEST_ERROR("PRD0008", "产品熔断测试错误");

    private String code;
    private String message;
}
