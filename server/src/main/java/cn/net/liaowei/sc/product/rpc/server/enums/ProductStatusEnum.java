package cn.net.liaowei.sc.product.rpc.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liaowei
 */
@Getter
@AllArgsConstructor
public enum ProductStatusEnum {
    /**
     * 产品状态 0-在售 1-停售
     */
    ONSALE(Short.valueOf("0"), "在售"),
    OFFSALE(Short.valueOf("1"), "停售");

    private Short code;
    private String message;
}
