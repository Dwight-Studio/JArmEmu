# JArmEmu
[![Copr build status](https://copr.fedorainfracloud.org/coprs/dwight-studio/JArmEmu/package/jarmemu/status_image/last_build.png)](https://copr.fedorainfracloud.org/coprs/dwight-studio/JArmEmu/package/jarmemu/)
Simple ARMv7 simulator written in Java, intended for educational purpose.

## Features
JArmEmu is a simple simulator with a graphical interface that offers basic control and information about a simulated
ARMv7 architecture.

You can write your program using the ARMv7 instruction set (refer to
[Instructions.md](https://github.com/Dwight-Studio/JArmEmu/blob/main/Instructions.md) or to synthaxic colorimetry
to see what is implemented) and include GNU Assembly directives (only the basic ones are implemented, again refer to synthaxic
colorimetry to see if it is implemented).

JArmEmu is powered by an ARMv7 interpreter made *Ex Nihilo* for this project.

## Limitations
Currently, known limitations or differences with the real architecture:
- SWI instruction is not implemented

## Licence
This project was created by Kévin "FoxYinx" TOLLEMER and Alexandre "Deleranax" LECONTE, students at INSA Rennes (independent
project). It is distributed in open source under GPL3 (refer to the LICENCE file).
