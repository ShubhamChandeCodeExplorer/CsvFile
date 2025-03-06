package com.fullstack.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(LoggerAdvice.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Pointcut("execution(* com.fullstack..*(..)) && !execution(* com.fullstack.controller.ProductController.testUpload(..))")
    public void pointCut() {
        // Pointcut to log all methods under com.fullstack except specific exclusions
    }

    @Around("pointCut()")
    public Object applicationLogger(ProceedingJoinPoint pcp) throws Throwable {
        String methodName = pcp.getSignature().getName();
        String className = pcp.getTarget().getClass().getSimpleName();
        Object[] args = pcp.getArgs();

        // Serialize method arguments safely
        String serializedArgs = serializeObject(args);

        logger.info("Invoking method: {}.{} with arguments: {}", className, methodName, serializedArgs);

        Object result = pcp.proceed();

        // Serialize method return value safely
        String serializedResult = serializeObject(result);

        logger.info("Method: {}.{} returned: {}", className, methodName, serializedResult);

        return result;
    }

    private String serializeObject(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.warn("Failed to serialize object: {}", e.getMessage());
            return "[Unserializable Object]";
        }
    }
}
