.global _start

.set N, 100
.set step, 1

.data
count:
	.word N

.text
_start:
	ldr r0, =count
	ldr r1, [r0]
	ldr r2, =step

Loop:
	sub r1, r2
	cmp r1, #0
	blt Save
	b Loop

Save:
	str r1, [r0]

End:
	b End

