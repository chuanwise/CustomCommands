package org.taixue.customcommands.script;

public class SleepScript extends Script {
    private int time = 0;
    @Override
    public void run() {
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
