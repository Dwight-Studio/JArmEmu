.equ offsetPGCD, 8
.equ offsetA, 12
.equ offsetB, 16


.data
N1: .word 221
N2: .word 782
R: .word 0
.align

.bss


.text

.global _start
_start:
	LDR R8, =N1
	LDR R9, =N2
	LDR R10, =R
	
	LDR R0, [R8]
	LDR R1, [R9]
	
	@ Appel de la fonction
	STMFD SP!, {R0, R1}
	SUB SP, SP, #4
	
	BL PGCD_1
	
	@ Récupération du retour
	LDMFD SP!, {R2}
	ADD SP, SP, #8
	
	STR R2, [R10]
	
STOP:
	BAL STOP
	
PGCD_1:
	@ Préparation de la fonction
	STMFD SP!, {LR}
	STMFD SP!, {FP}
	MOV FP, SP
	STMFD SP!, {R0, R1, R2}
	
	@ Fonction
	LDR R0, [FP, #offsetA]
	LDR R1, [FP, #offsetB]
	
	CMP R0, #0
	BNE PGCD_2
	MOV R2, #0
	BAL PGCD_RET
	
PGCD_2:
	CMP R1, #0
	BNE PGCD_3
	MOV R2, #0
	BAL PGCD_RET
	
PGCD_3:
	CMP R0, R1
	BNE PGCD_4
	MOV R2, R0
	BAL PGCD_RET
	
PGCD_4:
	CMP R0, R1
	BLE PGCD_5
	SUB R0, R0, R1
	BAL PGCD_REC

PGCD_5:
	SUB R1, R1, R0
	BAL PGCD_REC
	
PGCD_REC:
	@ Appel de la fonction
	STMFD SP!, {R0, R1}
	SUB SP, SP, #4
	
	BL PGCD_1
	
	@ Récupération du retour
	LDMFD SP!, {R2}
	ADD SP, SP, #8
	BAL PGCD_RET
	
PGCD_RET:
	STR R2, [FP, #offsetPGCD]
	LDMFD SP!, {R0,R1,R2}
	LDMFD SP!, {FP}
	LDMFD SP!, {LR}
	BX LR
	
