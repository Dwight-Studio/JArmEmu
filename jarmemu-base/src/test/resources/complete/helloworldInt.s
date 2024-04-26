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
mov r1, #104 @ 'h'
strb r1,[r0]
add r0, #1
mov r1, #101 @ 'e'
strb r1,[r0]
add r0, #1
mov r1, #108 @ 'l'
strb r1,[r0]
add r0, #1
mov r1, #108 @ 'l'
strb r1,[r0]
add r0, #1
mov r1, #111 @ 'o'
strb r1,[r0]
add r0, #1
mov r1, #32 @ ' '
strb r1,[r0]
add r0, #1
mov r1, #119 @ 'w'
strb r1,[r0]
add r0, #1
mov r1, #111 @ 'o'
strb r1,[r0]
add r0, #1
mov r1, #114 @ 'r'
strb r1,[r0]
add r0, #1
mov r1, #108 @ 'l'
strb r1,[r0]
add r0, #1
mov r1, #100 @ 'd'
strb r1,[r0]
my_exit: @do infinite loop at the end
b my_exit