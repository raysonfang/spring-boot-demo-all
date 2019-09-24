package cn.raysonblog.quartz.module.job.taskdemo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 定时任务demo
 * @author raysonfang
 */
@Slf4j
public class DemoJobBean extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("定时[发送邮件消息]任务开始执行......");
        stopWatch.stop();
        log.info("总计执行时间：{}", stopWatch.getTime());
    }
}
