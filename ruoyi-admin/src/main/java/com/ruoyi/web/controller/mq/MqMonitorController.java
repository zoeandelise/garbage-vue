package com.ruoyi.web.controller.mq;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.controller.mq.domain.ErrorMessage;
import com.ruoyi.web.controller.mq.domain.MqStatus;
import com.ruoyi.web.controller.mq.domain.Queue;

/**
 * 消息队列监控控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/mq")
public class MqMonitorController extends BaseController
{
    // 模拟消息队列状态数据
    private static final MqStatus mqStatus = new MqStatus();
    
    // 模拟队列列表数据
    private static final List<Queue> queueList = new ArrayList<>();
    
    // 模拟错误消息数据
    private static final Map<String, ErrorMessage> errorMessages = new ConcurrentHashMap<>();
    
    // 初始化一些测试数据
    static {
        // 初始化队列状态
        mqStatus.setStatus("running");
        mqStatus.setQueueCount(4);
        mqStatus.setMessageCount(126);
        mqStatus.setErrorCount(3);
        
        // 初始化队列列表
        queueList.add(new Queue("garbage-disposal", 35, 2, "active", DateUtils.parseDate("2023-06-01 08:00:00"), DateUtils.getNowDate(), 120, 35, 1));
        queueList.add(new Queue("points-calculation", 42, 3, "active", DateUtils.parseDate("2023-06-01 08:00:00"), DateUtils.getNowDate(), 315, 42, 2));
        queueList.add(new Queue("notification", 45, 2, "active", DateUtils.parseDate("2023-06-01 08:00:00"), DateUtils.getNowDate(), 430, 45, 0));
        queueList.add(new Queue("report-generation", 4, 1, "active", DateUtils.parseDate("2023-06-01 08:00:00"), DateUtils.getNowDate(), 86, 4, 0));
        
        // 初始化错误消息
        errorMessages.put("err-001", new ErrorMessage("err-001", "garbage-disposal", "error", "IllegalArgumentException", 
                "Invalid garbage type", 3, DateUtils.parseDate("2023-06-15 10:21:33"), 
                "{\"userId\":\"1001\",\"garbageType\":\"unknown\",\"weight\":1.5}", 
                "java.lang.IllegalArgumentException: Invalid garbage type\n\tat com.ruoyi.garbage.service.impl.GarbageDisposalServiceImpl.processDisposal(GarbageDisposalServiceImpl.java:45)"));
        
        errorMessages.put("err-002", new ErrorMessage("err-002", "points-calculation", "error", "NullPointerException", 
                "User not found", 2, DateUtils.parseDate("2023-06-16 14:36:45"), 
                "{\"userId\":\"9999\",\"points\":10,\"operation\":\"add\"}", 
                "java.lang.NullPointerException: User not found\n\tat com.ruoyi.garbage.service.impl.PointsServiceImpl.calculatePoints(PointsServiceImpl.java:78)"));
        
        errorMessages.put("err-003", new ErrorMessage("err-003", "garbage-disposal", "error", "DataAccessException", 
                "Database connection error", 1, DateUtils.parseDate("2023-06-17 09:15:22"), 
                "{\"userId\":\"1002\",\"garbageType\":\"recyclable\",\"weight\":2.3}", 
                "org.springframework.dao.DataAccessException: Database connection error\n\tat com.ruoyi.garbage.repository.impl.GarbageRepositoryImpl.save(GarbageRepositoryImpl.java:62)"));
    }

    /**
     * 获取消息队列状态
     */
    @PreAuthorize("@ss.hasPermi('mq:status:query')")
    @GetMapping("/status")
    public AjaxResult getMqStatus()
    {
        return success(mqStatus);
    }

    /**
     * 获取队列列表
     */
    @PreAuthorize("@ss.hasPermi('mq:queue:list')")
    @GetMapping("/queues")
    public AjaxResult getQueueList()
    {
        return success(queueList);
    }

    /**
     * 获取队列详情
     */
    @PreAuthorize("@ss.hasPermi('mq:queue:query')")
    @GetMapping("/queue/{queueName}")
    public AjaxResult getQueueDetail(@PathVariable("queueName") String queueName)
    {
        Queue queue = queueList.stream()
                .filter(q -> q.getQueueName().equals(queueName))
                .findFirst()
                .orElse(null);
        
        if (queue == null)
        {
            return error("队列不存在");
        }
        
        return success(queue);
    }

    /**
     * 清空队列
     */
    @PreAuthorize("@ss.hasPermi('mq:queue:clear')")
    @Log(title = "清空队列", businessType = BusinessType.UPDATE)
    @PostMapping("/queue/{queueName}/clear")
    public AjaxResult clearQueue(@PathVariable("queueName") String queueName)
    {
        Queue queue = queueList.stream()
                .filter(q -> q.getQueueName().equals(queueName))
                .findFirst()
                .orElse(null);
        
        if (queue == null)
        {
            return error("队列不存在");
        }
        
        // 清空队列消息
        queue.setMessageCount(0);
        queue.setPendingCount(0);
        
        // 更新总消息数
        updateMqStatus();
        
        return success();
    }

    /**
     * 重启队列
     */
    @PreAuthorize("@ss.hasPermi('mq:queue:restart')")
    @Log(title = "重启队列", businessType = BusinessType.UPDATE)
    @PostMapping("/queue/{queueName}/restart")
    public AjaxResult restartQueue(@PathVariable("queueName") String queueName)
    {
        Queue queue = queueList.stream()
                .filter(q -> q.getQueueName().equals(queueName))
                .findFirst()
                .orElse(null);
        
        if (queue == null)
        {
            return error("队列不存在");
        }
        
        // 设置队列为活跃状态
        queue.setStatus("active");
        queue.setLastActiveTime(DateUtils.getNowDate());
        
        return success();
    }

    /**
     * 获取错误消息列表
     */
    @PreAuthorize("@ss.hasPermi('mq:error:list')")
    @GetMapping("/errors")
    public TableDataInfo getErrorMessages(ErrorMessage errorMessage)
    {
        startPage();
        List<ErrorMessage> list = new ArrayList<>(errorMessages.values());
        
        // 过滤条件
        if (StringUtils.isNotEmpty(errorMessage.getQueueName()))
        {
            list = list.stream()
                    .filter(e -> e.getQueueName().equals(errorMessage.getQueueName()))
                    .collect(Collectors.toList());
        }
        
        if (StringUtils.isNotEmpty(errorMessage.getErrorType()))
        {
            list = list.stream()
                    .filter(e -> e.getErrorType().contains(errorMessage.getErrorType()))
                    .collect(Collectors.toList());
        }
        
        if (StringUtils.isNotEmpty(errorMessage.getStatus()))
        {
            list = list.stream()
                    .filter(e -> e.getStatus().equals(errorMessage.getStatus()))
                    .collect(Collectors.toList());
        }
        
        // 时间范围过滤
        if (errorMessage.getParams() != null)
        {
            if (errorMessage.getParams().get("beginTime") != null && errorMessage.getParams().get("endTime") != null)
            {
                String beginTime = (String) errorMessage.getParams().get("beginTime");
                String endTime = (String) errorMessage.getParams().get("endTime");
                
                Date beginDate = DateUtils.parseDate(beginTime);
                Date endDate = DateUtils.parseDate(endTime);
                
                list = list.stream()
                        .filter(e -> e.getProcessTime().compareTo(beginDate) >= 0 && e.getProcessTime().compareTo(endDate) <= 0)
                        .collect(Collectors.toList());
            }
        }
        
        return getDataTable(list);
    }

    /**
     * 获取错误消息详情
     */
    @PreAuthorize("@ss.hasPermi('mq:error:query')")
    @GetMapping("/error/{messageId}")
    public AjaxResult getErrorDetail(@PathVariable("messageId") String messageId)
    {
        ErrorMessage errorMessage = errorMessages.get(messageId);
        
        if (errorMessage == null)
        {
            return error("错误消息不存在");
        }
        
        return success(errorMessage);
    }

    /**
     * 重试消息
     */
    @PreAuthorize("@ss.hasPermi('mq:error:retry')")
    @Log(title = "重试消息", businessType = BusinessType.UPDATE)
    @PostMapping("/error/{messageId}/retry")
    public AjaxResult retryMessage(@PathVariable("messageId") String messageId)
    {
        ErrorMessage errorMessage = errorMessages.get(messageId);
        
        if (errorMessage == null)
        {
            return error("错误消息不存在");
        }
        
        // 模拟重试成功
        errorMessage.setStatus("success");
        errorMessage.setRetryCount(errorMessage.getRetryCount() + 1);
        errorMessage.setProcessTime(DateUtils.getNowDate());
        
        // 更新错误消息计数
        updateMqStatus();
        
        return success();
    }

    /**
     * 删除错误消息
     */
    @PreAuthorize("@ss.hasPermi('mq:error:remove')")
    @Log(title = "删除错误消息", businessType = BusinessType.DELETE)
    @DeleteMapping("/error/{messageId}")
    public AjaxResult deleteErrorMessage(@PathVariable("messageId") String messageId)
    {
        errorMessages.remove(messageId);
        
        // 更新错误消息计数
        updateMqStatus();
        
        return success();
    }

    /**
     * 导出错误消息
     */
    @PreAuthorize("@ss.hasPermi('mq:error:export')")
    @Log(title = "导出错误消息", businessType = BusinessType.EXPORT)
    @GetMapping("/errors/export")
    public void exportErrorMessages(HttpServletResponse response, ErrorMessage errorMessage)
    {
        List<ErrorMessage> list = new ArrayList<>(errorMessages.values());
        
        // 过滤条件
        if (StringUtils.isNotEmpty(errorMessage.getQueueName()))
        {
            list = list.stream()
                    .filter(e -> e.getQueueName().equals(errorMessage.getQueueName()))
                    .collect(Collectors.toList());
        }
        
        if (StringUtils.isNotEmpty(errorMessage.getErrorType()))
        {
            list = list.stream()
                    .filter(e -> e.getErrorType().contains(errorMessage.getErrorType()))
                    .collect(Collectors.toList());
        }
        
        if (StringUtils.isNotEmpty(errorMessage.getStatus()))
        {
            list = list.stream()
                    .filter(e -> e.getStatus().equals(errorMessage.getStatus()))
                    .collect(Collectors.toList());
        }
        
        ExcelUtil<ErrorMessage> util = new ExcelUtil<ErrorMessage>(ErrorMessage.class);
        util.exportExcel(response, list, "错误消息");
    }

    /**
     * 批量审核错误消息
     */
    @PreAuthorize("@ss.hasPermi('mq:error:retry')")
    @Log(title = "批量处理错误消息", businessType = BusinessType.UPDATE)
    @PostMapping("/errors/batch")
    public AjaxResult batchProcess(@RequestBody Map<String, Object> params)
    {
        List<String> ids = (List<String>) params.get("ids");
        String action = (String) params.get("action");
        
        if (ids == null || ids.isEmpty())
        {
            return error("未指定消息ID");
        }
        
        if (!"retry".equals(action) && !"delete".equals(action))
        {
            return error("不支持的操作类型");
        }
        
        for (String id : ids)
        {
            ErrorMessage errorMessage = errorMessages.get(id);
            
            if (errorMessage != null)
            {
                if ("retry".equals(action))
                {
                    // 模拟重试成功
                    errorMessage.setStatus("success");
                    errorMessage.setRetryCount(errorMessage.getRetryCount() + 1);
                    errorMessage.setProcessTime(DateUtils.getNowDate());
                }
                else if ("delete".equals(action))
                {
                    errorMessages.remove(id);
                }
            }
        }
        
        // 更新错误消息计数
        updateMqStatus();
        
        return success();
    }
    
    /**
     * 更新消息队列状态统计数据
     */
    private void updateMqStatus()
    {
        int totalMessages = queueList.stream().mapToInt(Queue::getMessageCount).sum();
        int errorCount = (int) errorMessages.values().stream()
                .filter(e -> "error".equals(e.getStatus()))
                .count();
        
        mqStatus.setMessageCount(totalMessages);
        mqStatus.setErrorCount(errorCount);
    }
} 