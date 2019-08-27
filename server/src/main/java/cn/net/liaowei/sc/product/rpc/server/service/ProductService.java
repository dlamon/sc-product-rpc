package cn.net.liaowei.sc.product.rpc.server.service;

import cn.net.liaowei.sc.product.rpc.common.DecreaseQuotaDTO;
import cn.net.liaowei.sc.product.rpc.server.domain.dos.ProductInfoDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author LiaoWei
 */
public interface ProductService {
    /**
     * 查询所有在售商品列表
     * @param pageable 分页参数
     * @return 在售商品列表
     */
    Page<ProductInfoDO> listAllOnSaleProduct(Pageable pageable);

    /**
     * 查询特定商品ID的商品列表
     * @param productIdList 商品ID列表
     * @param pageable 分页参数
     * @return 商品列表
     */
    Page<ProductInfoDO> listProductIn(List<Integer> productIdList, Pageable pageable);

    /**
     * 扣减商品额度
     * @param decreaseQuotaInputList 购买商品列表
     * @return 剩余的商品额度
     */
    void decreaseQuota(List<DecreaseQuotaDTO> decreaseQuotaInputList);

}
