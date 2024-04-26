@Exo 2 : PGCD

.equ a,12
.equ b,16
.equ res,8

.section .data
	x : .word 666
	y : .word 666

.section .bss

.section .text
.global _start
_start :
	ldr r0, =x		@a
	ldr r0, [r0]
	ldr r1, =y		@b
	ldr r1, [r1]
	stmfd sp!, {r0,r1}	@b et a ds la pile
	sub sp, sp, #4		@reservation res
	bl pgcd			@appel pgcd (MaJ lr)
	ldmfd sp!, {r2}		@recuperation res (depile)
	add sp, sp, #8		@liberation a et b

end: b end

pgcd :
	stmfd sp!, {lr}		@sauve adr retour
	stmfd sp!, {fp}		@sauve ancien fp
	mov fp, sp		@sauve sp ds fp pour servir de pivot
	stmfd sp!, {r0, r1, r2}	@sauve reg fonction appelante
	ldr r0,[fp,#a]		@load a ds r0
	ldr r1,[fp,#b]		@load b ds r1
@CODE
	cmp r0, #0
	beq retZ		@if a==0
	cmp r1, #0
	beq retZ		@if b==0
	cmp r0, r1
	beq retA		@if a==b
	bgt retP1		@if a>b
	@Dernier sinon
	sub r1, r1, r0		@b=b-a
	stmfd sp!, {r0,r1}	@b et a-b ds la pile
	sub sp, sp, #4		@reservation res
	bl pgcd			@appel pgcd (MaJ lr)
	ldmfd sp!, {r0}
	add sp, sp, #8
	str r0, [fp, #res]
	b fin
	retA :
		mov r2, r0
		str r2, [fp, #res]
		b fin
	retZ :
		mov r2, #0
		str r2, [fp, #res]
		b fin
	retP1 :
		sub r0, r0, r1		@a=a-b
		stmfd sp!, {r0,r1}	@b et a-b ds la pile
		sub sp, sp, #4		@reservation res
		bl pgcd			@appel pgcd (MaJ lr)
		ldmfd sp!, {r0}
		add sp, sp, #8
		str r0, [fp, #res]
		b fin

@CODE
	fin :	ldmfd sp!, {r0, r1, r2}	@restaure reg fonction appelante
					@pas de var locale
 		ldmfd sp!, {fp}		@restaure fp
		ldmfd sp!, {lr}		@restaure lr
		bx lr			@retour