package com.company.transactional.aspect;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
@Slf4j
public class CustomTransactionalAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @SneakyThrows
    @Around("@annotation(com.company.transactional.annotation.CustomTransactional)")
    public Object advice(ProceedingJoinPoint joinPoint) {
        entityManager.getTransaction().begin();

        try {
            Object result = joinPoint.proceed();
            entityManager.getTransaction().commit();
            return result;
        } catch (RuntimeException e) {
            entityManager.getTransaction().rollback();
            throw e;
        } catch (Throwable e) {
            entityManager.getTransaction().commit();
            throw e;
        }
    }
}
