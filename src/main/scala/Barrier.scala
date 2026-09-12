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

class IDEXBarrier extends Module {
  val io = IO(
    new Bundle {
      val instructionIn = Input(new Instruction())
      val instructionOut = Output(new Instruction())
      val PCIn = Input(UInt(32.W))
      val PCOut = Output(UInt(32.W))

      val controlSignalsIn = Input(new ControlSignals())
      val branchTypeIn = Input(UInt(3.W))
      val op1SelectIn = Input(UInt(1.W))
      val op2SelectIn = Input(UInt(1.W))
      val immTypeIn = Input(UInt(3.W))
      val ALUopIn = Input(UInt(4.W))

      val controlSignalsOut = Output(new ControlSignals())
      val branchTypeOut = Output(UInt(3.W))
      val op1SelectOut = Output(UInt(1.W))
      val op2SelectOut = Output(UInt(1.W))
      val immTypeOut = Output(UInt(3.W))
      val ALUopOut = Output(UInt(4.W))
    }
  )

  // Delaying everything by one cycle
  val instructionReg = Reg(new Instruction())
  instructionReg := io.instructionIn
  io.instructionOut := instructionReg

  val PCReg = RegInit(0.U(32.W))
  PCReg := io.PCIn
  io.PCOut := PCReg

  val controlSignalsReg = Reg(new ControlSignals())
  controlSignalsReg := io.controlSignalsIn
  io.controlSignalsOut := controlSignalsReg

  val branchTypeReg = Reg(UInt(3.W))
  branchTypeReg := io.branchTypeIn
  io.branchTypeOut := branchTypeReg

  val op1SelectReg = Reg(UInt(1.W))
  op1SelectReg := io.op1SelectIn
  io.op1SelectOut := op1SelectReg

  val op2SelectReg = Reg(UInt(1.W))
  op2SelectReg := io.op2SelectIn
  io.op2SelectOut := op2SelectReg

  val immTypeReg = Reg(UInt(3.W))
  immTypeReg := io.immTypeIn
  io.immTypeOut := immTypeReg

  val ALUopReg = Reg(UInt(4.W))
  ALUopReg := io.ALUopIn
  io.ALUopOut := ALUopReg

  // So it does not get optimised away in the early stages of development
  dontTouch(io.PCIn)
  dontTouch(io.PCOut)

  //dontTouch(io.controlSignalsIn)
  //dontTouch(io.controlSignalsOut)
}