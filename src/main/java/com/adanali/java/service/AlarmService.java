package com.adanali.java.service;

import com.adanali.java.model.Alarm;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.*;

public enum AlarmService {
    INSTANCE(5);

    private final ArrayBlockingQueue<Alarm> alarms;
    private final int MAX_ALARMS;
    Semaphore spots;
    Semaphore clientSpots;

    AlarmService(int maxAlarms){
        MAX_ALARMS = maxAlarms;
        alarms = new ArrayBlockingQueue<>(MAX_ALARMS);
        spots = new Semaphore(MAX_ALARMS);
        clientSpots = new Semaphore(MAX_ALARMS);
    }

    public void addAlarm(Alarm alarm){
            try {
                spots.acquire();
                if (alarm.getDateTime().isAfter(LocalDateTime.now())){
                    alarms.put(alarm);
                    System.out.println("Added "+alarm.getTitle()+" for : "+alarm.getDateTime());
                }
            }catch (InterruptedException e){
                System.out.println(e);
            }
    }

    public void startAlarming(){
        try {
            clientSpots.acquire();
            Alarm alarm = alarms.take();
            Duration durationUntilAlarm = Duration.between(LocalDateTime.now(),alarm.getDateTime());
            if (!durationUntilAlarm.isNegative()){
                Thread.sleep(durationUntilAlarm);
                System.out.println("Alarm Ringing : "+ alarm.getTitle());
            }
        } catch (InterruptedException e) {
            System.out.println(e);
        } finally {
            spots.release();
            clientSpots.release();
        }
    }
}
