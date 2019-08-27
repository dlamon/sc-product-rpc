package cn.net.liaowei.sc.product.rpc.server.client.impl;

import cn.net.liaowei.sc.product.rpc.client.ProductClient;
import cn.net.liaowei.sc.product.rpc.common.DecreaseQuotaDTO;
import cn.net.liaowei.sc.product.rpc.common.ProductInfoDTO;
import cn.net.liaowei.sc.product.rpc.server.domain.dos.ProductInfoDO;
import cn.net.liaowei.sc.product.rpc.server.enums.ErrorEnum;
import cn.net.liaowei.sc.product.rpc.server.exception.SCException;
import cn.net.liaowei.sc.product.rpc.server.service.CategoryService;
import cn.net.liaowei.sc.product.rpc.server.service.ProductService;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LiaoWei
 */

@Slf4j
@Api(tags = "/product", description = "产品服务")
@Service
@SofaService(
        interfaceType = ProductClient.class,
        bindings = @SofaServiceBinding(
                bindingType = "bolt",
                parameters = @SofaParameter(key = ConsulConstants.CONSUL_SERVICE_NAME_KEY, value = "${spring.application.name}")
        )
)
public class ProductClientImpl implements ProductClient {
    private ProductService productService;
    private CategoryService categoryService;

    public ProductClientImpl(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @ApiOperation("通过产品Id查询产品列表")
    @Override
    public List<ProductInfoDTO> listByProductId(List<Integer> productIdList) {
        long before = System.currentTimeMillis();
        // 测试hystrix配置超时时间
        if (productIdList.size() == 3) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 测试hystrix配置熔断
        // 1、不进行熔断计数
        if (productIdList.size() == 4) {
            throw new SCException(ErrorEnum.PRODUCT_HYSTRIX_TEST_ERROR);
        }
        // 2、进行熔断计数
        if (productIdList.size() == 5) {
            throw new RuntimeException();
        }
        Page<ProductInfoDO> productInfoDOPage = productService.listProductIn(productIdList, null);
        long end = System.currentTimeMillis();
        long times = end - before;
        log.info("Span times: {}", times);

        return productInfoDOPage.stream().map(e -> {
            ProductInfoDTO output = new ProductInfoDTO();
            BeanUtils.copyProperties(e, output);
            return output;
        }).collect(Collectors.toList());
    }

    @ApiOperation("扣减产品可用额度")
    @Override
    public void decreaseQuota(List<DecreaseQuotaDTO> decreaseQuotaInputList) {
        productService.decreaseQuota(decreaseQuotaInputList);
    }
}
