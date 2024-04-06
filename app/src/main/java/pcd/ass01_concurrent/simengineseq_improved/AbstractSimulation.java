package pcd.ass01_concurrent.simengineseq_improved;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pcd.ass01_concurrent.concurrent_components.SimulationEarlyStopper;
import pcd.ass01_concurrent.concurrent_components.SimulationPauser;
import pcd.ass01_concurrent.concurrent_components.SplitCyclicWorkloadMaster;

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

	private Optional<SimulationEarlyStopper> earlyStopper = Optional.empty();
	private Optional<SimulationPauser> pauser = Optional.empty();

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

	public void setEarlyStopper(SimulationEarlyStopper earlyStopper) {
		this.earlyStopper = Optional.ofNullable(earlyStopper);
	}

	public void setPauser(SimulationPauser pauser) {
		this.pauser = Optional.ofNullable(pauser);
	}

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
		for (AbstractAgent a : agents) {
			a.init(env);
		}

		this.notifyReset(t, agents, env);

		long timePerStep = 0;
		int nSteps = 0;

		final SplitCyclicWorkloadMaster master = new SplitCyclicWorkloadMaster(Optional.of(agents.size()),
				Optional.empty());
		
		// These tasks will be executed on every step. (can be instantiated just once here)
		List<Runnable> tasks = new ArrayList<>();
		for (AbstractAgent a : agents) {
			tasks.add(() -> {
				a.step(dt);
			});
		}

		boolean hasStopper = earlyStopper.isPresent();
		while (nSteps < numSteps && (!hasStopper || !earlyStopper.get().shouldStopSimulation())) {
			if (pauser.isPresent()) {
				try {
					pauser.get().waitWhilePaused();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			currentWallTime = System.currentTimeMillis();

			/* make a step */

			env.step(dt);

			/* clean the submitted actions */

			env.cleanActions();

			/* ask each agent to make a step */
			try {
				master.executeWorkload(tasks);
			} catch (InterruptedException e) {
				e.printStackTrace();
				master.workFinished();
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

		master.workFinished();

		endWallTime = System.currentTimeMillis();
		this.averageTimePerStep = timePerStep / numSteps;

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
		for (SimulationListener l : listeners) {
			l.notifyInit(t0, agents, env);
		}
	}

	private void notifyNewStep(int t, List<AbstractAgent> agents, AbstractEnvironment env) {
		for (SimulationListener l : listeners) {
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
