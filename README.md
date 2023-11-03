# JArmEmu
Simple ARMv7 simulator written in Java, intended for educational purpose.

## Features
JArmEmu is a simple simulator with a graphical interface that offers basic control and information about a simulated
ARMv7 interpreted architecture.

You can write you program and include GNU Assembly directives (only the basic ones are implemented, refer to syntax 
colorimetry to see if it is implemented).

JArmEmu is powered by a ARMv7 interpreter made *Ex Nihilo* for this project.

## Limitations
Currently known limitations or differences with the real architecture:
- Labels are considered instructions in memory, which means that the PC counts them.

## Licence
This project was created by Kévin "FoxYinx" TOLLEMER and Alexandre "Deleranax" LECONTE, students at INSA Rennes (independant
project). It is distributed in open source under GPL3 (refer to the LICENCE file).