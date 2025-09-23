package com.sprint.mission.discodeit.log;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Pointcut("execution(* com.sprint.mission.discodeit.service.basic.BasicUserService.create(..))"
      + "|| execution(* com.sprint.mission.discodeit.service.basic.BasicUserService.update(..))"
      + "|| execution(* com.sprint.mission.discodeit.service.basic.BasicUserService.delete(..))")
  public void userServicePointcut() {
  }

  @Pointcut("execution(* com.sprint.mission.discodeit.service.basic.BasicChannelService.create(..))"
      + "|| execution(* com.sprint.mission.discodeit.service.basic.BasicChannelService.update(..))"
      + "|| execution(* com.sprint.mission.discodeit.service.basic.BasicChannelService.delete(..))")
  public void channelServicePointcut() {
  }

  @Pointcut("execution(* com.sprint.mission.discodeit.service.basic.BasicMessageService.create(..))"
      + "|| execution(* com.sprint.mission.discodeit.service.basic.BasicMessageService.update(..))"
      + "|| execution(* com.sprint.mission.discodeit.service.basic.BasicMessageService.delete(..))")
  public void messageServicePointcut() {
  }

  @Pointcut(
      "execution(* com.sprint.mission.discodeit.storage.local.LocalBinaryContentStorage.put(..))"
          + "|| execution(* com.sprint.mission.discodeit.storage.local.LocalBinaryContentStorage.download(..))")
  public void BinaryContentPointcut() {
  }

  @Before("userServicePointcut() || channelServicePointcut()"
      + " || messageServicePointcut() || BinaryContentPointcut()")
  public void logBefore(JoinPoint joinPoint) {
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();
    log.debug("===> {}.{}({})", className, methodName, Arrays.toString(args));
  }

  @AfterReturning(pointcut = "userServicePointcut() || channelServicePointcut()"
      + " || messageServicePointcut() || BinaryContentPointcut()", returning = "result")
  public void logAfter(JoinPoint joinPoint, Object result) {
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();
    log.debug("<=== {}.{}({}), return : {}", className, methodName, Arrays.toString(args), result);
  }
}
