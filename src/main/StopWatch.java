package main;

public class StopWatch {

	public StopWatch() {
		startTime = 0L;
		stopTime = 0L;
		running = false;
	}

	public void start() {
		startTime = System.currentTimeMillis();
		running = true;
	}

	public void stop() {
		stopTime = System.currentTimeMillis();
		running = false;
	}

	public long getElapsedTime() {
		long elapsed;
		if (running)
			elapsed = System.currentTimeMillis() - startTime;
		else
			elapsed = stopTime - startTime;
		return elapsed;
	}

	public long getElapsedTimeSecs() {
		long elapsed;
		if (running)
			elapsed = (System.currentTimeMillis() - startTime) / 1000L;
		else
			elapsed = (stopTime - startTime) / 1000L;
		return elapsed;
	}

	private long startTime;
	private long stopTime;
	private boolean running;
}
