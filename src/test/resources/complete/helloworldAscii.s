.equ size, 11

.section .bss
	hello: .skip size
	.align
.section .text
.globl _start
_start:
ldr r0,=hello
@ ASCII codes stored
@ at [r0] get printed
mov r1, #'h' @ 'h'
strb r1,[r0]
add r0, #1
mov r1, #'e' @ 'e'
strb r1,[r0]
add r0, #1
mov r1, #'l' @ 'l'
strb r1,[r0]
add r0, #1
mov r1, #'l' @ 'l'
strb r1,[r0]
add r0, #1
mov r1, #'o' @ 'o'
strb r1,[r0]
add r0, #1
mov r1, #' ' @ ' '
strb r1,[r0]
add r0, #1
mov r1, #'w' @ 'w'
strb r1,[r0]
add r0, #1
mov r1, #'o' @ 'o'
strb r1,[r0]
add r0, #1
mov r1, #'r' @ 'r'
strb r1,[r0]
add r0, #1
mov r1, #'l' @ 'l'
strb r1,[r0]
add r0, #1
mov r1, #'d' @ 'd'
strb r1,[r0]
my_exit: @do infinite loop at the end
b my_exit