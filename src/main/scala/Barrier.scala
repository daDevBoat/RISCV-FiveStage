package FiveStage
import chisel3._
import chisel3.experimental.{MultiIOModule, dontTouch}


class IFIDBarrier extends Module {
  val io = IO(
    new Bundle {
      val instructionIn = Input(new Instruction())
      val instructionOut = Output(new Instruction())
      val PCIn = Input(UInt(32.W))
      val PCOut = Output(UInt(32.W))
    }
  )

  // Driving the instruction straight through
  io.instructionOut := io.instructionIn

  // Delaying the PC by one cycle
  val PCReg = RegInit(0.U(32.W))
  PCReg := io.PCIn
  io.PCOut := PCReg

  // So it does not get optimised away in the early stages of development
  dontTouch(io.PCIn)
  dontTouch(io.PCOut)
}