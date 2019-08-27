package cn.net.liaowei.sc.product.rpc.server.service.impl;


import cn.net.liaowei.sc.product.rpc.common.DecreaseQuotaDTO;
import cn.net.liaowei.sc.product.rpc.server.domain.dos.ProductInfoDO;
import cn.net.liaowei.sc.product.rpc.server.enums.ErrorEnum;
import cn.net.liaowei.sc.product.rpc.server.enums.ProductStatusEnum;
import cn.net.liaowei.sc.product.rpc.server.exception.SCException;
import cn.net.liaowei.sc.product.rpc.server.repository.ProductInfoRepository;
import cn.net.liaowei.sc.product.rpc.server.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author LiaoWei
 */
@Service
public class ProductServiceImpl implements ProductService {
    private ProductInfoRepository productInfoRepository;

    public ProductServiceImpl(ProductInfoRepository productInfoRepository) {
        this.productInfoRepository = productInfoRepository;
    }

    @Override
    public Page<ProductInfoDO> listAllOnSaleProduct(Pageable pageable) {
        return productInfoRepository.findByStatus(ProductStatusEnum.ONSALE.getCode(), pageable);
    }

    @Override
    public Page<ProductInfoDO> listProductIn(List<Integer> productIdList, Pageable pageable) {
        return productInfoRepository.findByProductIdIn(productIdList, pageable);
    }

    @Override
    @Transactional
    public void decreaseQuota(List<DecreaseQuotaDTO> decreaseQuotaInputList) {
        for (DecreaseQuotaDTO decreaseQuotaDTO : decreaseQuotaInputList) {
            // 判断产品是否存在
            Optional<ProductInfoDO> productInfoOptional = productInfoRepository.findById(decreaseQuotaDTO.getProductId());
            if (!productInfoOptional.isPresent()) {
                throw new SCException(ErrorEnum.PRODUCT_NOT_EXIST);
            }
            ProductInfoDO productInfoDO = productInfoOptional.get();

            // 判断产品是否在售
//            if (productInfoDO.getStatus() != 0) {
//                throw new SCException(ErrorEnum.PRODUCT_SALE_OFF);
//            }

            // 判断是否大于开始时间
            if (productInfoDO.getSaleBeginTime() != null &&
                    System.currentTimeMillis() < productInfoDO.getSaleBeginTime().getTime()) {
                throw new SCException(ErrorEnum.PRODUCT_SALE_NOT_STARTED);
            }

            // 判断是否小于结束时间
//            if (productInfoDO.getSaleEndTime() != null &&
//                    System.currentTimeMillis() > productInfoDO.getSaleEndTime().getTime()) {
//                throw new SCException(ErrorEnum.PRODUCT_SALE_EXCEEDED);
//            }
            // 判断是否大于最小购买金额
            if (productInfoDO.getMinBuyAmt() != null &&
                    decreaseQuotaDTO.getBuyAmount().compareTo(productInfoDO.getMinBuyAmt()) < 0) {
                throw new SCException(ErrorEnum.PRODUCT_LESS_MINI_AMT);
            }

            // 判断是否小于最大购买金额
            if (productInfoDO.getMaxBuyAmt() != null &&
                    decreaseQuotaDTO.getBuyAmount().compareTo(productInfoDO.getMaxBuyAmt()) > 0) {
                throw new SCException(ErrorEnum.PRODUCT_GREATER_MAX_AMT);
            }

            // 判断剩余额度是否充足
            if (productInfoDO.getRemainQuota() != null &&
                    decreaseQuotaDTO.getBuyAmount().compareTo(productInfoDO.getRemainQuota()) > 0) {
                throw new SCException(ErrorEnum.PRODUCT_REMAIN_NOT_ENOUGH);
            }

            if (productInfoDO.getRemainQuota() != null) {
                // 扣除购买额度
                BigDecimal remainQuota = productInfoDO.getRemainQuota().subtract(decreaseQuotaDTO.getBuyAmount());
                productInfoDO.setRemainQuota(remainQuota);
                productInfoRepository.save(productInfoDO);
            }
        }
    }
}
