.equ N, 3
.equ size, 4

.section .data
	matrice1: .word 1, 2, 3, 4, 5, 6, 7, 8, 9
	matrice2: .word 1, 1, 1, 2, 2, 2, 3, 3, 3
.align

.equ offsetI, -4
.equ offsetJ, -8
.equ offsetK, -12

.equ offsetResMatrice, 16
.equ offsetMatrice1, 8
.equ offsetMatrice2, 12

.section .bss
	array: .skip N * N * size

.section .text
.global _start
_start:
	ldr r0, =matrice1
	ldr r1, =matrice2
	ldr r2, =array
	@ 3 parametres d'entree
	stmfd sp!, {r0, r1, r2}
	@ saut a la fonction produit
	bl Produit
	@ liberer la place allouee aux 3 parametres d'entree
	add sp, sp, #12
	@ boucler sur la fin
end:
	bal end
	
Produit:
	@ sauvegarde addresse de retour
	stmfd sp!, {lr}
	@ sauvegarde ancien fp
	stmfd sp!, {fp}
	@ placement de fp
	mov fp, sp
	@ variables locales, reservations pour i, j et k
	sub sp, sp, #12
	@ sauvegarde des registres temporaires
	stmfd sp!, {r0-r8}
	
	@ corps de la fonction
	mov r0, #0
	str r0, [fp, #offsetI] @ i = 0
	str r0, [fp, #offsetJ] @ j = 0
	str r0, [fp, #offsetK] @ k = 0
	mov r8, #N			   @ R8 = N (soit 3)
	sub r8, r8, #1		   @ R8 = N-1 (soit 2)
	
	boucle1:
		ldr r0, [fp, #offsetI]
		cmp r0, r8
		bgt fin		@Si i == N-1 alors fin
		ldr r1, [fp, #offsetJ]
		mov r1, #0		@ On remet J a 0
		str r1, [fp, #offsetJ]
		boucle2:
			ldr r1, [fp, #offsetJ]
			cmp r1, r8
			bgt sortie2 @Si j == N-1 alors on retourne a la boucle externe
			ldr r3, [fp, #offsetResMatrice]
			mov r6, #N
			mla r5, r0, r6, r1
			mov r6, #size
			mul r5, r5, r6
			mov r4, #0
			str r4, [r3, r5]
			
			ldr r2, [fp, #offsetK]
			mov r2, #0		@ On remet K a 0
			str r2, [fp, #offsetK]
			boucle3:
				ldr r2, [fp, #offsetK]
				cmp r2, r8
				bgt sortie3
				ldr r7, [r3, r5] @ R7 = Result[i, j]
				ldr r4, [fp, #offsetMatrice1]
				mov r6, #N
				mla r5, r0, r6, r2
				mov r6, #size
				mul r5, r5, r6
				ldr r9, [r4, r5]	@R9 = A[i, k]
				
				ldr r4, [fp, #offsetMatrice2]
				mov r6, #N
				mla r5, r2, r6, r1
				mov r6, #size
				mul r5, r5, r6
				ldr r6, [r4, r5]	@R6 = B[k, j]
				
				mla r7, r9, r6, r7 @R7 = A[i, k] * B[k, j] + Result[i, j]
				
				ldr r3, [fp, #offsetResMatrice]
				mov r6, #N
				mla r5, r0, r6, r1
				mov r6, #size
				mul r5, r5, r6
				str r7, [r3, r5]	@ Result[i, j] = R7
				add r2, r2, #1
				str r2, [fp, #offsetK]
				bal boucle3
			sortie3:
				add r1, r1, #1
				str r1, [fp, #offsetJ]
				bal boucle2
		sortie2:
			add r0, r0, #1
			str r0, [fp, #offsetI]
			bal boucle1
	fin:
		@restauration des registres temporaires
		ldm sp!, {r0-r8}
		@ depiler les variables locales
		add sp, sp, #12
		@ restauration de fp
		ldmfd sp!, {fp}
		@ depiler addresse retour dans lr
		ldmfd sp!, {lr}
		@ retour
		bx lr
	
	
	
	
	
	
