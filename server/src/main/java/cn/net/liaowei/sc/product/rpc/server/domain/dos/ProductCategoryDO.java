package cn.net.liaowei.sc.product.rpc.server.domain.dos;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author LiaoWei
 */
@Data
@Entity
@ApiModel(description = "产品类别")
@Table(name="product_category")
public class ProductCategoryDO {
    @Id
    @ApiModelProperty("类别编号")
    private Integer categoryId;

    @ApiModelProperty("类别名称")
    private String categoryName;

    @ApiModelProperty("类别类型 1定期 2理财 3贷款")
    private Short categoryType;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
