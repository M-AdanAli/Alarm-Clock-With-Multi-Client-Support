import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.concurrent.*;

public enum AlarmClock {
    INSTANCE(5);

    private final ArrayBlockingQueue<Alarm> alarms;
    private final int MAX_ALARMS;

    AlarmClock(int maxAlarms){
        MAX_ALARMS = maxAlarms;
        alarms = new ArrayBlockingQueue<>(MAX_ALARMS);
    }

    public void addAlarm(Alarm alarm){
        synchronized (this){
            try {
                if (alarm.getDateTime().isAfter(LocalDateTime.now())){
                    alarms.put(alarm);
                    System.out.println("Added "+alarm.getTitle()+" for : "+alarm.getDateTime());
                }
            }catch (InterruptedException e){
                System.out.println(e);
            }
        }
    }

    public void startAlarming(){
        try {
            Alarm alarm = alarms.take();
            Duration durationUntilAlarm = Duration.between(LocalDateTime.now(),alarm.getDateTime());
            if (!durationUntilAlarm.isNegative()){
                Thread.sleep(durationUntilAlarm);
                System.out.println("Alarm Ringing : "+ alarm.getTitle());
            }
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
