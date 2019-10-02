package com.avanzarit.platform.saas.aws.util;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/** 
 * The CmwContext is used to manage logging and metrics throughout the system. It is completely aware of the component 
 * that it is running in due to the prefix, layer and component being specified on creation. 
 * <p> 
 * Next to being able to write metrics to CloudWatch its main purpose is writing logs in a structured
 * manner that can be later on used for indexing the logs to ElasticSearch.
 */ 
public class CmwContext {
    private static final Logger SEARCHLOGGER = LogManager.getLogger("search", new JsonMessageFactory());
 
    private static final String STATUS_CONFIRMATION = "CONFIRMATION"; 
    private static final String STATUS_INFO = "INFO"; 
    private static final String STATUS_DEBUG = "DEBUG"; 
    private static final String STATUS_DISCARDED = "DISCARDED"; 
    private static final String STATUS_WARNING = "WARNING"; 
    private static final String STATUS_ERROR = "ERROR"; 
    private static final int MAX_METRIC_CACHE_SIZE = 20; 
 
    private AmazonCloudWatch amazonCloudWatch; 
    private String prefix; 
    private String layer; 
    private String component; 
    private String awsRequestId; 
    private List<CmwMetric> metricCache; 
 
    /**
     * Creates a new context that is capable of writing metrics to CloudWatch
     * for the given prefix, layer, component and awsRequestId.
     */ 
 
    public CmwContext(AmazonCloudWatch amazonCloudWatch, String prefix, String layer, String component, 
                      String awsRequestId) { 
        verifyPrefix(prefix); 
 
        this.amazonCloudWatch = amazonCloudWatch; 
        this.prefix = prefix; 
        this.layer = layer; 
        this.component = component; 
        this.awsRequestId = awsRequestId; 
        this.metricCache = new ArrayList<>(); 
    } 
 
    /** 
     * Creates a new context fo the given prefix, layer, component and awsRequestId. 
     */ 
    public CmwContext(String prefix, String layer, String component, String awsRequestId) { 
        verifyPrefix(prefix); 
 
        this.prefix = prefix; 
        this.layer = layer; 
        this.component = component; 
        this.awsRequestId = awsRequestId; 
        this.metricCache = new ArrayList<>(); 
    } 
 
    /**
     * Creates a new context that is capable of writing metrics to CloudWatch
     * for the given prefix, layer and component.
     */ 
    public CmwContext(AmazonCloudWatch amazonCloudWatch, String prefix, String layer, String component) { 
        verifyPrefix(prefix); 
 
        this.amazonCloudWatch = amazonCloudWatch; 
        this.prefix = prefix; 
        this.layer = layer; 
        this.component = component; 
        this.metricCache = new ArrayList<>(); 
    } 
 
    /** 
     * Creates a new context fo the given prefix, layer and component. 
     */ 
    public CmwContext(String prefix, String layer, String component) { 
        verifyPrefix(prefix); 
 
        this.prefix = prefix; 
        this.layer = layer; 
        this.component = component; 
        this.metricCache = new ArrayList<>(); 
    } 
 
    private void verifyPrefix(String prefix) { 
        if (prefix == null) { 
            throw new CmwContextException("A prefix is required for setting up a context"); 
        } 
    } 
 
    public String getPrefix() { 
        return prefix; 
    } 
 
    public String getPrefixDashed() { 
        return prefix == null ? null : prefix.replace("_", "-"); 
    } 
 
    public String getLayer() { 
        return layer; 
    } 
 
    public String getComponent() { 
        return component; 
    } 
 
    public void setAwsRequestId(String awsRequestId) { 
        this.awsRequestId = awsRequestId; 
    } 
 
    public String getAwsRequestId() { 
        return awsRequestId; 
    } 
 
    public AmazonCloudWatch getAmazonCloudWatch() { 
        return amazonCloudWatch; 
    } 
 
    public List<CmwMetric> getMetricCache() { 
        return metricCache; 
    } 
 
    /** 
     * Logs a message and the metadata information describing the update with status confirmation. This status is used 
     * by the system to describe events in which an entity reached the end of a serializer. 
     */ 
    public void logConfirmation(UpdateInfo updateInfo, String message) { 
        log(updateInfo, message, STATUS_CONFIRMATION); 
    } 
 
    /** 
     * Logs an info message. 
     */ 
    public void logInfo(String message) { 
        log(new UpdateInfo(null, null, null), message, STATUS_INFO); 
    } 
 
    /** 
     * Logs an info message and the metadata information describing the update that caused it. 
     */ 
    public void logInfo(UpdateInfo updateInfo, String message) { 
        log(updateInfo, message, STATUS_INFO); 
    } 
 
    /** 
     * Logs a debug message. 
     */ 
    public void logDebug(String message) { 
        log(Level.DEBUG, new UpdateInfo(null, null, null), message, STATUS_DEBUG); 
    } 
 
    /** 
     * Logs a debug message and the metadata information describing the update that caused it. 
     */ 
    public void logDebug(UpdateInfo updateInfo, String message) { 
        log(Level.DEBUG, updateInfo, message, STATUS_DEBUG); 
    }

    /** 
     * Logs a message and the metadata information describing the update with status discarded. This is mostly used by 
     * the delta filtering functionality to describe that an update required no further processing because there where 
     * no changes. 
     */ 
    public void logDiscarded(UpdateInfo updateInfo, String message) { 
        log(updateInfo, message, STATUS_DISCARDED); 
    } 
 
