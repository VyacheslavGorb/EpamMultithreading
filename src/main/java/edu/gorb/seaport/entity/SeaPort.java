package edu.gorb.seaport.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SeaPort {
    private static final Logger logger = LogManager.getLogger();
    private static SeaPort instance;
    private static final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static final double MAX_LOAD_FACTOR = 0.75;
    private static final double MIN_LOAD_FACTOR = 0.25;
    private Deque<Pier> freePiers = new ArrayDeque<>();
    private Deque<Pier> busyPiers = new ArrayDeque<>();
    private Lock pierLocking = new ReentrantLock(true);
    private Condition freePierCondition = pierLocking.newCondition();
    private Lock containerStorageLocking = new ReentrantLock(true);
    private Condition unloadAvailableCondition = containerStorageLocking.newCondition();
    private Condition loadAvailableCondition = containerStorageLocking.newCondition();
    private final int TIMER_MILLISECONDS_DELAY = 500;
    private final int TIMER_MILLISECONDS_INTERVAL = 200;
    private final int PIER_COUNT;
    private final int CAPACITY;
    private int currentContainerAmount;
    private int waitingForLoadAmount;
    private int waitingForUnloadAmount;


    private SeaPort() {
        //TODO Read from file
        CAPACITY = 5;
        currentContainerAmount = 4;
        PIER_COUNT = 7;
        for (int i = 0; i < PIER_COUNT; i++) {
            freePiers.addLast(new Pier());
        }
        setTrainTask();
    }

    public static SeaPort getInstance() {
        while (instance == null) {
            if (isInitialized.compareAndSet(false, true)) {
                instance = new SeaPort();
            }
        }
        return instance;
    }

    public Pier obtainPier() {
        logger.log(Level.INFO, "Start obtaining pier");
        try {
            pierLocking.lock();
            try {
                if (freePiers.isEmpty()) {
                    logger.log(Level.INFO, "Waiting for pier");
                    freePierCondition.await();
                }
            } catch (InterruptedException e) {
                logger.log(Level.ERROR, "Error while obtaining pier: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
            Pier pier = freePiers.removeLast();
            busyPiers.addLast(pier);
            logger.log(Level.INFO, "Obtained pier {}", pier.getPierId());
            return pier;
        } finally {
            pierLocking.unlock();
        }
    }

    public void releasePier(Pier pier) {
        try {
            pierLocking.lock();
            busyPiers.remove(pier);
            freePiers.addLast(pier);
            freePierCondition.signal();
            logger.log(Level.INFO, "Pier released");
        } finally {
            pierLocking.unlock();
        }
    }

    public void loadContainer() {
        try {
            containerStorageLocking.lock();
            logger.log(Level.INFO, "Start load");
            if (currentContainerAmount == 0) {
                try {
                    logger.log(Level.DEBUG, "Waiting for load");
                    waitingForLoadAmount++;
                    loadAvailableCondition.await();
                    waitingForLoadAmount--;
                } catch (InterruptedException e) {
                    logger.log(Level.ERROR, "Error while loading container: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
            currentContainerAmount--;
            logger.log(Level.INFO, "Complete load, container amount: {}", currentContainerAmount);
        } finally {
            containerStorageLocking.unlock();
        }
    }

    public void unloadContainer() {
        try {
            containerStorageLocking.lock();
            logger.log(Level.INFO, "Start unload");
            if (currentContainerAmount == CAPACITY) {
                try {
                    logger.log(Level.DEBUG, "Waiting for unload");
                    waitingForUnloadAmount++;
                    unloadAvailableCondition.await();
                    waitingForUnloadAmount--;
                } catch (InterruptedException e) {
                    logger.log(Level.ERROR, "Error while unloading container: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
            currentContainerAmount++;
            logger.log(Level.INFO, "Complete unload, container amount: {}", currentContainerAmount);
        } finally {
            containerStorageLocking.unlock();
        }
    }

    private void setTrainTask() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    containerStorageLocking.lock();
                    double currentLoad = (double) currentContainerAmount / CAPACITY;

                    if (currentLoad < MIN_LOAD_FACTOR) {
                        currentContainerAmount += (int) (MIN_LOAD_FACTOR * CAPACITY + 1);
                    } else if (currentLoad > MAX_LOAD_FACTOR) {
                        currentContainerAmount -= (int) (MIN_LOAD_FACTOR * CAPACITY + 1);
                    }

                    int loadSignalCount = Math.min(currentContainerAmount, waitingForLoadAmount);
                    for (int i = 0; i < loadSignalCount; i++) {
                        loadAvailableCondition.signal();
                    }

                    int unloadSignalCount = Math.min(CAPACITY - currentContainerAmount, waitingForUnloadAmount);
                    for (int i = 0; i < unloadSignalCount; i++) {
                        unloadAvailableCondition.signal();
                    }

                    logger.log(Level.DEBUG, "Timer\n");
                } finally {
                    containerStorageLocking.unlock();
                }
            }
        }, TIMER_MILLISECONDS_DELAY, TIMER_MILLISECONDS_INTERVAL);
    }
}
