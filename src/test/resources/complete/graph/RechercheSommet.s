@ Graphes et parcours de graphes
@ Fonction RechercheSommet
@ Objectifs : 
@	- recherche de l''indice relatif au sommet passé en paramètre
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


@ Constantes nécessaires pour la fonction RechercheSommet
.set iRech,-4		@ |    i    |
			@ |  anc fp |
			@ |lr retour|
.set resRech,8		@ |param ret|	résultat de la fonction : indice tableau
.set SomRech,12		@ |  sommet |   caractère en param de fonction
.set adGRech,16		@ | @graphe |   adresse du graphe en param de fonction

.global RechercheSommet
RechercheSommet:
	@ {préparation de la pile}
	stmfd sp!,{lr}
	stmfd sp!,{fp}
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
	ldmfd sp!,{fp}
	ldmfd sp!,{lr}
	@ {retour au main}
	bx lr
	@ pointe sur le résultat

	
