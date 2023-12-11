@ Graphes et parcours de graphes
@ TP 3INFO
@ Objectifs : 
@	- tableaux et pointeurs de tableaux
@	- structures
@ 	- appel de fonction
@---------------------------------------

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


@ Constantes nécessaires pour la fonction RechercheSommet
.set iRech,-4		@ |    i    |
			@ |  anc fp |
			@ |lr retour|
.set resRech,8		@ |param ret|	résultat de la fonction : indice tableau
.set SomRech,12		@ |  sommet |   caractère en param de fonction
.set adGRech,16		@ | @graphe |   adresse du graphe en param de fonction

RechercheSommet:
	@ {préparation de la pile}
	stmfd sp!,{fp,lr}
	mov fp,sp
	sub sp,sp,#4
	
	@ {sauvegarde des registres temporaires}
	stmfd sp!,{r0,r1,r2,r3}

	@------------------------------------
	mov r0,#0
	str r0,[fp,#iRech]
TantQue:
	ldr r1,[fp,#iRech]		@ tant que g.nom[i] != sommet 
	ldr r0,[fp,#adGRech]
	ldr r2,[r0,#nom]
	ldrb r2,[r2,r1]
	ldrb r3,[fp,#SomRech]
	cmp r2, r3 		
	beq FinTQ
	
	ldr r0,[fp,#iRech]		@ i++
	add r0,r0,#1
	str r0,[fp,#iRech]
	
	bal TantQue

FinTQ:	
	ldr r0,[fp,#iRech]		@ retourne i
	str r0,[fp,#resRech]
	
	@------------------------------------
	@ {Restauration des registres temporaires}
	ldmfd sp!,{r0,r1,r2,r3}
	@ {libération de la pile - var locales}
	add sp, sp, #4
	ldmfd sp!,{fp,lr}
	@ {retour au main}
	bx lr
	@ pointe sur le résultat

	
@------------------------------------

@ Constantes nécessaires pour la fonction EstPointEntree
.set indice,-12			@ | indice  |
.set jPtEnt,-8			@ |    j    |
.set iPtEnt,-4			@ |    i    |
				@ |  anc fp |
				@ |lr retour|
.set resPtEntree,8		@ |param ret|	résultat de la fonction
.set sommet,12			@ |  sommet |   caractère en param de fonction
.set adGraphePtEnt,16		@ | @graphe |   adresse du graphe en param de fonction

EstPointEntree:
	@ {préparation de la pile}
	stmfd sp!,{fp,lr}
	mov fp,sp
	@ {réservation var locales}
	sub sp,sp,#12
	
	@ {sauvegarde des registres temporaires}
	stmfd sp!,{r0,r1,r2,r3,r4,r5}

	mov r0,#0
	str r0,[fp,#iPtEnt]
pouri:
	ldr r1,[fp,#iPtEnt]		@ pour i=0 à g.nbSommets - 1 faire
	ldr r0,[fp,#adGraphePtEnt]
	ldr r2,[r0,#nbSommets]
	cmp r1, r2 		
	bge finpouri

	mov r0,#0
	str r0,[fp,#jPtEnt]
pourj:
	ldr r1,[fp,#jPtEnt]		@ pour j=0 à g.nbSucc[i] - 1 faire 
	ldr r3,[fp,#iPtEnt]
	ldr r0,[fp,#adGraphePtEnt]
	ldr r2,[r0,#nbSucc]
	ldr r4,[r2,r3,lsl #2]
	cmp r1,r4 		
	bge finpourj
	
	@ si (g.lesSuccs[i][j]==sommet) alors retourne faux fsi
	ldr r0,[fp,#adGraphePtEnt]
	ldr r1,[r0,#lesSucc]
	ldr r2,[fp,#iPtEnt]
	ldr r3,[r1,r2,lsl #2]
	ldr r4,[fp,#jPtEnt]
	ldrb r4,[r3,r4,lsl #0]
	
	ldrb r5,[fp,#sommet]
	cmp r4,r5
	bne incj
	
	mov r0,#0
	str r0,[fp,#resPtEntree]
	bal finRechPtEntree
	
incj:	
	ldr r1,[fp,#jPtEnt]
	add r1,r1,#1
	str r1,[fp,#jPtEnt]
	bal pourj
	
finpourj:	
	ldr r1,[fp,#iPtEnt]
	add r1,r1,#1
	str r1,[fp,#iPtEnt]
	bal pouri
	
finpouri:
	mov r0,#1
	str r0,[fp,#resPtEntree]
	bal finRechPtEntree
	
finRechPtEntree:
	@ {Restauration des registres temporaires}
	ldmfd sp!,{r0,r1,r2,r3,r4,r5}
	@ {libération de la pile - var locales}
	add sp, sp, #12
	ldmfd sp!,{fp,lr}
	@ {retour au main}
	bx lr
	@ pointe sur le résultat
	
	
	
@------------------------------------
@ Constantes nécessaires pour la fonction DFS
.set wDFS,-16			@ |    w    |
.set indexw,-12			@ |  i_w    |
.set indexv,-8			@ |  i_v    |
.set jDFS,-4			@ |    j    |
				@ |  anc fp |
				@ |lr retour|
.set resDFS,8			@ |param ret|	résultat de la fonction
.set v,12			@ |  sommet |   caractère en param de fonction
.set adGrapheDFS,16		@ | @graphe |   adresse du graphe en param de fonction
.set marquerDFS,20		@ |@marquer |	adresse du tableau de marquage
	
DFS:
	@ {préparation de la pile}
	stmfd sp!,{fp,lr}
	mov fp,sp
	@ {réservation var locales}
	sub sp,sp,#16
	
	@ {sauvegarde des registres temporaires}
	stmfd sp!,{r0,r1,r2,r3,r4}

	ldr r0,[fp,#v]			@ i_v = RechercheSommet(v,g)
	ldr r1,[fp,#adGrapheDFS]
	stmfd sp!,{r0,r1}
	@ réservation pour résultat
	sub sp, sp, #4
	bl RechercheSommet
	@ fonction retourne indice
	ldmfd sp!,{r1}
	str r1,[fp,#indexv]
	@ libération dans la pile des param d''entrée
	add sp,	sp, #8
	
	ldr r0,[fp,#marquerDFS]		@ marquer [i_v] = 1
	ldr r1,[fp,#indexv]	
	mov r2,#1
	strb r2,[r0,r1,lsl #0]
	
	
	@ Pour chaque sommet w adjacent à v faire	
	mov r1,#0
	str r1,[fp,#jDFS]
pourjDFS:
	ldr r1,[fp,#jDFS]		@ pour j=0 à g.nbSucc[i_v] - 1 faire 
	ldr r3,[fp,#indexv]
	ldr r0,[fp,#adGrapheDFS]
	ldr r2,[r0,#nbSucc]
	ldr r4,[r2,r3,lsl #2]
	cmp r1,r4 		
	bge finpourjDFS
	
	ldr r0,[fp,#adGrapheDFS]	@ w = lesSucc(i_v,j)
	ldr r1,[r0,#lesSucc]
	ldr r2,[fp,#indexv]
	ldr r3,[r1,r2,lsl #2]
	ldr r4,[fp,#jDFS]
	ldrb r4,[r3,r4,lsl #0]
	strb r4,[fp,#wDFS]
	
	ldr r0,[fp,#wDFS]		@ i_w = RechercheSommet(w,g)
	ldr r1,[fp,#adGrapheDFS]
	stmfd sp!,{r0,r1}
	@ réservation pour résultat
	sub sp, sp, #4
	bl RechercheSommet
	@ fonction retourne indice
	ldmfd sp!,{r1}
	str r1,[fp,#indexw]
	@ libération dans la pile des param d''entrée
	add sp,	sp, #8	
		
 	ldr r0,[fp,#marquerDFS]		@ si marquer [i_w] = 0 
	ldr r1,[fp,#indexw]
	ldrb r2,[r0,r1,lsl #0]
	cmp r2,#0
	bne fsiMarquerDFS
	
	ldr r0,[fp,#wDFS]		@ alors DFS(w,g,marquer)
	ldr r1,[fp,#adGrapheDFS]
	ldr r2,[fp,#marquerDFS]
	stmfd sp!,{r0,r1,r2}
	@ réservation pour résultat
	sub sp, sp, #4
	bl DFS
	@ fonction retourne un booléen
	ldmfd sp!,{r2}
	@ libération dans la pile des param d''entrée
	add sp,	sp, #12		
 				
 
fsiMarquerDFS:
	ldr r1,[fp,#jDFS]
	add r1,r1,#1
	str r1,[fp,#jDFS]
	bal pourjDFS
	
finpourjDFS:
	
	@ {Restauration des registres temporaires}
	ldmfd sp!,{r0,r1,r2,r3,r4}
	@ {libération de la pile - var locales}
	add sp, sp, #16
	ldmfd sp!,{fp,lr}
	@ {retour au main}
	bx lr
	@ pointe sur le résultat

	
.global _Start
_Start:
	@---------------------------------------------------
	ldr r0,='b'			@ test : RechercheSommet('b');
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
	@ libération dans la pile des param d'entrée
	add sp,	sp, #12		
	
finStart:
	bal finStart

.end

