package FiveStage

import chisel3._

class WriteBack extends Module {
  val io = IO(
    new Bundle {
      val instructionIn = Input(new Instruction())
      val controlSignalsIn = Input(new ControlSignals())
      val aluResultIn = Input(UInt(32.W))
      val memDataIn = Input(UInt(32.W))

      //val writeEnable = Output(Bool())
      //val writeAddr = Output(UInt(5.W))
      //val writeData = Output(UInt(32.W))
    }
  )

  when(io.controlSignalsIn.regWrite) {

  }

}