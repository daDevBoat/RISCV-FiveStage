main:
    addi a0, x0, 5
loop:
    addi a0, a0, -1
    bne a0, x0, loop
    addi x0, x0, 10
    done