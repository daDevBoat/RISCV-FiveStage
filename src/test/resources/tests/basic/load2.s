main:
	addi x1, zero, 4
	addi x2, zero, 4
	addi x3, zero, 4 
	addi x4, zero, 4 
	lw x1, 0(x1)
	add x1, x1, x1
	lw x1, 0(x1)
	sw x1, 4(x1)
	lw x1, 8(x1)
	done
#memset 0x0,  4
#memset 0x4,  8
#memset 0x8,  12
#memset 0xc,  16
#memset 0x10, 20
#memset 0x14, 24
#memset 0x18, 28
#memset 0x1c, 32
#memset 0x20, 36
#memset 0x24, 40
