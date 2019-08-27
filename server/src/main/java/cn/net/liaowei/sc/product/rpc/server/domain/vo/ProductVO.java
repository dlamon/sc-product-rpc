package cn.net.liaowei.sc.product.rpc.server.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author LiaoWei
 */
@Data
public class ProductVO {
    @ApiModelProperty("类别名称")
    @JsonProperty("name")
    private String categoryName;

    @ApiModelProperty("类别类型 1定期 2理财 3贷款")
    @JsonProperty("type")
    private Short categoryType;

    @ApiModelProperty("产品信息")
    @JsonProperty("products")
    private List<ProductInfoVO> productInfoVOList;
}
