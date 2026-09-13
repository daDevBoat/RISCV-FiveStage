package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule


class MemoryFetch() extends MultiIOModule {


  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val DMEMsetup      = Input(new DMEMsetupSignals)
      val DMEMpeek       = Output(UInt(32.W))

      val testUpdates    = Output(new MemUpdates)
    })

  val io = IO(
    new Bundle {
      val instructionIn = Input(new Instruction())
      val PCIn = Input(UInt(32.W))
      val controlSignalsIn = Input(new ControlSignals())
      val aluResultIn = Input(UInt(32.W))
      val writeData = Input(UInt(32.W))
      val memoryAddress = Input(UInt(32.W))

      val instructionOut = Output(new Instruction())
      val PCOut = Output(UInt(32.W))
      val controlSignalsOut = Output(new ControlSignals())
      val aluResultOut = Output(UInt(32.W))
      val memDataOut = Output(UInt(32.W))
    })


  val DMEM = Module(new DMEM)


  /**
    * Setup. You should not change this code
    */
  DMEM.testHarness.setup  := testHarness.DMEMsetup
  testHarness.DMEMpeek    := DMEM.io.dataOut
  testHarness.testUpdates := DMEM.testHarness.testUpdates

  io.instructionOut := io.instructionIn
  io.PCOut := io.PCIn
  io.controlSignalsOut := io.controlSignalsIn
  io.aluResultOut := io.aluResultIn
  io.memDataOut := 0.U

  DMEM.io.dataAddress := io.memoryAddress
  DMEM.io.dataIn      := 0.U
  DMEM.io.writeEnable := false.B

  when(io.controlSignalsIn.memRead) {
    io.memDataOut := DMEM.io.dataOut
    //printf(p"memDataOut = ${io.memDataOut} and address = ${DMEM.io.dataAddress} \n")
  }.elsewhen(io.controlSignalsIn.memWrite) {
    // Uses the ALU result that is stalled on cycle
    DMEM.io.dataAddress := io.aluResultIn
    DMEM.io.dataIn := io.writeData
    DMEM.io.writeEnable := true.B
  }
}
