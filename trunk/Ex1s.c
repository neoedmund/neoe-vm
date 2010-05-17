#include <stdio.h>
#include <time.h>

#define byte char
#define bool char
#define true 1
#define false 0

bool is7(int p) {
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

int Th1(byte* data, int start, int cnt) {
	int total =0 ;
	int p = start;
	int i;
	for (i = 0; i < cnt; i++) {
		if (is7(p)) {
			data[i] = 0;
			total += 1;
		} else {
			data[i] = 1;
		}
	}
	return total;
}

int main(int _1, char** _2){
	printf("start\n");
	int  SIZE_PER_THREAD = 1000;
	int  THREAD_CNT = 100000;

	int t1 = clock();
	int total = 0;
	byte* data = (byte*)  malloc(SIZE_PER_THREAD * THREAD_CNT);
	int i;
	for ( i = 0; i < THREAD_CNT; i++) {
		total += Th1(data, i * SIZE_PER_THREAD, SIZE_PER_THREAD);
	}

	printf("res of %d=%d end %d\n" , THREAD_CNT * SIZE_PER_THREAD , total,  (clock() - t1));
}

