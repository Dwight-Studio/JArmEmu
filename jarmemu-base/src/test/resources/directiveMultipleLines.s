.bss .data
        .global      ExEC
A:
            b: .Word 3 .byTE 'x'     .global     Test
.Ascii   .Asciz   ""
.equ laBEL, 'c'
.data
.comment
    Hello, comment ça va ?
    .ascii "Hey"
.text
LDR R1, =b
.end
.text