package cn.net.liaowei.sc.product.rpc.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author LiaoWei
 */
@Data
public class ProductDTO {
    @ApiModelProperty("类别名称")
    private String categoryName;

    @ApiModelProperty("类别类型 1定期 2理财 3贷款")
    private Short categoryType;

    @ApiModelProperty("产品信息")
    private List<ProductInfoDTO> productInfoDTOList;
}
