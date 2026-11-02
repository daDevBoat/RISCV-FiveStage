package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule


class InstructionDecode extends MultiIOModule {

  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val registerSetup = Input(new RegisterSetupSignals)
      val registerPeek  = Output(UInt(32.W))

      val testUpdates   = Output(new RegisterUpdates)
    })


  val io = IO(
    new Bundle {
      val instructionIn = Input(new Instruction())
      val PCIn = Input(UInt(32.W))
      val writeEnable = Input(Bool())
      val writeAddress = Input(UInt(5.W))
      val writeData = Input(UInt(32.W))
      val branchPredictedTakenIn = Input(Bool())

      val instructionOut = Output(new Instruction())
      val PCOut = Output(UInt(32.W))
      val controlSignals = Output(new ControlSignals())
      val branchType = Output(UInt(3.W))
      val op1Select = Output(UInt(1.W))
      val op2Select = Output(UInt(1.W))
      val immType = Output(UInt(3.W))
      val ALUop = Output(UInt(4.W))
      val branchPredictedTakenOut = Output(Bool())

      val registerData1 = Output(UInt(32.W))
      val registerData2 = Output(UInt(32.W))
    }
  )

  val registers = Module(new Registers)
  val decoder   = Module(new Decoder).io


  /**
    * Setup. You should not change this code
    */
  registers.testHarness.setup := testHarness.registerSetup
  testHarness.registerPeek    := registers.io.readData1
  testHarness.testUpdates     := registers.testHarness.testUpdates

  ///*
  registers.io.readAddress1 := io.instructionIn.registerRs1
  registers.io.readAddress2 := io.instructionIn.registerRs2
  registers.io.writeEnable  := io.writeEnable
  registers.io.writeAddress := io.writeAddress
  registers.io.writeData    := io.writeData

  io.registerData1 := registers.io.readData1
  io.registerData2 := registers.io.readData2

  // Forwarding of data if writeback and read happens in the same cycle
  when(io.writeEnable && io.writeAddress =/= 0.U) {
    when(io.writeAddress === io.instructionIn.registerRs1) {
      io.registerData1 := io.writeData
    }
    when(io.writeAddress === io.instructionIn.registerRs2) {
      io.registerData2 := io.writeData
    }
  }

  decoder.instruction := io.instructionIn

  io.controlSignals := decoder.controlSignals
  io.branchType := decoder.branchType
  io.op1Select := decoder.op1Select
  io.op2Select := decoder.op2Select
  io.immType := decoder.immType
  io.ALUop := decoder.ALUop

  //*/

  // Drive the PC and instruction as outputs for the barrier inputs
  io.instructionOut := io.instructionIn
  io.PCOut := io.PCIn
  io.branchPredictedTakenOut := io.branchPredictedTakenIn

  when (io.branchPredictedTakenIn && !decoder.controlSignals.branch) {
    io.instructionOut := Instruction.NOP
    io.controlSignals := ControlSignals.nop
  }

}

