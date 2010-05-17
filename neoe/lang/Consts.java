package neoe.lang;

public interface Consts {
	// -------------vm index
	int p1 = 2, p2 = 3, p3 = 4, c1 = 1, vmindex=0 ;
	
	//--------
	int  RIR=0;
	int  RRR=1;
	int  IRR=2;
	int  RII=3;
	int  RRI=4;
	int  IIR=5;
	
	//-----------	status, 
	int NEW=0;
	int RUN=1;
	int PAUSE=2;
	int END=3;
	// --------------- proc info arr
//	int IP=0;
//	int STARTIP=1;
//	int STATUS=2;
//	int PARENTPROC=3;
//	int RESUME=4;
	// --------------- Byte type
	int BYTE=0;
	int I16=1;
	int INT=2;
	int DOUBLE=3;
	// ----1 byte code
	int ADD_RIR=0;
	int ADD_RRR=1;
	int DIV_IRR=2;
	int DIV_RIR=3;
	int DIV_RRR=4;
	int GETARR_BYTE_RIR=5;
	int GETARR_BYTE_RRR=6;
	int GETARR_INT_RIR=7;
	int GETARR_INT_RRR=8;
	int GETPROCVAL_RIR=9;
	int GETPROCVAL_RRR=10;
	int INC_R=11;
	int JE_RII=12;
	int JE_RRI=13;
	int JG_RII=14;
	int JG_RRI=15;
	int JGE_RII=16;
	int JGE_RRI=17;
	int JL_RII=18;
	int JL_RRI=19;
	int JLE_RII=20;
	int JLE_RRI=21;
	int JMP_I=22;
	int JNE_RII=23;
	int JNE_RRI=24;
	int PROCJOIN_R=25;
	int MOD_IRR=26;
	int MOD_RIR=27;
	int MOD_RRR=28;
	int MOV_IR=29;
	int MOV_RR=30;
	int MUL_RIR=31;
	int MUL_RRR=32;
	int NEWARR_IR=33;
	int NEWARR_RR=34;
	int NEWPROC_IR=35;
	int NEWPROCARR_IIR=36;
	int OUT_I=37;
	int OUT_R=38;
	int RET_=39;
	int RUNPROC_R=40;
	int SETARR_BYTE_RII=41;
	int SETARR_BYTE_RIR=42;
	int SETARR_BYTE_RRI=43;
	int SETARR_BYTE_RRR=44;
	int SETARR_INT_RII=45;
	int SETARR_INT_RIR=46;
	int SETARR_INT_RRI=47;
	int SETARR_INT_RRR=48;
	int SETPROCVAL_RII=49;
	int SETPROCVAL_RIR=50;
	int SETPROCVAL_RRI=51;
	int SETPROCVAL_RRR=52;
	int STARTPROC_R=53;
	int SUB_IRR=54;
	int SUB_RIR=55;
	int SUB_RRR=56;
	int TIME_R=57;
	int DEFPROC_III=58;
	int DELPROC_R=59;
	// code name
	String[] CODE_NAME={ "ADD_RIR",
			"ADD_RRR",
			"DIV_IRR",
			"DIV_RIR",
			"DIV_RRR",
			"GETARR_BYTE_RIR",
			"GETARR_BYTE_RRR",
			"GETARR_INT_RIR",
			"GETARR_INT_RRR",
			"GETPROCVAL_RIR",
			"GETPROCVAL_RRR",
			"INC_R",
			"JE_RII",
			"JE_RRI",
			"JG_RII",
			"JG_RRI",
			"JGE_RII",
			"JGE_RRI",
			"JL_RII",
			"JL_RRI",
			"JLE_RII",
			"JLE_RRI",
			"JMP_I",
			"JNE_RII",
			"JNE_RRI",
			"PROCJOIN_R",
			"MOD_IRR",
			"MOD_RIR",
			"MOD_RRR",
			"MOV_IR",
			"MOV_RR",
			"MUL_RIR",
			"MUL_RRR",
			"NEWARR_BYTE_IR",
			"NEWARR_BYTE_RR",
			"NEWARR_INT_IR",
			"NEWPROC_IR",
			"OUT_I",
			"OUT_R",
			"RET_",
			"RUNPROC_R",
			"SETARR_BYTE_RII",
			"SETARR_BYTE_RIR",
			"SETARR_BYTE_RRI",
			"SETARR_BYTE_RRR",
			"SETARR_INT_RII",
			"SETARR_INT_RIR",
			"SETARR_INT_RRI",
			"SETARR_INT_RRR",
			"SETPROCVAL_RII",
			"SETPROCVAL_RIR",
			"SETPROCVAL_RRI",
			"SETPROCVAL_RRR",
			"STARTPROC_R",
			"SUB_IRR",
			"SUB_RIR",
			"SUB_RRR",
			"TIME_R",
			"DEFPROC_III",
			"DELPROC_R",
			"NEWPROCARR_IR",
			"SETPROCARR_RII",
			"SETPROCARR_RIR",
			"SETPROCARR_RRI",
			"SETPROCARR_RRR",
			"GETPROCARR_RIR",
			"GETPROCARR_RRR",};
}
