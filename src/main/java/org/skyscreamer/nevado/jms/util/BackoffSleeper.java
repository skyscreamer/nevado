package org.skyscreamer.nevado.jms.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO - Description
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class BackoffSleeper {
    private final Log _log = LogFactory.getLog(getClass());

    private long _wait;
    private final long _minWait;
    private final long _maxWait;
    private final double _backoffMultiplier;

    public BackoffSleeper(long minWait, long maxWait, double backoffMultiplier) {
        if (minWait <= 0)
        {
            throw new IllegalArgumentException("Minimum wait must be at least 1 ms");
        }
        if (maxWait <= minWait)
        {
            throw new IllegalArgumentException("Maximum wait must be greater than minimum wait");
        }
        if (backoffMultiplier <= 1.0)
        {
            throw new IllegalArgumentException("Backoff multiplier must be greater than 1.0");
        }
        _minWait = minWait;
        _maxWait = maxWait;
        _backoffMultiplier = backoffMultiplier;
        reset();
    }

    public void reset() {
        _wait = _minWait;
    }

    public void sleep() {
        try {
            Thread.sleep(_wait);
        } catch (InterruptedException e) {
            _log.warn("Sleep interrupted", e);
        }
        if (_wait < _maxWait) {
            _wait = Math.round(_wait * _backoffMultiplier);
            if (_wait > _maxWait) {
                _wait = _maxWait;
            }
        }
    }
}
