package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.{IntParam, MultiIOModule}

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

      val registerData1Out = Output(UInt(32.W))
      val registerData2Out = Output(UInt(32.W))
    }
  )

  when(io.instructionMEM.registerRd === io.instructionEX.registerRs1) {
    io.registerData1Out := io.aluResultMEM
  }.elsewhen(io.instructionWB.registerRd === io.instructionEX.registerRs1) {
    io.registerData1Out := io.WBData
  }.otherwise {
    io.registerData1Out := io.registerData1In
  }

  when(io.instructionMEM.registerRd === io.instructionEX.registerRs2) {
    io.registerData2Out := io.aluResultMEM
  }.elsewhen(io.instructionWB.registerRd === io.instructionEX.registerRs2) {
    io.registerData2Out := io.WBData
  }.otherwise {
    io.registerData2Out := io.registerData2In
  }

}
