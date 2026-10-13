package org.jeecg.modules.wms.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.wms.goods.entity.WmsProductCategories;
import org.jeecg.modules.wms.goods.entity.WmsProducts;
import org.jeecg.modules.wms.goods.mapper.WmsProductCategoriesMapper;
import org.jeecg.modules.wms.goods.service.IWmsProductCategoriesService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 商品类别
 * @Author: jeecg-boot
 * @Date:   2025-04-13
 * @Version: V1.0
 */
@Service
public class WmsProductCategoriesServiceImpl extends ServiceImpl<WmsProductCategoriesMapper, WmsProductCategories> implements IWmsProductCategoriesService {

	@Override
	public void addWmsProductCategories(WmsProductCategories wmsProductCategories) {
	   //新增时设置hasChild为0
	    wmsProductCategories.setHasChild(IWmsProductCategoriesService.NOCHILD);
		if(oConvertUtils.isEmpty(wmsProductCategories.getParentId())){
			wmsProductCategories.setParentId(IWmsProductCategoriesService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			WmsProductCategories parent = baseMapper.selectById(wmsProductCategories.getParentId());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
        //设置分类的编号
        String code = generateCode(wmsProductCategories.getParentId());
        wmsProductCategories.setCategoryCode( code);
        baseMapper.insert(wmsProductCategories);
	}

    public String generateCode(String parentId) {
        //编码规则：第一级分类为两位编号，第二级为四位编码(前两位为父类编号，后两位为自身编号)，依次类推
        //如果parentId为空则查询所有根节点数量，新节点编号为所有分类数量+1
        if(oConvertUtils.isEmpty(parentId)||parentId.equals(IWmsProductCategoriesService.ROOT_PID_VALUE)){
            List<WmsProductCategories> list = baseMapper.selectList(new QueryWrapper<WmsProductCategories>().eq("parent_id", IWmsProductCategoriesService.ROOT_PID_VALUE));
            int i = list.size() + 1;
            return String.format("%02d", i);
        }else{
            //查询父节点
            WmsProductCategories parent = baseMapper.selectById(parentId);
            //查询该节点的下属节点
            List<WmsProductCategories> list = baseMapper.selectList(new QueryWrapper<WmsProductCategories>().eq("parent_id",parentId));
            int i = list.size() + 1;
            //新节点编码为父节点编码+i
            String code = parent.getCategoryCode() + String.format("%02d", i);
            return code;
        }
    }
	@Override
	public void updateWmsProductCategories(WmsProductCategories wmsProductCategories) {
		WmsProductCategories entity = this.getById(wmsProductCategories.getId());
		if(entity==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		String old_pid = entity.getParentId();
		String new_pid = wmsProductCategories.getParentId();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				wmsProductCategories.setParentId(IWmsProductCategoriesService.ROOT_PID_VALUE);
			}
			if(!IWmsProductCategoriesService.ROOT_PID_VALUE.equals(wmsProductCategories.getParentId())) {
				baseMapper.updateTreeNodeStatus(wmsProductCategories.getParentId(), IWmsProductCategoriesService.HASCHILD);
			}
		}
		baseMapper.updateById(wmsProductCategories);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteWmsProductCategories(String id) throws JeecgBootException {
		//查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        if(id.indexOf(",")>0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if(idVal != null){
                    WmsProductCategories wmsProductCategories = this.getById(idVal);
                    String pidVal = wmsProductCategories.getParentId();
                    //查询此节点上一级是否还有其他子节点
                    List<WmsProductCategories> dataList = baseMapper.selectList(new QueryWrapper<WmsProductCategories>().eq("parent_id", pidVal).notIn("id",Arrays.asList(idArr)));
                    boolean flag = (dataList == null || dataList.size() == 0) && !Arrays.asList(idArr).contains(pidVal) && !sb.toString().contains(pidVal);
                    if(flag){
                        //如果当前节点原本有子节点 现在木有了，更新状态
                        sb.append(pidVal).append(",");
                    }
                }
            }
            //批量删除节点
            baseMapper.deleteBatchIds(Arrays.asList(idArr));
            //修改已无子节点的标识
            String[] pidArr = sb.toString().split(",");
            for(String pid : pidArr){
                this.updateOldParentNode(pid);
            }
        }else{
            WmsProductCategories wmsProductCategories = this.getById(id);
            if(wmsProductCategories==null) {
                throw new JeecgBootException("未找到对应实体");
            }
            updateOldParentNode(wmsProductCategories.getParentId());
            baseMapper.deleteById(id);
        }
	}

	@Override
    public List<WmsProductCategories> queryTreeListNoPage(QueryWrapper<WmsProductCategories> queryWrapper) {
        List<WmsProductCategories> dataList = baseMapper.selectList(queryWrapper);
        List<WmsProductCategories> mapList = new ArrayList<>();
        for(WmsProductCategories data : dataList){
            String pidVal = data.getParentId();
            //递归查询子节点的根节点
            if(pidVal != null && !IWmsProductCategoriesService.NOCHILD.equals(pidVal)){
                WmsProductCategories rootVal = this.getTreeRoot(pidVal);
                if(rootVal != null && !mapList.contains(rootVal)){
                    mapList.add(rootVal);
                }
            }else{
                if(!mapList.contains(data)){
                    mapList.add(data);
                }
            }
        }
        return mapList;
    }

    @Override
    public List<SelectTreeModel> queryListByCode(String parentCode) {
        String pid = ROOT_PID_VALUE;
        if (oConvertUtils.isNotEmpty(parentCode)) {
            LambdaQueryWrapper<WmsProductCategories> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WmsProductCategories::getParentId, parentCode);
            List<WmsProductCategories> list = baseMapper.selectList(queryWrapper);
            if (list == null || list.size() == 0) {
                throw new JeecgBootException("该编码【" + parentCode + "】不存在，请核实!");
            }
            if (list.size() > 1) {
                throw new JeecgBootException("该编码【" + parentCode + "】存在多个，请核实!");
            }
            pid = list.get(0).getId();
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public List<SelectTreeModel> queryListByPid(String pid) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, null);
    }

	/**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!IWmsProductCategoriesService.ROOT_PID_VALUE.equals(pid)) {
			Long count = baseMapper.selectCount(new QueryWrapper<WmsProductCategories>().eq("parent_id", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IWmsProductCategoriesService.NOCHILD);
			}
		}
	}

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private WmsProductCategories getTreeRoot(String pidVal){
        WmsProductCategories data =  baseMapper.selectById(pidVal);
        if(data != null && !IWmsProductCategoriesService.ROOT_PID_VALUE.equals(data.getParentId())){
            return this.getTreeRoot(data.getParentId());
        }else{
            return data;
        }
    }

    /**
     * 根据id查询所有子节点id
     * @param ids
     * @return
     */
    private String queryTreeChildIds(String ids) {
        //获取id数组
        String[] idArr = ids.split(",");
        StringBuffer sb = new StringBuffer();
        for (String pidVal : idArr) {
            if(pidVal != null){
                if(!sb.toString().contains(pidVal)){
                    if(sb.toString().length() > 0){
                        sb.append(",");
                    }
                    sb.append(pidVal);
                    this.getTreeChildIds(pidVal,sb);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 递归查询所有子节点
     * @param pidVal
     * @param sb
     * @return
     */
    private StringBuffer getTreeChildIds(String pidVal,StringBuffer sb){
        List<WmsProductCategories> dataList = baseMapper.selectList(new QueryWrapper<WmsProductCategories>().eq("parent_id", pidVal));
        if(dataList != null && dataList.size()>0){
            for(WmsProductCategories tree : dataList) {
                if(!sb.toString().contains(tree.getId())){
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(),sb);
            }
        }
        return sb;
    }

}
