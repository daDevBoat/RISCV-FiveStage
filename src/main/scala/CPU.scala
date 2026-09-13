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
  val EXMEMBarrier  = Module(new EXMEMBarrier).io
  val MEMWBBarrier = Module(new MEMWBBarrier).io

  val IF  = Module(new InstructionFetch)
  val ID  = Module(new InstructionDecode)
  val EX  = Module(new Execute)
  val MEM = Module(new MemoryFetch)
  


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
  connectEXMEM()
  connectMEMWB()
  connectWBID()

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
    IDEXBarrier.registerData1In := ID.io.registerData1
    IDEXBarrier.registerData2In := ID.io.registerData2

    EX.io.instructionIn := IDEXBarrier.instructionOut
    EX.io.PCIn := IDEXBarrier.PCOut
    EX.io.controlSignalsIn := IDEXBarrier.controlSignalsOut
    EX.io.branchType := IDEXBarrier.branchTypeOut
    EX.io.op1Select := IDEXBarrier.op1SelectOut
    EX.io.op2Select := IDEXBarrier.op2SelectOut
    EX.io.immType := IDEXBarrier.immTypeOut
    EX.io.aluOp := IDEXBarrier.ALUopOut
    EX.io.registerData1 := IDEXBarrier.registerData1Out
    EX.io.registerData2 := IDEXBarrier.registerData2Out
  }

  private def connectEXMEM(): Unit = {
    EXMEMBarrier.instructionIn := EX.io.instructionOut
    EXMEMBarrier.PCIn := EX.io.PCOut
    EXMEMBarrier.controlSignalsIn := EX.io.controlSignalsOut
    EXMEMBarrier.aluResultIn := EX.io.aluResult
    EXMEMBarrier.writeDataIn := EX.io.registerData2

    MEM.io.instructionIn := EXMEMBarrier.instructionOut
    MEM.io.PCIn := EXMEMBarrier.PCOut
    MEM.io.controlSignalsIn := EXMEMBarrier.controlSignalsOut
    MEM.io.aluResultIn := EXMEMBarrier.aluResultOut
    MEM.io.writeData := EXMEMBarrier.writeDataOut
    MEM.io.memoryAddress := EXMEMBarrier.memoryAddress
  }

  private def connectMEMWB(): Unit = {
    MEMWBBarrier.instructionIn := MEM.io.instructionOut
    MEMWBBarrier.controlSignalsIn := MEM.io.controlSignalsOut
    MEMWBBarrier.aluResultIn := MEM.io.aluResultOut
    MEMWBBarrier.memDataIn := MEM.io.memDataOut
  }

  private def connectWBID(): Unit = {
    ID.io.writeEnable := MEMWBBarrier.controlSignalsOut.regWrite
    ID.io.writeAddress := MEMWBBarrier.instructionOut.registerRd
    ID.io.writeData := Mux(MEMWBBarrier.controlSignalsOut.memRead, MEMWBBarrier.memDataOut, MEMWBBarrier.aluResultOut)
  }

}