    /** 
     * Logs a warning message. 
     */ 
    public void logWarning(String message) { 
        log(new UpdateInfo(null, null, null), message, STATUS_WARNING); 
    } 
 
    /** 
     * Logs a warning message and the metadata information describing the update that caused it. 
     */ 
    public void logWarning(UpdateInfo updateInfo, String message) { 
        log(updateInfo, message, STATUS_WARNING); 
    } 
 
    /** 
     * Logs an error message. 
     */ 
    public void logError(String message) { 
        log(new UpdateInfo(null, null, null), message, STATUS_ERROR); 
    } 
 
    /** 
     * Logs an exception. 
     */ 
    public void logError(Exception exception) { 
        logError(new UpdateInfo(null, null, null), exception); 
    } 
 
    /** 
     * Logs an error message and the associated exception. 
     */ 
    public void logError(String message, Exception exception) { 
        logError(new UpdateInfo(null, null, null), message, exception); 
    } 
 
    /** 
     * Logs the metadata information describing the update that caused an error and the associated exception. 
     */ 
    public void logError(UpdateInfo updateInfo, Exception exception) { 
        logError(updateInfo, null, exception); 
    } 
 
    /** 
     * Logs an error message and the metadata information describing the update that caused the error. 
     */ 
    public void logError(UpdateInfo updateInfo, String message) { 
        logError(updateInfo, message, null); 
    } 
 
    /** 
     * Logs an error message, the associated exception and the metadata information describing the update that caused 
     * the error. 
     */ 
    public void logError(UpdateInfo updateInfo, String message, Exception exception) { 
        String finalMessage = createErrorMessage(message, exception); 
 
        log(updateInfo, finalMessage, STATUS_ERROR); 
    } 
 
    /** 
     * Saves the given metrics into a temporary cache. As soon as the cache reaches {@value MAX_METRIC_CACHE_SIZE}, it 
     * is flushed automatically by a call to {@link #flushMetrics()}. 
     * 
     * @throws CmwContextException If no Amazon CloudWatch client is specified. 
     */ 
    public void putMetrics(CmwMetric... metrics) { 
        if (amazonCloudWatch != null) { 
            if (metrics != null) { 
                for (CmwMetric metric : metrics) { 
                    metricCache.add(metric); 
 
                    if (metricCache.size() == MAX_METRIC_CACHE_SIZE) { 
                        flushMetrics(); 
                    } 
                } 
            } 
        } else { 
            throw new CmwContextException("No AmazonCloudWatch client is configured"); 
        } 
    } 
 
    /** 
     * Sends all cached metric data to CloudWatch. 
     * 
     * @throws CmwContextException If no Amazon CloudWatch client is specified. 
     */ 
    public void flushMetrics() { 
        if (amazonCloudWatch != null) { 
            if (!metricCache.isEmpty()) { 
                PutMetricDataRequest putMetricDataRequest = new PutMetricDataRequest()
                        .withNamespace("AvanzarIT/SAAS/Platform/" + prefix)
                        .withMetricData(metricCache.stream()
                                        .map((metric) -> metric.createMetric(prefix, layer)) 
                                        .collect(Collectors.toList()) 
                        ); 
 
                amazonCloudWatch.putMetricData(putMetricDataRequest); 
 
                metricCache.clear(); 
            } 
        } else { 
            throw new CmwContextException("No AmazonCloudWatch client is configured"); 
        } 
    } 
 
    private String createErrorMessage(String message, Throwable throwable) { 
        StringBuilder finalMessage = new StringBuilder(); 
 
        if (StringUtils.isNotBlank(message)) { 
            finalMessage.append(message); 
        } 
 
        if (throwable != null) { 
            if (finalMessage.length() > 0) { 
                finalMessage.append(" - "); 
            } 
 
            finalMessage.append(throwable.getClass().getSimpleName()); 
 
            if (StringUtils.isNotBlank(throwable.getMessage())) { 
                finalMessage.append(": ").append(throwable.getMessage()); 
            } 
 
            if (finalMessage.length() > 0) { 
                finalMessage.append("\n"); 
            } 
 
            finalMessage.append(ExceptionUtils.getStackTrace(throwable)); 
        } 
 
        return finalMessage.toString(); 
    } 
 
    private void log(UpdateInfo updateInfo, String message, String status) { 
        log(Level.INFO, updateInfo, message, status); 
    } 
 
    private void log(Level logLevel, UpdateInfo updateInfo, String message, String status) { 
        LogEvent.LogType logType; 
 
        if (logLevel.isLessSpecificThan(Level.DEBUG)) { 
            logType = LogEvent.LogType.LOGTYPE_CWL_CLOUDWATCH; 
        } else { 
            logType = LogEvent.LogType.LOGTYPE_CWL_SEARCH; 
        }

        LogEvent event = new LogEvent(prefix, layer, component, updateInfo.getObjectId(), updateInfo.getContext(),
                updateInfo.getUpdateId(), message, status, new Date(), awsRequestId, logType);
 
        SEARCHLOGGER.log(logLevel, event); 
    } 
 
    @Override 
    public String toString() { 
        return "CmwContext{" 
                + "environment='" + prefix + '\'' 
                + ", layer='" + layer + '\'' 
                + ", component='" + component + '\'' 
                + ", awsRequestId='" + awsRequestId + '\'' 
                + '}'; 
    } 
}