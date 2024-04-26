@ Graphes et parcours de graphes
@ TP 3INFO
@ Objectifs : 
@	- tableaux et pointeurs de tableaux
@	- structures
@ 	- appel de fonction
@---------------------------------------

.set indice,-12

@ les fonctions DFS, EstPointEntree et RechercheSommet sont définies en global dans des fichiers à part

@ Structure graphe {
.set nbSommets,0
.set nom,4
.set nbSucc,8
.set lesSucc,12
.set t_graphe,16
@}

@ Nb de sommmets
.set N,5


.data
pNoms: 		.byte 'a','b','c','d','e'
.align
pNbSucc:	.word 1,2,1,1,2
.align
t1: 		.byte 'b'
.align
t2: 		.byte 'c','d'
.align
t3: 		.byte 'd'
.align
t4: 		.byte 'e'
.align
t5: 		.byte 'b','c'
.align
pLesSucc:	.word t1,t2,t3,t4,t5

.align
g:		.word N,pNoms,pNbSucc,pLesSucc

.align
b:		.word 0

.bss
Marquer:	.skip N

.align


.text

.global _Start
_Start:
	mov r0,#0
	cmp r0,#N
	
	@---------------------------------------------------
	ldr r0,='b'			@ test : RechercheSommet('a');
	ldr r1,=g
	stmfd sp!,{r0,r1}
	@ réservation pour résultat
	sub sp, sp, #4
	bl RechercheSommet
	@ fonction retourne indice
	ldmfd sp!,{r1}
	str r1,[fp,#indice]
	@ libération dans la pile des param d''entrée
	add sp,	sp, #8	
	
	@---------------------------------------------------
	ldr r0,='a'			@ b = EstPointEntree('a',&g);
	ldr r1,=g
	stmfd sp!,{r0,r1}
	@ réservation pour résultat
	sub sp, sp, #4
	bl EstPointEntree
	@ fonction retourne un booléen
	ldmfd sp!,{r2}
	ldr r10, =0xFFFFFFF0
    	str r2, [r10]
	@ libération dans la pile des param d''entrée
	add sp,	sp, #8	

	@---------------------------------------------------
	ldr r0,='a'			@ DFS(v,&g,&marquer)
	ldr r1,=g
	ldr r2,=Marquer
	stmfd sp!,{r0,r1,r2}
	@ réservation pour résultat
	sub sp, sp, #4
	bl DFS
	@ fonction retourne un booléen
	ldmfd sp!,{r3}
	@ libération dans la pile des param d''entrée
	add sp,	sp, #12		
	
finStart:
	bal finStart

.end

