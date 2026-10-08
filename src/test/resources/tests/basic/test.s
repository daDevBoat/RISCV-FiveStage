main:
    addi x1, x0, 6
    addi x2, x0, 4
    sw   x1, 0(x0)
    addi x1, x0, -2
    sw   x1, 4(x0)
    lw   x2, 0(x0)
    lw   x3, 4(x0)
    add  x4, x2, x3
    done