.data
A:	.word	0,1,-2147483648,2147483647,4294967295
	.align
B:	.hword	0,1,-32768,32767,65535
	.align
C:	.byte	0,1,-128,127,255
	.align

.bss
D: 	.word
E:	.word
F:	.word
	
.text
_start:
	ldr r0, =C
	ldrb r1, [r0, #2]

