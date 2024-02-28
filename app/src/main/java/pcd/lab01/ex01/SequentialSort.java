package pcd.lab01.ex01;

import java.util.*;

public class SequentialSort {

	static final int VECTOR_SIZE = 400000000;
	
	public static void main(String[] args) {
	
		log("Generating array...");
		long[] v = genArray(VECTOR_SIZE);

		log("Array generated.");
		log("Sorting (" + VECTOR_SIZE + " elements)...");

		long t0 = System.nanoTime();

		long[] sortedV = Arrays.copyOfRange(v, 0, v.length);
		Arrays.sort(sortedV, 0, sortedV.length);
		long t1 = System.nanoTime();
		log("Done. Time elapsed: " + ((t1 - t0) / 1000000) + " ms");

		log("Sorting (" + VECTOR_SIZE + " elements)...");

		t0 = System.nanoTime();
		var nProcessors = Runtime.getRuntime().availableProcessors() - 1;

		List<Thread> threads = new ArrayList<>();
		Map<Integer, Integer> itemsPerThread = new HashMap<>();

		for (int i = 0; i < nProcessors; i++) {
			var proc = i;
			Thread th = new Thread(() -> {
				var nItems = (v.length + nProcessors) / nProcessors;
				int start = nItems * proc;
				int end = Math.min(start + nItems, v.length);

				itemsPerThread.put(proc, end - start);

				Arrays.sort(v, start, end);
			});
			th.start();
			threads.add(th);
		}

		for (Thread th : threads) {
			th.join();
		}

		long mySortedV[] = new long[v.length];

		for (int i = 0; i < v.length; i++) {
			Long min = Long.MAX_VALUE;
			int minThreadIndex = 0;
			for (Integer threadIndex : itemsPerThread.keySet()) {
				var remainingItems = itemsPerThread.get(threadIndex);
				if (remainingItems > 0) {
					var nItems = (v.length + nProcessors) / nProcessors;
					int start = nItems * threadIndex;
					int end = Math.min(start + nItems, v.length);
					if (v[end - remainingItems] < min) {
						min = v[end - remainingItems];
						minThreadIndex = threadIndex;
					}
				}
			}
			itemsPerThread.replace(minThreadIndex, itemsPerThread.get(minThreadIndex) - 1);
			mySortedV[i] = min;
		}

		t1 = System.nanoTime();
		log("Done. Time elapsed: " + ((t1 - t0) / 1000000) + " ms");

		if (Arrays.equals(mySortedV, sortedV)) {
			System.out.println("Array was sorted correcly");
		} else {
			System.err.println("Array was not sorted correcly");
		}
		
		// dumpArray(sortedV);
		// System.out.println();
		// System.out.println();
		// dumpArray(mySortedV);
	}

	private static long[] genArray(int n) {
		Random gen = new Random(System.currentTimeMillis());
		long v[] = new long[n];
		for (int i = 0; i < v.length; i++) {
			v[i] = gen.nextLong();
		}
		return v;
	}

	private static void dumpArray(long[] v) {
		for (long l : v) {
			System.out.print(l + " ");
		}
	}

	private static void log(String msg) {
		System.out.println(msg);
	}
}
