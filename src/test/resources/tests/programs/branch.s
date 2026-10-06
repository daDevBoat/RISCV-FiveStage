main:
    addi x1, x0, 5
    addi x2, x0, 5
    beq x1, x2, test
    addi x3, x0, 9
test:
    addi x3, x0, 10
    bne x1, x3, test2
    addi x4, x0, 14
test2:
    addi x4, x0, 15
    addi x5, x0, 16
    blt x4, x5, test3
    addi x4, x0, 20
test3:
    addi x6, x0, 17
    bge x6, x5, test4
    addi x7, x0, 21
test4:
    addi x7, x0, 22
    done