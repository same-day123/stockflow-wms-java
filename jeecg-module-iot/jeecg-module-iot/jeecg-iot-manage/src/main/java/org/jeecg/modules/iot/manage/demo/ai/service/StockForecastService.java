package org.jeecg.modules.iot.manage.demo.ai.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.jeecg.modules.iot.manage.demo.ai.dto.HistorySaleData;
import org.jeecg.modules.iot.manage.demo.ai.model.AiForecastInput;
import org.jeecg.modules.iot.manage.demo.ai.model.AiForecastOutput;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * 智能库存预测服务
 *
 * @author muht
 * @create 2025/8/8 11:21
 */
@Slf4j
public class StockForecastService {
    private static final StockForecastService stockForecastService = new StockForecastService();

    /**
     * 提示词文件路径
     */
    private static final String PROMPT_TEMPLATE_PATH = "D:\\idea_workspace\\cbec-wms\\jeecg-module-iot\\jeecg-iot-manage\\src\\main\\java\\org\\jeecg\\modules\\iot\\manage\\demo\\ai\\service\\StockForecastPrompt.md";
    // private static final String PROMPT_TEMPLATE_PATH = "/Users/muhaotian/IdeaProjects/cbec-wms/jeecg-module-iot/jeecg-iot-manage/src/main/java/org/jeecg/modules/iot/manage/ai/service/StockForecastPrompt.md";

    /**
     * Ai大模型服务
     */
    private final AiService aiService = new AiService();

    public static void main(String[] args) {
        stockForecastService.stockForecast();
    }

    /**
     * 库存预测
     */
    public void stockForecast() {
        // 获取历史日销售数据
        List<HistorySaleData> historySaleDataList = MockService.getMultiGoodsHistorySaleDatas();
        // 大模型输入数据处理与转换
        List<AiForecastInput> inputDatas = historySaleDataList.stream()
                .map(data -> {
                    AiForecastInput aiForecastInput = new AiForecastInput();
                    aiForecastInput.setGoodsInfo(data.getGoodsInfo());
                    List<AiForecastInput.SaleData> saleDataList = data.getMonthSaleDatas().stream()
                            .map(monthSaleData -> {
                                AiForecastInput.SaleData saleData = new AiForecastInput.SaleData();
                                // 销售月份
                                saleData.setSaleMonth(monthSaleData.getSaleMonth());
                                // 销售量
                                saleData.setAmount(monthSaleData.getAmount());
                                // 是否旺季
                                saleData.setIsHighSeason(monthSaleData.getIsHighSeason());
                                // 是否促销月
                                saleData.setIsPromotionMonth(monthSaleData.getIsPromotion());
                                return saleData;
                            }).toList();
                    // 封装销售数据
                    aiForecastInput.setSaleDatas(saleDataList);
                    aiForecastInput.setStockQuantity(data.getStockQuantity());
                    return aiForecastInput;
                })
                .toList();

        log.info("输入大模型销售数据：{}", JSON.toJSONString(inputDatas));

        // 从提示词工程中组装大模型提示词
        String promptTemplate = stockForecastService.getPromptTemplate();

        // 组装真实数据
        Map<String, String> data = Map.of("historySaleDatas", JSON.toJSONString(inputDatas));
        StringSubstitutor engine = new StringSubstitutor(data);
        engine.setEnableSubstitutionInVariables(true);
        // 完整提示词
        String fullPrompt = engine.replace(promptTemplate);

        // 对接AI大模型
        String result = aiService.askAi(fullPrompt);

        // 解析结果
        List<AiForecastOutput> aiForecastOutputs = JSONArray.parseArray(result, AiForecastOutput.class);
        log.info("大模型结果解析={}", JSON.toJSONString(aiForecastOutputs));
    }

    /**
     * 获取AI提示词模板
     *
     * @return
     */
    private String getPromptTemplate() {
        try {
            String prompt = Files.readString(Paths.get(PROMPT_TEMPLATE_PATH));
            return prompt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
