package FiveStage

import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


class CPU extends MultiIOModule {

  val testHarness = IO(
    new Bundle {
      val setupSignals = Input(new SetupSignals)
      val testReadouts = Output(new TestReadouts)
      val regUpdates   = Output(new RegisterUpdates)
      val memUpdates   = Output(new MemUpdates)
      val currentPC    = Output(UInt(32.W))
    }
  )

  /**
    You need to create the classes for these yourself
    */
  val IFIDBarrier  = Module(new IFIDBarrier).io
   val IDEXBarrier  = Module(new IDEXBarrier).io
  // val EXBarrier  = Module(new EXBarrier).io
  // val MEMBarrier = Module(new MEMBarrier).io

  val IF  = Module(new InstructionFetch)
  val ID  = Module(new InstructionDecode)
  val EX  = Module(new Execute)
  val MEM = Module(new MemoryFetch)
  // val WB  = Module(new Execute) (You may not need this one?)


  /**
    * Setup. You should not change this code
    */
  IF.testHarness.IMEMsetup     := testHarness.setupSignals.IMEMsignals
  ID.testHarness.registerSetup := testHarness.setupSignals.registerSignals
  MEM.testHarness.DMEMsetup    := testHarness.setupSignals.DMEMsignals

  testHarness.testReadouts.registerRead := ID.testHarness.registerPeek
  testHarness.testReadouts.DMEMread     := MEM.testHarness.DMEMpeek

  /**
    spying stuff
    */
  testHarness.regUpdates := ID.testHarness.testUpdates
  testHarness.memUpdates := MEM.testHarness.testUpdates
  testHarness.currentPC  := IF.testHarness.PC

  // All connections between stages:
  // --------------------------------------------------
  connectIFID()
  connectIDEX()

  private def connectIFID(): Unit = {
    // set up the IFID barrier functionality (driving the signals)
    IFIDBarrier.PCIn := IF.io.PC
    IFIDBarrier.instructionIn := IF.io.instruction

    // Drives the signals from the barrier to the ID
    ID.io.instructionIn := IFIDBarrier.instructionOut
    ID.io.PCIn := IFIDBarrier.PCOut
  }

  private def connectIDEX(): Unit = {
    IDEXBarrier.instructionIn := ID.io.instructionIn
    IDEXBarrier.PCIn := ID.io.PCIn
    IDEXBarrier.controlSignalsIn := ID.io.controlSignals
    IDEXBarrier.branchTypeIn := ID.io.branchType
    IDEXBarrier.op1SelectIn := ID.io.op1Select
    IDEXBarrier.op2SelectIn := ID.io.op2Select
    IDEXBarrier.immTypeIn := ID.io.immType
    IDEXBarrier.ALUopIn := ID.io.ALUop

    EX.io.instructionIn := IDEXBarrier.instructionOut
    EX.io.PCIn := IDEXBarrier.PCOut
    EX.io.controlSignalsIn := IDEXBarrier.controlSignalsOut
    EX.io.branchType := IDEXBarrier.branchTypeOut
    EX.io.op1Select := IDEXBarrier.op1SelectOut
    EX.io.op2Select := IDEXBarrier.op2SelectOut
    EX.io.immType := IDEXBarrier.immTypeOut
    EX.io.aluOp := IDEXBarrier.ALUopOut
  }

}
