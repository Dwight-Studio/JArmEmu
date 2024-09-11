.global _start
.text
_start:
	@ Beginning of the program
	addal r2, r4, r5
	adds r6, r2, r6
	addccs r6, r5
	addlos r2, r0, #6
	addne r2, r6, r5, lsr #1
	add r5, r4, r6, ror r5
	sub r2, r4, r5
	sub r6, r2, r6
	sub r6, r5
	sub r2, r0, #6
	sub r2, r6, r5, lsr #1
	sub r5, r4, r6, rrx
	rsb r9, r5, #6
	rsbhs r0, r6
	rsbges r0, r2, r3, lsl r6
	rsbccs r9, r5
	rsbne r0, r0, #55
	rsbcs r2, r3, r4
	adc r5, r6, r7
	adceq r5, r6, #8
	adchss r2, r2
	adcgt r9, sp, #5
	adc r5, r5, lsr #3
	adclos r11, r12, r13
	sbc r2, r4, r5
	sbc r6, r2, r6
	sbc r6, r5
	sbc r2, r0, #6
	sbc r2, r6, r5, lsr #1
	sbc r5, r4, r6, rrx
	rsc r9, r5, #6
	rschs r0, r6
	rscges r0, r2, r3, lsl r6
	rscccs r9, r5
	rscne r0, r0, #55
	rsccs r2, r3, r4
	mul r0, r0, r0
	muls r5, r6, r8
	mulnes r5, r6, r5
	muls r9, r9, r5
	mullt r10, sp, fp
	mulle r8, r6, r6
	mla r8, r6, r6, r1
	mlaeq r1, r8, r6, r5
	mlaccs r8, r11, sp, sp
	mlacc r8, r3, r1, r1
	mlane r7, r0, r6, r9
	mlavc r8, r6, r5, r4
	umull r0, r1, r2, r3
	umulls r1, r2, r3, r4
	umulleq r2, r3, r4, r5
	umullmi sp, fp, ip, lr
	umullvss sp, r8, r6, r8
	umulllts r4, r5, r6, r7
	umlal r0, r1, r2, r3
	umlals r1, r2, r3, r4
	umlaleq r2, r3, r4, r5
	umlalmi sp, fp, ip, lr
	umlalvss sp, r8, r6, r8
	umlallts r4, r5, r6, r7	
	smull r0, r1, r2, r3
	smulls r1, r2, r3, r4
	smulleq r2, r3, r4, r5
	smullmi sp, fp, ip, lr
	smullvss sp, r8, r6, r8
	smulllts r4, r5, r6, r7	
	smlal r0, r1, r2, r3
	smlals r1, r2, r3, r4
	smlaleq r2, r3, r4, r5
	smlalmi sp, fp, ip, lr
	smlalvss sp, r8, r6, r8
	smlallts r4, r5, r6, r7
	and r1, r2, r3, lsl #2
	andhss r5, r6
	andvs r6, r2, #5
	ands sp, fp, r2
	andeq r1, r2, rrx
	andpl r0, r1, r2
	orr r1, r2, r3, lsl #2
	orrhss r5, r6
	orrvs r6, r2, #5
	orrs sp, fp, r2
	orreq r1, r2, rrx
	orrpl r0, r1, r2
	eor r1, r2, r3, lsl #2
	eorhss r5, r6
	eorvs r6, r2, #5
	eors sp, fp, r2
	eoreq r1, r2, rrx
	eorpl r0, r1, r2
	bic r1, r2, r3, lsl #2
	bichss r5, r6
	bicvs r6, r2, #5
	bics sp, fp, r2
	biceq r1, r2, rrx
	bicpl r0, r1, r2
	cmp r0, #25
	cmpeq r5, r0
	cmpcs r6, r6, lsr #9
	cmpmi r10, r5
	cmplo sp, #255
	cmphs r8, r6, ror #2	
	cmn r0, #25
	cmneq r5, r0
	cmncs r6, r6, lsr #9
	cmnmi r10, r5
	cmnlo sp, #255
	cmnhs r8, r6, ror #2	
	tst r0, #25
	tsteq r5, r0
	tstcs r6, r6, lsr #9
	tstmi r10, r5
	tstlo sp, #255
	tsths r8, r6, ror #2	
	teq r0, #25
	teqeq r5, r0
	teqcs r6, r6, lsr #9
	teqmi r10, r5
	teqlo sp, #255
	teqhs r8, r6, ror #2

