.global _start
.text
_start:
	@ Beginning of the program
	addal r2, r4, r5
	addal r2, r4, r5
	addal r2, r4, r5
	adds r6, r2, r6
	mov r0, r1, lsl #1
	mov r0, r1, lsr #1
	mov r0, r1, asr #1
	mov r0, r1, rrx
	mov r0, r1, ror #3

