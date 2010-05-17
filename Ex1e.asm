#  SIZE_PER_THREAD = 1000;
#  THREAD_CNT = 100000;


is7:: param 1, out 1, local 4
#0 p #1 out
mod #0 7 #2
jne #2 0 L2
mov 1 #1
ret
L2:
#3 v
mov #0 #3
Loop:
#4 v2
mod #3 10 #4
jne #4 7 L3
mov 1 #1
ret
L3:
jl #3 10 break
div #3 10 #3
jmp loop
break:
mov 0 #1
ret


Th1:: param 3, out 0, local N
#0 data
#1 start
#2 cnt
#3 total
#4 p
#5 i
Mov #1 #4
Mov 0 #5
Loop:
newproc is7 #6
Setprocval #6 0 #4
Runproc #6
Getprocval #6 1 #7
delproc #6
Jne #7 1 else
Setarr byte #0 #5 0
Add #3 1 #3
jmp ENDIF
else:
Setarr byte #0 #5 1
ENDIF:
Inc #5
Jl #5 #2 loop
ret

main:: param 1, out 0 , local N
#0 args
#1 t1
#3 data
#4 ts
#5 i
#6 total
Time #1
newarr byte 100000000 #3
newprocarr 100000000 #4
Mov 0 #5
Loop:
newproc Th1 #7
Mul #5 1000 #8
setprocval #7 0 #3
setprocval #7 1 #8
setprocval #7 2 1000
Setprocarr #4 #5 #7
Startproc #7
Inc #5
Jl #5 100000 loop
Mov 0 #6

Mov 0 #5
Loop:
Getprocarr #4 #5 #9
procjoin #9
Getprocval #9 3 #10
delproc #9
Add #6 #10 #6
Inc #5
Jl #5 100000 loop
out #6
Time #10
Sub #10 #1 #10
Out #10
ret


