package Bank;

import java.util.Timer;
import java.util.TimerTask;

public class timer {

    int secondsPassed = 0;

    boolean timeIsOver;

    public timer(boolean timeIsOver) {
        this.timeIsOver = timeIsOver;
    }

    Timer timerSecond = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            secondsPassed++;

            if(secondsPassed > 5) {
                setTimeIsOver(true);
            }
            System.out.println("Seconds passed: " + secondsPassed);
        }
    };

    public void setTimeIsOver(boolean timeIsOver) {
        this.timeIsOver = timeIsOver;
        timerSecond.cancel();
    }

    public void start() {
        timerSecond.scheduleAtFixedRate(task, 1000, 1000);
    }


}
