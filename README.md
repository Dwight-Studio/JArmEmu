# JArmEmu
Simple ARMv7 simulator written in Java, intended for educational purpose.

## Features
JArmEmu is a simple simulator with a graphical interface that offers basic control and information about a simulated
ARMv7 architecture.

You can write your program and include GNU Assembly directives (only the basic ones are implemented, refer to synthaxic
colorimetry to see if it is implemented).

JArmEmu is powered by an ARMv7 interpreter made *Ex Nihilo* for this project.

## Limitations
Currently, known limitations or differences with the real architecture:
- Carry flag update for ASR may be inaccurate (no counter example found yet)
- SWI instruction is not implemented
- Shift doesn't perform flag update when used in MOV instruction

## Licence
This project was created by Kévin "FoxYinx" TOLLEMER and Alexandre "Deleranax" LECONTE, students at INSA Rennes (independent
project). It is distributed in open source under GPL3 (refer to the LICENCE file).