.global _start
.bss
VAL:
	.word
	.align

.text
_start:
	@ Beginning of the program
	LDR r5, =VAL
	
	mov r0, #0
	mov r1, #4
	mov r2, #2
	mov r3, #3
	adc r0, r1, r2, lsl r3
	str r0, [r5]

