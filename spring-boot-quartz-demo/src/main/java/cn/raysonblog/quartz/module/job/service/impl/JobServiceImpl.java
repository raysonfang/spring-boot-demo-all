package cn.raysonblog.quartz.module.job.service.impl;
import java.util.List;

import cn.raysonblog.quartz.base.service.impl.BaseServiceImpl;
import cn.raysonblog.quartz.module.job.entity.QuartzEntity;
import cn.raysonblog.quartz.module.job.mapper.JobMapper;
import cn.raysonblog.quartz.module.job.service.IJobService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class JobServiceImpl extends BaseServiceImpl<JobMapper, QuartzEntity> implements IJobService {

	@Autowired
	private Scheduler scheduler;

	@Override
	public List<QuartzEntity> listQuartzEntity(QuartzEntity quartzEntity) {
		return baseMapper.listQuartzEntity(quartzEntity);
	}

	@Override
	public Long listQuartzEntityCount(QuartzEntity quartzEntity) {
		return baseMapper.listQuartzEntityCount(quartzEntity);
	}

	/**
	 * 添加任务
	 * @param quartzEntity
	 * @return
	 */
	@Override
	public boolean addJob(QuartzEntity quartzEntity) {
		boolean ret = false;
		try {
			//获取Scheduler实例、废弃、使用自动注入的scheduler、否则spring的service将无法注入
			//Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			//如果是修改  展示旧的 任务
			if (quartzEntity.getOldJobGroup() != null) {
				JobKey key = new JobKey(quartzEntity.getOldJobName(), quartzEntity.getOldJobGroup());
				scheduler.deleteJob(key);
			}
			Class cls = Class.forName(quartzEntity.getJobClassName());
			cls.newInstance();
			//构建job信息
			JobDetail job = JobBuilder.newJob(cls).withIdentity(quartzEntity.getJobName(),
					quartzEntity.getJobGroup())
					.withDescription(quartzEntity.getDescription()).build();
			// 触发时间点
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartzEntity.getCronExpression());
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger" + quartzEntity.getJobName(), quartzEntity.getJobGroup())
					.startNow().withSchedule(cronScheduleBuilder).build();
			//交由Scheduler安排触发
			scheduler.scheduleJob(job, trigger);
			ret = true;
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("添加Job错误", e);
		}
		return ret;
	}

	/**
	 * 触发任务
	 * @param quartzEntity
	 * @return
	 */
	@Override
	public boolean triggerJob(QuartzEntity quartzEntity){
		boolean ret = false;
		try {
			JobKey key = new JobKey(quartzEntity.getJobName(),quartzEntity.getJobGroup());
			scheduler.triggerJob(key);
			ret = true;
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new RuntimeException("触发Job失败", e);
		}
		return ret;
	}

	/**
	 * 中断任务
	 * @param quartzEntity
	 * @return
	 */
	@Override
	public boolean pauseJob(QuartzEntity quartzEntity) {
		boolean ret = false;
		try {
			JobKey key = new JobKey(quartzEntity.getJobName(),quartzEntity.getJobGroup());
			scheduler.pauseJob(key);
			ret = true;
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new RuntimeException("暂停Job失败", e);
		}
		return ret;
	}

	/**
	 * 暂停任务
	 * @param quartzEntity
	 * @return
	 */
	public boolean resumeJob(QuartzEntity quartzEntity) {
		boolean ret = false;
		try {
			JobKey key = new JobKey(quartzEntity.getJobName(),quartzEntity.getJobGroup());
			scheduler.resumeJob(key);
			ret = true;
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new RuntimeException("暂停Job失败", e);
		}
		return ret;
	}

	/**
	 * 删除任务
	 * @param quartzEntity
	 * @return
	 */
	public boolean deleteJob(QuartzEntity quartzEntity) {
		boolean ret = false;
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(quartzEntity.getJobName(), quartzEntity.getJobGroup());
			// 停止触发器
			scheduler.pauseTrigger(triggerKey);
			// 移除触发器
			scheduler.unscheduleJob(triggerKey);
			// 删除任务
			scheduler.deleteJob(JobKey.jobKey(quartzEntity.getJobName(), quartzEntity.getJobGroup()));
			ret = true;
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new RuntimeException("暂停Job失败", e);
		}
		return ret;
	}

	/**
	 * 检查任务是否存在
	 * @param quartzEntity
	 * @return
	 */
	public boolean checkExistsJob(QuartzEntity quartzEntity) {
		boolean ret = false;
		try {
			JobKey key = new JobKey(quartzEntity.getJobName(),quartzEntity.getJobGroup());
			ret = scheduler.checkExists(key);
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new RuntimeException("暂停Job失败", e);
		}
		return ret;
	}
}
