ADDCCS r0, r9, #2
    MLAEQ r0, r0,r1,    r2
  SMLALALS r4, r5, r6, r7
BICLO   r5, r6, #5
LDRB r0,=x
stmfd     sp!   ,   { r0  ,    r1   , r2}
bal etiquette
ceciEstUneEtiquette: