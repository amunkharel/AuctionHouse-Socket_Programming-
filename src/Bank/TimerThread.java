package Bank;

public class TimerThread extends Thread {

    int counter = 0;

    boolean timeIsOver;

    public TimerThread(boolean timeIsOver) {
        this.timeIsOver = timeIsOver;
    }

    @Override
    public void run() {
        try {

            while (counter != 10) {
                Thread.sleep(1000);
                System.out.println(counter++);
            }

            if(counter >= 10) {
                timeIsOver = true;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
