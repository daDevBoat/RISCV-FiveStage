package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.{IntParam, MultiIOModule, dontTouch}

class FWR extends MultiIOModule {
  val io = IO(
    new Bundle {
      val instructionEX = Input(new Instruction())
      val instructionMEM = Input(new Instruction())
      val aluResultMEM = Input(UInt(32.W))
      val instructionWB = Input(new Instruction())
      val WBData = Input(UInt(32.W))
      val registerData1In = Input(UInt(32.W))
      val registerData2In = Input(UInt(32.W))
      val memRead = Input(Bool())
      val memReadData = Input(UInt(32.W))

      val registerData1Out = Output(UInt(32.W))
      val registerData2Out = Output(UInt(32.W))
      val stallSignal = Output(Bool())
    }
  )

  val memReadReg = Reg(Bool())
  val memRdReg = RegInit(UInt(5.W), 0.U)
  memReadReg := io.memRead

  when(io.memRead) {
    memRdReg := io.instructionMEM.registerRd
  }

  dontTouch(memReadReg)
  io.stallSignal := false.B

  io.registerData1Out := io.registerData1In
  io.registerData2Out := io.registerData2In

  //printf(p"memRead = ${io.memRead} memReadReg = ${memReadReg}\n")
  //printf(p"EXInst = 0x${Hexadecimal(io.instructionEX.instruction)}  EXRs1 = ${io.instructionEX.registerRs1}  EXRs2 = ${io.instructionEX.registerRs2} \n")
  //printf(p"MEMInst = 0x${Hexadecimal(io.instructionMEM.instruction)}  MEMRd = ${io.instructionMEM.registerRd} \n")
  when (io.instructionEX.registerRs1 =/= 0.U) {
    when(memReadReg && memRdReg === io.instructionEX.registerRs1 && (io.instructionMEM.registerRd === io.instructionEX.registerRs1)) {
      io.registerData1Out := io.memReadData
      //printf("Forwarded something\n")
      //printf("MEM -> EX forwarding of MEM Read Data\n")
      //printf(p"FORWARDING: memReadReg=${memReadReg} memRead=${io.memRead} MEMrd=${io.instructionMEM.registerRd} EXrs1=${io.instructionEX.registerRs1}\n")
    }.elsewhen(io.instructionMEM.registerRd === io.instructionEX.registerRs1) {
      when(io.memRead) {
        io.stallSignal := true.B
        //printf("Stalling\n")
      }.otherwise {
        io.registerData1Out := io.aluResultMEM
        //printf("Forwarded something\n")
        //printf("MEM -> EX Forwarding of ALU result\n")
      }
    }.elsewhen(io.instructionWB.registerRd === io.instructionEX.registerRs1) {
      //printf("Forwarded something\n")
      io.registerData1Out := io.WBData
      /*
      printf(p"memRead = ${io.memRead} memReadReg = ${memReadReg}\n")
      printf(p"EXInst = 0x${Hexadecimal(io.instructionEX.instruction)}  EXRs1 = ${io.instructionEX.registerRs1}  EXRs2 = ${io.instructionEX.registerRs2} \n")
      printf(p"WBInst = 0x${Hexadecimal(io.instructionWB.instruction)}  MEMRd = ${io.instructionWB.registerRd} \n")

       */
    }
  }

  //printf("\n")

  when (io.instructionEX.registerRs2 =/= 0.U) {
    when(memReadReg && memRdReg === io.instructionEX.registerRs2 && (io.instructionMEM.registerRd === io.instructionEX.registerRs2)) {
      io.registerData2Out := io.memReadData
      /*
      printf(p"RS2 MEMDATA: memRead = ${io.memRead} memReadReg = ${memReadReg}\n")
      printf(p"EXInst = 0x${Hexadecimal(io.instructionEX.instruction)}  EXRs1 = ${io.instructionEX.registerRs1}  EXRs2 = ${io.instructionEX.registerRs2} \n")
      printf(p"WBInst = 0x${Hexadecimal(io.instructionWB.instruction)}  MEMRd = ${io.instructionWB.registerRd} \n")

       */
    }.elsewhen(io.instructionMEM.registerRd === io.instructionEX.registerRs2) {
      when(io.memRead) {
        io.stallSignal := true.B
      }.otherwise {
        io.registerData2Out := io.aluResultMEM
      }
    }.elsewhen(io.instructionWB.registerRd === io.instructionEX.registerRs2) {
      io.registerData2Out := io.WBData
      /*
      printf(p"RS2: memRead = ${io.memRead} memReadReg = ${memReadReg}\n")
      printf(p"EXInst = 0x${Hexadecimal(io.instructionEX.instruction)}  EXRs1 = ${io.instructionEX.registerRs1}  EXRs2 = ${io.instructionEX.registerRs2} \n")
      printf(p"WBInst = 0x${Hexadecimal(io.instructionWB.instruction)}  MEMRd = ${io.instructionWB.registerRd} \n")

       */
    }
  }


  //printf("\n")
  dontTouch(io.registerData1Out)
  dontTouch(io.registerData2Out)
}
