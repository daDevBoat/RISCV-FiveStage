main:
    jal x2, test
    addi x3, x0, 15
test:
    addi x1, x0, 100
    jr x1
test2:
    addi x1, x0, 14
    addi x1, x0, 12
    done