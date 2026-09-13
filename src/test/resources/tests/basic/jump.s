main:
    jal x2, test
    addi x3, x0, 15
test:
    addi x1, x0, 11
    jalr x0, 0(x2)
test2:
    addi x1, x0, 14
    addi x1, x0, 12
    done