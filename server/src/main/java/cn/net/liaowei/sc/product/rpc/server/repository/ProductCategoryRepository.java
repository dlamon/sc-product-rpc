package cn.net.liaowei.sc.product.rpc.server.repository;

import cn.net.liaowei.sc.product.rpc.server.domain.dos.ProductCategoryDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author LiaoWei
 */
public interface ProductCategoryRepository extends JpaRepository<ProductCategoryDO, Integer> {
    /**
     * 通过类别编号列表查询编号列表对应的类别信息列表
     * @param categoryTypeList 类别编号列表
     * @return 类别信息列表
     */
    List<ProductCategoryDO> findByCategoryTypeIn(List<Short> categoryTypeList);
}
