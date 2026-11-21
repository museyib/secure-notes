package llc.devlab.app.audit;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditEventService;
    private final ClientIpProvider clientIpProvider;
    private final CurrentActorProvider currentActorProvider;

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer paramNameDiscoverer = new DefaultParameterNameDiscoverer();


    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String targetId = resolveTargetId(joinPoint, auditable.targetId());
        saveEventLog(auditable, targetId);
        return joinPoint.proceed();
    }

    private String resolveTargetId(ProceedingJoinPoint joinPoint, String targetIdExpression) {
        if (targetIdExpression == null || targetIdExpression.isBlank()) {
            return null;
        }
        // If it doesn't look like a SpEL expression, return the literal string
        if (!targetIdExpression.contains("#")) {
            return targetIdExpression;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                joinPoint.getTarget(), method, args, paramNameDiscoverer
        );

        Object value = parser.parseExpression(targetIdExpression).getValue(context);
        return value != null ? value.toString() : null;
    }

    private void saveEventLog(Auditable auditable, String targetId) {
        AuditEventRequest request = new AuditEventRequest(
                currentActorProvider.getCurrentActorId(),
                auditable.action(),
                auditable.riskLevel(),
                auditable.targetType(),
                targetId,
                clientIpProvider.getClientIp());
        auditEventService.logEvent(request);

    }
}
