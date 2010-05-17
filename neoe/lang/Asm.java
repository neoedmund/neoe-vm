package neoe.lang;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neoe.util.FileUtil;

public class Asm {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new Asm().run("neoe/lang/Ex1e.asm");
		System.out.println("end");
	}

	// static String[] opcode = { "nop", "add", "sub", "mul", "div", "mod",
	// "je",
	// "jne", "jl", "jle", "jg", "jge", "jmp", "mov", "ret", "time",
	// "newarr", "newprocarr", "getarr", "setarr", "getprocval",
	// "setprocval", "newproc", "runproc", "startproc", "joinproc",
	// "defproc", "inc", "out", };
	// static int[] oplen = { 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 2, 0, 1, 2,
	// 3, 3, 3, 3, 3, 2, 1, 1, 1, 3, 1, 1 };
	static String[] OP_TYPE = { "RIR", "RRR", "IRR", "RII", "RRI", "IIR" };
	static String[] BYTE_TYPE = { "byte", "b16", "int", "double" };
	Map<String, Integer> labelMap = new HashMap<String, Integer>();
	Map<String, Integer> procMap = new HashMap<String, Integer>();
	Map<String, List<int[]>> laterSetMap = new HashMap<String, List<int[]>>();
	Map<String, List<int[]>> laterSetMapProc = new HashMap<String, List<int[]>>();
	private int current;
	BufferedOutputStream out;
	int lineno;
	boolean hasLocal = false;
	private int localPos, maxLocal;

	private void run(String fn) throws Exception {
		String[] lines = FileUtil.readString(new FileInputStream(fn), "utf8")
				.split("\n");
		// System.out.println(Arrays.deepToString(lines));
		File fout = new File("Ex1.bin");
		out = new BufferedOutputStream(new FileOutputStream(fout));
		w8('N');
		w8('E');
		w8('O');
		w8('E');
		addMulti(laterSetMap, "main", current, current);
		w32(-1);
		for (String line : lines) {
			lineno++;
			if (line.startsWith("#")) {
				debug("skip " + line);
				continue;
			}
			if (line.trim().length() == 0) {
				debug("skip");
				setLaterValues();
				labelMap.clear();
				continue;
			}
			{
				int p1 = line.indexOf("::");
				if (p1 > 0) {
					setProcLocal();
					hasLocal = true;
					int p2 = current;
					w8(getOpCode("DEFPROC_III"));
					w32(0);// param
					w32(0);// out
					localPos = current;
					w32(0);// local
					String proc = line.substring(0, p1).trim().toLowerCase();
					procMap.put(proc, p2);
					setLaterValues();
					labelMap.clear();
					continue;
				}
			}
			{
				int p1 = line.indexOf(":");
				if (p1 > 0) {
					String label = line.substring(0, p1).trim().toLowerCase();
					if (label.indexOf(' ') >= 0)
						error("not valid label " + label);
					labelMap.put(label, current);
					continue;
				}
			}
			String[] ws = line.trim().split(" ");
			for (int i = 0; i < ws.length; i++)
				ws[i] = ws[i].trim().toLowerCase();
			int linecur = current;
			String cmd = ws[0].trim().toLowerCase();

			int p2 = 1;
			int bytetype = 2;

			if (ws.length > 1 && ws[1].equals("byte")) {
				bytetype = 0;
				p2 = 2;
			}
			if (ws.length > 1 && ws[1].equals("double")) {
				bytetype = 4;
				p2 = 2;
			}
			StringBuffer optype = new StringBuffer();
			for (int i = p2; i < ws.length; i++) {
				if (ws[i].startsWith("#")) {
					optype.append("R");
				} else {
					optype.append("I");
				}
			}
			int opt = getOpType(optype.toString());
			if (opt == -1) {
				error("unknow op type " + optype + " of " + line);
			}
			int id = getOpCode(cmd + "_" + optype);
			if (id == -1)
				id = getOpCode(cmd + "_" + BYTE_TYPE[bytetype] + "_" + optype);
			if (id == -1) {
				error("unknown " + cmd + "_" + BYTE_TYPE[bytetype] + "_"
						+ optype);
				continue;
			}
			System.out.println(cmd + "_" + BYTE_TYPE[bytetype] + "_" + optype
					+ "->" + id);
			w8(id);
			// System.out.println("bytetype " + bytetype + ",opt " + opt);
			// w8(((bytetype << 4) & 0xf0) | (opt & 0xf));
			{// check len
				int l1 = ws.length - p2;
				int l2 = optype.length();
				if (l1 != l2) {
					error("params expected " + l2 + " but get " + l1 + " : "
							+ line);
				}
			}
			for (int i = p2; i < ws.length; i++) {
				if (ws[i].startsWith("#")) {
					int v = toInt(ws[i].substring(1));
					if (!((cmd.equals("setprocval") || cmd.equals("setprocval")) && (p2
							- i == 1))) {
						if (cmd.equals("time"))
							maxLocal = Math.max(maxLocal, v + 1);
						else
							maxLocal = Math.max(maxLocal, v);

					}
					w32(v);
				} else if (!Character.isDigit(ws[i].charAt(0))) {
					addMulti(laterSetMap, ws[i], current, linecur);
					w32(0);
				} else {
					w32(toInt(ws[i]));
				}
			}
		}
		setLaterValues();
		labelMap.clear();
		setProcLocal();
		setLaterProcValues();
		out.close();
		RandomAccessFile raf = new RandomAccessFile(fout, "rw");
		for (int[] r : laterWrite) {
			raf.seek(r[0]);
			w32(raf, r[1]);
			System.out.println("[writelater]" + r[0] + ":" + r[1]);
		}
		raf.close();

	}

	private void setProcLocal() {
		if (hasLocal) {
			laterWrite.add(new int[] { localPos, maxLocal + 1 });
			maxLocal = -1;
			hasLocal = false;
		}
	}

	private void w32(RandomAccessFile out, int v) throws IOException {
		out.write(v & 0xff);
		out.write((v >> 8) & 0xff);
		out.write((v >> 16) & 0xff);
		out.write((v >> 24) & 0xff);
	}

	private void addMulti(Map<String, List<int[]>> map, String key, int value,
			int linecur) {
		System.out.println("[addMulti]" + key + " " + value);
		List<int[]> vs = map.get(key);
		if (vs == null) {
			vs = new ArrayList<int[]>();
			map.put(key, vs);
		}
		vs.add(new int[] { value, linecur });
	}

	List<int[]> laterWrite = new ArrayList<int[]>();

	private void setLaterValues() {
		System.out.println("laterSetMap=" + dumplasterSetMap(laterSetMap));
		for (String key : laterSetMap.keySet()) {
			Integer value = labelMap.get(key);
			if (value == null) {// must be a proc?
				if (laterSetMapProc.get(key) == null)
					laterSetMapProc.put(key, laterSetMap.get(key));
				else
					laterSetMapProc.get(key).addAll(laterSetMap.get(key));
				continue;
			}
			List<int[]> poses = laterSetMap.get(key);
			for (int[] pos : poses) {
				laterWrite.add(new int[] { pos[0], value - pos[1] });
			}

		}

		laterSetMap.clear();
		System.out.println("laterSetMapProc="
				+ dumplasterSetMap(laterSetMapProc));
	}

	private String dumplasterSetMap(Map<String, List<int[]>> map) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (String key : map.keySet()) {
			sb.append(key);
			sb.append(":");
			List<int[]> iii = map.get(key);
			for (int[] ii : iii) {
				sb.append("[");
				sb.append(ii[0]);
				sb.append(",");
				sb.append(ii[1]);
				sb.append("],");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	private void setLaterProcValues() {
		System.out.println("procMap=" + procMap);
		for (String key : laterSetMapProc.keySet()) {
			Integer value = procMap.get(key);
			if (value == null) {
				error("unkown proc " + key);
				continue;
			}
			List<int[]> poses = laterSetMapProc.get(key);
			for (int[] pos : poses) {
				laterWrite.add(new int[] { pos[0], value - pos[1] });
			}
		}
	}

	private void w32(int v) throws IOException {
		// System.out.println("[write]" + current + ":" + v);
		out.write(v & 0xff);
		out.write((v >> 8) & 0xff);
		out.write((v >> 16) & 0xff);
		out.write((v >> 24) & 0xff);
		current += 4;
	}

	private int toInt(String s) {
		if (s.startsWith("0x"))
			return Integer.parseInt(s.substring(2), 16);
		return Integer.parseInt(s);
	}

	private int getOpType(String s) {
		for (int i = 0; i < OP_TYPE.length; i++) {
			if (OP_TYPE[i].startsWith(s)) {
				return i;
			}
		}
		return -1;
	}

	private void error(String s) {
		System.err.println("[e " + lineno + "]" + s);
		System.exit(lineno);
	}

	private void w8(int id) throws Exception {
		// System.out.println("[write]" + current + ":" + id);
		// debug("[o]"+id);
		out.write(id);
		current++;
	}

	private int getOpCode(String cmd) {
		cmd = cmd.toUpperCase();
		for (int i = 0; i < Consts.CODE_NAME.length; i++) {
			if (Consts.CODE_NAME[i].equals(cmd))
				return i;
		}
		return -1;
	}

	private void debug(String s) {
		// System.out.println("[d]" + s);

	}

}
