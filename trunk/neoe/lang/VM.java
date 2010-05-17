package neoe.lang;

import static neoe.lang.Consts.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VM {
	static final boolean DEBUG = false;
	static boolean useNative = false;

	interface Run {
		void run();
	}

	static class Proc {
		public Proc(Proc parentProc, int startIp, int localSize) {
			Proc p = this;
			p.parent = parentProc;
			p.curIp = startIp + 1;
			p.startIp = startIp;
			p.status = NEW;
			p.pdata = new int[localSize];
			p.pdatao = new Object[localSize];
		}

		public String toString() {
			return "[proc" + startIp + ":"
					+ Integer.toString(hashCode(), Character.MAX_RADIX) + "]";
		}

		// ip + 1, ip, 0, parentProc, -1
		int curIp;
		int startIp;
		int status;
		Proc parent;
		Proc resume;
		int[] pdata;
		Object[] pdatao;
		Proc next;
		Proc prev;
	}

	public class Instr {

		int[] cs;
		int[] pd;
		Object[] pdo;

		void ADD_RIR() {
			pd[cs[p3]] = pd[cs[p1]] + cs[p2];
		}

		void ADD_RRR() {
			pd[cs[p3]] = pd[cs[p1]] + pd[cs[p2]];
		}

		void SUB_RRR() {
			pd[cs[p3]] = pd[cs[p1]] - pd[cs[p2]];
		}

		void SUB_IRR() {
			pd[cs[p3]] = cs[p1] - pd[cs[p2]];
		}

		void SUB_RIR() {
			pd[cs[p3]] = pd[cs[p1]] - cs[p2];
		}

		void MUL_RIR() {
			pd[cs[p3]] = pd[cs[p1]] * cs[p2];
		}

		void MUL_RRR() {
			pd[cs[p3]] = pd[cs[p1]] * pd[cs[p2]];
		}

		void DIV_RRR() {
			pd[cs[p3]] = pd[cs[p1]] / pd[cs[p2]];
		}

		void DIV_IRR() {
			pd[cs[p3]] = cs[p1] / pd[cs[p2]];
		}

		void DIV_RIR() {
			pd[cs[p3]] = pd[cs[p1]] / cs[p2];
		}

		void MOD_RRR() {
			pd[cs[p3]] = pd[cs[p1]] % pd[cs[p2]];
		}

		void MOD_IRR() {
			pd[cs[p3]] = cs[p1] % pd[cs[p2]];
		}

		void MOD_RIR() {
			pd[cs[p3]] = pd[cs[p1]] % cs[p2];
		}

		void JE_RII() {
			if (pd[cs[p1]] == cs[p2]) {
				cp.curIp = cs[p3];
			} else
				cp.curIp++;
		}

		void JE_RRI() {
			if (pd[cs[p1]] == pd[cs[p2]]) {
				cp.curIp = cs[p3];
			} else
				cp.curIp++;
		}

		void JNE_RII() {
			if (pd[cs[p1]] != cs[p2]) {
				cp.curIp = cs[p3];
			} else
				cp.curIp++;
		}

		void JNE_RRI() {
			if (pd[cs[p1]] != pd[cs[p2]]) {
				cp.curIp = cs[p3];
			} else
				cp.curIp++;
		}

		void JL_RII() {
			if (pd[cs[p1]] < cs[p2]) {
				cp.curIp = cs[p3];
			} else
				cp.curIp++;
		}

		void JL_RRI() {
			if (pd[cs[p1]] < pd[cs[p2]]) {
				cp.curIp = cs[p3];
			} else
				cp.curIp++;
		}

		void JLE_RII() {
			if (pd[cs[p1]] <= cs[p2]) {
				cp.curIp = cs[p3];
			} else
				cp.curIp++;
		}

		void JLE_RRI() {
			if (pd[cs[p1]] <= pd[cs[p2]]) {
				cp.curIp = cs[p3];
			} else
				cp.curIp++;
		}

		void JG_RII() {
			if (pd[cs[p1]] > cs[p2]) {
				cp.curIp = cs[p3];
			} else
				cp.curIp++;
		}

		void JG_RRI() {
			if (pd[cs[p1]] > pd[cs[p2]]) {
				cp.curIp = cs[p3];
			} else
				cp.curIp++;
		}

		void JGE_RII() {
			if (pd[cs[p1]] >= cs[p2]) {
				cp.curIp = cs[p3];
			} else
				cp.curIp++;
		}

		void JGE_RRI() {
			if (pd[cs[p1]] >= pd[cs[p2]]) {
				cp.curIp = cs[p3];
			} else
				cp.curIp++;
		}

		void MOV_RR() {
			pd[cs[p2]] = pd[cs[p1]];
			pdo[cs[p2]] = pdo[cs[p1]];
		}

		void MOV_IR() {
			pd[cs[p2]] = cs[p1];
		}

		void RET_() {
			cp.status = END;

			removeFromRunning(cp);
			if (DEBUG)
				debug("[proc]END " + cp);

			if (cp.resume != null) {
				resume(cp.resume);
			}
		}

		void TIME_R() {
			long t1 = System.currentTimeMillis();
			pd[cs[p1]] = (int) (t1 & 0xffffffff);
			pd[cs[p1] + 1] = (int) (t1 >> 32);
		}

		void NEWARR_BYTE_IR() {
			byte[] bs = new byte[cs[p1]];
			pdo[cs[p2]] = bs;
		}

		void NEWARR_BYTE_RR() {
			byte[] bs = new byte[pd[cs[p1]]];
			pdo[cs[p2]] = bs;
		}

		void OUT_R() {
			System.out.println(pd[cs[p1]]);
		}

		void OUT_I() {
			System.out.println(cs[p1]);
		}

		void GETPROCVAL_RIR() {
			Proc proc2 = ((Proc) pdo[cs[p1]]);
			pd[cs[p3]] = proc2.pdata[cs[p2]];
			pdo[cs[p3]] = proc2.pdatao[cs[p2]];
		}

		void GETPROCVAL_RRR() {
			Proc proc2 = ((Proc) pdo[cs[p1]]);
			pd[cs[p3]] = proc2.pdata[pd[cs[p2]]];
			pdo[cs[p3]] = proc2.pdatao[pd[cs[p2]]];
		}

		void SETPROCVAL_RRR() {
			Proc proc2 = ((Proc) pdo[cs[p1]]);
			proc2.pdata[pd[cs[p2]]] = pd[cs[p3]];
			proc2.pdatao[pd[cs[p2]]] = pdo[cs[p3]];
		}

		void SETPROCVAL_RRI() {
			int[] pd2 = ((Proc) pdo[cs[p1]]).pdata;
			pd2[pd[cs[p2]]] = cs[p3];
		}

		void SETPROCVAL_RII() {
			int[] pd2 = ((Proc) pdo[cs[p1]]).pdata;
			pd2[cs[p2]] = cs[p3];
		}

		void SETPROCVAL_RIR() {
			Proc proc2 = ((Proc) pdo[cs[p1]]);
			proc2.pdata[cs[p2]] = pd[cs[p3]];
			proc2.pdatao[cs[p2]] = pdo[cs[p3]];
		}

		protected void _set(int[] command) {
			this.cs = command;
			this.pd = cp.pdata;
			this.pdo = cp.pdatao;
		}

		public void NEWARR_INT_IR() {
			int[] bs = new int[cs[p1]];
			pdo[cs[p2]] = bs;
		}

		public void NEWPROCARR_IR() {
			Object[] bs = new Object[cs[p1]];
			pdo[cs[p2]] = bs;
		}

		public void NEWPROC_IR() {
			Proc p = newproc(cp, cs[p1]);
			pdo[cs[p2]] = p;
		}

		public void DELPROC_R() {
			Proc pid = (Proc) pdo[cs[p1]];
			pid.resume = null;
		}

		public void STARTPROC_R() {
			Proc pid = (Proc) pdo[cs[p1]];
			pid.status = RUN;
			addToRunning(pid);
			if (DEBUG)
				debug("[proc]start " + pid);
		}

		public void INC_R() {
			pd[cs[p1]] = pd[cs[p1]] + 1;
		}

		public void RUNPROC_R() {
			Proc pid = (Proc) pdo[cs[p1]];
			pid.status = RUN;
			pid.resume = cp;
			cp.status = PAUSE;
			addToRunning(pid);
			removeFromRunning(cp);
			if (DEBUG)
				debug("[proc]run " + pid + " pause " + cp);
		}

		public void JMP_I() {
			cp.curIp = cs[p1];
		}

		public void PROCJOIN_R() {
			Proc pid = (Proc) pdo[cs[p1]];
			if (pid.status != END) {
				pid.status = RUN;
				pid.resume = cp;
				cp.status = PAUSE;
				removeFromRunning(cp);
				if (DEBUG)
					debug("[proc]wait " + pid + " in " + cp);
			}
		}

		void GETARR_INT_RRR() {
			int[] bs = (int[]) pdo[cs[p1]];
			pd[cs[p3]] = bs[pd[cs[p2]]];
		}

		void GETARR_INT_RIR() {
			int[] bs = (int[]) pdo[cs[p1]];
			pd[cs[p3]] = bs[cs[p2]];
		}

		void SETARR_INT_RRR() {
			int[] bs = (int[]) pdo[cs[p1]];
			bs[pd[cs[p2]]] = pd[cs[p3]];
		}

		void SETARR_INT_RIR() {
			int[] bs = (int[]) pdo[cs[p1]];
			bs[cs[p2]] = pd[cs[p3]];
		}

		void SETARR_INT_RRI() {
			int[] bs = (int[]) pdo[cs[p1]];
			// if (DEBUG) debug(bs.length + "," + pd[cs[p2]] + "," + cs[p3]);
			bs[pd[cs[p2]]] = cs[p3];
		}

		void SETARR_INT_RII() {
			int[] bs = (int[]) pdo[cs[p1]];
			bs[cs[p2]] = cs[p3];
		}

		void GETPROCARR_RRR() {
			Object[] bs = (Object[]) pdo[cs[p1]];
			pdo[cs[p3]] = (Proc) bs[pd[cs[p2]]];
		}

		void GETPROCARR_RIR() {
			Object[] bs = (Object[]) pdo[cs[p1]];
			pdo[cs[p3]] = (Proc) bs[cs[p2]];
		}

		void SETPROCARR_RRR() {
			Object[] bs = (Object[]) pdo[cs[p1]];
			bs[pd[cs[p2]]] = pdo[cs[p3]];
		}

		void SETPROCARR_RIR() {
			Object[] bs = (Object[]) pdo[cs[p1]];
			bs[cs[p2]] = pdo[cs[p3]];
		}

		void SETPROCARR_RRI() {
			Object[] bs = (Object[]) pdo[cs[p1]];
			// if (DEBUG) debug(bs.length + "," + pd[cs[p2]] + "," + cs[p3]);
			bs[pd[cs[p2]]] = cs[p3];
		}

		void SETPROCARR_RII() {
			Object[] bs = (Object[]) pdo[cs[p1]];
			bs[cs[p2]] = cs[p3];
		}

		void GETARR_BYTE_RRR() {
			byte[] bs = (byte[]) (pdo[cs[p1]]);
			pd[cs[p3]] = bs[pd[cs[p2]]];
		}

		void GETARR_BYTE_RIR() {
			byte[] bs = (byte[]) (pdo[cs[p1]]);
			pd[cs[p3]] = bs[cs[p2]];
		}

		void SETARR_BYTE_RRR() {
			byte[] bs = (byte[]) (pdo[cs[p1]]);
			bs[pd[cs[p2]]] = (byte) pd[cs[p3]];
			System.out.println("bs[" + pd[cs[p1]] + "]=" + dumpArr(bs));
		}

		void SETARR_BYTE_RIR() {
			byte[] bs = (byte[]) (pdo[cs[p1]]);
			bs[cs[p2]] = (byte) pd[cs[p3]];
		}

		void SETARR_BYTE_RRI() {
			byte[] bs = (byte[]) (pdo[cs[p1]]);
			// if (DEBUG) debug(bs.length + "," + pd[cs[p2]] + "," + cs[p3]);
			bs[pd[cs[p2]]] = (byte) cs[p3];
		}

		void SETARR_BYTE_RII() {
			byte[] bs = (byte[]) (pdo[cs[p1]]);
			bs[cs[p2]] = (byte) cs[p3];
		}

		Run[] _r;

		Instr() {
			_r = new Run[] { new Run() {
				public void run() {
					ADD_RIR();
				}
			}, new Run() {
				public void run() {
					ADD_RRR();
				}
			}, new Run() {
				public void run() {
					DIV_IRR();
				}
			}, new Run() {
				public void run() {
					DIV_RIR();
				}
			}, new Run() {
				public void run() {
					DIV_RRR();
				}
			}, new Run() {
				public void run() {
					GETARR_BYTE_RIR();
				}
			}, new Run() {
				public void run() {
					GETARR_BYTE_RRR();
				}
			}, new Run() {
				public void run() {
					GETARR_INT_RIR();
				}
			}, new Run() {
				public void run() {
					GETARR_INT_RRR();
				}
			}, new Run() {
				public void run() {
					GETPROCVAL_RIR();
				}
			}, new Run() {
				public void run() {
					GETPROCVAL_RRR();
				}
			}, new Run() {
				public void run() {
					INC_R();
				}
			}, new Run() {
				public void run() {
					JE_RII();
				}
			}, new Run() {
				public void run() {
					JE_RRI();
				}
			}, new Run() {
				public void run() {
					JG_RII();
				}
			}, new Run() {
				public void run() {
					JG_RRI();
				}
			}, new Run() {
				public void run() {
					JGE_RII();
				}
			}, new Run() {
				public void run() {
					JGE_RRI();
				}
			}, new Run() {
				public void run() {
					JL_RII();
				}
			}, new Run() {
				public void run() {
					JL_RRI();
				}
			}, new Run() {
				public void run() {
					JLE_RII();
				}
			}, new Run() {
				public void run() {
					JLE_RRI();
				}
			}, new Run() {
				public void run() {
					JMP_I();
				}
			}, new Run() {
				public void run() {
					JNE_RII();
				}
			}, new Run() {
				public void run() {
					JNE_RRI();
				}
			}, new Run() {
				public void run() {
					PROCJOIN_R();
				}
			}, new Run() {
				public void run() {
					MOD_IRR();
				}
			}, new Run() {
				public void run() {
					MOD_RIR();
				}
			}, new Run() {
				public void run() {
					MOD_RRR();
				}
			}, new Run() {
				public void run() {
					MOV_IR();
				}
			}, new Run() {
				public void run() {
					MOV_RR();
				}
			}, new Run() {
				public void run() {
					MUL_RIR();
				}
			}, new Run() {
				public void run() {
					MUL_RRR();
				}
			}, new Run() {
				public void run() {
					NEWARR_BYTE_IR();
				}
			}, new Run() {
				public void run() {
					NEWARR_BYTE_RR();
				}
			}, new Run() {
				public void run() {
					NEWARR_INT_IR();
				}
			}, new Run() {
				public void run() {
					NEWPROC_IR();
				}
			}, new Run() {
				public void run() {
					OUT_I();
				}
			}, new Run() {
				public void run() {
					OUT_R();
				}
			}, new Run() {
				public void run() {
					RET_();
				}
			}, new Run() {
				public void run() {
					RUNPROC_R();
				}
			}, new Run() {
				public void run() {
					SETARR_BYTE_RII();
				}
			}, new Run() {
				public void run() {
					SETARR_BYTE_RIR();
				}
			}, new Run() {
				public void run() {
					SETARR_BYTE_RRI();
				}
			}, new Run() {
				public void run() {
					SETARR_BYTE_RRR();
				}
			}, new Run() {
				public void run() {
					SETARR_INT_RII();
				}
			}, new Run() {
				public void run() {
					SETARR_INT_RIR();
				}
			}, new Run() {
				public void run() {
					SETARR_INT_RRI();
				}
			}, new Run() {
				public void run() {
					SETARR_INT_RRR();
				}
			}, new Run() {
				public void run() {
					SETPROCVAL_RII();
				}
			}, new Run() {
				public void run() {
					SETPROCVAL_RIR();
				}
			}, new Run() {
				public void run() {
					SETPROCVAL_RRI();
				}
			}, new Run() {
				public void run() {
					SETPROCVAL_RRR();
				}
			}, new Run() {
				public void run() {
					STARTPROC_R();
				}
			}, new Run() {
				public void run() {
					SUB_IRR();
				}
			}, new Run() {
				public void run() {
					SUB_RIR();
				}
			}, new Run() {
				public void run() {
					SUB_RRR();
				}
			}, new Run() {
				public void run() {
					TIME_R();
				}
			}, null, new Run() {
				public void run() {
					DELPROC_R();
				}
			}, new Run() {
				public void run() {
					NEWPROCARR_IR();
				}
			}, new Run() {
				public void run() {
					SETPROCARR_RII();
				}
			}, new Run() {
				public void run() {
					SETPROCARR_RIR();
				}
			}, new Run() {
				public void run() {
					SETPROCARR_RRI();
				}
			}, new Run() {
				public void run() {
					SETPROCARR_RRR();
				}
			}, new Run() {
				public void run() {
					GETPROCARR_RIR();
				}
			}, new Run() {
				public void run() {
					GETPROCARR_RRR();
				}
			}, };
		}

		void _run(int i) {
			_r[i].run();
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("useNative=" + useNative);
		if (useNative)
			System.loadLibrary("NativeVm");
		new VM().load(new File("Ex1.bin")).mainLoop();
	}

	public void resume(Proc pid) {
		pid.status = RUN;
		addToRunning(pid);
		if (DEBUG)
			debug("[proc]resume " + pid);
	}

	private BufferedInputStream in;
	private int current;
	private Proc running = new Proc(null, 0, 0);
	private Proc cp;

	void mainLoop() {

		long t1 = System.currentTimeMillis();
		if (useNative) {
			mainLoopNative(code, entrypoint);
		} else {
			Proc i = running.next;
			while (true) {
				int size = runCnt;
				if (size == 0)
					break;
				step(i);
				if (i.next == null)
					i = running.next;
				else
					i = i.next;
			}
		}
		System.out.println((System.currentTimeMillis() - t1) + " ms");
		System.out.println("all proc end, VM end.");
	}

	native void mainLoopNative(List<int[]> data, int entrypoint);

	void step(Proc pid) {
		cp = pid;
		int[] command = code.get(pid.curIp);
		doCommand(command);
	}

	private void error(String s) {
		System.err.println(s);
		System.exit(1);
	}

	Instr instr = new Instr();
	private int cycle;
	int CMD = 1;

	void doCommand(int[] cs) {

		instr._set(cs);
		String cmd = Consts.CODE_NAME[cs[CMD]];

		if (DEBUG)
			debug((cycle++) + "[" + cp + "]do " + cmd + " " + dumpArr(cs)
					+ " | " + dumpArr(cp.pdata) + "|" + dumpArr(cp.pdatao));
		boolean d = true;
		boolean j = false;
		instr._run(cs[CMD]);
		if (d && cmd.startsWith("J")) {
			d = false;
			j = true;
		}
		if (!d && !j) {
			error("not done:" + dumpArr(cs) + "|" + "dumpArr(pd)");
		}
		if (d)
			cp.curIp++;
	}

	private String dumpArr(int[] r) {
		StringBuffer sb = new StringBuffer();
		for (int x : r) {
			sb.append(x);
			sb.append(" ");
		}
		// sb.append("\n");
		return sb.toString();
	}

	private String dumpArr(Object[] r) {
		StringBuffer sb = new StringBuffer();
		for (Object x : r) {
			sb.append(x);
			sb.append(" ");
		}
		// sb.append("\n");
		return sb.toString();
	}

	private String dumpArr(byte[] r) {
		StringBuffer sb = new StringBuffer();
		for (byte x : r) {
			sb.append(x);
			sb.append(" ");
		}
		// sb.append("\n");
		return sb.toString();
	}

	Proc newproc(Proc parentProc, int ip) {
		if (code.get(ip)[1] != DEFPROC_III) {
			System.err.println("not a proc entry " + ip);
			return null;
		}
		Proc p = new Proc(parentProc, ip, code.get(ip)[p3]);

		if (DEBUG)
			debug("[proc]newproc " + ip + ":" + parentProc + ","
					+ code.get(ip)[p3] + "," + p);
		return p;
	}

	List<int[]> code;
	private int entrypoint;
	private int runCnt;

	private VM load(File f) throws Exception {
		in = new BufferedInputStream(new FileInputStream(f));
		entrypoint = -1;
		{
			if (read() == 'N' && read() == 'E' && read() == 'O'
					&& read() == 'E') {
				entrypoint = read32() + 4;

			} else {
				error("signiture not found.");
			}
		}
		int i;
		int lineno = 0;
		// Map<Integer, int[]> data = new HashMap<Integer, int[]>();
		code = new ArrayList<int[]>();
		Map<Integer, Integer> linemap = new HashMap<Integer, Integer>();
		List<int[]> later = new ArrayList<int[]>();
		while (true) {
			int line = current;
			i = read();
			if (i == -1)
				break;
			String codename = Consts.CODE_NAME[i];
			// System.out.println(line+":"+codename);
			String opt = codename.substring(codename.lastIndexOf('_') + 1);
			int oplen = opt.length();
			int[] d = new int[2 + oplen];
			int j = 0;
			d[j++] = lineno++;
			d[j++] = i;
			for (int p = 0; p < oplen; p++) {
				char c = opt.charAt(p);
				if (c == 'R') {
					int v = read32();
					d[j++] = v;
				} else {
					int v = read32();
					if (codename.startsWith("J") && p == oplen - 1) {
						d[j++] = v + line;
						later.add(new int[] { d[0], j - 1 });
					} else if (p == 0 && (codename.startsWith("NEWPROC_"))) {
						d[j++] = v + line;
						later.add(new int[] { d[0], j - 1 });
					} else {
						d[j++] = v;
					}
				}
			}
			linemap.put(line, d[0]);
			code.add(d);
		}
		in.close();
		// System.out.println("linemap="+linemap);
		for (int[] x : later) {
			int v = code.get(x[0])[x[1]];
			// System.out.println("x[0]="+v);
			code.get(x[0])[x[1]] = linemap.get(v);
		}
		for (int[] r : code) {
			if (DEBUG)
				debug(r[0] + " = " + Consts.CODE_NAME[r[CMD]] + " "
						+ dumpArr(r));
		}
		if (entrypoint != -1) {
			// initIntPool();
			entrypoint = linemap.get(entrypoint);
			System.out.println("entrypoint=" + entrypoint + ", vm entry "
					+ entrypoint);
			Proc pid = newproc(null, entrypoint);
			pid.status = RUN;
			addToRunning(pid);
			if (DEBUG)
				debug("[proc]vm run " + pid);
		}
		return this;
	}

	private void addToRunning(Proc pid) {
		runCnt++;
		pid.prev = running;
		if (running.next == null) {
			running.next = pid;
			pid.next = null;
		} else {
			pid.next = running.next;
			running.next.prev = pid;
			running.next = pid;
		}

	}

	private void removeFromRunning(Proc pid) {
		runCnt--;
		if (pid.next == null) {
			pid.prev.next = null;
			pid.prev = null;
		} else {
			pid.next.prev = pid.prev;
			pid.prev.next = pid.next;
			pid.prev = null;
			pid.next = null;
		}
	}

	private void debug(String s) {
		System.out.println(s);
	}

	private int read() throws IOException {
		int i = in.read();
		current++;
		return i;
	}

	private int read32() throws IOException {
		byte[] bs = new byte[4];
		in.read(bs);
		current += 4;
		return Disasm.u32(bs);
	}
}
