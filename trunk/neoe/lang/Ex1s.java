package neoe.lang;

public class Ex1s {
	static final int SIZE_PER_THREAD = 1000;
	static final int THREAD_CNT = 100000;

	public static class Th1 extends Thread {
		int total;
		byte[] data;
		int start;
		int cnt;

		public Th1(byte[] data, int start, int cnt) {
			this.data = data;
			this.start = start;
			this.cnt = cnt;
		}

		public void run() {
			int p = start;
			for (int i = 0; i < cnt; i++) {
				if (is7(p)) {
					data[i] = 0;
					total += 1;
				} else {
					data[i] = 1;
				}
			}
		}

		boolean is7(int p) {
			if (p % 7 == 0)
				return true;
			int v = p;
			while (true) {
				int v2 = v % 10;
				if (v2 == 7)
					return true;
				if (v < 10)
					break;
				v = v / 10;
			}
			return false;
		}
	}

	public static void main(String[] args) throws Exception {

		long t1 = System.currentTimeMillis();
		byte[] data = new byte[SIZE_PER_THREAD * THREAD_CNT];
		Th1[] ts = new Th1[THREAD_CNT];
		for (int i = 0; i < THREAD_CNT; i++) {
			ts[i] = new Th1(data, i * SIZE_PER_THREAD, SIZE_PER_THREAD);
			ts[i].run();
		}
		int total = 0;
		for (int i = 0; i < THREAD_CNT; i++) {
			//ts[i].join();
			total += ts[i].total;
		}
		System.out.println("res of " + THREAD_CNT * SIZE_PER_THREAD + "="
				+ total + " end " + (System.currentTimeMillis() - t1));
	}
}
