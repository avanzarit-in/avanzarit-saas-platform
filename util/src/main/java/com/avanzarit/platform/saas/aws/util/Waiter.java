package com.avanzarit.platform.saas.aws.util;

import java.util.function.Supplier;

/**
 * The Waiter class provides utility methods that help with waiting until a certain condition
 * applies or does not apply.
 */
public class Waiter {
    private static final int UNTIL_MAX_TIME = 180000;
    private static final int UNTIL_INTERVAL = 250;
    private static final int NOT_EXPECTED_MAX_TIME = 30000;
    private static final int NOT_EXPECTED_INTERVAL = 1000;

    private long timeToWait;

    private Waiter(long timeToWait) {
        this.timeToWait = timeToWait;
    }

    /**
     * Waits until a certain condition applies or the time-out expires.
     */
    public void until(Supplier<Boolean> condition, String message) {
        int timeWaited = 0;

        while (!condition.get()) {
            try {
                if (timeWaited >= timeToWait) {
                    throw new WaiterTimeOutException(message);
                }

                Thread.sleep(UNTIL_INTERVAL);
                timeWaited += UNTIL_INTERVAL;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Waits until certain that a condition does not apply or the time-out has exceeded.
     */
    public void untilCertainThatConditionDoesNotApply(Supplier<Boolean> condition, String message) {
        int timeWaited = 0;

        while (!condition.get()) {
            try {
                if (timeWaited >= NOT_EXPECTED_MAX_TIME) {
                    return;
                }

                Thread.sleep(NOT_EXPECTED_INTERVAL);
                timeWaited += NOT_EXPECTED_INTERVAL;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        throw new WaiterConditionAppliedException(message);
    }

    /**
     * Factory method that instantiates a new {@link Waiter} object with a default wait time-out of
     * {@value UNTIL_MAX_TIME} milliseconds.
     */
    public static Waiter pause() {
        return new Waiter(UNTIL_MAX_TIME);
    }

    /**
     * Factory method that instantiates a new {@link Waiter} object with a configured wait
     * time-out.
     */
    public static Waiter pause(long timeToWait) {
        return new Waiter(timeToWait);
    }
}
