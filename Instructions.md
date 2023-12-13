# ARMv7 Instructions
This file regroups all implemented instructions in JArmEmu and a quick summary of their arguments.

---

| Instruction Name |    Arg1     | Arg2  | Arg3 | Arg4 |
|:----------------:|:-----------:|:-----:|:----:|:----:|
|       ADD        |     reg     |  reg  | arg  |      |
|       SUB        |     reg     |  reg  | arg  |      |
|       RSB        |     reg     |  reg  | reg  |      |    
|       ADC        |     reg     |  reg  | arg  |      |
|       SBC        |     reg     |  reg  | arg  |      |
|       MUL        |     reg     |  reg  | reg  |      |
|       MLA        |     reg     |  reg  | reg  | reg  |
|      UMULL       |     reg     |  reg  | reg  | reg  |
|      UMLAL       |     reg     |  reg  | reg  | reg  |
|      SMULL       |     reg     |  reg  | reg  | reg  |
|      SMLAL       |     reg     |  reg  | reg  | reg  |
|       AND        |     reg     |  reg  | arg  |      |
|       ORR        |     reg     |  reg  | arg  |      |
|       EOR        |     reg     |  reg  | arg  |      |
|       BIC        |     reg     |  reg  | arg  |      |
|       CMP        |     reg     |  arg  |      |      |
|       CMN        |     reg     |  arg  |      |      |
|       TST        |     reg     |  arg  |      |      |
|       TEQ        |     reg     |  arg  |      |      |
|       MOV        |     reg     |  arg  |      |      |
|       MVN        |     reg     |  arg  |      |      |
|       ADR        |     reg     | label |      |      |
|       LDR        |     reg     |  mem  |      |      |
|       STR        |     reg     |  mem  |      |      |
|       LDM        |     reg     | mreg  |      |      |
|       STM        |     reg     | mreg  |      |      |
|        B         |    label    |       |      |      |
|        BL        |    label    |       |      |      |
|        BX        |     reg     |       |      |      |
|       LSL        | reg / value |  reg  |      |      |
|       LSR        | reg / value |  reg  |      |      |
|       ASR        | reg / value |  reg  |      |      |
|       ROR        | reg / value |  reg  |      |      |
|       RRX        |     reg     |  reg  |      |      |

