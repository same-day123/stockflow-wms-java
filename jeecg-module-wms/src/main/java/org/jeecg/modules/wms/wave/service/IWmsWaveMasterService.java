package org.jeecg.modules.wms.wave.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.wms.outorder.entity.WmsOutOrders;
import org.jeecg.modules.wms.wave.entity.WmsWaveMaster;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 波次主表
 * @Author: jeecg-boot
 * @Date:   2025-06-03
 * @Version: V1.0
 */
public interface IWmsWaveMasterService extends IService<WmsWaveMaster> {

	/**
	 * 查询列表
	 * @param wmsWaveMaster
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public IPage<WmsWaveMaster> queryList(WmsWaveMaster wmsWaveMaster, Integer pageNo, Integer pageSize);
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
	 * 添加波次
	 * @param orders
	 * @param strategyType
	 * @param shipmentStrategy 包裹策略
	 * @return
	 */
	WmsWaveMaster addWave(List<WmsOutOrders> orders, String strategyType, String shipmentStrategy);

	/**
	 * 更新波次主表的拣货状态，如果所有波次下出库单的状态为已拣货，则更新波次主表的拣货状态为拣货完成
	 * @param waveId 波次ID
	 */
	void updatePickStatus(String waveId);

	/**
	 * 向波次表增加分拣数量
	 */
	void addSortingQuantity(String waveId, Integer sortingQuantity);


	/**
	 * 更新完成分拣订单数量
	 */
	void updateCompleteSortingOrderQuantity(List<String> waveIds);
	/**
	 * 创建波次
	 * @param strategyCodes
	 */
	void createWave(String warehouseId,List<String> strategyCodes);
}
