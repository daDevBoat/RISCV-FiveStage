main:
  sltiu gp, ra, 0x01AE
  srli sp, sp, 0x0005
  addi ra, sp, 0x0177
  srai sp, ra, 0x0003
  sub sp, ra, sp
  addi sp, ra, 0x000B
  done