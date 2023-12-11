@ Graphes et parcours de graphes
@ Fonction DFS
@ Objectifs : 
@	- parcours d'un graphe en profondeur
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

.text


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

.global DFS
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
	ldr r1,[fp,#jDFS]	@ pour j=0 à g.nbSucc[i_v] - 1 faire 
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
	@ libération dans la pile des param d'entrée
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

