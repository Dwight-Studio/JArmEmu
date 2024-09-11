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
