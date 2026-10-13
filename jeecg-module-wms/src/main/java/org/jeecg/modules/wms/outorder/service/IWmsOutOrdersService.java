package org.jeecg.modules.wms.outorder.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.wms.inventory.entity.WmsInventory;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersAllocation;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrdersItems;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 出库单
 * @Author: jeecg-boot
 * @Date:   2025-05-19
 * @Version: V1.0
 */
public interface IWmsOutOrdersService extends IService<WmsOutOrders> {

	/**
	 * 分页查询列表
	 */
	public IPage<WmsOutOrders> queryList(WmsOutOrders wmsOutOrders, Integer pageNo, Integer pageSize);

	/**
	 * 添加一对多
	 *
	 * @param wmsOutOrders
	 * @param wmsOutOrdersItemsList
	 */
	public void saveMain(WmsOutOrders wmsOutOrders,List<WmsOutOrdersItems> wmsOutOrdersItemsList) ;

	/**
	 * 新增出库单
	 */
	public void add(WmsOutOrders wmsOutOrders);
	/**
	 * 修改一对多
	 *
   * @param wmsOutOrders
   * @param wmsOutOrdersItemsList
	 */
	public void updateMain(WmsOutOrders wmsOutOrders,List<WmsOutOrdersItems> wmsOutOrdersItemsList);

	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);

	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);

	/**
	 * 生成出库单号
	 */
	public String generateOrderNumber();

	/**
	 * 分配库存
	 * @param orderId 出库单ID
	 * @return 分配状态 ："ALLOCATED"-分配成功 分配失败 "ALLOCATED_FAILED"-分配失败
	 */
	String allocateStock(String orderId);

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId
	 * @return List<WmsOutOrders>
	 */
	public List<WmsOutOrders> selectByMainId(String mainId);

	/**
	 * 根据波次id查询出库单
	 */
	List<WmsOutOrders> selectByWaveId(String waveId);

	/**
	 * 获取未分配波次的出库单,状态为已分配 ALLOCATED
	 */
	public List<WmsOutOrders> selectUnassignedOrders(String warehouseId);

	/**
	 * 根据出库单id更新订单的波次ID
	 */
	public void batchUpdateWaveId( List<String> orderIds, String waveId, String shipmentStrategy);

	/**
	 * 更新出库单分配状态
	 */
	public String updateAllocateStatus(String orderId);

	/**
	 * 更新出库单的拣货状态
	 */
	public void updatePickStatus(List<String> orderIds);


	/**
	 * 提交审核
	 * @param id
	 */
    void submitAudit(String id);

	/**
	 * 审核出库单
	 * @param wmsOutOrders
	 */
	void audit(WmsOutOrders wmsOutOrders);

	/**
	 * 为商品分配库存
	 * @param item 需分配的出库明细
	 * @param availableStocks 可用库存明细
	 * @return  分配状态
	 */
	public String allocateStockToItems(WmsOutOrdersItems item, List<WmsInventory> availableStocks);


}
