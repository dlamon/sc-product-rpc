package cn.net.liaowei.sc.product.rpc.server.provider;

import cn.net.liaowei.sc.product.rpc.client.ProductService;
import cn.net.liaowei.sc.product.rpc.common.DecreaseQuotaDTO;
import cn.net.liaowei.sc.product.rpc.common.ProductDTO;
import cn.net.liaowei.sc.product.rpc.common.ProductInfoDTO;
import cn.net.liaowei.sc.product.rpc.server.domain.ProductCategoryDO;
import cn.net.liaowei.sc.product.rpc.server.domain.ProductInfoDO;
import cn.net.liaowei.sc.product.rpc.server.enums.ErrorEnum;
import cn.net.liaowei.sc.product.rpc.server.enums.ProductStatusEnum;
import cn.net.liaowei.sc.product.rpc.server.exception.SCException;
import cn.net.liaowei.sc.product.rpc.server.repository.ProductCategoryRepository;
import cn.net.liaowei.sc.product.rpc.server.repository.ProductInfoRepository;
import com.alipay.sofa.rpc.registry.consul.ConsulConstants;
import com.alipay.sofa.runtime.api.annotation.SofaParameter;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author LiaoWei
 */

@Slf4j
@Api(tags = "/product", description = "产品服务")
@Service
@SofaService(
        bindings = @SofaServiceBinding(
                bindingType = "bolt",
                parameters = @SofaParameter(key = ConsulConstants.CONSUL_SERVICE_NAME_KEY, value = "${spring.application.name}")
        )
)
public class ProductServiceImpl implements ProductService {

    private ProductInfoRepository productInfoRepository;
    private ProductCategoryRepository productCategoryRepository;

    public ProductServiceImpl(ProductInfoRepository productInfoRepository, ProductCategoryRepository productCategoryRepository) {
        this.productInfoRepository = productInfoRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public List<ProductDTO> listOnSale() {
        // 获取所有在售的产品列表
        Page<ProductInfoDO> productInfoDOPage = productInfoRepository.findByStatus(ProductStatusEnum.ONSALE.getCode(), null);;

        // 获取产品对应的类别列表
        List<Short> categoryTypeList = productInfoDOPage.stream().map(ProductInfoDO::getCategoryType).collect(Collectors.toList());

        // 获取产品对应的类别信息
        List<ProductCategoryDO> productCategoryDOList = productCategoryRepository.findByCategoryTypeIn(categoryTypeList);

        // 构造返回数据
        List<ProductDTO> productDTOList = new ArrayList<>();
        for (ProductCategoryDO productCategoryDO : productCategoryDOList) {
            ProductDTO productDTO = new ProductDTO();
            BeanUtils.copyProperties(productCategoryDO, productDTO);
            List<ProductInfoDTO> productInfoDTOList = new ArrayList<>();
            for (ProductInfoDO productInfoDO : productInfoDOPage) {
                if ( productInfoDO.getCategoryType().equals(productCategoryDO.getCategoryType()) ) {
                    ProductInfoDTO productInfoDTO = new ProductInfoDTO();
                    BeanUtils.copyProperties(productInfoDO, productInfoDTO);
                    productInfoDTOList.add(productInfoDTO);
                }
            }
            productDTO.setProductInfoDTOList(productInfoDTOList);
            productDTOList.add(productDTO);
        }

        // 返回结果
        return productDTOList;
    }

    @ApiOperation("通过产品Id查询产品列表")
    @Override
    public List<ProductInfoDTO> listByIds(List<Integer> ids) {
        Page<ProductInfoDO> productInfoDOList = productInfoRepository.findByProductIdIn(ids, null);
        return productInfoDOList.stream().map(e -> {
            ProductInfoDTO output = new ProductInfoDTO();
            BeanUtils.copyProperties(e, output);
            return output;
        }).collect(Collectors.toList());
    }

    @ApiOperation("扣减产品可用额度")
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
