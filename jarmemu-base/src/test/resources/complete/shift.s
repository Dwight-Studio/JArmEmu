.global _start
.bss
VAL:
	.word
	.align

VAL2:
    .word
    .align

.text
_start:
	@ Beginning of the program
	LDR r5, =VAL
	LDR r6, =VAL2
	mov r0, #0
	b 20
	mov r0, #4
	mov r1, #4
	mov r2, #2
	mov r3, #3
	adc r0, r1, r2, lsl r3
	str r0, [r5], -r2
	str r5, [r6, +r1]

