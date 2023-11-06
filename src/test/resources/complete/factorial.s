.data
fact: .word 1
i: .word 1
.align

.equ N, 10	@ Sur 32 Bits, le maximum est 12

.text
.global _start

_start:
	LDR R9, =fact
	LDR R10, =i
	LDR R0, [R9]
	LDR R1, [R10]
	
LOOP:
	CMP R1, #N
	BGT END
	MUL R0, R0, R1
	ADD R1, R1, #1
	BAL LOOP

END:
	STR R0, [R9]
	
STOP:
	BAL STOP
	
@ Pour faire les calculs avec 64 bits, il faut :
@ Utiliser - registres (2 sources, 3 destinations)
@ Multiplier chaque registre source (avec SMULL et MUL resp.) par i
@ Additionner avec retenu sur 64 bits les deux regitres sources de poids forts