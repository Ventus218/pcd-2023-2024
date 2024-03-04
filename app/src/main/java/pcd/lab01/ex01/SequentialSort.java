package pcd.lab01.ex01;

import java.util.*;

public class SequentialSort {

	static final int VECTOR_SIZE = 400000000;
	
	public static void main(String[] args) throws InterruptedException {
	
		log("Generating array...");
		int[] v = genArray(VECTOR_SIZE);
		int[] sortedV = Arrays.copyOfRange(v, 0, v.length);
		log("Array generated.");
		
		log("Sorting (" + VECTOR_SIZE + " elements)...");
		long t0 = System.nanoTime();
		Arrays.sort(sortedV, 0, sortedV.length);
		long t1 = System.nanoTime();
		log("Done. Time elapsed: " + ((t1 - t0) / 1000000) + " ms");

		log("Sorting (" + VECTOR_SIZE + " elements)...");

		t0 = System.nanoTime();
		var nProcessors = Runtime.getRuntime().availableProcessors();

		List<Thread> threads = new ArrayList<>();
		int[] itemsPerThread = new int[nProcessors];

		for (int i = 0; i < nProcessors; i++) {
			var proc = i;
			Thread th = new Thread(() -> {
				var nItems = (v.length + nProcessors) / nProcessors;
				int start = nItems * proc;
				int end = Math.min(start + nItems, v.length);

				itemsPerThread[proc] = end - start;

				Arrays.sort(v, start, end);
			});
			th.start();
			threads.add(th);
		}

		for (Thread th : threads) {
			th.join();
		}

		int mySortedV[] = new int[v.length];

		for (int i = 0; i < v.length; i++) {
			var min = Integer.MAX_VALUE;
			int minThreadIndex = 0;
			for (int threadIndex = 0; threadIndex < nProcessors; threadIndex++) {
				var remainingItems = itemsPerThread[threadIndex];
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
			itemsPerThread[minThreadIndex] = itemsPerThread[minThreadIndex] - 1;
			mySortedV[i] = min;
		}

		t1 = System.nanoTime();
		log("Done. Time elapsed: " + ((t1 - t0) / 1000000) + " ms");

		if (Arrays.equals(mySortedV, sortedV)) {
			System.out.println("Array was sorted correcly");
		} else {
			System.err.println("Array was not sorted correcly");
		}
	}

	private static int[] genArray(int n) {
		Random gen = new Random(System.currentTimeMillis());
		int v[] = new int[n];
		for (int i = 0; i < v.length; i++) {
			v[i] = gen.nextInt();
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
