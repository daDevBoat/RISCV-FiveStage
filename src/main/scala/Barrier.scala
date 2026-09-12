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
      val PCIn = Input(UInt(32.W))
      val controlSignalsIn = Input(new ControlSignals())
      val branchTypeIn = Input(UInt(3.W))
      val op1SelectIn = Input(UInt(1.W))
      val op2SelectIn = Input(UInt(1.W))
      val immTypeIn = Input(UInt(3.W))
      val ALUopIn = Input(UInt(4.W))
      val registerData1In = Input(UInt(32.W))
      val registerData2In = Input(UInt(32.W))

      val PCOut = Output(UInt(32.W))
      val instructionOut = Output(new Instruction())
      val controlSignalsOut = Output(new ControlSignals())
      val branchTypeOut = Output(UInt(3.W))
      val op1SelectOut = Output(UInt(1.W))
      val op2SelectOut = Output(UInt(1.W))
      val immTypeOut = Output(UInt(3.W))
      val ALUopOut = Output(UInt(4.W))
      val registerData1Out = Output(UInt(32.W))
      val registerData2Out = Output(UInt(32.W))

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

  val registerData1Reg = Reg(UInt(32.W))
  registerData1Reg := io.registerData1In
  io.registerData1Out := registerData1Reg

  val registerData2Reg = Reg(UInt(32.W))
  registerData2Reg := io.registerData2In
  io.registerData2Out := registerData2Reg

  // So it does not get optimised away in the early stages of development
  dontTouch(io.PCIn)
  dontTouch(io.PCOut)

}

class EXMEMBarrier extends Module {
  val io = IO(
    new Bundle {
      val instructionIn = Input(new Instruction())
      val PCIn = Input(UInt(32.W))
      val controlSignalsIn = Input(new ControlSignals())
      val aluResultIn = Input(UInt(32.W))
      val writeDataIn = Input(UInt(32.W))

      val instructionOut = Output(new Instruction())
      val PCOut = Output(UInt(32.W))
      val controlSignalsOut = Output(new ControlSignals())
      val aluResultOut = Output(UInt(32.W))
      val writeDataOut = Output(UInt(32.W))
    }
  )

  val instructionReg = Reg(new Instruction())
  val PCReg = Reg(UInt(32.W))
  val controlSignalsReg = Reg(new ControlSignals())
  val aluResultReg = Reg(UInt(32.W))
  val writeDataReg = Reg(UInt(32.W))

  instructionReg := io.instructionIn
  PCReg := io.PCIn
  controlSignalsReg := io.controlSignalsIn
  aluResultReg := io.aluResultIn
  writeDataReg := io.writeDataIn

  io.instructionOut := instructionReg
  io.PCOut := PCReg
  io.controlSignalsOut := controlSignalsReg
  io.aluResultOut := aluResultReg
  io.writeDataOut := writeDataReg
}

class MEMWBBarrier extends Module {
  val io = IO(
    new Bundle {
      val instructionIn = Input(new Instruction())
      val controlSignalsIn = Input(new ControlSignals())
      val aluResultIn = Input(UInt(32.W))
      val memDataIn = Input(UInt(32.W))

      val instructionOut = Output(new Instruction())
      val controlSignalsOut = Output(new ControlSignals())
      val aluResultOut = Output(UInt(32.W))
      val memDataOut = Output(UInt(32.W))
    }
  )

  val instructionReg = Reg(new Instruction())
  val controlSignalsReg = Reg(new ControlSignals())
  val aluResultReg = Reg(UInt(32.W))
  val memDataReg = Reg(UInt(32.W))

  instructionReg := io.instructionIn
  controlSignalsReg := io.controlSignalsIn
  aluResultReg := io.aluResultIn
  memDataReg := io.memDataIn

  io.instructionOut := instructionReg
  io.controlSignalsOut := controlSignalsReg
  io.aluResultOut := aluResultReg
  io.memDataOut := memDataReg
}