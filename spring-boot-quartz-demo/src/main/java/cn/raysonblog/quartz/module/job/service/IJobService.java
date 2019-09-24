package cn.raysonblog.quartz.module.job.service;

import java.util.List;

import cn.raysonblog.quartz.base.service.IBaseService;
import cn.raysonblog.quartz.module.job.entity.QuartzEntity;

public interface IJobService extends IBaseService<QuartzEntity> {

    List<QuartzEntity> listQuartzEntity(QuartzEntity quartzEntity);

    Long listQuartzEntityCount(QuartzEntity quartzEntity);

    boolean addJob(QuartzEntity quartzEntity);

    boolean triggerJob(QuartzEntity quartzEntity);

    boolean pauseJob(QuartzEntity quartzEntity);

    boolean resumeJob(QuartzEntity quartzEntity);

    boolean deleteJob(QuartzEntity quartzEntity);

    boolean checkExistsJob(QuartzEntity quartzEntity);
}
