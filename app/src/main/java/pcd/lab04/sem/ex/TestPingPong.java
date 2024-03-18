package pcd.lab04.sem.ex;

import java.util.concurrent.Semaphore;

/**
 * Unsynchronized version
 * 
 * @TODO make it sync 
 * @author aricci
 *
 */
public class TestPingPong {
	public static void main(String[] args) {
		Semaphore pingEvent = new Semaphore(0);
		Semaphore pongEvent = new Semaphore(0);
		new Pinger(pingEvent, pongEvent).start();
		new Ponger(pingEvent, pongEvent).start();
		
		pongEvent.release(); // Let Pinger start
	}

}
