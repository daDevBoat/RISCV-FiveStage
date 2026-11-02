package FiveStage
import chisel3._
import chisel3.experimental.{MultiIOModule, dontTouch}

class InstructionFetch extends MultiIOModule {

  // Don't touch
  val testHarness = IO(
    new Bundle {
      val IMEMsetup = Input(new IMEMsetupSignals)
      val PC        = Output(UInt())
    }
  )


  /**
    * TODO: Add input signals for handling events such as jumps

    * TODO: Add output signal for the instruction. 
    * The instruction is of type Bundle, which means that you must
    * use the same syntax used in the testHarness for IMEM setup signals
    * further up.
    */
  val io = IO(
    new Bundle {
      val PCOverride = Input(Bool())
      val PCIn = Input(UInt(32.W))
      val stallSignal = Input(Bool())
      val branchWasTaken = Input(Bool())
      val updateBranchPredictor = Input(Bool())
      val PCOfBranchTaken = Input(UInt(32.W))

      val PC = Output(UInt())
      val instruction = Output(new Instruction())
      val branchPredictedTaken = Output(Bool())
    })

  val IMEM = Module(new IMEM)
  val PC   = RegInit(UInt(32.W), 0.U)


  /**
    * Setup. You should not change this code
    */
  IMEM.testHarness.setupSignals := testHarness.IMEMsetup
  testHarness.PC := IMEM.testHarness.requestedAddress

  io.PC := PC

  val instruction = Wire(new Instruction)
  instruction := IMEM.io.instruction.asTypeOf(new Instruction)
  io.instruction := instruction


  // Branch predictor
  val branchPredictor = Module(new BranchPredictor)
  branchPredictor.io.branchWasTaken := io.branchWasTaken
  branchPredictor.io.updateFlag := io.updateBranchPredictor
  branchPredictor.io.PCOfBranchTaken := io.PCOfBranchTaken
  branchPredictor.io.branchTarget := io.PCIn

  branchPredictor.io.PC := PC
  io.branchPredictedTaken := branchPredictor.io.branchPredictedTaken



  // PC override logic
  when(io.PCOverride) {
    io.PC := io.PCIn
    PC := io.PCIn + 4.U
  }.elsewhen(branchPredictor.io.BTBHit && branchPredictor.io.branchPredictedTaken) {
    PC := branchPredictor.io.predictedTarget
    printf(p"Predicted target 0x${Hexadecimal(branchPredictor.io.predictedTarget)} \n")
    //PC := PC + 4.U

  }.otherwise {
    when(!io.stallSignal) {
      PC := PC + 4.U
    }
  }

  IMEM.io.instructionAddress := io.PC


  /**
    * Setup. You should not change this code.
    */
  when(testHarness.IMEMsetup.setup) {
    PC := 0.U
    instruction := Instruction.NOP
  }

  dontTouch(io.PCIn)
  dontTouch(io.PCOverride)
}
