package cn.net.liaowei.sc.product.rpc.client;

import cn.net.liaowei.sc.product.rpc.common.DecreaseQuotaDTO;
import cn.net.liaowei.sc.product.rpc.common.ProductInfoDTO;

import java.util.List;

/**
 * @author liaowei
 */
public interface ProductClient {
    /**
     * 通过产品编号列表获取产品信息列表
     * @param productIdList 产品编号列表
     * @return 产品信息列表
     */
    List<ProductInfoDTO> listByProductId(List<Integer> productIdList);

    /**
     * 扣减可用额度
     * @param decreaseQuotaInputList 需要扣减额度的列表
     */
    void decreaseQuota(List<DecreaseQuotaDTO> decreaseQuotaInputList);
}