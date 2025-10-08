package szakdolgozat.project_tracking.utilities;

import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.sap.cds.ql.cqn.AnalysisResult;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnStructuredTypeRef;
import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.services.EventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.draft.DraftCancelEventContext;

@Component
public class EventContextAnalyzer {
    /**
     * Get the target keys for a Delete event
     *
     * @param context The event context to analyze
     * @return The targetKeys Map of the analysis result
     */
    public Map<String, Object> targetKeys(CdsDeleteEventContext context) {
        return createAnalyzer(context).analyze(context.getCqn()).targetKeys();
    }

    /**
     * Get the target keys for a DraftCancel event
     *
     * @param context The event context to analyze
     * @return The targetKeys Map of the analysis result
     */
    public Map<String, Object> targetKeys(DraftCancelEventContext context) {
        return targetKeys(context, ctx -> ctx.getCqn().ref());
    }

    /**
     * Get the target keys for a Read event
     *
     * @param context The event context to analyze
     * @return The targetKeys Map of the analysis result
     */
    public Map<String, Object> targetKeys(CdsReadEventContext context) {
        return targetKeys(context, ctx -> ctx.getCqn().ref());
    }

    /**
     * Creates a CqnAnalyzer for the given event context, analyzes the context with the created analyzer and returns the target keys.
     * The purpose of this method is that it's much easier to mock than the underlying implementation (and of course reusability).
     *
     * @param eventContext The event context to analyze
     * @param getCqnRef    Lambda to create a CqnStructuredTypeRef from the context. This is a parameter because there's no common ancestor that would enforce the .getCqn() method that is present on EventContext implementations
     * @param <T>          Type of the event context
     * @return The targetKeys Map of the analysis result
     */
    public <T extends EventContext> Map<String, Object> targetKeys(T eventContext, Function<T, ? extends CqnStructuredTypeRef> getCqnRef) {
        return analyze(eventContext, getCqnRef).targetKeys();
    }

    /**
     * Get the target values for a Delete event
     *
     * @param context The event context to analyze
     * @return The targetValues Map of the analysis result
     */
    public Map<String, Object> targetValues(CdsDeleteEventContext context) {
        return createAnalyzer(context).analyze(context.getCqn()).targetValues();
    }

    /**
     * Get the target values for a Read event
     *
     * @param context The event context to analyze
     * @return The targetValues Map of the analysis result
     */
    public Map<String, Object> targetValues(CdsReadEventContext context) {
        return createAnalyzer(context).analyze(context.getCqn()).targetValues();
    }

    /**
     * Creates a CqnAnalyzer for the given event context, analyzes the context with the created analyzer and returns the target entity.
     *
     * @param eventContext The event context to analyze
     * @param getCqnRef Lambda to create a CqnStructuredTypeRef from the context. This is a parameter because there's no common ancestor that would enforce the .getCqn() method that is present on EventContext implementations
     * @param <T> Type of the event context
     * @return The target CdsEntity
     */
    public <T extends EventContext> CdsEntity targetEntity(T eventContext, Function<T, ? extends CqnStructuredTypeRef> getCqnRef) {
        return analyze(eventContext, getCqnRef).targetEntity();
    }

    private static <T extends EventContext> AnalysisResult analyze(T eventContext, Function<T, ? extends CqnStructuredTypeRef> getCqnRef) {
        return createAnalyzer(eventContext).analyze(getCqnRef.apply(eventContext));
    }

    private static <T extends EventContext> CqnAnalyzer createAnalyzer(T eventContext) {
        return CqnAnalyzer.create(eventContext.getModel());
    }
}