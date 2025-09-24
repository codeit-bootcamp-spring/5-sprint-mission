package com.sprint.mission.discodeit.log;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

	@Pointcut("execution( * com.sprint.mission.discodeit.service.*.*(..)) ")
	public void serviceLayerPointcut(){}

	@Pointcut("execution( * com.sprint.mission.discodeit.controller.*.*(..)) ")
	public void controllerLayerPointcut(){}

	@Before("serviceLayerPointcut() || controllerLayerPointcut()")
	public void logBefore(JoinPoint joinPoint){
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();
		log.debug("===> {}.{}.{}", className, methodName, Arrays.toString(args));
	}

	@AfterReturning(pointcut = "serviceLayerPointcut() || controllerLayerPointcut()", returning = "result")
	public void logAfter(JoinPoint joinPoint, Object result){
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();
		log.debug("<=== {}.{}({}), return : {}", className, methodName, Arrays.toString(args), result);
	}

	@AfterThrowing(pointcut = "serviceLayerPointcut() || controllerLayerPointcut()", throwing = "e")
	public void logAfterThrowing(JoinPoint joinPoint, Exception e) {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		log.error("<=== {}.{}({})", className, methodName, e.getMessage(), e);
	}

	@Around("serviceLayerPointcut()")
	public Object logExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();
		String methodName = proceedingJoinPoint.getSignature().getName();

		long startTime = System.currentTimeMillis();

		try {
			Object result = proceedingJoinPoint.proceed();
			long endTime = System.currentTimeMillis();
			long executionTime = endTime - startTime;

			log.info("{}#{} 실행 시간: {}ms", className, methodName, executionTime);

			if (executionTime > 1000) {
				log.warn("[s2][LoggingAspect] {}#{} 실행 시간이 {}ms로 느립니다. 성능 최적화가 필요합니다.",
					className, methodName, executionTime);
			}

			return result;

		} catch (Throwable throwable) {
			long endTime = System.currentTimeMillis();
			long executionTime = endTime - startTime;

			log.error("{}#{} 실행 실패 - 실행 시간: {}ms, 예외: {}",
				className, methodName, executionTime, throwable.getMessage());
			throw throwable;
		}
	}
}
