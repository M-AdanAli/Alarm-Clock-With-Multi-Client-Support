void main() throws InterruptedException {
    for (int i = 0; i < 10; i++) {
        int index = i;
        Thread thread = new Thread(() -> {
            Alarm alarm = new Alarm(LocalDateTime.now().plusSeconds(index+10),"Alarm "+index);
            AlarmClock.INSTANCE.addAlarm(alarm);
        });
        thread.start();
        Thread.sleep(100);
    }

    Thread.sleep(1000);
    while (true){
        AlarmClock.INSTANCE.startAlarming();
    }
}