@ Graphes et parcours de graphes
@ Fonction EstPointEntree
@ Objectifs : 
@	- vérifie si le sommet est point d''entrée
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

@ Constantes nécessaires pour la fonction EstPointEntree
.set indice,-12			@ | indice  |
.set jPtEnt,-8			@ |    j    |
.set iPtEnt,-4			@ |    i    |
				@ |  anc fp |
				@ |lr retour|
.set resPtEntree,8		@ |param ret|	résultat de la fonction
.set sommet,12			@ |  sommet |   caractère en param de fonction
.set adGraphePtEnt,16		@ | @graphe |   adresse du graphe en param de fonction

.global EstPointEntree
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
	
	
