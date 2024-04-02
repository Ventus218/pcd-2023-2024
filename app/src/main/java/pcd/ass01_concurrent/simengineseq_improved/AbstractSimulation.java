package pcd.ass01_concurrent.simengineseq_improved;

import java.util.ArrayList;
import java.util.List;

import pcd.ass01_concurrent.concurrent_components.SelfResettingBarrier;
import pcd.ass01_concurrent.concurrent_components.SplitLoadWorker;
import pcd.ass01_concurrent.concurrent_components.TaskQueue;

/**
 * Base class for defining concrete simulations
 * 
 */
public abstract class AbstractSimulation {

	/* environment of the simulation */
	private AbstractEnvironment env;

	/* list of the agents */
	private List<AbstractAgent> agents;

	/* simulation listeners */
	private List<SimulationListener> listeners;

	/* logical time step */
	private int dt;

	/* initial logical time */
	private int t0;

	/* in the case of sync with wall-time */
	private boolean toBeInSyncWithWallTime;
	private int nStepsPerSec;

	/* for time statistics */
	private long currentWallTime;
	private long startWallTime;
	private long endWallTime;
	private long averageTimePerStep;

	protected AbstractSimulation() {
		agents = new ArrayList<AbstractAgent>();
		listeners = new ArrayList<SimulationListener>();
		toBeInSyncWithWallTime = false;
	}

	/**
	 * 
	 * Method used to configure the simulation, specifying env and agents
	 * 
	 */
	protected abstract void setup();

	/**
	 * Method running the simulation for a number of steps,
	 * using a sequential approach
	 * 
	 * @param numSteps
	 */
	public void run(int numSteps) {

		startWallTime = System.currentTimeMillis();

		/* initialize the env and the agents inside */
		int t = t0;

		env.init();
		for (var a : agents) {
			a.init(env);
		}

		this.notifyReset(t, agents, env);

		long timePerStep = 0;
		int nSteps = 0;

		final int numberOfWorkers = 8;
		SelfResettingBarrier barrier = new SelfResettingBarrier(numberOfWorkers + 1); // Main thread will wait at the barrier too
		List<TaskQueue> taskQueues = new ArrayList<>();
		List<SplitLoadWorker> splitLoadWorkers = new ArrayList<>();

		for (int i = 0; i < numberOfWorkers; i++) {
			TaskQueue taskQueue = new TaskQueue();
			taskQueues.add(taskQueue);
			SplitLoadWorker worker = new SplitLoadWorker(taskQueue, barrier, "Worker " + i);
			splitLoadWorkers.add(worker);
			worker.start(); // The worker will start and wait for new tasks in the queue
		}
		
		var numberOfAgentsForWorker = (agents.size() + numberOfWorkers - 1) / (numberOfWorkers);

		while (nSteps < numSteps) {

			currentWallTime = System.currentTimeMillis();

			/* make a step */

			env.step(dt);

			/* clean the submitted actions */

			env.cleanActions();

			/* ask each agent to make a step */

			for (int i = 0; i < numberOfWorkers; i++) {
				var startIndex = i * numberOfAgentsForWorker;
				var endIndex = Math.min(startIndex + numberOfAgentsForWorker, agents.size());
				taskQueues.get(i).enqueueTask(() -> {
					for (int j = startIndex; j < endIndex; j++) {
						agents.get(j).step(dt);
					}
				});
			}

			try {
				barrier.waitForOthers();
			} catch (InterruptedException e) {
				e.printStackTrace();
				stopWorkers(splitLoadWorkers);
				System.exit(1);
			}

			t += dt;

			/* process actions submitted to the environment */

			env.processActions();

			notifyNewStep(t, agents, env);

			nSteps++;
			timePerStep += System.currentTimeMillis() - currentWallTime;

			if (toBeInSyncWithWallTime) {
				syncWithWallTime();
			}
		}

		stopWorkers(splitLoadWorkers);

		endWallTime = System.currentTimeMillis();
		this.averageTimePerStep = timePerStep / numSteps;

	}

	private void stopWorkers(List<SplitLoadWorker> splitLoadWorkers) {
		for (SplitLoadWorker splitLoadWorker : splitLoadWorkers) {
			splitLoadWorker.beginStopping();
		}
	}

	public long getSimulationDuration() {
		return endWallTime - startWallTime;
	}

	public long getAverageTimePerCycle() {
		return averageTimePerStep;
	}

	/* methods for configuring the simulation */

	protected void setupTimings(int t0, int dt) {
		this.dt = dt;
		this.t0 = t0;
	}

	protected void syncWithTime(int nCyclesPerSec) {
		this.toBeInSyncWithWallTime = true;
		this.nStepsPerSec = nCyclesPerSec;
	}

	protected void setupEnvironment(AbstractEnvironment env) {
		this.env = env;
	}

	protected void addAgent(AbstractAgent agent) {
		agents.add(agent);
	}

	/* methods for listeners */

	public void addSimulationListener(SimulationListener l) {
		this.listeners.add(l);
	}

	private void notifyReset(int t0, List<AbstractAgent> agents, AbstractEnvironment env) {
		for (var l : listeners) {
			l.notifyInit(t0, agents, env);
		}
	}

	private void notifyNewStep(int t, List<AbstractAgent> agents, AbstractEnvironment env) {
		for (var l : listeners) {
			l.notifyStepDone(t, agents, env);
		}
	}

	/* method to sync with wall time at a specified step rate */

	private void syncWithWallTime() {
		try {
			long newWallTime = System.currentTimeMillis();
			long delay = 1000 / this.nStepsPerSec;
			long wallTimeDT = newWallTime - currentWallTime;
			if (wallTimeDT < delay) {
				Thread.sleep(delay - wallTimeDT);
			}
		} catch (Exception ex) {
		}
	}
}
